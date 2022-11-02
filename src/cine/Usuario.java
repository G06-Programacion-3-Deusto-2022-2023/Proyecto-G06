package cine;

import java.util.UUID;

public abstract class Usuario  {
	
	
	protected String nombre;
	protected String contrase�a;
	protected UUID id;
	
	public Usuario(String nombre, String contrase�a, UUID id) {
		super();
		this.nombre = nombre;
		this.contrase�a = contrase�a;
		this.id = UUID.randomUUID();
	}
	public Usuario() {
		super();
		this.nombre = "";
		this.contrase�a = "";
		this.id = UUID.randomUUID();
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getContrase�a() {
		return contrase�a;
	}
	public void setContrase�a(String contrase�a) {
		this.contrase�a = contrase�a;
	}
	
	@Override
	public String toString() {
		return "Usuario [nombre=" + nombre + ", contraseña=" + contrase�a + ", id=" + id + "]";
	}
	
	
	
}
