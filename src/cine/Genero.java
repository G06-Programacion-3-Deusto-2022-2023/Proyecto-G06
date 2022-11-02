package cine;

public enum Genero {
    NADA            ((short) 0),
    ACCION          ((short) 1),
    CIENCIA_FICCION ((short) (1 << 1)),
    COMEDIA         ((short) (1 << 2)),
    DRAMA           ((short) (1 << 3)),
    FANTASIA        ((short) (1 << 4)),
    MELODRAMA       ((short) (1 << 5)),
    MUSICAL         ((short) (1 << 6)),
    ROMANCE         ((short) (1 << 7)),
    SUSPENSE        ((short) (1 << 8)),
    TERROR          ((short) (1 << 9)),
    DOCUMENTAL      ((short) (1 << 10));

    protected final short value;

    private Genero (short value) {
        this.value = value;
    }

    private short getValue () {
        return this.value;
    }
}
