package VentanaGrafica;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class RegistroWindow extends JFrame {
    public RegistroWindow (GestorBD db) {
        this (db, null);
    }

    public RegistroWindow (GestorBD db, VentanaInicio w) throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar un gestor de bases de datos nulo a la ventana de registro de usuarios.");

        RegistroWindow f = this;

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (w != null)
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

                JComponent fields[][] = new JComponent [] [] {
                        { new JLabel ("Usuario:"), new JTextField (new JTextFieldLimit (30), "", 26) },
                        { new JLabel ("Contraseña:"), new JPasswordField (new JTextFieldLimit (28), "", 25) },
                        { new JLabel ("Confirmar contraseña:"), new JPasswordField (new JTextFieldLimit (28), "", 25) },
                        { new JLabel ("Rol:"), new JComboBox <String> (
                                new Vector <String> (Arrays.asList (new String [] { "Espectador", "Administrador" }))) }
                };

                ((JTextField) fields [0] [1]).setToolTipText ("Nombre de usuario (obligatorio)");
                ((JTextField) fields [1] [1]).setToolTipText ("Contraseña (obligatorio)");
                ((JTextField) fields [2] [1]).setToolTipText ("Confirmar contraseña (obligatorio)");
                ((JComboBox <String>) fields [3] [1]).setToolTipText ("Rol del usuario (obligatorio)");

                int max = 0;
                for (int i = 1; i < fields.length; i++)
                    max = ((JLabel) fields [max] [0]).getMinimumSize ().getWidth () < ((JLabel) fields [i] [0])
                            .getMinimumSize ()
                            .getWidth () ? i : max;
                for (int i = 0; i < fields.length; i++) {
                    ((JLabel) fields [i] [0]).setMinimumSize (((JLabel) fields [max] [0]).getMinimumSize ());
                    ((JLabel) fields [i] [0]).setPreferredSize (((JLabel) fields [max] [0]).getPreferredSize ());
                    ((JLabel) fields [i] [0]).setMaximumSize (((JLabel) fields [max] [0]).getMaximumSize ());
                }

                for (int i[] = new int [1]; i [0] < fields.length; i [0]++) {
                    q.add (((Supplier <JPanel>) ( () -> {
                        JPanel r = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 5));

                        r.add (fields [i [0]] [0]);
                        r.add (fields [i [0]] [1]);
                        if (i [0] == 1)
                            r.add (((Supplier <JPanel>) ( () -> {
                                JPanel s = new JPanel ();
                                s.setLayout (new BoxLayout (s, BoxLayout.X_AXIS));

                                s.add (Box.createRigidArea (new JTextField (2).getPreferredSize ()));

                                s.add (((Supplier <JButton>) ( () -> {
                                    JButton b = new JButton (new ImageIcon (new ImageIcon (this.getClass ()
                                            .getResource ("/toolbarButtonGraphics/general/ZoomIn16.gif"))
                                                    .getImage ()
                                                    .getScaledInstance (16, 16, Image.SCALE_SMOOTH)));

                                    b.setToolTipText ("Pulsa este botón para ver la contraseña.");

                                    b.addActionListener (e -> {
                                        if (((JPasswordField) fields [1] [1]).getEchoChar () == (char) 0) {
                                            b.setIcon (new ImageIcon (new ImageIcon (this.getClass ()
                                                    .getResource ("/toolbarButtonGraphics/general/ZoomIn16.gif"))
                                                            .getImage ()
                                                            .getScaledInstance (16, 16, Image.SCALE_SMOOTH)));

                                            ((JPasswordField) fields [1] [1])
                                                    .setEchoChar (new JPasswordField ().getEchoChar ());

                                            return;
                                        }

                                        b.setIcon (new ImageIcon (new ImageIcon (this.getClass ()
                                                .getResource ("/toolbarButtonGraphics/general/ZoomOut16.gif"))
                                                        .getImage ()
                                                        .getScaledInstance (16, 16, Image.SCALE_SMOOTH)));

                                        ((JPasswordField) fields [1] [1])
                                                .setEchoChar ((char) 0);
                                    });

                                    return b;
                                })).get ());

                                return s;
                            })).get ());
                        if (i [0] == 2)
                            r.add (((Supplier <JPanel>) ( () -> {
                                JPanel s = new JPanel ();
                                s.setLayout (new BoxLayout (s, BoxLayout.X_AXIS));

                                s.add (Box.createRigidArea (new JTextField (2).getPreferredSize ()));

                                s.add (((Supplier <JButton>) ( () -> {
                                    JButton b = new JButton (new ImageIcon (new ImageIcon (this.getClass ()
                                            .getResource ("/toolbarButtonGraphics/general/ZoomIn16.gif"))
                                                    .getImage ()
                                                    .getScaledInstance (16, 16, Image.SCALE_SMOOTH)));

                                    b.setToolTipText ("Pulsa este botón para ver la contraseña.");

                                    b.addActionListener (e -> {
                                        if (((JPasswordField) fields [2] [1]).getEchoChar () == (char) 0) {
                                            b.setIcon (new ImageIcon (new ImageIcon (this.getClass ()
                                                    .getResource ("/toolbarButtonGraphics/general/ZoomIn16.gif"))
                                                            .getImage ()
                                                            .getScaledInstance (16, 16, Image.SCALE_SMOOTH)));

                                            ((JPasswordField) fields [2] [1])
                                                    .setEchoChar (new JPasswordField ().getEchoChar ());

                                            return;
                                        }

                                        b.setIcon (new ImageIcon (new ImageIcon (this.getClass ()
                                                .getResource ("/toolbarButtonGraphics/general/ZoomOut16.gif"))
                                                        .getImage ()
                                                        .getScaledInstance (16, 16, Image.SCALE_SMOOTH)));

                                        ((JPasswordField) fields [2] [1])
                                                .setEchoChar ((char) 0);
                                    });

                                    return b;
                                })).get ());

                                return s;
                            })).get ());
                        if (i [0] == 1)
                            r.add (((Supplier <JPanel>) ( () -> {
                                JPanel s = new JPanel ();
                                s.setLayout (new BoxLayout (s, BoxLayout.X_AXIS));

                                s.add (Box.createRigidArea (new JTextField (2).getPreferredSize ()));

                                s.add (((Supplier <JButton>) ( () -> {
                                    JButton b = new JButton ("Generar contraseña");

                                    b.setToolTipText ("Pulsa este botón para generar una contraseña.");

                                    b.addActionListener (e -> {
                                        String password = Usuario.generatePassword ();

                                        ((JPasswordField) fields [1] [1]).setText (password);
                                        ((JPasswordField) fields [2] [1]).setText (password);
                                    });

                                    return b;
                                })).get ());

                                return s;
                            })).get ());

                        return r;
                    })).get ());
                }

                q.add (Box.createRigidArea (new Dimension (0, 20)));

                q.add (((Supplier <JButton>) ( () -> {
                    JButton b = new JButton ("Registrarse");

                    b.addActionListener (e -> {
                        if (((JTextField) fields [0] [1]).getText ().strip ().equals ("")) {
                            JOptionPane.showMessageDialog (f, "El usuario debe tener un nombre.",
                                    "Error en el registro", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (((JTextField) fields [0] [1]).getText ().strip ().length () < 3) {
                            JOptionPane.showMessageDialog (f,
                                    "El nombre de usuario debe ser de al menos 3 carácteres.", "Error en el registro",
                                    JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (((JTextField) fields [0] [1]).getText ().contains (" ")) {
                            JOptionPane.showMessageDialog (f, "El nombre de usuario no puede contener espacios.",
                                    "Error en el registro", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (((JTextField) fields [0] [1]).getText ().contains ("\"")
                                || ((JTextField) fields [0] [1]).getText ().contains ("'")
                                || ((JTextField) fields [0] [1]).getText ().contains ("`")) {
                            JOptionPane.showMessageDialog (f, "El nombre de usuario no puede contener comillas.",
                                    "Error en el registro", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if ((((JComboBox <String>) fields [3] [1]).getSelectedIndex () == 0
                                && db.getEspectadores ().stream ().map (Espectador::getNombre)
                                        .collect (Collectors.toList ())
                                        .contains (((JTextField) fields [0] [1]).getText ()))
                                || (((JComboBox <String>) fields [3] [1]).getSelectedIndex () == 1
                                        && db.getAdministradores ().stream ()
                                                .map (Administrador::getNombre)
                                                .collect (Collectors.toList ())
                                                .contains (((JTextField) fields [0] [1]).getText ()))) {
                            JOptionPane.showMessageDialog (f,
                                    "El nombre de usuario no está disponible.", "Error en el registro",
                                    JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (new String (((JPasswordField) fields [1] [1]).getPassword ()).strip ()
                                .equals ("")) {
                            JOptionPane.showMessageDialog (f,
                                    "El usuario debe tener una contraseña.", "Error en el registro",
                                    JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (new String (((JPasswordField) fields [1] [1]).getPassword ()).strip ()
                                .length () < 4) {
                            JOptionPane.showMessageDialog (f,
                                    "La contraseña del usuario debe ser de al menos 4 carácteres.",
                                    "Error en el registro", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (new String (((JPasswordField) fields [1] [1]).getPassword ()).contains (" ")) {
                            JOptionPane.showMessageDialog (f,
                                    "La contraseña del usuario no puede contener espacios.", "Error en el registro",
                                    JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (new String (((JPasswordField) fields [1] [1]).getPassword ()).contains ("\"")
                                || new String (((JPasswordField) fields [1] [1]).getPassword ()).contains ("'")
                                || ((JTextField) fields [0] [1]).getText ().contains ("`")) {
                            JOptionPane.showMessageDialog (f,
                                    "La contraseña del usuario no puede contener comillas.", "Error en el registro",
                                    JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (!new String (((JPasswordField) fields [2] [1]).getPassword ())
                                .equals (new String (((JPasswordField) fields [1] [1]).getPassword ()))) {
                            JOptionPane.showMessageDialog (f,
                                    "Las contraseñas no coinciden.", "Error en el registro", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (((JComboBox <String>) fields [3] [1]).getSelectedIndex () == 0) {
                            db.insert (
                                    new Espectador (((JTextField) fields [0] [1]).getText (),
                                            new String (((JPasswordField) fields [1] [1]).getPassword ())));

                            f.dispose ();

                            return;
                        }

                        if (db.getAdminKeys () == null || db.getAdminKeys ().isEmpty ()) {
                            JOptionPane.showMessageDialog (f,
                                    "No se puede registrar más administradores por el momento. Contacta con un administrador.",
                                    "Error en el registro de administrador",
                                    JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        String key;
                        if ((key = JOptionPane.showInputDialog (f,
                                "Para finalizar el registro como administrador introduce una de las llaves de un solo uso disponibles.")) == null)
                            return;

                        if (!db.getAdminKeys ().contains (key)) {
                            JOptionPane.showMessageDialog (f,
                                    "La clave introducida es incorrecta. Por favor, introduce una clave válida.",
                                    "Error en el registro de administrador",
                                    JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        db.consumeAdminKey (key);

                        db.insert (
                                new Administrador (((JTextField) fields [0] [1]).getText (),
                                        new String (((JPasswordField) fields [1] [1]).getPassword ())));

                        f.dispose ();
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
        this.setTitle ("Registrarse");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("Tree.expandedIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
