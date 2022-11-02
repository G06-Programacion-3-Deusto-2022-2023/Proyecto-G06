package test;

import static org.junit.Assert.*;

import java.time.Duration;

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
        pelicula = new Pelicula ("mineons", Genero.ACCION, Genero.CIENCIA_FICCION);

        assertEquals ("mineons", pelicula.getNombre ());
        assertArrayEquals (new Genero [] { Genero.ACCION, Genero.CIENCIA_FICCION }, pelicula.getGeneros ().toArray ());
    }

    @Test
    public void constructorTest4 () {
        pelicula = new Pelicula (new Pelicula ("Padreando", "", 7.1, "José Mourinho", Duration.ofMinutes (127), EdadRecomendada.DOCE,
                Genero.DRAMA, Genero.ROMANCE));

        assertEquals ("Padreando", pelicula.getNombre ());
        assertEquals ("", pelicula.getRutaImagen ());
        assertEquals (7.1, pelicula.getValoracion (), 0.01);
        assertEquals ("José Mourinho", pelicula.getDirector ());
        assertEquals (127, pelicula.getDuracion ().toMinutes ());
        assertEquals (2, pelicula.getEdad ().getValue ());
        assertEquals (Genero.DRAMA.getValue () | Genero.ROMANCE.getValue (), pelicula.valorGeneros ());
    }
}
