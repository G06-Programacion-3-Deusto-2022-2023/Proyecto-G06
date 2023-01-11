package VentanaGrafica;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cine.Administrador;
import cine.Pelicula;
import cine.SetPeliculas;
import internals.swing.JTextFieldLimit;
import internals.swing.PeliculasListRenderer;

public class SetPeliculasWindow extends JFrame {
    public SetPeliculasWindow (final SetPeliculas [] setpeliculas, final Collection <Pelicula> peliculas) {
        this (setpeliculas, peliculas, null);
    }

    public SetPeliculasWindow (final SetPeliculas [] setpeliculas, final Collection <Pelicula> peliculas,
            Administrador administrador) {
        this (setpeliculas, peliculas, administrador, null);
    }

    public SetPeliculasWindow (final SetPeliculas [] setpeliculas, final Collection <Pelicula> peliculas,
            final Administrador administrador, final GestionarPeliculasWindow w) {
        super ();

        if (setpeliculas == null)
            throw new NullPointerException (
                    "No se puede pasar un array nulo de sets de películas a la ventana de creación/modificación de sets de películas.");

        if (setpeliculas.length != 1)
            throw new UnsupportedOperationException (
                    "Solo se puede pasar un array de sets de películas que contenga un único elemento.");

        final SetPeliculasWindow f = this;
        final List <PeliculaDetailsWindow> dw = new ArrayList <PeliculaDetailsWindow> ();

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                for (int i = 0; i < dw.size (); dw.get (i++).dispose ())
                    ;

                w.setVisible (true);
            }
        });

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                JTextField nombre = new JTextField (new JTextFieldLimit (50),
                        setpeliculas [0] == null ? "" : setpeliculas [0].getNombre (), 32);

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                JList <Pelicula> lists[] = new JList [2];

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new FlowLayout (FlowLayout.LEFT, 10, 0));

                        s.add (((Supplier <JLabel>) ( () -> {
                            JLabel l = new JLabel ("Nombre:");

                            l.setFont (l.getFont ().deriveFont (Font.BOLD, 13f));

                            return l;
                        })).get ());

                        s.add (nombre);

                        return s;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (0, 25)));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel rr = new JPanel ();
                        rr.setLayout (new GridBagLayout ());

                        GridBagConstraints gbc = new GridBagConstraints ();
                        gbc.anchor = GridBagConstraints.PAGE_START;
                        gbc.gridwidth = GridBagConstraints.REMAINDER;
                        gbc.insets = new Insets (15, 0, 0, 0);

                        rr.add (((Supplier <JLabel>) ( () -> {
                            JLabel l = new JLabel ("Películas");

                            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                            return l;
                        })).get (), gbc);

                        gbc.anchor = GridBagConstraints.CENTER;
                        gbc.fill = GridBagConstraints.NONE;

                        rr.add (((Supplier <JPanel>) ( () -> {
                            JPanel s = new JPanel (new FlowLayout (FlowLayout.CENTER, 25, 0));

                            JScrollPane sp[] = new JScrollPane [2];
                            JButton b[] = new JButton [3];

                            s.add (((Supplier <JScrollPane>) ( () -> {
                                lists [0] = new JList <Pelicula> (new DefaultListModel <Pelicula> ());
                                ((DefaultListModel <Pelicula>) lists [0].getModel ())
                                        .addAll (((Supplier <SortedSet <Pelicula>>) ( () -> {
                                            SortedSet <Pelicula> ssp = peliculas.stream ()
                                                    .collect (Collectors.toCollection (TreeSet::new));
                                            if (setpeliculas [0] != null)
                                                ssp.removeAll (setpeliculas [0].getPeliculas ());

                                            return ssp;
                                        })).get ());
                                lists [0].setCellRenderer (new PeliculasListRenderer ());
                                lists [0].setVisibleRowCount (10);
                                lists [0].setLayoutOrientation (JList.VERTICAL);
                                lists [0].getSelectionModel ().addListSelectionListener (e -> {
                                    lists [1].clearSelection ();

                                    b [0].setEnabled (lists [0].getSelectedIndex () != -1);
                                    b [1].setEnabled (lists [0].getSelectedIndex () != -1
                                            && lists [1].getModel ().getSize () < SetPeliculas.maxSize ());
                                    b [2].setEnabled (false);
                                });

                                return sp [0] = new JScrollPane (lists [0]);
                            })).get ());

                            s.add (((Supplier <JPanel>) ( () -> {
                                JPanel t = new JPanel ();
                                t.setLayout (new GridBagLayout ());

                                GridBagConstraints gbc2 = new GridBagConstraints ();
                                gbc2.anchor = GridBagConstraints.NORTH;
                                gbc2.gridwidth = GridBagConstraints.REMAINDER;
                                gbc2.insets = new Insets (5, 0, 5, 0);
                                gbc2.fill = GridBagConstraints.NONE;

                                t.add (((Supplier <JButton>) ( () -> {
                                    b [0] = new JButton ("Ver detalles");

                                    b [0].setEnabled (false);
                                    b [0].addActionListener (e -> {
                                        Pelicula selection = lists [0].getSelectedIndex () == -1
                                                ? lists [1].getSelectedValue ()
                                                : lists [0].getSelectedValue ();

                                        for (int i = 0; i < dw.size (); i++)
                                            if (dw.get (i).getPelicula ().equals (selection)) {
                                                dw.get (i).setVisible (true);
                                                dw.get (i).setLocationRelativeTo (null);

                                                return;
                                            }

                                        dw.add (new PeliculaDetailsWindow (selection));
                                    });

                                    b [0].setSize (new Dimension (b [0].getWidth (), 16));

                                    return b [0];
                                })).get (), gbc2);

                                gbc2.anchor = GridBagConstraints.CENTER;

                                t.add (((Supplier <JButton>) ( () -> {
                                    b [1] = new JButton (new ImageIcon (
                                            getClass ()
                                                    .getResource ("/toolbarButtonGraphics/navigation/Forward16.gif")));

                                    b [1].setEnabled (false);
                                    b [1].addActionListener (e -> {
                                        Pelicula selection = ((DefaultListModel <Pelicula>) lists [0].getModel ())
                                                .remove (lists [0].getSelectedIndex ());

                                        boolean added = false;
                                        for (int i = 0, comp; i < lists [1].getModel ().getSize (); i++) {
                                            if ((comp = selection
                                                    .compareTo (lists [1].getModel ().getElementAt (i))) == 0)
                                                return;

                                            if (comp > 0)
                                                continue;

                                            added = true;
                                            ((DefaultListModel <Pelicula>) lists [1].getModel ()).add (i, selection);

                                            break;
                                        }

                                        if (!added)
                                            ((DefaultListModel <Pelicula>) lists [1].getModel ())
                                                    .addElement (selection);

                                        b [1].setEnabled (false);
                                        b [2].setEnabled (false);
                                        lists [0].repaint ();
                                        lists [1].repaint ();
                                    });

                                    b [1].setSize (b [0].getSize ());

                                    return b [1];
                                })).get (), gbc2);

                                gbc2.anchor = GridBagConstraints.SOUTH;

                                t.add (((Supplier <JButton>) ( () -> {
                                    b [2] = new JButton (new ImageIcon (
                                            getClass ().getResource ("/toolbarButtonGraphics/navigation/Back16.gif")));

                                    b [2].setEnabled (false);
                                    b [2].addActionListener (e -> {
                                        Pelicula selection = ((DefaultListModel <Pelicula>) lists [1].getModel ())
                                                .remove (lists [1].getSelectedIndex ());

                                        boolean added = false;
                                        for (int i = 0, comp; i < lists [0].getModel ().getSize (); i++) {
                                            if ((comp = selection
                                                    .compareTo (lists [0].getModel ().getElementAt (i))) == 0)
                                                return;

                                            if (comp > 0)
                                                continue;

                                            added = true;
                                            ((DefaultListModel <Pelicula>) lists [0].getModel ()).add (i, selection);

                                            break;
                                        }

                                        if (!added)
                                            ((DefaultListModel <Pelicula>) lists [0].getModel ())
                                                    .addElement (selection);

                                        b [1].setEnabled (false);
                                        b [2].setEnabled (false);
                                        lists [0].repaint ();
                                        lists [1].repaint ();
                                    });

                                    b [2].setSize (b [0].getSize ());

                                    return b [2];
                                })).get (), gbc2);

                                return t;
                            })).get ());

                            s.add (((Supplier <JScrollPane>) ( () -> {
                                lists [1] = new JList <Pelicula> (new DefaultListModel <Pelicula> ());
                                if (setpeliculas [0] != null)
                                    ((DefaultListModel <Pelicula>) lists [1].getModel ())
                                            .addAll (setpeliculas [0].getPeliculas ());
                                lists [1].setCellRenderer (new PeliculasListRenderer ());
                                lists [1].setVisibleRowCount (10);
                                lists [1].setLayoutOrientation (JList.VERTICAL);
                                lists [1].getSelectionModel ().addListSelectionListener (e -> {
                                    lists [0].clearSelection ();

                                    b [0].setEnabled (lists [1].getSelectedIndex () != -1);
                                    b [1].setEnabled (false);
                                    b [2].setEnabled (lists [1].getSelectedIndex () != -1);
                                });

                                sp [1] = new JScrollPane (lists [1]);
                                sp [1].setMinimumSize (sp [0].getMinimumSize ());
                                sp [1].setPreferredSize (sp [0].getPreferredSize ());
                                sp [1].setMaximumSize (sp [0].getMaximumSize ());
                                sp [1].setVerticalScrollBarPolicy (
                                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                                return sp [1];
                            })).get ());

                            return s;
                        })).get (), gbc);

                        return rr;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.CENTER, 25, 0));

                    r.add (((Supplier <JButton>) ( () -> {
                        JButton b = new JButton (setpeliculas [0] == null ? "Añadir" : "Modificar");

                        b.addActionListener (e -> {
                            if (nombre.getText ().replace (" ", "").length () == 0) {
                                JOptionPane.showMessageDialog (f,
                                        "El nombre del set de películas no puede estar vacío.",
                                        String.format ("Error al %s el set de películas",
                                                setpeliculas [0] == null ? "crear" : "modificar"),
                                        JOptionPane.ERROR_MESSAGE);

                                return;
                            }

                            if (nombre.getText ().contains ("'") || nombre.getText ().contains ("\"")
                                    || nombre.getText ().contains ("`")) {
                                JOptionPane.showMessageDialog (f,
                                        "El nombre del set de películas no puede contener comillas.",
                                        String.format ("Error al %s el set de películas",
                                                setpeliculas [0] == null ? "crear" : "modificar"),
                                        JOptionPane.ERROR_MESSAGE);

                                return;
                            }

                            if (lists [1].getModel ().getSize () < SetPeliculas.minSize ()) {
                                JOptionPane.showMessageDialog (f,
                                        String.format ("El set de película debe contener al menos %d películas.",
                                                SetPeliculas.minSize ()),
                                        String.format ("Error al %s el set de películas",
                                                setpeliculas [0] == null ? "crear" : "modificar"),
                                        JOptionPane.ERROR_MESSAGE);

                                return;
                            }

                            if (setpeliculas [0] == null) {
                                setpeliculas [0] = new SetPeliculas (administrador, nombre.getText (), Collections
                                        .list (((DefaultListModel <Pelicula>) lists [1].getModel ()).elements ()));

                                f.dispose ();

                                return;
                            }

                            setpeliculas [0].setNombre (nombre.getText ());
                            setpeliculas [0].setPeliculas (Collections
                                    .list (((DefaultListModel <Pelicula>) lists [1].getModel ()).elements ()));

                            f.dispose ();
                        });

                        return b;
                    })).get ());

                    r.add (((Supplier <JButton>) ( () -> {
                        JButton b = new JButton ("Aleatorizar");

                        b.addActionListener (e -> {
                            List <Pelicula> l[] = new List [] {
                                    peliculas.stream ().collect (Collectors.toList ()),
                                    peliculas.stream ().collect (Collectors.toList ())
                            };
                            Collections.shuffle (l [0]);
                            l [0] = peliculas.stream ()
                                    .limit (Math.max (SetPeliculas.minSize (),
                                            new Random ().nextInt (SetPeliculas.maxSize () + 1)))
                                    .collect (Collectors.toList ());
                            l [1].removeAll (l [0]);

                            ((DefaultListModel <Pelicula>) lists [0].getModel ()).clear ();
                            ((DefaultListModel <Pelicula>) lists [0].getModel ()).addAll (l [1]);

                            ((DefaultListModel <Pelicula>) lists [1].getModel ()).clear ();
                            ((DefaultListModel <Pelicula>) lists [1].getModel ()).addAll (l [0]);
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
        this.setTitle (String.format ("%s un set de películas", setpeliculas [0] == null ? "Añadir" : "Modificar"));
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("OptionPane.questionIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
