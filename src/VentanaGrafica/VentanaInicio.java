package VentanaGrafica;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import internals.GestorBD;

public class VentanaInicio extends JFrame {

    private static final long serialVersionUID = 1L;

    private JButton inicioSesion;
    private JButton Registrarse;
    private JButton Invitado;
    private JLabel DeustoCines;
    private JPanel Superior;
    private JPanel Inferior;

    public VentanaInicio () {
        this (null);
    }

    public VentanaInicio (GestorBD db) {
        VentanaInicio v = this;

        this.DeustoCines = new JLabel ("Deusto Cines");

        this.Superior = new JPanel ();
        this.Superior.add (DeustoCines);

        this.inicioSesion = new JButton ("Iniciar sesión");
        this.Registrarse = new JButton ("Registrarse");
        this.Invitado = new JButton ("Continuar como invitado");

        this.Inferior = new JPanel (new GridLayout (1, 3));
        this.Inferior.add (inicioSesion);
        this.Inferior.add (Registrarse);
        this.Inferior.add (Invitado);

        this.getContentPane ().setLayout (new GridLayout (2, 1));
        this.getContentPane ().add (Superior);
        this.getContentPane ().add (Inferior);

        this.inicioSesion.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                v.dispose ();
                SwingUtilities.invokeLater ( () -> new VentanaInicioSesion (db, v));
            }
        });
        this.Registrarse.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                v.dispose ();
                SwingUtilities.invokeLater ( () -> new RegistroWindow (new GestorBD (), v));

            }
        });
        this.Invitado.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                v.dispose ();
                SwingUtilities.invokeLater (
                        () -> new VentanaEspectador (db, v));

            }
        });

        this.setTitle ("Ventana Inicio");
        this.setIconImage (new ImageIcon (this.getClass ()
                .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ());
        this.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        this.setSize (800, 600);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
