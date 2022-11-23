package cine;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

public class Administrador extends Usuario {
    protected SortedSet <SetPeliculas> setsPeliculas;

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
            Collection <SetPeliculas> setsPeliculas) {
        this (UUID.randomUUID (), nombre, contrasena, setsPeliculas);
    }

    public Administrador (UUID id, String nombre, String contrasena, Collection <SetPeliculas> setsPeliculas) {
        super (id, nombre, contrasena);

        this.setSetsPeliculas (setsPeliculas);
    }

    public Administrador (Administrador administrador) {
        this (administrador.nombre, administrador.contrasena, administrador.setsPeliculas);
    }

    public SortedSet <SetPeliculas> getSetsPeliculas () {
        return this.setsPeliculas;
    }

    public void setSetsPeliculas (Collection <SetPeliculas> setsPeliculas) {
        this.setsPeliculas = new TreeSet <SetPeliculas> ((Comparator <SetPeliculas>) (a, b) -> a.getId ().compareTo (b.getId ()));

        if (setsPeliculas != null)
            this.setsPeliculas.addAll (setsPeliculas);
    }

    @Override
    public String toString () {
        return super.toString ();
    }
}
