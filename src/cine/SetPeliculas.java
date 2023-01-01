package cine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import internals.Utils;
import internals.GestorBD;
import internals.HasID;
import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class SetPeliculas implements Comparable <SetPeliculas>, Treeable <SetPeliculas>, HasID {
    private static final Random random = new Random ();
    private static final int MIN_SIZE = 7;
    private static final int MAX_SIZE = 35;

    private static boolean DEFAULT_SET = false;
    private static final SetPeliculas DEFAULT = new SetPeliculas (new UUID (0L, 0L), null, "Set por defecto",
            null);

    private UUID id;
    private Administrador administrador;
    private String nombre;
    private SortedSet <Pelicula> peliculas;

    public SetPeliculas () {
        this ("", null);
    }

    public SetPeliculas (String nombre) {
        this (nombre, null);
    }

    public SetPeliculas (Collection <Pelicula> peliculas) {
        this (null, peliculas);
    }

    public SetPeliculas (String nombre, Collection <Pelicula> peliculas) {
        this (null, nombre, peliculas);
    }

    public SetPeliculas (Administrador administrador, String nombre, Collection <Pelicula> peliculas) {
        this (UUID.randomUUID (), administrador, nombre, peliculas);
    }

    public SetPeliculas (UUID id, Administrador administrador, String nombre, Collection <Pelicula> peliculas) {
        super ();

        this.id = id != null && ((SetPeliculas.isDefault (id)
                && Utils.isAmongstCallers ("cine.SetPeliculas")
                && (!SetPeliculas.DEFAULT_SET
                        || (SetPeliculas.DEFAULT_SET
                                && Utils.isAmongstCallers ("internals.GestorBD"))))
                || !SetPeliculas.isDefault (id))
                        ? id
                        : UUID.randomUUID ();
        this.setAdministrador (administrador);
        this.setNombre (nombre);
        this.setPeliculas (peliculas);

        if (this.isDefault ())
            SetPeliculas.DEFAULT_SET = true;
    }

    public SetPeliculas (SetPeliculas setPeliculas) {
        this (setPeliculas.id, setPeliculas.administrador, setPeliculas.nombre, setPeliculas.peliculas);
    }

    public SetPeliculas (SetPeliculas setPeliculas, Administrador administrador) {
        this (setPeliculas.id, administrador, setPeliculas.nombre, setPeliculas.peliculas);
    }

    public UUID getId () {
        return this.id;
    }

    public Administrador getAdministrador () {
        return this.administrador;
    }

    public void setAdministrador (Administrador administrador) {
        if (this.isDefault () && SetPeliculas.DEFAULT_SET)
            return;

        this.administrador = administrador;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        if (this.isDefault () && SetPeliculas.DEFAULT_SET
                && !Utils.isAmongstCallers ("cine.SetPeliculas")
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        if (nombre != null && !nombre.equals ("")) {
            this.nombre = nombre;

            return;
        }

        if (this.administrador == null) {
            this.nombre = this.id.toString ();

            return;
        }

        SetPeliculas array[] = this.administrador.getSetsPeliculas ().toArray (new SetPeliculas [0]);

        int nuevas = 0;
        for (int i = 0; i < array.length; nuevas += array [i++].getNombre ().toLowerCase ().contains ("nuevo set") ? 1
                : 0)
            ;

        this.nombre = String.format ("Nuevo set%s", nuevas == 0 ? "" : String.format (" #%d", nuevas + 1));
    }

    public SortedSet <Pelicula> getPeliculas () {
        return new TreeSet <Pelicula> (this.peliculas);
    }

    public void setPeliculas (Collection <Pelicula> peliculas) {
        if (this.isDefault () && SetPeliculas.DEFAULT_SET
                && !Utils.isAmongstCallers ("cine.Pelicula") && !Utils.isAmongstCallers ("cine.SetPeliculas")
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.peliculas = new TreeSet <Pelicula> (
                peliculas == null || peliculas.size () < SetPeliculas.MIN_SIZE
                        || peliculas.size () > SetPeliculas.MAX_SIZE
                                ? Collections.emptySet ()
                                : peliculas);

        if (peliculas == null)
            return;

        Pelicula array[] = peliculas.toArray (new Pelicula [0]);

        for (int i = 0; i < array.length; array [i++].addSet (this))
            ;
    }

    @Override
    public int hashCode () {
        return super.hashCode ();
    }

    @Override
    public boolean equals (Object o) {
        return o instanceof SetPeliculas && this.id.equals (((SetPeliculas) o).id);
    }

    @Override
    public int compareTo (SetPeliculas set) {
        if (set == null)
            return 1;

        if (this.nombre.equals (this.id.toString ()) && !set.nombre.equals (set.id.toString ()))
            return 1;

        if (!this.nombre.equals (this.id.toString ()) && set.nombre.equals (set.id.toString ()))
            return -1;

        if (this.nombre.equals (this.id.toString ()) && set.nombre.equals (set.id.toString ()))
            return this.id.compareTo (set.id);

        int comp;
        if ((comp = this.nombre.toLowerCase ().compareTo (set.nombre.toLowerCase ())) != 0)
            return comp;

        return this.id.compareTo (set.id);
    }

    @Override
    public String toString () {
        return "Set de películas (hash: " + this.hashCode () + ") " + "{\n\tID: " + this.id.toString ()
                + (this.isDefault () ? " (set predeterminado)" : "") + "\n\tNombre: " + this.nombre + "\n\tTamaño: "
                + this.size ()
                + "\n\tAdministrador: "
                + (this.administrador == null ? ""
                        : String.format ("%s (ID: %s)", this.administrador.getNombre (),
                                this.administrador.getId ().toString ()))
                + "\n\tPelículas: "
                + this.peliculas.toString ().replace ("\n", "\n\t\t").replace ("[", "{\n\t\t").replace ("]", "\n\t\t}")
                + "\n}";
    }

    public int size () {
        return this.peliculas.size ();
    }

    public static int minSize () {
        return SetPeliculas.MIN_SIZE;
    }

    public static int maxSize () {
        return SetPeliculas.MAX_SIZE;
    }

    public boolean add (Pelicula pelicula) {
        if (this.isDefault () && SetPeliculas.DEFAULT_SET
                && !Utils.isAmongstCallers ("cine.Pelicula") && !Utils.isAmongstCallers ("cine.SetPeliculas")
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return false;

        if (pelicula == null)
            return false;

        if (this.peliculas.contains (pelicula))
            return true;

        this.peliculas.add (pelicula);
        return pelicula.addSet (this);
    }

    public boolean add (Collection <Pelicula> peliculas) {
        if (this.peliculas == null || peliculas == null)
            return false;

        Pelicula array[] = peliculas.toArray (new Pelicula [0]);

        boolean all = true;
        for (int i = 0; i < array.length; all = all && this.add (array [i++]))
            ;

        return all;
    }

    public boolean remove (Pelicula pelicula) {
        if (this.isDefault () && SetPeliculas.DEFAULT_SET
                && !Utils.isAmongstCallers ("cine.Pelicula") && !Utils.isAmongstCallers ("cine.SetPeliculas")
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return false;

        if (!this.contains (pelicula))
            return true;

        this.peliculas.remove (pelicula);
        return pelicula.removeSet (this);
    }

    public boolean remove (Collection <Pelicula> peliculas) {
        if (this.peliculas == null || peliculas == null)
            return false;

        Pelicula array[] = peliculas.toArray (new Pelicula [0]);

        boolean all = true;
        for (int i = 0; i < array.length; all = all && this.remove (array [i++]))
            ;

        return all;
    }

    public boolean contains (Pelicula pelicula) {
        return pelicula != null && this.peliculas.contains (pelicula);
    }

    public boolean contains (Collection <Pelicula> peliculas) {
        Pelicula array[] = peliculas.toArray (new Pelicula [0]);

        for (int i = 0; i < array.length;)
            if (!this.contains (array [i++]))
                return false;

        return true;
    }

    public boolean isDefault () {
        return SetPeliculas.isDefault (this.id);
    }

    public static boolean isDefault (UUID id) {
        return id.equals (new UUID (0L, 0L));
    }

    protected static boolean isDefaultSet () {
        return SetPeliculas.DEFAULT_SET;
    }

    public static SetPeliculas getDefault () {
        if (!SetPeliculas.DEFAULT.contains (Pelicula.DEFAULT_PELICULAS))
            SetPeliculas.DEFAULT.add (Pelicula.DEFAULT_PELICULAS);

        return SetPeliculas.DEFAULT;
    }

    public static SetPeliculas random () throws InterruptedException {
        return SetPeliculas.random (Pelicula.getDefault ());
    }

    public static SetPeliculas random (int n) {
        return SetPeliculas.random (Pelicula.getDefault (), n);
    }

    public static SetPeliculas random (Collection <Pelicula> peliculas) {
        if (peliculas == null || peliculas.size () < SetPeliculas.MIN_SIZE)
            return null;

        return SetPeliculas.random (peliculas, SetPeliculas.random.nextInt (MIN_SIZE,
                Math.min (new HashSet <Pelicula> (peliculas).size (), SetPeliculas.MAX_SIZE)));
    }

    public static SetPeliculas random (Collection <Pelicula> peliculas, int n) {
        if (peliculas == null || peliculas.size () < SetPeliculas.MIN_SIZE)
            return null;

        ArrayList <Pelicula> list = new ArrayList <Pelicula> (
                new HashSet <Pelicula> (peliculas).stream ().limit (SetPeliculas.MAX_SIZE)
                        .collect (Collectors.toList ()));
        Collections.shuffle (list);

        return new SetPeliculas (list.stream ().limit (n).collect (Collectors.toSet ()));
    }

    public List <String> getNombresPeliculas () {
        return Pelicula.getNombres (Arrays.asList (this.peliculas.toArray (new Pelicula [0])));
    }

    public static BST <SetPeliculas> tree (Collection <SetPeliculas> values) {
        return SetPeliculas.tree (values, null, null);
    }

    public static BST <SetPeliculas> tree (Collection <SetPeliculas> values, Comparator <SetPeliculas> comp) {
        return SetPeliculas.tree (values, comp, null);
    }

    public static BST <SetPeliculas> tree (Collection <SetPeliculas> values, Filter <SetPeliculas> filter) {
        return SetPeliculas.tree (values, null, filter);
    }

    public static BST <SetPeliculas> tree (Collection <SetPeliculas> values, Comparator <SetPeliculas> comp,
            Filter <SetPeliculas> filter) {
        return new SetPeliculas ().bst (values, comp, filter);
    }

    public static List <SetPeliculas> fromJSON (File file) throws NullPointerException, IOException, JSONException {
        if (file == null)
            throw new NullPointerException (String.format ("No se puede pasar un archivo nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        if (!file.exists ())
            throw new IOException (
                    String.format ("No se pudo encontrar el archivo especificado (%s).", file.getAbsolutePath ()));

        if (!Files.probeContentType (file.toPath ()).equals ("application/json"))
            throw new JSONException (
                    String.format ("El archivo especificado, %s, no es un archivo JSON válido.",
                            file.getAbsolutePath ()));

        try {
            return SetPeliculas.fromJSON (Files.readString (file.toPath ()));
        }

        catch (IOException e) {
            throw new IOException (
                    String.format ("No se pudo abrir el archivo %s para recoger los datos.", file.getAbsolutePath ()));
        }

        catch (JSONException e) {
            throw new JSONException (String.format (
                    "El archivo especificado, %s, no es un archivo JSON válido que contenga un JSON Array.",
                    file.getAbsolutePath ()));
        }
    }

    public static List <SetPeliculas> fromJSON (String jstr) throws NullPointerException, JSONException {
        if (jstr == null)
            throw new NullPointerException (String.format ("No se puede pasar un string nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        JSONArray json;
        try {
            json = new JSONArray (jstr);
        }

        catch (JSONException e) {
            throw new JSONException (Utils.isAmongstCallers ("cine.SetPeliculas.fromJSON") ? ""
                    : "No se puede extraer un JSONArray válido de esta cadena de carácteres");
        }

        List <SetPeliculas> list = new ArrayList <SetPeliculas> ();
        SortedSet <Integer> errors = new TreeSet <Integer> ();
        for (int i = 0; i < json.length (); i++)
            try {
                list.add (SetPeliculas.fromJSONObject (json.getJSONObject (i)));
            }

            catch (JSONException e) {
                errors.add (i);
            }

        Logger.getLogger (Pelicula.class.getName ()).log (errors.isEmpty () ? Level.INFO : Level.WARNING,
                errors.isEmpty () ? "Se importaron todos los sets de películas."
                        : String.format (
                                "Hubo errores tratando de importar %d de los sets de películas (con índice %s).",
                                errors.size (), ((Supplier <String>) ( () -> {
                                    StringBuilder str = new StringBuilder ();

                                    Integer errorsArray[] = errors.toArray (new Integer [0]);
                                    for (int i = 0; i < errorsArray.length; i++) {
                                        str.append (errorsArray [i]);

                                        if (i != errorsArray.length - 1)
                                            str.append (", ");
                                    }

                                    return str.toString ();
                                })).get ()));

        return list;
    }

    private static SetPeliculas fromJSONObject (JSONObject o) throws NullPointerException, JSONException {
        return SetPeliculas.fromJSONObject (o, null);
    }

    private static SetPeliculas fromJSONObject (JSONObject o, GestorBD bd) throws NullPointerException, JSONException {
        if (o == null)
            throw new NullPointerException (String.format ("No se puede pasar un JSONObject nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        Set <String> fields = new HashSet <String> (Arrays.asList ("id", "administrador", "nombre", "peliculas"));

        String keys[] = o.keySet ().toArray (new String [0]);
        for (int i = 0; i < keys.length; i++)
            if (!fields.contains (keys [i]))
                throw new JSONException (String.format ("JSONObject inválido: clave %s desconocida.", keys [i]));

        UUID id = UUID.randomUUID ();
        Administrador administrador = null;
        String nombre = "";
        SortedSet <Pelicula> peliculas = new TreeSet <Pelicula> ();

        try {
            id = UUID.fromString (o.getString ("id"));
        }

        catch (JSONException | IllegalArgumentException e) {
            Logger.getLogger ("No se pudo encontrar un ID válido para el set.");
        }

        try {
            if (bd != null)
                administrador = bd.obtenerDatosAdministradorPorNombre (o.getString ("administrador"));
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar un nombre válido para el set de películas.");
        }

        try {
            nombre = o.getString ("nombre");
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar un nombre válido para el set de películas.");
        }

        try {
            JSONArray peliculasjson = o.getJSONArray ("peliculas");

            String str;
            for (int i = 0; i < peliculasjson.length (); i++)
                try {
                    peliculas.add (Pelicula.fromJSONObject (peliculasjson.getJSONObject (i)));
                }

                catch (Exception e) {
                    String.format ("%s no se puede convertir a una película válida.", peliculasjson.get (i));
                }
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar una lista de películas válida para el set de películas.");
        }

        return new SetPeliculas (nombre, peliculas);
    }

    public static String toJSON (SetPeliculas set) {
        return SetPeliculas.toJSON (Collections.singleton (set), false);
    }

    public static String toJSON (SetPeliculas set, boolean extra) {
        return SetPeliculas.toJSON (Collections.singleton (set), extra);
    }

    public static String toJSON (Collection <SetPeliculas> sets) {
        return SetPeliculas.toJSON (sets, false);
    }

    public static String toJSON (Collection <SetPeliculas> sets, boolean extra) throws NullPointerException {
        if (sets == null)
            throw new NullPointerException ("No se puede convertir una coleción nula de sets de películas a JSON.");

        SetPeliculas array[] = new TreeSet <SetPeliculas> (sets).toArray (new SetPeliculas [0]);
        StringBuilder str = new StringBuilder ();
        for (int i = 0; i < array.length; i++)
            str.append (
                    array [i] == null ? ""
                            : new StringBuilder ((extra && array [i].id != null)
                                    || (extra && array [i].administrador != null)
                                    || (array [i].nombre != null && !array [i].nombre.equals (""))
                                    || (array [i].peliculas != null && !array [i].peliculas.isEmpty ())
                                            ? ("{\n" + (extra && array [i].id != null
                                                    ? "\"id\" : " + array [i].id.toString ()
                                                    : "")
                                                    + (extra && array [i].administrador != null
                                                            ? "\"administrador\" : "
                                                                    + array [i].administrador.getNombre ().toString ()
                                                            : "")
                                                    + (array [i].nombre != null && !array [i].nombre.equals ("")
                                                            ? "    \"nombre\" : " + "\"" + array [i].nombre + "\""
                                                                    + (array [i].peliculas != null
                                                                            && !array [i].peliculas.isEmpty () ? ",\n"
                                                                                    : "")
                                                            : "")
                                                    + (array [i].peliculas != null && !array [i].peliculas.isEmpty ()
                                                            ? ("    \"peliculas\" : "
                                                                    + Pelicula.toJSON (array [i].peliculas).indent (4))
                                                                            .replace ("\"peliculas\" :     [",
                                                                                    "\"peliculas\" : [")
                                                            : "")
                                                    + "}")
                                            : ""));

        for (int i = 0; (i = str.indexOf ("}{", i)) != -1;)
            str.insert (i + 1, ",\n    ");

        return "[\n" + str.toString ().indent (4) + "]";
    }
}
