package VentanaGrafica;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.awt.Font;
import java.awt.BorderLayout;

import java.util.Locale;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import internals.GestorBD;
import internals.Settings;
import internals.swing.ImageDisplayer;

public class VentanaInicio extends JFrame {
    public VentanaInicio () {
        this (null);
    }

    public VentanaInicio (GestorBD db) {
        final int lw = 200;

        final VentanaInicio f = this;

        final JLabel l = new JLabel (Settings.getNombre (), SwingConstants.CENTER);
        final ImageDisplayer id[] = new ImageDisplayer [1];
        try {
            Image img = new ImageIcon (new File (Settings.getLogo ()).toURI ().toURL ()).getImage ();
            id [0] = new ImageDisplayer (img, lw, img.getHeight (null) * lw / img.getWidth (null), Image.SCALE_SMOOTH);
        }

        catch (MalformedURLException e1) {
            Logger.getLogger (VentanaInicio.class.getName ()).log (Level.WARNING,
                    String.format ("No se pudo crear una URL a partir del archivo %s", Settings.getLogo ()));

            try {
                Image img = new ImageIcon (new File (Settings.defaults ().getProperty ("logo")).toURI ().toURL ())
                        .getImage ();
                id [0] = new ImageDisplayer (img, lw, img.getHeight (null) * lw / img.getWidth (null),
                        Image.SCALE_SMOOTH);
            }

            catch (MalformedURLException e2) {
                Logger.getLogger (VentanaInicio.class.getName ()).log (Level.WARNING,
                        String.format ("No se pudo crear una URL a partir del archivo %s", Settings.getLogo ()));

                id [0] = null;
            }
        }
        final JPanel ip[] = new JPanel [1];
        final Runnable addip[] = new Runnable [1];

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (db != null)
                    GestorBD.unlock ();

                Settings.save ();
            }
        });

        this.addComponentListener (new ComponentAdapter () {
            @Override
            public void componentShown (ComponentEvent e) {
                f.setTitle (Settings.getNombre ());
                l.setText (Settings.getNombre ());

                if (id [0] != null) {
                    try {
                        Image img = new ImageIcon (new File (Settings.getLogo ()).toURI ().toURL ()).getImage ();
                        id [0].setImage (img, lw, img.getHeight (null) * lw / img.getWidth (null), Image.SCALE_SMOOTH);
                    }

                    catch (MalformedURLException e2) {
                        Logger.getLogger (VentanaInicio.class.getName ()).log (Level.WARNING,
                                String.format ("No se pudo crear una URL a partir del archivo %s",
                                        Settings.getLogo ()));

                        id [0] = null;
                    }

                    return;
                }

                if (addip [0] != null) {
                    try {
                        Image img = new ImageIcon (new File (Settings.getLogo ()).toURI ().toURL ()).getImage ();
                        id [0] = new ImageDisplayer (img, lw, img.getHeight (null) * lw / img.getWidth (null));
                    }

                    catch (MalformedURLException e2) {
                        Logger.getLogger (VentanaInicio.class.getName ()).log (Level.WARNING,
                                String.format ("No se pudo crear una URL a partir del archivo %s",
                                        Settings.getLogo ()));

                        id [0] = null;
                    }

                    addip [0].run ();
                }
            }
        });

        this.setLayout (new BoxLayout (this.getContentPane (), BoxLayout.X_AXIS));
        this.add (Box.createRigidArea (new Dimension (25, 0)));
        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.Y_AXIS));

            p.add (Box.createRigidArea (new Dimension (0, 25)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel (new BorderLayout (0, 25));

                q.add (((Supplier <JLabel>) ( () -> {
                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 28f));

                    return l;
                })).get (), BorderLayout.PAGE_START);

                q.add (((Supplier <JPanel>) ( () -> {
                    (addip [0] = () -> {
                        if (ip [0] == null)
                            ip [0] = new JPanel (new FlowLayout (FlowLayout.CENTER, 25, 0));

                        else {
                            ip [0].removeAll ();
                            p.remove (ip [0]);
                        }

                        if (id [0] != null)
                            ip [0].add (id [0]);

                        ip [0].add (((Supplier <JPanel>) ( () -> {
                            JPanel r = new JPanel (new GridLayout (2, 1));

                            r.add (((Supplier <JPanel>) ( () -> {
                                JPanel s = new JPanel (new FlowLayout (FlowLayout.CENTER, 15, 0));

                                s.add (((Supplier <JButton>) ( () -> {
                                    JButton b = new JButton ("Iniciar sesión");

                                    b.addActionListener (e -> {
                                        if (db == null) {
                                            JOptionPane.showMessageDialog (f,
                                                    "No se puede iniciar sesión como un usuario registrado teniendo una base de datos nula.",
                                                    "Funcionalidad limitada", JOptionPane.WARNING_MESSAGE);

                                            return;
                                        }

                                        f.setVisible (false);
                                        new VentanaInicioSesion (db, f);
                                    });

                                    return b;
                                })).get ());

                                s.add (((Supplier <JButton>) ( () -> {
                                    JButton b = new JButton ("Registrarse");

                                    b.addActionListener (e -> {
                                        if (db == null) {
                                            JOptionPane.showMessageDialog (f,
                                                    "No se pueden registrar usuarios teniendo una base de datos nula.",
                                                    "Funcionalidad limitada", JOptionPane.WARNING_MESSAGE);

                                            return;
                                        }

                                        f.setVisible (false);
                                        new RegistroWindow (db, f);
                                    });

                                    return b;
                                })).get ());

                                return s;
                            })).get ());

                            r.add (Box.createRigidArea (new Dimension (0, 15)));

                            r.add (((Supplier <JPanel>) ( () -> {
                                JPanel s = new JPanel (new FlowLayout ());

                                s.add (((Supplier <JButton>) ( () -> {
                                    JButton b = new JButton ("Continuar como invitado");

                                    b.addActionListener (e -> {
                                        f.setVisible (false);
                                        new VentanaEspectador (db, null, f);
                                    });

                                    return b;
                                })).get ());

                                return s;
                            })).get ());

                            return r;
                        })).get ());

                        f.pack ();
                        f.repaint ();
                    }).run ();

                    return ip [0];
                })).get (), BorderLayout.CENTER);

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    r.add (Box.createRigidArea (new Dimension (0, 25)));

                    return r;
                })).get (), BorderLayout.PAGE_END);

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (0, 25)));

            return p;
        })).get ());

        this.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        this.setTitle (Settings.getNombre ());
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileChooser.homeFolderIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}