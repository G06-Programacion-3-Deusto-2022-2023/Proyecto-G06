package graphical;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Complemento;
import cine.Espectador;
import cine.Pelicula;
import cine.Sala;
import internals.GestorBD;
import internals.Pair;

public class ComplementosWindow extends JFrame {
    Set <Complemento> complementos;
    Map <Complemento, Integer> complementosEspectador;

    public ComplementosWindow (GestorBD db, SalaCineWindow v2, Espectador espectador, Pelicula pelicula, Sala sala,
            Pair <Integer, Integer> Butaca) {
        ComplementosWindow v = this;

        complementos = new TreeSet <Complemento> ();
        complementosEspectador = new HashMap <Complemento, Integer> ();
        JLabel seleccionarComplementos = new JLabel ("Seleccione Complementos");
        JLabel pedido = new JLabel ("Producto");
        JLabel unidadesTexto = new JLabel ("Undidades");
        JComboBox <String> productos = new JComboBox <String> ();
        JTextField unidades = new JTextField ();
        JButton anadir = new JButton ("Anadir");
        JButton quitar = new JButton ("Quitar");
        JButton confirmar = new JButton ("Confirmar");
        JPanel producto = new JPanel (new GridLayout (2, 2));
        JPanel central = new JPanel (new GridLayout (2, 2));

        producto.add (pedido);
        producto.add (unidadesTexto);
        producto.add (productos);
        producto.add (unidades);

        central.add (producto);
        central.add (anadir);
        central.add (confirmar);
        central.add (quitar);

        this.getContentPane ().setLayout (new BorderLayout ());
        this.getContentPane ().add (seleccionarComplementos, BorderLayout.NORTH);
        this.getContentPane ().add (central, BorderLayout.CENTER);

        anadir.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                complementosEspectador.putIfAbsent (
                        ObtenerComplemento (complementos, (String) productos.getSelectedItem ()),
                        Integer.valueOf (unidades.getText ()));
                for (Complemento c : complementosEspectador.keySet ()) {
                    if (c.equals (ObtenerComplemento (complementos, (String) productos.getSelectedItem ()))) {

                    }
                }

            }
        });
        quitar.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                for (Complemento c : complementosEspectador.keySet ()) {
                    if (c.getNombre () == unidades.getText ()) {
                        complementosEspectador.remove (c);
                    }
                }
            }
        });
        confirmar.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                SwingUtilities.invokeLater ( () -> new DatosAsistenciaWindow (db, v, espectador, pelicula, sala,
                        Butaca, complementosEspectador));

            }
        });
        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowOpened (WindowEvent e) {
                Complemento complemento1 = new Complemento ("Palomitas", BigDecimal.valueOf (8.5));
                Complemento complemento2 = new Complemento ("PerritoCaliente", BigDecimal.valueOf (10.5));
                Complemento complemento3 = new Complemento ("CocaCola", BigDecimal.valueOf (4.5));
                complementos.add (complemento1);
                complementos.add (complemento2);
                complementos.add (complemento3);

                AnadirComplementos (productos, complementos);

                v2.setVisible (false);
            }

            @Override
            public void windowClosed (WindowEvent e) {
                v2.setVisible (true);

            }
        });

        this.setTitle (espectador == null ? "Invitado" : espectador.getNombre ());
        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);

        this.setSize (1000, 800);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }

    public void AnadirComplementos (JComboBox <String> productos, Set <Complemento> complementos) {
        List <Complemento> Lista = new ArrayList <Complemento> (complementos);
        for (int i = 0; i < Lista.size (); i++) {
            productos.addItem (Lista.get (i).getNombre ());
        }
    }

    public Complemento ObtenerComplemento (Set <Complemento> complementos, String nombre) {
        Complemento complemento = null;
        for (Complemento c : complementos) {
            if (c.getNombre ().equals (nombre)) {
                complemento = new Complemento (c);
            }
        }
        return complemento;
    }
}
