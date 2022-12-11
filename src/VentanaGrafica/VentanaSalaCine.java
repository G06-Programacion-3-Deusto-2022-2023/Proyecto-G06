package VentanaGrafica;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Espectador;
import cine.Pelicula;
import cine.Sala;

import internals.Pair;

public class VentanaSalaCine extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	Sala sala;
	List<Pair<Integer, Integer>> ListFilaColumna;
	Image imagenIconOcupado;
	boolean FilaSeleccionada;
	boolean ColumnaSeleccionada;
	
	public VentanaSalaCine(VentanaSeleccionarPelicula v2,Espectador espectador, Pelicula pelicula) {
		
		VentanaSalaCine v = this;
		
		ColumnaSeleccionada = false;
		FilaSeleccionada = false;
		sala = new Sala();
		ListFilaColumna = new ArrayList<Pair<Integer,Integer>>();
		JLabel seleccionarPelicula = new JLabel("Seleccionar pelicula");
		JComboBox<Integer> Fila = new JComboBox<Integer>();
		JComboBox<Integer> Columna = new JComboBox<Integer>();
		JPanel PanelNorth = new JPanel(new GridLayout(2,1));
		JPanel ButacaSeleccionada = new JPanel();
		JPanel PanelCentral = new JPanel(new GridLayout(20, 15));
		JButton siguiente = new JButton ("Siguiente");
		siguiente.disable();
		
		PanelNorth.add(seleccionarPelicula);
		PanelNorth.add(ButacaSeleccionada);
		ButacaSeleccionada.add(Fila);
		ButacaSeleccionada.add(Columna);
		
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(PanelNorth, BorderLayout.NORTH);
		this.getContentPane().add(PanelCentral, BorderLayout.CENTER);
		this.getContentPane().add(siguiente,BorderLayout.SOUTH);
		
		Fila.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Columna.removeAllItems();
				for (int i = 0; i < ListFilaColumna.size(); i++) {
					if (ListFilaColumna.get(i).x == (Integer)Fila.getSelectedItem()) {
						Columna.addItem(ListFilaColumna.get(i).y);
					}
				}
				FilaSeleccionada = true;
				if (ColumnaSeleccionada == true && FilaSeleccionada == true) {
					siguiente.enable();
					siguiente.repaint();
				}
			}
		});
		Columna.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ColumnaSeleccionada = true;
				if (ColumnaSeleccionada == true && FilaSeleccionada == true) {
					siguiente.enable();
					siguiente.repaint();
				}
			}
		});
		siguiente.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Pair<Integer,Integer> butaca = new Pair<Integer, Integer>((Integer) Fila.getSelectedItem(), (Integer) Columna.getSelectedItem());
				
				SwingUtilities.invokeLater(() -> new VentanaComplementos(v, espectador, pelicula, sala, butaca));
				
			}
		});
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				sala.llenarSala(pelicula);
				Set<Integer> set = new TreeSet<Integer>();
				for (int i = 0; i < sala.getButacas().size(); i++) {
					if (!sala.getButacas().get(i).ocupada()) {
						ListFilaColumna.add(new Pair<Integer,Integer>(i/Sala.getColumnas(),i%Sala.getColumnas()));
					}
				}
				for (int j = 0; j < ListFilaColumna.size(); j++) {
					set.add(ListFilaColumna.get(j).x);
				}
				for (Integer integer : set) {
					Fila.addItem(integer);
				}
				v2.setVisible(false);
			}
			
			
			@Override
			public void windowClosed(WindowEvent e) {
				v2.setVisible(true);
				
			}
		});
		
		this.setTitle(espectador.getNombre());		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.setSize(1000, 800);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
