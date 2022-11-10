package cine;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

public class Espectador extends Usuario {
    private static final Random random = new Random ();
    public static final byte EDAD_DEFAULT = 18;

    protected byte edad;
    protected TreeMap <Genero.Nombre, Genero.Preferencia> preferencias;
    protected ArrayList <Entrada> historial;

    public Espectador () {
        this ("");
    }

    public Espectador (String nombre) {
        this (nombre, "");
    }

    public Espectador (String nombre, String contrasena) {
        this (nombre, contrasena, Espectador.EDAD_DEFAULT);
    }

    public Espectador (String nombre, String contrasena, byte edad) {
        this (nombre, contrasena, edad, null);
    }

    public Espectador (String nombre, String contrasena, byte edad,
            Map <Genero.Nombre, Genero.Preferencia> preferencias) {
        this (nombre, contrasena, edad, preferencias, null);
    }

    public Espectador (String nombre, String contrasena, byte edad,
            Map <Genero.Nombre, Genero.Preferencia> preferencias, List <Entrada> historial) {
        this (UUID.randomUUID (), nombre, contrasena, edad, preferencias, historial);
    }

    public Espectador (UUID id, String nombre, String contrasena, byte edad,
            Map <Genero.Nombre, Genero.Preferencia> preferencias, List <Entrada> historial) {
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
        this.edad = edad < 0 ? Espectador.EDAD_DEFAULT : edad;
    }

    public Map <Genero.Nombre, Genero.Preferencia> getPreferencias () {
        return this.preferencias;
    }

    public void setPreferencias (Map <Genero.Nombre, Genero.Preferencia> preferencias) {
        this.preferencias = new TreeMap <Genero.Nombre, Genero.Preferencia> (preferencias == null ? Collections.emptyMap () : preferencias);

        for (int i = 0; i < Genero.Nombre.values ().length; i++)
            if (!this.preferencias.containsKey (Genero.Nombre.values () [i]))
                this.preferencias.put (Genero.Nombre.values () [i], Genero.Preferencia.NADA);
    }

    public List <Entrada> getHistorial () {
        return this.historial;
    }

    public void setHistorial (List <Entrada> historial) {
        this.historial = new ArrayList <Entrada> (historial == null ? Collections.emptyList () : historial);
    }

    @Override
    public String toString () {
        return "Usuario " + super.toString () + ", preferencias = " + preferencias.toString ();
    }

    public static Espectador random () {
        return new Espectador ("", "", (byte) Espectador.random.nextInt (100), Genero.randomPrefs ());
    }

    public byte fromPreferencias (Pelicula pelicula) {
        byte ret = 0;
        for (int i = 0; i < pelicula.getGeneros ().size (); i++)
            ret += this.preferencias.get (pelicula.getGeneros ().get (i)).getValue ();

        return ret;
    }
}