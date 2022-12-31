package internals.swing;

import java.awt.Component;
import java.io.File;
import java.util.Locale;

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
                return f.isDirectory () || f.getName ().toLowerCase ().endsWith (".json");
            }
        });
        this.setFileFilter (f);
    }

    @Override
    public int showSaveDialog (Component parent) {
        int r = super.showSaveDialog (parent);

        if (r != JFileChooser.APPROVE_OPTION)
            return r;

        if (!this.getSelectedFile ().getName ().endsWith (".json"))
            this.setSelectedFile (new File (this.getSelectedFile ().getAbsolutePath () + ".json"));

        return r;
    }
}
