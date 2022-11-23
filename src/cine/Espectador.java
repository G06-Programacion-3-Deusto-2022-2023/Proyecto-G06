package cine;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

public class Espectador extends Usuario {
    private static final Random random = new Random ();

    public static final byte DEFAULT_EDAD = 18;
    public static final byte MAX_EDAD = 100;

    protected byte edad;
    protected SortedMap <Genero.Nombre, Genero.Preferencia> preferencias;
    protected SortedSet <Entrada> historial;

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
        this (UUID.randomUUID (), nombre, contrasena, edad, preferencias, historial);
    }

    public Espectador (UUID id, String nombre, String contrasena, byte edad,
            Map <Genero.Nombre, Genero.Preferencia> preferencias, Collection <Entrada> historial) {
        super (id, nombre, contrasena);

        this.setEdad (edad);
        this.setPreferencias (preferencias);
        this.setHistorial (historial);
    }

    public Espectador (Espectador espectador) {
        this (espectador.id, espectador.nombre, espectador.contrasena, espectador.edad, espectador.preferencias,
                espectador.historial);
    }

    public byte getEdad () {
        return this.edad;
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

    @Override
    public String toString () {
        return super.toString () + ", preferencias = " + preferencias.toString ();
    }

    public static Espectador random () {
        return new Espectador ("", "", (byte) Espectador.random.nextInt (Espectador.MAX_EDAD), Genero.randomPrefs ());
    }

    public byte fromPreferencias (Pelicula pelicula) {
        Genero.Nombre values [] = pelicula.getGeneros ().toArray (new Genero.Nombre [0]);

        byte ret = 0;
        for (int i = 0; i < values.length; ret += this.preferencias.get (values [i++]).getValue ())
            ;

        return ret;
    }
}