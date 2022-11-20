package test;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Duration;
import java.time.Year;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;

import cine.EdadRecomendada;
import cine.Genero;
import cine.Pelicula;

public class PeliculaTest {
    Pelicula pelicula;

    @Test
    public void constructorTest1 () {
        pelicula = new Pelicula ();

        assertEquals (pelicula.getId ().toString (), pelicula.getNombre ());
    }

    @Test
    public void constructorTest2 () {
        pelicula = new Pelicula ("mineons");

        assertEquals ("mineons", pelicula.getNombre ());
    }

    @Test
    public void constructorTest3 () {
        pelicula = new Pelicula ("Versos Perversos", Genero.Nombre.ACCION, Genero.Nombre.CIENCIA_FICCION);

        assertEquals ("Versos Perversos", pelicula.getNombre ());
        assertArrayEquals (new Genero [] { Genero.Nombre.ACCION, Genero.Nombre.CIENCIA_FICCION },
                pelicula.getGeneros ().toArray ());
    }

    @Test
    public void constructorTest4 () {
        pelicula = new Pelicula (new Pelicula ("Padreando", "", 7.1, Year.of (2001), "José Mourinho",
                Duration.ofMinutes (127), EdadRecomendada.DOCE,
                Genero.Nombre.DRAMA, Genero.Nombre.ROMANCE));

        assertEquals ("Padreando", pelicula.getNombre ());
        assertEquals ("", pelicula.getRutaImagen ());
        assertEquals (7.1, pelicula.getValoracion (), 0.01);
        assertEquals (2001, pelicula.getFecha ().getValue ());
        assertEquals ("José Mourinho", pelicula.getDirector ());
        assertEquals (127, pelicula.getDuracion ().toMinutes ());
        assertEquals (2, pelicula.getEdad ().getValue ());
        assertEquals (Genero.Nombre.DRAMA.getValue () | Genero.Nombre.ROMANCE.getValue (),
                Genero.Nombre.toValor (pelicula.getGeneros ()));
        assertEquals (pelicula.getGeneros (), Genero.Nombre.toGeneros (Genero.Nombre.toValor (pelicula.getGeneros ())));
    }

    @Test
    public void defaultPeliculaTest () throws Throwable {
        pelicula = Pelicula.DEFAULT_PELICULAS.stream ().collect (Collectors.toMap (e -> e.getNombre (), e -> e))
                .get ("Torrente, el brazo tonto de la ley");

        assertTrue (new File (Pelicula.DEFAULT_MOVIE_IMAGE_FILES.get (0)).exists ());
        assertEquals (new UUID (0L, 0L), pelicula.getId ());
        assertEquals (Pelicula.DEFAULT_MOVIE_IMAGE_FILES.get (0), pelicula.getRutaImagen ());
        pelicula.finalize ();
        assertFalse (new File (Pelicula.DEFAULT_MOVIE_IMAGE_FILES.get (0)).exists ());
    }
}
