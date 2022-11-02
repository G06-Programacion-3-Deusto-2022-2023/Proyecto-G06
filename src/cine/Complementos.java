package cine;

import java.math.BigDecimal;

public class Complementos {
    protected String nombre;
    protected BigDecimal precio;
    protected int descuento;

    public Complementos (String nombre, BigDecimal precio, int descuento) {
        super ();
        this.nombre = nombre;
        this.precio = AplicarDescuento (precio, descuento);
        this.descuento = descuento;
    }

    public Complementos () {
        super ();
        this.nombre = "";
        this.precio = new BigDecimal (0);
        this.descuento = 0;
    }

    public String getNombre () {
        return nombre;
    }

    public void setNombre (String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio () {
        return precio;
    }

    public void setPrecio (BigDecimal precio) {
        this.precio = AplicarDescuento (precio, descuento);
    }

    public int getDescuento () {
        return descuento;
    }

    public void setDescuento (int descuento) {
        if (descuento >= 0 && descuento < 100)
            this.descuento = descuento;
    }

    @Override
    public String toString () {
        return "Complementos [nombre=" + nombre + ", precio=" + precio + ", descuento=" + descuento + "]";
    }

    public BigDecimal AplicarDescuento (BigDecimal precio, int descuento) {
        if (descuento != 0) {
            precio = precio.subtract (precio.multiply (new BigDecimal (descuento / 100)));
        }
        return precio;
    }
}
