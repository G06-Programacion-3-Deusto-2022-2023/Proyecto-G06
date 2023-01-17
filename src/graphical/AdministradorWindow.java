package graphical;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.function.Supplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cine.Administrador;
import internals.GestorBD;

public class AdministradorWindow extends JFrame {
    public AdministradorWindow (final GestorBD db, final Administrador admin) {
        this (db, admin, null);
    }

    public AdministradorWindow (final GestorBD db, final Administrador admin, final InicioWindow w)
            throws NullPointerException, IllegalArgumentException {
        super ();

        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar un gestor de bases de datos nulo a la ventana de modo administrador.");

        if (admin == null)
            throw new NullPointerException (
                    "No se puede pasar un administador nulo a la ventana de modo administrador.");

        if (!db.getAdministradores ().contains (admin))
            throw new IllegalArgumentException (
                    "El administrador enviado a la ventana de modo administrador no se encuentra en la base de datos.");

        final InicioWindow pw[] = new InicioWindow [] { w };
        final AdministradorWindow f = this;

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
                            f.setVisible (false);
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
                ((ImageIcon) UIManager.getIcon ("FileChooser.homeFolderIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
