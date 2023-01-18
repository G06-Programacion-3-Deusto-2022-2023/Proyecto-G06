package test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Test;

import cine.Complemento;
import cine.Entrada;
import cine.Genero;
import cine.Pelicula;
import internals.bst.Filter;

public class BSTTest {
    @Test
    public void testPelicula () {
        assertTrue (Pelicula.tree (Pelicula.getDefault ()).getValuesSet ().containsAll (Pelicula.getDefault ()));
    }

    @Test
    public void testPelicula2 () {
        assertTrue (((Supplier <List <Pelicula>>) ( () -> {
            List <Pelicula> list = Pelicula.getDefault ().stream ().collect (Collectors.toList ());
            Collections.reverse (list);
            return list;
        })).get ().containsAll (Pelicula.tree (Pelicula.getDefault (),
                (Comparator <Pelicula>) ( (Pelicula a, Pelicula b) -> b.compareTo (a))).getValues ()));
    }

    @Test
    public void testPelicula3 () {
        assertTrue (Pelicula.getDefault ().stream ()
                .filter (p -> p.getGeneros ().contains (Genero.Nombre.ACCION))
                .collect (Collectors.toCollection (LinkedList::new)).containsAll (
                        Pelicula.tree (Pelicula.getDefault (),
                                (Filter <Pelicula>) ( (Pelicula p) -> p.getGeneros ()
                                        .contains (Genero.Nombre.ACCION)))
                                .getValues ()));
    }

    @Test
    public void testPelicula4 () {
        assertTrue (Pelicula.getDefault ().stream ()
                .filter ( (Pelicula p) -> p.getGeneros ().contains (Genero.Nombre.ACCION))
                .sorted ((Comparator <Pelicula>) ( (a, b) -> a.getFecha ().compareTo (b.getFecha ())))
                .collect (Collectors.toCollection (LinkedList::new)).containsAll (
                        Pelicula.tree (Pelicula.getDefault (),
                                (Comparator <Pelicula>) ( (a, b) -> a.getFecha ().compareTo (b.getFecha ())),
                                (Filter <Pelicula>) ( (Pelicula p) -> p.getGeneros ()
                                        .contains (Genero.Nombre.ACCION)))
                                .getValues ()));
    }

    @Test
    public void testEntrada () {
        assertEquals (5, Entrada.tree (((Supplier <List <Entrada>>) ( () -> {
            List <Entrada> list = new ArrayList <Entrada> ();
            for (int i = 0; i < 10; i++)
                list.add ((i & 1) == 1 ? ((Supplier <Entrada>) ( () -> {
                    Entrada entrada = new Entrada ();
                    entrada.setComplementos (
                            Collections.singletonMap (new Complemento (BigDecimal.TEN), BigInteger.valueOf (100)));
                    return entrada;
                })).get () : new Entrada ());
            return list;
        })).get (),
                (Filter <Entrada>) (e -> e.total ()
                        .compareTo (Entrada.getDefaultPrecio ().multiply (BigDecimal.valueOf (1.5D))) > 0))
                .getValues ().size ());
    }
}