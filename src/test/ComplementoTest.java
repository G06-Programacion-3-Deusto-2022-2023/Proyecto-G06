package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import cine.Complemento;

public class ComplementoTest {
    Complemento complemento;

    @Test
    public void constructorTest1 () {
        complemento = new Complemento ();

        assertEquals (complemento.getId ().toString (), complemento.getNombre ());
    }

    @Test
    public void constructorTest2 () {
        complemento = new Complemento ("Palomitas");

        assertEquals ("Palomitas", complemento.getNombre ());
    }

    @Test
    public void constructorTest3 () {
        complemento = new Complemento ("Refresco de naranja", new BigDecimal ("2.5"));

        assertEquals ("Refresco de naranja", complemento.getNombre ());
        assertEquals (2.5, complemento.getPrecio ().doubleValue (), 0.001);
    }

    @Test
    public void constructorTest4 () {
        complemento = new Complemento ("Cubo gigante de palomitas", BigDecimal.valueOf (5.5), 25);

        assertEquals ("Cubo gigante de palomitas", complemento.getNombre ());
        assertEquals (5.5 - 5.5 * 0.25, complemento.aplicarDescuento ().doubleValue (), 0.01);
    }
}
