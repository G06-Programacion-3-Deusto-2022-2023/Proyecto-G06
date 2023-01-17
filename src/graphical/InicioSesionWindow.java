package graphical;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cine.Administrador;
import cine.Espectador;
import cine.Usuario;
import internals.GestorBD;
import internals.swing.JTextFieldLimit;

public class InicioSesionWindow extends JFrame {
    public InicioSesionWindow (final GestorBD db) {
        this (db, null);
    }

    public InicioSesionWindow (final GestorBD db, final InicioWindow w) throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar una base de datos nula a la ventana de inicio de sesión");

        final InicioSesionWindow f = this;
        final InicioWindow pw[] = new InicioWindow [] { w };

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (pw [0] != null)
                    w.setVisible (true);
            }
        });

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));

            p.add (Box.createRigidArea (new Dimension (15, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                q.add (Box.createRigidArea (new Dimension (0, 15)));

                JComponent fields [] [] = new JComponent [] [] {
                    new JComponent [2],
                    new JComponent [2]
                };

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                    r.add (fields [0] [0] = new JLabel ("Nombre de usuario:"));

                    r.add (fields [0] [1] = new JTextField (new JTextFieldLimit (30), "", 26));

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 5)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                    r.add (fields [1] [0] = ((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Contraseña:");

                        l.setMinimumSize (fields [0] [0].getMinimumSize ());
                        l.setPreferredSize (fields [0] [0].getPreferredSize ());
                        l.setMaximumSize (fields [0] [0].getMaximumSize ());

                        return l;
                    })).get ());

                    r.add (fields [1] [1] = new JPasswordField (new JTextFieldLimit (28), "", 25));

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 20)));

                q.add (((Supplier <JButton>) ( () -> {
                    JButton b = new JButton ("Iniciar sesión");

                    b.addActionListener (e -> {
                        if (((JTextField) fields [0] [1]).getText ().equals ("")) {
                            JOptionPane.showMessageDialog (f,
                                    "El campo de nombre de usuario no puede estar vacío.",
                                    "Campo vacío", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (new String (((JPasswordField) fields [1] [1]).getPassword()).equals ("")) {
                            JOptionPane.showMessageDialog (f,
                                    "El campo de contraseña no puede estar vacío.",
                                    "Campo vacío", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        Usuario u = ((Supplier <HashMap <String, Usuario>>) ( () -> {
                            HashMap <String, Usuario> m = new HashMap <String, Usuario> ();

                            List <Usuario> l = new ArrayList <Usuario> ();
                            l.addAll (db.getAdministradores ());
                            l.addAll (db.getEspectadores ());

                            for (int i = 0; i < l.size (); i++)
                                m.put (l.get (i).getNombre (), l.get (i));

                            return m;
                        })).get ().get (((JTextField) fields [0] [1]).getText ());

                        if (u == null) {
                            JOptionPane.showMessageDialog (f,
                                    "El usuario introducido no pudo ser encontrado en la base de datos.",
                                    "Usuario no encontrado", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (!u.getContrasena ().equals (new String (((JPasswordField) fields [1] [1]).getPassword()))) {
                            JOptionPane.showMessageDialog (f,
                                    "La contraseña introducida no es correcta. Por favor, pruebe otra vez.",
                                    "Contraseña errónea", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        pw [0] = null;
                        f.setVisible (false);
                        pw [0] = w;

                        if (u instanceof Administrador) {
                            new AdministradorWindow (db, (Administrador) u, w);

                            return;
                        }

                        new EspectadorWindow (db, (Espectador) u, w);
                    });

                    return b;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 15)));

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (15, 0)));

            return p;
        })).get ());

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Iniciar sesión");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("OptionPane.questionIcon", new Locale ("es-ES"))).getImage ().getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
