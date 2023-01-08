package VentanaGrafica;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import internals.GestorBD;
import internals.Pair;
import internals.Utils;
import internals.swing.ImageDisplayer;

public class VentanaSalaCine extends JFrame {
    public VentanaSalaCine (final GestorBD db, final Espectador espectador, final Pelicula pelicula,
            final VentanaSeleccionarPelicula w)
            throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException ("El gestor de bases de datos no puede ser nulo.");

        if (pelicula == null)
            throw new NullPointerException ("La pelicula no puede ser nula.");

        final VentanaSalaCine f = this;
        final VentanaSeleccionarPelicula pw[] = new VentanaSeleccionarPelicula [] { w };

        new LoadingWindow ( () -> {
            f.addWindowListener (new WindowAdapter () {
                @Override
                public void windowClosed (WindowEvent e) {
                    if (pw [0] != null)
                        pw [0].setVisible (true);
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

                Sala sala = Sala.getSalas ().get (new Random ().nextInt (5));
                sala.llenarSala (pelicula);
                int selection[] = new int [] { -1 };

                JLabel selectionLabel = (((Supplier <JLabel>) ( () -> {
                    JLabel l = new JLabel ("Ninguna butaca seleccionada");

                    l.setFont (l.getFont ().deriveFont (Font.BOLD, 13f));

                    return l;
                })).get ());
                JButton sb[] = { new JButton ("Confirmar"), new JButton ("Limpiar selección") };
                List <ImageDisplayer> id = new ArrayList <ImageDisplayer> (Sala.getFilas () * Sala.getColumnas ());
                Image img[] = new Image [4];
                boolean usingid[] = new boolean [1];

                p.add (((Supplier <JPanel>) ( () -> {
                    JPanel q = new JPanel (new GridBagLayout ());

                    GridBagConstraints gbc = new GridBagConstraints ();
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.fill = GridBagConstraints.NONE;
                    gbc.anchor = GridBagConstraints.NORTH;
                    gbc.insets = new Insets (50, 0, 0, 0);

                    q.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Selecciona un asiento");

                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                        return l;
                    })).get (), gbc);

                    gbc.fill = GridBagConstraints.NONE;
                    gbc.anchor = GridBagConstraints.CENTER;

                    Runnable func = () -> {

                    };

                    final String SEAT_IMAGE_PATH = "data/assets/seat.png";
                    final String SEAT_IMAGE_URL = "https://cdn3.iconfinder.com/data/icons/movie-entertainment-flat-style/64/13_seat-movie-cinema-chair-theater-512.png";
                    final int SEAT_IMAGE_WIDTH = 25;
                    final int SEAT_IMAGE_HEIGHT = 25;

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
                                                    id.get (selection [0]).setImage (img [0]);

                                                disp.setImage (img [3]);
                                                selection [0] = id.indexOf (disp);
                                                selectionLabel.setText (String.format ("Fila %d | Butaca %d",
                                                        (int) (selection [0] / Sala.getFilas ()) + 1,
                                                        selection [0] % Sala.getColumnas () + 1));
                                            }

                                            @Override
                                            public void mouseEntered (MouseEvent e) {
                                                if (id.indexOf (disp) != selection [0])
                                                    disp.setImage (img [2]);
                                            }

                                            @Override
                                            public void mouseExited (MouseEvent e) {
                                                if (id.indexOf (disp) != selection [0])
                                                    disp.setImage (img [0]);
                                            }
                                        });

                                    id.add (disp);
                                    return disp;
                                })).get ());

                            return r;
                        })).get (), gbc);

                        usingid [0] = true;
                    }

                    catch (IOException e1) {
                        Logger.getLogger (VentanaSalaCine.class.getName ()).log (Level.WARNING, String.format (
                                "No se pudieron crear las imágenes derivadas de la imagen de butaca.",
                                SEAT_IMAGE_PATH));

                        func.run ();
                    }

                    return q;
                })).get ());

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
                            sb [0].addActionListener (e -> {
                                f.setVisible (false);

                                new VentanaComplementos (db, f, espectador, pelicula, sala,
                                        new Pair <Integer, Integer> (
                                                selection [0] / Sala.getFilas (), selection [0] % Sala.getColumnas ()));
                            });

                            return sb [0];
                        })).get ());

                        r.add (((Supplier <JButton>) ( () -> {
                            if (usingid [0]) {
                                sb [1].addActionListener (e -> {
                                    if (selection [0] == -1)
                                        return;

                                    id.get (selection [0]).setImage (img [0]);
                                    selectionLabel.setText ("Ninguna butaca seleccionada.");

                                    selection [0] = -1;
                                });

                                return sb [1];
                            }

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
