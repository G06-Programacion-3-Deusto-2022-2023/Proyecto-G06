package cine;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Sala {
    private static final int FILAS = 15;
    private static final int COLUMNAS = 20;
    private static final int NBUTACAS = Sala.FILAS * Sala.COLUMNAS;

    private static final int NRANDOM_ESPECTADORES = Sala.NBUTACAS;
    private static final int DEFAULT_FILL_THRESHOLD = 95 * Sala.NBUTACAS / 100;

    private static final double ADJACENCY_MOD = 1D / 3;

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

    public int llenas () {
        int s = 0;
        for (int i = 0; i < this.butacas.size (); s += this.butacas.get (i++).getEspectador () == null ? 0 : 1)
            ;

        return s;
    }

    public static int size () {
        return Sala.NBUTACAS;
    }

    public void llenarSala () {
        this.llenarSala (this.pelicula);
    }

    public void llenarSala (Pelicula pelicula) {
        this.llenarSala (pelicula, Sala.DEFAULT_FILL_THRESHOLD);
    }

    public void llenarSala (int lim) {
        this.llenarSala (this.pelicula, lim);
    }

    public void llenarSala (Pelicula pelicula, int lim) {
        this.llenarSala (Espectador.randoms (Sala.NRANDOM_ESPECTADORES), pelicula, lim);
    }

    public void llenarSala (Collection <Espectador> espectadores, Pelicula pelicula, int lim)
            throws UnsupportedOperationException, NullPointerException {
        if (espectadores == null)
            throw new NullPointerException ("No se puede llenar una sala usando una colección de espectadores nula.");

        if (lim < 0 || lim >= Sala.NBUTACAS)
            throw new UnsupportedOperationException (String
                    .format ("El límite de llenado de la sala debe estar en el intervalo [0, %d).", Sala.NBUTACAS));

        if (pelicula == null || pelicula.getGeneros ().contains (Genero.Nombre.NADA)) {
            this.clearSala ();

            return;
        }

        List <Espectador> list = espectadores.stream ().collect (Collectors.toList ());
        Collections.shuffle (list);
        list = list.subList (0, Math.min (Sala.NBUTACAS, list.size ()));

        for (int i = 0, j = 0; this.llenas () < lim && i < list.size (); i++) {
            if (this.butacas.get (i).ocupada ()) {
                j = i;

                continue;
            }

            this.butacas.get (i)
                    .setEspectador (Butaca.determinarOcupacion (Butaca.calcularProbabilidad (list.get (j), pelicula)
                            + (((int) (i / Sala.COLUMNAS - 1) * Sala.COLUMNAS >= 0 && this.butacas.get (
                                    (int) (i / Sala.COLUMNAS - 1) * Sala.COLUMNAS + i % Sala.COLUMNAS)
                                    .ocupada ()
                                    && this.butacas
                                            .get ((int) (i / Sala.COLUMNAS - 1) * Sala.COLUMNAS + i % Sala.COLUMNAS)
                                            .getEspectador ()
                                            .getGrupo () == list.get (j).getGrupo ())
                                    || ((int) (i / Sala.COLUMNAS + 1) * Sala.COLUMNAS < Sala.FILAS
                                            && this.butacas.get (
                                                    (int) (i / Sala.COLUMNAS + 1) * Sala.COLUMNAS + i % Sala.COLUMNAS)
                                                    .ocupada ()
                                            && this.butacas
                                                    .get ((int) (i / Sala.COLUMNAS + 1) * Sala.COLUMNAS
                                                            + i % Sala.COLUMNAS)
                                                    .getEspectador ()
                                                    .getGrupo () == list.get (j).getGrupo ())
                                    || (i % Sala.COLUMNAS - 1 >= 0 && this.butacas.get (i - 1).ocupada ()
                                            && this.butacas.get (i - 1).getEspectador ()
                                                    .getGrupo () == list.get (j).getGrupo ())
                                    || (i % Sala.COLUMNAS + 1 < Sala.COLUMNAS && this.butacas.get (i + 1).ocupada ()
                                            && this.butacas.get (i + 1).getEspectador ()
                                                    .getGrupo () == list.get (j).getGrupo ())
                                                            ? Sala.ADJACENCY_MOD
                                                            : 0)) ? list.get (j) : null);

            j++;
        }
    }

    public void clearSala () {
        this.pelicula = null;

        for (int i = 0; i < butacas.size (); butacas.get (i++).setEspectador (null))
            ;
    }

    public int [] getSeatIndex (Butaca butaca) {
        int index = this.butacas.indexOf (butaca);
        return index == -1 ? new int [] { -1, -1 } : new int [] { index / Sala.COLUMNAS, index % Sala.COLUMNAS };
    }
    public static int getColumnas() {
		return Sala.COLUMNAS;
    	
    }
    public static int getFilas() {
		return Sala.FILAS;
    	
    }
}
