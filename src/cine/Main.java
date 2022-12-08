package cine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class Main {

	public static void main (String [] args) {
		GestorBD gestorBD = new GestorBD ();

		// CREATE DATABASE: Se crea la BBDD
		gestorBD.crearBBDD ();

		// INSERT: Insertar datos en la BBDD
		List <Pelicula> peliculas = initPeliculas ();
		List <SetPeliculas> setsPeliculas = initSetPeliculas(peliculas);
		//List <Administrador> administradores = initAdministrador(setsPeliculas);
		//printAdministradores(administradores);
		printSetsPeliculas (setsPeliculas);
		
		gestorBD.insertarDatosPelicula (peliculas.toArray (new Pelicula [peliculas.size ()]));
		gestorBD.insertarDatosSetPelicula(setsPeliculas.toArray(new SetPeliculas [setsPeliculas.size()]));

		// SELECT: Se obtienen datos de la BBDD
		setsPeliculas = gestorBD.obtenerDatosSetPeliculas();
		printSetsPeliculas (setsPeliculas);
		
		// DELETE: Se borran datos de la BBDD
		gestorBD.borrarDatos ();

		// DROP DATABASE: Se borra la BBDD
		gestorBD.borrarBBDD ();
	}

	private static void printPeliculas (List <Pelicula> peliculas) {
		if (!peliculas.isEmpty ()) {
			for (Pelicula pelicula : peliculas) {
				System.out.println (String.format (" - %s", pelicula.toString ()));
			}
		}
	}
	private static void printSetsPeliculas (List <SetPeliculas> Setspeliculas) {
		if (!Setspeliculas.isEmpty ()) {
			for (SetPeliculas Setpeliculas : Setspeliculas) {
				System.out.println (String.format (" - %s", Setpeliculas.toString ()));
			}
		}
	}
	private static void printAdministradores (List <Administrador> administradores) {
		if (!administradores.isEmpty ()) {
			for (Administrador Administrador : administradores) {
				System.out.println (String.format (" - %s", Administrador.toString ()));
			}
		}
	}

	public static List <Pelicula> initPeliculas () {
		List <Pelicula> peliculas = new ArrayList <> ();

		Pelicula pelicula1 = new Pelicula ();
		pelicula1.setValoracion (4);
		pelicula1.setNombre ("Piratas del caribe");
		pelicula1.setDirector ("Rob Marshall");
		pelicula1.setDuracion (Duration.ofMinutes (143));
		pelicula1.setEdad (EdadRecomendada.DIECISEIS);
		pelicula1.setGeneros (Genero.Nombre.toGeneros ((short) 0b11));
		pelicula1.setSets(new TreeSet<SetPeliculas>());
		peliculas.add (pelicula1);

		Pelicula pelicula2 = new Pelicula ();
		pelicula2.setValoracion (5);
		pelicula2.setNombre ("Origen");
		pelicula2.setDirector ("Crishtopher nolan");
		pelicula2.setDuracion (Duration.ofMinutes (148));
		pelicula2.setEdad (EdadRecomendada.DIECISEIS);
		pelicula2.setGeneros (Genero.Nombre.toGeneros ((short) 0b101));
		peliculas.add (pelicula2);
		
		return peliculas;
	}
	public static List <SetPeliculas> initSetPeliculas (List <Pelicula> peliculas) {
		List<SetPeliculas> setsPeliculas = new ArrayList<SetPeliculas>();
		
		Administrador administrador = new Administrador("Mikel","1234");
		UUID id = UUID.fromString("c3c755d9-0213-45a5-b338-fe5388ee5c0a");
		
		SetPeliculas setPeliculas = new SetPeliculas(id,administrador, "Set", peliculas);
		setsPeliculas.add(setPeliculas);
		
		return  setsPeliculas;
		
	}
	public static List <Administrador> initAdministrador(List<SetPeliculas> SetsPeliculas) {
		List<Administrador> administradores = new ArrayList<Administrador>();
		Administrador administrador = new Administrador("iker", "1234", SetsPeliculas);
		administradores.add(administrador);
		
		return administradores;
		
	}
	
}