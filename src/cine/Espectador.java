package cine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Espectador extends Usuario {
    protected byte edad;
    protected ArrayList <Entrada> historial;

    public Espectador () {
        this ("");
    }

    public Espectador (String nombre) {
        this (nombre, "");
    }

    public Espectador (String nombre, String contrasena) {
        this (nombre, contrasena, (byte) 18);
    }

    public Espectador (String nombre, String contrasena, byte edad) {
        this (nombre, contrasena, edad, new ArrayList <Entrada> ());
    }

    public Espectador (String nombre, String contrasena, byte edad, List <Entrada> historial) {
        this (UUID.randomUUID (), nombre, contrasena, edad, historial);
    }

    public Espectador (UUID id, String nombre, String contrasena, byte edad, List <Entrada> historial) {
        super (id, nombre, contrasena);

        this.setEdad (edad);
        this.setHistorial (historial);
    }

    public Espectador (Espectador espectador) {
        this (espectador.id, espectador.nombre, espectador.contrasena, espectador.edad, espectador.historial);
    }

    public byte getEdad () {
        return this.edad;
    }

    public void setEdad (byte edad) {
        this.edad = (edad < 0 || edad > 100) ? 18 : edad;
    }

    public List <Entrada> getHistorial () {
        return this.historial;
    }

    public void setHistorial (List <Entrada> historial) {
        this.historial = historial == null ? new ArrayList <Entrada> () : (ArrayList <Entrada>) historial;
    }

    @Override
    public String toString () {
        return "Usuario " + super.toString ();
    }
}