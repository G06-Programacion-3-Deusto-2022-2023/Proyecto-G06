package cine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entrada {
    public static final BigDecimal PRECIOESTANDAR = new BigDecimal (7.9);
    public static final int DESCUENTOESPECTADOR = 30;

    protected UUID id;
    protected Espectador espectador;
    protected Pelicula pelicula;
    protected Calendar.Builder fecha;
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
        this (espectador, pelicula, new Calendar.Builder ().setDate (2003, 21, 02));
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar.Builder fecha) {
        this (espectador, pelicula, fecha, null, 0);
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar.Builder fecha, Sala sala, int butaca) {
        this (espectador, pelicula, fecha, sala, butaca, new HashMap <Complemento, Integer> ());
    }

    public Entrada (Espectador espectador, Pelicula pelicula, Calendar.Builder fecha, Sala sala, int butaca,
            HashMap <Complemento, Integer> complementos) {
        this (UUID.randomUUID (), espectador, pelicula, fecha, sala, butaca, complementos);
    }

    public Entrada (UUID id, Espectador espectador, Pelicula pelicula, Calendar.Builder fecha, Sala sala, int butaca,
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

    @Override
    public String toString () {
        return "Entrada [id=" + id + ", fecha=" + fecha + ", butaca=" + butaca + ", precio=" + precio
                + ", pelicula=" + pelicula + ", complemento=" + complementos + ", espectador=" + espectador
                + ", sala=" + sala + "]";
    }

    protected void calcularPrecio () {
        this.precio = Entrada.PRECIOESTANDAR
                .subtract (new BigDecimal (Entrada.DESCUENTOESPECTADOR).scaleByPowerOfTen (-2));

        ArrayList <Map.Entry <Complemento, Integer>> keyValueArray = new ArrayList <Map.Entry <Complemento, Integer>> (
                complementos.entrySet ());
        for (int i = 0; i < complementos.size (); i++)
            this.precio = this.precio.add (keyValueArray.get (i).getKey ().getPrecio ()
                    .multiply (new BigDecimal (keyValueArray.get (i).getValue ())));
    }
}
