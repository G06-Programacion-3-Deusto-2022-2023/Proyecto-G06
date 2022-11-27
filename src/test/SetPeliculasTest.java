package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.junit.Test;

import cine.Pelicula;
import cine.SetPeliculas;

public class SetPeliculasTest {
    SetPeliculas set;

    @Test
    public void test1 () {
        List <Pelicula> list;
        set = new SetPeliculas ("¿La de trabajar te la sabes?", list = ((Supplier <List <Pelicula>>) ( () -> {
            List <Pelicula> anonlist = new ArrayList <Pelicula> ();
            for (int i = 0; i < 10; i++)
                anonlist.add (Pelicula.random ());
            return anonlist;
        })).get ());

        assertEquals ("¿La de trabajar te la sabes?", set.getNombre ());
        assertEquals (new TreeSet <Pelicula> (list), set.getPeliculas ());
        assertEquals (Pelicula.getNombres (list), set.getNombresPeliculas ());
    }

    @Test
    public void test2 () {
        set = SetPeliculas.random (Pelicula.getDefault (), 7);

        assertEquals (7, set.getPeliculas ().size ());
        assertTrue (Pelicula.getDefault ().containsAll (set.getPeliculas ()));

        List <Pelicula> list = set.getPeliculas ().stream ().collect (Collectors.toList ());
        assertEquals (7, list.size ());
        for (int i = 0; list.isEmpty (); list.remove (0)) {
            assertTrue (list.get (0).isDefault ());
            assertTrue (new File (list.get (0).getRutaImagen ()).exists ());
        }
    }

    @Test
    public void test3 () {
        set = new SetPeliculas (Pelicula.getDefault (0L, 12L, 24L));
        assertEquals (set.getId ().toString (), set.getNombre ());
        assertTrue (((BooleanSupplier) ( () -> {
            Pelicula array[] = Pelicula.getDefault (0L, 12L, 24L).toArray (new Pelicula [0]);
            for (int i = 0; i < array.length;)
                if (!array [i++].getSets ().contains (set))
                    return false;

            return true;
        })).getAsBoolean ());
        assertEquals (
                Pelicula.getNombres (Pelicula.getDefault ().stream ()
                        .filter (p -> Arrays.asList (new Long [] { 0L, 12L, 24L })
                                .contains (p.getId ().getLeastSignificantBits ()))
                        .collect (Collectors.toList ())),
                set.getNombresPeliculas ());
    }

    @Test
    public void test4 () {

    }
}
