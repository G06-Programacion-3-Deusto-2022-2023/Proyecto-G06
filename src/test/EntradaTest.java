package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import cine.Complemento;
import cine.Entrada;

public class EntradaTest {
    Entrada entrada;

    @Test
    public void constructorTest1 () {
        entrada = new Entrada ();

        assertEquals (21, entrada.getFecha ().get (Calendar.DAY_OF_MONTH));
        assertEquals (2, entrada.getFecha ().get (Calendar.MONTH));
        assertEquals (2003, entrada.getFecha ().get (Calendar.YEAR));
        assertEquals (Entrada.getDefaultPrecio ().doubleValue (), entrada.getPrecio ().doubleValue (), 0.001);
    }

    @Test
    public void constructorTest2 () {
        entrada = new Entrada ();
        entrada.setComplementos (new HashMap <Complemento, BigInteger> (Collections
                .singletonMap (new Complemento (BigDecimal.valueOf (5)), BigInteger.valueOf (2))));

        assertEquals (Entrada.getDefaultPrecio ().doubleValue () + 5 * 2, entrada.total ().doubleValue (), 0.001);
    }
}
