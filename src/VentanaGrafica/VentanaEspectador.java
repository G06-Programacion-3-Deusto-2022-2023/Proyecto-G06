package VentanaGrafica;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import cine.Espectador;

public class VentanaEspectador extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public VentanaEspectador(Espectador espectador) {
		
		
		
		
		this.setTitle(espectador.getNombre());		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
