package cine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

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
		//Se abre la conexi贸n y se obtiene el Statement
		//Al abrir la conexi贸n, si no exist铆a el fichero, se crea la base de datos
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			
	        String sql1 = "CREATE TABLE IF NOT EXISTS PELICULA (\n"
	                   + " ID_PELICULA STRING ,\n"
	                   + " NOMBRE_PELICULA STRING PRIMARY KEY NOT NULL,\n"
	                   + " RUTA_IMAGEN STRING, \n"
	                   + " VALORACION INTEGER NOT NULL,\n"
	                   + " FECHA INTEGER, \n"
	                   + " DIRECTOR STRING NOT NULL,\n"
	                   + " DURACION INTEGER NOT NULL,\n"
	                   + " EDAD_RECOMENDADA INTEGER NOT NULL,\n"
	                   + " GENEROS INTEGER NOT NULL\n"
	                   + ");";
	        String sql2 = "CREATE TABLE IF NOT EXISTS ADMINISTRADOR (\n"
	                   + " ID_ADMINISTRADOR STRING ,\n"
	                   + " NOMBRE_ADMINISTRADOR STRING PRIMARY KEY NOT NULL ,\n"
	                   + " CONTRASENA_ADMINISTRADOR STRING \n"
	                   + ");";
	        String sql3 = "CREATE TABLE IF NOT EXISTS ESPECTADOR (\n"
	                   + " ID_ESPECTADOR STRING ,\n"
	                   + " NOMBRE_ESPECTADOR STRING PRIMARY KEY NOT NULL ,\n"
	                   + " CONTRASENA_ESPECTADOR STRING, \n"
	                   + " EDAD INTEGER \n"
	                   + ");";
	        String sql4 = "CREATE TABLE IF NOT EXISTS COMPLEMENTO (\n"
	        		   + " ID_COMPLEMENTO STRING ,\n"
	        		   + " NOMBRE_COMPLEMENTO STRING PRIMARY KEY NOT NULL ,\n"
	        		   + " PRECIO DECIMAL(6,4), \n"
	        		   + " DESCUENTO INTEGER \n"
	        		   + ");";
	        String sql5 = "CREATE TABLE IF NOT EXISTS SET_PELICULA (\n"
	        		   + "ID_SET_PELICULA STRING"
	        		   + "NOMBRE_ADMINISTRADOR STRING, \n"
	        		   + "NOMBRE_SET_PELICULA STRING \n"
	        		   + ");";
	        	        
	        if (!stmt.execute(sql1) && !stmt.execute(sql2) && !stmt.execute(sql3) && !stmt.execute(sql4) && !stmt.execute(sql5)) {
	        	System.out.println("- Se ha creado la tabla pelicula, la tabla administrador, la tabla espectador, la tabla complemento y la tabla set_Pelicula");
	        }
	        
		} catch (Exception ex) {
			System.err.println(String.format("* Error al crear la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();			
		}
	}
	
	public void borrarBBDD() {
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			
	        String sql1 = "DROP TABLE IF EXISTS PELICULA";
	        String sql2 = "DROP TABLE IF EXISTS ADMINISTRADOR";
	        String sql3 = "DROP TABLE IF EXISTS ESPECTADOR";
	        String sql4 = "DROP TABLE IF EXISTS COMPLEMENTO";
	        String sql5 = "DROP TABLE IF EXISTS SET_PELICULA";
			
	        //Se ejecuta la sentencia de creaci贸n de la tabla Estudiantes
	        if (!stmt.execute(sql1) && !stmt.execute(sql2) && !stmt.execute(sql3) && !stmt.execute(sql4) && !stmt.execute(sql5)) {
	        	System.out.println("Se han borrado las tablas");
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
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se define la plantilla de la sentencia SQL
			String sql = "INSERT INTO PELICULA (ID_PELICULA, NOMBRE_PELICULA,RUTA_IMAGEN, VALORACION, FECHA, DIRECTOR, DURACION, EDAD_RECOMENDADA, GENEROS) VALUES ('%s', '%s', '%s', %d, '%s','%s', %d, '%s', %d);";
			
			System.out.println("- Insertando peliculas...");
			
			//Se recorren los clientes y se insertan uno a uno
			for (Pelicula p : peliculas) {
				int Valoracion = (int) p.getValoracion();
				if (1 == stmt.executeUpdate(String.format(sql, p.getId().toString(), p.getNombre(),p.getRutaImagen(),(int)Valoracion,p.getFecha(), p.getDirector(),(int) p.getDuracion().toMinutes(), p.getEdad().getValue(), Genero.Nombre.toValor(p.getGeneros())))) {					
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
	public void insertarDatosAdministrador(Administrador...administrador) {
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se define la plantilla de la sentencia SQL
			String sql = "INSERT INTO ADMINISTRADOR (ID_ADMINISTRADOR, NOMBRE_ADMINISTRADOR, CONTRASENA_ADMINISTRADOR) VALUES ('%s', '%s', %s');";
			
			System.out.println("- Insertando administrador...");
			
			for (Administrador a: administrador) {
				if (1 == stmt.executeUpdate(String.format(sql,a.getId(),a.getNombre(),a.getContrasena()))) {					
					System.out.println(String.format(" - Administrador insertada: %s", a.toString()));
				} else {
					System.out.println(String.format(" - No se ha insertado el administrador: %s", a.toString()));
				}
			}			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al insertar datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}				
	}
	public void insertarDatosEspectador(Espectador...espectador) {
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se define la plantilla de la sentencia SQL
			String sql = "INSERT INTO ESPECTADOR (ID_ESPECTADOR, NOMBRE_ESPECTADOR, CONTRASENA_ESPECTADOR, EDAD) VALUES ('%s', '%s', '%s', %d);";
			
			System.out.println("- Insertando usuarios...");
			
			for (Espectador e: espectador) {
				if (1 == stmt.executeUpdate(String.format(sql,e.getId(),e.getNombre(),e.getContrasena(), e.getEdad()))) {					
					System.out.println(String.format(" - Usuario insertada: %s", e.toString()));
				} else {
					System.out.println(String.format(" - No se ha insertado el usuario: %s", e.toString()));
				}
			}			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al insertar datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}				
	}
	public void insertarDatosComplemento(Complemento...complementos) {
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
			     Statement stmt = con.createStatement()) {
				//Se define la plantilla de la sentencia SQL
				String sql = "INSERT INTO COMPLEMENTO (ID_COMPLEMENTO, NOMBRE_COMPLEMENTO, PRECIO, DESCUENTO) VALUES ('%s', '%s', f% , '%s');";
				
				System.out.println("- Insertando complementos...");
				
				//Se recorren los clientes y se insertan uno a uno
				for (Complemento c : complementos) {
					if (1 == stmt.executeUpdate(String.format(sql, c.getId(), c.getNombre(), c.getPrecio(), c.getDescuento()))) {					
						System.out.println(String.format(" - Complementos insertada: %s", c.toString()));
					} else {
						System.out.println(String.format(" - No se ha insertado el complemento: %s", c.toString()));
					}
				}			
			} catch (Exception ex) {
				System.err.println(String.format("* Error al insertar datos de la BBDD: %s", ex.getMessage()));
				ex.printStackTrace();						
			}
	}
	public void insertarDatosSetPelicula(SetPeliculas...setPeliculas) {
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
			     Statement stmt = con.createStatement()) {
				//Se define la plantilla de la sentencia SQL
				String sql = "INSERT INTO SET_PELICULA (ID_SET_PELICULA, NOMBRE_ADMINISTRADOR, NOMBRE_SET_PELICULA) VALUES ('%s', '%s', '%s');";
				
				System.out.println("- Insertando setPeliculas...");
				
				//Se recorren los clientes y se insertan uno a uno
				for (SetPeliculas s: setPeliculas) {
					if (1 == stmt.executeUpdate(String.format(sql, s.getId(), s.getAdministrador().getNombre(), s.getNombre()))) {					
						System.out.println(String.format(" - set_Peliculas insertadas: %s", s.toString()));
					} else {
						System.out.println(String.format(" - No se ha insertado el setPeliculas: %s", s.toString()));
					}
				}			
			} catch (Exception ex) {
				System.err.println(String.format("* Error al insertar datos de la BBDD: %s", ex.getMessage()));
				ex.printStackTrace();						
			}
	}
	
	public List<Pelicula> obtenerDatosPeliculas() {
		List<Pelicula> peliculas = new ArrayList<>();
		
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			String sql = "SELECT * FROM PELICULA";
			
			//Se ejecuta la sentencia y se obtiene el ResultSet con los resutlados
			ResultSet rs = stmt.executeQuery(sql);			
			Pelicula pelicula;
			
			//Se recorre el ResultSet y se crean objetos Cliente
			while (rs.next()) {
				
				UUID id = UUID.fromString(rs.getString("ID_PELICULA"));
				double Valoracion = (double)rs.getInt("VALORACION");
				Year fecha = Year.of(rs.getInt("FECHA"));
				Set<Genero.Nombre> genero = (Set<Genero.Nombre>) Genero.Nombre.toGeneros((short) rs.getInt("GENEROS"));
				
				pelicula = new Pelicula(id,rs.getString("NOMBRE_PELICULA"),rs.getString("RUTA_IMAGEN"), Valoracion, fecha, rs.getString("DIRECTOR"), Duration.ofMinutes(rs.getInt("DURACION")), EdadRecomendada.fromValue(rs.getByte("EDAD_RECOMENDADA")),genero, null);
				
                peliculas.add(pelicula);
				
			}
			
			//Se cierra el ResultSet
			rs.close();
			
			System.out.println(String.format("- Se han recuperado %d peliculas...", peliculas.size()));			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al obtener datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
		
		return peliculas;
	}
	public List<Administrador> obtenerDatosAdministradores() {
		List<Administrador> administradores = new ArrayList<>();
		
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			String sql = "SELECT * FROM ADMINISTRADOR";
			
			//Se ejecuta la sentencia y se obtiene el ResultSet con los resutlados
			ResultSet rs = stmt.executeQuery(sql);			
			Administrador administrador;
			
			//Se recorre el ResultSet y se crean objetos Cliente
			while (rs.next()) {
				UUID id = UUID.fromString(rs.getString("ID_ADMINISTRADOR"));
				
				administrador = new Administrador(id, rs.getString("NOMBRE_ADMINISTRADOR"), rs.getString("CONTRASE袮_ADMINISTRADOR"), null);
				
				administradores.add(administrador);
			}
			
			//Se cierra el ResultSet
			rs.close();
			
			System.out.println(String.format("- Se han recuperado %d administradores...", administradores.size()));			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al obtener datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
		
		return administradores;
	}
	public List<Espectador> obtenerDatosEspectadores() {
		List<Espectador> espectadores = new ArrayList<>();
		
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			String sql = "SELECT * FROM ESPECTADOR";
			
			//Se ejecuta la sentencia y se obtiene el ResultSet con los resutlados
			ResultSet rs = stmt.executeQuery(sql);			
			Espectador espectador;
			
			//Se recorre el ResultSet y se crean objetos Cliente
			while (rs.next()) {
				UUID id = UUID.fromString(rs.getString("ID_ESPECTADOR"));
				Map <Genero.Nombre, Genero.Preferencia> preferencias = new TreeMap<Genero.Nombre, Genero.Preferencia>();
				Collection <Entrada> historial = new TreeSet<Entrada>();
				Set <Espectador> grupo = new TreeSet<Espectador>();
				
				espectador = new Espectador(id, rs.getString("NOMBRE_ESPECTADOR"), rs.getString("CONTRASE袮_ESPECTADOR"),(byte) rs.getInt("EDAD"), preferencias, historial, grupo);
				
				espectadores.add(espectador);
			}
			
			//Se cierra el ResultSet
			rs.close();
			
			System.out.println(String.format("- Se han recuperado %d espectadores...", espectadores.size()));			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al obtener datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
		
		return espectadores;
	}
	public List<Complemento> obtenerDatosComplementos() {
		List<Complemento> complementos = new ArrayList<>();
		
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			String sql = "SELECT * FROM COMPLEMENTO";
			
			//Se ejecuta la sentencia y se obtiene el ResultSet con los resutlados
			ResultSet rs = stmt.executeQuery(sql);			
			Complemento complemento;
			
			//Se recorre el ResultSet y se crean objetos Cliente
			while (rs.next()) {
				
				UUID id = UUID.fromString(rs.getString("ID_COMPLEMENTO"));
				
				complemento = new Complemento(id, rs.getString("NOMBRE_COMPLEMENTO"), rs.getBigDecimal("PRECIO"), rs.getInt("DESCUENTO"));
				
				complementos.add(complemento);
			}
			
			//Se cierra el ResultSet
			rs.close();
			
			System.out.println(String.format("- Se han recuperado %d complementos...", complementos.size()));			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al obtener datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
		
		return complementos;
	}
	public List<SetPeliculas> obtenerDatosSetPeliculas() {
		List<SetPeliculas> setsPeliculas = new ArrayList<>();
		
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			String sql = "SELECT * FROM SET_PELICULAS";
			
			//Se ejecuta la sentencia y se obtiene el ResultSet con los resutlados
			ResultSet rs = stmt.executeQuery(sql);			
			SetPeliculas setPeliculas;
			
			//Se recorre el ResultSet y se crean objetos Cliente
			while (rs.next()) {
				
				UUID id = UUID.fromString("ID_SET_PELICULA");
				
				setPeliculas = new SetPeliculas(id, obtenerDatosAdministradorPorNombre(rs.getString("NOMBRE_ADMINISTRADOR")), rs.getString("NOMBRE_SET_PELICULA"), null);
				
				setsPeliculas.add(setPeliculas);
			}
			
			//Se cierra el ResultSet
			rs.close();
			
			System.out.println(String.format("- Se han recuperado %d sets_peliculas...", setsPeliculas.size()));			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al obtener datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
		
		return setsPeliculas;
	}
	public Administrador obtenerDatosAdministradorPorNombre(String NombreAdministrador) {
		Administrador administrador = new Administrador();
		
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			String sql = "SELECT * FROM ADMINISTRADOR WHERE NOMBRE_ADMINISTRADOR = '" + NombreAdministrador + "'";
			
			//Se ejecuta la sentencia y se obtiene el ResultSet con los resutlados
			ResultSet rs = stmt.executeQuery(sql);		
			
			while (rs.next()) {
				UUID id = UUID.fromString(rs.getString("ID_ADMINISTRADOR"));
				
			 administrador = new Administrador(id, rs.getString("NOMBRE_ADMINISTRADOR"), rs.getString("CONTRASE袮_ADMINISTRADOR"), null);
				

			}
			
			//Se cierra el ResultSet
			rs.close();
			
			System.out.println(String.format("- Se ha recuperado el administrador '%s'...", NombreAdministrador));			
		} catch (Exception ex) {
			System.err.println(String.format("* Error al obtener datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
		
		return administrador;
	}
	

	//public void actualizarValoracion(Pelicula pelicula, double newValoracion) {
		//Se abre la conexi贸n y se obtiene el Statement
	//	try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
	//	     Statement stmt = con.createStatement()) {
	//		//Se ejecuta la sentencia de borrado de datos
	//		String sql = "UPDATE PELICULA SET VALORACION = '%f' WHERE NOMBRE = '%s' ;";
	//		
	//		int result = stmt.executeUpdate(String.format(sql, newValoracion, pelicula.getNombre()));
	//		
	//		System.out.println(String.format("- Se ha actulizado %d peliculas", result));
	//	} catch (Exception ex) {
	//		System.err.println(String.format("* Error actualizando datos de la BBDD: %s", ex.getMessage()));
	//		ex.printStackTrace();						
	//	}		
	//	}
	
	public void borrarDatos() {
		//Se abre la conexi贸n y se obtiene el Statement
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     Statement stmt = con.createStatement()) {
			//Se ejecuta la sentencia de borrado de datos
			String sql1 = "DELETE FROM PELICULA;";
			String sql2 = "DELETE FROM ADMINISTRADOR;";	
			String sql3 = "DELETE FROM ESPECTADOR;";	
			String sql4 = "DELETE FROM COMPLEMENTO;";	
			String sql5 = "DELETE FROM SET_PELICULA;";	
			
			int result1 = stmt.executeUpdate(sql1);
			int result2 = stmt.executeUpdate(sql2);
			int result3 = stmt.executeUpdate(sql3);
			int result4 = stmt.executeUpdate(sql4);
			int result5 = stmt.executeUpdate(sql5);
			
			System.out.println(String.format("- Se han borrado %d peliculas, %d Administradores, %d espectadores, %d complementos, %d setsPeliculas,", result1, result2, result3, result4, result5));
		} catch (Exception ex) {
			System.err.println(String.format("* Error al borrar datos de la BBDD: %s", ex.getMessage()));
			ex.printStackTrace();						
		}		
	}	
}