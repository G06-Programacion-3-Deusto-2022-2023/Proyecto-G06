package internals;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.sqlite.SQLiteException;

import cine.Administrador;
import cine.Complemento;
import cine.EdadRecomendada;
import cine.Entrada;
import cine.Espectador;
import cine.Genero;
import cine.Pelicula;
import cine.SetPeliculas;
import cine.Usuario;

public class GestorBD {
    protected static final String DRIVER_NAME = "org.sqlite.JDBC";
    protected static final String DATABASE_FILE = "data/db/database.db";
    protected static final String CONNECTION_STRING = "jdbc:sqlite:" + DATABASE_FILE;

    public GestorBD () {
        try {
            // Cargar el diver SQLite
            Class.forName (GestorBD.DRIVER_NAME);
        }
        catch (ClassNotFoundException ex) {
            System.err.println (String.format ("* Error al cargar el driver de BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    public void crearBBDD () {
        new File (new File (GestorBD.DATABASE_FILE).getParent ()).mkdirs ();

        // Se abre la conexión y se obtiene el Statement
        // Al abrir la conexión, si no existía el fichero, se crea la base de
        // datos
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {

            String sql1 = "CREATE TABLE IF NOT EXISTS PELICULA (\n"
                    + " ID_PELICULA STRING PRIMARY KEY NOT NULL,\n"
                    + " NOMBRE_PELICULA STRING,\n"
                    + " RUTA_IMAGEN STRING, \n"
                    + " VALORACION INTEGER NOT NULL,\n"
                    + " FECHA INTEGER, \n"
                    + " DIRECTOR STRING NOT NULL,\n"
                    + " DURACION INTEGER NOT NULL,\n"
                    + " EDAD_RECOMENDADA INTEGER NOT NULL,\n"
                    + " GENEROS INTEGER NOT NULL\n"
                    + ");";
            String sql2 = "CREATE TABLE IF NOT EXISTS ADMINISTRADOR (\n"
                    + " ID_ADMINISTRADOR STRING PRIMARY KEY NOT NULL,\n"
                    + " NOMBRE_ADMINISTRADOR STRING,\n"
                    + " CONTRASENA_ADMINISTRADOR STRING \n"
                    + ");";
            String sql3 = "CREATE TABLE IF NOT EXISTS ESPECTADOR (\n"
                    + " ID_ESPECTADOR STRING ,\n"
                    + " NOMBRE_ESPECTADOR STRING PRIMARY KEY NOT NULL ,\n"
                    + " CONTRASENA_ESPECTADOR STRING, \n"
                    + " EDAD INTEGER \n"
                    + ");";
            String sql4 = "CREATE TABLE IF NOT EXISTS COMPLEMENTO (\n"
                    + " ID_COMPLEMENTO STRING ,\n"
                    + " NOMBRE_COMPLEMENTO STRING PRIMARY KEY NOT NULL ,\n"
                    + " PRECIO DECIMAL(6,4), \n"
                    + " DESCUENTO INTEGER \n"
                    + ");";
            String sql5 = "CREATE TABLE IF NOT EXISTS SET_PELICULA (\n"
                    + "ID_SET_PELICULA STRING PRIMARY KEY NOT NULL, "
                    + "NOMBRE_ADMINISTRADOR STRING, \n"
                    + "NOMBRE_SET_PELICULA STRING \n"
                    + ");";
            String sql6 = "CREATE TABLE IF NOT EXISTS ARRAY_SETPELICULA (\n"
                    + " NOMBRE_SETPELICULA STRING,\n"
                    + " NOMBRE_PELICULA STRING \n"
                    + ");";
            String sql7 = "CREATE TABLE IF NOT EXISTS LLAVES (\n"
                    + " LLAVE STRING \n"
                    + ");";

            if (!stmt.execute (sql1) && !stmt.execute (sql2) && !stmt.execute (sql3) && !stmt.execute (sql4)
                    && !stmt.execute (sql5) && !stmt.execute (sql6) && !stmt.execute (sql7)) {
                System.out.println (
                        "- Se ha creado la tabla pelicula, la tabla administrador, la tabla espectador, la tabla complemento y la tabla set_Pelicula");
            }
        }

        catch (Exception ex) {
            System.err.println (String.format ("* Error al crear la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }

        this.insert (Complemento.getDefault ().stream ().collect (Collectors.toList ()),
                Collections.singleton (SetPeliculas.getDefault ()));

        this.createAdminKeys ();
    }

    public void borrarBBDD () {
        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {

            String sql1 = "DROP TABLE IF EXISTS PELICULA";
            String sql2 = "DROP TABLE IF EXISTS ADMINISTRADOR";
            String sql3 = "DROP TABLE IF EXISTS ESPECTADOR";
            String sql4 = "DROP TABLE IF EXISTS COMPLEMENTO";
            String sql5 = "DROP TABLE IF EXISTS SET_PELICULA";
            String sql6 = "DROP TABLE IF EXISTS ARRAY_SET_PELICULA";
            String sql7 = "DROP TABLE IF EXISTS LLAVES";
            // Se ejecuta la sentencia de creación de la tabla Estudiantes
            if (!stmt.execute (sql1) && !stmt.execute (sql2) && !stmt.execute (sql3) && !stmt.execute (sql4)
                    && !stmt.execute (sql5) && !stmt.execute (sql6) && !stmt.execute (sql7)) {
                System.out.println ("Se han borrado las tablas");
            }
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al borrar la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }

        try {
            // Se borra el fichero de la BBDD
            Files.delete (Paths.get (DATABASE_FILE));
            System.out.println ("- Se ha borrado el fichero de la BBDD");
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al borrar el archivo de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
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
        for (int i[] = new int [1]; i [0] < data.length; i [0]++)
            new Runnable [] {
                    data [i [0]] instanceof Administrador
                            ? () -> this.insertarDatosAdministrador ((Administrador) data [i [0]])
                            : () -> {
                            },
                    data [i [0]] instanceof Complemento
                            ? () -> this.insertarDatosComplemento ((Complemento) data [i [0]])
                            : () -> {
                            },
                    data [i [0]] instanceof Espectador ? () -> this.insertarDatosEspectador ((Espectador) data [i [0]])
                            : () -> {
                            },
                    data [i [0]] instanceof Pelicula ? () -> this.insertarDatosPelicula ((Pelicula) data [i [0]])
                            : () -> {
                            },
                    data [i [0]] instanceof SetPeliculas
                            ? () -> this.insertarDatosSetPelicula ((SetPeliculas) data [i [0]])
                            : () -> {
                            }
            } [Arrays
                    .asList (Administrador.class, Complemento.class, Espectador.class, Pelicula.class,
                            SetPeliculas.class)
                    .indexOf (data [i [0]].getClass ())].run ();
    }

    private void insertarDatosPelicula (Pelicula... peliculas) {
        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se define la plantilla de la sentencia SQL
            String sql = "INSERT INTO PELICULA (ID_PELICULA, NOMBRE_PELICULA,RUTA_IMAGEN, VALORACION, FECHA, DIRECTOR, DURACION, EDAD_RECOMENDADA, GENEROS) VALUES ('%s', '%s', '%s', %d, '%s','%s', %d, '%s', %d);";

            System.out.println ("- Insertando peliculas...");

            // Se recorren los clientes y se insertan uno a uno
            for (Pelicula p : peliculas) {
                int Valoracion = (int) p.getValoracion ();
                if (1 == stmt
                        .executeUpdate (String.format (sql, p.getId ().toString (), p.getNombre (), p.getRutaImagen (),
                                (int) Valoracion, p.getFecha (), p.getDirector (), (int) p.getDuracion ().toMinutes (),
                                p.getEdad ().getValue (), Genero.Nombre.toValor (p.getGeneros ())))) {
                    System.out.println (String.format (" - Pelicula insertada: %s", p.toString ()));
                }
                else {
                    System.out.println (String.format (" - No se ha insertado la pelicula: %s", p.toString ()));
                }
            }
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al insertar datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    private void insertarDatosAdministrador (Administrador... administrador) {
        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se define la plantilla de la sentencia SQL
            String sql = "INSERT INTO ADMINISTRADOR (ID_ADMINISTRADOR, NOMBRE_ADMINISTRADOR, CONTRASENA_ADMINISTRADOR) VALUES ('%s', '%s', '%s');";

            System.out.println ("- Insertando administrador...");

            for (Administrador a : administrador) {
                if (1 == stmt.executeUpdate (String.format (sql, a.getId (), a.getNombre (), a.getContrasena ()))) {
                    System.out.println (String.format (" - Administrador insertada: %s", a.toString ()));
                }
                else {
                    System.out.println (String.format (" - No se ha insertado el administrador: %s", a.toString ()));
                }
            }
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al insertar datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    private void insertarDatosEspectador (Espectador... espectador) {
        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se define la plantilla de la sentencia SQL
            String sql = "INSERT INTO ESPECTADOR (ID_ESPECTADOR, NOMBRE_ESPECTADOR, CONTRASENA_ESPECTADOR, EDAD) VALUES ('%s', '%s', '%s', %d);";

            System.out.println ("- Insertando usuarios...");

            for (Espectador e : espectador) {
                if (1 == stmt.executeUpdate (
                        String.format (sql, e.getId (), e.getNombre (), e.getContrasena (), e.getEdad ()))) {
                    System.out.println (String.format (" - Usuario insertada: %s", e.toString ()));
                }
                else {
                    System.out.println (String.format (" - No se ha insertado el usuario: %s", e.toString ()));
                }
            }
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al insertar datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    private void insertarDatosComplemento (Complemento... complementos) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se define la plantilla de la sentencia SQL
            String sql = "INSERT INTO COMPLEMENTO (ID_COMPLEMENTO, NOMBRE_COMPLEMENTO, PRECIO, DESCUENTO) VALUES ('%s', '%s', '%.2f', '%s');";

            System.out.println ("- Insertando complementos...");

            // Se recorren los clientes y se insertan uno a uno
            for (Complemento c : complementos) {
                try {
                    if (1 == stmt.executeUpdate (
                            String.format (sql, c.getId (), c.getNombre (), c.getPrecio (), c.getDescuento ()))) {
                        System.out.println (String.format (" - Complementos insertada: %s", c.toString ()));
                    }

                    else {
                        System.out.println (String.format (" - No se ha insertado el complemento: %s", c.toString ()));
                    }
                }

                catch (SQLiteException e) {
                    System.out.println (String.format (" - No se ha insertado el complemento: %s", c.toString ()));
                }
            }
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al insertar datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    private void insertarDatosSetPelicula (SetPeliculas... setPeliculas) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se define la plantilla de la sentencia SQL

            System.out.println ("- Insertando setPeliculas...");
            this.insertarDatosArraySetPelicula (setPeliculas);

            // Se recorren los clientes y se insertan uno a uno
            for (SetPeliculas s : setPeliculas) {
                String sql = "INSERT INTO SET_PELICULA (ID_SET_PELICULA, NOMBRE_ADMINISTRADOR, NOMBRE_SET_PELICULA) VALUES ('%s', '%s', '%s');";
                if (!(s.getAdministrador () == null)) {
                    if (1 == stmt.executeUpdate (
                            String.format (sql, s.getId (), s.getAdministrador ().getNombre (), s.getNombre ()))) {
                        this.insertarDatosAdministrador (s.getAdministrador ());
                        System.out.println (String.format (" - set_Peliculas insertadas: %s", s.toString ()));
                    }
                    else {
                        System.out.println (String.format (" - No se ha insertado el setPeliculas: %s", s.toString ()));
                    }
                }
                else {
                    if (1 == stmt.executeUpdate (String.format (sql, s.getId (), "", s.getNombre ()))) {
                        System.out.println (String.format (" - set_Peliculas insertadas: %s", s.toString ()));
                    }
                    else {
                        System.out.println (String.format (" - No se ha insertado el setPeliculas: %s", s.toString ()));
                    }
                }
            }
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al insertar datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    private void createAdminKeys () {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            for (int i = 0; i < 10; i++) {
                String key = Usuario.generatePassword ();
                String sql = String.format ("INSERT INTO LLAVES (LLAVE) VALUES ('%s')", key);
                if (1 == stmt.executeUpdate (sql)) {
                    System.out.println (String.format (" - llave insertada: %s", key));
                }
                else {
                    System.out.println (String.format (" - No se han insertado llaves"));
                }
            }

        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al insertar datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    private void insertarDatosArraySetPelicula (SetPeliculas... setPeliculas) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se define la plantilla de la sentencia SQL

            // Se recorren los clientes y se insertan uno a uno
            for (SetPeliculas s : setPeliculas) {
                for (Pelicula p : s.getPeliculas ()) {
                    String sql = "INSERT INTO ARRAY_SETPELICULA (NOMBRE_SETPELICULA, NOMBRE_PELICULA) VALUES ('%s', '%s');";
                    if (1 == stmt.executeUpdate (String.format (sql, s.getNombre (), p.getNombre ()))) {
                        System.out.println (String.format (" - array_set_Peliculas insertadas: %s", s.toString ()));
                    }
                    else {
                        System.out.println (
                                String.format (" - No se ha insertado el array_setPeliculas: %s", s.toString ()));
                    }
                }
            }
        }

        catch (Exception ex) {
            System.err.println (String.format ("* Error al insertar datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    public void update (HasID o) {
        String strs[] = new String [] [] {
                new String [] {
                        "ADMINISTRADOR", "administrador", "administradores", "el administrador", String.format (
                                "UPDATE ADMINISTRADOR SET NOMBRE_ADMINISTRADOR = '%s', CONTRASENA_ADMINISTRADOR = '%s' WHERE ID_ADMINISTRADOR = '%s'",
                                o instanceof Administrador ? ((Administrador) o).getNombre () : "",
                                o instanceof Administrador ? ((Administrador) o).getContrasena () : "",
                                o.getId ())
                },
                new String [] {
                        "COMPLEMENTO", "complemento", "complementos", "el complemento", String.format (
                                "UPDATE COMPLEMENTO SET NOMBRE_COMPLEMENTO = '%s', PRECIO = '%.2f', DESCUENTO = %d WHERE ID_COMPLEMENTO = '%s'",
                                o instanceof Complemento ? ((Complemento) o).getNombre () : "",
                                o instanceof Complemento ? ((Complemento) o).getPrecio ().doubleValue () : 0.0f,
                                o instanceof Complemento ? ((Complemento) o).getDescuento () : 0,
                                o.getId ())
                },
                new String [] {
                        "ESPECTADOR", "espectador", "espectadores", "el espectador", String.format (
                                "UPDATE ESPECTADOR SET NOMBRE_ESPECTADOR = '%s', CONTRASENA_ESPECTADOR = '%s', EDAD = %d WHERE ID_ESPECTADOR = '%s'",
                                o instanceof Espectador ? ((Espectador) o).getNombre () : "",
                                o instanceof Espectador ? ((Espectador) o).getContrasena () : "",
                                o instanceof Espectador ? ((Espectador) o).getEdad () : 0,
                                o.getId ())
                },
                new String [] {
                        "PELICULA", "película", "películas", "la película", String.format (
                                "UPDATE PELICULA SET NOMBRE_PELICULA = '%s', RUTA_IMAGEN = '%s', VALORACION = %.1f, FECHA = %d, DIRECTOR = '%s', DURACION = %d, EDAD_RECOMENDAD = %d, GENEROS = %d WHERE ID_PELICULA = '%s'",
                                o instanceof Pelicula ? ((Pelicula) o).getNombre () : "",
                                o instanceof Pelicula ? ((Pelicula) o).getRutaImagen () : "",
                                o instanceof Pelicula ? ((Pelicula) o).getValoracion () : 0.0,
                                o instanceof Pelicula ? ((Pelicula) o).getFecha ().getValue () : 0,
                                o instanceof Pelicula ? ((Pelicula) o).getDirector () : "",
                                o instanceof Pelicula ? ((Pelicula) o).getDuracion ().toMinutes () : 0,
                                o instanceof Pelicula ? ((Pelicula) o).getEdad ().getValue () : 0,
                                o instanceof Pelicula ? Genero.Nombre.toValor (((Pelicula) o).getGeneros ()) : 0,
                                o.getId ())
                },
                new String [] {
                        "SETS_PELICULAS", "set de peliculas", "sets de películas", "el set de películas",
                        String.format (
                                "UPDATE SET_PELICULA SET NOMBRE_SETPELICULA = '%s', NOMBRE_ADMINISTRADOR = '%s' WHERE ID_COMPLEMENTO = '%s'",
                                o instanceof SetPeliculas ? ((SetPeliculas) o).getNombre () : "",
                                o instanceof SetPeliculas ? ((SetPeliculas) o).getAdministrador ().getNombre () : "",
                                o.getId ())
                }
        } [Arrays.asList (Administrador.class, Complemento.class, Espectador.class, Pelicula.class, SetPeliculas.class)
                .indexOf (o.getClass ())];

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            if (stmt.executeQuery (
                    String.format ("SELECT COUNT(*) FROM %s WHERE ID_%s = '%s'",
                            strs [0], strs [0], o.getId ()))
                    .getInt ("COUNT(*)") == 0) {
                this.insert (o);

                return;
            }

            int result = stmt.executeUpdate (strs [4]);

            Logger.getLogger (GestorBD.class.getName ()).log (result == 1 ? Level.INFO : Level.WARNING,
                    String.format ("%s %s con ID %s.",
                            result == 1 ? "Se actualizó con éxito" : "No se pudo actualizar", strs [3],
                            o.getId ().toString ()));
        }

        catch (Exception e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al actualizar %s con ID %s en la BBDD: %s",
                            strs [3], o.getId ().toString (), e.getMessage ()));
            e.printStackTrace ();
        }
    }

    public void delete (HasID o) {
        String strs[] = new String [] [] {
                new String [] {
                        "ADMINISTRADOR", "administrador", "administradores", "el administrador"
                },
                new String [] {
                        "COMPLEMENTO", "complemento", "complementos", "el complemento"
                },
                new String [] {
                        "ESPECTADOR", "espectador", "espectadores", "el espectador"
                },
                new String [] {
                        "PELICULA", "película", "películas", "la película"
                },
                new String [] {
                        "SETS_PELICULAS", "set de peliculas", "sets de películas", "el set de películas"
                }
        } [Arrays.asList (Administrador.class, Complemento.class, Espectador.class, Pelicula.class, SetPeliculas.class)
                .indexOf (o.getClass ())];

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {

            int deleted = stmt.executeUpdate (
                    String.format ("DELETE FROM %s WHERE ID_%s = '%s'", strs [0], strs [0], o.getId ()));
            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO, String.format ("Se ha%s eliminado %d %s.",
                    deleted == 1 ? "" : "n", deleted, strs [deleted == 1 ? 1 : 2]));
        }

        catch (Exception e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("Error al eliminar %s con ID %s de la BBDD: %s",
                            strs [3], o.getId ().toString (), e.getMessage ()));
            e.printStackTrace ();
        }
    }

    public List <Pelicula> obtenerDatosPeliculas () {
        List <Pelicula> peliculas = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql = "SELECT * FROM PELICULA";

            // Se ejecuta la sentencia y se obtiene el ResultSet con los
            // resutlados
            ResultSet rs = stmt.executeQuery (sql);
            Pelicula pelicula;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {

                UUID id = UUID.fromString (rs.getString ("ID_PELICULA"));
                double Valoracion = (double) rs.getInt ("VALORACION");
                Year fecha = Year.of (rs.getInt ("FECHA"));
                Set <Genero.Nombre> genero = (Set <Genero.Nombre>) Genero.Nombre
                        .toGeneros ((short) rs.getInt ("GENEROS"));

                pelicula = new Pelicula (id, rs.getString ("NOMBRE_PELICULA"), rs.getString ("RUTA_IMAGEN"), Valoracion,
                        fecha, rs.getString ("DIRECTOR"), Duration.ofMinutes (rs.getInt ("DURACION")),
                        EdadRecomendada.fromValue (rs.getByte ("EDAD_RECOMENDADA")), genero, null);

                peliculas.add (pelicula);

            }

            // Se cierra el ResultSet
            rs.close ();

            System.out.println (String.format ("- Se han recuperado %d peliculas...", peliculas.size ()));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al obtener datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }

        return peliculas;
    }

    public List <Administrador> obtenerDatosAdministradores () {
        List <Administrador> administradores = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql = "SELECT * FROM ADMINISTRADOR";

            // Se ejecuta la sentencia y se obtiene el ResultSet con los
            // resutlados
            ResultSet rs = stmt.executeQuery (sql);
            Administrador administrador;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {
                UUID id = UUID.fromString (rs.getString ("ID_ADMINISTRADOR"));

                administrador = new Administrador (id, rs.getString ("NOMBRE_ADMINISTRADOR"),
                        rs.getString ("CONTRASENA_ADMINISTRADOR"), null);

                administradores.add (administrador);
            }

            // Se cierra el ResultSet
            rs.close ();

            System.out.println (String.format ("- Se han recuperado %d administradores...", administradores.size ()));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al obtener datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }

        return administradores;
    }

    public List <Espectador> obtenerDatosEspectadores () {
        List <Espectador> espectadores = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql = "SELECT * FROM ESPECTADOR";

            // Se ejecuta la sentencia y se obtiene el ResultSet con los
            // resutlados
            ResultSet rs = stmt.executeQuery (sql);
            Espectador espectador;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {
                UUID id = UUID.fromString (rs.getString ("ID_ESPECTADOR"));
                Map <Genero.Nombre, Genero.Preferencia> preferencias = new TreeMap <Genero.Nombre, Genero.Preferencia> ();
                Collection <Entrada> historial = new TreeSet <Entrada> ();
                Set <Espectador> grupo = new TreeSet <Espectador> ();

                espectador = new Espectador (id, rs.getString ("NOMBRE_ESPECTADOR"),
                        rs.getString ("CONTRASENA_ESPECTADOR"), (byte) rs.getInt ("EDAD"), preferencias, historial,
                        grupo);

                espectadores.add (espectador);
            }

            // Se cierra el ResultSet
            rs.close ();

            System.out.println (String.format ("- Se han recuperado %d espectadores...", espectadores.size ()));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al obtener datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }

        return espectadores;
    }

    public List <Complemento> obtenerDatosComplementos () {
        List <Complemento> complementos = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql = "SELECT * FROM COMPLEMENTO";

            // Se ejecuta la sentencia y se obtiene el ResultSet con los
            // resutlados
            ResultSet rs = stmt.executeQuery (sql);
            Complemento complemento;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {

                UUID id = UUID.fromString (rs.getString ("ID_COMPLEMENTO"));

                complemento = new Complemento (id, rs.getString ("NOMBRE_COMPLEMENTO"),
                        new BigDecimal (rs.getString ("PRECIO").replace (",", ".")),
                        rs.getInt ("DESCUENTO"));

                complementos.add (complemento);
            }

            // Se cierra el ResultSet
            rs.close ();

            System.out.println (String.format ("- Se han recuperado %d complementos...", complementos.size ()));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al obtener datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }

        return complementos;
    }

    public List <SetPeliculas> obtenerDatosSetPeliculas () {
        List <SetPeliculas> setsPeliculas = new ArrayList <> ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql = "SELECT * FROM SET_PELICULA";

            // Se ejecuta la sentencia y se obtiene el ResultSet con los
            // resutlados
            ResultSet rs = stmt.executeQuery (sql);
            SetPeliculas setPeliculas;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {

                UUID id = UUID.fromString (rs.getString ("ID_SET_PELICULA"));
                String nombre = rs.getString ("NOMBRE_SET_PELICULA");

                setPeliculas = new SetPeliculas (id,
                        obtenerDatosAdministradorPorNombre (rs.getString ("NOMBRE_ADMINISTRADOR")), nombre,
                        this.obtenerDatosArraySetPeliculas (nombre));

                setsPeliculas.add (setPeliculas);
            }

            // Se cierra el ResultSet
            rs.close ();

            System.out.println (String.format ("- Se han recuperado %d sets_peliculas...", setsPeliculas.size ()));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al obtener datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }

        return setsPeliculas;
    }

    public List <Pelicula> obtenerDatosArraySetPeliculas (String nombre) {
        List <Pelicula> peliculas = new ArrayList <Pelicula> ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql = "SELECT NOMBRE_PELICULA FROM ARRAY_SETPELICULA WHERE NOMBRE_SETPELICULA = " + nombre;

            // Se ejecuta la sentencia y se obtiene el ResultSet con los
            // resutlados
            ResultSet rs = stmt.executeQuery (sql);
            Pelicula pelicula;

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {

                pelicula = new Pelicula (rs.getString ("NOMBRE_PELICULA"));
                peliculas.add (pelicula);
            }

            // Se cierra el ResultSet
            rs.close ();

            System.out.println (String.format ("- Se han recuperado %d array_sets_peliculas...", peliculas.size ()));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al obtener datos de la BBDD : %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
        return peliculas;

    }

    public Administrador obtenerDatosAdministradorPorNombre (String NombreAdministrador) {
        Administrador administrador = new Administrador ();

        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql = "SELECT * FROM ADMINISTRADOR WHERE NOMBRE_ADMINISTRADOR = '" + NombreAdministrador + "'";

            // Se ejecuta la sentencia y se obtiene el ResultSet con los
            // resutlados
            ResultSet rs = stmt.executeQuery (sql);

            while (rs.next ()) {
                UUID id = UUID.fromString (rs.getString ("ID_ADMINISTRADOR"));

                administrador = new Administrador (id, rs.getString ("NOMBRE_ADMINISTRADOR"),
                        rs.getString ("CONTRASENA_ADMINISTRADOR"), null);

            }

            // Se cierra el ResultSet
            rs.close ();

            System.out.println (String.format ("- Se ha recuperado el administrador '%s'...", NombreAdministrador));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al obtener datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }

        return administrador;
    }

    public List <String> getAdminKeys () {
        List <String> llaves = new ArrayList <String> ();

        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql = "SELECT * FROM LLAVES";

            // Se ejecuta la sentencia y se obtiene el ResultSet con los
            // resutlados
            ResultSet rs = stmt.executeQuery (sql);

            // Se recorre el ResultSet y se crean objetos Cliente
            while (rs.next ()) {

                llaves.add (rs.getString ("LLAVE"));

            }

            // Se cierra el ResultSet
            rs.close ();

            System.out.println (String.format ("- Se han recuperado %d llaves...", llaves.size ()));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al obtener datos de la BBDD : %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
        return llaves;

    }

    public void deleteAdminKeys () {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            String sql7 = "DELETE FROM LLAVES;";

            int result7 = stmt.executeUpdate (sql7);

            System.out.println (String.format ("- Se han borrado %d llaves", result7));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al borrar llaves: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    public void regenerateAdminKeys () {
        this.deleteAdminKeys ();
        this.createAdminKeys ();
    }

    public void consumeAdminKey (String key) {
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);) {
            Statement stmt = con.createStatement ();

            int r = stmt.executeUpdate (String.format ("DELETE FROM LLAVES WHERE LLAVE = \'%s\'", key));

            Logger.getLogger (GestorBD.class.getName ()).log (Level.INFO,
                    String.format ("Se ha%s eliminado %d llave%s.", r == 1 ? "" : "n", r, r == 1 ? "" : "s"));
        }

        catch (Exception e) {
            Logger.getLogger (GestorBD.class.getName ()).log (Level.WARNING,
                    String.format ("* Error al borrar llaves: \"%s\"", e.getMessage ()));
            e.printStackTrace ();
        }
    }

    public void borrarDatos () {
        // Se abre la conexión y se obtiene el Statement
        try (Connection con = DriverManager.getConnection (GestorBD.CONNECTION_STRING);
                Statement stmt = con.createStatement ()) {
            // Se ejecuta la sentencia de borrado de datos
            String sql1 = "DELETE FROM PELICULA;";
            String sql2 = "DELETE FROM ADMINISTRADOR;";
            String sql3 = "DELETE FROM ESPECTADOR;";
            String sql4 = "DELETE FROM COMPLEMENTO;";
            String sql5 = "DELETE FROM SET_PELICULA;";
            String sql6 = "DELETE FROM ARRAY_SETPELICULA";
            String sql7 = "DELETE FROM LLAVES;";

            int result1 = stmt.executeUpdate (sql1);
            int result2 = stmt.executeUpdate (sql2);
            int result3 = stmt.executeUpdate (sql3);
            int result4 = stmt.executeUpdate (sql4);
            int result5 = stmt.executeUpdate (sql5);
            stmt.executeUpdate (sql6);
            int result7 = stmt.executeUpdate (sql7);

            System.out.println (String.format (
                    "- Se han borrado %d peliculas, %d Administradores, %d espectadores, %d complementos, %d setsPeliculas, %d llaves",
                    result1, result2, result3, result4, result5, result7));
        }
        catch (Exception ex) {
            System.err.println (String.format ("* Error al borrar datos de la BBDD: %s", ex.getMessage ()));
            ex.printStackTrace ();
        }
    }

    public static File getDBFile () {
        return new File (GestorBD.DATABASE_FILE);
    }
}