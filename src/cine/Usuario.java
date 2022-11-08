package cine;

import java.util.UUID;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public abstract class Usuario {
    private static final int RPASSLEN = 14;

    protected UUID id;
    protected String nombre;
    protected String contrasena;

    protected Usuario () {
        this ("");
    }

    protected Usuario (String nombre) {
        this (nombre, "");
    }

    protected Usuario (String nombre, String contrasena) {
        this (UUID.randomUUID (), nombre, contrasena);
    }

    protected Usuario (UUID id, String nombre, String contrasena) {
        super ();

        this.id = id;
        this.setNombre (nombre);
        this.setContrasena (contrasena);
    }

    public UUID getId () {
        return this.id;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        this.nombre = nombre == null || nombre.equals ("") ? this.id.toString () : nombre;
    }

    public String getContrasena () {
        return this.contrasena;
    }

    public void setContrasena (String contrasena) {
        this.contrasena = contrasena == null || contrasena.equals ("")
            ? new PasswordGenerator ().generatePassword (
                Usuario.RPASSLEN,
                new CharacterRule (EnglishCharacterData.Special),
                new CharacterRule (EnglishCharacterData.LowerCase),
                new CharacterRule (EnglishCharacterData.UpperCase),
                new CharacterRule (EnglishCharacterData.Digit)
            )
            : contrasena
        ;
    }

    @Override
    public String toString () {
        return "[nombre=" + this.nombre + ", contraseña=" + this.contrasena + ", id=" + this.id + "]";
    }
}
