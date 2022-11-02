package cine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		GestorBD gestorBD = new GestorBD();		
		
		//CREATE DATABASE: Se crea la BBDD
		gestorBD.crearBBDD();
		
		//INSERT: Insertar datos en la BBDD		
		List<Pelicula> peliculas = initPeliculas();
		gestorBD.insertarDatos(peliculas.toArray(new Pelicula[peliculas.size()]));
		
		//SELECT: Se obtienen datos de la BBDD
		peliculas = gestorBD.obtenerDatos();
		printClientes(peliculas);
		
		//UPDATE: Se actualiza la password de un cliente
		double newValoracion = 4.5;
		gestorBD.actualizarValoracion(peliculas.get(0), newValoracion);

		//SELECT: Se obtienen datos de la BBDD
		peliculas = gestorBD.obtenerDatos();
		printClientes(peliculas);

		//DELETE: Se borran datos de la BBDD
		gestorBD.borrarDatos();
		
		//DROP DATABASE: Se borra la BBDD
		gestorBD.borrarBBDD();
	}
	
	private static void printClientes(List<Pelicula> peliculas) {
		if (!peliculas.isEmpty()) {		
			for(Pelicula pelicula : peliculas) {
				System.out.println(String.format(" - %s", pelicula.toString()));
			}
		}		
	}
	
	public static List<Pelicula> initPeliculas() {
		List<Pelicula> peliculas = new ArrayList<>();
		
		Pelicula pelicula1 = new Pelicula();
		Pelicula pelicula1 = new Pelicula()
		ArrayList Generos1 = new ArrayList<>();
		Generos1.add(Genero.ACCION);
		Generos1.add(Genero.COMEDIA);
		pelicula1.setValoracion(4.0);
		pelicula1.setNombre("Piratas del caribe");
		pelicula1.setDirector("Rob Marshall");
		pelicula1.setDuracion(Duration.ofMinutes(143));
		pelicula1.setEdad(EdadRecomendada.DIECISEIS);
		pelicula1.setGeneros(Generos1);
		peliculas.add(pelicula1);
		
		Pelicula pelicula2 = new Pelicula();
		ArrayList Generos2 = new ArrayList<>();
		Generos2.add(Genero.CIENCIA_FICCION);
		pelicula2.setValoracion(5);
		pelicula2.setNombre("Origen");
		pelicula2.setDirector("Crishtopher nolan");
		pelicula2.setDuracion(Duration.ofMinutes(148));
		pelicula2.setEdad(EdadRecomendada.DIECISEIS);
		pelicula2.setGeneros(Generos2);
		peliculas.add(pelicula2);
		
		return peliculas;
	}

}
