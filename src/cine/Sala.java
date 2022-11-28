package cine;

import java.util.function.Supplier;
import java.util.List;
import java.util.Vector;

public class Sala {
    private static final int NBUTACAS = 40;

    private Pelicula pelicula;
    private Vector <Butaca> butacas;

    public Sala () {
        super ();

        this.setPelicula (null);
        this.butacas = ((Supplier <Vector <Butaca>>) ( () -> {
            Vector <Butaca> v = new Vector <Butaca> ();
            for (int i = 0; i < Sala.NBUTACAS; i++)
                v.add (new Butaca ());

            return v;
        })).get ();
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

    public static int size () {
        return Sala.NBUTACAS;
    }

    public void clearSala () {
        this.pelicula = null;

        for (int i = 0; i < butacas.size (); butacas.get (i++).setEspectador (null))
            ;
    }
}
