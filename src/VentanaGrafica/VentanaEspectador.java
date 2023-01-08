package VentanaGrafica;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Font;

import java.util.Locale;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cine.Espectador;
import internals.GestorBD;

public class VentanaEspectador extends JFrame {
    public VentanaEspectador () {
        this (null, null);
    }

    public VentanaEspectador (final GestorBD db, final Espectador espectador) {
        this (db, espectador, null);
    }

    public VentanaEspectador (final GestorBD db, final Espectador espectador, final VentanaInicio w)
            throws NullPointerException {
        super ();

        if (db == null && espectador != null)
            throw new NullPointerException ("Si la base de datos es nula el espectador debe ser también nulo.");

        final VentanaEspectador f = this;
        final VentanaInicio pw[] = new VentanaInicio [] { w };
        final JButton b[] = new JButton [2];

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowOpened (WindowEvent e) {
                if (b [1] != null && espectador != null && !espectador.getHistorial ().isEmpty ())
                    b [1].setEnabled (true);
            }

            @Override
            public void windowClosed (WindowEvent e) {
                if (pw [0] != null)
                    pw [0].setVisible (true);
            }
        });

        this.add (((Supplier <JLabel>) ( () -> {
            JLabel l = new JLabel (espectador == null ? "Invitado" : espectador.getNombre ());

            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

            return l;
        })).get (), BorderLayout.PAGE_START);

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setBorder (BorderFactory.createEmptyBorder (25, 25, 25, 25));
            p.setLayout (new GridBagLayout ());

            GridBagConstraints gbc = new GridBagConstraints ();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTH;

            p.add (((Supplier <JButton>) ( () -> {
                b [0] = new JButton ("Asistir a una película.");

                b [0].addActionListener (e -> {
                    f.setVisible (false);
                    new VentanaSeleccionarPelicula (db, espectador, f);
                });

                return b [0];
            })).get (), gbc);

            gbc.insets = new Insets (15, 0, 0, 0);
            gbc.anchor = GridBagConstraints.SOUTH;

            p.add (((Supplier <JButton>) ( () -> {
                b [1] = new JButton ("Ver historial");

                b [1].setEnabled (espectador != null && !espectador.getHistorial ().isEmpty ());
                b [1].addActionListener (e -> {
                    f.setVisible (false);
                    new HistorialWindow (espectador, f);
                });

                return b [1];
            })).get (), gbc);

            return p;
        })).get (), BorderLayout.CENTER);

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle (
                String.format ("Menú principal de %s", espectador == null ? "Invitado" : espectador.getNombre ()));
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileChooser.homeFolderIcon", new Locale ("es-ES"))).getImage ()
                        .getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
