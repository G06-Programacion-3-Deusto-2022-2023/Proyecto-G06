package cine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public interface Genero {
    short getValue ();

    public enum Nombre implements Genero {
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

        private final short value;

        private Nombre (short value) {
            this.value = value;
        }

        @Override
        public short getValue () {
            return this.value;
        }

        public static short toValor (List <Genero.Nombre> generos) {
            short ret = Genero.Nombre.NADA.getValue ();

            if (generos != null)
                for (int i = 0; i < generos.size (); ret |= generos.get (i++).getValue ())
                    ;

            return ret;
        }

        public static List <Genero.Nombre> toGeneros (short generos) {
            ArrayList <Genero.Nombre> lista = new ArrayList <Genero.Nombre> ();

            for (int i = 0; i < Genero.Nombre.values ().length; i++)
                if ((generos & Genero.Nombre.values () [i].getValue ()) != 0)
                    lista.add (Genero.Nombre.values () [i]);

            return lista;
        }
    }

    public enum Preferencia implements Genero {
        NADA ((short) 0),
        BIEN ((short) +1),
        MAL ((short) -1);

        private final short value;

        private Preferencia (short value) {
            this.value = value;
        }

        @Override
        public short getValue () {
            return this.value;
        }
    }

    static List <Genero.Nombre> randomGeneros () {
        ArrayList <Genero.Nombre> generos = new ArrayList <Genero.Nombre> (Arrays.asList (Genero.Nombre.values ()));
        Collections.shuffle (generos);

        for (int i = 0, n = new Random ().nextInt (Genero.Nombre.values ().length); i < n; generos.remove (generos.size () - (i++ + 1)))
            ;

        return generos;
    }

    static Map <Genero.Nombre, Genero.Preferencia> randomPrefs () {
        TreeMap <Genero.Nombre, Genero.Preferencia> map = new TreeMap <Genero.Nombre, Genero.Preferencia> ();

        Random random = new Random ();
        for (int i = 0, p; i < Genero.Nombre.values ().length; map.put (Genero.Nombre.values () [i++],
                (p = new Random ().nextInt (2)) == 0 ? Genero.Preferencia.NADA
                        : p == 1 ? Genero.Preferencia.BIEN : Genero.Preferencia.MAL))
            ;

        return map;
    }
}