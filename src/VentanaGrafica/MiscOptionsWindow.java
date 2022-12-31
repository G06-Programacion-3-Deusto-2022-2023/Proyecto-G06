package VentanaGrafica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.json.JSONException;

import cine.Administrador;
import cine.Complemento;
import internals.GestorBD;
import internals.swing.ComplementosTableModel;
import internals.swing.JSONChooser;
import internals.swing.JTextFieldLimit;

public class MiscOptionsWindow extends JFrame {
    public MiscOptionsWindow (GestorBD db) {
        this (db, null, null);
    }

    public MiscOptionsWindow (GestorBD db, Administrador admin) {
        this (db, admin, null);
    }

    public MiscOptionsWindow (GestorBD db, Administrador admin, AdministradorWindow w) throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException ("No se puede pasar una base de datos nula a la ventana de opciones.");

        MiscOptionsWindow f = this;

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (w == null)
                    return;

                w.setVisible (true);
            }
        });

        if (admin != null && db.obtenerDatosAdministradores ().contains (admin))
            this.add (((Supplier <JLabel>) ( () -> {
                JLabel l = new JLabel (admin.getNombre ());
                l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                return l;
            })).get (), BorderLayout.PAGE_START);

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));

            p.add (Box.createRigidArea (new Dimension (15, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                return q;
            })).get ());

            p.add (new JSeparator (SwingConstants.VERTICAL));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                return q;
            })).get ());

            p.add (new JSeparator (SwingConstants.VERTICAL));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                q.add ((((Supplier <JLabel>) ( () -> {
                    JLabel l = new JLabel ("Complementos");
                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 20f));

                    return l;
                }))).get ());

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                JTable t = new JTable ();

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.CENTER, 25, 0));

                    JButton b[] = new JButton [] { new JButton ("Crear"), new JButton ("Eliminar"),
                            new JButton ("Importar"), new JButton ("Exportar") };

                    r.add (((Supplier <JScrollPane>) ( () -> {
                        JScrollPane s = new JScrollPane (t);

                        t.setModel (new ComplementosTableModel (db));

                        t.getSelectionModel ()
                                .addListSelectionListener (e -> b [1].setEnabled ((((BooleanSupplier) ( () -> {
                                    int i = t.getSelectionModel ().getMinSelectionIndex ();

                                    return !(i == -1 || ((ComplementosTableModel) t.getModel ()).get (i).isDefault ());
                                })).getAsBoolean ())));

                        t.setRowSorter ((((Supplier <TableRowSorter>) ( () -> {
                            TableRowSorter rs = new TableRowSorter <ComplementosTableModel> (
                                    (ComplementosTableModel) t.getModel ()) {
                                @Override
                                public void toggleSortOrder (int column) {
                                    super.toggleSortOrder (column);

                                    ((ComplementosTableModel) this.getModel ()).orderBy (
                                            ComplementosTableModel.OrderCriteria.values () [column + 1],
                                            !((ComplementosTableModel) this.getModel ()).getOrders () [column]);
                                }
                            };

                            rs.setComparator (0, (Comparator <String>) ( (String x, String y) -> x.compareTo (y)));
                            rs.setComparator (1, (Comparator <String>) ( (String x,
                                    String y) -> new BigDecimal (x.replace (" ", "").replace ("€", ""))
                                            .compareTo (new BigDecimal (y.replace (" ", "").replace ("€", "")))));
                            rs.setComparator (2,
                                    (Comparator <String>) ( (String x, String y) -> Integer
                                            .valueOf (x.replace (" ", "").replace ("%", ""))
                                            .compareTo (Integer.valueOf (y.replace (" ", "").replace ("%", "")))));

                            return rs;
                        })).get ()));

                        t.getColumnModel ().getColumn (0).setCellEditor (
                                new DefaultCellEditor (new JTextField (new JTextFieldLimit (50), "", 32)));

                        t.getColumnModel ().getColumn (1)
                                .setCellEditor (new DefaultCellEditor (((Supplier <JTextField>) ( () -> {
                                    JTextField tf = new JTextField ("");

                                    ((AbstractDocument) tf.getDocument ())
                                            .setDocumentFilter (new DocumentFilter () {
                                                @Override
                                                public void insertString (DocumentFilter.FilterBypass fb, int offset,
                                                        String text, AttributeSet attr)
                                                        throws BadLocationException {
                                                    StringBuilder buffer = new StringBuilder (text.length ());

                                                    char ch;
                                                    for (int i = text.length () - 1; i >= 0;)
                                                        if (((ch = text.charAt (i--)) >= '0' && ch <= '9') || ch == ','
                                                                || ch == '.')
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

                                    return tf;
                                })).get ()));

                        t.getColumnModel ().getColumn (1)
                                .setCellEditor (new DefaultCellEditor (((Supplier <JTextField>) ( () -> {
                                    JTextField tf = new JTextField ("");

                                    ((AbstractDocument) tf.getDocument ())
                                            .setDocumentFilter (new DocumentFilter () {
                                                @Override
                                                public void insertString (DocumentFilter.FilterBypass fb, int offset,
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

                                    return tf;
                                })).get ()));

                        return s;
                    })).get ());

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel ();
                        s.setLayout (new BoxLayout (s, BoxLayout.Y_AXIS));

                        s.add (((Supplier <JButton>) ( () -> {
                            b [0].addActionListener (e -> {
                                JTextField fields[] = new JTextField [] {
                                        new JTextField (new JTextFieldLimit (50), "", 32),
                                        new JTextField ("1,00"),
                                        new JTextField (new JTextFieldLimit (2), "0", 1)
                                };

                                fields [0].setToolTipText ("Nombre de complemento (obligatorio)");
                                fields [1].setToolTipText (String.format ("Precio (en euros, dejar vacío para %.2f €)",
                                        Complemento.getDefaultPrecio ().doubleValue ()));
                                fields [2].setToolTipText (
                                        "Descuento (porcentaje, dejar vacío para un descuento del 0 %)");

                                ((AbstractDocument) fields [1].getDocument ())
                                        .setDocumentFilter (new DocumentFilter () {
                                            @Override
                                            public void insertString (DocumentFilter.FilterBypass fb, int offset,
                                                    String text, AttributeSet attr)
                                                    throws BadLocationException {
                                                StringBuilder buffer = new StringBuilder (text.length ());

                                                char ch;
                                                for (int i = text.length () - 1; i >= 0;)
                                                    if (((ch = text.charAt (i--)) >= '0' && ch <= '9') || ch == ','
                                                            || ch == '.')
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

                                ((AbstractDocument) fields [2].getDocument ())
                                        .setDocumentFilter (new DocumentFilter () {
                                            @Override
                                            public void insertString (DocumentFilter.FilterBypass fb, int offset,
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

                                for (;;) {
                                    if (Arrays.asList (JOptionPane.CANCEL_OPTION, JOptionPane.CLOSED_OPTION)
                                            .contains (JOptionPane.showOptionDialog (f,
                                                    new Object [] {
                                                            "Introduce un nombre, un precio y un descuento.",
                                                            fields [0],
                                                            fields [1],
                                                            fields [2] },
                                                    "Crear complemento", JOptionPane.OK_CANCEL_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE,
                                                    null, null, null)))
                                        return;

                                    if (fields [0].getText ().equals ("")) {
                                        JOptionPane.showMessageDialog (f,
                                                "El complemento debe tener un nombre.",
                                                "Error en la creación de complemento",
                                                JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    if (db.obtenerDatosComplementos ().stream ()
                                            .anyMatch (x -> x.getNombre ().equals (fields [0].getText ()))) {
                                        JOptionPane.showMessageDialog (f,
                                                "Ya hay un complemento con el mismo nombre en la base de datos.",
                                                "Error en la creación de complemento",
                                                JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    if (!fields [1].getText ().equals ("") && (fields [1].getText ().charAt (0) == ','
                                            || fields [1].getText ().charAt (0) == '.'))
                                        fields [1].setText (String.format ("0%s", fields [1].getText ()));

                                    if (!fields [1].getText ().equals ("") && (fields [1].getText ()
                                            .charAt (fields [1].getText ().length () - 1) == ','
                                            || fields [1].getText ()
                                                    .charAt (fields [1].getText ().length () - 1) == '.'))
                                        fields [1].setText (String.format ("%s0", fields [1].getText ()));

                                    try {
                                        new BigDecimal (fields [1].getText ().replace (",", "."));
                                    }

                                    catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog (f,
                                                "El precio del complemento no está en un formato válido.",
                                                "Error en la creación de complemento",
                                                JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    ((ComplementosTableModel) t.getModel ()).addRow (new String [] {
                                            fields [0].getText (),
                                            fields [1].getText (),
                                            fields [2].getText ()
                                    });

                                    return;
                                }
                            });

                            return b [0];
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 15)));

                        s.add (((Supplier <JButton>) ( () -> {
                            b [1].setEnabled (false);

                            b [1].addActionListener (e -> {
                                for (int i = 0; i < t.getRowCount (); i++)
                                    if (t.isRowSelected (i))
                                        ((ComplementosTableModel) t.getModel ()).removeRow (i);
                            });

                            return b [1];
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 15)));

                        s.add (((Supplier <JButton>) ( () -> {
                            b [2].addActionListener (e -> {
                                JSONChooser fc;
                                if ((fc = new JSONChooser ()).showOpenDialog (f) != JFileChooser.APPROVE_OPTION)
                                    return;

                                List <Complemento> l;
                                try {
                                    l = Complemento.fromJSON (fc.getSelectedFile ());
                                }

                                catch (NullPointerException | IOException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "No pudo abrirse el archivo especificado.");

                                    return;
                                }

                                catch (JSONException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "El archivo especificado no es un archivo JSON válido.");

                                    return;
                                }

                                db.insert (l);
                                ((ComplementosTableModel) t.getModel ()).update ();
                            });

                            return b [2];
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 15)));

                        s.add (((Supplier <JButton>) ( () -> {
                            b [3].addActionListener (e -> {
                                JSONChooser fc;
                                if ((fc = new JSONChooser ()).showSaveDialog (f) != JFileChooser.APPROVE_OPTION)
                                    return;

                                String str;
                                try {
                                    str = Complemento.toJSON (db.obtenerDatosComplementos ());
                                }

                                catch (NullPointerException | JSONException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "Los complementos no pudieron ser exportados a JSON.");

                                    try {
                                        Files.delete (fc.getSelectedFile ().toPath ());
                                    }

                                    catch (IOException e1) {
                                        Logger.getLogger (MiscOptionsWindow.class.getName ()).log (Level.WARNING,
                                                String.format ("No se pudo eliminar el archivo %s.",
                                                        fc.getSelectedFile ().getAbsolutePath ()));
                                    }

                                    return;
                                }

                                try (BufferedWriter bw = new BufferedWriter (new FileWriter (fc.getSelectedFile ()))) {
                                    bw.write (str);
                                }

                                catch (IOException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "No pudo abrirse el archivo especificado.");

                                    try {
                                        Files.delete (fc.getSelectedFile ().toPath ());
                                    }

                                    catch (IOException e1) {
                                        Logger.getLogger (MiscOptionsWindow.class.getName ()).log (Level.WARNING,
                                                String.format ("No se pudo eliminar el archivo %s.",
                                                        fc.getSelectedFile ().getAbsolutePath ()));
                                    }

                                    return;
                                }
                            });

                            return b [3];
                        })).get ());

                        return s;
                    })).get ());

                    return r;
                })).get ());

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (15, 0)));

            return p;
        })).get (), BorderLayout.CENTER);

        if (admin != null && db.obtenerDatosAdministradores ().contains (admin))
            this.add (((Supplier <JLabel>) ( () ->

            {
                JLabel l = new JLabel (" ");
                l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                return l;
            })).get (), BorderLayout.PAGE_END);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Opciones");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileView.floppyDriveIcon", new Locale ("es-ES"))).getImage ());
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}