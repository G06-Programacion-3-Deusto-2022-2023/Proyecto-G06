package graphical;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Espectador;
import cine.Pelicula;
import internals.GestorBD;
import internals.Pair;
import internals.Settings;
import internals.Triplet;
import internals.swing.ImageDisplayer;

public class SeleccionarPeliculaWindow extends JFrame {
    public SeleccionarPeliculaWindow (final GestorBD db, final Espectador espectador, final EspectadorWindow w)
            throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar una base de datos nula a la ventana de selección de películas.");

        final EspectadorWindow pw[] = new EspectadorWindow [] { w };
        final SeleccionarPeliculaWindow f = this;

        final Dimension idims = new Dimension (200, 200 * 16 / 9);
        final long sleep = 15;

        final LinkedList <Pelicula> lp = new LinkedList <Pelicula> (Settings.getActiveSet ().getPeliculas ());
        final Triplet <ImageDisplayer, JLabel, Runnable> pd[] = new Triplet [3];
        pd [0] = new Triplet <ImageDisplayer, JLabel, Runnable> (new ImageDisplayer (), ((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel ();

            l.setFont (l.getFont ().deriveFont (Font.BOLD, 14f));
            l.setMinimumSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setPreferredSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setMaximumSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setHorizontalAlignment (SwingConstants.CENTER);

            return l;
        })).get (), () -> {
            Dimension psz = pd [0].x.getParent () == null ? null : new Dimension (pd [0].x.getParent ().getSize ());

            Image img;
            try {
                img = new ImageIcon (new File (lp.get (0).getRutaImagen ()).toURI ().toURL ()).getImage ();
            }

            catch (MalformedURLException e1) {
                Logger.getLogger (SeleccionarPeliculaWindow.class.getName ()).log (Level.WARNING,
                        String.format ("No se pudo crear una URL a partir del archivo %s",
                                Settings.getLogo ()));

                img = new ImageIcon (this.getClass ()
                        .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ();
            }

            pd [0].x.setImage (img, (int) idims.getWidth (), (int) idims.getHeight (), Image.SCALE_SMOOTH);
            pd [0].y.setText (lp.get (0).getNombre ());

            if (psz != null) {
                pd [0].x.getParent ().setSize (psz);
                pd [0].x.getParent ().repaint ();
            }

            try {
                Thread.sleep (sleep);
            }

            catch (InterruptedException e1) {
                e1.printStackTrace ();
            }

            f.repaint ();
        });
        pd [1] = new Triplet <ImageDisplayer, JLabel, Runnable> (new ImageDisplayer (), ((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel ();

            l.setFont (l.getFont ().deriveFont (Font.BOLD, 14f));
            l.setMinimumSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setPreferredSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setMaximumSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setHorizontalAlignment (SwingConstants.CENTER);

            return l;
        })).get (), () -> {
            Dimension psz = pd [1].x.getParent () == null ? null : new Dimension (pd [1].x.getParent ().getSize ());

            Image img;
            try {
                img = new ImageIcon (new File (lp.get (1).getRutaImagen ()).toURI ().toURL ()).getImage ();
            }

            catch (MalformedURLException e1) {
                Logger.getLogger (SeleccionarPeliculaWindow.class.getName ()).log (Level.WARNING,
                        String.format ("No se pudo crear una URL a partir del archivo %s",
                                Settings.getLogo ()));

                img = new ImageIcon (this.getClass ()
                        .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ();
            }

            pd [1].x.setImage (img, (int) idims.getWidth (), (int) idims.getHeight (), Image.SCALE_SMOOTH);
            pd [1].y.setText (lp.get (1).getNombre ());

            if (psz != null) {
                pd [1].x.getParent ().setSize (psz);
                pd [1].x.getParent ().repaint ();
            }

            try {
                Thread.sleep (sleep);
            }

            catch (InterruptedException e1) {
                e1.printStackTrace ();
            }

            f.repaint ();
        });
        pd [2] = new Triplet <ImageDisplayer, JLabel, Runnable> (new ImageDisplayer (), ((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel ();

            l.setFont (l.getFont ().deriveFont (Font.BOLD, 14f));
            l.setMinimumSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setPreferredSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setMaximumSize (new Dimension ((int) idims.getWidth (), (int) (idims.getHeight () / 5)));
            l.setHorizontalAlignment (SwingConstants.CENTER);

            return l;
        })).get (), () -> {
            Dimension psz = pd [2].x.getParent () == null ? null : new Dimension (pd [2].x.getParent ().getSize ());

            Image img;
            try {
                img = new ImageIcon (new File (lp.get (2).getRutaImagen ()).toURI ().toURL ()).getImage ();
            }

            catch (MalformedURLException e1) {
                Logger.getLogger (SeleccionarPeliculaWindow.class.getName ()).log (Level.WARNING,
                        String.format ("No se pudo crear una URL a partir del archivo %s",
                                Settings.getLogo ()));

                img = new ImageIcon (this.getClass ()
                        .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ();
            }

            pd [2].x.setImage (img, (int) idims.getWidth (), (int) idims.getHeight (), Image.SCALE_SMOOTH);
            pd [2].y.setText (lp.get (2).getNombre ());

            if (psz != null) {
                pd [2].x.getParent ().setSize (psz);
                pd [2].x.getParent ().repaint ();
            }

            try {
                Thread.sleep (sleep);
            }

            catch (InterruptedException e1) {
                e1.printStackTrace ();
            }

            f.repaint ();
        });

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (pw [0] != null)
                    w.setVisible (true);
            }
        });

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel (new GridBagLayout ());
            p.setBorder (BorderFactory.createEmptyBorder (25, 25, 25, 25));

            GridBagConstraints gbc = new GridBagConstraints ();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.insets = new Insets (10, 0, 10, 0);

            p.add (((Supplier <JButton>) ( () -> {
                JButton b = new JButton (new ImageIcon (this.getClass ()
                        .getResource ("/toolbarButtonGraphics/navigation/Up16.gif")));

                b.addActionListener (e -> {
                    lp.addFirst (lp.removeLast ());
                    lp.addFirst (lp.removeLast ());
                    lp.addFirst (lp.removeLast ());

                    pd [0].z.run ();
                    pd [1].z.run ();
                    pd [2].z.run ();
                });

                return b;
            })).get (), gbc);

            gbc.anchor = GridBagConstraints.CENTER;

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel (new FlowLayout (FlowLayout.CENTER, 15, 0));

                for (int i[] = new int [1]; i [0] < pd.length; i [0]++)
                    q.add (((Supplier <JPanel>) ( () -> {
                        JPanel r = new JPanel (new GridBagLayout ());
                        r.setBorder (BorderFactory.createEmptyBorder (1, 1, 1, 1));

                        GridBagConstraints gbc2 = new GridBagConstraints ();
                        gbc2.gridwidth = GridBagConstraints.REMAINDER;
                        gbc2.fill = GridBagConstraints.NONE;
                        gbc2.anchor = GridBagConstraints.NORTH;

                        Pair <Triplet <ImageDisplayer, JLabel, Runnable>, Integer> cp = new Pair <Triplet <ImageDisplayer, JLabel, Runnable>, Integer> (
                                pd [i [0]], i [0]);

                        cp.x.z.run ();

                        r.addMouseListener (new MouseAdapter () {
                            @Override
                            public void mouseClicked (MouseEvent e) {
                                if (SwingUtilities.isRightMouseButton (e)) {
                                    new PeliculaDetailsWindow (lp.get (cp.y));

                                    return;
                                }

                                if (!SwingUtilities.isLeftMouseButton (e))
                                    return;

                                pw [0] = null;
                                f.dispose ();

                                new SalaCineWindow (db, espectador, lp.get (cp.y), w);
                            }

                            @Override
                            public void mouseEntered (MouseEvent e) {
                                r.setBorder (BorderFactory.createLineBorder (Color.GRAY, 1));
                            }

                            @Override
                            public void mouseExited (MouseEvent e) {
                                r.setBorder (BorderFactory.createEmptyBorder (1, 1, 1, 1));
                            }
                        });

                        r.add (cp.x.x, gbc2);

                        gbc2.insets = new Insets (5, 0, 0, 0);
                        gbc2.anchor = GridBagConstraints.SOUTH;

                        r.add (cp.x.y, gbc2);

                        return r;
                    })).get ());

                return q;
            })).get (), gbc);

            gbc.anchor = GridBagConstraints.SOUTH;

            p.add (((Supplier <JButton>) ( () -> {
                JButton b = new JButton (new ImageIcon (this.getClass ()
                        .getResource ("/toolbarButtonGraphics/navigation/Down16.gif")));

                b.addActionListener (e -> {
                    lp.add (lp.pop ());
                    lp.add (lp.pop ());
                    lp.add (lp.pop ());

                    pd [0].z.run ();
                    pd [1].z.run ();
                    pd [2].z.run ();
                });

                return b;
            })).get (), gbc);

            return p;
        })).get ());

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImage (new ImageIcon (this.getClass ()
                .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.setTitle ("Asistir a una película");
        this.setSize ((int) (idims.getWidth () * 3.75), (int) (idims.getHeight () * 1.75));
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
