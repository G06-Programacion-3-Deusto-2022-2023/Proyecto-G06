package internals.swing;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import java.awt.Image;
import java.awt.Component;
import java.awt.image.BufferedImage;

import cine.Pelicula;

public final class PeliculasComboBoxRenderer extends JLabel implements ListCellRenderer <Pelicula> {
    public PeliculasComboBoxRenderer () {
        this.setOpaque (true);
        this.setHorizontalAlignment (SwingConstants.CENTER);
        this.setVerticalAlignment (SwingConstants.CENTER);
    }

    @Override
    public Component getListCellRendererComponent (JList list, Pelicula value, int index, boolean isSelected,
            boolean cellHasFocus) {
        if (value == null)
            return this;

        this.setBackground (isSelected ? list.getSelectionBackground () : list.getBackground ());
        this.setForeground (isSelected ? list.getSelectionForeground () : list.getForeground ());
        this.setIconTextGap (25);

        this.setText (value.getNombre ());
        this.setIcon (new ImageIcon (((Supplier <Image>) ( () -> {
            Image img;

            try {
                BufferedImage imgbuf;

                if ((imgbuf = ImageIO.read (new File (value.getRutaImagen ()))) == null)
                    throw new IllegalArgumentException (
                            String.format ("No se puede leer la imagen especificada por %s",
                                    value.getRutaImagen ()));

                img = new ImageIcon (imgbuf).getImage ()
                        .getScaledInstance (64, 64, 0);
            }

            catch (IllegalArgumentException | IOException e) {
                Logger.getLogger (PeliculasComboBoxRenderer.class.getName ()).log (Level.WARNING,
                        String.format ("No se pudo crear una imagen a partir del archivo %s",
                                value.getRutaImagen ()));

                img = new ImageIcon (this.getClass ()
                        .getResource ("/toolbarButtonGraphics/media/Movie24.gif")).getImage ()
                                .getScaledInstance (64, 64, 0);
            }

            return img;
        })).get ()));

        return this;
    }
}
