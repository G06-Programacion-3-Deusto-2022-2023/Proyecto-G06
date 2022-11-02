package cine;

import java.math.BigDecimal;
import java.util.UUID;

public class Complemento {
    protected UUID id;
    protected String nombre;
    protected BigDecimal precio;
    protected int descuento;

    public Complemento () {
        this ("");
    }

    public Complemento (String nombre) {
        this (nombre, BigDecimal.ONE);
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
            this.precio = (this.precio = precio) == null || precio.compareTo (BigDecimal.ZERO) <= 0 ? BigDecimal.ONE : this.aplicarDescuento (this.descuento);
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

    @Override
    public String toString () {
        return "[nombre=" + nombre + ", precio=" + precio + ", descuento=" + descuento + "]";
    }

    protected BigDecimal aplicarDescuento (int descuento) {
        return this.precio.subtract (this.precio.multiply (new BigDecimal (descuento).divide (new BigDecimal (100))));
    }
}
