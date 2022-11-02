package cine;

import java.util.UUID;

public abstract class Usuario {
    protected String nombre;
    protected String contrasena;
    protected UUID id;

    public Usuario (String nombre, String contrasena) {
        super ();
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.id = UUID.randomUUID ();
    }

    public Usuario () {
        super ();
        this.nombre = "";
        this.contrasena = "";
        this.id = UUID.randomUUID ();
    }

    public String getNombre () {
        return nombre;
    }

    public void setNombre (String nombre) {
        this.nombre = nombre;
    }

    public String getcontrasena () {
        return contrasena;
    }

    public void setcontrasena (String contrasena) {
        this.contrasena = contrasena;
    }

    @Override
    public String toString () {
        return "Usuario [nombre=" + nombre + ", contraseña=" + contrasena + ", id=" + id + "]";
    }

}
