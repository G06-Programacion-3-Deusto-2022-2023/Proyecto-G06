package VentanaGrafica;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class VentanaInicioSesion extends JFrame{
	
	private static final long serialVersionUID = 1L;

	public VentanaInicioSesion() {
		
		JLabel usuario = new JLabel("Usuario");
		JLabel contraseña = new JLabel("Contraseña");
		JButton iniciarSesion = new JButton("Iniciar sesion");
		JTextField textoUsuario = new JTextField();
		JTextField textoContraseña = new JTextField();
		
		this.getContentPane().setLayout(new GridLayout(3,2));
		this.getContentPane().add(usuario);
		this.getContentPane().add(textoUsuario);
		this.getContentPane().add(contraseña);
		this.getContentPane().add(textoContraseña);
		this.getContentPane().add(iniciarSesion);
		
		this.setTitle("Ventana inicio sesion");		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
