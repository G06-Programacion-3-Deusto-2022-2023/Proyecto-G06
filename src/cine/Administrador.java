package cine;

import java.util.ArrayList;
import java.util.Collections;
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
        this (nombre, contrasena, null);
    }

    public Administrador (String nombre, String contrasena,
            List <SetPeliculas> setsPeliculas) {
        this (UUID.randomUUID (), nombre, contrasena, setsPeliculas);
    }

    public Administrador (UUID id, String nombre, String contrasena, List <SetPeliculas> setsPeliculas) {
        super (id, nombre, contrasena);

        this.setSetsPeliculas (setsPeliculas);
    }

    public Administrador (Administrador administrador) {
        this (administrador.nombre, administrador.contrasena, administrador.setsPeliculas);
    }

    public List <SetPeliculas> getSetsPeliculas () {
        return this.setsPeliculas;
    }

    public void setSetsPeliculas (List <SetPeliculas> setsPeliculas) {
        this.setsPeliculas = new ArrayList <SetPeliculas> (setsPeliculas == null ? Collections.emptyList () : (ArrayList <SetPeliculas>) setsPeliculas);
    }

    @Override
    public String toString () {
        return "Admistrador " + super.toString ();
    }
}
