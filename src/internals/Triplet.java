package internals;

public class Triplet <X, Y, Z> {
    public final X x;
    public final Y y;
    public final Z z;

    public Triplet (X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals (Object o) {
        return o instanceof Triplet && this.x.equals (((Triplet) o).x) && this.y.equals (((Triplet) o).y)
                && this.z.equals (((Triplet) o).z);
    }
}
