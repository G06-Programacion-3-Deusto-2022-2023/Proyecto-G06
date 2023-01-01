package cine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import internals.HasID;
import internals.Pair;
import internals.Settings;
import internals.Triplet;
import internals.Utils;
import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class Entrada implements Comparable <Entrada>, Treeable <Entrada>, HasID {
    private UUID id;
    private Espectador espectador;
    private Pelicula pelicula;
    private Calendar fecha;
    private Sala sala;
    private Butaca butaca;
    private Map <Complemento, Integer> complementos;
    private double valoracion;
    private BigDecimal precio;

    public Entrada () {
        this (new Espectador ());
    }

    public Entrada (Espectador espectador) {
        this (espectador, new Pelicula ());
    }

    public Entrada (Espectador espectador, Pelicula pelicula) {
        this (espectador, pelicula,
                new Calendar.Builder ().setCalendarType ("gregorian").setDate (2003, 2, 21).build ());
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha) {
        this (espectador, pelicula, fecha, null, null);
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca) {
        this (espectador, pelicula, fecha, sala, butaca, new HashMap <Complemento, Integer> ());
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca,
            Map <Complemento, Integer> complementos) {
        this (espectador, pelicula, fecha, sala, butaca, complementos, Double.NaN);
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca,
            Map <Complemento, Integer> complementos, double valoracion) {
        this (UUID.randomUUID (), espectador, pelicula, fecha, sala, butaca, complementos, valoracion);
    }

    public Entrada (UUID id, Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, Butaca butaca,
            Map <Complemento, Integer> complementos, double valoracion) {
        super ();

        this.id = id;
        this.setEspectador (espectador);
        this.setPelicula (pelicula);
        this.setFecha (fecha);
        this.setSala (sala);
        this.setButaca (butaca);
        this.setComplementos (complementos);
        this.setValoracion (valoracion);
        this.setPrecio ();
    }

    public Entrada (Entrada entrada) {
        this (entrada.id, entrada.espectador, entrada.pelicula, entrada.fecha, entrada.sala, entrada.butaca,
                entrada.complementos, entrada.valoracion);
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

    public Map <Complemento, Integer> getComplementos () {
        return this.complementos;
    }

    public void setComplementos (Map <Complemento, Integer> complementos) {
        if (complementos == null) {
            if (this.complementos == null)
                this.complementos = new TreeMap <Complemento, Integer> ();

            return;
        }

        this.complementos = complementos;
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
        this.precio = Settings.getPrecioEntrada ()
                .subtract (new BigDecimal (Settings.getDescuentoEspectador ()).scaleByPowerOfTen (-2)
                        .multiply (Settings.getDiaEspectador () == Utils.getCurrentDay () ? BigDecimal.ONE
                                : BigDecimal.ZERO))
                .setScale (2, RoundingMode.HALF_EVEN);
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
                this.pelicula.getNombre (), this.pelicula.getId (), ((Supplier <String>) ( () -> {
                    Triplet <Integer, Integer, Integer> date = Utils.getDate (this.fecha);

                    return String.format ("%d/%02d/%d", date.x, date.y, date.z);
                })).get (), ((Supplier <String>) ( () -> {
                    int i = Sala.indexOf (this.sala);

                    return i == -1 ? "-" : String.format ("%d", i);
                })).get (), (((Supplier <String>) ( () -> {
                    Pair <Integer, Integer> i = this.sala == null ? null : this.sala.indexOf (this.butaca);

                    return String.format ("\tButaca: %s%n\tFila: %s", i == null ? "-" : Integer.toString (i.x + 1),
                            i == null ? "-" : Integer.toString (i.y + 1));
                })).get ()), ((Supplier <String>) ( () -> {
                    if (this.complementos.isEmpty ())
                        return "ninguno";

                    StringBuilder str = new StringBuilder ("\t{");

                    List <Map.Entry <Complemento, Integer>> c = this.complementos.entrySet ().stream ()
                            .collect (Collectors.toList ());
                    for (int i = 0; i < c.size (); i++)
                        str.append (String.format ("\t\t%n%s (%.2f €%s, x%d),", c.get (i).getKey ().getNombre (),
                                c.get (i).getKey ().getPrecio ().doubleValue (),
                                c.get (i).getKey ().getDescuento () == 0 ? ""
                                        : String.format ("descuento del %d %%", c.get (i).getKey ().getDescuento ()),
                                c.get (i).getValue ().intValue ()));

                    return str.deleteCharAt (str.length () - 1).append ("%n\t}").toString ();
                })).get (), this.precio.doubleValue (),
                Double.isNaN (this.valoracion) || this.valoracion < 1 || this.valoracion > 10 ? "-"
                        : String.format ("%.1f", this.valoracion),
                this.total ().doubleValue ());
    }

    public BigDecimal total () {
        BigDecimal p = new BigDecimal (this.precio.doubleValue ());

        ArrayList <Map.Entry <Complemento, Integer>> keyValueArray = new ArrayList <Map.Entry <Complemento, Integer>> (
                complementos.entrySet ());
        for (int i = 0; i < complementos.size (); i++)
            p = p.add (keyValueArray.get (i).getKey ().getPrecio ()
                    .multiply (new BigDecimal (keyValueArray.get (i).getValue ())));

        return p.setScale (2, RoundingMode.HALF_EVEN);
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
}
