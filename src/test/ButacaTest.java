package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cine.Butaca;
import cine.Espectador;
import cine.Pelicula;

public class ButacaTest {
    Butaca butaca;

    @Test
    public void test1 () {
        butaca = new Butaca ();

        assertEquals (null, butaca.getEspectador ());
    }

    @Test
    public void test2 () {
        Espectador espectador = Espectador.random ();
        boolean ocupada = Butaca.determinarOcupacion (espectador, Pelicula.random ());

        assertEquals ((butaca = new Butaca (espectador, ocupada)).getEspectador (), ocupada ? espectador : null);
    }
}
