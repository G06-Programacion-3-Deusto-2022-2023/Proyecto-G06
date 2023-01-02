package VentanaGrafica;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import cine.Genero;
import cine.Pelicula;

public class PeliculaDetailsWindow extends JFrame {
    public PeliculaDetailsWindow (Pelicula pelicula) {
        this (pelicula, null);
    }

    public PeliculaDetailsWindow (Pelicula pelicula, JFrame w) throws NullPointerException {
        super ();

        if (pelicula == null)
            throw new NullPointerException (
                    "No es posible pasar una película nula a la ventana de ver detalles de una película.");

        PeliculaDetailsWindow f = this;

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (w == null)
                    return;

                w.setVisible (true);
            }
        });

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));
            p.setAlignmentX (Component.CENTER_ALIGNMENT);
            p.setAlignmentY (Component.CENTER_ALIGNMENT);

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));
                q.setAlignmentX (Component.CENTER_ALIGNMENT);

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                q.add (((Supplier <JLabel>) ( () -> {
                    JLabel l = new JLabel (new ImageIcon (((Supplier <Image>) ( () -> {
                        Image img;

                        try {
                            img = new ImageIcon (ImageIO.read (new File (pelicula.getRutaImagen ()))).getImage ()
                                    .getScaledInstance (200, 200, Image.SCALE_SMOOTH);
                        }

                        catch (IOException e) {
                            Logger.getLogger (GestionarPeliculasWindow.class.getName ()).log (Level.WARNING,
                                    String.format ("No se pudo crear una imagen a partir del archivo %s",
                                            pelicula.getRutaImagen ()));

                            img = new ImageIcon (this.getClass ()
                                    .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ()
                                            .getScaledInstance (200, 200, Image.SCALE_SMOOTH);
                        }

                        return img;
                    })).get ()));
                    l.setAlignmentX (Component.CENTER_ALIGNMENT);

                    return l;
                })).get ());
                q.add (Box.createRigidArea (new Dimension (0, 15)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout ());

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Nombre:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                13f));

                        return l;
                    })).get ());

                    r.add (new JLabel (pelicula.getNombre ()));

                    return r;
                })).get ());
                q.add (Box.createRigidArea (new Dimension (0, 10)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout ());

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Director:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                13f));

                        return l;
                    })).get ());

                    r.add (new JLabel (pelicula.getDirector () == null || pelicula.getDirector ().equals ("") ? "-"
                            : pelicula.getDirector ()));

                    return r;
                })).get ());
                q.add (Box.createRigidArea (new Dimension (0, 10)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout ());

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Fecha:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                13f));

                        return l;
                    })).get ());

                    r.add (new JLabel (
                            pelicula.getFecha () == null || pelicula.getFecha ().compareTo (Pelicula.minFecha ()) < 0
                                    || pelicula.getFecha ().compareTo (Pelicula.maxFecha ()) > 0 ? "-"
                                            : pelicula.getFecha ().toString ()));

                    return r;
                })).get ());
                q.add (Box.createRigidArea (new Dimension (0, 10)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout ());

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Valoración:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                13f));

                        return l;
                    })).get ());

                    r.add (new JLabel (Double.isNaN (pelicula.getValoracion ()) || pelicula.getValoracion () < 1
                            || pelicula.getValoracion () > 10 ? "-"
                                    : String.format ("%.1f", pelicula.getValoracion ())));

                    return r;
                })).get ());
                q.add (Box.createRigidArea (new Dimension (0, 10)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout ());

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Duración:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                13f));

                        return l;
                    })).get ());

                    r.add (new JLabel (pelicula.getDuracion ().isZero () || pelicula.getDuracion ().isNegative () ? "-"
                            : pelicula.duracionToString ()));

                    return r;
                })).get ());
                q.add (Box.createRigidArea (new Dimension (0, 10)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout ());

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Edad recomendada:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                13f));

                        return l;
                    })).get ());

                    r.add (new JLabel (pelicula.getEdad ().toString ()));

                    return r;
                })).get ());
                q.add (Box.createRigidArea (new Dimension (0, 10)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel (new FlowLayout ());

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Géneros:");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD,
                                13f));

                        return l;
                    })).get ());

                    r.add (new JLabel (((Supplier <String>) ( () -> {
                        StringBuilder str = new StringBuilder ();

                        List <Genero.Nombre> g = pelicula.getGeneros ().stream ().collect (Collectors.toList ());
                        for (int i = 0; i < g.size (); i++)
                            str.append (String.format ("%s%s",
                                    g.get (i).toString (),
                                    i != g.size () - 1
                                            ? " · "
                                            : ""));

                        return str.toString ();
                    })).get ()));

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            return p;
        })).get (), BorderLayout.CENTER);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImage (new ImageIcon (this.getClass ()
                .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.setTitle (String.format ("Detalles de %s", pelicula.getNombre ()));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
