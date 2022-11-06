package cine;

import java.util.ArrayList;
import java.util.List;

public enum Genero {
    NADA ((short) 0),
    ACCION ((short) 1),
    CIENCIA_FICCION ((short) (1 << 1)),
    COMEDIA ((short) (1 << 2)),
    DRAMA ((short) (1 << 3)),
    FANTASIA ((short) (1 << 4)),
    MELODRAMA ((short) (1 << 5)),
    MUSICAL ((short) (1 << 6)),
    ROMANCE ((short) (1 << 7)),
    SUSPENSE ((short) (1 << 8)),
    TERROR ((short) (1 << 9)),
    DOCUMENTAL ((short) (1 << 10));

    protected final short value;

    private Genero (short value) {
        this.value = value;
    }

    public short getValue () {
        return this.value;
    }

    public static short toValor (List <Genero> generos) {
        short ret = Genero.NADA.getValue ();
        for (int i = 0; i < generos.size (); ret |= generos.get (i++).getValue ())
            ;

        return ret;
    }

    public static List <Genero> toGeneros (short generos) {
        ArrayList <Genero> lista = new ArrayList <Genero> ();

        for (int i = 0; i < Genero.values ().length; i++)
            if ((generos & Genero.values () [i].getValue ()) != 0)
                lista.add (Genero.values () [i]);

        return lista;
    }
}
