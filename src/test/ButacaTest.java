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
        double probabilidad = Butaca.calcularProbabilidad (espectador, Pelicula.random ());
        boolean ocupada = Math.random () < probabilidad;

        butaca = new Butaca (espectador, ocupada);

        assertEquals (butaca.getEspectador (), ocupada ? espectador : null);
    }
}
