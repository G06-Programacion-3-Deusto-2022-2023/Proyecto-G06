package cine;

import java.util.UUID;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import internals.HasID;

public abstract class Usuario implements HasID {
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
                ? Usuario.generatePassword ()
                : contrasena;
    }

    public int compareTo (Usuario usuario) {
        if (usuario == null)
            return 1;

        if (this.nombre.equals (this.id.toString ()) && !usuario.nombre.equals (usuario.id.toString ()))
            return 1;

        if (!this.nombre.equals (this.id.toString ()) && usuario.nombre.equals (usuario.id.toString ()))
            return -1;

        if (this.nombre.equals (this.id.toString ()) && usuario.nombre.equals (usuario.id.toString ()))
            return this.id.compareTo (usuario.id);

        int comp;
        if ((comp = this.nombre.toLowerCase ().compareTo (usuario.nombre.toLowerCase ())) != 0)
            return comp;

        return this.id.compareTo (usuario.id);
    }

    @Override
    public int hashCode () {
        return super.hashCode ();
    }

    @Override
    public boolean equals (Object o) {
        return this.getClass ().isInstance (o) && this.id.equals ((this.getClass ().cast (o)).id);
    }

    @Override
    public String toString () {
        return this.getClass ().toString () + " " + "[nombre=" + this.nombre + ", contraseña=" + this.contrasena
                + ", id=" + this.id + "]";
    }

    public static String generatePassword () {
        enum CustomCharacterData implements org.passay.CharacterData {
            Special ("INSUFFICIENT_SPECIAL", "!#$%&()*+,-./:;<=>?@[\\]^_`{|}~");

            private final String errorCode;
            private final String characters;

            CustomCharacterData (final String code, final String charString) {
                errorCode = code;
                characters = charString;
            }

            @Override
            public String getErrorCode () {
                return errorCode;
            }

            @Override
            public String getCharacters () {
                return characters;
            }
        }

        return new PasswordGenerator ().generatePassword (
                Usuario.RPASSLEN,
                new CharacterRule (CustomCharacterData.Special),
                new CharacterRule (EnglishCharacterData.LowerCase),
                new CharacterRule (EnglishCharacterData.UpperCase),
                new CharacterRule (EnglishCharacterData.Digit));
    }
}
