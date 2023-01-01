package internals.swing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class ImageChooser extends JFileChooser {
    public ImageChooser () {
        super ();

        FileFilter f;
        this.addChoosableFileFilter (f = new FileFilter () {
            @Override
            public String getDescription () {
                return Locale.getDefault ().getLanguage ().equalsIgnoreCase ("es") ? "Imágenes"
                        : "Images";
            }

            @Override
            public boolean accept (File f) {
                try {
                    return f.isDirectory () || Files.probeContentType (f.toPath ()).split ("/") [0].equals ("image");
                }

                catch (NullPointerException | IOException e) {
                    Logger.getLogger (ImageChooser.class.getName ()).log (Level.WARNING,
                            String.format ("No se pudo abrir el archivo %s para comprobar su tipo de contenido.",
                                    f.getAbsolutePath ()));

                    return false;
                }
            }
        });
        this.setFileFilter (f);
    }
}