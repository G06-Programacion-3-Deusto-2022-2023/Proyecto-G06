package cine;

import java.util.ArrayList;
import java.util.UUID;

public class Administrador extends Usuario{
	
	protected String llave;
	protected ArrayList<SetPeliculas> adminDpeliculas;
	
	
	public Administrador(String nombre, String contraseña, UUID id, String llave,
			ArrayList<SetPeliculas> adminDpeliculas) {
		super(nombre, contraseña, id);
		this.llave = llave;
		this.adminDpeliculas = adminDpeliculas;
	}
	
	public String getLlave() {
		return llave;
	}
	public void setLlave(String llave) {
		this.llave = llave;
	}

	public ArrayList<SetPeliculas> getAdminDpeliculas() {
		return adminDpeliculas;
	}
	public void setAdminDpeliculas(ArrayList<SetPeliculas> adminDpeliculas) {
		this.adminDpeliculas = adminDpeliculas;
	}

	@Override
	public String toString() {
		return "Administrador [llave=" + llave + ", adminDpeliculas=" + adminDpeliculas + "]";
	}
	
}
