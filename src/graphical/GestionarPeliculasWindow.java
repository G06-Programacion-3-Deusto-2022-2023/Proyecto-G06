package graphical;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONException;

import cine.Administrador;
import cine.EdadRecomendada;
import cine.Genero;
import cine.Pelicula;
import cine.SetPeliculas;
import internals.GestorBD;
import internals.Settings;
import internals.bst.Filter;
import internals.swing.JSONChooser;
import internals.swing.JTextFieldLimit;
import internals.swing.PeliculasComboBoxRenderer;
import internals.swing.SetsPeliculasComboBoxRenderer;

public class GestionarPeliculasWindow extends JFrame {
    public GestionarPeliculasWindow (GestorBD db) {
        this (db, null, null);
    }

    public GestionarPeliculasWindow (GestorBD db, Administrador admin) {
        this (db, admin, null);
    }

    public GestionarPeliculasWindow (GestorBD db, Administrador admin, AdministradorWindow w)
            throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar una base de datos nula a la ventana de gestión de películas.");

        AdministradorWindow pw[] = new AdministradorWindow [] { w };
        GestionarPeliculasWindow f = this;

        Pelicula pelicula[] = new Pelicula [1];
        SetPeliculas setpeliculas[] = new SetPeliculas [1];
        final AtomicReferenceArray <Runnable> filters = new AtomicReferenceArray <Runnable> (2);

        this.addComponentListener (new ComponentAdapter () {
            @Override
            public void componentShown (ComponentEvent e) {
                if (pelicula [0] != null) {
                    if (pelicula [0].getNombre ().equals (pelicula [0].getId ().toString ())) {
                        Pelicula array[] = db.getPeliculas ().toArray (new Pelicula [0]);

                        int nuevas = 0;
                        for (int i = 0; i < array.length; nuevas += array [i++].getNombre ()
                                .toLowerCase ()
                                .contains ("nueva película") ? 1 : 0)
                            ;

                        pelicula [0].setNombre (String.format ("Nueva película%s",
                                nuevas == 0 ? "" : String.format (" #%d", nuevas + 1)));
                    }

                    db.update (pelicula [0]);
                    pelicula [0] = null;
                }

                if (setpeliculas [0] != null) {
                    if (setpeliculas [0].getNombre ().equals (setpeliculas [0].getId ().toString ())) {
                        SetPeliculas array[] = db.getSetsPeliculas ().toArray (new SetPeliculas [0]);

                        int nuevas = 0;
                        for (int i = 0; i < array.length; nuevas += array [i++].getNombre ()
                                .toLowerCase ()
                                .contains ("nuevo set de películas") ? 1 : 0)
                            ;

                        setpeliculas [0].setNombre (String.format ("Nuevo set de películas%s",
                                nuevas == 0 ? "" : String.format (" #%d", nuevas + 1)));
                    }

                    db.update (setpeliculas [0]);
                    setpeliculas [0] = null;
                }

                if (filters.get (0) != null)
                    filters.get (0).run ();

                if (filters.get (1) != null)
                    filters.get (1).run ();
            }
        });

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (pw [0] == null)
                    return;

                w.setVisible (true);
            }
        });

        if (w != null)
            w.addWindowListener (new WindowAdapter () {
                @Override
                public void windowClosed (WindowEvent e) {
                    f.dispose ();
                }
            });

        this.add (((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel (
                    admin != null && db.getAdministradores ().contains (admin) ? admin.getNombre () : "");
            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

            return l;
        })).get (), BorderLayout.PAGE_START);

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.Y_AXIS));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.X_AXIS));

                q.add (Box.createRigidArea (new Dimension (15, 0)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    JComboBox <Pelicula> peliculas = new JComboBox <Pelicula> (
                            new Vector <Pelicula> (db.getPeliculas ()));
                    peliculas.setRenderer (new PeliculasComboBoxRenderer ());
                    peliculas.setMaximumRowCount (5);
                    peliculas.setSelectedIndex (peliculas.getItemCount () > 0 ? 0 : -1);

                    JTextField nombre = new JTextField (new JTextFieldLimit (75), "", 48);
                    nombre.setToolTipText (
                            "Filtrar por nombre (el nombre de la pelicula debe contener el texto introducido).");

                    JTextField director = new JTextField (new JTextFieldLimit (50), "", 32);
                    director.setToolTipText (
                            "Filtrar por nombre (el nombre del director de la película debe contener el texto introducido).");

                    List <JCheckBox> generos = new ArrayList <JCheckBox> (Arrays.asList (new JCheckBox [] {
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Acción");

                                b.setToolTipText ("Incluir películas que tengan la acción como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Ciencia ficción");

                                b.setToolTipText (
                                        "Incluir películas que tengan la ciencia ficción como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Comedia");

                                b.setToolTipText ("Incluir películas que tengan la comedia como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Documental");

                                b.setToolTipText (
                                        "Incluir películas que tengan el documental como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Drama");

                                b.setToolTipText ("Incluir películas que tengan el drama como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Fantasía");

                                b.setToolTipText ("Incluir películas que tengan la fantasía como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Melodrama");

                                b.setToolTipText ("Incluir películas que tengan el melodrama como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Musical");

                                b.setToolTipText ("Incluir películas que tengan el musical como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Romance");

                                b.setToolTipText ("Incluir películas que tengan el romance como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Suspense");

                                b.setToolTipText ("Incluir películas que tengan el suspense como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Terror");

                                b.setToolTipText ("Incluir películas que tengan el terror como uno de sus géneros.");
                                b.setSelected (true);

                                return b;
                            })).get ()
                    }));

                    List <JCheckBox> edades = new ArrayList <JCheckBox> (Arrays.asList (new JCheckBox [] {
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Todas las edades");

                                b.setToolTipText ("Incluir películas aptas para todos los públicos.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("+7");

                                b.setToolTipText ("Incluir películas para mayores de 7 años.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("+12");

                                b.setToolTipText ("Incluir películas para mayores de 12 años.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("+16");

                                b.setToolTipText ("Incluir películas para mayores de 16 años.");
                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("+18");

                                b.setToolTipText ("Incluir películas para adultos.");
                                b.setSelected (true);

                                return b;
                            })).get ()
                    }));

                    JSpinner minVal = new JSpinner (new SpinnerNumberModel (1, 1, 10, 0.1));
                    minVal.setToolTipText ("La valoración mínima de la película (un número del 1 al 10).");

                    JSpinner maxVal = new JSpinner (new SpinnerNumberModel (10, 1, 10, 0.1));
                    maxVal.setToolTipText ("La valoración máxima de la película (un número del 1 al 10).");

                    JSpinner minDur = new JSpinner (new SpinnerNumberModel ());
                    ((SpinnerNumberModel) minDur.getModel ()).setMinimum (1);
                    ((SpinnerNumberModel) minDur.getModel ())
                            .setValue (((SpinnerNumberModel) minDur.getModel ()).getMinimum ());
                    minDur.setToolTipText ("La duración mínima de la película (en minutos).");

                    JSpinner maxDur = new JSpinner (new SpinnerNumberModel ());
                    ((SpinnerNumberModel) maxDur.getModel ()).setMinimum (1);
                    ((SpinnerNumberModel) maxDur
                            .getModel ())
                                    .setValue (db.getPeliculas ().isEmpty () ? Integer.MAX_VALUE
                                            : Pelicula
                                                    .orderBy (db.getPeliculas (),
                                                            (Comparator <Pelicula>) ( (Pelicula x, Pelicula y) -> x
                                                                    .getDuracion ().compareTo (y.getDuracion ())),
                                                            true)
                                                    .get (0).getDuracion ().toMinutes ());
                    maxDur.setToolTipText ("La duración máxima de la película (en minutos).");

                    JSpinner minFecha = new JSpinner (
                            new SpinnerNumberModel (Pelicula.minFecha ().getValue (),
                                    Pelicula.minFecha ().getValue (), Pelicula.maxFecha ().getValue (), 1));
                    minFecha.setToolTipText (
                            String.format ("El año mínimo de salida de la película (desde el %s al %s).",
                                    Pelicula.minFecha (), Pelicula.maxFecha ()));

                    JSpinner maxFecha = new JSpinner (
                            new SpinnerNumberModel (Pelicula.maxFecha ().getValue (),
                                    Pelicula.minFecha ().getValue (), Pelicula.maxFecha ().getValue (), 1));
                    maxFecha.setToolTipText (
                            String.format ("El año máximo de salida de la película (desde el %s al %s).",
                                    Pelicula.minFecha (), Pelicula.maxFecha ()));

                    ButtonGroup orderBy = new ButtonGroup ();

                    JCheckBox desc = new JCheckBox ("Orden descendente");

                    filters.set (0, () -> {
                        if (((SpinnerNumberModel) minVal.getModel ()).getNumber ()
                                .doubleValue () < 1
                                || ((SpinnerNumberModel) minVal.getModel ()).getNumber ()
                                        .doubleValue () > 10
                                || ((SpinnerNumberModel) maxVal.getModel ()).getNumber ()
                                        .doubleValue () < 1
                                || ((SpinnerNumberModel) maxVal.getModel ()).getNumber ()
                                        .doubleValue () > 10) {
                            JOptionPane.showMessageDialog (f,
                                    "La valoración debe ser un número entre el 1 y el 10.",
                                    "Error al filtrar", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (((SpinnerNumberModel) minFecha.getModel ()).getNumber ()
                                .intValue () < Pelicula.minFecha ().getValue ()
                                || ((SpinnerNumberModel) minFecha.getModel ()).getNumber ()
                                        .intValue () > Pelicula.maxFecha ().getValue ()
                                || ((SpinnerNumberModel) maxFecha.getModel ()).getNumber ()
                                        .intValue () < Pelicula.minFecha ().getValue ()
                                || ((SpinnerNumberModel) maxFecha.getModel ()).getNumber ()
                                        .intValue () > Pelicula.maxFecha ().getValue ()) {
                            JOptionPane.showMessageDialog (f,
                                    String.format ("La fecha de salida estar entre el %s y el %s.",
                                            Pelicula.minFecha ().getValue (), Pelicula.maxFecha ().getValue ()),
                                    "Error al filtrar", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (((SpinnerNumberModel) minVal.getModel ()).getNumber ()
                                .doubleValue () > ((SpinnerNumberModel) maxVal.getModel ()).getNumber ()
                                        .doubleValue ()) {
                            JOptionPane.showMessageDialog (f,
                                    "La valoración mínima no puede ser mayor que la valoración máxima.",
                                    "Error al filtrar", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (((SpinnerNumberModel) minFecha.getModel ()).getNumber ()
                                .intValue () > ((SpinnerNumberModel) maxFecha.getModel ()).getNumber ()
                                        .intValue ()) {
                            JOptionPane.showMessageDialog (f,
                                    "La fecha de salida mínima no puede ser mayor que la fecha de salida máxima.",
                                    "Error al filtrar", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        peliculas.removeAllItems ();

                        // Copiar y pegar es una guarrada pero no me apetece
                        // nada crear una variable.
                        List <Pelicula> list = Pelicula.tree (db.getPeliculas (),
                                desc.isSelected ()
                                        ? (Comparator <Pelicula>) ((new Comparator [] {
                                                (Object x, Object y) -> ((Pelicula) y)
                                                        .getNombre ()
                                                        .compareToIgnoreCase (
                                                                ((Pelicula) x).getNombre ()),
                                                (Object x,
                                                        Object y) -> ((Double) ((Pelicula) y)
                                                                .getValoracion ()).compareTo (
                                                                        (Double) ((Pelicula) x)
                                                                                .getValoracion ()),
                                                (Object x, Object y) -> ((Pelicula) y)
                                                        .getFecha ()
                                                        .compareTo (
                                                                ((Pelicula) x).getFecha ()),
                                                (Object x, Object y) -> ((Pelicula) y)
                                                        .getDirector ()
                                                        .compareToIgnoreCase (
                                                                ((Pelicula) x).getDirector ()),
                                                (Object x, Object y) -> ((Pelicula) y)
                                                        .getDuracion ()
                                                        .compareTo (
                                                                ((Pelicula) x)
                                                                        .getDuracion ()) }) [((IntSupplier) ( () -> {
                                                                            List <AbstractButton> l = Collections
                                                                                    .list (orderBy
                                                                                            .getElements ());
                                                                            for (int i = 0; i < l
                                                                                    .size (); i++)
                                                                                if (l.get (i)
                                                                                        .isSelected ())
                                                                                    return i;

                                                                            return 0;
                                                                        })).getAsInt ()])
                                        : (Comparator <Pelicula>) ((new Comparator [] {
                                                (Object x, Object y) -> ((Pelicula) x)
                                                        .getNombre ()
                                                        .compareToIgnoreCase (
                                                                ((Pelicula) y).getNombre ()),
                                                (Object x,
                                                        Object y) -> ((Double) ((Pelicula) x)
                                                                .getValoracion ()).compareTo (
                                                                        (Double) ((Pelicula) y)
                                                                                .getValoracion ()),
                                                (Object x, Object y) -> ((Pelicula) x)
                                                        .getFecha ()
                                                        .compareTo (
                                                                ((Pelicula) y).getFecha ()),
                                                (Object x, Object y) -> ((Pelicula) x)
                                                        .getDirector ()
                                                        .compareToIgnoreCase (
                                                                ((Pelicula) y).getDirector ()),
                                                (Object x, Object y) -> ((Pelicula) x)
                                                        .getDuracion ()
                                                        .compareTo (
                                                                ((Pelicula) y)
                                                                        .getDuracion ()) }) [((IntSupplier) ( () -> {
                                                                            List <AbstractButton> l = Collections
                                                                                    .list (orderBy
                                                                                            .getElements ());
                                                                            for (int i = 0; i < l
                                                                                    .size (); i++)
                                                                                if (l.get (i)
                                                                                        .isSelected ())
                                                                                    return i;

                                                                            return 0;
                                                                        })).getAsInt ()]),
                                (Filter <Pelicula>) ( (Pelicula x) -> x.getNombre ().toLowerCase (Locale.ROOT)
                                        .contains (nombre.getText ().replace ("'", "").replace ("\"", "")
                                                .replace ("`", "").toLowerCase (Locale.ROOT))
                                        && x.getValoracion () >= ((SpinnerNumberModel) minVal
                                                .getModel ()).getNumber ().doubleValue ()
                                        && x.getValoracion () <= ((SpinnerNumberModel) maxVal
                                                .getModel ()).getNumber ().doubleValue ()
                                        && x.getFecha ().compareTo (Year
                                                .of (((SpinnerNumberModel) minFecha.getModel ())
                                                        .getNumber ().intValue ())) >= 0
                                        && x.getFecha ().compareTo (Year
                                                .of (((SpinnerNumberModel) maxFecha.getModel ())
                                                        .getNumber ().intValue ())) <= 0
                                        && x.getDirector ().toLowerCase (Locale.ROOT)
                                                .contains (director.getText ().toLowerCase (Locale.ROOT))
                                        && x.getDuracion ().compareTo (Duration.ofMinutes (
                                                ((SpinnerNumberModel) minDur.getModel ())
                                                        .getNumber ().longValue ())) >= 0
                                        && x.getDuracion ().compareTo (Duration.ofMinutes (
                                                ((SpinnerNumberModel) maxDur.getModel ())
                                                        .getNumber ().longValue ())) <= 0
                                        && ((BooleanSupplier) ( () -> {
                                            for (int i = 0; i < edades.size (); i++)
                                                if (edades.get (i).isSelected ()
                                                        && x.getEdad ()
                                                                .getValue () == EdadRecomendada
                                                                        .values () [i]
                                                                                .getValue ())
                                                    return true;

                                            return false;
                                        })).getAsBoolean () && ((BooleanSupplier) ( () -> {
                                            short values = 0;

                                            for (int i = 0; i < generos.size (); i++)
                                                if (generos.get (i).isSelected ())
                                                    values += 1 << i;

                                            return (Genero.Nombre.toValor (x.getGeneros ())
                                                    & values) != 0;
                                        })).getAsBoolean ()))
                                .getValues ();

                        for (int i = 0; i < list.size (); peliculas.addItem (list.get (i++)))
                            ;

                        peliculas.repaint ();
                    });
                    ActionListener filterAL = e -> filters.get (0).run ();
                    ChangeListener filterCL = e -> filters.get (0).run ();
                    DocumentListener filterDL = new DocumentListener () {
                        @Override
                        public void insertUpdate (DocumentEvent e) {
                            this.changedUpdate (e);
                        }

                        @Override
                        public void removeUpdate (DocumentEvent e) {
                            this.changedUpdate (e);
                        }

                        @Override
                        public void changedUpdate (DocumentEvent e) {
                            nombre.postActionEvent ();
                        }
                    };

                    nombre.addActionListener (filterAL);
                    nombre.getDocument ().addDocumentListener (filterDL);
                    director.addActionListener (filterAL);
                    director.getDocument ().addDocumentListener (filterDL);
                    for (int i = 0; i < generos.size (); generos.get (i++).addActionListener (filterAL))
                        ;
                    for (int i = 0; i < edades.size (); edades.get (i++).addActionListener (filterAL))
                        ;
                    minVal.addChangeListener (filterCL);
                    maxVal.addChangeListener (filterCL);
                    minDur.addChangeListener (filterCL);
                    maxDur.addChangeListener (filterCL);
                    minFecha.addChangeListener (filterCL);
                    maxFecha.addChangeListener (filterCL);
                    desc.addActionListener (filterAL);

                    JButton peliculaButtons[] = new JButton [] {
                            ((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Ver detalles");

                                b.setEnabled (peliculas.getSelectedItem () != null);
                                b.addActionListener (e -> {
                                    try {
                                        pw [0] = null;
                                        f.setVisible (false);
                                        pw [0] = w;

                                        new PeliculaDetailsWindow ((Pelicula) peliculas.getSelectedItem (), f);
                                    }

                                    catch (NullPointerException e1) {
                                        JOptionPane.showMessageDialog (f,
                                                "La película seleccionada es nula por lo que no pueden verse sus detalles.",
                                                "Error al ver los detalles de la película", JOptionPane.ERROR_MESSAGE);

                                        f.setVisible (true);
                                    }
                                });

                                return b;
                            })).get (),
                            ((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Modificar");

                                b.setEnabled (false);
                                b.addActionListener (e -> {
                                    pw [0] = null;
                                    f.setVisible (false);
                                    pw [0] = w;

                                    pelicula [0] = (Pelicula) peliculas.getSelectedItem ();
                                    new PeliculaWindow (pelicula, f);
                                });

                                return b;
                            })).get (),
                            ((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Eliminar");

                                b.setEnabled (false);
                                b.addActionListener (e -> {
                                    if (JOptionPane.showOptionDialog (f,
                                            "Lo que estás a punto de hacer es una acción irreversible.\n¿Estás seguro de querer continuar?",
                                            "Eliminar película", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.WARNING_MESSAGE,
                                            null, new String [] {
                                                    "Confirmar",
                                                    "Cancelar"
                                            }, JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                        return;

                                    db.delete ((Pelicula) (peliculas.getSelectedItem ()));
                                    filters.get (0).run ();
                                    filters.get (1).run ();
                                });

                                return b;
                            })).get ()
                    };

                    peliculas.addActionListener (e -> {
                        peliculaButtons [0].setEnabled (peliculas.getSelectedItem () != null);

                        if (peliculas.getSelectedItem () == null
                                || ((Pelicula) peliculas.getSelectedItem ()).isDefault ()) {
                            peliculaButtons [1].setEnabled (false);
                            peliculaButtons [2].setEnabled (false);

                            return;
                        }

                        peliculaButtons [1].setEnabled (true);
                        peliculaButtons [2].setEnabled (true);
                    });

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Películas");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 20f));

                        return l;
                    })).get ());
                    r.add (Box.createRigidArea (new Dimension (0, 25)));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel ();
                        s.setLayout (new BoxLayout (s, BoxLayout.X_AXIS));

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (((Supplier <JPanel>) ( () -> {
                                JPanel u = new JPanel ();
                                u.setLayout (new BoxLayout (u, BoxLayout.Y_AXIS));

                                u.add (((Supplier <JLabel>) ( () -> {
                                    JLabel l = new JLabel ("Filtrar");
                                    l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                            16f));

                                    return l;
                                })).get ());

                                u.add (Box.createRigidArea (new Dimension (0, 15)));

                                u.add (((Supplier <JPanel>) ( () -> {
                                    JPanel v = new JPanel ();
                                    v.setLayout (new BoxLayout (v, BoxLayout.Y_AXIS));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Nombre:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (nombre);

                                        return ww;
                                    })).get ());
                                    v.add (Box.createRigidArea (new Dimension (0, 10)));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Valoración:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new FlowLayout (FlowLayout.LEFT));

                                            x.add (new JLabel ("desde"));
                                            x.add (minVal);

                                            return x;
                                        })).get ());

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new FlowLayout (FlowLayout.LEFT));

                                            x.add (new JLabel ("a"));
                                            x.add (maxVal);

                                            return x;
                                        })).get ());

                                        return ww;
                                    })).get ());
                                    v.add (Box.createRigidArea (new Dimension (0, 10)));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Fecha:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new FlowLayout (FlowLayout.LEFT));

                                            x.add (new JLabel ("desde"));
                                            x.add (minFecha);

                                            return x;
                                        })).get ());

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new FlowLayout (FlowLayout.LEFT));

                                            x.add (new JLabel ("a"));
                                            x.add (maxFecha);

                                            return x;
                                        })).get ());

                                        return ww;
                                    })).get ());
                                    v.add (Box.createRigidArea (new Dimension (0, 10)));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Director:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (director);

                                        return ww;
                                    })).get ());
                                    v.add (Box.createRigidArea (new Dimension (0, 10)));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Duración:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new FlowLayout (FlowLayout.LEFT));

                                            x.add (new JLabel ("de"));
                                            x.add (minDur);
                                            x.add (new JLabel ("min"));

                                            return x;
                                        })).get ());

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new FlowLayout (FlowLayout.LEFT));

                                            x.add (new JLabel ("a"));
                                            x.add (maxDur);
                                            x.add (new JLabel ("min"));

                                            return x;
                                        })).get ());

                                        return ww;
                                    })).get ());
                                    v.add (Box.createRigidArea (new Dimension (0, 10)));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.CENTER, 10, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Géneros:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new GridLayout (2, 6, 15, 15));

                                            for (int i = 0; i < generos.size (); x.add (generos.get (i++)))
                                                ;

                                            return x;
                                        })).get ());

                                        return ww;
                                    })).get ());
                                    v.add (Box.createRigidArea (new Dimension (0, 10)));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel ();
                                        ww.setLayout (new FlowLayout (FlowLayout.CENTER, 25, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Edad recomendada:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new GridLayout (2, 3, 15, 15));

                                            for (int i = 0; i <= 4; x.add (edades.get (i++)))
                                                ;

                                            return x;
                                        })).get ());

                                        return ww;
                                    })).get ());

                                    return v;
                                })).get ());

                                return u;
                            })).get ());

                            t.add (Box.createRigidArea (new Dimension (0, 25)));

                            t.add (((Supplier <JPanel>) ( () -> {
                                JPanel u = new JPanel ();
                                u.setLayout (new BoxLayout (u, BoxLayout.Y_AXIS));

                                u.add (((Supplier <JLabel>) ( () -> {
                                    JLabel l = new JLabel ("Ordenar");
                                    l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                            16f));

                                    return l;
                                })).get ());

                                u.add (((Supplier <JPanel>) ( () -> {
                                    JPanel v = new JPanel (new FlowLayout (FlowLayout.CENTER, 0, 25));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new GridLayout (2, 3, 15, 15));

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Nombre");

                                            b.setSelected (true);
                                            b.addActionListener (filterAL);

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Valoracion");

                                            b.addActionListener (filterAL);

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Fecha");

                                            b.addActionListener (filterAL);

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Director");

                                            b.addActionListener (filterAL);

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Duracion");

                                            b.addActionListener (filterAL);

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (Box.createRigidArea (new Dimension (0, 0)));

                                        return ww;
                                    })).get ());

                                    v.add (desc);

                                    return v;
                                })).get ());

                                return u;
                            })).get ());

                            t.add (Box.createRigidArea (new Dimension (0, 25)));

                            t.add (((Supplier <JPanel>) ( () -> {
                                JPanel u = new JPanel ();

                                u.add (peliculas, BorderLayout.CENTER);

                                return u;
                            })).get ());

                            return t;
                        })).get ());

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (Box.createRigidArea (new Dimension (0, 500)));

                            t.add (peliculaButtons [0]);

                            t.add (Box.createRigidArea (new Dimension (0, 25)));

                            t.add (peliculaButtons [1]);

                            t.add (Box.createRigidArea (new Dimension (0, 25)));

                            t.add (peliculaButtons [2]);

                            return t;
                        })).get ());

                        return s;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (0, 75)));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new GridLayout (2, 3, 25, 25));

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Crear");

                            b.addActionListener (e -> {
                                pw [0] = null;
                                f.setVisible (false);
                                pw [0] = w;

                                new PeliculaWindow (pelicula, f);
                            });

                            return b;
                        })).get ());

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Importar");

                            b.addActionListener (e -> {
                                JSONChooser fc;
                                if ((fc = new JSONChooser ()).showOpenDialog (f) != JFileChooser.APPROVE_OPTION)
                                    return;

                                new LoadingWindow ( () -> {
                                    List <Pelicula> l;
                                    try {
                                        l = Pelicula.fromJSON (fc.getSelectedFile ());
                                    }

                                    catch (NullPointerException | IOException ex) {
                                        JOptionPane.showMessageDialog (f,
                                                "No pudo abrirse el archivo especificado.");

                                        return;
                                    }

                                    catch (JSONException ex) {
                                        JOptionPane.showMessageDialog (f,
                                                "El archivo especificado no es un archivo JSON válido.");

                                        return;
                                    }

                                    l.removeAll (Pelicula.getDefault ());
                                    db.update (l);
                                });
                            });

                            return b;
                        })).get ());

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Exportar");

                            b.addActionListener (e -> {
                                JSONChooser fc;
                                if ((fc = new JSONChooser ()).showSaveDialog (f) != JFileChooser.APPROVE_OPTION)
                                    return;

                                String str;
                                try {
                                    str = Pelicula.toJSON (db.getPeliculas (), true);
                                }

                                catch (NullPointerException | JSONException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "Las películas no pudieron ser exportados a JSON.");

                                    try {
                                        Files.delete (fc.getSelectedFile ().toPath ());
                                    }

                                    catch (IOException e1) {
                                        Logger.getLogger (GestionarPeliculasWindow.class.getName ()).log (Level.WARNING,
                                                String.format ("No se pudo eliminar el archivo %s.",
                                                        fc.getSelectedFile ().getAbsolutePath ()));
                                    }

                                    return;
                                }

                                try (BufferedWriter bw = new BufferedWriter (new FileWriter (fc.getSelectedFile ()))) {
                                    bw.write (str);
                                }

                                catch (IOException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "No pudo abrirse el archivo especificado.");

                                    try {
                                        Files.delete (fc.getSelectedFile ().toPath ());
                                    }

                                    catch (IOException e1) {
                                        Logger.getLogger (GestionarPeliculasWindow.class.getName ()).log (Level.WARNING,
                                                String.format ("No se pudo eliminar el archivo %s.",
                                                        fc.getSelectedFile ().getAbsolutePath ()));
                                    }

                                    return;
                                }
                            });

                            return b;
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 0)));

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Eliminar todas las películas");

                            b.addActionListener (e -> {
                                if (JOptionPane.showOptionDialog (f,
                                        "Lo que estás a punto de hacer es una acción irreversible.\n¿Estás seguro de querer continuar?",
                                        "Eliminar todas las películas", JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE,
                                        null, new String [] {
                                                "Confirmar",
                                                "Cancelar"
                                        }, JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                    return;

                                new LoadingWindow ( () -> {
                                    List <Pelicula> l = db.getPeliculas ();
                                    l.removeAll (Pelicula.getDefault ());
                                    db.delete (l);

                                    filters.get (0).run ();
                                    filters.get (1).run ();
                                });
                            });

                            return b;
                        })).get ());

                        return s;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (15, 0)));
                q.add (new JSeparator (SwingConstants.VERTICAL));
                q.add (Box.createRigidArea (new Dimension (15, 0)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    JComboBox <SetPeliculas> setspeliculas = new JComboBox <SetPeliculas> (
                            new Vector <SetPeliculas> (db.getSetsPeliculas ().stream ()
                                    .filter (e -> e.getAdministrador () == null || e.getAdministrador ().equals (admin))
                                    .collect (Collectors.toList ())));
                    setspeliculas.setRenderer (new SetsPeliculasComboBoxRenderer ());
                    setspeliculas.setMaximumRowCount (5);
                    setspeliculas.setSelectedIndex (setspeliculas.getItemCount () > 0 ? 0 : -1);

                    JTextField nombre = new JTextField (new JTextFieldLimit (75), "", 48);
                    nombre.setToolTipText (
                            "Filtrar por nombre (el nombre del set debe contener el texto introducido).");

                    JSpinner minSize = new JSpinner (new SpinnerNumberModel (7, 7, 35, 1));
                    minSize.setToolTipText ("El tamaño mínimo del set (un número del 7 al 35).");

                    JSpinner maxSize = new JSpinner (new SpinnerNumberModel (35, 7, 35, 1));
                    maxSize.setToolTipText ("El tamaño máximo del set (un número del 7 al 35).");

                    ButtonGroup orderBy = new ButtonGroup ();

                    JCheckBox desc = new JCheckBox ("Orden descendente");

                    filters.set (1, () -> {
                        if (((SpinnerNumberModel) minSize.getModel ()).getNumber ().intValue () < SetPeliculas
                                .minSize ()
                                || ((SpinnerNumberModel) minSize.getModel ()).getNumber ()
                                        .intValue () > SetPeliculas.maxSize ()
                                || ((SpinnerNumberModel) maxSize.getModel ()).getNumber ()
                                        .intValue () < SetPeliculas.minSize ()
                                || ((SpinnerNumberModel) maxSize.getModel ()).getNumber ()
                                        .intValue () > SetPeliculas.maxSize ()) {
                            JOptionPane.showMessageDialog (f,
                                    String.format ("El tamaño del set debe ser un número en el intervalo [%d, %d].",
                                            SetPeliculas.minSize (), SetPeliculas.maxSize ()),
                                    "Error al filtrar", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (((SpinnerNumberModel) minSize.getModel ()).getNumber ()
                                .intValue () > ((SpinnerNumberModel) maxSize.getModel ()).getNumber ()
                                        .intValue ()) {
                            JOptionPane.showMessageDialog (f,
                                    "El tamaño máximo no puede ser menor que el tamaño mínimo.",
                                    "Error al filtrar", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        setspeliculas.removeAllItems ();

                        List <SetPeliculas> list = SetPeliculas.tree (db.getSetsPeliculas ().stream ()
                                .filter (x -> x.getAdministrador () == null || x.getAdministrador ().equals (admin))
                                .collect (Collectors.toList ()),
                                (Collections.list (orderBy.getElements ()).get (0).isSelected ()
                                        ? 0
                                        : 1) == 0
                                                ? (desc.isSelected ()
                                                        ? (Comparator <SetPeliculas>) ( (SetPeliculas x,
                                                                SetPeliculas y) -> y.getNombre ()
                                                                        .compareToIgnoreCase (x.getNombre ()))
                                                        : (Comparator <SetPeliculas>) ( (SetPeliculas x,
                                                                SetPeliculas y) -> x.getNombre ()
                                                                        .compareToIgnoreCase (y.getNombre ())))
                                                : (desc.isSelected ()
                                                        ? (Comparator <SetPeliculas>) ( (SetPeliculas x,
                                                                SetPeliculas y) -> ((Integer) y.size ())
                                                                        .compareTo ((Integer) x.size ()))
                                                        : (Comparator <SetPeliculas>) ( (SetPeliculas x,
                                                                SetPeliculas y) -> ((Integer) x.size ())
                                                                        .compareTo ((Integer) y.size ()))),
                                (Filter <SetPeliculas>) ( (SetPeliculas x) -> x.getNombre ()
                                        .toLowerCase (Locale.ROOT)
                                        .contains (nombre.getText ().replace ("'", "").replace ("\"", "")
                                                .replace ("`", "").toLowerCase (Locale.ROOT))
                                        && x.size () >= ((SpinnerNumberModel) minSize.getModel ()).getNumber ()
                                                .intValue ()
                                        && x.size () <= ((SpinnerNumberModel) maxSize.getModel ()).getNumber ()
                                                .intValue ()))
                                .getValues ();

                        for (int i = 0; i < list.size (); setspeliculas.addItem (list.get (i++)))
                            ;
                        ;
                    });
                    ActionListener filterAL = e -> filters.get (1).run ();
                    ChangeListener filterCL = e -> filters.get (1).run ();

                    nombre.addActionListener (filterAL);
                    minSize.addChangeListener (filterCL);
                    maxSize.addChangeListener (filterCL);
                    desc.addActionListener (filterAL);

                    JButton setButtons[] = new JButton [] {
                            ((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Ver detalles");

                                b.setEnabled (setspeliculas.getSelectedItem () != null);
                                b.addActionListener (e -> {
                                    try {
                                        pw [0] = null;
                                        f.setVisible (false);
                                        pw [0] = w;

                                        new SetPeliculasDetailsWindow (
                                                (SetPeliculas) setspeliculas.getSelectedItem (), f);
                                    }

                                    catch (NullPointerException e1) {
                                        f.setVisible (false);
                                        JOptionPane.showMessageDialog (f,
                                                "El set de películas seleccionado es nulo por lo que no pueden verse sus detalles.",
                                                "Error al ver los detalles del set de películas",
                                                JOptionPane.ERROR_MESSAGE);
                                        f.setVisible (true);
                                    }
                                });

                                return b;
                            })).get (),
                            ((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Marcar como activo");
                                
                                b.setEnabled (false);
                                b.addActionListener (e -> {
                                    Settings.setActiveSet ((SetPeliculas) setspeliculas.getSelectedItem ());

                                    b.setEnabled (false);
                                });

                                return b;
                            })).get (),
                            ((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Modificar");

                                b.setEnabled (false);
                                b.addActionListener (e -> {
                                    try {
                                        pw [0] = null;
                                        f.setVisible (false);
                                        pw [0] = w;

                                        setpeliculas [0] = (SetPeliculas) setspeliculas.getSelectedItem ();
                                        new SetPeliculasWindow (setpeliculas, db.getPeliculas (), admin, f);
                                    }

                                    catch (NullPointerException e1) {
                                        f.setVisible (false);
                                        JOptionPane.showMessageDialog (f,
                                                "El set de películas seleccionado es nulo por lo que no puede modificarse.",
                                                "Error al modificar el set de películas", JOptionPane.ERROR_MESSAGE);
                                        f.setVisible (true);
                                    }
                                });

                                return b;
                            })).get (),
                            ((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Eliminar");

                                b.setEnabled (false);
                                b.addActionListener (e -> {
                                    if (JOptionPane.showOptionDialog (f,
                                            "Lo que estás a punto de hacer es una acción irreversible.\n¿Estás seguro de querer continuar?",
                                            "Eliminar set de películas", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.WARNING_MESSAGE,
                                            null, new String [] {
                                                    "Confirmar",
                                                    "Cancelar"
                                            }, JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                        return;

                                    db.delete ((SetPeliculas) setspeliculas.getSelectedItem ());
                                    filters.get (1).run ();
                                });

                                return b;
                            })).get ()
                    };

                    setspeliculas.addActionListener (e -> {
                        setButtons [0].setEnabled (setspeliculas.getSelectedItem () != null);

                        setButtons [2].setEnabled (setspeliculas.getSelectedItem () != null
                                && !((SetPeliculas) setspeliculas.getSelectedItem ())
                                        .equals (Settings.getActiveSet ()));

                        if (setspeliculas.getSelectedItem () == null
                                || ((SetPeliculas) setspeliculas.getSelectedItem ()).isDefault ()) {
                            setButtons [1].setEnabled (false);
                            setButtons [3].setEnabled (false);

                            return;
                        }

                        setButtons [1].setEnabled (true);
                        setButtons [3].setEnabled (true);
                    });

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Sets de películas");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 20f));

                        return l;
                    })).get ());
                    r.add (Box.createRigidArea (new Dimension (0, 25)));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel ();
                        s.setLayout (new BoxLayout (s, BoxLayout.X_AXIS));

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (((Supplier <JPanel>) ( () -> {
                                JPanel u = new JPanel ();
                                u.setLayout (new BoxLayout (u, BoxLayout.Y_AXIS));

                                u.add (((Supplier <JLabel>) ( () -> {
                                    JLabel l = new JLabel ("Filtrar");
                                    l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                            16f));

                                    return l;
                                })).get ());

                                u.add (Box.createRigidArea (new Dimension (0, 15)));

                                u.add (((Supplier <JPanel>) ( () -> {
                                    JPanel v = new JPanel ();
                                    v.setLayout (new BoxLayout (v, BoxLayout.Y_AXIS));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Nombre:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (nombre);

                                        return ww;
                                    })).get ());
                                    v.add (Box.createRigidArea (new Dimension (0, 10)));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Tamaño:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new FlowLayout (FlowLayout.LEFT));

                                            x.add (new JLabel ("desde"));
                                            x.add (minSize);

                                            return x;
                                        })).get ());

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new FlowLayout (FlowLayout.LEFT));

                                            x.add (new JLabel ("a"));
                                            x.add (maxSize);

                                            return x;
                                        })).get ());

                                        return ww;
                                    })).get ());

                                    return v;
                                })).get ());

                                return u;
                            })).get ());

                            t.add (Box.createRigidArea (new Dimension (0, 25)));

                            t.add (((Supplier <JPanel>) ( () -> {
                                JPanel u = new JPanel ();
                                u.setLayout (new BoxLayout (u, BoxLayout.Y_AXIS));

                                u.add (((Supplier <JLabel>) ( () -> {
                                    JLabel l = new JLabel ("Ordenar");
                                    l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                            16f));

                                    return l;
                                })).get ());

                                u.add (((Supplier <JPanel>) ( () -> {
                                    JPanel v = new JPanel (new FlowLayout (FlowLayout.CENTER, 25, 0));

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel (new GridLayout (2, 1, 15, 15));

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Nombre");

                                            b.setSelected (true);
                                            b.addActionListener (filterAL);

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Valoracion");

                                            b.addActionListener (filterAL);

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        return ww;
                                    })).get ());

                                    v.add (desc);

                                    return v;
                                })).get ());

                                return u;
                            })).get ());

                            t.add (Box.createRigidArea (new Dimension (0, 25)));

                            t.add (((Supplier <JPanel>) ( () -> {
                                JPanel u = new JPanel ();

                                u.add (setspeliculas, BorderLayout.CENTER);

                                return u;
                            })).get ());

                            return t;
                        })).get ());

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (Box.createRigidArea (new Dimension (0, 500)));

                            t.add (setButtons [0]);

                            t.add (Box.createRigidArea (new Dimension (0, 25)));

                            t.add (setButtons [1]);

                            t.add (Box.createRigidArea (new Dimension (0, 25)));

                            t.add (setButtons [2]);

                            return t;
                        })).get ());

                        return s;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (0, 75)));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new GridLayout (2, 3, 25, 25));

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Crear");

                            b.addActionListener (e -> {
                                pw [0] = null;
                                f.setVisible (false);
                                pw [0] = w;

                                new SetPeliculasWindow (setpeliculas, db.getPeliculas (), admin, f);
                            });

                            return b;
                        })).get ());

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Importar");

                            b.addActionListener (e -> {
                                JSONChooser fc;
                                if ((fc = new JSONChooser ()).showOpenDialog (f) != JFileChooser.APPROVE_OPTION)
                                    return;

                                new LoadingWindow ( () -> {
                                    List <SetPeliculas> l;
                                    try {
                                        l = SetPeliculas.fromJSON (fc.getSelectedFile ());
                                    }

                                    catch (NullPointerException | IOException ex) {
                                        JOptionPane.showMessageDialog (f,
                                                "No pudo abrirse el archivo especificado.");

                                        return;
                                    }

                                    catch (JSONException ex) {
                                        JOptionPane.showMessageDialog (f,
                                                "El archivo especificado no es un archivo JSON válido.");

                                        return;
                                    }

                                    l.remove (SetPeliculas.getDefault ());
                                    db.update (l);

                                    filters.get (1).run ();
                                });
                            });

                            return b;
                        })).get ());

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Exportar");

                            b.addActionListener (e -> {
                                JSONChooser fc;
                                if ((fc = new JSONChooser ()).showSaveDialog (f) != JFileChooser.APPROVE_OPTION)
                                    return;

                                String str;
                                try {
                                    str = SetPeliculas.toJSON (db.getSetsPeliculas (), true);
                                }

                                catch (NullPointerException | JSONException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "Los sets de películas no pudieron ser exportados a JSON.");

                                    try {
                                        Files.delete (fc.getSelectedFile ().toPath ());
                                    }

                                    catch (IOException e1) {
                                        Logger.getLogger (GestionarPeliculasWindow.class.getName ()).log (Level.WARNING,
                                                String.format ("No se pudo eliminar el archivo %s.",
                                                        fc.getSelectedFile ().getAbsolutePath ()));
                                    }

                                    return;
                                }

                                try (BufferedWriter bw = new BufferedWriter (new FileWriter (fc.getSelectedFile ()))) {
                                    bw.write (str);
                                }

                                catch (IOException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "No pudo abrirse el archivo especificado.");

                                    try {
                                        Files.delete (fc.getSelectedFile ().toPath ());
                                    }

                                    catch (IOException e1) {
                                        Logger.getLogger (GestionarPeliculasWindow.class.getName ()).log (Level.WARNING,
                                                String.format ("No se pudo eliminar el archivo %s.",
                                                        fc.getSelectedFile ().getAbsolutePath ()));
                                    }

                                    return;
                                }
                            });

                            return b;
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 0)));

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Eliminar todos los sets");

                            b.addActionListener (e -> {
                                if (JOptionPane.showOptionDialog (f,
                                        "Lo que estás a punto de hacer es una acción irreversible.\n¿Estás seguro de querer continuar?",
                                        "Eliminar todos los set de películas", JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE,
                                        null, new String [] {
                                                "Confirmar",
                                                "Cancelar"
                                        }, JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                    return;

                                new LoadingWindow ( () -> {
                                    List <SetPeliculas> l = db.getSetsPeliculas ();
                                    l.remove (SetPeliculas.getDefault ());
                                    db.delete (l);

                                    filters.get (1).run ();
                                });
                            });

                            return b;
                        })).get ());

                        return s;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (15, 0)));

                return q;
            })).get ());

            return p;
        })).get (), BorderLayout.CENTER);

        this.add (((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel (" ");
            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

            return l;
        })).get (), BorderLayout.PAGE_END);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Gestionar películas");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileView.hardDriveIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
