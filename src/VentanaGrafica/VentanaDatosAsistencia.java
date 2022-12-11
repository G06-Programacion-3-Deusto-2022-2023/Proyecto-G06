package VentanaGrafica;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Espectador;
import cine.Butaca;
import cine.Complemento;
import cine.Entrada;
import cine.Pelicula;
import cine.Sala;
import internals.Pair;

public class VentanaDatosAsistencia extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public VentanaDatosAsistencia(VentanaComplementos v2,Espectador espectador, Pelicula pelicula,Sala sala ,Pair<Integer,Integer> butaca, Map<Complemento,Integer> complementos) {
		BigDecimal precio = BigDecimal.valueOf(7);
		precio = PrecioComplementos(complementos, precio);
		
		JLabel peliculaTexto = new JLabel("Pelicula:" + pelicula.getNombre());
		JLabel fecha = new JLabel("Fecha:");
		JLabel asiento = new JLabel("Asiento:" + butaca.toString());
		JLabel complementosTexto = new JLabel("Complementos:" + complementos.toString());
		JLabel precioTexto = new JLabel("Precio:" + precio.toString());
		JButton confirmar = new JButton("Confirmar");
		JPanel central = new JPanel(new GridLayout(5,1));
		
		central.add(peliculaTexto);
		central.add(fecha);
		central.add(asiento);
		central.add(complementosTexto);
		central.add(precioTexto);
		
		confirmar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Entrada entrada = new Entrada(espectador, pelicula, Calendar.getInstance(), sala, new Butaca(espectador), complementos);
				espectador.getHistorial().add(entrada);
				setVisible(false);
				SwingUtilities.invokeLater(() -> new VentanaEspectador(new VentanaInicioSesion(new VentanaInicio()),espectador));
			}
		});
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(confirmar, BorderLayout.SOUTH);
		this.getContentPane().add(central,BorderLayout.CENTER);
		
		this.setTitle(espectador.getNombre());		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.setSize(1000, 800);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private BigDecimal PrecioComplementos(Map<Complemento,Integer> complementos, BigDecimal precio) {
		for (Complemento c : complementos.keySet()) {
			precio.add(c.getPrecio().multiply(BigDecimal.valueOf(complementos.get(c))));
		}
		return precio;
	}
}
