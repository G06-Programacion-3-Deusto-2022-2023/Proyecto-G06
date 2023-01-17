package internals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cine.Administrador;
import cine.Complemento;
import cine.EdadRecomendada;
import cine.Entrada;
import cine.Espectador;
import cine.Genero;
import cine.Pelicula;
import cine.Sala;
import cine.SetPeliculas;
import cine.Usuario;

public class GestorBD {
    private static final String DRIVER_NAME = "org.sqlite.JDBC";
    private static final String DATABASE_FILE = "data/db/database.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + GestorBD.DATABASE_FILE;
    private static final Pair <String, Pair <String, String> []> TABLES[] = new Pair [] {
            new Pair <String, Pair <String, String> []> ("PELICULA", new Pair [] {
                    new Pair <String, String> ("ID_PELICULA", "'%s'"),
                    new Pair <String, String> ("NOMBRE_PELICULA", "'%s'"),
                    new Pair <String, String> ("RUTA_IMAGEN", "'%s'"),
                    new Pair <String, String> ("VALORACION", "%s"),
                    new Pair <String, String> ("FECHA", "%d"),
                    new Pair <String, String> ("DIRECTOR", "'%s'"),
                    new Pair <String, String> ("DURACION", "%d"),
                    new Pair <String, String> ("EDAD_RECOMENDADA", "%d"),
                    new Pair <String, String> ("GENEROS", "%d")
            }),
            new Pair <String, Pair <String, String> []> ("ADMINISTRADOR", new Pair [] {
                    new Pair <String, String> ("ID_ADMINISTRADOR", "'%s'"),
                    new Pair <String, String> ("NOMBRE_ADMINISTRADOR", "'%s'"),
                    new Pair <String, String> ("CONTRASENA_ADMINISTRADOR", "'%s'")
            }),
            new Pair <String, Pair <String, String> []> ("ESPECTADOR", new Pair [] {
                    new Pair <String, String> ("ID_ESPECTADOR", "'%s'"),
                    new Pair <String, String> ("NOMBRE_ESPECTADOR", "'%s'"),
                    new Pair <String, String> ("CONTRASENA_ESPECTADOR", "'%s'"),
                    new Pair <String, String> ("EDAD", "%d")
            }),
            new Pair <String, Pair <String, String> []> ("COMPLEMENTO", new Pair [] {
                    new Pair <String, String> ("ID_COMPLEMENTO", "'%s'"),
                    new Pair <String, String> ("NOMBRE_COMPLEMENTO", "'%s'"),
                    new Pair <String, String> ("PRECIO", "'%.2f'"),
                    new Pair <String, String> ("DESCUENTO", "%d")
            }),
            new Pair <String, Pair <String, String> []> ("SETPELICULAS", new Pair [] {
                    new Pair <String, String> ("ID_SETPELICULAS", "'%s'"),
                    new Pair <String, String> ("NOMBRE_ADMINISTRADOR", "'%s'"),
                    new Pair <String, String> ("NOMBRE_SETPELICULAS", "'%s'")
            }),
            new Pair <String, Pair <String, String> []> ("ARRAY_SETPELICULAS", new Pair [] {
                    new Pair <String, String> ("ID_PELICULA", "'%s'"),
                    new Pair <String, String> ("ID_SETPELICULAS", "'%s'")
            }),
            new Pair <String, Pair <String, String> []> ("ENTRADA", new Pair [] {
                    new Pair <String, String> ("ID_ENTRADA", "'%s'"),
                    new Pair <String, String> ("ID_ESPECTADOR", "'%s'"),
                    new Pair <String, String> ("ID_PELICULA", "'%s'"),
                    new Pair <String, String> ("FECHA", "'%s'"),
                    new Pair <String, String> ("SALA", "%d"),
                    new Pair <String, String> ("BUTACA", "%d"),
                    new Pair <String, String> ("VALORACION", "%s"),
                    new Pair <String, String> ("PRECIO", "%s")
            }),
            new Pair <String, Pair <String, String> []> ("ARRAY_ENTRADA", new Pair [] {
                    new Pair <String, String> ("ID_ENTRADA", "'%s'"),
                    new Pair <String, String> ("ID_COMPLEMENTO", "'%s'"),
                    new Pair <String, String> ("NOMBRE_COMPLEMENTO", "'%s'"),
                    new Pair <String, String> ("PRECIO", "'%.2f'"),
                    new Pair <String, String> ("DESCUENTO", "%d"),
                    new Pair <String, String> ("CANTIDAD", "%d")
            }),
            new Pair <String, Pair <String, String> []> ("LLAVE", new Pair [] {
                    new Pair <String, String> ("LLAVE", "'%s'")
            })
    };

    private static boolean FILE_LOCKED;
    private static final boolean WINPERMS[] = new boolean [4];
    private static final String DEFAULT_UNIX_PERMS = "rwxr-xr-x";
    private static Pair <Boolean, String> UNIXPERMS = new Pair <Boolean, String> (false,
            GestorBD.DEFAULT_UNIX_PERMS);

    public GestorBD () {
        try {
            // Cargar el diver SQLite
            Class.forName (GestorBD.DRIVER_NAME);
        }

        catch (ClassNotFoundException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                    String.format ("* Error al cargar el driver de BBDD: %s", e.getMessage ()));
            e.printStackTrace ();

            Thread.currentThread ().interrupt ();
        }
    }

    public static File getFile () {
        return new File (GestorBD.DATABASE_FILE);
    }

    public static String getFields (int table) throws IllegalArgumentException {
        if (table < 0 || table >= GestorBD.TABLES.length)
            throw new IllegalArgumentException (
                    String.format ("El índice de la tabla debe estar entre 0 y %d",
                            GestorBD.TABLES.length - 1));

        return String.join (", ", (((Supplier <String []>) () -> {
            List <String> l = new ArrayList <String> ();
            for (int i = 0; i < GestorBD.TABLES [table].y.length; l.add (GestorBD.TABLES [table].y [i++].x))
                ;

            return l.toArray (new String [0]);
        })).get ());
    }

    public static String getFieldFormats (int table) throws IllegalArgumentException {
        if (table < 0 || table >= GestorBD.TABLES.length)
            throw new IllegalArgumentException (
                    String.format ("El índice de la tabla debe estar entre 0 y %d",
                            GestorBD.TABLES.length - 1));

        return String.join (", ", (((Supplier <String []>) () -> {
            List <String> l = new ArrayList <String> ();
            for (int i = 0; i < GestorBD.TABLES [table].y.length; l.add (GestorBD.TABLES [table].y [i++].y))
                ;

            return l.toArray (new String [0]);
        })).get ());
    }

    public static String insertIntoStatement (int table) throws IllegalArgumentException {
        if (table < 0 || table >= GestorBD.TABLES.length)
            throw new IllegalArgumentException (
                    String.format ("El índice de la tabla debe estar entre 0 y %d",
                            GestorBD.TABLES.length - 1));

        return String.format ("INSERT INTO %s (%s) VALUES (%s)", GestorBD.TABLES [table].x,
                GestorBD.getFields (table),
                GestorBD.getFieldFormats (table));
    }

    public static String updateStatement (int table) throws IllegalArgumentException {
        if (table < 0 || table >= GestorBD.TABLES.length)
            throw new IllegalArgumentException (
                    String.format ("El índice de la tabla debe estar entre 0 y %d",
                            GestorBD.TABLES.length - 2));

        StringBuilder str = new StringBuilder (String.format ("UPDATE %s SET ", GestorBD.TABLES [table].x));
        for (int i = 1; i < GestorBD.TABLES [table].y.length; i++)
            str.append (String.format ("%s = %s, ", GestorBD.TABLES [table].y [i].x,
                    GestorBD.TABLES [table].y [i].y));

        str.delete (str.lastIndexOf (", "), str.length () - 1);

        return str.toString ()
                + String.format ("WHERE %s = %s;", GestorBD.TABLES [table].y [0].x,
                        GestorBD.TABLES [table].y [0].y);
    }

    public static void lock () {
        if (GestorBD.FILE_LOCKED)
            return;

        File f;
        if (!(f = new File (GestorBD.DATABASE_FILE)).exists ())
            return;

        if (System.getProperty ("os.name").startsWith ("Windows") && !GestorBD.WINPERMS [0]) {
            GestorBD.WINPERMS [1] = f.canRead ();
            GestorBD.WINPERMS [2] = f.canExecute ();
            GestorBD.WINPERMS [3] = f.canWrite ();
        }

        else if (Boolean.FALSE.equals (GestorBD.UNIXPERMS.x))
            GestorBD.UNIXPERMS = new Pair <Boolean, String> (true, ((Supplier <String>) ( () -> {
                Process p;
                try {
                    // Sí, usar comandos es una guarrada, lo
                    // sé
                    p = Runtime.getRuntime ()
                            .exec (String.format ("stat -c \"%%A\" %s",
                                    f.getAbsolutePath ()));
                }

                catch (IOException e) {
                    Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                            "No se pudieron obtener los permisos originales del archivo de base de datos.");

                    return GestorBD.DEFAULT_UNIX_PERMS;
                }

                try (BufferedReader r = new BufferedReader (
                        new InputStreamReader (p.getInputStream ()))) {
                    p.waitFor ();
                    return r.readLine ().replaceFirst ("-", "").replace ("\"", "");
                }

                catch (IOException | InterruptedException e) {
                    Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                            "No se pudieron obtener los permisos originales del archivo de base de datos.");

                    if (e instanceof InterruptedException)
                        Thread.currentThread ().interrupt ();

                    return GestorBD.DEFAULT_UNIX_PERMS;
                }
            })).get ());

        try {
            if (System.getProperty ("os.name").startsWith ("Windows") && !f.setReadOnly ())
                throw new IOException ("No se pudo bloquear el archivo de base de datos.");

            if (!System.getProperty ("os.name").startsWith ("Windows")) {
                Files.setPosixFilePermissions (f.toPath (),
                        PosixFilePermissions.fromString ("r--r--r--"));

                try {
                    // Sé que usar sudo en un programa así
                    // como así es una cosa
                    // bastante insegura, pero tampoco
                    // estamos jugándonos la
                    // vida con este programa.
                    Process p = new ProcessBuilder ("sh", "-c",
                            String.format ("[ -z $(lsattr %s | grep \"i\") ] && chattr +i %s || true",
                                    f.getAbsolutePath (), f.getAbsolutePath ()))
                                            .start ();
                    p.waitFor ();
                    if (p.exitValue () != 0)
                        throw new IOException (
                                "No se pudo aplicar el flag de inmutabilidad al archivo de base de datos.");
                }

                catch (IOException e) {
                    Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                            "No se pudo aplicar el flag de inmutabilidad al archivo de base de datos.");
                }

                catch (InterruptedException e) {
                    e.printStackTrace ();
                    Thread.currentThread ().interrupt ();
                }
            }
        }

        catch (IOException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                    "No se pudo bloquear el archivo de base de datos.");

            return;
        }

        GestorBD.FILE_LOCKED = true;
    }

    public static void unlock () {
        File f;
        if (!(f = new File (GestorBD.DATABASE_FILE)).exists ())
            return;

        if (System.getProperty ("os.name").startsWith ("Windows")) {
            try {
                if ((GestorBD.WINPERMS [0] = !(f.setReadable (GestorBD.WINPERMS [1])
                        || !f.setExecutable (GestorBD.WINPERMS [2])
                        || !f.setExecutable (GestorBD.WINPERMS [3]))))
                    throw new IOException ("No se pudo desbloquear el archivo de base de datos.");
            }

            catch (IOException e) {
                Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                        "No se pudo desbloquear el archivo de base de datos.");

                return;
            }

            GestorBD.FILE_LOCKED = false;

            return;
        }

        try {
            Process p = new ProcessBuilder ("sh", "-c",
                    String.format ("[ ! -z $(lsattr %s | grep \"i\") ] && chattr -i %s || true",
                            f.getAbsolutePath (),
                            f.getAbsolutePath ())).start ();
            p.waitFor ();
            if (p.exitValue () != 0)
                throw new IOException ("No se pudo desbloquear el archivo de base de datos.");
        }

        catch (IOException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    "No se pudo quitar el flag de inmutabilidad del archivo de base de datos.");
        }

        catch (InterruptedException e) {
            e.printStackTrace ();
            Thread.currentThread ().interrupt ();
        }

        try {
            Files.setPosixFilePermissions (f.toPath (),
                    PosixFilePermissions.fromString (GestorBD.UNIXPERMS.y));
        }

        catch (IOException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                    "No se pudo desbloquear el archivo de base de datos.");

            return;
        }

        GestorBD.FILE_LOCKED = false;
    }

    public void create () {
        GestorBD.unlock ();

        try {
            Files.createDirectories (new File (GestorBD.DATABASE_FILE).getParentFile ().toPath ());
        }

        catch (FileAlreadyExistsException e) {
            try {
                Files.delete (new File (GestorBD.DATABASE_FILE).toPath ());
            }

            catch (IOException e1) {
                Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                        "No pudo crearse la estructura de directorios del archivo de base de datos.");
                e.printStackTrace ();

                return;
            }
        }

        catch (IOException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                    "No pudo crearse la estructura de directorios del archivo de base de datos.");
            e.printStackTrace ();

            return;
        }

        // Se abre la conexión y se obtiene el Statement
        // Al abrir la conexión, si no existía el fichero, se crea la
        // base de datos
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql[] = new String [] {
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n", GestorBD.TABLES [0].x)
                            + String.format ("%s VARCHAR(36) PRIMARY KEY,%n",
                                    GestorBD.TABLES [0].y [0].x)
                            + String.format ("%s STRING,%n", GestorBD.TABLES [0].y [1].x)
                            + String.format ("%s STRING, %n", GestorBD.TABLES [0].y [2].x)
                            + String.format ("%s DECIMAL(3, 1) NOT NULL,%n",
                                    GestorBD.TABLES [0].y [3].x)
                            + String.format ("%s INTEGER,%n", GestorBD.TABLES [0].y [4].x)
                            + String.format ("%s STRING NOT NULL,%n",
                                    GestorBD.TABLES [0].y [5].x)
                            + String.format ("%s INTEGER NOT NULL,%n",
                                    GestorBD.TABLES [0].y [6].x)
                            + String.format ("%s INTEGER NOT NULL,%n",
                                    GestorBD.TABLES [0].y [7].x)
                            + String.format ("%s INTEGER NOT NULL%n",
                                    GestorBD.TABLES [0].y [8].x)
                            + ");",
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n", GestorBD.TABLES [1].x)
                            + String.format ("%s VARCHAR(36) PRIMARY KEY,%n",
                                    GestorBD.TABLES [1].y [0].x)
                            + String.format ("%s STRING,%n", GestorBD.TABLES [1].y [1].x)
                            + String.format ("%s STRING NOT NULL%n",
                                    GestorBD.TABLES [1].y [2].x)
                            + ");",
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n", GestorBD.TABLES [2].x)
                            + String.format ("%s VARCHAR(36) PRIMARY KEY,%n",
                                    GestorBD.TABLES [2].y [0].x)
                            + String.format ("%s STRING NOT NULL,%n",
                                    GestorBD.TABLES [2].y [1].x)
                            + String.format ("%s STRING NOT NULL,%n",
                                    GestorBD.TABLES [2].y [2].x)
                            + String.format ("%s INTEGER%n", GestorBD.TABLES [2].y [3].x)
                            + ");",
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n", GestorBD.TABLES [3].x)
                            + String.format ("%s VARCHAR(36) PRIMARY KEY,%n",
                                    GestorBD.TABLES [3].y [0].x)
                            + String.format ("%s STRING NOT NULL,%n",
                                    GestorBD.TABLES [3].y [1].x)
                            + String.format ("%s DECIMAL(65,2),%n",
                                    GestorBD.TABLES [3].y [2].x)
                            + String.format ("%s INTEGER%n", GestorBD.TABLES [3].y [3].x)
                            + ");",
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n", GestorBD.TABLES [4].x)
                            + String.format ("%s VARCHAR(36) PRIMARY KEY,%n",
                                    GestorBD.TABLES [4].y [0].x)
                            + String.format ("%s STRING,%n", GestorBD.TABLES [4].y [1].x)
                            + String.format ("%s STRING%n", GestorBD.TABLES [4].y [2].x)
                            + ");",
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n", GestorBD.TABLES [5].x)
                            + String.format ("%s VARCHAR(36) NOT NULL,%n",
                                    GestorBD.TABLES [5].y [0].x)
                            + String.format ("%s VARCHAR(36) NOT NULL,%n",
                                    GestorBD.TABLES [5].y [1].x)
                            + String.format ("PRIMARY KEY (%s, %s)%n",
                                    GestorBD.TABLES [5].y [0].x,
                                    GestorBD.TABLES [5].y [1].x)
                            + ");",
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n", GestorBD.TABLES [6].x)
                            + String.format ("%s VARCHAR(36) PRIMARY KEY,%n",
                                    GestorBD.TABLES [6].y [0].x)
                            + String.format ("%s VARCHAR(36),%n",
                                    GestorBD.TABLES [6].y [1].x)
                            + String.format ("%s VARCHAR(36),%n",
                                    GestorBD.TABLES [6].y [2].x)
                            + String.format ("%s STRING,%n", GestorBD.TABLES [6].y [3].x)
                            + String.format ("%s INTEGER CHECK (SALA BETWEEN -1 AND %d),%n",
                                    GestorBD.TABLES [6].y [4].x,
                                    Sala.getSalas ().size () - 1)
                            + String.format (
                                    "%s INTEGER CHECK (BUTACA BETWEEN -1 AND %d),%n",
                                    GestorBD.TABLES [6].y [5].x,
                                    Sala.getFilas () * Sala.getColumnas () - 1)
                            + String.format ("%s DECIMAL(3, 1),%n",
                                    GestorBD.TABLES [6].y [6].x)
                            + String.format ("%s DECIMAL(65, 2)%n",
                                    GestorBD.TABLES [6].y [7].x)
                            + ");",
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n",
                            GestorBD.TABLES [7].x)
                            + String.format ("%s VARCHAR(36) NOT NULL,%n",
                                    GestorBD.TABLES [7].y [0].x)
                            + String.format ("%s VARCHAR(36) NOT NULL,%n",
                                    GestorBD.TABLES [7].y [1].x)
                            + String.format ("%s STRING NOT NULL,%n",
                                    GestorBD.TABLES [7].y [2].x)
                            + String.format ("%s DECIMAL(65,2),%n",
                                    GestorBD.TABLES [7].y [3].x)
                            + String.format ("%s INTEGER,%n", GestorBD.TABLES [7].y [4].x)
                            + String.format ("%s INTEGER UNSIGNED NOT NULL,%n",
                                    GestorBD.TABLES [7].y [5].x)
                            + String.format ("PRIMARY KEY (%s, %s)%n",
                                    GestorBD.TABLES [7].y [0].x,
                                    GestorBD.TABLES [7].y [1].x)
                            + ");",
                    String.format ("CREATE TABLE IF NOT EXISTS %s (%n", GestorBD.TABLES [8].x)
                            + String.format ("%s STRING PRIMARY KEY%n",
                                    GestorBD.TABLES [8].y [0].x)
                            + ");"
            };

            boolean error;
            for (int i = 0; i < sql.length; i++) {
                error = stmt.execute (sql [i]);
                Logger.getLogger (GestorBD.class.getName ()).log (error ? Level.SEVERE : Level.INFO,
                        String.format ("%s la tabla %s.",
                                error ? "No pudo crearse" : "Creada",
                                GestorBD.TABLES [i].x));

                if (error)
                    throw new SQLException (String.format ("No pudo crearse la tabla %s",
                            GestorBD.TABLES [i].x));
            }
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                    String.format ("Error al crear la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();

            return;
        }

        this.insertDefault ();

        GestorBD.lock ();
    }

    public void remove () {
        GestorBD.unlock ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            boolean error;
            for (int i = 0; i < GestorBD.TABLES.length; i++) {
                error = stmt.execute (String.format ("DROP TABLE IF EXISTS %s", GestorBD.TABLES [i].x));

                Logger.getLogger (GestorBD.class.getName ()).log (error ? Level.SEVERE : Level.INFO,
                        String
                                .format ("%s la tabla %s",
                                        error ? "No se pudo eliminar"
                                                : "Eliminada",
                                        GestorBD.TABLES [i].x));
            }
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                    String.format ("Error al borrar la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();

            return;
        }

        try {
            // Se borra el fichero de la BBDD
            Files.delete (Paths.get (GestorBD.DATABASE_FILE));
            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    "Se ha borrado el archivo de la BBDD.");
        }

        catch (IOException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE, String.format (
                    "Error al borrar el archivo de la BBDD (%s): %s", GestorBD.DATABASE_FILE,
                    e.getMessage ()));
            e.printStackTrace ();
        }
    }

    public void insert (Collection <? extends HasID>... data) {
        List <HasID> l = new ArrayList <HasID> ();

        for (int i = 0; i < data.length; l.addAll (data [i++]))
            ;

        this.insert (l.toArray (new HasID [0]));
    }

    public void insert (HasID []... data) {
        List <HasID> l = new ArrayList <HasID> ();

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data [i].length; l.add (data [i] [j]))
                ;

        this.insert (l.toArray (new HasID [0]));
    }

    public void insert (HasID... data) {
        GestorBD.unlock ();

        for (int i[] = new int [1]; i [0] < data.length; i [0]++) {
            if (data [i [0]] == null)
                continue;

            new Runnable [] {
                    data [i [0]] instanceof Administrador
                            ? () -> this.insertAdministrador ((Administrador) data [i [0]])
                            : () -> {
                            },
                    data [i [0]] instanceof Complemento
                            ? () -> this.insertComplemento ((Complemento) data [i [0]])
                            : () -> {
                            },
                    data [i [0]] instanceof Entrada
                            ? () -> this.insertEntrada ((Entrada) data [i [0]])
                            : () -> {
                                this.insertArrayEntrada ((Entrada) data [i [0]]);
                            },
                    data [i [0]] instanceof Espectador
                            ? () -> this.insertEspectador ((Espectador) data [i [0]])
                            : () -> {
                            },
                    data [i [0]] instanceof Pelicula
                            ? () -> this.insertPelicula ((Pelicula) data [i [0]])
                            : () -> {
                            },
                    data [i [0]] instanceof SetPeliculas
                            ? () -> this.insertSetPelicula ((SetPeliculas) data [i [0]])
                            : () -> {
                                this.insertArraySetPelicula (
                                        (SetPeliculas) data [i [0]]);
                            }
            } [Arrays
                    .asList (Administrador.class, Complemento.class, Entrada.class,
                            Espectador.class, Pelicula.class,
                            SetPeliculas.class)
                    .indexOf (data [i [0]].getClass ())].run ();
        }

        GestorBD.lock ();
    }

    private void insertPelicula (Pelicula pelicula) {
        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            if (stmt
                    .executeUpdate (String.format (
                            GestorBD.insertIntoStatement (0),
                            pelicula.getId ().toString (), pelicula.getNombre (),
                            pelicula.getRutaImagen (),
                            Double.toString (pelicula.getValoracion ()).replace (",", "."),
                            pelicula.getFecha ().getValue (),
                            pelicula.getDirector (), pelicula.getDuracion ().toMinutes (),
                            pelicula.getEdad ().getValue (),
                            Genero.Nombre.toValor (pelicula.getGeneros ()))) == 1)
                Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                        String.format ("Pelicula insertada: %s", pelicula.toString ()));

            else
                Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                        String.format ("No se ha insertado la pelicula: %s",
                                pelicula.toString ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }
    }

    private void insertAdministrador (Administrador administrador) {
        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            if (stmt.executeUpdate (String.format (
                    GestorBD.insertIntoStatement (1), administrador.getId (),
                    administrador.getNombre (),
                    administrador.getContrasena ())) == 1)
                Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                        String.format ("Administrador insertado: %s",
                                administrador.toString ()));

            else
                Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                        String.format ("No se ha insertado el administrador: %s",
                                administrador.toString ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }
    }

    private void insertEspectador (Espectador espectador) {
        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            if (stmt.executeUpdate (
                    String.format (
                            GestorBD.insertIntoStatement (2),
                            espectador.getId (), espectador.getNombre (),
                            espectador.getContrasena (),
                            espectador.getEdad ())) == 1)
                Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                        String.format ("Espectador insertado: %s", espectador.toString ()));

            else
                Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                        String.format ("No se ha insertado el espectador: %s",
                                espectador.toString ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }
    }

    private void insertComplemento (Complemento complemento) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            if (stmt.executeUpdate (
                    String.format (
                            GestorBD.insertIntoStatement (3),
                            complemento.getId (), complemento.getNombre (),
                            complemento.getPrecio (),
                            complemento.getDescuento ())) == 1)
                Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                        String.format ("Complemento insertado: %s", complemento.toString ()));

            else
                Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                        String.format ("No se ha insertado el complemento: %s",
                                complemento.toString ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }
    }

    private void insertSetPelicula (SetPeliculas setPeliculas) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            this.insertArraySetPelicula (setPeliculas);

            if (stmt.executeUpdate (
                    String.format (GestorBD.insertIntoStatement (4), setPeliculas.getId (),
                            setPeliculas.getAdministrador () == null ? ""
                                    : setPeliculas.getAdministrador ().getNombre (),
                            setPeliculas.getNombre ())) == 1)

                Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                        String.format ("Set de películas insertado: %s",
                                setPeliculas.toString ()));

            else
                Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                        String.format ("No se ha insertado el set de películas: %s",
                                setPeliculas.toString ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }
    }

    private void insertArraySetPelicula (SetPeliculas setPeliculas) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {

            Pelicula p[] = setPeliculas.getPeliculas ().toArray (new Pelicula [0]);
            for (int i = 0; i < p.length; i++) {
                if (stmt.executeUpdate (String.format (
                        GestorBD.insertIntoStatement (5),
                        p [i].getId ().toString (), setPeliculas.getId ().toString ())) == 1)
                    Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                            (String.format ("Insertada la película con ID %s del set de películas con ID %s.",
                                    p [i].getId ().toString (),
                                    setPeliculas.getId ().toString ())));

                else
                    Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                            String.format ("No se ha insertado la película con ID %s del set de películas con ID %s.",
                                    p [i].getId ().toString (),
                                    setPeliculas.getId ().toString ()));
            }
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }
    }

    private void insertEntrada (Entrada entrada) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            this.insertArrayEntrada (entrada);

            stmt.executeUpdate (String.format (
                    GestorBD.insertIntoStatement (6), entrada.getId ().toString (),
                    entrada.getEspectador () == null ? ""
                            : entrada.getEspectador ().getId ().toString (),
                    entrada.getPelicula () == null ? ""
                            : entrada.getPelicula ().getId ().toString (),
                    new SimpleDateFormat ("yyyy-MM-dd").format (entrada.getFecha ().getTime ()),
                    entrada.getSala () == null ? -1 : Sala.indexOf (entrada.getSala ()),
                    entrada.getSala () == null || entrada.getButaca () == null ? -1
                            : ((IntSupplier) ( () -> {
                                Pair <Integer, Integer> i = entrada.getSala ()
                                        .indexOf (entrada.getButaca ());

                                return i.x == -1 || i.y == -1 ? -1
                                        : i.x * Sala.getColumnas () + i.y;
                            })).getAsInt (),
                    String.format ("%.1f",
                            ((Double) entrada.getValoracion ()).isNaN () || entrada.getValoracion () < 1.0f
                                    || entrada.getValoracion () > 10.0f ? 0
                                            : entrada.getValoracion ())
                            .replace (",", "."),
                    entrada.getPrecio ().toPlainString ().replace (",", ".")));

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Entrada insertada: %s", entrada.toString ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING, String.format (
                    "No pudo insertarse la entrada %s: %s", entrada.getId ().toString (),
                    e.getMessage ()));
            e.printStackTrace ();
        }
    }

    private void insertArrayEntrada (Entrada entrada) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            Map.Entry <Complemento, BigInteger> kv[] = entrada.getComplementos ().entrySet ()
                    .toArray (new Map.Entry [0]);
            for (int i = 0; i < kv.length; i++)
                stmt.executeUpdate (
                        String.format (GestorBD.insertIntoStatement (7),
                                entrada.getId ().toString (),
                                kv [i].getKey ().getId ().toString (),
                                kv [i].getKey ().getNombre (),
                                kv [i].getKey ().getPrecio (),
                                kv [i].getKey ().getDescuento (),
                                kv [i].getValue ()));

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se insertaron los datos de los complementos de la entrada con ID %s.",
                            entrada.getId ().toString ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("No pudieron insertarse los datos de los complementos de la entrada con ID %s: %s",
                            entrada.getId ().toString (), e.getMessage ()));
            e.printStackTrace ();
        }
    }

    private void createAdminKeys () {
        this.createAdminKeys (true);
    }

    private void createAdminKeys (boolean defaultKey) {
        GestorBD.unlock ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            List <String> keys = defaultKey ? Stream
                    .concat (Stream.of (Settings.getAdminKey ()),
                            Stream.generate (Usuario::generatePassword).limit (10))
                    .toList () : Stream.generate (Usuario::generatePassword).limit (10).toList ();

            for (int i = 0; i < keys.size (); i++) {
                if (1 == stmt.executeUpdate (
                        String.format (GestorBD.insertIntoStatement (8), keys.get (i))))
                    Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                            String.format ("Llave insertada: %s", keys.get (i)));

                else
                    Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                            String.format ("Llave no insertada: %s", keys.get (i)));
            }
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        GestorBD.lock ();
    }

    public void insertDefault () {
        this.insert (Complemento.getDefault ().stream ().collect (Collectors.toList ()),
                Collections.singleton (SetPeliculas.getDefault ()), Pelicula.getDefault ());

        this.createAdminKeys (true);
    }

    public void update (Collection <? extends HasID>... data) {
        List <HasID> l = new ArrayList <HasID> ();

        for (int i = 0; i < data.length; l.addAll (data [i++]))
            ;

        this.update (l.toArray (new HasID [0]));
    }

    public void update (HasID []... data) {
        List <HasID> l = new ArrayList <HasID> ();

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data [i].length; l.add (data [i] [j]))
                ;

        this.insert (l.toArray (new HasID [0]));
    }

    public void update (HasID... data) {
        GestorBD.unlock ();

        for (int i[] = new int [1]; i [0] < data.length; i [0]++) {
            if (data [i [0]] == null
                    || (data [i [0]] instanceof Pelicula && ((Pelicula) data [i [0]]).isDefault ())
                    || (data [i [0]] instanceof SetPeliculas
                            && ((SetPeliculas) data [i [0]]).isDefault ())
                    || (data [i [0]] instanceof Complemento
                            && ((Complemento) data [i [0]]).isDefault ()))
                continue;

            String strs[] = new String [] [] {
                    new String [] {
                            GestorBD.TABLES [1].x, "administrador", "administradores",
                            "el administrador",
                            String.format (
                                    GestorBD.updateStatement (1),
                                    data [i [0]] instanceof Administrador
                                            ? ((Administrador) data [i [0]])
                                                    .getNombre ()
                                            : "",
                                    data [i [0]] instanceof Administrador
                                            ? ((Administrador) data [i [0]])
                                                    .getContrasena ()
                                            : "",
                                    data [i [0]].getId ())
                    },
                    new String [] {
                            GestorBD.TABLES [3].x, "complemento", "complementos",
                            "el complemento", String.format (
                                    GestorBD.updateStatement (3),
                                    data [i [0]] instanceof Complemento
                                            ? ((Complemento) data [i [0]])
                                                    .getNombre ()
                                            : "",
                                    data [i [0]] instanceof Complemento
                                            ? ((Complemento) data [i [0]])
                                                    .getPrecio ()
                                                    .doubleValue ()
                                            : 0.0f,
                                    data [i [0]] instanceof Complemento
                                            ? ((Complemento) data [i [0]])
                                                    .getDescuento ()
                                            : 0,
                                    data [i [0]].getId ())
                    },
                    new String [] {
                            GestorBD.TABLES [6].x, "entrada", "entradas", "la entrada",
                            String.format (GestorBD.updateStatement (6),
                                    data [i [0]] instanceof Entrada
                                            ? ((Entrada) data [i [0]])
                                                    .getEspectador () == null
                                                            ? ""
                                                            : ((Entrada) data [i [0]])
                                                                    .getEspectador ()
                                                                    .getId ()
                                                                    .toString ()
                                            : "",
                                    data [i [0]] instanceof Entrada
                                            ? ((Entrada) data [i [0]])
                                                    .getPelicula () == null
                                                            ? ""
                                                            : ((Entrada) data [i [0]])
                                                                    .getPelicula ()
                                                                    .getId ()
                                                                    .toString ()
                                            : "",
                                    data [i [0]] instanceof Entrada
                                            ? new SimpleDateFormat (
                                                    "yyyy-MM-dd")
                                                            .format (((Entrada) data [i [0]])
                                                                    .getFecha ()
                                                                    .getTime ())
                                            : "",
                                    data [i [0]] instanceof Entrada
                                            ? ((Entrada) data [i [0]])
                                                    .getSala () == null
                                                            ? -1
                                                            : Sala.indexOf (((Entrada) data [i [0]])
                                                                    .getSala ())
                                            : -1,
                                    data [i [0]] instanceof Entrada
                                            ? ((Entrada) data [i [0]])
                                                    .getSala () == null
                                                    || ((Entrada) data [i [0]])
                                                            .getButaca () == null
                                                                    ? -1
                                                                    : ((IntSupplier) ( () -> {
                                                                        Pair <Integer, Integer> j = ((Entrada) data [i [0]])
                                                                                .getSala ()
                                                                                .indexOf (
                                                                                        ((Entrada) data [i [0]])
                                                                                                .getButaca ());

                                                                        return j.x == -1 || j.y == -1
                                                                                ? -1
                                                                                : j.x * Sala.getColumnas ()
                                                                                        + j.y;
                                                                    })).getAsInt ()
                                            : -1,
                                    data [i [0]] instanceof Entrada
                                            ? String.format ("%.1f", ((Double) ((Entrada) data [i [0]])
                                                    .getValoracion ())
                                                            .isNaN ()
                                                    || ((Entrada) data [i [0]])
                                                            .getValoracion () < 1.0f
                                                    || ((Entrada) data [i [0]])
                                                            .getValoracion () > 10.0f
                                                                    ? 0
                                                                    : ((Entrada) data [i [0]])
                                                                            .getValoracion ())
                                                    .replace (",", ".")
                                            : "0",
                                    data [i [0]] instanceof Entrada
                                            ? ((Entrada) data [i [0]])
                                                    .getPrecio ()
                                                    .toString ()
                                            : "0.0",
                                    data [i [0]].getId ().toString ())
                    },
                    new String [] {
                            GestorBD.TABLES [2].x, "espectador", "espectadores",
                            "el espectador", String.format (
                                    GestorBD.updateStatement (2),
                                    data [i [0]] instanceof Espectador
                                            ? ((Espectador) data [i [0]])
                                                    .getNombre ()
                                            : "",
                                    data [i [0]] instanceof Espectador
                                            ? ((Espectador) data [i [0]])
                                                    .getContrasena ()
                                            : "",
                                    data [i [0]] instanceof Espectador
                                            ? ((Espectador) data [i [0]])
                                                    .getEdad ()
                                            : 0,
                                    data [i [0]].getId ())
                    },
                    new String [] {
                            GestorBD.TABLES [0].x, "película", "películas", "la película",
                            String.format (
                                    GestorBD.updateStatement (0),
                                    data [i [0]] instanceof Pelicula
                                            ? ((Pelicula) data [i [0]])
                                                    .getNombre ()
                                            : "",
                                    data [i [0]] instanceof Pelicula
                                            ? ((Pelicula) data [i [0]])
                                                    .getRutaImagen ()
                                            : "",
                                    data [i [0]] instanceof Pelicula
                                            ? ((Double) ((Pelicula) data [i [0]])
                                                    .getValoracion ())
                                                            .toString ()
                                                            .replace (",", ".")
                                            : "0.0",
                                    data [i [0]] instanceof Pelicula
                                            ? ((Pelicula) data [i [0]])
                                                    .getFecha ()
                                                    .getValue ()
                                            : 0,
                                    data [i [0]] instanceof Pelicula
                                            ? ((Pelicula) data [i [0]])
                                                    .getDirector ()
                                            : "",
                                    data [i [0]] instanceof Pelicula
                                            ? ((Pelicula) data [i [0]])
                                                    .getDuracion ()
                                                    .toMinutes ()
                                            : 0,
                                    data [i [0]] instanceof Pelicula
                                            ? ((Pelicula) data [i [0]])
                                                    .getEdad ()
                                                    .getValue ()
                                            : 0,
                                    data [i [0]] instanceof Pelicula
                                            ? Genero.Nombre.toValor (
                                                    ((Pelicula) data [i [0]])
                                                            .getGeneros ())
                                            : 0,
                                    data [i [0]].getId ())
                    },
                    new String [] {
                            GestorBD.TABLES [4].x, "set de peliculas", "sets de películas",
                            "el set de películas",
                            String.format (
                                    GestorBD.updateStatement (4),
                                    data [i [0]] instanceof SetPeliculas
                                            ? ((SetPeliculas) data [i [0]])
                                                    .getNombre ()
                                            : "",
                                    data [i [0]] instanceof SetPeliculas
                                            ? ((SetPeliculas) data [i [0]])
                                                    .getAdministrador ()
                                                    .getNombre ()
                                            : "",
                                    data [i [0]].getId ())
                    }
            } [Arrays
                    .asList (Administrador.class, Complemento.class, Entrada.class,
                            Espectador.class, Pelicula.class,
                            SetPeliculas.class)
                    .indexOf (data [i [0]].getClass ())];

            try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                    Statement stmt = con.createStatement ()) {
                ResultSet rs;
                if ((rs = stmt.executeQuery (
                        String.format ("SELECT COUNT(*) FROM %s WHERE ID_%s = '%s'",
                                strs [0], strs [0], data [i [0]].getId ())))
                                        .getInt ("COUNT(*)") == 0) {
                    rs.close ();
                    this.insert (data [i [0]]);

                    continue;
                }
                rs.close ();

                if (data [i [0]] instanceof SetPeliculas) {
                    Set <Pelicula> p = ((SetPeliculas) data [i [0]]).getPeliculas ();
                    this.deleteSetPeliculasData ((SetPeliculas) data [i [0]]);
                    ((SetPeliculas) data [i [0]]).setPeliculas (p);
                    this.insertArraySetPelicula ((SetPeliculas) data [i [0]]);
                }

                else if (data [i [0]] instanceof Administrador) {
                    GestorBD db = this;

                    Set <SetPeliculas> add_delete[] = new Set [] {
                            ((Supplier <Set <SetPeliculas>>) ( () -> {
                                Set <SetPeliculas> sp = ((Administrador) data [i [0]])
                                        .getSetsPeliculas ();
                                sp.removeAll (db.getSetsPeliculas ());

                                return sp;
                            })).get (), ((Supplier <Set <SetPeliculas>>) ( () -> {
                                Set <SetPeliculas> sp = db.getSetsPeliculas ().stream ()
                                        .filter (e -> ((Administrador) data [i [0]])
                                                .equals (e.getAdministrador ()))
                                        .collect (Collectors.toSet ());
                                sp.removeAll (((Administrador) data [i [0]])
                                        .getSetsPeliculas ());

                                return sp;
                            })).get () };

                    this.insert (add_delete [0]);
                    this.delete (add_delete [1]);
                }

                else if (data [i [0]] instanceof Espectador) {
                    GestorBD db = this;

                    List <Entrada> add_delete[] = new List [] { this.getEntradas (), new ArrayList <Entrada> (),
                            new ArrayList <Entrada> () };

                    for (int j = 0; j < add_delete [0].size (); j++) {
                        if (((Espectador) data [i [0]]).getHistorial ().contains (add_delete [0].get (j))) {
                            add_delete [1].add (add_delete [0].get (j));

                            continue;
                        }

                        add_delete [2].add (add_delete [0].get (j));
                    }
                    add_delete [1].addAll (((Espectador) data [i [0]]).getHistorial ());

                    this.update (add_delete [1]);
                    this.delete (add_delete [2]);
                }

                int result = stmt.executeUpdate (strs [4]);

                Logger.getLogger (GestorBD.class.getName ()).log (
                        result == 1 ? Level.INFO : Level.WARNING,
                        String.format ("%s %s con ID %s.",
                                result == 1 ? "Se actualizó con éxito"
                                        : "No se pudo actualizar",
                                strs [3],
                                data [i [0]].getId ().toString ()));
            }

            catch (SQLException e) {
                Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                        String.format ("Error al actualizar %s con ID %s en la BBDD: %s",
                                strs [3], data [i [0]].getId ().toString (),
                                e.getMessage ()));
                e.printStackTrace ();
            }
        }

        GestorBD.lock ();
    }

    public void delete (Collection <? extends HasID>... data) {
        List <HasID> l = new ArrayList <HasID> ();

        for (int i = 0; i < data.length; l.addAll (data [i++]))
            ;

        this.delete (l.toArray (new HasID [0]));
    }

    public void delete (HasID []... data) {
        List <HasID> l = new ArrayList <HasID> ();

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data [i].length; l.add (data [i] [j]))
                ;

        this.delete (l.toArray (new HasID [0]));
    }

    public void delete (HasID... data) {
        for (int i[] = new int [1]; i [0] < data.length; i [0]++) {
            if (data [i [0]] == null)
                continue;

            GestorBD.unlock ();

            Object delete[] = new Object [] [] {
                    new Object [] {
                            GestorBD.TABLES [1].x, "administrador", "administradores",
                            "el administrador",
                            (Runnable) () -> {
                                try {
                                    this.deleteAdminData (
                                            (Administrador) data [i [0]]);
                                }

                                catch (ClassCastException e) {
                                }
                            }
                    },
                    new Object [] {
                            GestorBD.TABLES [3].x, "complemento", "complementos",
                            "el complemento",
                            (Runnable) ( () -> {
                                try {
                                    try (Connection con = DriverManager
                                            .getConnection (GestorBD.CONNECTION_STRING);
                                            Statement stmt = con
                                                    .createStatement ()) {
                                        stmt.execute (String.format (
                                                "DELETE FROM %s WHERE %s = '%s'",
                                                GestorBD.TABLES [7].x,
                                                GestorBD.TABLES [7].y [1].x,
                                                ((Complemento) data [i [0]])
                                                        .getId ()
                                                        .toString ()));
                                    }

                                    catch (SQLException e) {
                                        Logger.getLogger (GestorBD.class
                                                .getName ())
                                                .log (Level.WARNING,
                                                        "Hubo un problema al eliminar mapas de elementos y unidades de la base de datos.");
                                    }
                                }

                                catch (ClassCastException e) {
                                }
                            })
                    },
                    new Object [] {
                            GestorBD.TABLES [6].x, "entrada", "entradas", "la entrada",
                            (Runnable) () -> {
                                try {
                                    this.deleteEntradaData ((Entrada) data [i [0]]);
                                }

                                catch (ClassCastException e) {
                                }
                            }
                    },
                    new Object [] {
                            GestorBD.TABLES [2].x, "espectador", "espectadores",
                            "el espectador", (Runnable) () -> {
                                try {
                                    this.deleteEspectadorData (
                                            (Espectador) data [i [0]]);
                                }

                                catch (ClassCastException e) {
                                }
                            }
                    },
                    new Object [] {
                            GestorBD.TABLES [0].x, "película", "películas", "la película",
                            null
                    },
                    new Object [] {
                            GestorBD.TABLES [4].x, "set de peliculas", "sets de películas",
                            "el set de películas",
                            (Runnable) () -> {
                                try {
                                    if (Settings.getActiveSet ()
                                            .equals ((SetPeliculas) data [i [0]])) {
                                        Files.delete (new File (Settings
                                                .getActiveSetPath ())
                                                        .toPath ());

                                        Settings.setActiveSet ();
                                    }
                                    this.deleteSetPeliculasData (
                                            (SetPeliculas) data [i [0]]);
                                }

                                catch (ClassCastException e) {
                                }

                                catch (IOException e) {
                                    Logger.getLogger (GestorBD.class.getName ())
                                            .log (Level.WARNING,
                                                    String.format (
                                                            "No pudo eliminarse el archivo %s, que contiene el set de películas activo.",
                                                            new File (Settings
                                                                    .getActiveSetPath ())
                                                                            .getAbsolutePath ()));
                                }
                            }
                    }
            } [Arrays
                    .asList (Administrador.class, Complemento.class, Entrada.class,
                            Espectador.class, Pelicula.class,
                            SetPeliculas.class)
                    .indexOf (data [i [0]].getClass ())];

            try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                    Statement stmt = con.createStatement ()) {
                GestorBD.unlock ();

                int deleted = stmt.executeUpdate (
                        String.format ("DELETE FROM %s WHERE ID_%s = '%s'", delete [0],
                                delete [0],
                                data [i [0]].getId ()));
                Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                        String.format ("Se ha%s eliminado %d %s.",
                                deleted == 1 ? "" : "n", deleted,
                                delete [deleted == 1 ? 1 : 2]));
            }

            catch (Exception e) {
                Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                        String.format ("Error al eliminar %s con ID %s de la BBDD: %s",
                                delete [3], data [i [0]].getId ().toString (),
                                e.getMessage ()));
                e.printStackTrace ();
            }

            if (delete [4] != null)
                ((Runnable) delete [4]).run ();
        }

        GestorBD.lock ();
    }

    public void deleteEspectadorData (Espectador espectador) {
        this.delete (espectador.getHistorial ());
        espectador.getHistorial ().clear ();
    }

    public void deleteAdminData (Administrador admin) {
        this.delete (admin.getSetsPeliculas ());
        admin.getSetsPeliculas ().clear ();
    }

    public void deleteSetPeliculasData (SetPeliculas setPeliculas) {
        GestorBD.unlock ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            stmt.executeUpdate (String.format ("DELETE FROM %s WHERE %s = '%s'",
                    GestorBD.TABLES [5].x, GestorBD.TABLES [5].y [1].x, setPeliculas.getId ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("No pudieron ser eliminados los datos del set de películas con ID %s: %s",
                            setPeliculas.getId ().toString (), e.getMessage ()));
            e.printStackTrace ();
        }

        GestorBD.lock ();
    }

    private void deleteEntradaData (Entrada entrada) {
        GestorBD.unlock ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            stmt.executeUpdate (String.format ("DELETE FROM %s WHERE %s = '%s'",
                    GestorBD.TABLES [7].x, GestorBD.TABLES [7].y [0].x,
                    String.format (GestorBD.TABLES [7].y [0].x, entrada.getId ())));
            entrada.getComplementos ().clear ();
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("No pudieron ser eliminados los datos de la entrada con ID %s: %s",
                            entrada.getId ().toString (), e.getMessage ()));
            e.printStackTrace ();
        }

        GestorBD.lock ();
    }

    public List <Pelicula> getPeliculas () {
        List <Pelicula> peliculas = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se ejecuta la sentencia y se obtiene el ResultSet con
            // los
            // resutlados
            ResultSet rs = stmt.executeQuery (String.format ("SELECT * FROM %s", GestorBD.TABLES [0].x));

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {
                peliculas.add (new Pelicula (
                        UUID.fromString (rs.getString (GestorBD.TABLES [0].y [0].x)),
                        rs.getString (GestorBD.TABLES [0].y [1].x),
                        rs.getString (GestorBD.TABLES [0].y [2].x),
                        rs.getDouble (GestorBD.TABLES [0].y [3].x),
                        Year.of (rs.getInt (GestorBD.TABLES [0].y [4].x)),
                        rs.getString (GestorBD.TABLES [0].y [5].x),
                        Duration.ofMinutes (rs.getInt (GestorBD.TABLES [0].y [6].x)),
                        EdadRecomendada.fromValue (rs.getByte (GestorBD.TABLES [0].y [7].x)),
                        Genero.Nombre
                                .toGeneros ((short) rs
                                        .getInt (GestorBD.TABLES [0].y [8].x)),
                        null));
            }

            // Se cierra el ResultSet
            rs.close ();

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se han recuperado %d películas.", peliculas.size ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        return peliculas;
    }

    public List <Administrador> getAdministradores () {
        List <Administrador> administradores = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se ejecuta la sentencia y se obtiene el ResultSet con
            // los
            // resutlados
            ResultSet rs = stmt.executeQuery (String.format ("SELECT * FROM %s", GestorBD.TABLES [1].x));
            Administrador administrador;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {
                UUID id = UUID.fromString (rs.getString (GestorBD.TABLES [1].y [0].x));

                administrador = new Administrador (id, rs.getString (GestorBD.TABLES [1].y [1].x),
                        rs.getString (GestorBD.TABLES [1].y [2].x), null);

                administradores.add (administrador);
            }

            // Se cierra el ResultSet
            rs.close ();

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se han recuperado %d administradores.",
                            administradores.size ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        return administradores;
    }

    public List <Espectador> getEspectadores () {
        List <Espectador> espectadores = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se ejecuta la sentencia y se obtiene el ResultSet con
            // los
            // resutlados
            ResultSet rs = stmt.executeQuery (String.format ("SELECT * FROM %s", GestorBD.TABLES [2].x));
            Espectador espectador;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {
                UUID id = UUID.fromString (rs.getString (GestorBD.TABLES [2].y [0].x));

                espectador = new Espectador (id, rs.getString (GestorBD.TABLES [2].y [1].x),
                        rs.getString (GestorBD.TABLES [2].y [2].x),
                        (byte) rs.getInt (GestorBD.TABLES [2].y [3].x),
                        null, null, null);

                List <Entrada> entradas = this.getEntradas ().stream ()
                        .filter (e -> e.getEspectador ().getId ().equals (id)).toList ();
                for (int i = 0; i < entradas.size (); entradas.get (i++).setEspectador (espectador))
                    ;
                espectador.setHistorial (entradas);

                espectadores.add (espectador);
            }

            // Se cierra el ResultSet
            rs.close ();

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se han recuperado %d espectadores.", espectadores.size ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        return espectadores;
    }

    public List <Complemento> getComplementos () {
        List <Complemento> complementos = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se ejecuta la sentencia y se obtiene el ResultSet con
            // los
            // resutlados
            ResultSet rs = stmt.executeQuery (String.format ("SELECT * FROM %s", GestorBD.TABLES [3].x));
            Complemento complemento;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {

                UUID id = UUID.fromString (rs.getString (GestorBD.TABLES [3].y [0].x));

                complemento = new Complemento (id, rs.getString (GestorBD.TABLES [3].y [1].x),
                        new BigDecimal (rs.getString (GestorBD.TABLES [3].y [2].x).replace (",",
                                ".")),
                        rs.getInt (GestorBD.TABLES [3].y [3].x));

                complementos.add (complemento);
            }

            // Se cierra el ResultSet
            rs.close ();

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se han recuperado %d complementos.", complementos.size ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        return complementos;
    }

    public List <SetPeliculas> getSetsPeliculas () {
        List <SetPeliculas> setsPeliculas = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se ejecuta la sentencia y se obtiene el ResultSet con
            // los resutlados
            ResultSet rs = stmt.executeQuery (String.format ("SELECT * FROM %s", GestorBD.TABLES [4].x));

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {
                UUID id = UUID.fromString (rs.getString (GestorBD.TABLES [4].y [0].x));

                SetPeliculas sp = new SetPeliculas (id,
                        this.getAdministradorByName (
                                rs.getString (GestorBD.TABLES [4].y [1].x)),
                        rs.getString (GestorBD.TABLES [4].y [2].x), null);
                sp.add (this.getArraySetPeliculas (id));

                setsPeliculas.add (sp);
            }

            // Se cierra el ResultSet
            rs.close ();

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se han recuperado %d sets de películas.",
                            setsPeliculas.size ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        return setsPeliculas;
    }

    private List <Pelicula> getArraySetPeliculas (UUID id) {
        List <Pelicula> peliculas = new ArrayList <Pelicula> ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ();
                Statement stmt2 = con.createStatement ()) {
            // Se ejecuta la sentencia y se obtiene el ResultSet con
            // los
            // resutlados
            ResultSet rs[] = new ResultSet [] { stmt.executeQuery (
                    String.format ("SELECT %s FROM %s WHERE %s = '%s'", GestorBD.TABLES [5].y [0].x,
                            GestorBD.TABLES [5].x,
                            GestorBD.TABLES [5].y [1].x,
                            id.toString ())),
                    null };

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs [0].next ()) {
                rs [1] = stmt2.executeQuery (String.format ("SELECT * FROM %s WHERE %s = '%s'",
                        GestorBD.TABLES [0].x, GestorBD.TABLES [0].y [0].x,
                        rs [0].getString (GestorBD.TABLES [5].y [0].x)));

                peliculas.add (new Pelicula (
                        UUID.fromString (rs [1].getString (GestorBD.TABLES [0].y [0].x)),
                        rs [1].getString (GestorBD.TABLES [0].y [1].x),
                        rs [1].getString (GestorBD.TABLES [0].y [2].x),
                        rs [1].getDouble (GestorBD.TABLES [0].y [3].x),
                        Year.of (rs [1].getInt (GestorBD.TABLES [0].y [4].x)),
                        rs [1].getString (GestorBD.TABLES [0].y [5].x),
                        Duration.ofMinutes (rs [1].getInt (GestorBD.TABLES [0].y [6].x)),
                        EdadRecomendada.fromValue (
                                rs [1].getByte (GestorBD.TABLES [0].y [7].x)),
                        Genero.Nombre
                                .toGeneros ((short) rs [1]
                                        .getInt (GestorBD.TABLES [0].y [8].x)),
                        null));

                rs [1].close ();
            }

            // Se cierra el ResultSet
            rs [0].close ();

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se han recuperado %d arrays de películas de sets.",
                            peliculas.size ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al insertar datos en la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        return peliculas;
    }

    private List <Entrada> getEntradas () {
        List <Entrada> l = new ArrayList <Entrada> ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            ResultSet rs = stmt.executeQuery (String.format ("SELECT * FROM %s", GestorBD.TABLES [6].x));

            for (; rs.next ();) {
                UUID id = UUID.fromString (rs.getString (GestorBD.TABLES [6].y [0].x));
                int i[] = new int [] { rs.getInt (GestorBD.TABLES [6].y [4].x),
                        rs.getInt (GestorBD.TABLES [6].y [5].x) };

                l.add (new Entrada (id,
                        new Espectador (UUID.fromString (
                                rs.getString (GestorBD.TABLES [6].y [1].x)), "", "",
                                Espectador.getDefaultEdad (), null, null, null),
                        this.getPeliculas ().stream ().filter (
                                e -> {
                                    try {
                                        return e.getId ()
                                                .equals (UUID.fromString (
                                                        rs.getString (GestorBD.TABLES [6].y [2].x)));
                                    }
                                    catch (SQLException e1) {
                                        Logger.getLogger (GestorBD.class
                                                .getName ())
                                                .log (Level.WARNING,
                                                        String.format ("No pudo obtenerse un ID de película: %s",
                                                                e1.getMessage ()));
                                        e1.printStackTrace ();

                                        return false;
                                    }
                                })
                                .findFirst ().orElse (null),
                        ((Supplier <Calendar>) ( () -> {
                            Calendar c = Calendar.getInstance ();
                            try {
                                c.setTime (new SimpleDateFormat ("yyyy-MM-dd")
                                        .parse (rs.getString (GestorBD.TABLES [6].y [3].x)));
                            }
                            catch (SQLException | ParseException e1) {
                                Logger.getLogger (GestorBD.class.getName ()).log (
                                        Level.WARNING,
                                        String.format ("No pudo obtenerse una fecha: %s",
                                                e1.getMessage ()));
                                e1.printStackTrace ();

                                return null;
                            }

                            return c;
                        })).get (), i [0] == -1 ? null : Sala.getSalas ().get (i [0]),
                        i [0] == -1 || i [1] == -1 ? null
                                : Sala.getSalas ().get (i [0]).getButacas ()
                                        .get (i [1]),
                        this.getArrayEntrada (id), rs.getDouble (GestorBD.TABLES [6].y [6].x),
                        rs.getBigDecimal (GestorBD.TABLES [6].y [7].x)));
            }

            rs.close ();
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("No se pudieron obtener los datos de las entradas: %s",
                            e.getMessage ()));
            e.printStackTrace ();

            return Collections.emptyList ();
        }

        return l;
    }

    private Map <Complemento, BigInteger> getArrayEntrada (UUID id) {
        Map <Complemento, BigInteger> m = new HashMap <Complemento, BigInteger> ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            ResultSet rs = stmt
                    .executeQuery (String.format ("SELECT * FROM %s WHERE %s = %s",
                            GestorBD.TABLES [7].x, GestorBD.TABLES [7].y [0].x,
                            String.format (GestorBD.TABLES [7].y [0].y, id.toString ())));

            for (; rs.next ();)
                m.put (new Complemento (UUID.fromString (rs.getString (GestorBD.TABLES [7].y [1].x)),
                        rs.getString (GestorBD.TABLES [7].y [2].x),
                        new BigDecimal (rs.getString (GestorBD.TABLES [7].y [3].x).replace (",", ".")),
                        rs.getInt (GestorBD.TABLES [7].y [4].x)),
                        rs.getBigDecimal (GestorBD.TABLES [7].y [5].x).toBigInteger ());

            rs.close ();
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("No pudieron obtenerse los complementos de la entrada con ID %s: %s",
                            id.toString (),
                            e.getMessage ()));
            e.printStackTrace ();

            return Collections.emptyMap ();
        }

        return m;
    }

    public Administrador getAdministradorByName (String name) {
        if (name == null || name.length () == 0)
            return null;

        Administrador administrador = new Administrador ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ();
                Statement stmt2 = con.createStatement ()) {
            // Se ejecuta la sentencia y se obtiene el ResultSet con
            // los
            // resutlados
            ResultSet rs = stmt.executeQuery (String.format ("SELECT COUNT(*) FROM %s WHERE %s = '%s'",
                    GestorBD.TABLES [1].x, GestorBD.TABLES [1].y [1].x, name));
            if (rs.getInt ("COUNT(*)") == 0) {
                Logger.getLogger (String.format ("No se recuperó ningún administrador con nombre '%s'.",
                        name));
                rs.close ();

                return null;
            }

            rs = stmt.executeQuery (String.format ("SELECT * FROM %s WHERE %s = '%s'",
                    GestorBD.TABLES [1].x, GestorBD.TABLES [1].y [1].x, name));

            administrador = new Administrador (UUID.fromString (rs.getString (GestorBD.TABLES [1].y [0].x)),
                    rs.getString (GestorBD.TABLES [1].y [1].x),
                    rs.getString (GestorBD.TABLES [1].y [2].x), null);

            // Se cierra el ResultSet
            rs.close ();

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se ha recuperado el administrador '%s' con ID: %s.", name,
                            administrador.getId ().toString ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al recuperar datos de la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();

            return null;
        }

        return administrador;
    }

    public List <String> getAdminKeys () {
        List <String> llaves = new ArrayList <String> ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se ejecuta la sentencia y se obtiene el ResultSet con
            // los
            // resutlados
            ResultSet rs = stmt.executeQuery (String.format ("SELECT * FROM %s", GestorBD.TABLES [8].x));
            // Se recorre el ResultSet y se crean objetos Cliente
            for (; rs.next ();)
                llaves.add (rs.getString (GestorBD.TABLES [8].y [0].x));
            // Se cierra el ResultSet
            rs.close ();

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se han recuperado %d llaves.", llaves.size ()));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al obtener datos de la BBDD : %s", e.getMessage ()));
            e.printStackTrace ();
        }

        return llaves;
    }

    public void deleteAdminKeys () {
        GestorBD.unlock ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se han borrado %d llaves",
                            stmt.executeUpdate (String.format ("DELETE FROM %s",
                                    GestorBD.TABLES [8].x))));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                    String.format ("Error al borrar llaves: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        GestorBD.lock ();
    }

    public void regenerateAdminKeys () {
        this.regenerateAdminKeys (false);
    }

    public void regenerateAdminKeys (boolean defaultKey) {
        this.deleteAdminKeys ();
        this.createAdminKeys (defaultKey);
    }

    public void consumeAdminKey (String key) {
        GestorBD.unlock ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            int r = stmt.executeUpdate (
                    String.format ("DELETE FROM %s WHERE %s IN ('%s', '%s');",
                            GestorBD.TABLES [8].x,
                            GestorBD.TABLES [8].y [0].x, key, Settings.getAdminKey ()));

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se ha%s eliminado %d llave%s.", r == 1 ? "" : "n", r,
                            r == 1 ? "" : "s"));
        }

        catch (Exception e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al borrar llaves: \"%s\"", e.getMessage ()));
            e.printStackTrace ();
        }

        GestorBD.lock ();
    }

    public void wipe () {
        GestorBD.unlock ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            stmt.execute (String.format ("DELETE FROM %s;", GestorBD.TABLES [5].x));
            stmt.execute (String.format ("DELETE FROM %s WHERE %s != %s;", GestorBD.TABLES [7].x,
                    GestorBD.TABLES [7].y [0].x,
                    String.format (GestorBD.TABLES [7].y [0].y,
                            SetPeliculas.getDefault ().getId ().toString ())));

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format (
                            "Han sido borrados:%n\t%d películas.%n\t%d administradores.%n\t%d espectadores.%n\t%d complementos.%n\t%d sets de películas.%n\t%d entradas.%n\t%d llaves",
                            stmt.executeUpdate (String.format (
                                    "DELETE FROM %s WHERE %s NOT IN (%s);",
                                    GestorBD.TABLES [0].x,
                                    GestorBD.TABLES [0].y [0].x,
                                    String.join (", ", Pelicula.getDefault ()
                                            .stream ()
                                            .map (e -> String.format (
                                                    GestorBD.TABLES [0].y [0].y,
                                                    e.getId ().toString ()))
                                            .collect (Collectors
                                                    .toList ())))),
                            stmt.executeUpdate (String.format (
                                    "DELETE FROM %s;", GestorBD.TABLES [1].x)),
                            stmt.executeUpdate (String.format (
                                    "DELETE FROM %s;", GestorBD.TABLES [2].x)),
                            stmt.executeUpdate (String.format (
                                    "DELETE FROM %s WHERE %s NOT IN (%s);",
                                    GestorBD.TABLES [3].x,
                                    GestorBD.TABLES [3].y [0].x,
                                    String.join (", ", Complemento.getDefault ()
                                            .stream ()
                                            .map (e -> String.format (
                                                    GestorBD.TABLES [3].y [0].y,
                                                    e.getId ().toString ()))
                                            .collect (Collectors
                                                    .toList ())))),
                            stmt.executeUpdate (String.format (
                                    "DELETE FROM %s WHERE %s != %s;",
                                    GestorBD.TABLES [4].x,
                                    GestorBD.TABLES [4].y [0].x,
                                    String.format (GestorBD.TABLES [4].y [0].y,
                                            SetPeliculas.getDefault ()
                                                    .getId ()
                                                    .toString ()))),
                            stmt.executeUpdate (String.format (
                                    "DELETE FROM %s;", GestorBD.TABLES [6].x)),
                            stmt.executeUpdate (String.format ("DELETE FROM %s;",
                                    GestorBD.TABLES [8].x))));
        }

        catch (SQLException e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.SEVERE,
                    String.format ("Error al borrar datos de la BBDD: %s", e.getMessage ()));
            e.printStackTrace ();
        }

        GestorBD.lock ();
    }
}