package cine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Pelicula {
    UUID id;
    String nombre;
    String rutaImagen;
    double valoracion;
    String director;
    Duration duracion;
    EdadRecomendada edad;
    ArrayList <Genero> generos;

    Pelicula () {
        this ("");
    }

    Pelicula (String nombre) {
        this (nombre, Genero.NADA);
    }

    Pelicula (String nombre, Genero... generos) {
        this (nombre, "", Double.NaN, "", Duration.ZERO, EdadRecomendada.TODOS, generos);
    }

    Pelicula (String nombre, String rutaImagen, double valoracion, String director, Duration duracion, EdadRecomendada edad, Genero... generos) {
        super ();

        this.id = UUID.randomUUID ();
        this.setNombre (nombre);
        this.setRutaImagen (rutaImagen);
        this.setValoracion (valoracion);
        this.setDirector (director);
        this.setDuration (duracion);
        this.setEdad (edad);
        this.setGeneros (new ArrayList <Genero> (Arrays.asList (generos)));
    }

    Pelicula (Pelicula pelicula) {
        this (pelicula.nombre, pelicula.rutaImagen, pelicula.valoracion, pelicula.director, pelicula.duracion, pelicula.edad, pelicula.generos.toArray (new Genero [pelicula.generos.size ()]));
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
        this.director = director == null || director.equals ("") ? ""
                : director;
    }

    public Duration getDuracion () {
        return this.duracion;
    }

    public void setDuracion (Duration duracion) {
        this.duracion = Duration.ofMinutes (duracion.toMinutes ());
    }

    public EdadRecomendada getEdad () {
        return this.edad;
    }

    public void setEdad (EdadRecomendada edad) {
        this.edad = edad;
    }

    public ArrayList <Genero> getGenero () {
        return this.generos;
    }

    public void setGenero (ArrayList <Genero> generos) {
        this.generos = (ArrayList <Genero>) generos.clone ();
    }

    @Override
    public String toString () {
        return super.toString ();
    }
}
