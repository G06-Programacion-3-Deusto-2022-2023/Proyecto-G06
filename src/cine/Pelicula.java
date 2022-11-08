package cine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.time.Duration;

public class Pelicula {
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
        this (nombre, "", Double.NaN, "", Duration.ZERO, EdadRecomendada.TODOS, generos);
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
                || valoracion > 10 ? Double.NaN : valoracion;
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
        return new Pelicula ("", Genero.randomGeneros ());
    }
}
