package test;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Duration;
import java.time.Year;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.junit.Test;

import cine.EdadRecomendada;
import cine.Genero;
import cine.Pelicula;
import cine.SetPeliculas;

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
    public void defaultPeliculaTest () {
        assertTrue (((BooleanSupplier) ( () -> {
            Pelicula array[] = Pelicula.getDefault ().toArray (new Pelicula [0]);

            for (; !Pelicula.defaultImagesDownloaded();)
                ;

            for (int i = 0; i < array.length;)
                if (!new File (array [i++].getRutaImagen ()).exists ())
                    return false;

            return true;
        })).getAsBoolean ());
    }

    @Test
    public void defaultPeliculaTest2 () throws Throwable {
        pelicula = Pelicula.getDefault ().stream ().collect (Collectors.toMap (Pelicula::getNombre, e -> e))
                .get ("Torrente, el brazo tonto de la ley");
        assertTrue (Pelicula.getDefault ().contains (pelicula));

        String nombre = new String (pelicula.getNombre ());
        pelicula.setNombre ("Luis Padrique");
        assertEquals (nombre, pelicula.getNombre ());

        assertTrue (new File (pelicula.getRutaImagen ()).exists ());
        assertEquals (new UUID (0L, 0L), pelicula.getId ());
        assertEquals (pelicula.getRutaImagen (), pelicula.getRutaImagen ());
        pelicula.finalize ();
        assertFalse (new File (pelicula.getRutaImagen ()).exists ());
    }

    @Test
    public void defaultPeliculaTest3 () {
        pelicula = Pelicula.getDefault ().stream ().collect (Collectors.toMap (Pelicula::getNombre, e -> e))
                .get ("A todo gas: Tokyo Race");
        assertTrue (Pelicula.getDefault ().contains (pelicula));

        SetPeliculas set;
        pelicula.addSet (set = SetPeliculas.random (0));
        assertTrue (set.contains (pelicula));
        set.remove (pelicula);
        assertFalse (pelicula.isInSet (set));
    }
}
