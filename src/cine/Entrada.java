package cine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import internals.HasID;
import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class Entrada implements Comparable <Entrada>, Treeable <Entrada>, HasID {
    private static final BigDecimal DEFAULT_PRECIO = BigDecimal.valueOf (7.9);
    private static final int DESCUENTO_ESPECTADOR = 30;

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
        this.butaca = this.sala == null || butaca == null || !this.sala.getButacas().contains (butaca) ? this.butaca : butaca;
    }

    public Map <Complemento, Integer> getComplementos () {
        return this.complementos;
    }

    public void setComplementos (Map <Complemento, Integer> complementos) {
        if (complementos == null || complementos.equals (this.complementos))
            return;

        this.complementos = complementos;
        this.calcularPrecio ();
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
        return new BigDecimal (Entrada.DEFAULT_PRECIO.toString ());
    }

    public static int getDescuentoEspectador () {
        return Entrada.DESCUENTO_ESPECTADOR;
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
        return "Entrada [id=" + id + ", espectador=" + espectador + ", pelicula=" + pelicula + ", fecha=" + fecha
                + ", sala=" + sala + ", butaca=" + butaca + ", complementos=" + complementos + ", precio=" + precio
                + ", valoracion=" + valoracion + "]";
    }

    private void calcularPrecio () {
        this.precio = Entrada.DEFAULT_PRECIO
                .subtract (new BigDecimal (Entrada.DESCUENTO_ESPECTADOR).scaleByPowerOfTen (-2)
                        .multiply (false ? BigDecimal.ONE : BigDecimal.ZERO));

        ArrayList <Map.Entry <Complemento, Integer>> keyValueArray = new ArrayList <Map.Entry <Complemento, Integer>> (
                complementos.entrySet ());
        for (int i = 0; i < complementos.size (); i++)
            this.precio = this.precio.add (keyValueArray.get (i).getKey ().getPrecio ()
                    .multiply (new BigDecimal (keyValueArray.get (i).getValue ())));
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
