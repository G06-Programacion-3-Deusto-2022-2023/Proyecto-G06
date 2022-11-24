package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import cine.Pelicula;
import cine.SetPeliculas;

public class SetPeliculasTest {
    SetPeliculas set;

    @Test
    public void test1 () {
        set = new SetPeliculas ("¿La de trabajar te la sabes?", Pelicula.getDefault ());

        assertEquals ("¿La de trabajar te la sabes?", set.getNombre ());
        assertEquals (Pelicula.getDefault (), set.getPeliculas ());
        assertEquals (Pelicula.getNombres (Pelicula.getDefault ().stream ().collect (Collectors.toList ())),
                set.getNombresPeliculas ());
    }

    @Test
    public void test2 () {
        set = SetPeliculas.random (Pelicula.getDefault (), 7);

        assertEquals (7, set.getPeliculas ().size ());
        assertTrue (Pelicula.getDefault ().containsAll (set.getPeliculas ()));

        List <Pelicula> list = set.getPeliculas ().stream ().collect (Collectors.toList ());
        assertEquals (7, list.size ());
        for (int i = 0; i < 7; assertTrue (new File (list.get (i++).getRutaImagen ()).exists ()))
            assertTrue (list.get (i).isDefault ());
    }
}
