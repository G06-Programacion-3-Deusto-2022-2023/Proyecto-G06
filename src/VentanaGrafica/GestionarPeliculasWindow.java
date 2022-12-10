package VentanaGrafica;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Year;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import cine.Administrador;
import cine.EdadRecomendada;
import cine.Genero;
import cine.GestorBD;
import cine.Pelicula;
import internals.JTextFieldLimit;
import internals.bst.Filter;

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

        final class PeliculasComboBoxRenderer extends JLabel implements ListCellRenderer <Pelicula> {
            public PeliculasComboBoxRenderer () {
                this.setOpaque (true);
                this.setHorizontalAlignment (SwingConstants.CENTER);
                this.setVerticalAlignment (SwingConstants.CENTER);
            }

            @Override
            public Component getListCellRendererComponent (JList list, Pelicula value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value == null)
                    return this;

                this.setBackground (isSelected ? list.getSelectionBackground () : list.getBackground ());
                this.setForeground (isSelected ? list.getSelectionForeground () : list.getForeground ());

                this.setText (value.getNombre ());
                this.setIcon (new ImageIcon (((Supplier <Image>) ( () -> {
                    Image img;

                    try {
                        img = new ImageIcon (ImageIO.read (new File (value.getRutaImagen ()))).getImage ()
                                .getScaledInstance (64, 64, 0);
                    }

                    catch (IOException e) {
                        Logger.getLogger (GestionarPeliculasWindow.class.getName ()).log (Level.WARNING,
                                String.format ("No se pudo crear una imagen a partir del archivo %s",
                                        value.getRutaImagen ()));

                        img = new ImageIcon (getClass ()
                                .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ()
                                        .getScaledInstance (64, 64, 0);
                    }

                    return img;
                })).get ()));

                return this;
            }
        }

        GestionarPeliculasWindow f = this;

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (w == null)
                    return;

                w.setVisible (true);
            }
        });

        this.add (((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel (
                    admin != null && db.obtenerDatosAdministradores ().contains (admin) ? admin.getNombre () : "");
            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

            return l;
        })).get (), BorderLayout.PAGE_START);

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.Y_AXIS));

            p.add (Box.createRigidArea (new Dimension (0, 50)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.X_AXIS));

                q.add (Box.createRigidArea (new Dimension (15, 0)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    JComboBox <Pelicula> peliculas = new JComboBox <Pelicula> (
                            new Vector <Pelicula> (db.obtenerDatosPeliculas ()));
                    peliculas.setRenderer (((Supplier <PeliculasComboBoxRenderer>) ( () -> {
                        PeliculasComboBoxRenderer renderer = new PeliculasComboBoxRenderer ();
                        renderer.setPreferredSize (new Dimension (200, 100));

                        return renderer;
                    })).get ());
                    peliculas.setMaximumRowCount (5);

                    JTextField nombre = new JTextField (new JTextFieldLimit (75), "", 48);
                    nombre.setToolTipText (
                            "Filtrar por nombre (el nombre de la pelicula debe contener el texto introducido).");

                    JTextField director = new JTextField (new JTextFieldLimit (50), "", 32);
                    director.setToolTipText (
                            "Filtrar por nombre (el nombre del director de la película debe contener el texto introducido).");

                    List <JCheckBox> generos = new ArrayList <JCheckBox> (Arrays.asList (new JCheckBox [] {
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Acción");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Ciencia ficción");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Comedia");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Documental");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Drama");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Fantasía");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Melodrama");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Musical");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Romance");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Suspense");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Suspense");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Terror");

                                b.setSelected (true);

                                return b;
                            })).get ()
                    }));

                    List <JCheckBox> edades = new ArrayList <JCheckBox> (Arrays.asList (new JCheckBox [] {
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("Todas las edades");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("+7");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("+12");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("+16");

                                b.setSelected (true);

                                return b;
                            })).get (),
                            ((Supplier <JCheckBox>) ( () -> {
                                JCheckBox b = new JCheckBox ("+18");

                                b.setSelected (true);

                                return b;
                            })).get ()
                    }));

                    JSpinner minVal = new JSpinner (new SpinnerNumberModel (1, 1, 10, 0.1));
                    minVal.setToolTipText ("La valoración mínima de la película (un número del 1 al 10).");

                    JSpinner maxVal = new JSpinner (new SpinnerNumberModel (10, 1, 10, 0.1));
                    maxVal.setToolTipText ("La valoración máxima de la película (un número del 1 al 10).");

                    JSpinner minDur = new JSpinner (new SpinnerNumberModel ());
                    ((SpinnerNumberModel) minDur.getModel ()).setMinimum (0);
                    minDur.setToolTipText ("La duración mínima de la película (en minutos).");

                    JSpinner maxDur = new JSpinner (new SpinnerNumberModel ());
                    ((SpinnerNumberModel) maxDur.getModel ()).setMinimum (0);
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

                    JButton filterButton = ((Supplier <JButton>) ( () -> {
                        JButton b = new JButton (new ImageIcon (
                                f.getClass ()
                                        .getResource ("/toolbarButtonGraphics/general/Search24.gif")));

                        b.addActionListener (e -> {
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
                            List <Pelicula> list = Pelicula.tree (db.obtenerDatosPeliculas (),
                                    desc.isSelected ()
                                            ? (Comparator <Pelicula>) ((new Comparator [] {
                                                    (Object x, Object y) -> ((Pelicula) y)
                                                            .getNombre ()
                                                            .compareTo (
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
                                                            .compareTo (
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
                                                            .compareTo (
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
                                                            .compareTo (
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
                                    (Filter <Pelicula>) ( (Pelicula x) -> x.getNombre ()
                                            .contains (nombre.getText ())
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
                                            && x.getDirector ().contains (director.getText ())
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
                                                        | values) != 0;
                                            })).getAsBoolean ()))
                                    .getValues ();

                            for (int i = 0; i < list.size (); peliculas.addItem (list.get (i++)))
                                ;

                            peliculas.repaint ();
                        });

                        return b;
                    })).get ();

                    JButton peliculaButtons[] = new JButton [] {
                            ((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Ver detalles");

                                b.setEnabled (false);
                                b.addActionListener (e -> {
                                    try {
                                        new PeliculaDetailsWindow ((Pelicula) peliculas.getSelectedItem (), f);
                                        f.dispose ();
                                    }

                                    catch (NullPointerException e1) {
                                        f.setVisible (false);
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

                                    filterButton.doClick (0);
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
                    r.add (Box.createRigidArea (new Dimension (0, 50)));

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
                                        JPanel ww = new JPanel (new FlowLayout (FlowLayout.CENTER, 10, 0));

                                        ww.add (((Supplier <JLabel>) ( () -> {
                                            JLabel l = new JLabel ("Géneros:");
                                            l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                                    14f));

                                            return l;
                                        })).get ());
                                        ww.add (Box.createRigidArea (new Dimension (10, 0)));

                                        ww.add (((Supplier <JPanel>) ( () -> {
                                            JPanel x = new JPanel (new GridLayout (3, 4, 15, 15));

                                            for (int i = 0; i <= 11; x.add (generos.get (i++)))
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
                                })).get (), BorderLayout.CENTER);

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

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Valoracion");

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Fecha");

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Director");

                                            orderBy.add (b);

                                            return b;
                                        })).get ());

                                        ww.add (((Supplier <JRadioButton>) ( () -> {
                                            JRadioButton b = new JRadioButton ("Duracion");

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

                            t.add (filterButton);

                            t.add (Box.createRigidArea (new Dimension (0, 100)));

                            t.add (((Supplier <JPanel>) ( () -> {
                                JPanel u = new JPanel ();

                                u.add (peliculas, BorderLayout.CENTER);

                                return u;
                            })).get ());

                            return t;
                        })).get ());

                        s.add (new JLabel (" "));

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (Box.createRigidArea (new Dimension (0, 250)));

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

                                peliculas.repaint ();
                            });

                            return b;
                        })).get ());

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Importar");

                            b.addActionListener (e -> {

                            });

                            return b;
                        })).get ());

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Exportar");

                            b.addActionListener (e -> {

                            });

                            return b;
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 0)));

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Eliminar todas las películas");

                            b.addActionListener (e -> {

                            });

                            return b;
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 0)));

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

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Sets de películas");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 20f));

                        return l;
                    })).get ());
                    r.add (Box.createRigidArea (new Dimension (0, 50)));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel ();
                        s.setLayout (new BoxLayout (s, BoxLayout.X_AXIS));

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            JTextField nombre = new JTextField (new JTextFieldLimit (75), "", 48);
                            nombre.setToolTipText (
                                    "Filtrar por nombre (el nombre de la pelicula debe contener el texto introducido).");

                            return t;
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
                ((ImageIcon) UIManager.getIcon ("FileView.hardDriveIcon", new Locale ("es-ES"))).getImage ());
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
