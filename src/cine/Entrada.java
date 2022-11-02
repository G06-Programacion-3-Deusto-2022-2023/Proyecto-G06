package cine;

import java.sql.Date;
import java.util.Objects;
import java.util.UUID;

public class Entrada {
    protected UUID id;
    protected Date fecha;
    protected int butaca;
    protected int precio;
    protected Pelicula pelicula;
    protected Complementos complemento;
    protected Espectador espectador;
    protected Sala sala;

    public Entrada (Date fecha, int butaca, int precio, Pelicula pelicula, Complementos complemento,
            Espectador espectador, Sala sala) {
        super ();

        this.id = UUID.randomUUID ();
        this.fecha = fecha;
        this.butaca = butaca;
        this.precio = precio;
        this.setPelicula (pelicula);
        this.setComplemento (complemento);
        this.setEspectador (espectador);
        this.setSala (sala);
    }

    public Entrada () {
        super ();

        this.id = UUID.randomUUID ();
        this.fecha = fecha;
        this.butaca = 0;
        this.precio = 0;
    }

    public UUID getId () {
        return id;
    }

    public void setId (UUID id) {
        this.id = id;
    }

    public Date getFecha () {
        return fecha;
    }

    public void setFecha (Date fecha) {
        this.fecha = fecha;
    }

    public int getButaca () {
        return butaca;
    }

    public void setButaca (int butaca) {
        this.butaca = butaca;
    }

    public int getPrecio () {
        return precio;
    }

    public void setPrecio (int precio) {
        this.precio = precio;
    }

    public Pelicula getPelicula () {
        return pelicula;
    }

    public void setPelicula (Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Complementos getComplemento () {
        return complemento;
    }

    public void setComplemento (Complementos complemento) {
        this.complemento = complemento;
    }

    public Espectador getEspectador () {
        return espectador;
    }

    public void setEspectador (Espectador espectador) {
        this.espectador = espectador;
    }

    public Sala getSala () {
        return sala;
    }

    public void setSala (Sala sala) {
        this.sala = sala;
    }

    @Override
    public String toString () {
        return "Entrada [id=" + id + ", fecha=" + fecha + ", butaca=" + butaca + ", precio=" + precio
                + ", pelicula=" + pelicula + ", complemento=" + complemento + ", espectador=" + espectador
                + ", sala=" + sala + "]";
    }
}
