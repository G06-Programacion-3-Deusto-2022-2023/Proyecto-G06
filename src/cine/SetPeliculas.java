package cine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class SetPeliculas {
    private static final Random random = new Random ();
    public static final int MIN_SIZE = 7;
    public static final int MAX_SIZE = 35;

    public static final SetPeliculas DEFAULT = new SetPeliculas (new UUID (0L, 0L), null, "Set por defecto",
            Pelicula.DEFAULT_PELICULAS);

    protected UUID id;
    protected Administrador administrador;
    protected String nombre;
    protected TreeSet <Pelicula> peliculas;

    public SetPeliculas () {
        this ("", null);
    }

    public SetPeliculas (String nombre) {
        this (nombre, null);
    }

    public SetPeliculas (Collection <Pelicula> peliculas) {
        this (null, peliculas);
    }

    public SetPeliculas (String nombre, Collection <Pelicula> peliculas) {
        this (null, nombre, peliculas);
    }

    public SetPeliculas (Administrador administrador, String nombre, Collection <Pelicula> peliculas) {
        this (UUID.randomUUID (), administrador, nombre, peliculas);
    }

    public SetPeliculas (UUID id, Administrador administrador, String nombre, Collection <Pelicula> peliculas) {
        super ();

        this.id = id;
        this.setAdministrador (administrador);
        this.setNombre (nombre);
        this.setPeliculas (peliculas);
    }

    public SetPeliculas (SetPeliculas setPeliculas) {
        this (setPeliculas.id, setPeliculas.administrador, setPeliculas.nombre, setPeliculas.peliculas);
    }

    public SetPeliculas (SetPeliculas setPeliculas, Administrador administrador) {
        this (setPeliculas.id, administrador, setPeliculas.nombre, setPeliculas.peliculas);
    }

    public UUID getId () {
        return this.id;
    }

    public Administrador getAdministrador () {
        return this.administrador;
    }

    public void setAdministrador (Administrador administrador) {
        this.administrador = administrador;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        if (nombre != null && !nombre.equals ("")) {
            this.nombre = nombre;

            return;
        }

        if (this.administrador == null) {
            this.nombre = this.id.toString ();

            return;
        }

        int nuevas = 0;
        for (int i = 0; i < this.administrador.getSetsPeliculas ()
                .size (); nuevas += this.administrador.getSetsPeliculas ()
                        .get (i++).getNombre ().contains ("Nuevo set") ? 1 : 0)
            ;

        this.nombre = String.format ("Nuevo set%s", nuevas == 0 ? "" : String.format (" #%d", nuevas + 1));
    }

    public Set <Pelicula> getPeliculas () {
        return this.peliculas;
    }

    public void setPeliculas (Collection <Pelicula> peliculas) {
        this.peliculas = new TreeSet <Pelicula> (
                peliculas == null || peliculas.size () < SetPeliculas.MIN_SIZE
                        || peliculas.size () > SetPeliculas.MAX_SIZE
                                ? Collections.emptySet ()
                                : peliculas);
    }

    @Override
    public String toString () {
        return "SetPeliculas [id=" + id.toString () + ", peliculas=" + peliculas.toString () + "]";
    }

    public int size () {
        return this.peliculas.size ();
    }

    public static SetPeliculas random () {
        return SetPeliculas.random (Pelicula.DEFAULT_PELICULAS);
    }

    public static SetPeliculas random (Collection <Pelicula> peliculas) {
        if (peliculas == null || peliculas.size () < SetPeliculas.MIN_SIZE)
            return null;

        return SetPeliculas.random (peliculas, SetPeliculas.random.nextInt (MIN_SIZE,
                Math.min (new HashSet <Pelicula> (peliculas).size (), SetPeliculas.MAX_SIZE)));
    }

    public static SetPeliculas random (Collection <Pelicula> peliculas, int n) {
        if (peliculas == null || peliculas.size () < SetPeliculas.MIN_SIZE)
            return null;

        ArrayList <Pelicula> list = new ArrayList <Pelicula> (
                new HashSet <Pelicula> (peliculas).stream ().limit (SetPeliculas.MAX_SIZE)
                        .collect (Collectors.toList ()));
        Collections.shuffle (list);

        return new SetPeliculas (list.stream ().limit (n).collect (Collectors.toSet ()));
    }

    public List <String> getNombresPeliculas () {
        return Pelicula.getNombres (Arrays.asList (this.peliculas.toArray (new Pelicula [0])));
    }
}
