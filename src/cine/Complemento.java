package cine;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;

import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class Complemento implements Treeable <Complemento>, Comparable <Complemento> {
    private static final BigDecimal DEFAULT_PRECIO = BigDecimal.ONE;

    private UUID id;
    private String nombre;
    private BigDecimal precio;
    private int descuento;

    public Complemento () {
        this (null, null);
    }

    public Complemento (String nombre) {
        this (nombre, Complemento.DEFAULT_PRECIO);
    }

    public Complemento (BigDecimal precio) {
        this (null, precio);
    }

    public Complemento (String nombre, BigDecimal precio) {
        this (nombre, precio, 0);
    }

    public Complemento (String nombre, BigDecimal precio, int descuento) {
        this (UUID.randomUUID (), nombre, precio, descuento);
    }

    public Complemento (UUID id, String nombre, BigDecimal precio, int descuento) {
        super ();

        this.id = id;
        this.setNombre (nombre);
        this.setPrecio (precio);
        this.setDescuento (descuento);
    }

    public Complemento (Complemento complemento) {
        this (complemento.id, complemento.nombre, complemento.precio, complemento.descuento);
    }

    public UUID getId () {
        return this.id;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        this.nombre = nombre == null || nombre.equals ("") ? this.id.toString () : nombre;
    }

    public BigDecimal getPrecio () {
        return this.precio;
    }

    public void setPrecio (BigDecimal precio) {
            this.precio = (this.precio = precio) == null || precio.compareTo (BigDecimal.ZERO) <= 0 ? Complemento.DEFAULT_PRECIO : this.aplicarDescuento (this.descuento);
    }

    public int getDescuento () {
        return this.descuento;
    }

    public void setDescuento (int descuento) {
        if (descuento < 0 || descuento >= 100)
            return;

        this.descuento = descuento;
        this.setPrecio (this.precio);
    }

    public BigDecimal getDefaultPrecio () {
        return new BigDecimal (Complemento.DEFAULT_PRECIO.toString());
    }

    @Override
    public int compareTo (Complemento complemento) {
        if (complemento == null)
            return 1;

        if (this.nombre.equals (this.id.toString ()) && !complemento.nombre.equals (complemento.id.toString ()))
            return 1;

        if (!this.nombre.equals (this.id.toString ()) && complemento.nombre.equals (complemento.id.toString ()))
            return -1;

        if (this.nombre.equals (this.id.toString ()) && complemento.nombre.equals (complemento.id.toString ()))
            return this.id.compareTo (complemento.id);

        int comp;
        if ((comp = this.nombre.compareTo (complemento.nombre)) != 0)
            return comp;

        return this.id.compareTo (complemento.id);
    }

    @Override
    public String toString () {
        return "[nombre=" + nombre + ", precio=" + precio + ", descuento=" + descuento + "]";
    }

    protected BigDecimal aplicarDescuento (int descuento) {
        return this.precio.subtract (this.precio.multiply (new BigDecimal (descuento).scaleByPowerOfTen (-2)));
    }

    public static BST <Complemento> tree (Collection <Complemento> values) {
        return Complemento.tree (values, null, null);
    }

    public static BST <Complemento> tree (Collection <Complemento> values, Comparator <Complemento> comp) {
        return Complemento.tree (values, comp, null);
    }

    public static BST <Complemento> tree (Collection <Complemento> values, Filter <Complemento> filter) {
        return Complemento.tree (values, null, filter);
    }

    public static BST <Complemento> tree (Collection <Complemento> values, Comparator <Complemento> comp, Filter <Complemento> filter) {
        return new Complemento ().bst (values, comp, filter);
    }
}
