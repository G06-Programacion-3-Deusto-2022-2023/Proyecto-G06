package internals;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cine.SetPeliculas;

public final class Settings {
    private static final String SETTINGS_PATH = "data/settings/settings.properties";
    private static final String DEFAULT_SETTINGS_PATH = "data/default/settings/settings.properties";
    private static final String COMMENT = "AVISO: ESTE ARCHIVO SOLO DEBE SER MODIFICADO DESDE EL PROGRAMA.";
    private static final String DEFAULT_LOGO_PATH = "data/assets/logo.png";
    private static final String DEFAULT_LOGO_URL = "https://clipartmag.com/images/movie-reel-logo-17.png";
    private static final String ACTIVE_SET_PATH = "data/serialized/activeset.json";
    private static Properties properties;
    private static final Properties defaults = ((Supplier <Properties>) ( () -> {
        Properties p = new Properties ();

        p.setProperty ("nombre", "DeustoCines");
        p.setProperty ("logo", Settings.DEFAULT_LOGO_PATH);
        p.setProperty ("precioentrada", "7.9");
        p.setProperty ("diaespectador", "2");
        p.setProperty ("descuentoespectador", "20");
        p.setProperty ("fallbackseatrenderer", Boolean.FALSE.toString ());
        p.setProperty ("adminkey", "proyecto06");

        if (!new File (Settings.ACTIVE_SET_PATH).exists ())
            Settings.setActiveSet ();

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

        Properties temp = new Properties (p);
        try (FileInputStream fis = new FileInputStream (new File (Settings.SETTINGS_PATH))) {
            temp.load (fis);
        }

        catch (IOException | IllegalArgumentException e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING, String.format (
                    "No pudo cargarse la configuración del programa desde el archivo %s.", Settings.SETTINGS_PATH));

            Settings.properties = new Properties (p);

            return p;
        }

        List <String> l = Collections.list (p.keys ()).stream ().map (Object::toString)
                .collect (Collectors.toList ());
        for (int i = 0; i < l.size (); i++)
            Settings.properties.setProperty (l.get (i), temp.getProperty (l.get (i)));

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
        Properties temp = new Properties (Settings.defaults);

        try (FileInputStream fis = new FileInputStream (new File (Settings.SETTINGS_PATH))) {
            temp.load (fis);
        }

        catch (IOException | IllegalArgumentException e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING, String.format (
                    "No pudo cargarse la configuración del programa desde el archivo %s.", Settings.SETTINGS_PATH));

            Settings.properties = new Properties (Settings.defaults);

            return;
        }

        List <String> l = Collections.list (Settings.defaults.keys ()).stream ().map (Object::toString)
                .collect (Collectors.toList ());
        for (int i = 0; i < l.size (); i++)
            Settings.properties.setProperty (l.get (i), temp.getProperty (l.get (i)));
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

    public static String getAdminKey () {
        return Settings.properties.getProperty ("adminkey");
    }

    public static void setAdminKey () {
        Settings.properties.setProperty ("adminkey", Settings.defaults.getProperty ("adminkey"));
    }

    public static void setAdminKey (String key) throws NullPointerException, IllegalArgumentException {
        if (key == null)
            throw new NullPointerException ("No se puede cambiar la clave de administrador usando un string nulo.");

        if (!Utils.isValidAdminKey (key))
            throw new IllegalArgumentException (
                    "Se han encontrado carácteres ilegales en la candidata a clave de administrador.");

        Settings.properties.setProperty ("adminkey", key);
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

        if (nombre.strip ().equals (""))
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
            Files.copy (f.toPath (), new File (Settings.defaults.getProperty ("logo")).toPath (),
                    StandardCopyOption.REPLACE_EXISTING);
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
        try {
            return new BigDecimal (Settings.properties.getProperty ("precioentrada").replace (",", ".")).setScale (2,
                    RoundingMode.HALF_EVEN);
        }

        catch (NumberFormatException e) {
            return new BigDecimal (Settings.defaults.getProperty ("precioentrada").replace (",", ".")).setScale (2,
                    RoundingMode.HALF_EVEN);
        }
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
        try {
            return Integer.parseInt (Settings.properties.getProperty ("diaespectador"));
        }

        catch (NumberFormatException e) {
            return Integer.parseInt (Settings.defaults.getProperty ("descuentoespectador"));
        }
    }

    public static void setDiaEspectador () {
        Settings.properties.setProperty ("diaespectador", Settings.defaults.getProperty ("diaespectador"));
    }

    public static void setDiaEspectador (int d) throws IllegalArgumentException {
        if (d < 0 || d > 6)
            throw new IllegalArgumentException ("El día de la semana debe ser un número entero entre el 0 y el 6.");

        Settings.properties.setProperty ("diaespectador", Integer.toString (d));
    }

    public static int getDescuentoEspectador () {
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

    public static boolean usingFallbackRenderer () {
        return Boolean.parseBoolean (Settings.properties.getProperty ("fallbackseatrenderer"));
    }

    public static void useFallbackRenderer (boolean fallbackrenderer) {
        Settings.properties.setProperty ("fallbackseatrenderer", Boolean.toString (fallbackrenderer));
    }

    public static String getActiveSetPath () {
        return Settings.ACTIVE_SET_PATH;
    }

    public static SetPeliculas getActiveSet () {
        if (!new File (Settings.ACTIVE_SET_PATH).exists ())
            return SetPeliculas.getDefault ();

        try {
            SetPeliculas s = SetPeliculas.fromJSON (new File (Settings.ACTIVE_SET_PATH)).stream ().findFirst ()
                    .orElse (SetPeliculas.getDefault ());

            if (s.size () < SetPeliculas.minSize () || s.size () > SetPeliculas.maxSize ()) {
                Settings.setActiveSet ();

                return SetPeliculas.getDefault ();
            }

            return s;
        }

        catch (Exception e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING, String.format (
                    "No se ha podido obtener el set de películas activo del archivo %s, por lo que se usará el set por defecto.",
                    new File (Settings.ACTIVE_SET_PATH).getAbsolutePath ()));

            return SetPeliculas.getDefault ();
        }
    }

    public static void setActiveSet () {
        Settings.setActiveSet (SetPeliculas.getDefault ());
    }

    public static void setActiveSet (SetPeliculas setPeliculas) throws NullPointerException {
        if (setPeliculas == null)
            throw new NullPointerException (
                    "No se puede establecer un set de películas nulo como el set de películas activo.");

        File f = new File (Settings.ACTIVE_SET_PATH);
        try {
            Files.createDirectories (f.getParentFile ().toPath ());
        }

        catch (FileAlreadyExistsException e) {
            try {
                Files.delete (f.toPath ());
            }

            catch (IOException e1) {
                Logger.getLogger (Settings.class.getName ()).log (Level.WARNING,
                        "No pudo crearse la estructura de directorios del set activo.");
                e.printStackTrace ();

                return;
            }
        }

        catch (IOException e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING,
                    "No pudo crearse la estructura de directorios del set activo.");
            e.printStackTrace ();

            return;
        }

        try (BufferedOutputStream bos = new BufferedOutputStream (
                new FileOutputStream (f))) {
            bos.write (SetPeliculas.toJSON (((Supplier <SetPeliculas>) ( () -> {
                SetPeliculas sp = new SetPeliculas (setPeliculas);
                sp.setAdministrador (null);

                return sp;
            })).get (), true).getBytes ());
        }

        catch (IOException e) {
            Logger.getLogger (Settings.class.getName ()).log (Level.WARNING,
                    String.format ("No pudo escribirse el set de películas con ID %s en el archivo %s: %s",
                            setPeliculas.getId (), f.getAbsolutePath (),
                            e.getMessage ()));
            e.printStackTrace ();
        }
    }
}
