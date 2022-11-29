package VentanaGrafica;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class VentanaInicio extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JButton inicioSesion;
	private JButton Registrarse;
	private JButton Invitado;
	private JLabel DeustoCines;
	private JPanel Superior;
	private JPanel Inferior;
	
	public VentanaInicio() {
		
		this.DeustoCines = new JLabel("Deusto Cines");
		
		this.Superior = new JPanel();
		this.Superior.add(DeustoCines);
		
		this.inicioSesion = new JButton("Iniciar sesion como usuario");
		this.Registrarse = new JButton("Registrarse");
		this.Invitado = new JButton("Continuar como invitado");
		
		this.Inferior = new JPanel(new GridLayout(1,3));
		this.Inferior.add(inicioSesion);
		this.Inferior.add(Registrarse);
		this.Inferior.add(Invitado);
		
		this.getContentPane().setLayout(new GridLayout(2,1));
		this.getContentPane().add(Superior);
		this.getContentPane().add(Inferior);
		
		this.inicioSesion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> new VentanaInicioSesion());
				
			}
		});
		this.Registrarse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Hola");
				
			}
		});
		this.Invitado.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Hola");
		
			}
		});
		
		this.setTitle("Ventana Inicio");		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);	
	}
	
}
