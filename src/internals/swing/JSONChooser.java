package internals.swing;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class JSONChooser extends JFileChooser {
    public JSONChooser () {
        super ();

        FileFilter f;
        this.addChoosableFileFilter (f = new FileFilter () {
            @Override
            public String getDescription () {
                return Locale.getDefault ().getLanguage ().equalsIgnoreCase ("es") ? "Archivos JSON (*.json)"
                        : "JSON files (*.json)";
            }

            @Override
            public boolean accept (File f) {
                try {
                    return f.isDirectory () || (f.getName ().toLowerCase ().endsWith (".json")
                            && Files.probeContentType (f.toPath ()).equals ("application/json"));
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

    @Override
    public int showSaveDialog (Component parent) {
        int r = super.showSaveDialog (parent);

        if (r != JFileChooser.APPROVE_OPTION)
            return r;

        if (!this.getSelectedFile ().getName ().toLowerCase ().endsWith (".json"))
            this.setSelectedFile (new File (this.getSelectedFile ().getAbsolutePath () + ".json"));

        return r;
    }
}
