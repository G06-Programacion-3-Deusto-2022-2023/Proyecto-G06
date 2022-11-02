package cine;

public enum EdadRecomendada {
    TODOS     ((byte) 0),
    SIETE     ((byte) 1),
    DOCE      ((byte) 2),
    DIECISEIS ((byte) 3),
    DIECIOCHO ((byte) 4);

    protected final byte value;

    EdadRecomendada (byte value) {
        this.value = value;
    }
}
