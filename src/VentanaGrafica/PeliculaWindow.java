package VentanaGrafica;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.time.Year;
import java.time.Duration;
import java.util.SortedSet;
import java.util.TreeSet;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cine.Pelicula;
import internals.JTextFieldLimit;
import cine.EdadRecomendada;
import cine.Genero;

/*
 * Esta es la primera ventana que hice en el proyecto y se nota
 */

public class PeliculaWindow extends JFrame {
    public PeliculaWindow (Collection <Pelicula> peliculas) {
        this (peliculas, null);
    }

    public PeliculaWindow (Collection <Pelicula> peliculas, Pelicula pelicula) throws NullPointerException {
        super ();

        if (peliculas == null)
            throw new NullPointerException (
                    "No se puede pasar una colección nula de películas a la ventana de creación de películas.");

        PeliculaWindow f = this;

        JTextField nombre = new JTextField (new JTextFieldLimit (75), pelicula == null ? "" : pelicula.getNombre (),
                48);
        nombre.setToolTipText ("Dejar vacío este campo implica que el nombre de la película será su ID.");

        JTextField rutaImagen = new JTextField (pelicula == null ? "" : pelicula.getRutaImagen (), 75);
        rutaImagen.setToolTipText ("Deja vacío este campo para que la película use una imagen por defecto.");

        JSpinner valoracion = new JSpinner (
                new SpinnerNumberModel (pelicula == null ? 5 : pelicula.getValoracion (), 1, 10, 0.1));
        valoracion.setToolTipText ("La valoración de la película (un número del 1 al 10).");

        JSpinner fecha = new JSpinner (new SpinnerNumberModel (
                pelicula == null ? Pelicula.defaultFecha ().getValue () : pelicula.getFecha ().getValue (),
                Pelicula.minFecha ().getValue (), Pelicula.maxFecha ().getValue (), 1));
        fecha.setToolTipText (String.format ("El año de salida de la película (desde el %s al %s).",
                Pelicula.minFecha (), Pelicula.maxFecha ()));

        JTextField director = new JTextField (new JTextFieldLimit (50), pelicula == null ? "" : pelicula.getDirector (),
                32);
        director.setToolTipText (
                "Nombre del director de la película. Si se deja vacío se asume que la película no tiene director.");

        JSpinner horas = new JSpinner (new SpinnerNumberModel ());
        ((SpinnerNumberModel) horas.getModel ()).setMinimum (0);
        if (pelicula != null)
            ((SpinnerNumberModel) horas.getModel ()).setValue (pelicula.getDuracion ().toHoursPart ());

        JSpinner minutos = new JSpinner (
                new SpinnerNumberModel (pelicula == null ? 0 : pelicula.getDuracion ().toMinutesPart (), 0, 59, 1));

        ButtonGroup edad = new ButtonGroup ();
        EdadRecomendada edadValue[] = new EdadRecomendada [] {
                pelicula == null ? EdadRecomendada.TODOS : pelicula.getEdad () };

        List <JCheckBox> generos = new ArrayList <JCheckBox> ();
        SortedSet <Genero.Nombre> generosValues = new TreeSet <Genero.Nombre> (
                (Comparator <Genero.Nombre>) ( (Genero.Nombre a, Genero.Nombre b) -> Short
                        .compareUnsigned (a.getValue (), b.getValue ())));

        this.setLayout (new BoxLayout (this.getContentPane (), BoxLayout.Y_AXIS));

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r;
                    GroupLayout l = new GroupLayout (r = new JPanel ());
                    l.setHorizontalGroup (
                            l.createSequentialGroup ().addComponent (new JLabel ("Nombre:")).addComponent (nombre));

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r;
                    GroupLayout l = new GroupLayout (r = new JPanel ());
                    l.setHorizontalGroup (l.createSequentialGroup ().addComponent (new JLabel ("Imagen:"))
                            .addComponent (rutaImagen)
                            .addComponent (((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Seleccionar un archivo",
                                        UIManager.getIcon ("FileView.fileIcon", new Locale ("es-ES")));
                                b.setToolTipText ("Abre una ventana externa para seleccionar un archivo.");
                                b.addActionListener (e -> {
                                    JFileChooser fc;
                                    if ((fc = new JFileChooser ()).showOpenDialog (f) == JFileChooser.APPROVE_OPTION)
                                        rutaImagen.setText (fc.getSelectedFile ().getAbsolutePath ());
                                });

                                return b;
                            })).get ()));

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r;
                    GroupLayout l = new GroupLayout (r = new JPanel ());
                    l.setHorizontalGroup (
                            l.createSequentialGroup ().addComponent (new JLabel ("Valoración:"))
                                    .addComponent (valoracion));

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r;
                    GroupLayout l = new GroupLayout (r = new JPanel ());
                    l.setHorizontalGroup (
                            l.createSequentialGroup ().addComponent (new JLabel ("Año:")).addComponent (fecha));

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r;
                    GroupLayout l = new GroupLayout (r = new JPanel ());
                    l.setHorizontalGroup (
                            l.createSequentialGroup ().addComponent (new JLabel ("Director:")).addComponent (director));

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r;
                    GroupLayout l = new GroupLayout (r = new JPanel ());
                    l.setHorizontalGroup (
                            l.createSequentialGroup ().addComponent (new JLabel ("Duración:"))
                                    .addComponent (((Supplier <JPanel>) ( () -> {
                                        JPanel s;
                                        GroupLayout m = new GroupLayout (s = new JPanel ());
                                        m.setHorizontalGroup (
                                                m.createSequentialGroup ().addComponent (horas)
                                                        .addComponent (new JLabel ("h")).addComponent (minutos)
                                                        .addComponent (new JLabel ("min")));

                                        return s;
                                    })).get ()));

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r;
                    GroupLayout l = new GroupLayout (r = new JPanel ());
                    l.setHorizontalGroup (
                            l.createSequentialGroup ().addComponent (new JLabel ("Edad:"))
                                    .addComponent (((Supplier <JRadioButton>) ( () -> {
                                        JRadioButton b = new JRadioButton ("Todos los públicos", true);

                                        b.setSelected (edadValue [0] == EdadRecomendada.TODOS);
                                        b.addActionListener (e -> edadValue [0] = EdadRecomendada.TODOS);
                                        edad.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JRadioButton>) ( () -> {
                                        JRadioButton b = new JRadioButton ("+7");

                                        b.setSelected (edadValue [0] == EdadRecomendada.SIETE);
                                        b.addActionListener (e -> edadValue [0] = EdadRecomendada.SIETE);
                                        edad.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JRadioButton>) ( () -> {
                                        JRadioButton b = new JRadioButton ("+12");

                                        b.setSelected (edadValue [0] == EdadRecomendada.DOCE);
                                        b.addActionListener (e -> edadValue [0] = EdadRecomendada.DOCE);
                                        edad.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JRadioButton>) ( () -> {
                                        JRadioButton b = new JRadioButton ("+16");

                                        b.setSelected (edadValue [0] == EdadRecomendada.DIECISEIS);
                                        b.addActionListener (e -> edadValue [0] = EdadRecomendada.DIECISEIS);
                                        edad.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JRadioButton>) ( () -> {
                                        JRadioButton b = new JRadioButton ("+18");

                                        b.setSelected (edadValue [0] == EdadRecomendada.DIECIOCHO);
                                        b.addActionListener (e -> edadValue [0] = EdadRecomendada.DIECIOCHO);
                                        edad.add (b);

                                        return b;
                                    })).get ()));

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r;
                    GroupLayout l = new GroupLayout (r = new JPanel ());
                    l.setHorizontalGroup (
                            l.createSequentialGroup ().addComponent (new JLabel ("Géneros:"))
                                    .addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Acción");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.ACCION));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.ACCION)) {
                                                generosValues.remove (Genero.Nombre.ACCION);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.ACCION);
                                        });

                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Ciencia ficción");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ()
                                                                .contains (Genero.Nombre.CIENCIA_FICCION));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.CIENCIA_FICCION)) {
                                                generosValues.remove (Genero.Nombre.CIENCIA_FICCION);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.CIENCIA_FICCION);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Comedia");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.COMEDIA));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.COMEDIA)) {
                                                generosValues.remove (Genero.Nombre.COMEDIA);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.COMEDIA);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Documental");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.DOCUMENTAL));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.DOCUMENTAL)) {
                                                generosValues.remove (Genero.Nombre.DOCUMENTAL);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.DOCUMENTAL);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Drama");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.DRAMA));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.DRAMA)) {
                                                generosValues.remove (Genero.Nombre.DRAMA);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.DRAMA);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Fantasia");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.FANTASIA));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.FANTASIA)) {
                                                generosValues.remove (Genero.Nombre.FANTASIA);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.FANTASIA);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Melodrama");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.MELODRAMA));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.MELODRAMA)) {
                                                generosValues.remove (Genero.Nombre.MELODRAMA);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.MELODRAMA);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Musical");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.MUSICAL));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.MUSICAL)) {
                                                generosValues.remove (Genero.Nombre.MUSICAL);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.MUSICAL);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Romance");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.ROMANCE));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.ROMANCE)) {
                                                generosValues.remove (Genero.Nombre.ROMANCE);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.ROMANCE);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Suspense");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.SUSPENSE));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.SUSPENSE)) {
                                                generosValues.remove (Genero.Nombre.SUSPENSE);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.SUSPENSE);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()).addComponent (((Supplier <JCheckBox>) ( () -> {
                                        JCheckBox b = new JCheckBox ("Terror");

                                        b.setSelected (
                                                pelicula != null
                                                        && pelicula.getGeneros ().contains (Genero.Nombre.TERROR));
                                        b.addActionListener (e -> {
                                            if (generosValues.contains (Genero.Nombre.TERROR)) {
                                                generosValues.remove (Genero.Nombre.TERROR);

                                                return;
                                            }

                                            generosValues.add (Genero.Nombre.TERROR);
                                        });
                                        generos.add (b);

                                        return b;
                                    })).get ()));

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 50)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.CENTER, 15, 0));

                    r.add (((Supplier <JButton>) ( () -> {
                        JButton b = new JButton (pelicula == null ? "Añadir" : "Modificar");
                        b.addActionListener (e -> {
                            long d;
                            if ((d = ((SpinnerNumberModel) horas.getModel ()).getNumber ().longValue () * 60
                                    + ((SpinnerNumberModel) minutos.getModel ()).getNumber ().longValue ()) == 0) {
                                JOptionPane.showMessageDialog (f,
                                        "La película no puede tener una duración de 0 minutos.");

                                return;
                            }

                            if (!(rutaImagen.getText () == null || rutaImagen.getText ().equals ("")) &&
                                    (!new File (rutaImagen.getText ()).exists () || ((BooleanSupplier) ( () -> {
                                        try {
                                            return !Files.probeContentType (new File (rutaImagen.getText ()).toPath ())
                                                    .split ("/") [0].equals ("image");
                                        }

                                        catch (IOException | NullPointerException e1) {
                                            return true;
                                        }
                                    })).getAsBoolean ())) {
                                JOptionPane.showMessageDialog (f,
                                        "La ruta especificada para la imagen de la película debe ser nula o apuntar a una imagen accesible.");

                                return;
                            }

                            Pelicula np = new Pelicula (nombre.getText (), rutaImagen.getText (),
                                    (Double) valoracion.getValue (),
                                    Year.of (((SpinnerNumberModel) fecha.getModel ()).getNumber ().intValue ()),
                                    director.getText (),
                                    Duration.ofMinutes (d),
                                    edadValue [0],
                                    generosValues);

                            if (np.getNombre ().equals (np.getId ().toString ())) {
                                Pelicula array[] = peliculas.toArray (new Pelicula [0]);

                                int nuevas = 0;
                                for (int i = 0; i < array.length; nuevas += array [i++].getNombre ().toLowerCase ()
                                        .contains ("nueva película") ? 1 : 0)
                                    ;

                                np.setNombre (String.format ("Nueva película%s",
                                        nuevas == 0 ? "" : String.format (" #%d", nuevas + 1)));
                            }

                            peliculas.add (pelicula == null ? np : ((Supplier <Pelicula>) ( () -> {
                                pelicula.setNombre (np.getNombre ());
                                pelicula.setValoracion (np.getValoracion ());
                                pelicula.setFecha (np.getFecha ());
                                pelicula.setDirector (np.getDirector ());
                                pelicula.setDuracion (np.getDuracion ());
                                pelicula.setEdad (np.getEdad ());
                                pelicula.setGeneros (np.getGeneros ());

                                return pelicula;
                            })).get ());

                            f.dispose ();
                        });

                        return b;
                    })).get ());

                    r.add (((Supplier <JButton>) ( () -> {
                        JButton b = new JButton ("Aleatorizar");
                        b.setToolTipText ("Rellena los campos con valores aleatorios.");
                        b.addActionListener (e -> {
                            Pelicula np = Pelicula.random ();

                            valoracion.setValue (np.getValoracion ());
                            fecha.setValue (np.getFecha ().getValue ());
                            horas.setValue (np.getDuracion ().toHours ());
                            minutos.setValue (np.getDuracion ().toMinutesPart ());

                            List <AbstractButton> l = Collections.list (edad.getElements ());
                            for (int i = 0; i < l.size (); i++) {
                                if (!l.get (i).getText ().equals (np.getEdad ().toString ()))
                                    continue;

                                l.get (i).doClick (0);

                                break;
                            }

                            for (int i = 0; i < generos.size (); i++)
                                if (np.getGeneros ().contains (Genero.Nombre.values () [i + 1]))
                                    generos.get (i).doClick (0);

                            f.repaint ();
                        });

                        return b;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            return p;
        })).get ());

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Añadir una película");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("OptionPane.questionIcon", new Locale ("es-ES"))).getImage ());
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}