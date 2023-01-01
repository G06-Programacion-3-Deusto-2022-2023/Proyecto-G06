package internals;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class ImageDisplayer extends JPanel {
    private final Image image;

    public ImageDisplayer (Image image) {
        this (image, image.getWidth (null), image.getHeight (null));
    }

    public ImageDisplayer (Image image, int width) {
        this (image, width, image.getHeight (null) * width / image.getWidth (null));
    }

    public ImageDisplayer (Image image, int width, int height) {
        super ();

        this.image = image.getScaledInstance (width, height, 0);
    }

    @Override
    protected void paintComponent (Graphics g) {
        super.paintComponent (g);

        g.drawImage (this.image, 0, 0, this.getWidth (), this.getHeight (), this);
    }

    @Override
    public Dimension getMinimumSize () {
        return this.getPreferredSize ();
    }

    @Override
    public Dimension getPreferredSize () {
        return new Dimension (this.image.getHeight (this), this.image.getWidth (this));
    }

    @Override
    public Dimension getMaximumSize () {
        return this.getPreferredSize ();
    }
}
