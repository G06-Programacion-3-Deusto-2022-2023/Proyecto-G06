package cine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Cine {
	public static final int NSalas = 5;
	
	protected String nombre;
	protected String imagenCine;;
	protected HashMap<String,HashMap<Integer,Pelicula>> peliculasDia;
	
	public Cine() {
		this("");
	}

	public Cine(String nombre) {
		this(nombre,"");
	}
	
	public Cine(String nombre, String imagenCine) {
		this(nombre,imagenCine,new HashMap<String,HashMap<Integer,Pelicula>>());
	}
	
	public Cine(String nombre, String imagenCine, HashMap<String, HashMap<Integer, Pelicula>> peliculasDia) {
		super();
		
		this.nombre = nombre;
		this.imagenCine = imagenCine;
		this.peliculasDia = peliculasDia;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getImagenCine() {
		return imagenCine;
	}

	public void setImagenCine(String imagenCine) {
		this.imagenCine = imagenCine;
	}

	public HashMap<String, HashMap<Integer, Pelicula>> getDia() {
		return peliculasDia;
	}

	public void setDia(HashMap<String, HashMap<Integer, Pelicula>> peliculasDia) {
		this.peliculasDia = peliculasDia;
	}

	@Override
	public String toString() {
		return "Cine [nombre=" + nombre + ", imagenCine=" + imagenCine + ", peliculasDia=" + peliculasDia + "]";
	}
	
	public ArrayList<Pelicula> getNombresPeliculas(HashMap<String,HashMap<Integer,Pelicula>> peliculasDia) {
		ArrayList<Pelicula> Peliculas = new ArrayList<>();
		
		for (Entry<String, HashMap<Integer, Pelicula>> entry : peliculasDia.entrySet()) {
			String key = entry.getKey();
			for (int i = 0; i < NSalas; i++) {
				Peliculas.add(peliculasDia.get(key).get(i));
			}
		}
		
		return Peliculas;
		
	}
	
	
}
