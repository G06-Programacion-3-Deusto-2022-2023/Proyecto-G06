package test;

import java.util.function.Supplier;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import cine.Genero;
import cine.Pelicula;
import internals.bst.BST;
import internals.bst.Filter;

public class BSTTest {
    @Test
    public void testPelicula () {
        assertEquals (BST.fromValues (Pelicula.getDefault ()).getValuesSet (), Pelicula.getDefault ());
    }

    @Test
    public void testPelicula2 () {
        assertEquals (((Supplier <List <Pelicula>>) ( () -> {
            List <Pelicula> list = Pelicula.getDefault ().stream ().collect (Collectors.toList ());
            Collections.reverse (list);
            return list;
        })).get (), BST.fromValues (Pelicula.getDefault (),
                (Comparator <Pelicula>) ( (Pelicula a, Pelicula b) -> b.compareTo (a))).getValues ());
    }

    @Test
    public void testPelicula3 () {
        assertEquals (
                Pelicula.getDefault ().stream ()
                        .filter (p -> p.getGeneros ().contains (Genero.Nombre.ACCION))
                        .collect (Collectors.toCollection (LinkedList::new)),
                BST.fromValues (Pelicula.getDefault (),
                        (Filter <Pelicula>) ( (Pelicula p) -> p.getGeneros ().contains (Genero.Nombre.ACCION)))
                        .getValues ());
    }

    @Test
    public void testPelicula4 () {
        assertEquals (
                Pelicula.getDefault ().stream ()
                        .filter ( (Pelicula p) -> p.getGeneros ().contains (Genero.Nombre.ACCION))
                        .sorted ((Comparator <Pelicula>) ( (a, b) -> a.getFecha ().compareTo (b.getFecha ())))
                        .collect (Collectors.toCollection (LinkedList::new)),
                BST.fromValues (Pelicula.getDefault (),
                        (Comparator <Pelicula>) ( (a, b) -> a.getFecha ().compareTo (b.getFecha ())),
                        (Filter <Pelicula>) ( (Pelicula p) -> p.getGeneros ().contains (Genero.Nombre.ACCION)))
                        .getValues ());
    }
}