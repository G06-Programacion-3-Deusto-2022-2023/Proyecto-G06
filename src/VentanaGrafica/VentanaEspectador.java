package VentanaGrafica;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Espectador;
import cine.Sala;
import internals.Pair;

public class VentanaEspectador extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public VentanaEspectador(VentanaInicioSesion v2,Espectador espectador) {
		VentanaEspectador v = this;
		
		JButton pelicula = new JButton("Asistir a una pelcula");
		JButton historial = new JButton("Historial");
		
		this.getContentPane().setLayout(new GridLayout(2,1));
		this.getContentPane().add(pelicula);
		this.getContentPane().add(historial);
		
		pelicula.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> new VentanaSeleccionarPelicula(espectador, v));
				
			}
		});
		historial.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> new HistorialWindow(espectador));
				
			}
		});
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				v2.setVisible(false);
			}
			
			
			@Override
			public void windowClosed(WindowEvent e) {
				v2.setVisible(true);
				
			}
		});
		this.setTitle(espectador.getNombre());		
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
