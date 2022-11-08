package cine;

public enum EdadRecomendada {
    TODOS     ((byte) 0),
    SIETE     ((byte) 1),
    DOCE      ((byte) 2),
    DIECISEIS ((byte) 3),
    DIECIOCHO ((byte) 4);

    private final byte value;

    private EdadRecomendada (byte value) {
        this.value = value;
    }

    public byte getValue () {
        return this.value;
    }
}
