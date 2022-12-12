package VentanaGrafica;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cine.Administrador;
import cine.Espectador;
import cine.GestorBD;
import cine.Usuario;
import internals.bst.Filter;
import internals.swing.JTextFieldLimit;

public class GestionarUsuariosWindow extends JFrame {
    public GestionarUsuariosWindow (GestorBD db, Administrador admin) {
        this (db, admin, null);
    }

    public GestionarUsuariosWindow (GestorBD db, Administrador admin, AdministradorWindow w)
            throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar una base de datos nula a la ventana de gestión de usuarios.");

        if (admin == null)
            throw new NullPointerException (
                    "No se puede pasar un administador nulo a la ventana de gestión de usuarios.");

        // if (!db.obtenerDatosAdministradores ().contains (admin))
        //     throw new UnsupportedOperationException (
        //             "El administrador enviado a la ventana de gestión de usuarios no se encuentra en la base de datos.");

        AdministradorWindow pw [] = new AdministradorWindow [] { w };
        GestionarUsuariosWindow f = this;

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

            p.add (Box.createRigidArea (new Dimension (0, 50)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.X_AXIS));

                JComboBox <String> users = new JComboBox <String> (((Supplier <Vector <String>>) ( () -> {
                    Vector <String> v = new Vector <String> ();

                    v.addAll ((((Supplier <List <String>>) ( () -> {
                        List <String> l = db.obtenerDatosAdministradores ().stream ()
                                .map (Administrador::getNombre)
                                .collect (Collectors.toList ());
                        Collections.sort (l);

                        return l.stream ().map (e -> e + " (A)").collect (Collectors.toList ());
                    }))).get ());

                    v.addAll ((((Supplier <List <String>>) ( () -> {
                        List <String> l = db.obtenerDatosEspectadores ().stream ()
                                .map (Espectador::getNombre)
                                .collect (Collectors.toList ());
                        Collections.sort (l);

                        return l.stream ().map (e -> e + " (E)").collect (Collectors.toList ());
                    }))).get ());

                    return v;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.X_AXIS));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel ();
                        s.setLayout (new BoxLayout (s, BoxLayout.Y_AXIS));

                        JTextField filter = new JTextField (new JTextFieldLimit (30), "", 26);
                        filter.setToolTipText (
                                "Filtrar por nombre (el nombre de usuario debe contener el texto introducido).");

                        List <JCheckBox> bb = new ArrayList <JCheckBox> (Arrays.asList (new JCheckBox [] {
                                ((Supplier <JCheckBox>) ( () -> {
                                    JCheckBox b = new JCheckBox ("Administradores");

                                    b.setSelected (true);

                                    return b;
                                })).get (),
                                ((Supplier <JCheckBox>) ( () -> {
                                    JCheckBox b = new JCheckBox ("Espectadores");

                                    b.setSelected (true);

                                    return b;
                                })).get (),
                                new JCheckBox (
                                        "Orden descendente")
                        }));

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel (new FlowLayout (FlowLayout.CENTER, 10, 0));

                            t.add (((Supplier <JLabel>) ( () -> {
                                JLabel l = new JLabel ("Filtrar:");
                                l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                        15f));

                                return l;
                            })).get ());

                            t.add (((Supplier <JPanel>) ( () -> {
                                JPanel u = new JPanel ();
                                u.setLayout (new BoxLayout (u, BoxLayout.Y_AXIS));
                                u.setAlignmentX (Component.LEFT_ALIGNMENT);

                                u.add (Box.createRigidArea (new Dimension (0, 60)));

                                u.add (filter);

                                u.add (((Supplier <JPanel>) ( () -> {
                                    JPanel v = new JPanel (new FlowLayout ());

                                    v.add (((Supplier <JPanel>) ( () -> {
                                        JPanel ww = new JPanel ();
                                        ww.setLayout (new BoxLayout (ww, BoxLayout.Y_AXIS));

                                        ww.add (bb.get (0));
                                        ww.add (bb.get (1));

                                        return ww;
                                    })).get ());

                                    v.add (bb.get (2));

                                    return v;
                                })).get ());

                                return u;
                            })).get (), BorderLayout.CENTER);

                            t.add (Box.createRigidArea (new Dimension (15, 0)));

                            t.add (((Supplier <JButton>) ( () -> {
                                JButton b = new JButton (new ImageIcon (
                                        getClass ()
                                                .getResource ("/toolbarButtonGraphics/general/Search24.gif")));

                                b.addActionListener (e -> {
                                    users.removeAllItems ();

                                    List <Usuario> list = ((Supplier <List <Usuario>>) ( () -> {
                                        List <Usuario> u = new ArrayList <Usuario> ();

                                        if (bb.get (0).isSelected ())
                                            u.addAll (Administrador
                                                    .tree (db.obtenerDatosAdministradores (),
                                                            !bb.get (2).isSelected ()
                                                                    ? (Comparator <Administrador>) ( (
                                                                            Administrador x,
                                                                            Administrador y) -> x.compareTo (y))
                                                                    : (Comparator <Administrador>) ( (
                                                                            Administrador x,
                                                                            Administrador y) -> y
                                                                                    .compareTo (x)),
                                                            (Filter <Administrador>) ( (Administrador x) -> x
                                                                    .getNombre ().contains (filter.getText ())))
                                                    .getValues ());

                                        if (bb.get (1).isSelected ())
                                            u.addAll (Espectador
                                                    .tree (db.obtenerDatosEspectadores (),
                                                            !bb.get (2).isSelected ()
                                                                    ? (Comparator <Espectador>) ( (Espectador x,
                                                                            Espectador y) -> x.compareTo (y))
                                                                    : (Comparator <Espectador>) ( (Espectador x,
                                                                            Espectador y) -> y.compareTo (x)),
                                                            (Filter <Espectador>) ( (Espectador x) -> x
                                                                    .getNombre ().contains (filter.getText ())))
                                                    .getValues ());

                                        return u;
                                    })).get ();

                                    users.repaint ();
                                });

                                return b;
                            })).get ());

                            return t;
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 100)));

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();

                            t.add (users, BorderLayout.CENTER);

                            return t;
                        })).get ());

                        return s;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (10, 0)));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel t = new JPanel (new GridLayout (3, 1, 0, 25));

                        t.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Cambiar contraseña");

                            return b;
                        })).get ());

                        t.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Borrar datos");

                            return b;
                        })).get ());

                        t.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Eliminar");

                            b.addActionListener (e -> {
                                if (JOptionPane.showOptionDialog (f,
                                        "Lo que estás a punto de hacer es una acción irreversible.\n¿Estás seguro de querer continuar?",
                                        "Eliminar película", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                                        null, new String [] {
                                                "Confirmar",
                                                "Cancelar"
                                        }, JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                    return;

                                if (!((String) users.getSelectedItem ()).equals (admin.getNombre ()))
                                    return;

                                JOptionPane.showMessageDialog (f,
                                        "Se ha eliminado el usuario activo por lo que se procederá a cerrar sesión.",
                                        "Usuario activo eliminado", JOptionPane.INFORMATION_MESSAGE);
                                f.dispose ();
                                Principal.main (null);
                            });

                            return b;
                        })).get ());

                        return t;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (10, 0)));
                q.add (new JSeparator (SwingConstants.VERTICAL));
                q.add (Box.createRigidArea (new Dimension (10, 0)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new GridLayout (2, 2, 25, 0));
                        s.setAlignmentX (Component.CENTER_ALIGNMENT);

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Crear usuario");

                                b.addActionListener (e -> {
                                    JTextField user = new JTextField (new JTextFieldLimit (30), "", 26);
                                    user.setToolTipText ("Nombre de usuario (obligatorio)");

                                    JPasswordField pass = new JPasswordField (new JTextFieldLimit (28), "", 25);
                                    pass.setToolTipText ("Contraseña (dejar vacío para usar una contraseña aleatoria)");

                                    JComboBox <String> role = new JComboBox <String> (new Vector <String> (
                                            Arrays.asList (new String [] { "Espectador", "Administrador" })));

                                    for (;;) {
                                        if (JOptionPane.showOptionDialog (f,
                                                new Object [] { "Introduce un nombre de usuario y una contraseña", user,
                                                        pass,
                                                        role },
                                                "Crear usuario", JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.QUESTION_MESSAGE,
                                                null, null, null) == JOptionPane.CANCEL_OPTION)
                                            return;

                                        if (user.getText ().equals ("")) {
                                            JOptionPane.showMessageDialog (f,
                                                    "El usuario debe tener un nombre.");

                                            continue;
                                        }

                                        if (user.getText ().length () < 3) {
                                            JOptionPane.showMessageDialog (f,
                                                    "El nombre de usuario debe ser de al menos 3 carácteres.");

                                            continue;
                                        }

                                        if (user.getText ().contains (" ")) {
                                            JOptionPane.showMessageDialog (f,
                                                    "El nombre de usuario no puede contener espacios.");

                                            continue;
                                        }

                                        if ((role.getSelectedIndex () == 0
                                                && db.obtenerDatosEspectadores ().stream ().map (Espectador::getNombre)
                                                        .collect (Collectors.toList ()).contains (user.getText ()))
                                                || (role.getSelectedIndex () == 1
                                                        && db.obtenerDatosAdministradores ().stream ()
                                                                .map (Administrador::getNombre)
                                                                .collect (Collectors.toList ())
                                                                .contains (user.getText ()))) {
                                            JOptionPane.showMessageDialog (f,
                                                    "El nombre de usuario no está disponible.");

                                            continue;
                                        }

                                        if (!new String (pass.getPassword ()).equals ("")
                                                && new String (pass.getPassword ()).length () < 4) {
                                            JOptionPane.showMessageDialog (f,
                                                    "La contraseña del usuario debe ser de al menos 4 carácteres.");

                                            continue;
                                        }

                                        if (role.getSelectedIndex () == 0) {
                                            db.insertarDatosEspectador (new Espectador (user.getText (),
                                                    new String (pass.getPassword ()).equals ("")
                                                            ? Usuario.generatePassword ()
                                                            : new String (pass.getPassword ())));

                                            return;
                                        }

                                        db.insertarDatosAdministrador (new Administrador (user.getText (),
                                                new String (pass.getPassword ()).equals ("")
                                                        ? Usuario.generatePassword ()
                                                        : new String (pass.getPassword ())));

                                        return;
                                    }
                                });

                                return b;
                            })).get ());

                            t.add (new JLabel (" "));

                            return t;
                        })).get ());

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));
                            t.setAlignmentY (CENTER_ALIGNMENT);

                            JLabel l = new JLabel (String.format ("%d llaves restantes", 0));

                            t.add (((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Ver llaves");

                                b.addActionListener (e -> {
                                    pw [0] = null;
                                    f.setVisible (false);
                                    pw [0] = w;

                                    new SeeKeysWindow (db, f);
                                });

                                return b;
                            })).get ());
                            t.add (Box.createRigidArea (new Dimension (0, 10)));

                            t.add (((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Regenerar llaves");

                                b.addActionListener (e -> {
                                    db.regenerateAdminKeys ();

                                    l.setText (String.format ("%d llaves restantes", db.getAdminKeys().size ()));
                                    l.repaint ();
                                });

                                return b;
                            })).get ());

                            t.add (l);

                            return t;
                        })).get ());

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Importar usuarios");

                                b.addActionListener (e -> {

                                });

                                return b;
                            })).get ());

                            t.add (new JLabel (" "));

                            return t;
                        })).get ());

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Exportar usuarios");

                                b.addActionListener (e -> {

                                });

                                return b;
                            })).get ());

                            t.add (new JLabel (" "));

                            return t;
                        })).get ());

                        return s;
                    })).get ());

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel ();

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Eliminar todos los espectadores");

                            b.addActionListener (e -> {

                            });

                            return b;
                        })).get ());

                        return s;
                    })).get ());

                    return r;
                })).get ());

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
        this.setTitle ("Gestionar usuarios");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileView.hardDriveIcon", new Locale ("es-ES"))).getImage ());
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
