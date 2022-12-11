package internals.bst;

/*
 * Realmente, podría usar Predicate para esto
*/
public interface Filter <T> {
    boolean filter (T o);
}
