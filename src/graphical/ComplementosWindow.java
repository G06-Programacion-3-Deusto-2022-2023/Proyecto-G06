package graphical;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.jdesktop.swingx.JXPanel;

import cine.Complemento;
import cine.Consumible;
import cine.Entrada;
import internals.GestorBD;
import internals.swing.ChosenComplementosTableModel;

public class ComplementosWindow extends JFrame {
    public ComplementosWindow (ConcurrentMap <Complemento, BigInteger> c, GestorBD db, Entrada entrada,
            JFrame w) {
        this (c, db.getComplementos (), entrada, w);
    }

    public ComplementosWindow (ConcurrentMap <Complemento, BigInteger> c, Collection <Complemento> cs,
            Entrada entrada, JFrame w)
            throws NullPointerException, IllegalArgumentException {
        super ();

        if (c == null)
            throw new NullPointerException (
                    "No se puede pasar un mapa de complementos y unidades nulo a la ventana de complementos.");

        if (cs == null)
            throw new NullPointerException (
                    "No se puede pasar una lista de complementos nula a la ventana de complementos.");

        if (w != null && !(w instanceof DatosAsistenciaWindow || w instanceof EspectadorWindow))
            throw new IllegalArgumentException (
                    "No se puede pasar una ventana no nula a la lista de complementos que no sea una instancia ni de DatosAsistenciaWindow ni de EspectadorWindow");

        final ComplementosWindow f = this;
        final ConcurrentMap <Complemento, BigInteger> ic = new ConcurrentHashMap <Complemento, BigInteger> (c);
        final boolean ric[] = new boolean [] { true };

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (w instanceof EspectadorWindow) {
                    if (ric [0]) {
                        w.setVisible (true);

                        return;
                    }

                    entrada.setComplementos (c);
                    new DatosAsistenciaWindow (entrada, cs, (EspectadorWindow) w);

                    return;
                }

                if (!ric [0])
                    return;

                c.clear ();
                c.putAll (ic);

                if (w != null)
                    w.setVisible (true);
            }
        });

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.Y_AXIS));

            List <JPanel> pl = new ArrayList <JPanel> ();
            List <Complemento> cl = new ArrayList <Complemento> ();
            JTable ct = new JTable () {
                @Override
                public TableCellRenderer getCellRenderer (int row, int col) {
                    return col == 0 ? new DefaultTableCellRenderer () {
                        @Override
                        public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected,
                                boolean hasFocus, int row, int column) {
                            if (value == null)
                                return this;

                            this.setBackground (isSelected ? table.getSelectionBackground () : table.getBackground ());
                            this.setForeground (isSelected ? table.getSelectionForeground () : table.getForeground ());
                            this.setText (((Complemento) value).getNombre ());
                            this.setFont (this.getFont ().deriveFont (Font.PLAIN));

                            return this;
                        }
                    } : super.getCellRenderer (row, col);
                }
            };
            Runnable clear = () -> {
                for (int i = 0; i < pl.size (); pl.get (i++).setBorder (BorderFactory.createEmptyBorder (1, 1, 1, 1)))
                    ;

                pl.clear ();
                cl.clear ();
            };
            JButton rb = new JButton ("Quitar");

            p.add (Box.createRigidArea (new Dimension (0, 25)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.X_AXIS));

                Complemento csa[] = cs.toArray (new Complemento [0]);

                q.add (Box.createRigidArea (new Dimension (25, 0)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    r.add (((Supplier <JXPanel>) ( () -> {
                        JXPanel s = new JXPanel (new GridLayout ((int) Math.sqrt (cs.size ()),
                                cs.size () / (int) Math.sqrt (cs.size ()), 10, 10));

                        for (int i[] = new int [1]; i [0] < csa.length; i [0]++)
                            s.add (((Supplier <JPanel>) ( () -> {
                                JPanel t = new JPanel (new FlowLayout (FlowLayout.LEFT, 10, 0));
                                t.setBorder (BorderFactory.createEmptyBorder (1, 1, 1, 1));

                                Complemento cc = csa [i [0]];

                                t.addMouseListener (new MouseAdapter () {
                                    @Override
                                    public void mouseClicked (MouseEvent e) {
                                        if (pl.contains (t)) {
                                            t.setBorder (BorderFactory.createEmptyBorder (1, 1, 1, 1));

                                            pl.remove (t);
                                            cl.remove (cc);

                                            return;
                                        }

                                        t.setBorder (BorderFactory.createLineBorder (Color.GRAY, 1));

                                        pl.add (t);
                                        cl.add (cc);
                                    }

                                    @Override
                                    public void mouseEntered (MouseEvent e) {
                                        t.setBorder (pl.contains (t)
                                                ? BorderFactory.createLineBorder (Color.BLACK, 1)
                                                : BorderFactory.createMatteBorder (1, 1, 1, 1, Color.GRAY));
                                    }

                                    @Override
                                    public void mouseExited (MouseEvent e) {
                                        t.setBorder (pl.contains (t)
                                                ? BorderFactory.createLineBorder (Color.GRAY, 1)
                                                : BorderFactory.createEmptyBorder (1, 1, 1, 1));
                                    }
                                });

                                t.add (((Supplier <JLabel>) ( () -> {
                                    JLabel l = new JLabel (cc.getNombre ());
                                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 14f));

                                    return l;
                                })).get ());

                                t.add (cc.getDescuento () == 0 ? new JLabel (String.format ("%s €",
                                        cc.getPrecio ().setScale (2, RoundingMode.HALF_EVEN)))
                                        : ((Supplier <JPanel>) ( () -> {
                                            JPanel u = new JPanel (new GridLayout (2, 1, 0, 3));

                                            u.add (((Supplier <JLabel>) ( () -> {
                                                JLabel l = new JLabel (String.format ("%s €",
                                                        cc.getPrecio ().setScale (2, RoundingMode.HALF_EVEN)
                                                                .toPlainString ()));
                                                l.setFont (((Supplier <Font>) ( () -> {
                                                    ConcurrentMap <TextAttribute, Object> fa = (ConcurrentMap <TextAttribute, Object>) l
                                                            .getFont ().getAttributes ();

                                                    fa.put (TextAttribute.STRIKETHROUGH,
                                                            TextAttribute.STRIKETHROUGH_ON);

                                                    return new Font (fa);
                                                })).get ());

                                                return l;
                                            })).get ());

                                            u.add (new JLabel (String.format ("%s €",
                                                    cc.aplicarDescuento ().setScale (2,
                                                            RoundingMode.HALF_EVEN))));

                                            return u;
                                        })).get ());

                                return t;
                            })).get ());

                        return s;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (0, 15)));

                    r.add (((Supplier <JButton>) ( () -> {
                        JButton b = new JButton ("Añadir");

                        b.addActionListener (e -> {
                            for (int i = 0; i < cl.size (); ((ChosenComplementosTableModel) ct.getModel ())
                                    .add (cl.get (i++)))
                                ;

                            clear.run ();

                        });

                        return b;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (15, 0)));
                q.add (new JSeparator (SwingConstants.VERTICAL));
                q.add (Box.createRigidArea (new Dimension (15, 0)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new FlowLayout (FlowLayout.LEFT, 25, 0));

                        s.add (((Supplier <JScrollPane>) ( () -> {
                            ct.setModel (new ChosenComplementosTableModel (c));

                            ct.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
                            ct.getSelectionModel ()
                                    .addListSelectionListener (e -> rb.setEnabled (ct.getSelectedRow () != -1));

                            ct.getColumnModel ().getColumn (1)
                                    .setCellEditor (new DefaultCellEditor (((Supplier <JTextField>) ( () -> {
                                        JTextField t = new JTextField ("");

                                        ((AbstractDocument) t.getDocument ()).setDocumentFilter (new DocumentFilter () {
                                            @Override
                                            public void insertString (DocumentFilter.FilterBypass fb,
                                                    int offset,
                                                    String text, AttributeSet attr)
                                                    throws BadLocationException {
                                                StringBuilder buffer = new StringBuilder (text.length ());

                                                char ch;
                                                for (int i = text.length () - 1; i >= 0;)
                                                    if ((ch = text.charAt (i--)) >= '0' && ch <= '9')
                                                        buffer.append (ch);

                                                super.insertString (fb, offset, buffer.toString (), attr);
                                            }

                                            @Override
                                            public void replace (DocumentFilter.FilterBypass fb,
                                                    int offset, int length, String string, AttributeSet attr)
                                                    throws BadLocationException {
                                                if (length > 0)
                                                    fb.remove (offset, length);

                                                this.insertString (fb, offset, string, attr);
                                            }
                                        });

                                        return t;
                                    })).get ()));

                            ct.getTableHeader ().setReorderingAllowed (false);

                            return new JScrollPane (ct);
                        })).get ());

                        s.add (((Supplier <JButton>) ( () -> {
                            rb.setEnabled (false);
                            rb.addActionListener (e -> {
                                ((ChosenComplementosTableModel) ct.getModel ())
                                        .removeRow (ct.getSelectedRow ());
                            });

                            return rb;
                        })).get ());

                        return s;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (0, 25)));

                    r.add (((ChosenComplementosTableModel) ct.getModel ()).getLabel ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (25, 0)));

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (0, 25)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel (new FlowLayout (FlowLayout.CENTER, 15, 0));

                q.add (((Supplier <JButton>) ( () -> {
                    JButton b = new JButton ("Confirmar");

                    b.addActionListener (e -> {
                        if (Complemento.sum (c).compareTo (Consumible.getMaxPrecio ()) > 0) {
                            JOptionPane.showMessageDialog (f,
                                    String.format ("Los complementos seleccionados exceden el precio máximo de %s €.",
                                            Consumible.getMaxPrecio ()),
                                    "Error al seleccionar los complementos", JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        ric [0] = false;
                        f.dispose ();
                    });

                    return b;
                })).get ());

                q.add (((Supplier <JButton>) ( () -> {
                    JButton b = new JButton ("Limpiar selección");

                    b.addActionListener (e -> {
                        clear.run ();
                        ((ChosenComplementosTableModel) ct.getModel ()).clear ();
                    });

                    return b;
                })).get ());

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (0, 25)));

            return p;
        })).get ());

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImage (((ImageIcon) UIManager.getIcon ("FileView.hardDriveIcon", new Locale ("es-ES"))).getImage ()
                .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.setTitle ("Seleccionar complementos");
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
