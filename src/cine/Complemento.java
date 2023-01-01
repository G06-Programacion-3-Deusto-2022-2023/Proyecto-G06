package cine;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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

import internals.HasID;
import internals.Utils;
import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class Complemento implements Treeable <Complemento>, Comparable <Complemento>, HasID {
    private static final BigDecimal DEFAULT_PRECIO = BigDecimal.ONE;

    private static short DEFAULT_SET;
    private static final int NDEFAULT_COMPLEMENTOS = 15;

    // Los precios son un poquito un robo pero que conste que los he cogido de
    // la página de Cinesa.
    private static final Set <Complemento> DEFAULT_COMPLEMENTOS = Arrays.asList (new Complemento [] {
            new Complemento (new UUID (0, 0), "Palomitas grandes", BigDecimal.valueOf (6.2f), 0),
            new Complemento (new UUID (0, 1), "Palomitas medianas", BigDecimal.valueOf (5.8f), 0),
            new Complemento (new UUID (0, 2), "Coca-Cola 1 L", BigDecimal.valueOf (5.8f), 0),
            new Complemento (new UUID (0, 3), "Coca-Cola 750 mL", BigDecimal.valueOf (5.2f), 0),
            new Complemento (new UUID (0, 4), "Kas Naranja 1 L", BigDecimal.valueOf (5.8f), 0),
            new Complemento (new UUID (0, 5), "Kas Naranja 750 mL", BigDecimal.valueOf (5.2f), 0),
            new Complemento (new UUID (0, 6), "Kas Limón 1 L", BigDecimal.valueOf (5.8f), 0),
            new Complemento (new UUID (0, 7), "Kas Limón 750 mL", BigDecimal.valueOf (5.2f), 0),
            new Complemento (new UUID (0, 8), "Nestea 1 L", BigDecimal.valueOf (5.8f), 0),
            new Complemento (new UUID (0, 9), "Nestea 750 mL", BigDecimal.valueOf (5.2f), 0),
            new Complemento (new UUID (0, 10), "Agua 750 mL", BigDecimal.valueOf (3.0f), 0),
            new Complemento (new UUID (0, 11), "Kit Kat", BigDecimal.valueOf (2.5f), 0),
            new Complemento (new UUID (0, 12), "Kinder Bueno", BigDecimal.valueOf (2.5f), 0),
            new Complemento (new UUID (0, 13), "Perrito caliente", BigDecimal.valueOf (6.0f), 0),
            new Complemento (new UUID (0, 14), "Tiras de pollo", BigDecimal.valueOf (6.2f), 0),
    }).stream ()
            .collect (Collectors.toCollection (TreeSet::new));

    private UUID id;
    private String nombre;
    private BigDecimal precio;
    private int descuento;

    public Complemento () {
        this (null, null);
    }

    public Complemento (String nombre) {
        this (nombre, Complemento.DEFAULT_PRECIO);
    }

    public Complemento (BigDecimal precio) {
        this (null, precio);
    }

    public Complemento (String nombre, BigDecimal precio) {
        this (nombre, precio, 0);
    }

    public Complemento (String nombre, BigDecimal precio, int descuento) {
        this (UUID.randomUUID (), nombre, precio, descuento);
    }

    public Complemento (UUID id, String nombre, BigDecimal precio, int descuento) {
        super ();

        this.id = id != null && ((Complemento.isDefault (id)
                && Utils.isAmongstCallers ("cine.Complemento")
                && ((Complemento.DEFAULT_SET & (short) (1 << id.getLeastSignificantBits ())) == 0
                        || ((Complemento.DEFAULT_SET & (short) (1 << id.getLeastSignificantBits ())) != 0
                                && (Utils.isAmongstCallers ("internals.GestorBD")
                                        || Utils.isAmongstCallers ("internals.swing.ComplementosTableModel")))))
                || !Complemento.isDefault (id))
                        ? id
                        : UUID.randomUUID ();
        this.setNombre (nombre);
        this.setPrecio (precio);
        this.setDescuento (descuento);

        if (this.isDefault ())
            Complemento.DEFAULT_SET |= (short) (1 << this.id.getLeastSignificantBits ());
    }

    public Complemento (Complemento complemento) {
        this (complemento.id, complemento.nombre, complemento.precio, complemento.descuento);
    }

    public UUID getId () {
        return this.id;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        this.nombre = nombre == null || nombre.equals ("") ? this.id.toString () : nombre;
    }

    public BigDecimal getPrecio () {
        return this.precio;
    }

    public void setPrecio (BigDecimal precio) {
        this.precio = precio == null || precio.signum () != 1
                ? this.precio == null ? Complemento.DEFAULT_PRECIO : this.precio
                : precio;
    }

    public int getDescuento () {
        return this.descuento;
    }

    public void setDescuento (int descuento) {
        if (descuento < 0 || descuento >= 100)
            return;

        this.descuento = descuento;
        this.setPrecio (this.precio);
    }

    public static BigDecimal getDefaultPrecio () {
        return new BigDecimal (Complemento.DEFAULT_PRECIO.toString ());
    }

    @Override
    public int hashCode () {
        return super.hashCode ();
    }

    @Override
    public boolean equals (Object o) {
        return o instanceof Complemento && this.id.equals (((Complemento) o).id);
    }

    @Override
    public int compareTo (Complemento complemento) {
        if (complemento == null)
            return 1;

        if (this.nombre.equals (this.id.toString ()) && !complemento.nombre.equals (complemento.id.toString ()))
            return 1;

        if (!this.nombre.equals (this.id.toString ()) && complemento.nombre.equals (complemento.id.toString ()))
            return -1;

        if (this.nombre.equals (this.id.toString ()) && complemento.nombre.equals (complemento.id.toString ()))
            return this.id.compareTo (complemento.id);

        int comp;
        if ((comp = this.nombre.toLowerCase ().compareTo (complemento.nombre.toLowerCase ())) != 0)
            return comp;

        return this.id.compareTo (complemento.id);
    }

    @Override
    public String toString () {
        return String.format ("Complemento (%d) {%n\tID: %s%n\tNombre: %s%n\tPrecio: %.2f €%n\tDescuento: %d %%%n}",
                this.hashCode (), this.id.toString (), this.nombre, this.precio.doubleValue (), this.descuento);
    }

    public BigDecimal aplicarDescuento (int descuento) {
        return this.precio.subtract (this.precio.multiply (new BigDecimal (descuento).scaleByPowerOfTen (-2)))
                .setScale (2, RoundingMode.HALF_EVEN);
    }

    public boolean isDefault () {
        return Complemento.isDefault (this.id);
    }

    public static boolean isDefault (UUID id) {
        return id.getMostSignificantBits () == 0 && id.getLeastSignificantBits () >= 0
                && id.getLeastSignificantBits () < Complemento.NDEFAULT_COMPLEMENTOS;
    }

    public static Set <Complemento> getDefault () {
        return new TreeSet <Complemento> (Complemento.DEFAULT_COMPLEMENTOS);
    }

    public static BST <Complemento> tree (Collection <Complemento> values) {
        return Complemento.tree (values, null, null);
    }

    public static BST <Complemento> tree (Collection <Complemento> values, Comparator <Complemento> comp) {
        return Complemento.tree (values, comp, null);
    }

    public static BST <Complemento> tree (Collection <Complemento> values, Filter <Complemento> filter) {
        return Complemento.tree (values, null, filter);
    }

    public static BST <Complemento> tree (Collection <Complemento> values, Comparator <Complemento> comp,
            Filter <Complemento> filter) {
        return new Complemento ().bst (values, comp, filter);
    }

    public static List <Complemento> fromJSON (File file) throws NullPointerException, IOException, JSONException {
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
            return Complemento.fromJSON (Files.readString (file.toPath ()));
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

    public static List <Complemento> fromJSON (String jstr) throws NullPointerException, JSONException {
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

        List <Complemento> list = new ArrayList <Complemento> ();
        SortedSet <Integer> errors = new TreeSet <Integer> ();
        for (int i = 0; i < json.length (); i++)
            try {
                list.add (Complemento.fromJSONObject (json.getJSONObject (i)));
            }

            catch (JSONException e) {
                errors.add (i);
            }

        Logger.getLogger (Pelicula.class.getName ()).log (errors.isEmpty () ? Level.INFO : Level.WARNING,
                errors.isEmpty () ? "Se importaron todas las películas."
                        : String.format ("Hubo errores tratando de importar %d de las películas (con índice %s).",
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

    private static Complemento fromJSONObject (JSONObject o) throws NullPointerException, JSONException {
        if (o == null)
            throw new NullPointerException (String.format ("No se puede pasar un JSONObject nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        final Set <String> fields = new HashSet <String> (
                Arrays.asList (new String [] { "id", "nombre", "precio", "descuento" }));

        String keys[] = o.keySet ().toArray (new String [0]);
        for (int i = 0; i < keys.length; i++)
            if (!fields.contains (keys [i]))
                throw new JSONException (String.format ("JSONObject inválido: clave %s desconocida.", keys [i]));

        UUID id = null;
        String nombre = "";
        BigDecimal precio = Complemento.DEFAULT_PRECIO;
        int descuento = 0;

        if (o.has ("id"))
            try {
                id = UUID.fromString (o.getString ("id"));
            }

            catch (JSONException | IllegalArgumentException e) {
                Logger.getLogger (Complemento.class.getName ()).log (Level.WARNING,
                        "No se pudo encontrar un ID válido para el complemento.");
            }

        try {
            nombre = o.getString ("nombre");
        }

        catch (JSONException e) {
            Logger.getLogger (Complemento.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar un nombre válido para el complemento.");
        }

        try {
            BigDecimal temp;
            if ((temp = o.getBigDecimal ("precio")).signum () != 1)
                throw new ArithmeticException ("El precio debe ser un número positivo.");

            precio = temp;
        }

        catch (ArithmeticException | JSONException e) {
            Logger.getLogger (Complemento.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar un precio válido para el complemento.");
        }

        try {
            int temp;
            if ((temp = o.getInt ("descuento")) < 0 || temp >= 100)
                throw new ArithmeticException ("El descuento no puede ser negativo o mayor que 100");

            descuento = temp;
        }

        catch (ArithmeticException | JSONException e) {
            Logger.getLogger (Complemento.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar un descuento válido para el complemento.");
        }

        return new Complemento (id, nombre, precio, descuento);
    }

    public static String toJSON (Complemento complemento) throws NullPointerException {
        return Complemento.toJSON (Collections.singleton (complemento), false);
    }

    public static String toJSON (Complemento complemento, boolean extra) throws NullPointerException {
        return Complemento.toJSON (Collections.singleton (complemento), extra);
    }

    public static String toJSON (Collection <Complemento> complementos) throws NullPointerException {
        return Complemento.toJSON (complementos, false);
    }

    public static String toJSON (Collection <Complemento> complementos, boolean extra) throws NullPointerException {
        if (complementos == null)
            throw new NullPointerException ("No se puede convertir una coleción nula de complementos a JSON.");

        JSONArray json = new JSONArray ();

        Complemento array[] = new TreeSet <Complemento> (complementos).toArray (new Complemento [0]);
        for (int i = 0; i < array.length; json.put (array [i++].toJSONObject (extra)))
            ;

        StringBuilder str = new StringBuilder (json.toString ().replace ("[", "[\n    ")
                .replace ("{\"", "{\n\"").replace ("}", "\n    }")
                .replace ("},", "},\n    ").replace ("]", "\n]")
                .replace (",\"", ",\n\"").replace ("\":", "\" : ").replace ("\n\"", "\n        \""));

        return str.toString ();
    }

    private JSONObject toJSONObject () {
        return this.toJSONObject (false);
    }

    private JSONObject toJSONObject (boolean extra) {
        JSONObject o = new JSONObject ().put ("nombre", this.nombre)
                .put ("precio", this.precio.setScale (2, RoundingMode.HALF_EVEN))
                .put ("descuento", this.descuento);

        if (extra)
            o.put ("id", this.id.toString ());

        return o;
    }
}
