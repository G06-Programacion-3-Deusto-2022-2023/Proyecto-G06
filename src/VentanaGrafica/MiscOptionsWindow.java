package VentanaGrafica;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.json.JSONException;

import cine.Administrador;
import cine.Complemento;
import cine.Consumible;
import internals.GestorBD;
import internals.Settings;
import internals.swing.ComplementosTableModel;
import internals.swing.JSONChooser;
import internals.swing.JTextFieldLimit;
import internals.swing.PNGChooser;

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

        if (admin != null && db.getAdministradores ().contains (admin))
            this.add (((Supplier <JPanel>) ( () -> {
                JPanel p = new JPanel ();
                p.setLayout (new BoxLayout (p, BoxLayout.Y_AXIS));

                p.add (((Supplier <JLabel>) ( () -> {
                    JLabel l = new JLabel (admin.getNombre ());
                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                    return l;
                })).get ());

                p.add (Box.createRigidArea (new Dimension (0, 15)));

                return p;
            })).get (), BorderLayout.PAGE_START);

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));

            p.add (Box.createRigidArea (new Dimension (15, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                JTextField t[] = new JTextField [] {
                        new JTextField (new JTextFieldLimit (30), Settings.getNombre (), 25),
                        new JTextField (new File (Settings.getLogo ()).getAbsolutePath (), 50)
                };

                JSpinner sp = new JSpinner (new SpinnerNumberModel ());

                JComboBox <String> cb = new JComboBox <String> (
                        Arrays.asList ("Asientos", "Tabla").stream ().collect (Collectors.toCollection (Vector::new)));

                JButton b[] = new JButton [] {
                        new JButton (new ImageIcon (
                                MiscOptionsWindow.class.getResource ("/toolbarButtonGraphics/general/Save24.gif"))),
                        new JButton (new ImageIcon (
                                MiscOptionsWindow.class.getResource ("/toolbarButtonGraphics/general/Save24.gif"))),
                        new JButton (new ImageIcon (
                                MiscOptionsWindow.class.getResource ("/toolbarButtonGraphics/general/Save24.gif"))),
                        new JButton (new ImageIcon (
                                MiscOptionsWindow.class.getResource ("/toolbarButtonGraphics/general/Save24.gif"))),
                        new JButton (UIManager.getIcon ("FileView.fileIcon", new Locale ("es-ES"))),
                        new JButton ("Por defecto")
                };

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.LEADING, 1, 0));

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Nombre del cine:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 14f));

                        return l;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (2, 0)));

                    r.add (((Supplier <JTextField>) ( () -> {
                        t [0].getDocument ().addDocumentListener (new DocumentListener () {
                            @Override
                            public void insertUpdate (DocumentEvent e) {
                                this.changedUpdate (e);
                            }

                            @Override
                            public void removeUpdate (DocumentEvent e) {
                                this.changedUpdate (e);
                            }

                            @Override
                            public void changedUpdate (DocumentEvent e) {
                                try {
                                    b [0].setEnabled (t [0].getText ().strip ().length () != 0
                                            && !t [0].getText ().equals (Settings.getNombre ()));

                                    b [5].setEnabled (!(t [0].getText ()
                                            .equals (Settings.defaults ().getProperty ("nombre"))
                                            && new File (t [1].getText ()).getCanonicalPath ()
                                                    .equals (new File (Settings.defaults ().getProperty ("logo"))
                                                            .getCanonicalPath ())
                                            && new BigDecimal (String.format ("%.2f",
                                                    ((SpinnerNumberModel) sp.getModel ()).getNumber ().doubleValue ())
                                                    .replace (",", "."))
                                                            .setScale (2, RoundingMode.HALF_EVEN)
                                                            .equals (new BigDecimal (
                                                                    Settings.defaults ()
                                                                            .getProperty ("precioentrada")).setScale (2,
                                                                                    RoundingMode.HALF_EVEN))
                                            && !Boolean.parseBoolean (
                                                    Settings.defaults ().getProperty ("fallbackseatrenderer"))
                                                            ? cb.getSelectedIndex () == 0
                                                            : cb.getSelectedIndex () == 1));
                                }

                                catch (IOException e1) {
                                    e1.printStackTrace ();
                                }
                            }
                        });

                        return t [0];
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (13, 0)));

                    r.add (((Supplier <JButton>) ( () -> {
                        b [0].setEnabled (false);

                        b [0].addActionListener (e -> {
                            Settings.setNombre (t [0].getText ().strip ());
                            Settings.save ();

                            b [0].setEnabled (false);
                        });

                        return b [0];
                    })).get ());

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.LEADING, 1, 0));

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Logo del cine:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 14f));

                        return l;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (2, 0)));

                    r.add (((Supplier <JTextField>) ( () -> {
                        t [1].getDocument ().addDocumentListener (new DocumentListener () {
                            @Override
                            public void insertUpdate (DocumentEvent e) {
                                this.changedUpdate (e);
                            }

                            @Override
                            public void removeUpdate (DocumentEvent e) {
                                this.changedUpdate (e);
                            }

                            @Override
                            public void changedUpdate (DocumentEvent e) {
                                try {
                                    b [1].setEnabled (!new File (t [1].getText ()).exists () ||
                                            !new File (t [1].getText ()).getCanonicalFile ()
                                                    .equals (new File (Settings.getLogo ()).getCanonicalFile ()));

                                    b [5].setEnabled (!(t [0].getText ()
                                            .equals (Settings.defaults ().getProperty ("nombre"))
                                            && new File (t [1].getText ()).getCanonicalPath ()
                                                    .equals (new File (Settings.defaults ().getProperty ("logo"))
                                                            .getCanonicalPath ())
                                            && new BigDecimal (String.format ("%.2f",
                                                    ((SpinnerNumberModel) sp.getModel ()).getNumber ().doubleValue ())
                                                    .replace (",", "."))
                                                            .setScale (2, RoundingMode.HALF_EVEN)
                                                            .equals (new BigDecimal (
                                                                    Settings.defaults ()
                                                                            .getProperty ("precioentrada")).setScale (2,
                                                                                    RoundingMode.HALF_EVEN))
                                            && !Boolean.parseBoolean (
                                                    Settings.defaults ().getProperty ("fallbackseatrenderer"))
                                                            ? cb.getSelectedIndex () == 0
                                                            : cb.getSelectedIndex () == 1));
                                }

                                catch (IOException e1) {
                                    e1.printStackTrace ();
                                }
                            }
                        });

                        return t [1];
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (3, 0)));

                    r.add (((Supplier <JButton>) ( () -> {
                        b [4].addActionListener (e -> {
                            PNGChooser ic;
                            if ((ic = new PNGChooser ()).showOpenDialog (f) != JFileChooser.APPROVE_OPTION)
                                return;

                            t [1].setText (ic.getSelectedFile ().getAbsolutePath ());
                        });

                        return b [4];
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (13, 0)));

                    r.add (((Supplier <JButton>) ( () -> {
                        b [1].setEnabled (false);

                        b [1].addActionListener (e -> {
                            try {
                                Settings.setLogo (t [1].getText ());
                            }

                            catch (IllegalArgumentException ex) {
                                JOptionPane.showMessageDialog (f, "El archivo especificado no es un PNG válido.",
                                        "Error al establecer el logo.", JOptionPane.ERROR_MESSAGE);

                                return;
                            }

                            Settings.save ();

                            b [1].setEnabled (false);
                        });

                        return b [1];
                    })).get ());

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.LEADING, 1, 0));

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Precio de las entradas:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 14f));

                        return l;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (2, 0)));

                    r.add (((Supplier <JSpinner>) ( () -> {
                        ((SpinnerNumberModel) sp.getModel ()).setMinimum (0.01);
                        ((SpinnerNumberModel) sp.getModel ()).setValue (Settings.getPrecioEntrada ().doubleValue ());
                        ((SpinnerNumberModel) sp.getModel ()).setStepSize (0.1);
                        sp.setEditor (new JSpinner.NumberEditor (sp));

                        sp.addChangeListener (e -> {
                            BigDecimal n;
                            b [2].setEnabled ((n = new BigDecimal (String.format ("%.2f",
                                    ((SpinnerNumberModel) sp.getModel ()).getNumber ().doubleValue ())
                                    .replace (",", "."))).signum () == 1
                                    && n.compareTo (Consumible.getMaxPrecio ()) <= 0
                                    && !n.equals (Settings.getPrecioEntrada ()));

                            try {
                                b [5].setEnabled (!(t [0].getText ()
                                        .equals (Settings.defaults ().getProperty ("nombre"))
                                        && new File (t [1].getText ()).getCanonicalPath ()
                                                .equals (new File (Settings.defaults ().getProperty ("logo"))
                                                        .getCanonicalPath ())
                                        && new BigDecimal (String.format ("%.2f",
                                                ((SpinnerNumberModel) sp.getModel ()).getNumber ().doubleValue ())
                                                .replace (",", "."))
                                                        .setScale (2, RoundingMode.HALF_EVEN)
                                                        .equals (new BigDecimal (
                                                                Settings.defaults ()
                                                                        .getProperty ("precioentrada")).setScale (2,
                                                                                RoundingMode.HALF_EVEN))
                                        && !Boolean.parseBoolean (
                                                Settings.defaults ().getProperty ("fallbackseatrenderer"))
                                                        ? cb.getSelectedIndex () == 0
                                                        : cb.getSelectedIndex () == 1));
                            }

                            catch (IOException e1) {
                                e1.printStackTrace ();
                            }
                        });

                        return sp;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (1, 0)));

                    r.add (new JLabel ("€"));

                    r.add (Box.createRigidArea (new Dimension (13, 0)));

                    r.add (((Supplier <JButton>) ( () -> {
                        b [2].setEnabled (false);

                        b [2].addActionListener (e -> {
                            try {
                                Settings.setPrecioEntrada ();
                            }

                            catch (IllegalArgumentException ex) {
                                JOptionPane.showMessageDialog (f,
                                        "El precio de las entradas debe ser un número positivo.",
                                        "Error al establecer el precio de las entradas", JOptionPane.ERROR_MESSAGE);
                            }
                            Settings.save ();

                            b [2].setEnabled (false);
                        });

                        return b [2];
                    })).get ());

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout (FlowLayout.LEADING, 1, 0));

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Precio de las entradas:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 14f));

                        return l;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (2, 0)));

                    r.add (((Supplier <JComboBox <String>>) ( () -> {
                        cb.addActionListener (e -> {
                            b [3].setEnabled (Settings.usingFallbackRenderer () ? cb.getSelectedIndex () == 0
                                    : cb.getSelectedIndex () == 1);

                            try {
                                b [5].setEnabled (!(t [0].getText ()
                                        .equals (Settings.defaults ().getProperty ("nombre"))
                                        && new File (t [1].getText ()).getCanonicalPath ()
                                                .equals (new File (Settings.defaults ().getProperty ("logo"))
                                                        .getCanonicalPath ())
                                        && new BigDecimal (String.format ("%.2f",
                                                ((SpinnerNumberModel) sp.getModel ()).getNumber ().doubleValue ())
                                                .replace (",", "."))
                                                        .setScale (2, RoundingMode.HALF_EVEN)
                                                        .equals (new BigDecimal (
                                                                Settings.defaults ()
                                                                        .getProperty ("precioentrada")).setScale (2,
                                                                                RoundingMode.HALF_EVEN))
                                        && !Boolean.parseBoolean (
                                                Settings.defaults ().getProperty ("fallbackseatrenderer"))
                                                        ? cb.getSelectedIndex () == 0
                                                        : cb.getSelectedIndex () == 1));
                            }

                            catch (IOException e1) {
                                e1.printStackTrace ();
                            }
                        });

                        return cb;
                    })).get ());

                    r.add (Box.createRigidArea (new Dimension (13, 0)));

                    r.add (((Supplier <JButton>) ( () -> {
                        b [3].setEnabled (false);

                        b [3].addActionListener (e -> {
                            Settings.useFallbackRenderer (cb.getSelectedIndex () == 1);
                            Settings.save ();

                            b [3].setEnabled (false);
                        });

                        return b [3];
                    })).get ());

                    return r;
                })).get ());

                q.add (((Supplier <JButton>) ( () -> {
                    try {
                        b [5].setEnabled (!(t [0].getText ()
                                .equals (Settings.defaults ().getProperty ("nombre"))
                                && new File (t [1].getText ()).getCanonicalPath ()
                                        .equals (new File (Settings.defaults ().getProperty ("logo"))
                                                .getCanonicalPath ())
                                && new BigDecimal (String.format ("%.2f",
                                        ((SpinnerNumberModel) sp.getModel ()).getNumber ().doubleValue ())
                                        .replace (",", "."))
                                                .setScale (2, RoundingMode.HALF_EVEN)
                                                .equals (new BigDecimal (
                                                        Settings.defaults ()
                                                                .getProperty ("precioentrada")).setScale (2,
                                                                        RoundingMode.HALF_EVEN))
                                && !Boolean.parseBoolean (
                                        Settings.defaults ().getProperty ("fallbackseatrenderer"))
                                                ? cb.getSelectedIndex () == 0
                                                : cb.getSelectedIndex () == 1));
                    }

                    catch (IOException e1) {
                        e1.printStackTrace ();
                    }

                    b [5].addActionListener (e -> {
                        Settings.setNombre ();
                        Settings.setLogo ();
                        Settings.setPrecioEntrada ();
                        Settings.useFallbackRenderer (
                                Boolean.parseBoolean (Settings.defaults ().getProperty ("fallbackseatrenderer")));
                        Settings.save ();

                        t [0].setText (Settings.getNombre ());
                        t [1].setText (new File (Settings.getLogo ()).getAbsolutePath ());
                        ((SpinnerNumberModel) sp.getModel ()).setValue (Settings.getPrecioEntrada ());
                        cb.setSelectedIndex (Settings.usingFallbackRenderer () ? 1 : 0);

                        b [0].setEnabled (false);
                        b [1].setEnabled (false);
                        b [2].setEnabled (false);
                        b [5].setEnabled (false);
                    });

                    return b [5];
                })).get ());

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (15, 0)));

            p.add (new JSeparator (SwingConstants.VERTICAL));

            p.add (Box.createRigidArea (new Dimension (15, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setAlignmentX (Component.CENTER_ALIGNMENT);
                q.setAlignmentY (Component.CENTER_ALIGNMENT);
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                q.add ((((Supplier <JLabel>) ( () -> {
                    JLabel l = new JLabel ("Día del espectador");
                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 20f));

                    return l;
                }))).get ());

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));
                    r.setAlignmentX (Component.CENTER_ALIGNMENT);
                    r.setAlignmentY (Component.CENTER_ALIGNMENT);

                    JComboBox <String> cb = new JComboBox <String> (
                            new Vector <String> (
                                    Arrays.asList (new String [] { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes",
                                            "Sábado", "Domingo" })));

                    JSpinner sp = new JSpinner (new SpinnerNumberModel (Settings.getDescuentoEspectador (), 0, 99, 1));

                    JButton b[] = new JButton [] {
                            new JButton (new ImageIcon (
                                    MiscOptionsWindow.class.getResource ("/toolbarButtonGraphics/general/Save24.gif"))),
                            new JButton (new ImageIcon (
                                    MiscOptionsWindow.class.getResource ("/toolbarButtonGraphics/general/Save24.gif"))),
                            new JButton ("Por defecto")
                    };

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new FlowLayout (FlowLayout.LEADING, 15, 0));

                        s.add (((Supplier <JComboBox <String>>) ( () -> {
                            cb.setSelectedIndex (Settings.getDiaEspectador ());

                            cb.addActionListener (e -> {
                                b [0].setEnabled (cb.getSelectedIndex () != Settings.getDiaEspectador ());

                                b [2].setEnabled (!(cb.getSelectedIndex () == Integer
                                        .parseInt (Settings.defaults ().getProperty ("diaespectador"))
                                        && ((SpinnerNumberModel) sp.getModel ()).getNumber ().intValue () == Integer
                                                .parseInt (Settings
                                                        .defaults ().getProperty ("descuentoespectador"))));
                            });

                            return cb;
                        })).get ());

                        s.add (((Supplier <JButton>) ( () -> {
                            b [0].setEnabled (false);

                            b [0].addActionListener (e -> {
                                Settings.setDiaEspectador (cb.getSelectedIndex ());
                                Settings.save ();

                                b [0].setEnabled (false);
                            });

                            return b [0];
                        })).get ());

                        return s;
                    })).get ());

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new FlowLayout (FlowLayout.LEADING, 1, 0));

                        s.add (((Supplier <JSpinner>) ( () -> {
                            sp.addChangeListener (e -> {
                                int n;
                                b [1].setEnabled (
                                        !((n = ((SpinnerNumberModel) sp.getModel ()).getNumber ().intValue ()) < 0
                                                || n >= 100 || n == Settings.getDescuentoEspectador ()));

                                b [2].setEnabled (!(cb.getSelectedIndex () == Integer
                                        .parseInt (Settings.defaults ().getProperty ("diaespectador"))
                                        && ((SpinnerNumberModel) sp.getModel ()).getNumber ().intValue () == Integer
                                                .parseInt (Settings
                                                        .defaults ().getProperty ("descuentoespectador"))));
                            });

                            return sp;
                        })).get ());

                        s.add (new JLabel ("%"));

                        s.add (Box.createRigidArea (new Dimension (12, 0)));

                        s.add (((Supplier <JButton>) ( () -> {
                            b [1].setEnabled (false);

                            b [1].addActionListener (e -> {
                                try {
                                    Settings.setDescuentoEspectador (
                                            (((SpinnerNumberModel) sp.getModel ()).getNumber ().intValue ()));
                                }

                                catch (IllegalArgumentException ex) {
                                    JOptionPane.showMessageDialog (f,
                                            "El descuento del día del espectador debe ser un número entero en el intervalo [0, 100) que represente un porcentaje",
                                            "Error al establecer el descuento del día de espectador.",
                                            JOptionPane.ERROR_MESSAGE);

                                    return;
                                }

                                Settings.save ();

                                b [1].setEnabled (false);
                            });

                            return b [1];
                        })).get ());

                        return s;
                    })).get ());

                    r.add (((Supplier <JButton>) ( () -> {
                        b [2].setEnabled (!(Settings.getDiaEspectador () == Integer
                                .parseInt (Settings.defaults ().getProperty ("diaespectador"))
                                && Settings.getDescuentoEspectador () == Integer
                                        .parseInt (Settings.defaults ().getProperty ("descuentoespectador"))));

                        b [2].addActionListener (e -> {
                            cb.setSelectedIndex (Integer.parseInt (Settings.defaults ().getProperty ("diaespectador")));
                            ((SpinnerNumberModel) sp.getModel ())
                                    .setValue (Integer
                                            .parseInt (Settings.defaults ().getProperty ("descuentoespectador")));

                            Settings.setDiaEspectador ();
                            Settings.setDescuentoEspectador ();
                            Settings.save ();

                            b [0].setEnabled (false);
                            b [1].setEnabled (false);
                            b [2].setEnabled (false);
                        });

                        return b [2];
                    })).get ());

                    return r;
                })).get ());

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (15, 0)));

            p.add (new JSeparator (SwingConstants.VERTICAL));

            p.add (Box.createRigidArea (new Dimension (15, 0)));

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

                        t.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
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
                                    String y) -> new BigDecimal (x.strip ().replace ("€", ""))
                                            .compareTo (new BigDecimal (y.strip ().replace ("€", "")))));
                            rs.setComparator (2,
                                    (Comparator <String>) ( (String x, String y) -> Integer
                                            .valueOf (x.strip ().replace ("%", ""))
                                            .compareTo (Integer.valueOf (y.strip ().replace ("%", "")))));

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

                                    if (fields [0].getText ().strip ().equals ("")) {
                                        JOptionPane.showMessageDialog (f,
                                                "El complemento debe tener un nombre.",
                                                "Error en la creación de complemento",
                                                JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    if (db.getComplementos ().stream ()
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

                                    BigDecimal bd;
                                    try {
                                        bd = new BigDecimal (fields [1].getText ().replace (",", "."));
                                    }

                                    catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog (f,
                                                "El precio del complemento no está en un formato válido.",
                                                "Error en la creación de complemento",
                                                JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    if (bd.signum () != 1) {
                                        JOptionPane.showMessageDialog (f,
                                                "El precio del complemento debe ser positivo.",
                                                "Error en la creación de complemento", JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    if (bd.compareTo (Consumible.getMaxPrecio ()) > 0) {
                                        JOptionPane.showMessageDialog (f,
                                                String.format (
                                                        "El precio del complemento excede el precio máximo de %s €.",
                                                        Consumible.getMaxPrecio ().toPlainString ()),
                                                "Error en la creación de complemento", JOptionPane.ERROR_MESSAGE);

                                        continue;
                                    }

                                    for (;;) {
                                        if (!fields [0].getText ().endsWith (" "))
                                            break;

                                        fields [0].setText (fields [0].getText ().substring (0,
                                                fields [0].getText ().length () - 2));
                                    }

                                    ((ComplementosTableModel) t.getModel ()).addRow (new String [] {
                                            fields [0].getText (),
                                            fields [1].getText (),
                                            fields [2].getText ()
                                    });

                                    break;
                                }

                                ((ComplementosTableModel) t.getModel ()).update ();
                            });

                            return b [0];
                        })).get ());

                        s.add (Box.createRigidArea (new Dimension (0, 15)));

                        s.add (((Supplier <JButton>) ( () -> {
                            b [1].setEnabled (false);

                            b [1].addActionListener (e -> {
                                for (int i = 0; i < t
                                        .getSelectedRows ().length; ((ComplementosTableModel) t.getModel ())
                                                .removeRow (t.getSelectedRows () [i++]))
                                    ;
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

                                l.removeAll (Complemento.getDefault ());
                                db.update (l);
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
                                    str = Complemento.toJSON (db.getComplementos (), true);
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

        if (admin != null && db.getAdministradores ().contains (admin))
            this.add (((Supplier <JLabel>) ( () ->

            {
                JLabel l = new JLabel (" ");
                l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                return l;
            })).get (), BorderLayout.PAGE_END);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Opciones");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileView.floppyDriveIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}