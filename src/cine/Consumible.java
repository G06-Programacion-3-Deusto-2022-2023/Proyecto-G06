package cine;

import java.math.BigDecimal;

public interface Consumible {
    public BigDecimal getPrecio ();
    public void setPrecio (BigDecimal precio);

    public static BigDecimal getMaxPrecio () {
        return new BigDecimal ("9".repeat (63) + ".99");
    }
}
