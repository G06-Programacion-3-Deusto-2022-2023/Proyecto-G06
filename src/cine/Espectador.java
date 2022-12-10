package cine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class Espectador extends Usuario implements Treeable <Espectador>, Comparable <Espectador> {
    private static final Random random = new Random ();

    private static final int MAX_GRUPOS = 25;
    private static final int MAX_SIZE_GRUPO = 5;

    private static final byte DEFAULT_EDAD = 18;
    private static final byte MAX_EDAD = 100;

    private byte edad;
    private SortedMap <Genero.Nombre, Genero.Preferencia> preferencias;
    private SortedSet <Entrada> historial;
    private Set <Espectador> grupo;

    public Espectador () {
        this ("");
    }

    public Espectador (String nombre) {
        this (nombre, "");
    }

    public Espectador (String nombre, String contrasena) {
        this (nombre, contrasena, Espectador.DEFAULT_EDAD);
    }

    public Espectador (String nombre, String contrasena, byte edad) {
        this (nombre, contrasena, edad, null);
    }

    public Espectador (String nombre, String contrasena, byte edad,
            Map <Genero.Nombre, Genero.Preferencia> preferencias) {
        this (nombre, contrasena, edad, preferencias, null);
    }

    public Espectador (String nombre, String contrasena, byte edad,
            Map <Genero.Nombre, Genero.Preferencia> preferencias, Collection <Entrada> historial) {
        this (nombre, contrasena, edad, preferencias, historial, null);
    }

    public Espectador (String nombre, String contrasena, byte edad,
            Map <Genero.Nombre, Genero.Preferencia> preferencias, Collection <Entrada> historial,
            Set <Espectador> grupo) {
        this (UUID.randomUUID (), nombre, contrasena, edad, preferencias, historial, grupo);
    }

    public Espectador (UUID id, String nombre, String contrasena, byte edad,
            Map <Genero.Nombre, Genero.Preferencia> preferencias, Collection <Entrada> historial,
            Set <Espectador> grupo) {
        super (id, nombre, contrasena);

        this.setEdad (edad);
        this.setPreferencias (preferencias);
        this.setHistorial (historial);
        this.setGrupo (grupo);
    }

    public Espectador (Espectador espectador) {
        this (espectador.id, espectador.nombre, espectador.contrasena, espectador.edad, espectador.preferencias,
                espectador.historial, espectador.grupo);
    }

    public byte getEdad () {
        return this.edad;
    }

    public static byte getDefaultEdad () {
        return Espectador.DEFAULT_EDAD;
    }

    public static byte getMaxEdad () {
        return Espectador.MAX_EDAD;
    }

    public void setEdad (byte edad) {
        this.edad = edad < 0 ? Espectador.DEFAULT_EDAD : edad;
    }

    public Map <Genero.Nombre, Genero.Preferencia> getPreferencias () {
        return this.preferencias;
    }

    public void setPreferencias (Map <Genero.Nombre, Genero.Preferencia> preferencias) {
        this.preferencias = new TreeMap <Genero.Nombre, Genero.Preferencia> (
                preferencias == null ? Collections.emptyMap () : preferencias);

        for (int i = 0; i < Genero.Nombre.values ().length; i++)
            if (!this.preferencias.containsKey (Genero.Nombre.values () [i]))
                this.preferencias.put (Genero.Nombre.values () [i], Genero.Preferencia.NADA);
    }

    public SortedSet <Entrada> getHistorial () {
        return this.historial;
    }

    public void setHistorial (Collection <Entrada> historial) {
        this.historial = new TreeSet <Entrada> (historial == null ? Collections.emptySet () : historial);
    }

    public Set <Espectador> getGrupo () {
        return this.grupo;
    }

    public void setGrupo (Set <Espectador> grupo) {
        this.grupo = grupo;
    }

    public int compareTo (Espectador o) {
        return super.compareTo (o);
    }

    @Override
    public String toString () {
        return super.toString () + ", preferencias = " + preferencias.toString ();
    }

    public static Espectador random () {
        return new Espectador ("", "", (byte) Espectador.random.nextInt (Espectador.MAX_EDAD), Genero.randomPrefs ());
    }

    public static Set <Espectador> randoms (int n) throws UnsupportedOperationException {
        if (n < 0)
            throw new UnsupportedOperationException (
                    "El número de espectadores aleatorios a generar tiene que ser un número natural.");

        Set <Espectador> set = new HashSet <Espectador> ();
        for (; set.size () < n; set.add (Espectador.random ()))
            ;

        List <Set <Espectador>> grupos = new ArrayList <Set <Espectador>> ();

        List <Espectador> shuffle;
        for (int i = 0; i < 25 && grupos.size () < Espectador.MAX_GRUPOS; i++)
            if (Math.random () <= 1D / 3) {
                grupos.add (new HashSet <Espectador> ());

                Collections
                        .shuffle (shuffle = set.stream ().filter (e -> e.grupo == null).collect (Collectors.toList ()));
                shuffle = shuffle.subList (0, 50);
                for (int j = 0; j < shuffle.size ()
                        && grupos.get (grupos.size () - 1).size () < Espectador.MAX_SIZE_GRUPO; j++)
                    if (Math.random () < 0.05D)
                        grupos.get (grupos.size () - 1).add (shuffle.get (j));
            }

        return set;
    }

    public byte fromPreferencias (Pelicula pelicula) {
        Genero.Nombre values[] = pelicula.getGeneros ().toArray (new Genero.Nombre [0]);

        byte ret = 0;
        for (int i = 0; i < values.length; ret += this.preferencias.get (values [i++]).getValue ())
            ;

        return ret;
    }

    public static BST <Espectador> tree (Collection <Espectador> values) {
        return Espectador.tree (values, null, null);
    }

    public static BST <Espectador> tree (Collection <Espectador> values, Comparator <Espectador> comp) {
        return Espectador.tree (values, comp, null);
    }

    public static BST <Espectador> tree (Collection <Espectador> values, Filter <Espectador> filter) {
        return Espectador.tree (values, null, filter);
    }

    public static BST <Espectador> tree (Collection <Espectador> values, Comparator <Espectador> comp,
            Filter <Espectador> filter) {
        return new Espectador ().bst (values, comp, filter);
    }
}