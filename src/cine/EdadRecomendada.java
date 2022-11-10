package cine;

import java.util.Random;

public enum EdadRecomendada {
    TODOS     ((byte) 0),
    SIETE     ((byte) 1),
    DOCE      ((byte) 2),
    DIECISEIS ((byte) 3),
    DIECIOCHO ((byte) 4);

    private static final Random random = new Random ();
    private final byte value;

    private EdadRecomendada (byte value) {
        this.value = value;
    }

    public byte getValue () {
        return this.value;
    }

    public static EdadRecomendada random () {
        return EdadRecomendada.values () [random.nextInt (EdadRecomendada.values ().length)];
    }
}
