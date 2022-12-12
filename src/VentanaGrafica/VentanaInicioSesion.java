package VentanaGrafica;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Administrador;
import cine.Espectador;
import cine.GestorBD;
import cine.Usuario;

public class VentanaInicioSesion extends JFrame{
	
	private static final long serialVersionUID = 1L;
		
	public VentanaInicioSesion(GestorBD bd, VentanaInicio v) {
		
		VentanaInicioSesion v2 = this;
		
		ArrayList<Espectador> espectadores = new ArrayList<>();
		ArrayList<Administrador> administradores = new ArrayList<>();
		ArrayList<Usuario> usuarios = new ArrayList<>();
		
		JLabel usuario = new JLabel("Usuario");
		JLabel contrasena = new JLabel("Contraseña");
		JButton iniciarSesion = new JButton("Iniciar sesion");
		JTextField textoUsuario = new JTextField();
		JTextField textoContrasena = new JTextField();
		
		this.getContentPane().setLayout(new GridLayout(3,2));
		this.getContentPane().add(usuario);
		this.getContentPane().add(textoUsuario);
		this.getContentPane().add(contrasena);
		this.getContentPane().add(textoContrasena);
		this.getContentPane().add(iniciarSesion);
		
		this.setTitle("Ventana inicio sesión");		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		iniciarSesion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Usuario usuario : usuarios) {
					if (usuario.getNombre().equals(textoUsuario.getText()) && usuario.getContrasena().equals(textoContrasena.getText())) {
						if (administradores.contains(usuario)) {
							SwingUtilities.invokeLater(() -> new AdministradorWindow(bd, (Administrador) usuario));
						} else if (espectadores.contains(usuario)) {
							SwingUtilities.invokeLater(() -> new VentanaEspectador(bd, v2, (Espectador) usuario));
						}
					}
				}
				
			}
		});
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				v.setVisible(false);
				
				Espectador espectador = new Espectador("iker", "1234");
				espectadores.add(espectador);
				
				Administrador administrador = new Administrador("mikel", "1234");
				administradores.add(administrador);
				usuarios.addAll(espectadores);
				usuarios.addAll(administradores);
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				v.setVisible(true);
				
			}
		});
	}
	
}
