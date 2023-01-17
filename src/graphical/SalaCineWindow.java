package graphical;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTable;

import cine.Complemento;
import cine.Entrada;
import cine.Espectador;
import cine.Pelicula;
import cine.Sala;
import internals.GestorBD;
import internals.Settings;
import internals.Utils;
import internals.swing.ImageDisplayer;

public class SalaCineWindow extends JFrame {
    public SalaCineWindow (final GestorBD db, final Espectador espectador, final Pelicula pelicula) {
        this (db, espectador, pelicula, null);
    }

    public SalaCineWindow (final GestorBD db, final Espectador espectador, final Pelicula pelicula,
            final EspectadorWindow w)
            throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException ("El gestor de bases de datos no puede ser nulo.");

        if (pelicula == null)
            throw new NullPointerException ("La pelicula no puede ser nula.");

        final SalaCineWindow f = this;
        final EspectadorWindow pw[] = new EspectadorWindow [] { w };
        final boolean cont[] = new boolean [1];

        Sala sala = Sala.getSalas ().get (new Random ().nextInt (5));
        int selection[] = new int [] { -1 };

        new LoadingWindow ( () -> {
            f.addWindowListener (new WindowAdapter () {
                @Override
                public void windowClosed (WindowEvent e) {
                    if (pw [0] != null && !cont [0])
                        pw [0].setVisible (true);

                    if (cont [0])
                        new ComplementosWindow (new ConcurrentHashMap <Complemento, BigInteger> (), db,
                                new Entrada (espectador, pelicula, Calendar.getInstance (), sala,
                                        sala.getButacas ().get (selection [0])), w);
                }
            });

            f.add (((Supplier <JLabel>) ( () -> {
                JLabel l = new JLabel (espectador == null ? "Invitado" : espectador.getNombre ());

                l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                return l;
            })).get (), BorderLayout.PAGE_START);

            f.add (((Supplier <JPanel>) ( () -> {
                JPanel p = new JPanel ();
                p.setBorder (BorderFactory.createEmptyBorder (25, 25, 25, 25));
                p.setLayout (new BoxLayout (p, BoxLayout.Y_AXIS));

                sala.llenarSala (pelicula);

                JLabel selectionLabel = (((Supplier <JLabel>) ( () -> {
                    JLabel l = new JLabel ("Ninguna butaca seleccionada");

                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 13f));

                    return l;
                })).get ());
                JButton sb[] = { new JButton ("Confirmar"), new JButton ("Limpiar selección") };
                JXTable t[] = new JXTable [1];
                List <ImageDisplayer> id[] = new List [1];
                Image img[] = new Image [4];
                boolean usingid[] = new boolean [1];

                p.add (((Supplier <JPanel>) ( () -> {
                    JPanel q = new JPanel (new GridBagLayout ());

                    GridBagConstraints gbc = new GridBagConstraints ();
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.fill = GridBagConstraints.NONE;
                    gbc.anchor = GridBagConstraints.NORTH;
                    gbc.insets = new Insets (25, 0, 0, 25);

                    q.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Selecciona un asiento");

                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                        return l;
                    })).get (), gbc);

                    gbc.fill = GridBagConstraints.NONE;
                    gbc.anchor = GridBagConstraints.CENTER;

                    final String SEAT_IMAGE_PATH = "data/assets/seat.png";
                    final String SEAT_IMAGE_URL = "https://cdn3.iconfinder.com/data/icons/movie-entertainment-flat-style/64/13_seat-movie-cinema-chair-theater-512.png";
                    final int SEAT_IMAGE_WIDTH = 25;
                    final int SEAT_IMAGE_HEIGHT = 25;

                    Runnable func = () -> {
                        q.add (((Supplier <JXTable>) ( () -> {
                            t [0] = new JXTable (new DefaultTableModel (Sala.getFilas (), Sala.getColumnas ()) {
                                @Override
                                public boolean isCellEditable (int row, int column) {
                                    return false;
                                }
                            }) {
                                @Override
                                public Component prepareRenderer (TableCellRenderer renderer, int row, int column) {
                                    Component c = super.prepareRenderer (renderer, row, column);

                                    c.setBackground (sala.getButacas ().get (Sala.getColumnas () * row + column)
                                            .ocupada ()
                                                    ? Color.GRAY
                                                    : row == t [0].getSelectedRow ()
                                                            && column == t [0].getSelectedColumn ()
                                                                    ? new Color (200, 0, 100)
                                                                    : Color.WHITE);

                                    return c;
                                }

                                @Override
                                public void clearSelection () {
                                    selection [0] = -1;
                                    super.clearSelection ();
                                }
                            };

                            t [0].setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
                            t [0].setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

                            t [0].setDefaultRenderer (Object.class, new DefaultTableCellRenderer () {
                                @Override
                                public Component getTableCellRendererComponent (JTable table, Object value,
                                        boolean isSelected, boolean hasFocus,
                                        int row, int column) {
                                    Component c = super.getTableCellRendererComponent (table,
                                            value,
                                            isSelected,
                                            hasFocus, row, column);

                                    c.setBackground (sala.getButacas ().get (Sala.getColumnas () * row + column)
                                            .ocupada ()
                                                    ? Color.GRAY
                                                    : row == (int) (selection [0] / Sala.getColumnas ())
                                                            && column == selection [0] % Sala.getColumnas ()
                                                            && isSelected && selection [0] != -1
                                                                    ? new Color (200, 0, 100)
                                                                    : Color.WHITE);

                                    return c;
                                }
                            });

                            for (int i = 0; i < t [0].getColumnCount (); t [0].getColumnModel ().getColumn (i++)
                                    .setPreferredWidth (SEAT_IMAGE_WIDTH))
                                ;

                            t [0].setRowHeight (SEAT_IMAGE_HEIGHT);

                            t [0].getSelectionModel ().addListSelectionListener (e -> {
                                if ((selection [0] == -1
                                        && (t [0].getSelectedRow () == -1 || t [0].getSelectedColumn () == -1)) || sala
                                                .getButacas ().get (
                                                        Sala.getColumnas () * t [0].getSelectedRow ()
                                                                + t [0].getSelectedColumn ())
                                                .ocupada ()) {
                                    if (selection [0] != -1) {
                                        t [0].setRowSelectionInterval ((int) (selection [0] / Sala.getColumnas ()),
                                                (int) (selection [0] / Sala.getColumnas ()));

                                        t [0].setColumnSelectionInterval (selection [0] % Sala.getColumnas (),
                                                selection [0] % Sala.getColumnas ());

                                        selectionLabel.setText (String.format ("Fila %d | Butaca %d",
                                                (int) (selection [0] / Sala.getColumnas ()) + 1,
                                                selection [0] % Sala.getColumnas () + 1));
                                    }

                                    sb [0].setEnabled (selection [0] != -1);

                                    return;
                                }

                                selection [0] = Sala.getColumnas () * t [0].getSelectedRow ()
                                        + t [0].getSelectedColumn ();

                                selectionLabel.setText (String.format ("Fila %d | Butaca %d",
                                        t [0].getSelectedRow () + 1,
                                        t [0].getSelectedColumn () + 1));

                                sb [0].setEnabled (true);
                            });

                            return t [0];
                        })).get (), gbc);
                    };

                    if (Settings.usingFallbackRenderer ()) {
                        func.run ();

                        return q;
                    }

                    try {
                        Utils.downloadFile (SEAT_IMAGE_PATH, SEAT_IMAGE_URL);
                    }

                    catch (Exception e) {
                        Logger.getLogger (LoadingWindow.class.getName ()).log (Level.WARNING, String.format (
                                "No se pudo descargar el archivo %s desde %s.", SEAT_IMAGE_PATH, SEAT_IMAGE_URL));
                    }

                    try {
                        img [0] = new ImageIcon (new File (SEAT_IMAGE_PATH).toURI ().toURL ()).getImage ()
                                .getScaledInstance (SEAT_IMAGE_WIDTH, SEAT_IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                    }

                    catch (Exception e) {
                        Logger.getLogger (LoadingWindow.class.getName ()).log (Level.WARNING,
                                String.format ("No se pudo cargar el archivo %s.", SEAT_IMAGE_PATH));

                        img [0] = null;
                    }

                    if (img [0] == null) {
                        func.run ();

                        return q;
                    }

                    try {
                        Color c;

                        img [1] = ImageIO.read (new File (SEAT_IMAGE_PATH));
                        for (int i = 0, r, g, b; i < img [1].getWidth (null); i++)
                            for (int j = 0; j < img [1].getHeight (null); j++) {
                                c = new Color (((BufferedImage) img [1]).getRGB (i, j));
                                r = (int) (c.getRed () * 0.299f);
                                g = (int) (c.getGreen () * 0.587f);
                                b = (int) (c.getBlue () * 0.114f);

                                ((BufferedImage) img [1]).setRGB (i, j,
                                        new Color (r + g + b, r + g + b, r + g + b).getRGB ());
                            }
                        img [1] = img [1].getScaledInstance (SEAT_IMAGE_WIDTH, SEAT_IMAGE_HEIGHT, Image.SCALE_SMOOTH);

                        img [2] = ImageIO.read (new File (SEAT_IMAGE_PATH));
                        c = new Color (200, 200, 200);
                        for (int i = 0; i < img [2].getWidth (null); i++)
                            for (int j = 0; j < img [2].getHeight (null); ((BufferedImage) img [2]).setRGB (i, j++,
                                    c.getRGB ()))
                                ;
                        img [2] = img [2].getScaledInstance (SEAT_IMAGE_WIDTH, SEAT_IMAGE_HEIGHT, Image.SCALE_SMOOTH);

                        img [3] = ImageIO.read (new File (SEAT_IMAGE_PATH));
                        c = new Color (200, 0, 100);
                        for (int i = 0; i < img [3].getWidth (null); i++)
                            for (int j = 0; j < img [3].getHeight (null); ((BufferedImage) img [3]).setRGB (i, j++,
                                    c.getRGB ()))
                                ;
                        img [3] = img [3].getScaledInstance (SEAT_IMAGE_WIDTH, SEAT_IMAGE_HEIGHT, Image.SCALE_SMOOTH);

                        id [0] = new ArrayList <ImageDisplayer> (Sala.getFilas () * Sala.getColumnas ());

                        q.add (((Supplier <JPanel>) ( () -> {
                            JPanel r = new JPanel (new GridLayout (Sala.getFilas (), Sala.getColumnas (), 2, 2));

                            for (int i[] = new int [1]; i [0] < Sala.getFilas () * Sala.getColumnas (); i [0]++)
                                r.add (((Supplier <ImageDisplayer>) ( () -> {
                                    ImageDisplayer disp = new ImageDisplayer (
                                            img [sala.getButacas ().get (i [0]).ocupada () ? 1 : 0]);

                                    if (!sala.getButacas ().get (i [0]).ocupada ())
                                        disp.addMouseListener (new MouseAdapter () {
                                            @Override
                                            public void mouseReleased (MouseEvent e) {
                                                if (selection [0] != -1)
                                                    id [0].get (selection [0]).setImage (img [0]);

                                                disp.setImage (img [3]);
                                                selection [0] = id [0].indexOf (disp);
                                                selectionLabel.setText (String.format ("Fila %d | Butaca %d",
                                                        (int) (selection [0] / Sala.getColumnas ()) + 1,
                                                        selection [0] % Sala.getColumnas () + 1));

                                                sb [0].setEnabled (true);
                                            }

                                            @Override
                                            public void mouseEntered (MouseEvent e) {
                                                if (id [0].indexOf (disp) != selection [0])
                                                    disp.setImage (img [2]);
                                            }

                                            @Override
                                            public void mouseExited (MouseEvent e) {
                                                if (id [0].indexOf (disp) != selection [0])
                                                    disp.setImage (img [0]);
                                            }
                                        });

                                    id [0].add (disp);
                                    return disp;
                                })).get ());

                            return r;
                        })).get (), gbc);

                        usingid [0] = true;
                    }

                    catch (IOException e1) {
                        Logger.getLogger (SalaCineWindow.class.getName ()).log (Level.WARNING, String.format (
                                "No se pudieron crear las imágenes derivadas de la imagen de butaca.",
                                SEAT_IMAGE_PATH));

                        func.run ();
                    }

                    return q;
                })).get ());

                p.add (Box.createRigidArea (new Dimension (0, 25)));

                p.add (((Supplier <JPanel>) ( () -> {
                    JPanel q = new JPanel (new GridBagLayout ());

                    GridBagConstraints gbc = new GridBagConstraints ();
                    gbc.anchor = GridBagConstraints.CENTER;
                    gbc.fill = GridBagConstraints.NONE;
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.insets = new Insets (15, 0, 15, 0);

                    q.add (selectionLabel, gbc);

                    q.add (((Supplier <JPanel>) ( () -> {
                        JPanel r = new JPanel (new FlowLayout (FlowLayout.CENTER, 10, 0));

                        r.add (((Supplier <JButton>) ( () -> {
                            sb [0].setEnabled (false);

                            sb [0].addActionListener (e -> {
                                cont [0] = true;
                                f.dispose ();
                            });

                            return sb [0];
                        })).get ());

                        r.add (((Supplier <JButton>) ( () -> {
                            if (usingid [0]) {
                                sb [1].addActionListener (e -> {
                                    if (selection [0] == -1)
                                        return;

                                    id [0].get (selection [0]).setImage (img [0]);
                                    selectionLabel.setText ("Ninguna butaca seleccionada.");

                                    selection [0] = -1;
                                });

                                return sb [1];
                            }

                            sb [1].addActionListener (e -> {
                                if (selection [0] == -1)
                                    return;

                                t [0].clearSelection ();
                                selection [0] = -1;
                                selectionLabel.setText ("Ninguna butaca seleccionada.");
                                sb [0].setEnabled (false);
                            });

                            return sb [1];
                        })).get ());

                        return r;
                    })).get ());

                    return q;
                })).get ());

                return p;
            })).get (), BorderLayout.CENTER);

            f.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
            f.setIconImage (new ImageIcon (this.getClass ()
                    .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ()
                            .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
            f.setTitle (String.format ("Asistir a %s", pelicula.getNombre ()));
            f.pack ();
            f.setResizable (false);
            f.setLocationRelativeTo (w);
            f.setVisible (true);
        }, true);
    }
}
