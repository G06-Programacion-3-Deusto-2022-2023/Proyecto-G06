package cine;

import java.util.Random;

public enum EdadRecomendada {
    TODOS ((byte) 0, "Todas las edades"),
    SIETE ((byte) 1, "+7"),
    DOCE ((byte) 2, "+12"),
    DIECISEIS ((byte) 3, "+16"),
    DIECIOCHO ((byte) 4, "+18");

    private static final Random random = new Random ();
    private final byte value;
    private final String label;

    private EdadRecomendada (byte value, String label) {
        this.value = value;
        this.label = label;
    }

    public byte getValue () {
        return this.value;
    }

    public static EdadRecomendada fromValue (byte value) {
        if (value < 0 || value > 4)
            return null;

        return new EdadRecomendada [] { EdadRecomendada.TODOS, EdadRecomendada.SIETE, EdadRecomendada.DOCE,
                EdadRecomendada.DIECISEIS, EdadRecomendada.DIECIOCHO } [value];
    }

    @Override
    public String toString () {
        return this.label;
    }

    public static EdadRecomendada random () {
        return EdadRecomendada.values () [random.nextInt (EdadRecomendada.values ().length)];
    }
}
