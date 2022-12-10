package cine;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class Administrador extends Usuario implements Treeable <Administrador>, Comparable <Administrador> {
    private SortedSet <SetPeliculas> setsPeliculas;

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
        this.setsPeliculas = new TreeSet <SetPeliculas> (
                (Comparator <SetPeliculas>) (a, b) -> a.getId ().compareTo (b.getId ()));

        if (setsPeliculas != null)
            this.setsPeliculas.addAll (setsPeliculas);
    }

    public int compareTo (Administrador o) {
        return super.compareTo(o);
    }

    @Override
    public String toString () {
        return super.toString ();
    }

    public static BST <Administrador> tree (Collection <Administrador> values) {
        return Administrador.tree (values, null, null);
    }

    public static BST <Administrador> tree (Collection <Administrador> values, Comparator <Administrador> comp) {
        return Administrador.tree (values, comp, null);
    }

    public static BST <Administrador> tree (Collection <Administrador> values, Filter <Administrador> filter) {
        return Administrador.tree (values, null, filter);
    }

    public static BST <Administrador> tree (Collection <Administrador> values, Comparator <Administrador> comp,
            Filter <Administrador> filter) {
        return new Administrador ().bst (values, comp, filter);
    }
}
