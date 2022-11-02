package cine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorBD {
    protected static final String DRIVER_NAME = "org.sqlite.JDBC";
    protected static final String DATABASE_FILE = "db/database.db";
    protected static final String CONNECTION_STRING = "jdbc:sqlite:" + DATABASE_FILE;
	
	public GestorBD() {		
		try {
			//Cargar el diver SQLite
			Class.forName(DRIVER_NAME);
		} catch (ClassNotFoundException ex) {
			System.err.println(String.format("* Error al cargar el driver de BBDD: %s", ex.getMessage()));
			ex.printStackTrace();
		}
	}
		
	public void crearBBDD() {
		//Se abre la conexión y se obtiene el Statement
		//Al abrir la conexión, si no existía el fichero, se crea la base de datos
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			
	        String sql = "CREATE TABLE IF NOT EXISTS PELICULA (\n"
	                   + " ID_PELICULA STRING PRIMARY KEY,\n"
	                   + " NOMBRE TEXT NOT NULL,\n"
	                   + " VALORACION DOUBLE NOT NULL,\n"
	                   + " DIRECTOR TEXT NOT NULL,\n"
	                   + " DURACION INTEGER NOT NULL,\n"
	                   + " EDAD_RECOMENDADA TEXT NOT NULL,\n"
	                   + " GENEROS ARRAY NOT NULL\n"
	                   + ");";
	        	        
	        if (!stmt.execute(sql)) {
	        	System.out.println("- Se ha creado la tabla Pelicula");
	        }
		} catch (Exception ex) {
			System.err.println(String.format("* Error al crear la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();			
		}
	}
	
	public void borrarBBDD() {
		//Se abre la conexion y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			
	        String sql = "DROP TABLE IF EXISTS PELICULA";
			
	        //Se ejecuta la sentencia de creacion de la tabla Estudiantes
	        if (!stmt.execute(sql)) {
	        	System.out.println("- Se ha borrado la tabla Pelicula");
	        }
		} catch (Exception ex) {
			System.err.println(String.format("* Error al borrar la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();			
		}
		
		try {
			//Se borra el fichero de la BBDD
			Files.delete(Paths.get(DATABASE_FILE));
			System.out.println("- Se ha borrado el fichero de la BBDD");
		} catch (Exception ex) {
			System.err.println(String.format("* Error al borrar el archivo de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}
	}
	
	public void insertarDatos(Pelicula... peliculas ) {
		//Se abre la conexión y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se define la plantilla de la sentencia SQL
			String sql = "INSERT INTO PELICULA (ID_PELICULA ,NOMBRE, VALORACION, DIRECTOR, DURACION, EDAD_RECOMENDADA, GENEROS) VALUES ('%s','%s', '%f', '%s', '%s', '%s', '%s');";
			
			System.out.println("- Insertando peliculas...");
			
			//Se recorren los clientes y se insertan uno a uno
			for (Pelicula p : peliculas) {
				if (1 == stmt.executeUpdate(String.format(sql, p.getId().toString(), p.getNombre(), p.getValoracion(), p.getDirector(), p.getDuracion(), p.getEdad(),p.getGeneros()))) {					
					System.out.println(String.format(" - Pelicula insertada: %s", p.toString()));
				} else {
					System.out.println(String.format(" - No se ha insertado la pelicula: %s", p.toString()));
				}
			}			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al insertar datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}				
	}
	
	public List<Pelicula> obtenerDatos() {
		List<Pelicula> peliculas = new ArrayList<>();
		
		//Se abre la conexión y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			String sql = "SELECT * FROM PELICULA";
			
			//Se ejecuta la sentencia y se obtiene el ResultSet con los resutlados
			ResultSet rs = stmt.executeQuery(sql);			
			Pelicula pelicula;
			
			//Se recorre el ResultSet y se crean objetos Cliente
			while (rs.next()) {
				pelicula = new Pelicula();
				
				pelicula.setNombre(rs.getString("NOMBRE"));
				pelicula.setValoracion(rs.getDouble("VALORACION"));
				pelicula.setDirector(rs.getString("DIRECTOR"));
				pelicula.setDuracion(Duration.ofMinutes(rs.getInt("DURACION")));
				pelicula.setEdad(EdadRecomendada.valueOf(rs.getString("EDAD_RECOMENDADA")));
				pelicula.setGeneros(new ArrayList<>());
				
				//Se inserta cada nueva pelicula en la lista de peliculas
				peliculas.add(pelicula);
			}
			
			//Se cierra el ResultSet
			rs.close();
			
			System.out.println(String.format("- Se han recuperado %d pelicula...", peliculas.size()));			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al obtener datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
		
		return peliculas;
	}

	public void actualizarValoracion(Pelicula pelicula, double newValoracion) {
		//Se abre la conexión y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se ejecuta la sentencia de borrado de datos
			String sql = "UPDATE PELICULA SET VALORACION = '%s' WHERE ID_PELICULA = '%s';";
			
			int result = stmt.executeUpdate(String.format(sql, newValoracion, pelicula.getId()));
			
			System.out.println(String.format("- Se ha actulizado %d peliculas", result));
		} catch (Exception ex) {
			System.err.println(String.format("* Error actualizando datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
	}
	
	public void borrarDatos() {
		//Se abre la conexión y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se ejecuta la sentencia de borrado de datos
			String sql = "DELETE FROM PELICULA;";			
			int result = stmt.executeUpdate(sql);
			
			System.out.println(String.format("- Se han borrado %d peliculas", result));
		} catch (Exception ex) {
			System.err.println(String.format("* Error al borrar datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
	}
}