package graphical;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Random;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import cine.Complemento;
import cine.Consumible;
import cine.Entrada;
import cine.Sala;
import internals.Pair;
import internals.Triplet;
import internals.Utils;
import internals.swing.ChosenComplementosTableModel;

public class DatosAsistenciaWindow extends JFrame {
    public DatosAsistenciaWindow (Entrada entrada, Collection <Complemento> cs, EspectadorWindow w)
            throws NullPointerException, IllegalArgumentException {
        super ();

        if (entrada == null)
            throw new NullPointerException ("No se puede pasar una entrada nula a la ventana de datos de asistencia.");

        final DatosAsistenciaWindow f = this;

        final JTable ct[] = new JTable [1];

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowOpened (WindowEvent e) {
                if (ct [0] != null)
                    ((ChosenComplementosTableModel) ct [0].getModel ()).update ();
            }

            @Override
            public void windowClosed (WindowEvent e) {
                if (w != null)
                    w.setVisible (true);
            }
        });

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel (new GridBagLayout ());

                GridBagConstraints gbc = new GridBagConstraints ();
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.anchor = GridBagConstraints.NORTH;

                final JButton fb = new JButton ("Confirmar");

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.LEFT, 50, 0));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new GridBagLayout ());

                        s.add (((Supplier <JLabel>) ( () -> {
                            JLabel l = new JLabel ("Detalles de la entrada");
                            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                            return l;
                        })).get (), gbc);

                        gbc.insets = new Insets (15, 0, 0, 0);
                        gbc.anchor = GridBagConstraints.CENTER;

                        Pair <Integer, Integer> index = entrada.getSala () == null ? null
                                : entrada.getSala ().indexOf (entrada.getButaca ());
                        JLabel l[][] = new JLabel [] [] {
                                { new JLabel ("<html><b>Espectador:</b></html>"),
                                        new JLabel (entrada.getEspectador () == null ? "Invitado"
                                                : entrada.getEspectador ().getNombre ()) },
                                { new JLabel ("<html><b>Película:</b></html>"),
                                        new JLabel (entrada.getPelicula () == null ? "-"
                                                : entrada.getPelicula ().getNombre ()) },
                                { new JLabel ("<html><b>Fecha:</b></html>"),
                                        new JLabel (entrada.getFecha () == null ? "--/--/--"
                                                : ((Supplier <String>) ( () -> {
                                                    Triplet <Integer, Integer, Integer> date = Utils
                                                            .getDate (entrada.getFecha ());

                                                    return String.format ("%d/%02d/%d", date.x, date.y, date.z);
                                                })).get ()) },
                                { new JLabel ("<html><b>Sala:</b></html>"), new JLabel (((Supplier <String>) ( () -> {
                                    int i = Sala.indexOf (entrada.getSala ());

                                    return i == -1 ? "-" : String.format ("%d", i + 1);
                                })).get ()) },
                                { new JLabel ("<html><b>Fila:</b></html>"),
                                        new JLabel (index == null ? "-" : String.format ("%d", index.x + 1)) },
                                { new JLabel ("<html><b>Butaca:</b></html>"),
                                        new JLabel (index == null ? "-" : String.format ("%d", index.y + 1)) },
                                { new JLabel ("<html><b>Precio:</b></html>"),
                                        new JLabel (String.format ("%s €",
                                                entrada.getPrecio ().toPlainString ().replace (".", ","))) }
                        };

                        for (int i = 0; i < l.length; i++) {
                            l [i] [0].setFont (l [i] [0].getFont ().deriveFont (Font.PLAIN, 14f));
                            l [i] [1].setFont (l [i] [1].getFont ().deriveFont (Font.PLAIN, 14f));
                        }

                        int max = 0;
                        for (int i = 1; i < l.length; i++)
                            max = l [max] [0].getMinimumSize ().getWidth () < l [i] [0].getMinimumSize ().getWidth ()
                                    ? i
                                    : max;

                        for (int i = 0; i < l.length; i++) {
                            l [i] [0].setMinimumSize (l [max] [0].getMinimumSize ());
                            l [i] [0].setPreferredSize (l [max] [0].getPreferredSize ());
                            l [i] [0].setMaximumSize (l [max] [0].getMaximumSize ());
                        }

                        s.add (((Supplier <JPanel>) ( () -> {
                            JPanel t = new JPanel (new GridLayout (l.length, 1, 5, 0));

                            for (int i[] = new int [1]; i [0] < l.length; i [0]++)
                                t.add (((Supplier <JPanel>) ( () -> {
                                    JPanel u = new JPanel (new FlowLayout (FlowLayout.LEFT, 5, 0));

                                    u.add (l [i [0]] [0]);
                                    u.add (l [i [0]] [1]);

                                    return u;
                                })).get ());

                            return t;
                        })).get (), gbc);

                        return s;
                    })).get ());

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new GridBagLayout ());

                        gbc.insets = new Insets (0, 0, 0, 0);
                        gbc.anchor = GridBagConstraints.NORTH;

                        s.add (((Supplier <JLabel>) ( () -> {
                            JLabel l = new JLabel ("Complementos");
                            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                            return l;
                        })).get (), gbc);

                        gbc.insets = new Insets (15, 0, 0, 0);
                        gbc.anchor = GridBagConstraints.CENTER;

                        s.add (((Supplier <JScrollPane>) ( () -> {
                            (ct [0] = new JTable () {
                                @Override
                                public TableCellRenderer getCellRenderer (int row, int col) {
                                    return col == 0 ? new DefaultTableCellRenderer () {
                                        @Override
                                        public Component getTableCellRendererComponent (JTable table, Object value,
                                                boolean isSelected,
                                                boolean hasFocus, int row, int column) {
                                            if (value == null)
                                                return this;

                                            this.setBackground (isSelected ? table.getSelectionBackground ()
                                                    : table.getBackground ());
                                            this.setForeground (isSelected ? table.getSelectionForeground ()
                                                    : table.getForeground ());
                                            this.setText (((Complemento) value).getNombre ());
                                            this.setFont (this.getFont ().deriveFont (Font.PLAIN));

                                            return this;
                                        }
                                    } : super.getCellRenderer (row, col);
                                }

                                @Override
                                public boolean getRowSelectionAllowed () {
                                    return false;
                                }

                                @Override
                                public boolean getColumnSelectionAllowed () {
                                    return false;
                                }
                            }).setModel (new ChosenComplementosTableModel (entrada.getComplementos ()) {
                                @Override
                                public void update () {
                                    this.setComplementos (this.c);

                                    try {
                                        this.l.setText (
                                                String.format ("<html><b>Total: </b>%s €</html>",
                                                        entrada.total ().toPlainString ()));
                                        fb.setEnabled (true);
                                    }

                                    catch (ArithmeticException ex) {
                                        Logger.getLogger (DatosAsistenciaWindow.class.getName ()).log (Level.INFO,
                                                String.format (
                                                        "Se ha excedido el precio máximo de %s € por lo que no se podrá continuar.",
                                                        Consumible.getMaxPrecio ()));

                                        this.l.setText ("<html><b>Precio máximo excedido</b></html>");
                                        fb.setEnabled (false);
                                    }
                                }

                                @Override
                                public boolean isCellEditable (int row, int col) {
                                    return false;
                                }
                            });

                            ct [0].getTableHeader ().setReorderingAllowed (false);

                            return new JScrollPane (ct [0]);
                        })).get (), gbc);

                        gbc.anchor = GridBagConstraints.SOUTH;

                        s.add (((Supplier <JButton>) ( () -> {
                            JButton b = new JButton ("Modificar complementos");

                            b.addActionListener (e -> {
                                f.setVisible (false);
                                new ComplementosWindow (entrada.getComplementos (), cs, entrada, f);
                            });

                            return b;
                        })).get (), gbc);

                        return s;
                    })).get ());

                    return r;
                })).get (), gbc);

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new GridBagLayout ());

                    gbc.insets = new Insets (0, 0, 0, 0);
                    gbc.anchor = GridBagConstraints.NORTH;

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = ((ChosenComplementosTableModel) ct [0].getModel ()).getLabel ();
                        l.setFont (l.getFont ().deriveFont (Font.PLAIN, 15f));

                        return l;
                    })).get (), gbc);

                    gbc.insets = new Insets (15, 0, 0, 0);
                    gbc.anchor = GridBagConstraints.SOUTH;

                    r.add (((Supplier <JButton>) ( () -> {
                        fb.addActionListener (e -> {
                            new LoadingWindow (new Thread ( () -> {
                                try {
                                    Thread.sleep (new Random ().nextInt (1000, 2501));

                                    JOptionPane.showMessageDialog (f,
                                            "La transacción fue completada con éxito.\n¡Disfrute de su película!",
                                            "Pago finalizado", JOptionPane.INFORMATION_MESSAGE);

                                    if (entrada.getEspectador () != null)
                                        entrada.getEspectador ().getHistorial ().add (entrada);

                                    f.dispose ();
                                }

                                catch (InterruptedException e1) {
                                    e1.printStackTrace ();
                                }
                            }), "Procesando transacción...");
                        });

                        return fb;
                    })).get (), gbc);

                    gbc.insets = new Insets (25, 0, 25, 0);
                    gbc.anchor = GridBagConstraints.SOUTH;

                    return r;
                })).get (), gbc);

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            return p;
        })).get ());

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImage (new ImageIcon (this.getClass ()
                .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.setTitle ("Resumen de los datos de asistencia");
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
