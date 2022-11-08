package cine;

public class Butaca {
    protected Espectador espectador;

    public Butaca () {
        this (null, null);
    }

    public Butaca (Espectador espectador, Pelicula pelicula) {
        this (espectador, Butaca.calcularProbabilidad (espectador, pelicula));
    }

    public Butaca (Espectador espectador, double probabilidad) {
        this (espectador, !((Double) probabilidad).isNaN () && Math.random () <= probabilidad);
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

    public static double calcularProbabilidad (Espectador espectador, Pelicula pelicula) {
        return espectador == null || pelicula == null ? 0 : (
            0.5
            + 0.25 * espectador.fromPreferencias (pelicula) / Genero.Nombre.values ().length
            + 0.2 * (true ? 0 : 1)
            + 0.15 * (false ? 1 : 0)
        );
    }
}
