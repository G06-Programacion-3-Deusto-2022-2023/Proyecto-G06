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
		gestorBD.insertarDatosPelicula(peliculas.toArray(new Pelicula[peliculas.size()]));
		
		//SELECT: Se obtienen datos de la BBDD
		peliculas = gestorBD.obtenerDatos();
		printClientes(peliculas);
		
		//UPDATE: Se actualiza la password de un cliente
		int newValoracion = 5;
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
		pelicula1.setValoracion(4);
		pelicula1.setNombre("Piratas del caribe");
		pelicula1.setDirector("Rob Marshall");
		pelicula1.setDuracion(Duration.ofMinutes(143));
		pelicula1.setEdad(EdadRecomendada.DIECISEIS);
		pelicula1.setGeneros(Genero.toGeneros((short)0b11));
		peliculas.add(pelicula1);
		
		Pelicula pelicula2 = new Pelicula();
		pelicula2.setValoracion(5);
		pelicula2.setNombre("Origen");
		pelicula2.setDirector("Crishtopher nolan");
		pelicula2.setDuracion(Duration.ofMinutes(148));
		pelicula2.setEdad(EdadRecomendada.DIECISEIS);
		pelicula2.setGeneros(Genero.toGeneros((short)0b101));
		peliculas.add(pelicula2);
		
		return peliculas;
	}

}
