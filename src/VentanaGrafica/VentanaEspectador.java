package VentanaGrafica;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Espectador;
import internals.GestorBD;

public class VentanaEspectador extends JFrame {
    public VentanaEspectador (GestorBD db) {
        this (db, (Espectador) null);
    }

    public VentanaEspectador (GestorBD db, Espectador espectador) {
        this (db, espectador, null);
    }

    public VentanaEspectador (GestorBD db, VentanaInicio v2) {
        this (db, null, v2);
    }

    public VentanaEspectador (GestorBD db, Espectador espectador, VentanaInicio v2)
            throws NullPointerException, UnsupportedOperationException {
        super ();

        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar un gestor de bases de datos nulo a la ventana de modo espectador.");

        VentanaEspectador v = this;

        JButton pelicula = new JButton ("Asistir a una película");
        JButton historial = new JButton ("Historial");

        this.getContentPane ().setLayout (new GridLayout (2, 1));
        this.getContentPane ().add (pelicula);
        this.getContentPane ().add (historial);

        pelicula.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed (ActionEvent e) {
                SwingUtilities.invokeLater ( () -> new VentanaSeleccionarPelicula (db, espectador, v));
            }
        });

        historial.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed (ActionEvent e) {
                SwingUtilities.invokeLater ( () -> new HistorialWindow (espectador));
            }
        });

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowOpened (WindowEvent e) {
                historial.setEnabled (espectador != null && espectador.getHistorial () != null
                        && !espectador.getHistorial ().isEmpty ());
            }

            @Override
            public void windowClosed (WindowEvent e) {
                v2.setVisible (true);
            }
        });

        this.setTitle (espectador == null ? "Anónimo" : espectador.getNombre ());
        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize (800, 600);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
