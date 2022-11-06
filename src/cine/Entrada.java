package cine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entrada {
    public static final BigDecimal PRECIOESTANDAR = BigDecimal.valueOf (7.9);
    public static final int DESCUENTOESPECTADOR = 30;

    protected UUID id;
    protected Espectador espectador;
    protected Pelicula pelicula;
    protected Calendar fecha;
    protected Sala sala;
    protected int butaca;
    protected HashMap <Complemento, Integer> complementos;
    protected BigDecimal precio;

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
        this (espectador, pelicula, fecha, null, 0);
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, int butaca) {
        this (espectador, pelicula, fecha, sala, butaca, new HashMap <Complemento, Integer> ());
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, int butaca,
            HashMap <Complemento, Integer> complementos) {
        this (UUID.randomUUID (), espectador, pelicula, fecha, sala, butaca, complementos);
    }

    public Entrada (UUID id, Espectador espectador, Pelicula pelicula, Calendar fecha, Sala sala, int butaca,
            HashMap <Complemento, Integer> complementos) {
        super ();

        this.id = id;
        this.setEspectador (espectador);
        this.setPelicula (pelicula);
        this.setFecha (fecha);
        this.setSala (sala);
        this.setButaca (butaca);
        this.setComplementos (complementos);
    }

    public Entrada (Entrada entrada) {
        this (entrada.id, entrada.espectador, entrada.pelicula, entrada.fecha, entrada.sala, entrada.butaca,
                entrada.complementos);
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

    public int getButaca () {
        return this.butaca;
    }

    public void setButaca (int butaca) {
        this.butaca = butaca < 0 || butaca >= Sala.NBUTACAS ? this.butaca : butaca;
    }

    public Map <Complemento, Integer> getComplementos () {
        return this.complementos;
    }

    public void setComplementos (Map <Complemento, Integer> complementos) {
        if (complementos == null || complementos.equals (this.complementos))
            return;

        this.complementos = (HashMap <Complemento, Integer>) complementos;
        this.calcularPrecio ();
    }

    public BigDecimal getPrecio () {
        return this.precio;
    }

    @Override
    public String toString () {
        return "Entrada [id=" + id + ", espectador=" + espectador + ", pelicula=" + pelicula + ", fecha=" + fecha
                + ", sala=" + sala + ", butaca=" + butaca + ", complementos=" + complementos + ", precio=" + precio
                + "]";
    }

    private void calcularPrecio () {
        this.precio = Entrada.PRECIOESTANDAR
                .subtract (new BigDecimal (Entrada.DESCUENTOESPECTADOR).scaleByPowerOfTen (-2)
                        .multiply (false ? BigDecimal.ONE : BigDecimal.ZERO));

        ArrayList <Map.Entry <Complemento, Integer>> keyValueArray = new ArrayList <Map.Entry <Complemento, Integer>> (
                complementos.entrySet ());
        for (int i = 0; i < complementos.size (); i++)
            this.precio = this.precio.add (keyValueArray.get (i).getKey ().getPrecio ()
                    .multiply (new BigDecimal (keyValueArray.get (i).getValue ())));
    }
}
