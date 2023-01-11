package VentanaGrafica;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cine.Administrador;
import cine.Espectador;
import cine.Usuario;
import internals.GestorBD;
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

        if (!db.getAdministradores ().contains (admin))
            throw new UnsupportedOperationException (
                    "El administrador enviado a la ventana de gestión de usuarios no se encuentra en la base de datos.");

        AdministradorWindow pw[] = new AdministradorWindow [] { w };
        GestionarUsuariosWindow f = this;

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
            JLabel l = new JLabel (admin.getNombre ());
            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

            return l;
        })).get (), BorderLayout.PAGE_START);

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.Y_AXIS));

            p.add (Box.createRigidArea (new Dimension (0, 25)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.X_AXIS));

                q.add (Box.createRigidArea (new Dimension (25, 0)));

                JButton bbb[] = new JButton [] {
                        new JButton ("Cambiar contraseña"),
                        new JButton ("Borrar datos"),
                        new JButton ("Eliminar")
                };

                JComboBox <String> users = new JComboBox <String> (((Supplier <Vector <String>>) ( () -> {
                    Vector <String> v = new Vector <String> ();

                    v.addAll ((((Supplier <List <String>>) ( () -> {
                        List <String> l = db.getAdministradores ().stream ()
                                .map (Administrador::getNombre)
                                .collect (Collectors.toList ());
                        Collections.sort (l);

                        return l.stream ().map (e -> e + " (A)").collect (Collectors.toList ());
                    }))).get ());

                    v.addAll ((((Supplier <List <String>>) ( () -> {
                        List <String> l = db.getEspectadores ().stream ()
                                .map (Espectador::getNombre)
                                .collect (Collectors.toList ());
                        Collections.sort (l);

                        return l.stream ().map (e -> e + " (E)").collect (Collectors.toList ());
                    }))).get ());

                    return v;
                })).get ());
                users.addActionListener (e -> {
                    bbb [0].setEnabled (users.getItemCount () != 0);
                    bbb [1].setEnabled (users.getItemCount () != 0);
                    bbb [2].setEnabled (users.getItemCount () != 0);
                });
                if (users.getItemCount () != 0)
                    users.setSelectedIndex (0);

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

                        ActionListener filterAL = e -> {
                            users.removeAllItems ();

                            List <Usuario> list = ((Supplier <List <Usuario>>) ( () -> {
                                List <Usuario> u = new ArrayList <Usuario> ();

                                if (bb.get (0).isSelected ())
                                    u.addAll (Administrador
                                            .tree (db.getAdministradores (),
                                                    !bb.get (2).isSelected ()
                                                            ? (Comparator <Administrador>) ( (
                                                                    Administrador x,
                                                                    Administrador y) -> x.compareTo (y))
                                                            : (Comparator <Administrador>) ( (
                                                                    Administrador x,
                                                                    Administrador y) -> y
                                                                            .compareTo (x)),
                                                    (Filter <Administrador>) ( (Administrador x) -> x
                                                            .getNombre ().contains (filter.getText ().replace ("'", "").replace ("\"", "").replace ("`", ""))))
                                            .getValues ());

                                if (bb.get (1).isSelected ())
                                    u.addAll (Espectador
                                            .tree (db.getEspectadores (),
                                                    !bb.get (2).isSelected ()
                                                            ? (Comparator <Espectador>) ( (Espectador x,
                                                                    Espectador y) -> x.compareTo (y))
                                                            : (Comparator <Espectador>) ( (Espectador x,
                                                                    Espectador y) -> y.compareTo (x)),
                                                    (Filter <Espectador>) ( (Espectador x) -> x
                                                            .getNombre ().contains (filter.getText ().replace ("'", "").replace ("\"", "").replace ("`", ""))))
                                            .getValues ());

                                return u;
                            })).get ();

                            for (int i = 0; i < list.size (); i++)
                                users.addItem (list.get (i).getNombre ()
                                        + (list.get (i) instanceof Administrador ? " (A)" : " (E)"));

                            users.repaint ();
                        };

                        filter.addActionListener (filterAL);
                        filter.getDocument ().addDocumentListener (new DocumentListener () {
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
                                if (filter.getText ().strip ().length () > 0)
                                    filter.postActionEvent ();
                            }
                        });
                        bb.get (0).addActionListener (filterAL);
                        bb.get (1).addActionListener (filterAL);
                        bb.get (2).addActionListener (filterAL);

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
                            bbb [0].setEnabled (users.getSelectedIndex () != -1);

                            bbb [0].addActionListener (e -> {
                                JPasswordField passwd = new JPasswordField (new JTextFieldLimit (28), "", 25);

                                for (;;) {
                                    if (JOptionPane.showOptionDialog (f,
                                            new Object [] { "Introduce la nueva contraseña.", passwd },
                                            "Cambiar contraseña", JOptionPane.YES_OPTION,
                                            JOptionPane.INFORMATION_MESSAGE,
                                            null, new String [] { "Confirmar", "Cancelar" },
                                            JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                        return;

                                    if (new String (passwd.getPassword ()).strip ().equals ("")) {
                                        JOptionPane.showMessageDialog (f, "No puede introducirse una contraseña vacía.",
                                                "Error al cambiar la contraseña", JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    if (new String (passwd.getPassword ()).strip ().length () < 4) {
                                        JOptionPane.showMessageDialog (f,
                                                "La contraseña debe tener 4 o más carácteres.",
                                                "Error al cambiar la contraseña", JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    if (new String (passwd.getPassword ()).contains (" ")) {
                                        JOptionPane.showMessageDialog (f, "La contraseña no puede contener espacios.",
                                                "Error al cambiar la contraseña", JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    if (new String (passwd.getPassword ()).contains ("\"")
                                            || new String (passwd.getPassword ()).contains ("'") || new String (passwd.getPassword ()).contains ("`")) {
                                        JOptionPane.showMessageDialog (f, "La contraseña no puede contener comillas.",
                                                "Error al cambiar la contraseña", JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    String username = ((String) users.getSelectedItem ()).substring (0,
                                            ((String) users.getSelectedItem ()).length () - 4);

                                    Usuario user = ((String) users.getSelectedItem ()).endsWith ((" (A)"))
                                            ? db.getAdministradorPorNombre (username)
                                            : db.getEspectadores ().stream ()
                                                    .filter (x -> x.getNombre ().equals (username)).findFirst ().get ();

                                    if (new String (passwd.getPassword ()).equals (user.getContrasena ())) {
                                        JOptionPane.showMessageDialog (f,
                                                "La contraseña introducida es la contraseña del usuario.",
                                                "Error al cambiar la contraseña", JOptionPane.WARNING_MESSAGE);

                                        continue;
                                    }

                                    user.setContrasena (new String (passwd.getPassword ()));
                                    db.update (user);

                                    if (((String) users.getSelectedItem ())
                                            .substring (0, ((String) users.getSelectedItem ()).length () - 4)
                                            .equals (admin.getNombre ())) {
                                        JOptionPane.showMessageDialog (f,
                                                "Se ha cambiado la contraseña del usuario activo por lo que se procederá a cerrar sesión.",
                                                "Contraseña del usuario activo modificada",
                                                JOptionPane.INFORMATION_MESSAGE);

                                        if (w == null)
                                            f.dispose ();

                                        w.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
                                        pw [0] = null;
                                        f.dispose ();
                                        w.dispose ();
                                    }

                                    return;
                                }
                            });

                            return bbb [0];
                        })).get ());

                        t.add (((Supplier <JButton>) ( () -> {
                            bbb [1].setEnabled (users.getSelectedIndex () != -1);

                            bbb [1].addActionListener (e -> {
                                if (JOptionPane.showOptionDialog (f,
                                        "Lo que estás a punto de hacer es una acción irreversible.\n¿Estás seguro de querer continuar?",
                                        "Eliminar película", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                                        null, new String [] {
                                                "Confirmar",
                                                "Cancelar"
                                        }, JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                    return;

                                if (((String) users.getSelectedItem ()).endsWith (" (E)")) {
                                    db.deleteEspectadorData (db.getEspectadores ().stream ()
                                            .filter (x -> x.getNombre ().equals (((String) users.getSelectedItem ())
                                                    .substring (0,
                                                            ((String) users.getSelectedItem ()).length () - 4)))
                                            .findFirst ().get ());

                                    return;
                                }

                                db.deleteAdminData (
                                        db.getAdministradorPorNombre (((String) users.getSelectedItem ())
                                                .substring (0, ((String) users.getSelectedItem ()).length () - 4)));
                            });

                            return bbb [1];
                        })).get ());

                        t.add (((Supplier <JButton>) ( () -> {
                            bbb [2].setEnabled (users.getSelectedIndex () != -1);

                            bbb [2].addActionListener (e -> {
                                if (JOptionPane.showOptionDialog (f,
                                        "Lo que estás a punto de hacer es una acción irreversible.\n¿Estás seguro de querer continuar?",
                                        "Eliminar película", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                                        null, new String [] {
                                                "Confirmar",
                                                "Cancelar"
                                        }, JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                    return;

                                String username = ((String) users.getSelectedItem ()).substring (0,
                                        ((String) users.getSelectedItem ()).length () - 4);

                                if (((String) users.getSelectedItem ()).endsWith (" (E)")) {
                                    db.delete (db.getEspectadores ().stream ()
                                            .filter (x -> x.getNombre ().equals (username)).findFirst ().get ());

                                    return;
                                }

                                db.delete (db.getAdministradorPorNombre (username));

                                if (((String) users.getSelectedItem ())
                                        .substring (0, ((String) users.getSelectedItem ()).length () - 4)
                                        .equals (admin.getNombre ())) {
                                    JOptionPane.showMessageDialog (f,
                                            "Se ha eliminado el usuario activo por lo que se procederá a cerrar sesión.",
                                            "Usuario activo eliminado", JOptionPane.INFORMATION_MESSAGE);

                                    if (w == null)
                                        f.dispose ();

                                    w.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
                                    pw [0] = null;
                                    f.dispose ();
                                    w.dispose ();

                                    return;
                                }

                                users.removeItem (users.getSelectedItem ());
                                users.repaint ();
                            });

                            return bbb [2];
                        })).get ());

                        return t;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (25, 0)));
                q.add (new JSeparator (SwingConstants.VERTICAL));
                q.add (Box.createRigidArea (new Dimension (25, 0)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new GridLayout (2, 2, 25, 0));
                        s.setAlignmentX (Component.CENTER_ALIGNMENT);

                        JLabel l = new JLabel (String.format ("%d llaves restantes", db.getAdminKeys ().size ()));

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel ();
                            t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));

                            t.add (((Supplier <JButton>) ( () -> {
                                JButton b = new JButton ("Crear usuario");

                                b.addActionListener (e -> {
                                    JTextField user = new JTextField (new JTextFieldLimit (30), "", 26);
                                    user.setToolTipText ("Nombre de usuario (obligatorio)");

                                    JPasswordField pass = new JPasswordField (new JTextFieldLimit (28), "", 25);

                                    pass.setToolTipText (
                                            "Contraseña (dejar vacío para usar una contraseña aleatoria)");

                                    JComboBox <String> role = new JComboBox <String> (new Vector <String> (
                                            Arrays.asList (new String [] { "Espectador", "Administrador" })));

                                    for (;;) {
                                        if (Arrays.asList (JOptionPane.CANCEL_OPTION, JOptionPane.CLOSED_OPTION)
                                                .contains (JOptionPane.showOptionDialog (f,
                                                        new Object [] {
                                                                "Introduce un nombre de usuario y una contraseña", user,
                                                                pass, role },
                                                        "Crear usuario", JOptionPane.OK_CANCEL_OPTION,
                                                        JOptionPane.QUESTION_MESSAGE,
                                                        null, null, null)))
                                            return;

                                        if (user.getText ().equals ("")) {
                                            JOptionPane.showMessageDialog (f,
                                                    "El usuario debe tener un nombre.",
                                                    "Error en el registro",
                                                    JOptionPane.ERROR_MESSAGE);

                                            continue;
                                        }

                                        if (user.getText ().length () < 3) {
                                            JOptionPane.showMessageDialog (f,
                                                    "El nombre de usuario debe ser de al menos 3 carácteres.",
                                                    "Error en el registro",
                                                    JOptionPane.ERROR_MESSAGE);

                                            continue;
                                        }

                                        if (user.getText ().contains (" ")) {
                                            JOptionPane.showMessageDialog (f,
                                                    "El nombre de usuario no puede contener espacios.",
                                                    "Error en el registro",
                                                    JOptionPane.ERROR_MESSAGE);

                                            continue;
                                        }

                                        if (db.getEspectadores ().stream ().map (Espectador::getNombre)
                                                .collect (Collectors.toList ()).contains (user.getText ())
                                                || db.getAdministradores ().stream ()
                                                        .map (Administrador::getNombre)
                                                        .collect (Collectors.toList ())
                                                        .contains (user.getText ())) {
                                            JOptionPane.showMessageDialog (f,
                                                    "El nombre de usuario no está disponible.",
                                                    "Error en el registro",
                                                    JOptionPane.ERROR_MESSAGE);

                                            continue;
                                        }

                                        if (!new String (pass.getPassword ()).equals ("")
                                                && new String (pass.getPassword ()).strip ().length () < 4) {
                                            JOptionPane.showMessageDialog (f,
                                                    "La contraseña del usuario debe ser de al menos 4 carácteres.",
                                                    "Error en el registro",
                                                    JOptionPane.ERROR_MESSAGE);

                                            continue;
                                        }

                                        if (new String (pass.getPassword ()).contains (" ")) {
                                            JOptionPane.showMessageDialog (f,
                                                    "La contraseña no puede contener espacios.",
                                                    "Error en el registro", JOptionPane.ERROR_MESSAGE);

                                            continue;
                                        }

                                        if (new String (pass.getPassword ()).contains ("\"")
                                                || new String (pass.getPassword ()).contains ("'") || new String (pass.getPassword ()).contains ("`")) {
                                            JOptionPane.showMessageDialog (f,
                                                    "La contraseña no puede contener comillas.",
                                                    "Error en el registro", JOptionPane.ERROR_MESSAGE);

                                            continue;
                                        }

                                        if (role.getSelectedIndex () == 0) {
                                            db.insert (new Espectador (user.getText (),
                                                    new String (pass.getPassword ()).equals ("")
                                                            ? Usuario.generatePassword ()
                                                            : new String (pass.getPassword ())));

                                            return;
                                        }

                                        if (db.getAdminKeys ().isEmpty ()) {
                                            JOptionPane.showMessageDialog (f,
                                                    "No se puede registrar más administradores por el momento. Contacta con un administrador.",
                                                    "Error en el registro de administrador",
                                                    JOptionPane.ERROR_MESSAGE);

                                            return;
                                        }

                                        for (;;) {
                                            String key;
                                            if ((key = JOptionPane.showInputDialog (f,
                                                    "Para finalizar el registro como administrador introduce una de las llaves de un solo uso disponibles.")) == null)
                                                return;

                                            if (!db.getAdminKeys ().contains (key))
                                                continue;

                                            db.consumeAdminKey (key);

                                            l.setText (
                                                    String.format ("%d llaves restantes", db.getAdminKeys ().size ()));
                                            l.repaint ();

                                            break;
                                        }

                                        db.insert (new Administrador (user.getText (),
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
                            t.setAlignmentY (Component.CENTER_ALIGNMENT);

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

                                b.addActionListener (e -> new LoadingWindow ( () -> {
                                    db.regenerateAdminKeys ();

                                    l.setText (String.format ("%d llaves restantes", db.getAdminKeys ().size ()));
                                    l.repaint ();
                                }));

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
                                if (JOptionPane.showOptionDialog (f,
                                        "Lo que estás a punto de hacer es una acción irreversible.\n¿Estás seguro de querer continuar?",
                                        "Eliminar película", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                                        null, new String [] {
                                                "Confirmar",
                                                "Cancelar"
                                        }, JOptionPane.NO_OPTION) != JOptionPane.YES_OPTION)
                                    return;

                                db.delete (db.getEspectadores ());
                            });

                            return b;
                        })).get ());

                        return s;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (25, 0)));

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (0, 25)));

            return p;
        })).get (), BorderLayout.CENTER);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Gestionar usuarios");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileView.hardDriveIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
