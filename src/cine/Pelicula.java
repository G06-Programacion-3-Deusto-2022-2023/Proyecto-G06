package cine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Pelicula {
    protected UUID id;
    protected String nombre;
    protected String rutaImagen;
    protected double valoracion;
    protected String director;
    protected Duration duracion;
    protected EdadRecomendada edad;
    protected ArrayList <Genero> generos;

    public Pelicula () {
        this ("");
    }

    public Pelicula (String nombre) {
        this (nombre, Genero.NADA);
    }

    public Pelicula (String nombre, Genero... generos) {
        this (nombre, "", Double.NaN, "", Duration.ZERO, EdadRecomendada.TODOS, generos);
    }

    public Pelicula (String nombre, String rutaImagen, double valoracion, String director, Duration duracion,
            EdadRecomendada edad, Genero... generos) {
        super ();

        this.id = UUID.randomUUID ();
        this.setNombre (nombre);
        this.setRutaImagen (rutaImagen);
        this.setValoracion (valoracion);
        this.setDirector (director);
        this.setDuracion (duracion);
        this.setEdad (edad);
        this.setGeneros (new ArrayList <Genero> (Arrays.asList (generos)));
    }

    public Pelicula (Pelicula pelicula) {
        this (pelicula.nombre, pelicula.rutaImagen, pelicula.valoracion, pelicula.director, pelicula.duracion,
                pelicula.edad, pelicula.generos.toArray (new Genero [pelicula.generos.size ()]));
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

    public List <Genero> getGeneros () {
        return this.generos;
    }

    public void setGeneros (ArrayList <Genero> generos) {
        this.generos = (ArrayList <Genero>) generos.clone ();
    }

    public short valorGeneros () {
        short value = Genero.NADA.getValue ();
            for (int i = 0; i < this.getGeneros ().size (); value |= this.getGeneros ().get (i++).getValue ());

        return value;
    }

	@Override
	public String toString() {
		return "Pelicula [id=" + id + ", nombre=" + nombre + ", rutaImagen=" + rutaImagen + ", valoracion=" + valoracion
				+ ", director=" + director + ", duracion=" + duracion + ", edad=" + edad + ", generos=" + generos + "]";
	}

   
}
