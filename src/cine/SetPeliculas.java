package cine;

import java.util.ArrayList;
import java.util.UUID;

public class SetPeliculas {
	
	protected UUID id;
	protected ArrayList<Pelicula> peliculas;
	
	public SetPeliculas() {
		this(new ArrayList<Pelicula>());
	}

	public SetPeliculas(ArrayList<Pelicula> peliculas) {
		super();
		
		this.id = UUID.randomUUID();
		this.peliculas = peliculas;
	}

	public UUID getId() {
		return id;
	}

	public ArrayList<Pelicula> getPeliculas() {
		return peliculas;
	}

	public void setPeliculas(ArrayList<Pelicula> peliculas) {
		this.peliculas = peliculas;
	}

	@Override
	public String toString() {
		return "SetPeliculas [id=" + id + ", peliculas=" + peliculas + "]";
	}
	
	public ArrayList<String> getNombresPeliculas(ArrayList<Pelicula> Peliculas) {
		ArrayList<String> NombrePeliculas = new ArrayList<String>();
		
		for (int i = 0; i < Peliculas.size(); i++) {
			NombrePeliculas.add(Peliculas.get(i).getNombre());
		}
		return NombrePeliculas;
		
	}
	
}
