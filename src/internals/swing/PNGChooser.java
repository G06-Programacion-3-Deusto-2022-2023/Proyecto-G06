package internals.swing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class PNGChooser extends JFileChooser {
    public PNGChooser () {
        super ();

        this.setAcceptAllFileFilterUsed (false);
        this.setFileFilter (new FileFilter () {
            @Override
            public String getDescription () {
                return Locale.getDefault ().getLanguage ().equalsIgnoreCase ("es") ? "Archivos PNG (*.png)"
                        : "PNG files (*.png)";
            }

            @Override
            public boolean accept (File f) {
                try {
                    return f.isDirectory () || (f.getName ().toLowerCase ().endsWith (".png")
                            && Files.probeContentType (f.toPath ()).equals ("image/png"));
                }

                catch (NullPointerException | IOException e) {
                    Logger.getLogger (ImageChooser.class.getName ()).log (Level.WARNING,
                            String.format ("No se pudo abrir el archivo %s para comprobar su tipo de contenido.",
                                    f.getAbsolutePath ()));

                    return false;
                }
            }
        });
    }
}
