package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;

import cine.Pelicula;
import cine.SetPeliculas;

public class SetPeliculasTest {
    SetPeliculas set;

    @Test
    public void test1 () {
        set = new SetPeliculas ("¿La de trabajar te la sabes?", Pelicula.DEFAULT_PELICULAS);

        assertEquals ("¿La de trabajar te la sabes?", set.getNombre ());
        assertEquals (Pelicula.DEFAULT_PELICULAS, set.getPeliculas ());
        assertEquals (Pelicula.getNombres (Pelicula.DEFAULT_PELICULAS.stream ().collect (Collectors.toList ())),
                set.getNombresPeliculas ());
    }

    @Test
    public void test2 () throws Throwable {
        set = SetPeliculas.random (Pelicula.DEFAULT_PELICULAS, 7);

        assertEquals (7, set.getPeliculas ().size ());
        assertTrue (Pelicula.DEFAULT_PELICULAS.containsAll (set.getPeliculas ()));

        List <Pelicula> list = set.getPeliculas ().stream ().collect (Collectors.toList ());
        assertEquals (7, list.size ());
        for (int i = 0; i < 7; assertFalse (new File (Pelicula.DEFAULT_MOVIE_IMAGE_FILES.get ((int) list.get (i++).getId ().getLeastSignificantBits ())).exists ())) {
            assertTrue (list.get (i).getId ().compareTo (new UUID (0L, 35L)) < 0);
            assertTrue (new File (Pelicula.DEFAULT_MOVIE_IMAGE_FILES.get ((int) list.get (i).getId ().getLeastSignificantBits ())).exists ());
            list.get (i).finalize ();
        }
    }
}
