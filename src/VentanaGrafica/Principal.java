package VentanaGrafica;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import cine.GestorBD;
import cine.Pelicula;
import cine.SetPeliculas;

public class Principal {

	public static void main (String [] args) {
		GestorBD db = new GestorBD ();
		db.borrarBBDD ();
		db.crearBBDD ();
		db.insertarDatosPelicula (Pelicula.getDefault ().toArray (new Pelicula [0]));
		db.insertarDatosSetPelicula (SetPeliculas.getDefault ());
		db.createAdminKeys ();
			SwingUtilities.invokeLater ( () -> new VentanaInicio (db));
	}

}
