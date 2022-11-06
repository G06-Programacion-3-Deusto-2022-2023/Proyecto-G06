package cine;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Sala {
    private interface EmptyArrayCreator {
        Butaca [] butacasVacias ();
    }

    public static final int NBUTACAS = 40;

    protected Pelicula pelicula;
    protected Vector <Butaca> butacas;
    private EmptyArrayCreator emptyArrayCreator;

    public Sala () {
        super ();

        this.setPelicula (null);
        this.butacas = new Vector <Butaca> (Arrays.asList ((emptyArrayCreator = () -> {
            Butaca array[] = new Butaca [Sala.NBUTACAS];
            for (int i = 0; i < Sala.NBUTACAS; array [i++] = new Butaca ())
                ;
            return array;
        }).butacasVacias ()));
    }

    public Pelicula getPelicula () {
        return this.pelicula;
    }

    public void setPelicula (Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public List <Butaca> getButacas () {
        return this.butacas;
    }

    public void clearSala () {
        this.pelicula = null;

        for (int i = 0; i < butacas.size (); butacas.get (i++).setEspectador (null));
    }
}
