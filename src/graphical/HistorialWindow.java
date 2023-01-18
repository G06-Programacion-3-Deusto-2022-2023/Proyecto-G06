package graphical;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cine.Complemento;
import cine.Entrada;
import cine.Espectador;
import cine.Pelicula;
import internals.Pair;
import internals.Triplet;
import internals.Utils;
import internals.swing.ComplementosListCellRenderer;
import internals.swing.PeliculasListRenderer;

public class HistorialWindow extends JFrame {
    public HistorialWindow (Espectador espectador) {
        this (espectador, null);
    }

    public HistorialWindow (Espectador espectador, EspectadorWindow w)
            throws NullPointerException, UnsupportedOperationException {
        super ();

        if (espectador == null)
            throw new NullPointerException (
                    "No se puede pasar un espectador nulo a la ventana de visualización de historiales.");

        if (espectador.getHistorial () == null)
            throw new NullPointerException ("El espectador no puede tener un historial nulo.");

        if (espectador.getHistorial ().isEmpty ())
            throw new UnsupportedOperationException (
                    "No se puede llamar a la ventana de visualización de historiales sobre un historial vacío.");

        HistorialWindow f = this;
        JLabel valoracion = new JLabel ();

        Entrada historial[] = espectador.getHistorial ().toArray (new Entrada [0]);
        int current[] = new int [] { 0 };

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (w != null)
                    w.setVisible (true);
            }
        });

        this.add (((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel (espectador.getNombre ());
            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

            return l;
        })).get (), BorderLayout.PAGE_START);

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                JPanel r = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));
                r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                JLabel pelicula = ((Supplier <JLabel>) ( () -> {
                    JLabel l = new JLabel ();
                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                    return l;
                })).get ();
                JLabel sala = new JLabel ();
                JLabel asiento = new JLabel ();
                JLabel fecha = new JLabel ();
                JLabel duracion = new JLabel ();
                JSplitPane complementosPane = new JSplitPane ();
                Dimension dims;
                complementosPane.setRightComponent (Box.createRigidArea (dims = new Dimension (200, 100)));
                Component filler = Box.createRigidArea (new Dimension (10, 0));
                JPanel s = ((Supplier <JPanel>) ( () -> {
                    JPanel t = new JPanel ();
                    t.setLayout (new BoxLayout (t, BoxLayout.X_AXIS));
                    t.setAlignmentX (Component.LEFT_ALIGNMENT);

                    return t;
                })).get ();

                Runnable a = () -> {
                    pelicula.setText (historial [current [0]].getPelicula () == null ? "Ninguna película"
                            : historial [current [0]].getPelicula ().getNombre ());

                    sala.setText ("Sala: " + historial [current [0]].getSala () == null ? "ninguna" : "");

                    asiento.setText ("Asiento: " + historial [current [0]].getSala () == null
                            || historial [current [0]].getButaca () == null ? "ninguno" : ((Supplier <String>) ( () -> {
                                int rc[] = historial [current [0]].getSala ()
                                        .getSeatIndex (historial [current [0]].getButaca ());
                                return rc [0] == -1 ? "ninguno" : String.format ("fila %d, butaca %d", rc [0], rc [1]);
                            })).get ());

                    fecha.setText (String.format ("Fecha: %s", historial [current [0]].getFecha () == null ? "--/--/--"
                            : ((Supplier <String>) ( () -> {
                                Triplet <Integer, Integer, Integer> date = Utils
                                        .getDate (historial [current [0]].getFecha ());

                                return String.format ("%d/%02d/%d", date.x, date.y, date.z);
                            })).get ()));

                    duracion.setText ("Duración: " + historial [current [0]].getPelicula ().duracionToString ());

                    s.removeAll ();
                    s.add (new JLabel ("Complementos:"));

                    if (historial [current [0]].getComplementos () != null
                            && !historial [current [0]].getComplementos ().isEmpty ()) {
                        complementosPane.setLeftComponent (((Supplier <JScrollPane>) ( () -> {
                            JList <Complemento> l = new JList <Complemento> (
                                    historial [current [0]].getComplementos () == null ? new Vector <Complemento> ()
                                            : historial [current [0]].getComplementos ().keySet ().stream ()
                                                    .collect (Collectors.toCollection (Vector::new)));
                            l.setCellRenderer (new ComplementosListCellRenderer ());
                            l.setVisibleRowCount (5);

                            l.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
                            l.addListSelectionListener (e -> {
                                if (historial [current [0]].getComplementos () == null
                                        || l.getSelectedValue () == null) {
                                    complementosPane.setRightComponent (Box.createRigidArea (new Dimension (10, 10)));

                                    return;
                                }

                                complementosPane.setRightComponent (((Supplier <JScrollPane>) ( () -> {
                                    JPanel t = new JPanel ();
                                    t.setLayout (new BoxLayout (t, BoxLayout.Y_AXIS));
                                    t.setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));

                                    JScrollPane sp = new JScrollPane (t,
                                            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED) {
                                        @Override
                                        public Dimension getSize () {
                                            return this.getPreferredSize ();
                                        }

                                        @Override
                                        public Dimension getPreferredSize () {
                                            return dims;
                                        }

                                        @Override
                                        public Dimension getMinimumSize () {
                                            return this.getPreferredSize ();
                                        }

                                        @Override
                                        public Dimension getMaximumSize () {
                                            return this.getPreferredSize ();
                                        }
                                    };

                                    Pair <Complemento, BigInteger> c = ((Supplier <Pair <Complemento, BigInteger>>) ( () -> {
                                        Complemento ks[] = historial [current [0]].getComplementos ().keySet ()
                                                .toArray (new Complemento [0]);
                                        for (int i = 0; i < ks.length; i++)
                                            if (ks [i].equals (l.getSelectedValue ()))
                                                return new Pair <Complemento, BigInteger> (ks [i],
                                                        historial [current [0]].getComplementos ().get (ks [i]));

                                        return null;
                                    })).get ();

                                    if (c == null)
                                        return sp;

                                    int d;

                                    t.add (new JLabel ("Nombre: " + c.x.getNombre ()));
                                    t.add (new JLabel (
                                            "Precio: " + String.format ("%.2f €",
                                                    c.x.aplicarDescuento ().doubleValue ())));
                                    if ((d = c.x.getDescuento ()) != 0)
                                        t.add (new JLabel ("Descuento: " + d + "%"));
                                    t.add (new JLabel ("Unidades: " + c.y));

                                    return sp;
                                })).get ());
                            });
                            l.setSelectedIndex (0);

                            return new JScrollPane (l, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED) {
                                @Override
                                public Dimension getSize () {
                                    return this.getPreferredSize ();
                                }

                                @Override
                                public Dimension getPreferredSize () {
                                    return dims;
                                }

                                @Override
                                public Dimension getMinimumSize () {
                                    return this.getPreferredSize ();
                                }

                                @Override
                                public Dimension getMaximumSize () {
                                    return this.getPreferredSize ();
                                }
                            };
                        })).get ());

                        s.add (filler);
                        s.add (complementosPane);
                    }

                    else
                        s.add (new JLabel (" ninguno"));

                    valoracion.setText (
                            "Valoración personal: " + (Double.isNaN (historial [current [0]].getValoracion ()) ? "-"
                                    : String.format ("%.1f", historial [current [0]].getValoracion ())));

                    q.repaint ();
                };

                JButton b1 = new JButton (
                        new ImageIcon (getClass ().getResource ("/toolbarButtonGraphics/navigation/Back24.gif")));

                JButton b2 = new JButton (
                        new ImageIcon (getClass ().getResource ("/toolbarButtonGraphics/navigation/Forward24.gif")));

                q.add (((Supplier <JPanel>) ( () -> {
                    r.add (pelicula);
                    r.add (asiento);
                    r.add (fecha);
                    r.add (duracion);
                    r.add (s);
                    r.add (valoracion);

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 10)));
                q.add (new JSeparator (SwingConstants.HORIZONTAL));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel t = new JPanel (new FlowLayout ());

                    t.add (((Supplier <JButton>) ( () -> {
                        b1.setEnabled (false);
                        b1.addActionListener (e -> {
                            if (--current [0] == 0)
                                b1.setEnabled (false);

                            b2.setEnabled (historial.length != 1);

                            a.run ();
                        });

                        return b1;
                    })).get ());

                    t.add (((Supplier <JButton>) ( () -> {
                        b2.setEnabled (historial.length != 1);

                        b2.addActionListener (e -> {
                            if (++current [0] == historial.length - 1)
                                b2.setEnabled (false);

                            b1.setEnabled (historial.length != 1);

                            a.run ();
                        });

                        return b2;
                    })).get ());

                    return t;
                })).get ());

                a.run ();

                return q;
            })).get ());

            return p;
        })).get (), BorderLayout.CENTER);

        this.add (((Supplier <JPanel>) ( () ->

        {
            JPanel p = new JPanel ();
            p.setAlignmentX (Component.CENTER_ALIGNMENT);

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));
                q.setAlignmentX (Component.CENTER_ALIGNMENT);

                q.add (((Supplier <JLabel>) ( () -> {
                    JLabel l = new JLabel ("Valorar una película");
                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));
                    l.setAlignmentX (Component.CENTER_ALIGNMENT);

                    return l;
                })).get (), BorderLayout.PAGE_START);

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout ());
                    r.setAlignmentX (Component.CENTER_ALIGNMENT);

                    JButton b = new JButton ("Valorar");

                    JList <Pelicula> list = new JList <Pelicula> (
                            espectador.getHistorial ().isEmpty () ? new Vector <Pelicula> ()
                                    : espectador.getHistorial ().stream ().map (e -> e.getPelicula ())
                                            .collect (Collectors.toCollection (Vector::new)));
                    list.setCellRenderer (new PeliculasListRenderer ());

                    list.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
                    list.addListSelectionListener (e -> b.setEnabled (list.getSelectedValue () != null));

                    JSpinner s = new JSpinner (new SpinnerNumberModel (5, 1, 10, 0.1));
                    s.setToolTipText ("La valoración de la película (un número del 1 al 10).");

                    r.add (list, BorderLayout.LINE_START);

                    r.add (s, BorderLayout.LINE_END);

                    r.add (((Supplier <JButton>) ( () -> {
                        b.setEnabled (false);

                        b.addActionListener (e -> {
                            if (list.getSelectedValue () == null)
                                return;

                            List <Entrada> le = ((Supplier <Map <Pelicula, List <Entrada>>>) ( () -> {
                                Map <Pelicula, List <Entrada>> map = new HashMap <Pelicula, List <Entrada>> ();

                                for (int i[] = new int [1]; i [0] < historial.length; i [0]++)
                                    map.putIfAbsent (historial [i [0]].getPelicula (),
                                            Arrays.asList (historial).stream ()
                                                    .filter (
                                                            x -> x.getPelicula ()
                                                                    .equals (historial [i [0]].getPelicula ()))
                                                    .toList ());

                                return map;
                            })).get ().get (list.getSelectedValue ());
                            for (int i = 0; i < le.size (); le.get (i++).setValoracion ((Double) s.getValue ()))
                                ;

                            valoracion.setText (
                                    "Valoración personal: " + (Double.isNaN (historial [current [0]].getValoracion ())
                                            ? "-"
                                            : String.format ("%.1f", historial [current [0]].getValoracion ())));

                            valoracion.repaint ();

                            list.setSelectedValue (null, false);
                        });

                        return b;
                    })).get ());

                    return r;
                })).get ());

                return q;
            })).get ());

            return p;
        })).get (), BorderLayout.LINE_END);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Mi historial");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("OptionPane.informationIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
