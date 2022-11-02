package cine;

import java.util.ArrayList;
import java.util.UUID;

public class Administrador extends Usuario {
    protected ArrayList <SetPeliculas> adminDpeliculas;

    public Administrador (String nombre, String contrasena,
            ArrayList <SetPeliculas> adminDpeliculas) {
        super (nombre, contrasena);
        this.adminDpeliculas = adminDpeliculas;
    }

    public Administrador (String nombre, String contrasena, UUID id) {
        super (nombre, contrasena);
        this.adminDpeliculas = new ArrayList <SetPeliculas> ();
    }

    public ArrayList <SetPeliculas> getAdminDpeliculas () {
        return adminDpeliculas;
    }

    public void setAdminDpeliculas (ArrayList <SetPeliculas> adminDpeliculas) {
        this.adminDpeliculas = adminDpeliculas;
    }

    @Override
    public String toString () {
        return "Administrador [llave=" + ", adminDpeliculas=" + adminDpeliculas + "]";
    }
}
