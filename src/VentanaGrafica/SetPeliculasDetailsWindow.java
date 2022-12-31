package VentanaGrafica;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.Vector;
import java.util.function.Supplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cine.Pelicula;
import cine.SetPeliculas;
import internals.swing.PeliculasComboBoxRenderer;

public class SetPeliculasDetailsWindow extends JFrame {
    public SetPeliculasDetailsWindow (SetPeliculas set) {
        this (set, null);
    }

    public SetPeliculasDetailsWindow (SetPeliculas set, GestionarPeliculasWindow w) {
        super ();

        if (set == null)
            throw new NullPointerException (
                    "No es posible pasar un set de películas nulo a la ventana de ver detalles de un set de películas.");

        GestionarPeliculasWindow pw[] = new GestionarPeliculasWindow [] { w };
        SetPeliculasDetailsWindow f = this;

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (pw [0] == null)
                    return;

                w.setVisible (true);
            }
        });

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));
                q.setAlignmentX (Component.CENTER_ALIGNMENT);

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new GridLayout (2, 1, 0, 5));
                    r.setAlignmentX (Component.CENTER_ALIGNMENT);

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Nombre");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 18f));

                        return l;
                    })).get ());

                    r.add (new JLabel (set.getNombre ()));

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new GridLayout (2, 1, 0, 5));
                    r.setAlignmentX (Component.CENTER_ALIGNMENT);

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Tamaño");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 18f));

                        return l;
                    })).get ());

                    r.add (new JLabel (Integer.toString (set.size ())));

                    return r;
                })).get ());

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new GridLayout (2, 1, 0, 5));
                    r.setAlignmentX (Component.CENTER_ALIGNMENT);

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Películas");
                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 18f));

                        return l;
                    })).get ());

                    r.add (((Supplier <JComboBox>) ( () -> {
                        JComboBox <Pelicula> peliculas = new JComboBox <Pelicula> (
                                new Vector <Pelicula> (set.getPeliculas ()));

                        peliculas.setRenderer (new PeliculasComboBoxRenderer ());
                        peliculas.setMaximumRowCount (5);
                        peliculas.setSelectedIndex (peliculas.getItemCount () > 0 ? 0 : -1);

                        peliculas.addActionListener (e -> {
                            try {
                                pw [0] = null;
                                f.setVisible (false);
                                pw [0] = w;

                                new PeliculaDetailsWindow ((Pelicula) peliculas.getSelectedItem (), f);
                            }

                            catch (NullPointerException e1) {
                                JOptionPane.showMessageDialog (f,
                                        "La película seleccionada es nula por lo que no pueden verse sus detalles.",
                                        "Error al ver los detalles de la película", JOptionPane.ERROR_MESSAGE);

                                f.setVisible (true);
                            }
                        });

                        return peliculas;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            return p;
        })).get (), BorderLayout.CENTER);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.pack ();
        this.setResizable (false);
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileView.directoryIcon", new Locale ("es-ES"))).getImage ());
        this.setTitle (String.format ("Detalles de %s", set.getNombre ()));
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}
