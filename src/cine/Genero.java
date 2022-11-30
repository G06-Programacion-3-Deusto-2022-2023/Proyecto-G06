package cine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public interface Genero {
    short getValue ();

    public enum Nombre implements Genero {
        NADA ((short) 0, "-"),
        ACCION ((short) 1, "Acción"),
        CIENCIA_FICCION ((short) (1 << 1), "Ciencia ficción"),
        COMEDIA ((short) (1 << 2), "Comedia"),
        DOCUMENTAL ((short) (1 << 3), "Documental"),
        DRAMA ((short) (1 << 4), "Drama"),
        FANTASIA ((short) (1 << 5), "Fantasía"),
        MELODRAMA ((short) (1 << 6), "Melodrama"),
        MUSICAL ((short) (1 << 7), "Musical"),
        ROMANCE ((short) (1 << 8), "Romance"),
        SUSPENSE ((short) (1 << 9), "Suspense"),
        TERROR ((short) (1 << 10), "Terror");

        protected static Random random = new Random ();
        private final short value;
        private final String label;

        private Nombre (short value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public short getValue () {
            return this.value;
        }

        @Override
        public String toString () {
            return this.label;
        }

        public static short toValor (Collection <Genero.Nombre> generos) {
            if (generos == null || generos.size () == 0)
                return Genero.Nombre.NADA.getValue ();

            Genero.Nombre array[] = generos.toArray (new Genero.Nombre [0]);

            short ret = 0;
            for (int i = 0; i < generos.size (); ret |= array [i++].getValue ())
                ;

            return ret;
        }

        public static Set <Genero.Nombre> toGeneros (short generos) {
            // Me niego a hacer un for-each o usar un iterator
            Set <Genero.Nombre> set = new TreeSet <Genero.Nombre> ();

            for (int i = 0; i < Genero.Nombre.values ().length; i++)
                if ((generos & Genero.Nombre.values () [i].getValue ()) != 0)
                    set.add (Genero.Nombre.values () [i]);

            return set;
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

    static Set <Genero.Nombre> randomGeneros () {
        List <Genero.Nombre> generos = new ArrayList <Genero.Nombre> (Arrays.asList (Genero.Nombre.values ()));
        generos.remove (Genero.Nombre.NADA);
        Collections.shuffle (generos);
        generos = generos.subList (0, (int) Math.abs (Genero.Nombre.random.nextGaussian (3D, 1.5D)) % generos.size ());

        return new TreeSet <Genero.Nombre> (
                generos.isEmpty () ? Collections.singletonList (Genero.Nombre.NADA) : generos);
    }

    static Map <Genero.Nombre, Genero.Preferencia> randomPrefs () {
        TreeMap <Genero.Nombre, Genero.Preferencia> map = new TreeMap <Genero.Nombre, Genero.Preferencia> ();

        for (int i = 0, p; i < Genero.Nombre.values ().length; map.put (Genero.Nombre.values () [i++],
                (p = Genero.Nombre.random.nextInt (2)) == 0 ? Genero.Preferencia.NADA
                        : p == 1 ? Genero.Preferencia.BIEN : Genero.Preferencia.MAL))
            ;
        map.remove (Genero.Nombre.NADA);

        return map;
    }
}