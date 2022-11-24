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

public class SetPeliculas implements Comparable <SetPeliculas> {
    private static final Random random = new Random ();
    private static final int MIN_SIZE = 7;
    private static final int MAX_SIZE = 35;

    private static boolean DEFAULT_SET = false;
    private static final SetPeliculas DEFAULT = new SetPeliculas (new UUID (0L, 0L), null, "Set por defecto",
            Pelicula.getDefault ());

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

        interface GetPreviousClassName {
            String show (StackTraceElement stackTrace[]);
        }

        this.id = id != null && ((id.getMostSignificantBits () == 0
                && id.getLeastSignificantBits () == 0
                && ((GetPreviousClassName) (st -> String.format ("%s", st [1].getClassName ())))
                        .show (Thread.currentThread ().getStackTrace ()).equals ("cine.SetPeliculas"))
                || id.getMostSignificantBits () != 0 || id.getLeastSignificantBits () != 0)
                        ? id
                        : UUID.randomUUID ();
        this.setAdministrador (administrador);
        this.setNombre (nombre);
        this.setPeliculas (peliculas);

        if (this.isDefault ())
            SetPeliculas.DEFAULT_SET = true;
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
        if (this.isDefault () && SetPeliculas.DEFAULT_SET)
            return;

        this.administrador = administrador;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        if (this.isDefault () && SetPeliculas.DEFAULT_SET)
            return;

        if (nombre != null && !nombre.equals ("")) {
            this.nombre = nombre;

            return;
        }

        if (this.administrador == null) {
            this.nombre = this.id.toString ();

            return;
        }

        SetPeliculas array[] = this.administrador.getSetsPeliculas ().toArray (new SetPeliculas [0]);

        int nuevas = 0;
        for (int i = 0; i < array.length; nuevas += array [i++].getNombre ().contains ("Nuevo set") ? 1 : 0)
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
    public boolean equals (Object o) {
        return o instanceof SetPeliculas && this.id.equals (((SetPeliculas) o).id);
    }

    @Override
    public int compareTo (SetPeliculas set) {
        if (set == null)
            return 1;

        if (this.nombre.equals (this.id.toString ()) && !set.nombre.equals (set.id.toString ()))
            return 1;

        if (!this.nombre.equals (this.id.toString ()) && set.nombre.equals (set.id.toString ()))
            return -1;

        if (this.nombre.equals (this.id.toString ()) && set.nombre.equals (set.id.toString ()))
            return this.id.compareTo (set.id);

        int comp;
        if ((comp = this.nombre.compareTo (set.nombre)) != 0)
            return comp;

        return this.id.compareTo (set.id);
    }

    @Override
    public String toString () {
        return "SetPeliculas [id=" + this.id.toString () + ", peliculas=" + this.peliculas.toString () + "]";
    }

    public int size () {
        return this.peliculas.size ();
    }

    public boolean add (Pelicula pelicula) {
        if (this.isDefault () && SetPeliculas.DEFAULT_SET)
            return false;

        if (pelicula == null)
            return false;

        if (this.peliculas.add (pelicula)) {
            if (!pelicula.addSet (this)) {
                this.remove (pelicula);

                return false;
            }

            return true;
        }

        return false;
    }

    public boolean remove (Pelicula pelicula) {
        if (this.isDefault () && SetPeliculas.DEFAULT_SET)
            return false;

        if (!this.contains (pelicula))
            return false;

        if (this.peliculas.remove (pelicula)) {
            if (!pelicula.removeSet (this)) {
                this.add (pelicula);

                return false;
            }

            return true;
        }

        return false;
    }

    public boolean contains (Pelicula pelicula) {
        return pelicula != null && this.peliculas.contains (pelicula);
    }

    public boolean isDefault () {
        return this.id.equals (new UUID (0L, 0L));
    }

    public static SetPeliculas getDefault () {
        return SetPeliculas.DEFAULT;
    }

    public static SetPeliculas random () {
        return SetPeliculas.random (Pelicula.getDefault ());
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
