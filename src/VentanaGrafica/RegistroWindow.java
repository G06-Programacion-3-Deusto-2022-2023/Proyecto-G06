package VentanaGrafica;

import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.function.BooleanSupplier;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import cine.Administrador;
import cine.Espectador;
import cine.GestorBD;
import cine.Usuario;
import internals.JTextFieldLimit;

public class RegistroWindow extends JFrame {
    public RegistroWindow (GestorBD bd) throws NullPointerException {
        super ();

        if (bd == null)
            throw new NullPointerException (
                    "No se puede pasar un gestor de bases de datos nulo a la ventana de registro de usuarios.");

        RegistroWindow f = this;

        JTextField usuario = new JTextField (new JTextFieldLimit (30), "", 26);
        usuario.setToolTipText ("Nombre de usuario (obligatorio)");

        JPasswordField pass = new JPasswordField (new JTextFieldLimit (28), "", 25);
        pass.setToolTipText ("Contraseña (obligatorio)");

        JPasswordField confirm = new JPasswordField (new JTextFieldLimit (28), "", 25);
        confirm.setToolTipText ("Confirmar contraseña (obligatorio)");

        JList <String> rol = new JList <String> (
                new Vector <String> (Arrays.asList (new String [] { "Espectador", "Administrador" })));

        this.setLayout (new BoxLayout (this.getContentPane (), BoxLayout.Y_AXIS));

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel (new GridLayout (4, 1));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q;
                GroupLayout l = new GroupLayout (q = new JPanel ());
                l.setHorizontalGroup (
                        l.createSequentialGroup ().addComponent (new JLabel ("Usuario:")).addComponent (usuario));

                return q;
            })).get ());

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q;
                GroupLayout l = new GroupLayout (q = new JPanel ());
                l.setHorizontalGroup (
                        l.createSequentialGroup ().addComponent (new JLabel ("Contraseña:")).addComponent (pass)
                                .addComponent (((Supplier <JButton>) ( () -> {
                                    JButton b = new JButton ("Generar contraseña");
                                    b.setToolTipText ("Pulsa este botón para generar una contraseña");

                                    b.addActionListener (e -> {
                                        String password = Usuario.generatePassword ();

                                        pass.setText (password);
                                        confirm.setText (password);
                                    });
                                })).get ()));

                return q;
            })).get ());

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q;
                GroupLayout l = new GroupLayout (q = new JPanel ());
                l.setHorizontalGroup (
                        l.createSequentialGroup ().addComponent (new JLabel ("Confirmar contraseña:"))
                                .addComponent (confirm));

                return q;
            })).get ());

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q;
                GroupLayout l = new GroupLayout (q = new JPanel ());
                l.setHorizontalGroup (
                        l.createSequentialGroup ().addComponent (new JLabel ("Rol:")).addComponent (rol));

                return q;
            })).get ());

            return p;
        })).get ());

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel (new FlowLayout ());
            p.add (((Supplier <JButton>) ( () -> {
                JButton b = new JButton ("Registrarse");
                b.addActionListener (e -> {
                    if (usuario.getText ().equals ("")) {
                        JOptionPane.showMessageDialog (f,
                                "El usuario debe tener un nombre.");

                        return;
                    }

                    if (usuario.getText ().length () < 3) {
                        JOptionPane.showMessageDialog (f,
                                "El nombre de usuario debe ser de al menos 3 carácteres.");

                        return;
                    }

                    if (bd.obtenerDatosEspectadores ().stream ().map (Espectador::getNombre)
                            .collect (Collectors.toList ()).contains (usuario.getText ())
                            || bd.obtenerDatosAdministradorPorNombre (usuario.getText ()) != null) {
                        JOptionPane.showMessageDialog (f,
                                "El nombre de usuario debe ser de al menos 3 carácteres.");

                        return;
                    }

                    if (new String (pass.getPassword ()).equals ("")) {
                        JOptionPane.showMessageDialog (f,
                                "El usuario debe tener una contraseña.");

                        return;
                    }

                    if (new String (pass.getPassword ()).length () < 4) {
                        JOptionPane.showMessageDialog (f,
                                "La contraseña del usuario debe ser de al menos 4 carácteres.");

                        return;
                    }

                    if (!new String (confirm.getPassword ()).equals (new String (pass.getPassword ()))) {
                        JOptionPane.showMessageDialog (f,
                                "Las contraseñas no coinciden.");

                        return;
                    }

                    if (rol.getSelectedIndex () == 0) {
                        bd.insertarDatosEspectador (
                                new Espectador (usuario.getText (), new String (pass.getPassword ())));

                        f.dispose ();
                    }

                    if (bd.getAdminKeys () == null || bd.getAdminKeys ().isEmpty ()) {
                        JOptionPane.showMessageDialog (f,
                                "No se puede registrar más administradores por el momento. Contacta con un administrador.",
                                "Error en el registro de administrador",
                                JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    String key;
                    if (!bd.getAdminKeys ().contains (key = JOptionPane.showInputDialog (f,
                            "Para finalizar el registro como administrador introduce una de las llaves de un solo uso disponibles."))) {
                        JOptionPane.showMessageDialog (f, "", "Error en el registro de administrador",
                                JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    bd.consumeKey (key);

                    bd.insertarDatosAdministrador (
                            new Administrador (usuario.getText (), new String (pass.getPassword ())));

                    f.dispose ();
                });

                return b;
            })).get ());

            return p;
        })).get ());

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Añadir una película");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("Tree.expandedIcon", new Locale ("es-ES"))).getImage ());
        this.pack ();
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
