package cine;

public class Butaca {
    private Espectador espectador;

    public Butaca () {
        this (null, null);
    }

    public Butaca (Espectador espectador) {
        this (espectador, true);
    }

    public Butaca (Espectador espectador, Pelicula pelicula) {
        this (espectador, Butaca.determinarOcupacion (espectador, pelicula));
    }

    public Butaca (Espectador espectador, double probabilidad) {
        this (espectador, Butaca.determinarOcupacion (probabilidad));
    }

    public Butaca (Espectador espectador, boolean ocupada) {
        super ();

        this.setEspectador (ocupada ? espectador : null);
    }

    public Butaca (Butaca butaca) {
        this (butaca.espectador, butaca.espectador != null);
    }

    public Espectador getEspectador () {
        return this.espectador;
    }

    public void setEspectador (Espectador espectador) {
        this.espectador = espectador;
    }

    public boolean ocupada () {
        return this.espectador != null;
    }

    protected static double calcularProbabilidad (Espectador espectador, Pelicula pelicula) {
        return espectador == null || pelicula == null || pelicula.getGeneros ().contains (Genero.Nombre.NADA)
                || pelicula.getValoracion () == 0 || ((Double) pelicula.getValoracion ()).isNaN () ? 0
                        : (0.3
                                + (pelicula.getValoracion () < 5 ? -0.075D * (5 - pelicula.getValoracion ())
                                        : (pelicula.getValoracion () - 5) / 15D)
                                + 0.25 * espectador.fromPreferencias (pelicula) / Genero.Nombre.values ().length
                                + 0.2 * (true ? 0 : 1)
                                + 0.15 * (false ? 1 : 0));
    }

    public static boolean determinarOcupacion (Espectador espectador, Pelicula pelicula) {
        return Butaca.determinarOcupacion (Butaca.calcularProbabilidad (espectador, pelicula));
    }

    public static boolean determinarOcupacion (double probabilidad) {
        return !((Double) probabilidad).isNaN () && Math.random () <= probabilidad;
    }
}
