package cine;

import java.util.UUID;

public abstract class Usuario  {
	
	
	protected String nombre;
	protected String contraseña;
	protected UUID id;
	
	public Usuario(String nombre, String contraseña, UUID id) {
		super();
		this.nombre = nombre;
		this.contraseña = contraseña;
		this.id = UUID.randomUUID();
	}
	public Usuario() {
		super();
		this.nombre = "";
		this.contraseña = "";
		this.id = UUID.randomUUID();
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getContraseña() {
		return contraseña;
	}
	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}
	
	@Override
	public String toString() {
		return "Usuario [nombre=" + nombre + ", contraseña=" + contraseña + ", id=" + id + "]";
	}
	
	
	
}
