package internals;

public class Pair <X, Y> {
    public final X x;
    public final Y y;

    public Pair (X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals (Object o) {
        return o instanceof Pair && this.x.equals (((Pair) o).x) && this.y.equals (((Pair) o).y);
    }

    @Override
    public String toString () {
        return "Pair [Fila=" + x + ", Columna=" + y + "]";
    }
}
