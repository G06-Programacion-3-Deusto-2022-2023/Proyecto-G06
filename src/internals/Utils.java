package internals;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.passay.EnglishCharacterData;

public final class Utils {
    private Utils () {
    }

    private static final int STACK_DEPTH = 25;

    public static boolean isAmongstCallers (String str) {
        return isAmongstCallers (str, Thread.currentThread ().getStackTrace ());
    }

    public static boolean isAmongstCallers (String str, StackTraceElement stt[]) {
        for (int i = 3; i <= Utils.STACK_DEPTH && i < stt.length;)
            if (stt [i++].getClassName ().equals (str))
                return true;

        return false;
    }

    public static void downloadFile (String file, String url) throws NullPointerException, IOException {
        Utils.downloadFile (file, url, false);
    }

    public static void downloadFile (String file, String url, boolean replace)
            throws NullPointerException, IOException {
        URL url2 = null;
        try {
            url2 = new URL (url);
        }

        catch (MalformedURLException e) {
            throw new MalformedURLException (String.format ("No se pudo crear una URL a partir de %s", url));
        }

        Utils.downloadFile (file, url2, replace);
    }

    public static void downloadFile (String file, URL url) throws NullPointerException, IOException {
        Utils.downloadFile (file, url, false);
    }

    public static void downloadFile (String file, URL url, boolean replace)
            throws NullPointerException, IOException {
        if (file == null)
            throw new NullPointerException ("No se puede descargar un archivo a un directorio nulo");

        if (url == null)
            throw new NullPointerException ("No se puede descargar un archivo a partir de una URL nula");

        if (!Arrays.asList ("http", "https", "ftp").contains (url.getProtocol ()))
            throw new MalformedURLException ("Solo se admiten archivos y URLs de protocolo HTTP, HTTPS y FTP.");

        File f;
        if ((f = new File (file)).exists () && !f.isDirectory () && !replace)
            return;

        try {
            Files.createDirectories (f.isDirectory () ? f.toPath ()
                    : f.getParentFile ().toPath ());
        }

        catch (FileAlreadyExistsException e) {
            throw new FileAlreadyExistsException (String.format ("La ruta %s ya existe y no es un directorio.", file));
        }

        catch (IOException e) {
            throw new IOException ("No se pudo crear el directorio especificado.");
        }

        String filename = f.isDirectory ()
                ? String.format ("%s%c%s", file, File.separatorChar, new File (url.getFile ()).getName ())
                : file;

        try (FileOutputStream fos = new FileOutputStream (new File (filename));
                ReadableByteChannel c = Channels
                        .newChannel (url.openStream ())) {
            fos.getChannel ().transferFrom (c, 0, Long.MAX_VALUE);
        }

        catch (Exception e) {
            throw new IOException (
                    String.format ("Hubo un error al descargar el archivo desde %s a %s", url.toString (), filename));
        }
    }

    public static Triplet <Integer, Integer, Integer> getDate () {
        return Utils.getDate (Calendar.getInstance ());
    }

    public static Triplet <Integer, Integer, Integer> getDate (Calendar c) {
        return new Triplet <Integer, Integer, Integer> (c.get (Calendar.DAY_OF_MONTH), c.get (Calendar.MONTH) + 1,
                c.get (Calendar.YEAR));
    }

    public static int getCurrentDay () {
        return Utils.getCurrentDay (true);
    }

    public static int getCurrentDay (boolean mondayFirst) {
        int d = Calendar.getInstance ().get (Calendar.DAY_OF_WEEK);
        return mondayFirst ? (d - 1 == -1 ? 6 : d - 1) : d;
    }

    public static boolean isDiaDelEspectador () {
        return Utils.getCurrentDay () == Settings.getDiaEspectador ();
    }

    public static void copyToClipboard (String s) throws NullPointerException {
        if (s == null)
            throw new NullPointerException ("No se puede pasar un string nulo al método Utils.copyToClipboard.");

        StringSelection selection = new StringSelection (s);
        Toolkit.getDefaultToolkit ().getSystemClipboard ().setContents (selection, selection);
    }

    public static boolean isValidAdminKey (String key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException (
                    "No se puede analizar una clave de administrador consistente en un string nulo.");

        return ((Supplier <Set <Character>>) ( () -> {
            Set <Character> s = key.codePoints ().mapToObj (e -> Character.valueOf ((char) e))
                    .collect (Collectors.toSet ());

            s.removeAll (EnglishCharacterData.LowerCase.getCharacters ().codePoints ()
                    .mapToObj (e -> Character.valueOf ((char) e)).collect (Collectors.toSet ()));
            s.removeAll (EnglishCharacterData.UpperCase.getCharacters ().codePoints ()
                    .mapToObj (e -> Character.valueOf ((char) e)).collect (Collectors.toSet ()));
            s.removeAll (EnglishCharacterData.Digit.getCharacters ().codePoints ()
                    .mapToObj (e -> Character.valueOf ((char) e)).collect (Collectors.toSet ()));
            s.removeAll (CustomCharacterData.Special.getCharacters ().codePoints ()
                    .mapToObj (e -> Character.valueOf ((char) e)).collect (Collectors.toSet ()));

            return s;
        })).get ().isEmpty ();
    }
}
