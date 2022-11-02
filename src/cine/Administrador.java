package cine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Administrador extends Usuario {
    protected ArrayList <SetPeliculas> setsPeliculas;

    public Administrador () {
        this ("");
    }

    public Administrador (String nombre) {
        this (nombre, "");
    }

    public Administrador (String nombre, String contrasena) {
        this (nombre, contrasena, new ArrayList <SetPeliculas> ());
    }

    public Administrador (String nombre, String contrasena,
            ArrayList <SetPeliculas> setsPeliculas) {
        this (UUID.randomUUID (), nombre, contrasena, setsPeliculas);
    }

    public Administrador (UUID id, String nombre, String contrasena, ArrayList <SetPeliculas> setsPeliculas) {
        super (id, nombre, contrasena);

        this.setSetsPeliculas (setsPeliculas);
    }

    public Administrador (Administrador administrador) {
        this (administrador.nombre, administrador.contrasena, administrador.setsPeliculas);
    }

    public List <SetPeliculas> getSetsPeliculas () {
        return this.setsPeliculas;
    }

    public void setSetsPeliculas (ArrayList <SetPeliculas> setsPeliculas) {
        this.setsPeliculas = setsPeliculas == null ? new ArrayList <SetPeliculas> () : (ArrayList <SetPeliculas>) setsPeliculas;
    }

    @Override
    public String toString () {
        return "Admistrador " + super.toString ();
    }
}
