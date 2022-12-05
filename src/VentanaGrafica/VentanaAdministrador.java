package VentanaGrafica;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import cine.Administrador;

public class VentanaAdministrador extends JFrame {

	
	private static final long serialVersionUID = 1L;

	public VentanaAdministrador(Administrador administrador) {
		
		
		this.setTitle(administrador.getNombre());		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
