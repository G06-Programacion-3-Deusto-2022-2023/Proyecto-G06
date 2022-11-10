package cine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.UUID;
import java.time.Duration;

public class Pelicula {
    // Media y desviación estándar de la distribución de las valoraciones de las películas sacadas de los primeros 1048576 elementos
    // de la columna averageRating del archivo title.ratings.tsv/data.tsv del dataset de Kaggle https://www.kaggle.com/datasets/ashirwadsangwan/imdb-dataset.
    private static final Random random = new Random ();
    private static final double RANDOMAVG = 6.91973762512207;
    private static final double RANDOMSTDEV = 1.38597026838328;

    public static final Duration DEFAULT_DURACION = Duration.ofMinutes (90);

    protected UUID id;
    protected String nombre;
    protected String rutaImagen;
    protected double valoracion;
    protected String director;
    protected Duration duracion;
    protected EdadRecomendada edad;
    protected ArrayList <Genero.Nombre> generos;

    public Pelicula () {
        this ("");
    }

    public Pelicula (String nombre) {
        this (nombre, Genero.Nombre.NADA);
    }

    public Pelicula (String nombre, Genero.Nombre... generos) {
        this (nombre, new ArrayList <Genero.Nombre> (Arrays.asList (generos)));
    }

    public Pelicula (String nombre, List <Genero.Nombre> generos) {
        this (nombre, Double.NaN, EdadRecomendada.TODOS, generos);
    }

    public Pelicula (String nombre, double valoracion, EdadRecomendada edad, Genero.Nombre [] generos) {
        this (nombre, valoracion, edad, new ArrayList <Genero.Nombre> (generos == null ? Collections.emptyList () : Arrays.asList (generos)));
    }

    public Pelicula (String nombre, double valoracion, EdadRecomendada edad, List <Genero.Nombre> generos) {
        this (nombre, "", valoracion, "", DEFAULT_DURACION, edad, generos);
    }

    public Pelicula (String nombre, String rutaImagen, double valoracion, String director, Duration duracion,
            EdadRecomendada edad, Genero.Nombre... generos) {
        this (UUID.randomUUID (), nombre, rutaImagen, valoracion, director, duracion, edad,
                new ArrayList <Genero.Nombre> (Arrays.asList (generos)));
    }

    public Pelicula (String nombre, String rutaImagen, double valoracion, String director, Duration duracion,
            EdadRecomendada edad, List <Genero.Nombre> generos) {
        this (UUID.randomUUID (), nombre, rutaImagen, valoracion, director, duracion, edad, generos);
    }

    public Pelicula (UUID id, String nombre, String rutaImagen, double valoracion, String director, Duration duracion,
            EdadRecomendada edad, Genero.Nombre... generos) {
        this (id, nombre, rutaImagen, valoracion, director, duracion, edad,
                new ArrayList <Genero.Nombre> (Arrays.asList (generos)));
    }

    public Pelicula (UUID id, String nombre, String rutaImagen, double valoracion, String director, Duration duracion,
            EdadRecomendada edad, List <Genero.Nombre> generos) {
        super ();

        this.id = id;
        this.setNombre (nombre);
        this.setRutaImagen (rutaImagen);
        this.setValoracion (valoracion);
        this.setDirector (director);
        this.setDuracion (duracion);
        this.setEdad (edad);
        this.setGeneros (generos);
    }

    public Pelicula (Pelicula pelicula) {
        this (pelicula.id, pelicula.nombre, pelicula.rutaImagen, pelicula.valoracion, pelicula.director,
                pelicula.duracion,
                pelicula.edad, pelicula.generos);
    }

    public UUID getId () {
        return this.id;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        this.nombre = nombre == null || nombre.equals ("") ? this.id.toString ()
                : nombre;
    }

    public String getRutaImagen () {
        return this.rutaImagen;
    }

    public void setRutaImagen (String rutaImagen) {
        this.rutaImagen = rutaImagen == null ? "" : rutaImagen;
    }

    public double getValoracion () {
        return this.valoracion;
    }

    public void setValoracion (double valoracion) {
        this.valoracion = ((Double) valoracion).isNaN () || valoracion < 1
                || valoracion > 10 ? Double.NaN : Math.floor (valoracion * 10) / 10;
    }

    public String getDirector () {
        return this.director;
    }

    public void setDirector (String director) {
        this.director = director == null ? "" : director;
    }

    public Duration getDuracion () {
        return this.duracion;
    }

    public void setDuracion (Duration duracion) {
        this.duracion = duracion == null ? this.duracion : Duration.ofMinutes (duracion.toMinutes ());
    }

    public EdadRecomendada getEdad () {
        return this.edad;
    }

    public void setEdad (EdadRecomendada edad) {
        this.edad = edad == null ? EdadRecomendada.TODOS : edad;
    }

    public List <Genero.Nombre> getGeneros () {
        return this.generos;
    }

    public void setGeneros (List <Genero.Nombre> generos) {
        this.generos = new ArrayList <Genero.Nombre> (
                generos == null ? Collections.singletonList (Genero.Nombre.NADA) : generos);
    }

    @Override
    public String toString () {
        return "Pelicula [id=" + id + ", nombre=" + nombre + ", rutaImagen=" + rutaImagen + ", valoracion=" + valoracion
                + ", director=" + director + ", duracion=" + duracion + ", edad=" + edad + ", generos=" + generos + "]";
    }

    public static Pelicula random () {
        return Pelicula.random ("");
    }
    
    public static Pelicula random (String nombre) {
        return new Pelicula (nombre, Pelicula.random.nextGaussian (Pelicula.RANDOMAVG, Pelicula.RANDOMSTDEV),
                EdadRecomendada.random (), Genero.randomGeneros ());
    }

    public static List <String> getNombres (List <Pelicula> peliculas) {
        ArrayList <String> nombres = new ArrayList <String> ();

        for (int i = 0; i < peliculas.size (); i++) {
            nombres.add (peliculas.get (i).getNombre ());
        }

        return nombres;
    }
}
