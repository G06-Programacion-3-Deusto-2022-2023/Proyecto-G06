package VentanaGrafica;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Locale;
import java.util.function.Supplier;

import cine.Administrador;
import cine.GestorBD;
import cine.Pelicula;
import cine.SetPeliculas;

public class AdministradorWindow extends JFrame {
    public AdministradorWindow (GestorBD db, Administrador admin) {
        this (db, admin, null);
    }

    public AdministradorWindow (GestorBD db, Administrador admin, VentanaInicio w)
            throws NullPointerException, UnsupportedOperationException {
        super ();

        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar un gestor de bases de datos nulo a la ventana de modo administrador.");

        if (admin == null)
            throw new NullPointerException (
                    "No se puede pasar un administador nulo a la ventana de modo administrador.");

        // if (!db.obtenerDatosAdministradores ().contains (admin))
        // throw new UnsupportedOperationException (
        // "El administrador enviado a la ventana de modo administrador no se
        // encuentra en la base de datos.");

        VentanaInicio pw[] = new VentanaInicio [] { w };
        AdministradorWindow f = this;

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (pw [0] == null)
                    return;

                w.setVisible (true);
            }
        });

        this.add (((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel (admin.getNombre ());
            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

            return l;
        })).get (), BorderLayout.PAGE_START);

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.Y_AXIS));

            p.add (Box.createRigidArea (new Dimension (0, 25)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel (new GridLayout (2, 1));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.CENTER, 25, 0));

                    r.add (((Supplier <JButton>) ( () -> {
                        JButton b = new JButton ("Gestionar usuarios");

                        b.addActionListener (e -> {
                            pw [0] = null;
                            f.setVisible (false);
                            pw [0] = w;

                            new GestionarUsuariosWindow (db, admin, f);
                        });

                        return b;
                    })).get ());

                    r.add (((Supplier <JButton>) ( () -> {
                        JButton b = new JButton ("Gestionar películas");

                        b.addActionListener (e -> {
                            pw [0] = null;
                            f.setVisible (false);
                            pw [0] = w;

                            new GestionarPeliculasWindow (db, admin, f);
                        });

                        return b;
                    })).get ());

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.CENTER));

                    r.add (((Supplier <JButton>) ( () -> {
                        JButton b = new JButton ("Otras opciones");

                        b.addActionListener (e -> {
                            pw [0] = null;
                            f.dispose ();
                            pw [0] = w;

                            new MiscOptionsWindow (db, admin, f);
                        });

                        return b;
                    })).get ());

                    return r;
                })).get ());

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (0, 0)));

            return p;
        })).get (), BorderLayout.CENTER);

        this.add (((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel (" ");
            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

            return l;
        })).get (), BorderLayout.PAGE_END);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle (String.format ("Menú principal de %s", admin.getNombre ()));
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileChooser.homeFolderIcon", new Locale ("es-ES"))).getImage ());
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
