package VentanaGrafica;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Administrador;
import cine.Espectador;
import cine.Usuario;

public class VentanaInicioSesion extends JFrame{
	
	private static final long serialVersionUID = 1L;

	public VentanaInicioSesion() {
		
		ArrayList<Espectador> espectadores = new ArrayList<>();
		ArrayList<Administrador> administradores = new ArrayList<>();
		ArrayList<Usuario> usuarios = new ArrayList<>();
		
		JLabel usuario = new JLabel("Usuario");
		JLabel contraseña = new JLabel("Contraseña");
		JButton iniciarSesion = new JButton("Iniciar sesion");
		JTextField textoUsuario = new JTextField("usuario");
		JTextField textoContraseña = new JTextField("contraseña");
		
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
		
		iniciarSesion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Usuario usuario : usuarios) {
					if (usuario.getNombre().equals(textoUsuario.getText()) && usuario.getContrasena().equals(textoContraseña.getText())) {
						if (administradores.contains(usuario)) {
							System.out.println("administrador");
							SwingUtilities.invokeLater(() -> new VentanaAdministrador((Administrador) usuario));
						} else if (espectadores.contains(usuario)) {
							System.out.println("espectador");
							SwingUtilities.invokeLater(() -> new VentanaEspectador((Espectador) usuario));
						}
					}
				}
				
			}
		});
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				
				
				Espectador espectador = new Espectador("iker", "1234");
				espectadores.add(espectador);
				
				Administrador administrador = new Administrador("mikel", "1234");
				administradores.add(administrador);
				usuarios.addAll(espectadores);
				usuarios.addAll(administradores);
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
