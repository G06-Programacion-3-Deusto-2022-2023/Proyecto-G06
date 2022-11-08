package cine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
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
			
	        String sql1 = "CREATE TABLE IF NOT EXISTS PELICULA (\n"
	                   + " ID_PELICULA STRING ,\n"
	                   + " NOMBRE STRING PRIMARY KEY NOT NULL,\n"
	                   + " VALORACION INTEGER NOT NULL,\n"
	                   + " DIRECTOR STRING NOT NULL,\n"
	                   + " DURACION INTEGER NOT NULL,\n"
	                   + " EDAD_RECOMENDADA STRING NOT NULL,\n"
	                   + " GENEROS INTEGER NOT NULL\n"
	                   + ");";
	        String sql2 = "CREATE TABLE IF NOT EXISTS USUARIO (\n"
	                   + " ID_USUARIO STRING ,\n"
	                   + " NOMBRE_USUARIO STRING PRIMARY KEY NOT NULL ,\n"
	                   + " CONTRASENA_USUARIO STRING \n"
	                   + ");";
	        	        
	        if (!stmt.execute(sql1) && !stmt.execute(sql2)) {
	        	System.out.println("- Se ha creado la tabla pelicula" + " y la tabla usuario");
	        }
		} catch (Exception ex) {
			System.err.println(String.format("* Error al crear la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();			
		}
	}
	
	public void borrarBBDD() {
		//Se abre la conexión y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			
	        String sql = "DROP TABLE IF EXISTS PELICULA";
			
	        //Se ejecuta la sentencia de creación de la tabla Estudiantes
	        if (!stmt.execute(sql)) {
	        	System.out.println("- Se ha borrado la tabla pelicula");
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
	
	public void insertarDatosPelicula(Pelicula... peliculas) {
		//Se abre la conexión y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se define la plantilla de la sentencia SQL
			String sql = "INSERT INTO PELICULA (ID_PELICULA, NOMBRE, VALORACION, DIRECTOR, DURACION, EDAD_RECOMENDADA, GENEROS) VALUES ('%s', '%s', %d,'%s', %d, '%s', %d);";
			
			System.out.println("- Insertando peliculas...");
			
			//Se recorren los clientes y se insertan uno a uno
			for (Pelicula p : peliculas) {
				int Valoracion = (int) (p.getValoracion() * 10);
				if (1 == stmt.executeUpdate(String.format(sql, p.getId().toString(), p.getNombre(),(int)Valoracion, p.getDirector(),(int) p.getDuracion().toMinutes(), p.getEdad(), Genero.toValor(p.getGeneros())))) {					
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
	public void insertarDatosUsuario(Usuario... usuarios) {
		//Se abre la conexión y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se define la plantilla de la sentencia SQL
			String sql = "INSERT INTO USUARIO (ID_USUARIOS, NOMBRE_USUARIO, CONTRASENA_USUARIO) VALUES ('%s', '%s', %s');";
			
			System.out.println("- Insertando usuarios...");
			
			//Se recorren los clientes y se insertan uno a uno
			for (Usuario u : usuarios) {
				if (1 == stmt.executeUpdate(String.format(sql,u.getId(),u.getNombre(),u.getContrasena()))) {					
					System.out.println(String.format(" - Usuario insertada: %s", u.toString()));
				} else {
					System.out.println(String.format(" - No se ha insertado el usuario: %s", u.toString()));
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
				pelicula = new Pelicula ();
				
				double Valoracion = ((double)rs.getInt("VALORACION"))/10;
				
                pelicula.setNombre (rs.getString ("NOMBRE"));
                pelicula.setValoracion (Valoracion);
                pelicula.setDirector (rs.getString ("DIRECTOR"));
                pelicula.setDuracion (Duration.ofMinutes (rs.getInt ("DURACION")));
                pelicula.setEdad (EdadRecomendada.valueOf (rs.getString ("EDAD_RECOMENDADA")));
                pelicula.setGeneros (Genero.toGeneros((short) rs.getInt("GENEROS")));
                
                peliculas.add(pelicula);
				
			}
			
			//Se cierra el ResultSet
			rs.close();
			
			System.out.println(String.format("- Se han recuperado %d clientes...", peliculas.size()));			
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
			String sql = "UPDATE PELICULA SET VALORACION = '%f' WHERE NOMBRE = '%s' ;";
			
			int result = stmt.executeUpdate(String.format(sql, newValoracion, pelicula.getNombre()));
			
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
			
			System.out.println(String.format("- Se han borrado %d pelicula", result));
		} catch (Exception ex) {
			System.err.println(String.format("* Error al borrar datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
	}	
}