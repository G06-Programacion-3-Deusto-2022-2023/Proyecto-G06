package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import cine.Administrador;
import cine.Espectador;

public class UsuarioTest {
    Espectador espectador;
    Administrador administrador;

    @Test
    public void constructorTest1 () {
        espectador = new Espectador ();
        administrador = new Administrador ();

        assertEquals (espectador.getId ().toString (), espectador.getNombre ());
        assertEquals (administrador.getId ().toString (), administrador.getNombre ());
    }

    @Test
    public void constructorTest2 () {
        espectador = new Espectador ("ElBokeron");
        administrador = new Administrador ("Su Morenito 19");

        assertEquals ("ElBokeron", espectador.getNombre ());
        assertEquals ("Su Morenito 19", administrador.getNombre ());
    }

    @Test
    public void constructorTest3 () {
        espectador = new Espectador ("Josemicod5", "_buenasnochessenoritas");
        administrador = new Administrador ("Alexelcapso", "pagarCarreteras!");

        assertEquals ("Josemicod5", espectador.getNombre ());
        assertEquals ("Alexelcapso", administrador.getNombre ());
        assertEquals ("_buenasnochessenoritas", espectador.getContrasena ());
        assertEquals ("pagarCarreteras!", administrador.getContrasena ());
    }
}