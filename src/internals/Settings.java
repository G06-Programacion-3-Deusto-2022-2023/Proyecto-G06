package internals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Settings {
    private static final String SETTINGS_PATH = "data/settings/settings.properties";
    private static final String DEFAULT_SETTINGS_PATH = "data/default/settings/settings.properties";
    private static final String COMMENT = "AVISO: ESTE ARCHIVO SOLO DEBE SER MODIFICADO DESDE EL PROGRAMA.";
    private static final String DEFAULT_LOGO_PATH = "data/assets/logo.png";
    private static final String DEFAULT_LOGO_URL = "https://w7.pngwing.com/pngs/130/1021/png-transparent-movie-logo-movie-logo-film-tape-cinema.png";
    private static Properties properties = new Properties ();
    private static final Properties defaults = ((Supplier <Properties>) ( () -> {
        Properties p = new Properties ();

        p.setProperty ("nombre", "DeustoCines");
        p.setProperty ("logo", Settings.DEFAULT_LOGO_PATH);
        p.setProperty ("precioentrada", "7.9");
        p.setProperty ("diaespectador", "3");
        p.setProperty ("descuentoespectador", "20");

        Settings.properties = new Properties (p);

        if (!new File (Settings.DEFAULT_SETTINGS_PATH).exists ())
            Settings.save (p, Settings.DEFAULT_SETTINGS_PATH);

        if (!new File (Settings.SETTINGS_PATH).exists ())
            Settings.save ();

        File f;
        try {
            if (!(f = new File (p.getProperty ("logo"))).exists ()
                    || !Files.probeContentType (f.toPath ()).equals ("image/png"))
                Settings.downloadDefaultLogo ();
        }

        catch (IOException e) {
            Settings.downloadDefaultLogo ();
        }

        return p;
    })).get ();

    private Settings () {
    }

    public static Properties defaults () {
        return new Properties (Settings.defaults);
    }

    public static void restoreDefaults () {
        properties = new Properties (defaults);
        Settings.downloadDefaultLogo ();
    }

    public static void load () {
        Properties temp = new Properties ();

        try (FileInputStream fis = new FileInputStream (new File (Settings.SETTINGS_PATH))) {
            temp.load (fis);
        }

        catch (IOException | IllegalArgumentException e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING, String.format (
                    "No pudo cargarse la configuración del programa desde el archivo %s.", Settings.SETTINGS_PATH));

            Settings.properties = new Properties (Settings.defaults);

            return;
        }

        List <String> l = (List <String>) Collections.list (Settings.defaults.propertyNames ());
        for (int i = 0; i < l.size (); i++)
            Settings.properties.setProperty (temp.getProperty (l.get (i)), Settings.defaults.getProperty (l.get (i)));
    }

    public static void save () {
        Settings.save (Settings.properties, Settings.SETTINGS_PATH);
    }

    private static void save (Properties properties, String file) {
        File f = new File (file);
        try {
            Files.createDirectories (f.getParentFile ().toPath ());
        }

        catch (IOException e1) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING,
                    String.format ("La configuración no pudo guardarse en el archivo %s.", file));

            return;
        }

        try (FileOutputStream fos = new FileOutputStream (f)) {
            properties.store (fos, Settings.COMMENT);
        }

        catch (IOException | ClassCastException e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING,
                    String.format ("La configuración no pudo guardarse en el archivo %s.", file));
        }
    }

    public static String getNombre () {
        return Settings.properties.getProperty ("nombre");
    }

    public static void setNombre () {
        Settings.properties.setProperty ("nombre", Settings.defaults.getProperty ("nombre"));
    }

    public static void setNombre (String nombre) throws NullPointerException, IllegalArgumentException {
        if (nombre == null)
            throw new NullPointerException ("No se puede cambiar el nombre del cine usando un string nulo.");

        if (nombre.replace (" ", "").equals (""))
            throw new IllegalArgumentException ("No se puede cambiar el nombre del cine por el de un string vacío");

        Settings.properties.setProperty ("nombre", nombre);
    }

    public static String getLogo () {
        return Settings.properties.getProperty ("logo");
    }

    public static void setLogo () {
        Settings.properties.setProperty ("logo", Settings.defaults.getProperty ("logo"));
        Settings.downloadDefaultLogo ();
    }

    public static void setLogo (String logo) throws NullPointerException, IllegalArgumentException {
        if (logo == null)
            throw new NullPointerException ("No se puede cambiar el logo del cine usando un string nulo");

        File f;
        try {
            Files.createDirectories (new File ("data/assets").toPath ());
        }

        catch (IOException e1) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING,
                    "No se pudo crear el directorio de assets.");

            return;
        }

        try {
            if (!(f = new File (logo)).exists () || !Files.probeContentType (f.toPath ()).equals ("image/png"))
                throw new IllegalArgumentException (String.format (
                        "El archivo especificado no es un archivo, %s, válido como logo: debe ser un archivo PNG válido.",
                        logo));
        }

        catch (IOException e) {
            throw new IllegalArgumentException (String.format (
                    "El archivo especificado no es un archivo, %s, válido como logo: debe ser un archivo PNG válido.",
                    logo));
        }

        try {
            Files.copy (f.toPath (), new File (Settings.defaults.getProperty ("logo")).toPath ());
        }

        catch (IOException e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING, "No se pudo copiar el logo especificado.");

            Settings.setLogo ();
        }
    }

    private static void downloadDefaultLogo () {
        try {
            Utils.downloadFile (Settings.DEFAULT_LOGO_PATH, Settings.DEFAULT_LOGO_URL);
        }

        catch (NullPointerException | IOException e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING,
                    String.format ("No pudo descargarse el logo por defecto del cine desde %s.",
                            Settings.DEFAULT_LOGO_URL));
        }
    }

    public static BigDecimal getPrecioEntrada () {
        return new BigDecimal (Settings.properties.getProperty ("precioentrada"));
    }

    public static void setPrecioEntrada () {
        Settings.properties.setProperty ("precioentrada", Settings.defaults.getProperty ("precioentrada"));
    }

    public static void setPrecioEntrada (Number n) throws NullPointerException, IllegalArgumentException {
        if (n == null)
            throw new NullPointerException ("No se puede establecer el precio de las entradas usando un número nulo.");

        if (n.doubleValue () <= 0)
            throw new IllegalArgumentException ("El precio de las entradas debe ser un número positivo.");

        Settings.properties.setProperty ("precioentrada", String.format ("%.2f", n.doubleValue ()));
    }

    public static int getDiaEspectador () {
        return Integer.parseInt (Settings.properties.getProperty ("diaespectador"));
    }

    public static void setDiaEspectador () {
        Settings.properties.setProperty ("diaespectador", Settings.defaults.getProperty ("diaespectador"));
    }

    public static void setDiaEspectador (int d) throws IllegalArgumentException {
        if (d < 0 || d > 6)
            throw new IllegalArgumentException ("El día de la semana debe ser un número entero entre el 0 y el 6.");

        Settings.properties.setProperty ("diaespectador", Integer.toString (d));
    }

    public static int getDescuento () {
        return Integer.parseInt (Settings.properties.getProperty ("descuentoespectador"));
    }

    public static void setDescuentoEspectador () {
        Settings.properties.setProperty ("descuentoespectador", Settings.defaults.getProperty ("descuentoespectador"));
    }

    public static void setDescuentoEspectador (int d) throws IllegalArgumentException {
        if (d < 0 || d >= 100)
            throw new IllegalArgumentException ("El descuento debe ser un número entero en el intervalo [0, 100).");

        Settings.properties.setProperty ("descuentoespectador", Integer.toString (d));
    }
}
