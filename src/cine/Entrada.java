package cine;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import internals.HasID;
import internals.Pair;
import internals.Settings;
import internals.Triplet;
import internals.Utils;
import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class Entrada implements Comparable <Entrada>, Treeable <Entrada>, HasID {
    private static final Calendar DEFAULT_FECHA = new Calendar.Builder ().setCalendarType ("gregorian")
            .setDate (2003, 2, 21).build ();

    private UUID id;
    private Espectador espectador;
    private Pelicula pelicula;
    private Calendar fecha;
    private Sala sala;
    private Butaca butaca;
    private ConcurrentMap <Complemento, BigInteger> complementos;
    private double valoracion;
    private BigDecimal precio;

    public Entrada () {
        this (new Espectador ());
    }

    public Entrada (Espectador espectador) {
        this (espectador, new Pelicula ());
    }

    public Entrada (Espectador espectador, Pelicula pelicula) {
        this (espectador, pelicula, Entrada.DEFAULT_FECHA);
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha) {
        this (espectador, pelicula, fecha, null, null);
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca) {
        this (espectador, pelicula, fecha, sala, butaca, new HashMap <Complemento, BigInteger> ());
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca,
            Map <Complemento, BigInteger> complementos) {
        this (espectador, pelicula, fecha, sala, butaca, complementos, Double.NaN);
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca,
            Map <Complemento, BigInteger> complementos, double valoracion) {
        this (espectador, pelicula, fecha, sala, butaca, complementos, valoracion, null);
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca,
            Map <Complemento, BigInteger> complementos, double valoracion, BigDecimal precio) {
        this (UUID.randomUUID (), espectador, pelicula, fecha, sala, butaca, complementos, valoracion, precio);
    }

    public Entrada (UUID id, Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca,
            Map <Complemento, BigInteger> complementos, double valoracion, BigDecimal precio) {
        super ();

        this.id = id;
        this.setEspectador (espectador);
        this.setPelicula (pelicula);
        this.setFecha (fecha);
        this.setSala (sala);
        this.setButaca (butaca);
        this.setComplementos (complementos);
        this.setValoracion (valoracion);
        this.setPrecio (precio);
    }

    public Entrada (Entrada entrada) {
        this (entrada.id, entrada.espectador, entrada.pelicula, entrada.fecha, entrada.sala, entrada.butaca,
                entrada.complementos, entrada.valoracion, entrada.precio);
    }

    public UUID getId () {
        return this.id;
    }

    public Espectador getEspectador () {
        return this.espectador;
    }

    public void setEspectador (Espectador espectador) {
        this.espectador = espectador == null ? this.espectador : espectador;
    }

    public Pelicula getPelicula () {
        return this.pelicula;
    }

    public void setPelicula (Pelicula pelicula) {
        this.pelicula = pelicula == null ? this.pelicula : pelicula;
    }

    public Calendar getFecha () {
        return this.fecha;
    }

    public static Calendar getDefaultFecha () {
        return Entrada.DEFAULT_FECHA;
    }

    public void setFecha (Calendar fecha) {
        this.fecha = fecha == null ? this.fecha : fecha;
    }

    public Sala getSala () {
        return this.sala;
    }

    public void setSala (Sala sala) {
        this.sala = sala == null ? this.sala : sala;
    }

    public Butaca getButaca () {
        return this.butaca;
    }

    public void setButaca (Butaca butaca) {
        this.butaca = this.sala == null || butaca == null || !this.sala.getButacas ().contains (butaca) ? this.butaca
                : butaca;
    }

    public ConcurrentMap <Complemento, BigInteger> getComplementos () {
        return this.complementos;
    }

    public void setComplementos (Map <Complemento, BigInteger> complementos) {
        if (complementos == null) {
            if (this.complementos == null)
                this.complementos = new ConcurrentHashMap <Complemento, BigInteger> ();

            return;
        }

        this.complementos = new ConcurrentHashMap <Complemento, BigInteger> (complementos);
    }

    public double getValoracion () {
        return this.valoracion;
    }

    public void setValoracion (double valoracion) {
        this.valoracion = Double.isNaN (valoracion) || valoracion < 1 || valoracion > 10 ? Double.NaN : valoracion;
    }

    public BigDecimal getPrecio () {
        return this.precio;
    }

    public static BigDecimal getDefaultPrecio () {
        return Settings.getPrecioEntrada ();
    }

    public void setPrecio () {
        this.precio = Settings.getPrecioEntrada ().subtract (Settings.getPrecioEntrada ()
                .multiply (new BigDecimal (Settings.getDescuentoEspectador ()).multiply (
                        Settings.getDiaEspectador () == Utils.getCurrentDay () ? BigDecimal.ONE : BigDecimal.ZERO)
                        .scaleByPowerOfTen (-2)))
                .setScale (2, RoundingMode.HALF_EVEN);
    }

    public void setPrecio (BigDecimal precio) {
        if (precio == null || precio.signum () != 1
                || precio.compareTo (Consumible.getMaxPrecio ()) > 0) {
            if (this.precio == null)
                this.setPrecio ();

            return;
        }

        this.precio = new BigDecimal (precio.toString ().replace (",", ".")).setScale (2, RoundingMode.HALF_EVEN);
    }

    @Override
    public int compareTo (Entrada entrada) {
        if (entrada == null)
            return 1;

        if (this.equals (entrada))
            return 0;

        int comp;
        if ((comp = this.fecha.compareTo (entrada.fecha)) != 0)
            return comp;

        if (this.pelicula == null) {
            if (entrada.pelicula == null)
                return 0;

            return -1;
        }

        if ((comp = this.pelicula.compareTo (entrada.pelicula)) != 0)
            return comp;

        return this.id.compareTo (entrada.id);
    }

    @Override
    public int hashCode () {
        return super.hashCode ();
    }

    @Override
    public boolean equals (Object o) {
        return o instanceof Entrada && this.id.equals (((Entrada) o).id);
    }

    @Override
    public String toString () {
        return String.format (
                "Entrada (hash: %d) {%n\tID: %s%n\tEspectador: %s (ID: %s)%n\tPelícula: %s (ID: %s)%n\tFecha: %s%n\tSala: %s%n%s%n\tComplementos: %s%n\tPrecio: %.2f €%n\tValoración: %s%n\tTotal: %.2f €%n}",
                this.hashCode (), this.id.toString (), this.espectador.getNombre (), this.espectador.getId (),
                this.pelicula == null ? "-" : this.pelicula.getNombre (),
                this.pelicula == null ? "-" : this.pelicula.getId (),
                this.fecha == null ? "--/--/--" : ((Supplier <String>) ( () -> {
                    Triplet <Integer, Integer, Integer> date = Utils.getDate (this.fecha);

                    return String.format ("%d/%02d/%d", date.x, date.y, date.z);
                })).get (), ((Supplier <String>) ( () -> {
                    int i = Sala.indexOf (this.sala);

                    return i == -1 ? "-" : String.format ("%d", i);
                })).get (), ((Supplier <String>) ( () -> {
                    Pair <Integer, Integer> i = this.sala == null ? null : this.sala.indexOf (this.butaca);

                    return String.format ("\tButaca: %s%n\tFila: %s", i == null ? "-" : Integer.toString (i.x + 1),
                            i == null ? "-" : Integer.toString (i.y + 1));
                })).get (), ((Supplier <String>) ( () -> {
                    if (this.complementos.isEmpty ())
                        return "ninguno";

                    StringBuilder str = new StringBuilder ("\t{");

                    List <Map.Entry <Complemento, BigInteger>> c = this.complementos.entrySet ().stream ()
                            .collect (Collectors.toList ());
                    for (int i = 0; i < c.size (); i++)
                        str.append (String.format ("\t\t%n%s (%.2f €%s, x%d),", c.get (i).getKey ().getNombre (),
                                c.get (i).getKey ().getPrecio ().doubleValue (),
                                c.get (i).getKey ().getDescuento () == 0 ? ""
                                        : String.format ("descuento del %d %%", c.get (i).getKey ().getDescuento ()),
                                c.get (i).getValue ().intValue ()));

                    return str.deleteCharAt (str.length () - 1).append ("\n\t}").toString ();
                })).get (), this.precio.doubleValue (),
                Double.isNaN (this.valoracion) || this.valoracion < 1 || this.valoracion > 10 ? "-"
                        : String.format ("%.1f", this.valoracion),
                this.total ().doubleValue ());
    }

    public BigDecimal total () throws ArithmeticException {
        BigDecimal p = this.precio.add (Complemento.sum (this.complementos));

        if ((p = p.setScale (2, RoundingMode.HALF_EVEN)).compareTo (Consumible.getMaxPrecio ()) > 0)
            throw new ArithmeticException (
                    "Se ha sobrepasado el precio de entrada máximo almacenable en la base de datos");

        return p;
    }

    public static BST <Entrada> tree (Collection <Entrada> values) {
        return Entrada.tree (values, null, null);
    }

    public static BST <Entrada> tree (Collection <Entrada> values, Comparator <Entrada> comp) {
        return Entrada.tree (values, comp, null);
    }

    public static BST <Entrada> tree (Collection <Entrada> values, Filter <Entrada> filter) {
        return Entrada.tree (values, null, filter);
    }

    public static BST <Entrada> tree (Collection <Entrada> values, Comparator <Entrada> comp, Filter <Entrada> filter) {
        return new Entrada ().bst (values, comp, filter);
    }

    public static List <Usuario> fromJSON (File file) throws NullPointerException, IOException, JSONException {
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
            return Usuario.fromJSON (Files.readString (file.toPath ()));
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

    public static List <Entrada> fromJSON (String jstr) throws NullPointerException, JSONException {
        if (jstr == null)
            throw new NullPointerException (String.format ("No se puede pasar un string nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        JSONArray json;
        try {
            json = new JSONArray (jstr);
        }

        catch (JSONException e) {
            throw new JSONException (Utils.isAmongstCallers ("cine.Entrada.fromJSON") ? ""
                    : "No se puede extraer un JSONArray válido de esta cadena de carácteres");
        }

        List <Entrada> list = new ArrayList <Entrada> ();
        SortedSet <Integer> errors = new TreeSet <Integer> ();
        for (int i = 0; i < json.length (); i++)
            try {
                list.add (Entrada.fromJSONObject (json.getJSONObject (i)));
            }

            catch (JSONException e) {
                errors.add (i);
            }

        Logger.getLogger (Usuario.class.getName ()).log (errors.isEmpty () ? Level.INFO : Level.WARNING,
                errors.isEmpty () ? "Se importaron todas las entradas."
                        : String.format ("Hubo errores tratando de importar %d de las entradas (con índice %s).",
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

    private static Entrada fromJSONObject (JSONObject o) throws NullPointerException, JSONException {
        if (o == null)
            throw new NullPointerException ("No se puede obtener una entrada a partir de un JSONObject nulo.");

        final Set <String> fields = Arrays.asList (new String [] {
                "id", "espectador", "pelicula", "fecha", "butaca", "complementos", "valoracion", "precio"
        }).stream ().collect (Collectors.toSet ());

        String keys[] = o.keySet ().toArray (new String [0]);
        for (int i = 0; i < keys.length; i++)
            if (!fields.contains (keys [i]))
                throw new JSONException (String.format ("JSONObject inválido: clave %s desconocida", keys [i]));

        UUID id = null;
        UUID espectador = null;
        Pelicula pelicula = null;
        Date fecha = null;
        Sala sala = null;
        Butaca butaca = null;
        Map <Complemento, BigInteger> complementos = new HashMap <Complemento, BigInteger> ();
        double valoracion = 0.0f;
        BigDecimal precio = null;

        if (o.has ("id"))
            try {
                id = UUID.fromString (o.getString ("id"));
            }

            catch (IllegalArgumentException e) {
                Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                        "No se pudo obtener un ID válido a partir del JSONObject.");
            }

        try {
            espectador = UUID.fromString (o.getString ("espectador"));
        }

        catch (IllegalArgumentException | JSONException e) {
            Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener un ID de espectador válido del JSONObject.");
        }

        try {
            pelicula = Pelicula.fromJSONObject (o.getJSONObject ("pelicula"));
        }

        catch (JSONException e) {
            Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener una película válida del JSONObject.");
        }

        try {
            fecha = new SimpleDateFormat ("yyyy-MM-dd").parse (o.getString ("fecha"), new ParsePosition (0));
        }

        catch (JSONException e) {
            Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener una fecha válida del JSONObject.");
        }

        try {
            sala = Sala.getSalas ().get ((o.getJSONObject ("butaca").getInt ("sala")));
        }

        catch (JSONException e) {
            Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener una sala válida del JSONObject.");
        }

        if (sala != null)
            try {
                butaca = sala.getButacas ().get (o.getJSONObject ("butaca").getInt ("fila") * Sala.getColumnas ()
                        + o.getJSONObject ("sala").getInt ("butaca"));
            }

            catch (JSONException e) {
                Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                        "No se pudo obtener una butaca válida del JSONObject.");
            }

        try {
            JSONArray jsonarray = o.getJSONArray ("complementos");
            for (int i = 0; i < jsonarray.length (); i++)
                complementos.put (
                        Complemento.fromJSONObject (jsonarray.getJSONObject (i).getJSONObject ("complemento")),
                        jsonarray.getJSONObject (i).getBigInteger ("cantidad"));
        }

        catch (JSONException e) {
            Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener una lista de complementos válida del JSONObject.");
            complementos.clear ();
        }

        try {
            valoracion = o.getDouble ("valoracion");
        }

        catch (JSONException e) {
            Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener una valoración personal válida del JSONObject.");
        }

        try {
            precio = o.getBigDecimal ("precio");
        }

        catch (JSONException e) {
            Logger.getLogger (Entrada.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener un precio del JSONObject.");
        }

        return new Entrada (id, new Espectador (id, "", "", Espectador.getDefaultEdad (), null, null, null), pelicula,
                new Calendar.Builder ().setCalendarType ("gregorian").setInstant (fecha).build (), sala, butaca,
                complementos, valoracion, precio);
    }

    public static String toJSON (Entrada entrada) throws NullPointerException {
        return Entrada.toJSON (Collections.singleton (entrada), false);
    }

    public static String toJSON (Entrada entrada, boolean extra) throws NullPointerException {
        return Entrada.toJSON (Collections.singleton (entrada), extra);
    }

    public static String toJSON (Collection <Entrada> entradas) throws NullPointerException {
        return Entrada.toJSON (entradas, false);
    }

    public static String toJSON (Collection <Entrada> entradas, boolean extra) throws NullPointerException {
        if (entradas == null)
            throw new NullPointerException ("No se puede exportar a JSON una colección nula de entradas.");

        JSONArray json = new JSONArray ();

        Entrada array[] = new TreeSet <Entrada> (entradas).toArray (new Entrada [0]);
        StringBuilder str = new StringBuilder ();
        for (int i[] = new int [1]; i [0] < array.length; i [0]++)
            str.append (array [i [0]] == null ? ""
                    : new StringBuilder ((extra && array [i [0]].id != null) || (array [i [0]].espectador != null)
                            || (array [i [0]].fecha != null) || (array [i [0]].sala != null)
                            || (array [i [0]].butaca != null) || (array [i [0]].complementos != null)
                            || (!((Double) array [i [0]].valoracion).isNaN () && array [i [0]].valoracion < 1
                                    && array [i [0]].valoracion > 10)
                            || (array [i [0]].precio != null && array [i [0]].precio.signum () == 1)
                                    ? ("{\n" + (extra && array [i [0]].id != null
                                            ? "    \"id\" : " + array [i [0]].id.toString () + ",\n"
                                            : "")
                                            + (array [i [0]].espectador != null
                                                    ? "    \"espectador\" : "
                                                            + array [i [0]].espectador.getId ().toString ()
                                                            + ",\n"
                                                    : "")
                                            + (array [i [0]].pelicula != null
                                                    ? "    \"pelicula\" : " + array [i [0]].pelicula.toJSONObject ()
                                                            .toString (8).indent (4).replace ("}\n", "}")
                                                            .replace ("    {", "{") + ",\n"
                                                    : "")
                                            + (array [i [0]].fecha != null
                                                    ? "    \"fecha\" : " + new SimpleDateFormat ("yyyy-MM-dd")
                                                            .format (array [i [0]].fecha.getTime ()) + ",\n"
                                                    : "")
                                            + (array [i [0]].sala != null ? "\"butaca\" : {\n    \"sala\" : "
                                                    + Sala.indexOf (array [i [0]].sala) + ((Supplier <String>) ( () -> {
                                                        Pair <Integer, Integer> index = array [i [0]].sala
                                                                .indexOf (array [i [0]].butaca);

                                                        return ",\n    \"fila\" : " + index.x + ",\n    \"butaca\" : "
                                                                + index.y + "\n}";
                                                    })).get () + ",\n" : "")
                                            + (array [i [0]].complementos != null
                                                    && !array [i [0]].complementos.isEmpty ()
                                                            ? ("\"complementos\" : "
                                                                    + ((Supplier <JSONArray>) ( () -> {
                                                                        JSONArray jsonar = new JSONArray ();

                                                                        List <Map.Entry <Complemento, BigInteger>> entries = array [i [0]].complementos
                                                                                .entrySet ()
                                                                                .stream ()
                                                                                .collect (Collectors.toList ());

                                                                        for (int j = 0; j < entries.size (); j++)
                                                                            jsonar.put (new JSONObject ()
                                                                                    .put ("complemento",
                                                                                            entries.get (j).getKey ()
                                                                                                    .toJSONObject (
                                                                                                            true))
                                                                                    .put ("cantidad", entries.get (j)
                                                                                            .getValue ().intValue ()));

                                                                        return jsonar;
                                                                    })).get ().toString (4).replace (
                                                                            "\"complementos\" :     [",
                                                                            "\"complementos\" : [")
                                                                    + ",\n").indent (4)
                                                            : "")
                                            + (!((Double) array [i [0]].valoracion).isNaN ()
                                                    && array [i [0]].valoracion >= 1 && array [i [0]].valoracion <= 10
                                                            ? "\"valoracion: \"" + array [i [0]].valoracion + ",\n"
                                                            : "")
                                            + (array [i [0]].precio != null && array [i [0]].precio.signum () == 1
                                                    && array [i [0]].precio
                                                            .compareTo (Consumible.getMaxPrecio ()) != 1
                                                                    ? "    \"precio\" : " + array [i [0]].precio
                                                                    : "")
                                            + "\n}")
                                    : ""));

        for (int i = 0; (i = str.indexOf ("}{", i)) != -1;)
            str.insert (i + 1, ",\n");

        return "[\n" + str.toString ().indent (4).replace (",\n\n}", "\n}") + "]";
    }

    private JSONObject toJSONObject () {
        return toJSONObject (false);
    }

    private JSONObject toJSONObject (boolean extra) {
        JSONObject o = new JSONObject ().put ("espectador", this.espectador.toJSONObject (true)).put ("pelicula",
                this.pelicula.toJSONObject (true))
                .put ("fecha", new SimpleDateFormat ("yyyy-MM-dd").format (this.fecha.getTime ()))
                .put ("butaca",
                        this.sala == null ? new JSONObject ().put ("sala", -1).put ("fila", -1).put ("butaca", -1)
                                : ((Supplier <JSONObject>) ( () -> {
                                    Pair <Integer, Integer> i = this.sala.indexOf (this.butaca);
                                    return new JSONObject ().put ("sala", Sala.indexOf (this.sala)).put ("fila", i.x)
                                            .put ("butaca",
                                                    i.y);
                                })).get ())
                .put ("complementos", ((Supplier <JSONArray>) ( () -> {
                    JSONArray array = new JSONArray ();

                    List <Map.Entry <Complemento, BigInteger>> entries = this.complementos.entrySet ()
                            .stream ().collect (Collectors.toList ());

                    for (int i = 0; i < entries.size (); i++)
                        array.put (new JSONObject ()
                                .put ("complemento", entries.get (i).getKey ().toJSONObject (true))
                                .put ("cantidad", entries.get (i).getValue ().intValue ()));

                    return array;
                })).get ()).put ("valoracion", this.valoracion).put ("precio", this.precio);

        if (extra)
            o.put ("id", this.id.toString ());

        return o;
    }
}
