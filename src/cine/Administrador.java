package cine;

import java.util.ArrayList;
import java.util.UUID;

public class Administrador extends Usuario{
	
	protected ArrayList<SetPeliculas> adminDpeliculas;
	
	
	public Administrador(String nombre, String contrase�a, UUID id, String llave,
			ArrayList<SetPeliculas> adminDpeliculas) {
		super(nombre, contrase�a, id);
		this.adminDpeliculas = adminDpeliculas;
	}
	public Administrador(String nombre, String contrase�a, UUID id) {
		super(nombre, contrase�a, id);
		this.adminDpeliculas = new ArrayList<SetPeliculas>()  ;
	}
	

	public ArrayList<SetPeliculas> getAdminDpeliculas() {
		return adminDpeliculas;
	}
	public void setAdminDpeliculas(ArrayList<SetPeliculas> adminDpeliculas) {
		this.adminDpeliculas = adminDpeliculas;
	}

	@Override
	public String toString() {
		return "Administrador [llave=" + ", adminDpeliculas=" + adminDpeliculas + "]";
	}
	
}
