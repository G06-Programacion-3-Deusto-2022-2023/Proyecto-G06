package cine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import internals.CustomCharacterData;
import internals.HasID;
import internals.Utils;

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
        return new PasswordGenerator ().generatePassword (
                Usuario.RPASSLEN,
                new CharacterRule (EnglishCharacterData.LowerCase),
                new CharacterRule (EnglishCharacterData.UpperCase),
                new CharacterRule (EnglishCharacterData.Digit),
                new CharacterRule (CustomCharacterData.Special));
    }

    public static List <Usuario> fromJSON (File file) throws NullPointerException, IOException, JSONException {
        if (file == null)
            throw new NullPointerException (String.format ("No se puede pasar un archivo nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        if (!file.exists ())
            throw new IOException (
                    String.format ("No se pudo encontrar el archivo especificado (%s).", file.getAbsolutePath ()));

        if (!Files.probeContentType (file.toPath ()).equals ("application/json"))
            throw new JSONException (
                    String.format ("El archivo especificado, %s, no es un archivo JSON válido.",
                            file.getAbsolutePath ()));

        try {
            return Usuario.fromJSON (Files.readString (file.toPath ()));
        }

        catch (IOException e) {
            throw new IOException (
                    String.format ("No se pudo abrir el archivo %s para recoger los datos.", file.getAbsolutePath ()));
        }

        catch (JSONException e) {
            throw new JSONException (String.format (
                    "El archivo especificado, %s, no es un archivo JSON válido que contenga un JSON Array.",
                    file.getAbsolutePath ()));
        }
    }

    public static List <Usuario> fromJSON (String jstr) throws NullPointerException, JSONException {
        if (jstr == null)
            throw new NullPointerException (String.format ("No se puede pasar un string nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        JSONArray json;
        try {
            json = new JSONArray (jstr);
        }

        catch (JSONException e) {
            throw new JSONException (Utils.isAmongstCallers ("cine.Usuario.fromJSON") ? ""
                    : "No se puede extraer un JSONArray válido de esta cadena de carácteres");
        }

        List <Usuario> list = new ArrayList <Usuario> ();
        SortedSet <Integer> errors = new TreeSet <Integer> ();
        for (int i = 0; i < json.length (); i++)
            try {
                list.add (Usuario.fromJSONObject (json.getJSONObject (i)));
            }

            catch (JSONException e) {
                errors.add (i);
            }

        Logger.getLogger (Usuario.class.getName ()).log (errors.isEmpty () ? Level.INFO : Level.WARNING,
                errors.isEmpty () ? "Se importaron todos los usuarios."
                        : String.format ("Hubo errores tratando de importar %d de los usuarios (con índice %s).",
                                errors.size (), ((Supplier <String>) ( () -> {
                                    StringBuilder str = new StringBuilder ();

                                    Integer errorsArray[] = errors.toArray (new Integer [0]);
                                    for (int i = 0; i < errorsArray.length; i++) {
                                        str.append (errorsArray [i]);

                                        if (i != errorsArray.length - 1)
                                            str.append (", ");
                                    }

                                    return str.toString ();
                                })).get ()));

        return list;
    }

    private static Usuario fromJSONObject (JSONObject o) {
        if (o == null)
            throw new NullPointerException ("No se puede obtener un usuario de un JSONObject nulo.");

        final Set <String> fields = Arrays.asList ("id", "nombre", "contraseña", "administrador", "sets", "historial")
                .stream ()
                .collect (Collectors.toSet ());

        String keys[] = o.keySet ().toArray (new String [0]);
        for (int i = 0; i < keys.length; i++)
            if (!fields.contains (keys [i]))
                throw new JSONException (String.format ("JSONObject inválido: clave %s desconocida.", keys [i]));

        if (!o.keySet ().contains ("administrador"))
            throw new JSONException (
                    "JSONObject inválido: el JSONObject debe contener una clave booleana llamada \"administrador\" para poder determinar el tipo de usuario a importar.");

        boolean isAdmin;
        try {
            isAdmin = o.getBoolean ("administrador");
        }

        catch (JSONException e) {
            throw new JSONException (
                    "JSONObject inválido: la clave \"administrador\" debe contener un valor booleano.");
        }

        UUID id = null;
        String nombre = "";
        String pass = "";
        List <SetPeliculas> sets = null;
        List <Entrada> historial = null;

        if (o.has ("id"))
            try {
                id = UUID.fromString (o.getString ("id"));
            }

            catch (IllegalArgumentException e) {
                Logger.getLogger (Usuario.class.getName ()).log (Level.WARNING,
                        "No se pudo obtener un ID válido del JSONObject.");
            }

        try {
            nombre = o.getString ("nombre");
        }

        catch (JSONException e) {
            Logger.getLogger (Usuario.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener un nombre de usuario válido del JSONObject.");
        }

        try {
            pass = o.getString ("contraseña");
        }

        catch (JSONException e) {
            Logger.getLogger (Usuario.class.getName ()).log (Level.WARNING,
                    "No se pudo obtener una contraseña válida del JSONObject.");
        }

        if (isAdmin)
            try {
                sets = SetPeliculas.fromJSON (o.getJSONArray ("sets").toString ());
            }

            catch (JSONException e) {
                Logger.getLogger (Usuario.class.getName ()).log (Level.WARNING,
                        "No se pudo obtener un conjunto de sets de películas válido del JSONObject.");
            }

        else
            try {
                historial = Entrada.fromJSON (o.getJSONArray ("historial").toString ());
            }

            catch (JSONException e) {
                Logger.getLogger (Usuario.class.getName ()).log (Level.WARNING,
                        "No se pudo obtener un historial válido del JSONObject");
            }

        return isAdmin ? new Administrador (id, nombre, pass, null)
                : new Espectador (id, nombre, pass, Espectador.getDefaultEdad (), null, historial, null);
    }

    public static String toJSON (Usuario usuario) throws NullPointerException {
        return Usuario.toJSON (Collections.singleton (usuario), false);
    }

    public static String toJSON (Usuario usuario, boolean extra) throws NullPointerException {
        return Usuario.toJSON (Collections.singleton (usuario), extra);
    }

    public static String toJSON (Collection <Usuario> usuarios) throws NullPointerException {
        return Usuario.toJSON (usuarios, false);
    }

    public static String toJSON (Collection <Usuario> usuarios, boolean extra) throws NullPointerException {
        if (usuarios == null)
            throw new NullPointerException ("No se puede convertir una coleción nula de sets de películas a JSON.");

        Usuario array[] = new TreeSet <Usuario> (usuarios).toArray (new Usuario [0]);
        StringBuilder str = new StringBuilder ();
        for (int i = 0; i < array.length; i++)
            str.append (
                    array [i] == null ? ""
                            : new StringBuilder ((extra && array [i].id != null)
                                    || (array [i].nombre != null && !array [i].nombre.equals (""))
                                    || (array [i].contrasena != null && !array [i].contrasena.equals (""))
                                            ? ("{\n" + (extra && array [i].id != null
                                                    ? "    \"id\" : " + array [i].id.toString ()
                                                    : "")
                                                    + "    \"administrador\" : "
                                                    + ((Boolean) (array [i] instanceof Administrador)).toString ()
                                                    + ",\n"
                                                    + (array [i].nombre != null && !array [i].nombre.equals ("")
                                                            ? "    \"nombre\" : " + "\"" + array [i].nombre + "\",\n"
                                                            : "")
                                                    + (array [i] instanceof Administrador
                                                            ? ("\"sets\" : " + SetPeliculas.toJSON (
                                                                    ((Administrador) array [i]).getSetsPeliculas ()))
                                                                            .replace ("\"sets\" :     [",
                                                                                    "\"sets\" : [")
                                                                            .indent (8)
                                                                    + ",\n"
                                                            : "")
                                                    + (array [i] instanceof Espectador
                                                            ? ("\"historial\" : " + Entrada
                                                                    .toJSON (((Espectador) array [i]).getHistorial ()))
                                                                            .replace ("\"historial\" :     [",
                                                                                    "\"historial\" : [")
                                                                            .indent (8)
                                                                    + ",\n"
                                                            : "")
                                                    + "}")
                                            : ""));

        for (int i = 0; (i = str.indexOf ("}{", i)) != -1;)
            str.insert (i + 1, ",\n    ");

        return "[\n" + str.toString ().indent (4).replace (",\n\n}", "\n}") + "]";
    }

    protected JSONObject toJSONObject () {
        return toJSONObject (false);
    }

    protected JSONObject toJSONObject (boolean extra) {
        JSONObject o = new JSONObject ().put ("nombre", this.nombre).put ("contraseña", this.contrasena);

        if (extra)
            o.put ("id", this.id.toString ());

        return o;
    }
}
