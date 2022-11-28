package internals.bst;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface Treeable <T extends Treeable <T> & Comparable <T>> {
    default BST <T> bst (Collection <T> values) {
        return bst (values, null, null);
    }

    default BST <T> bst (Collection <T> values, Comparator <T> comp) {
        return bst (values, comp, null);
    }

    default BST <T> bst (Collection <T> values, Filter <T> filter) {
        return bst (values, null, filter);
    }

    default BST <T> bst (Collection <T> values, Comparator <T> comp, Filter <T> filter) throws NullPointerException {
        if (values == null)
            throw new NullPointerException ("Cannot build a binary search tree from a null collection of values.");

        BST <T> bst = new BST <T> (null, comp, filter);

        List <T> list = values.stream ().collect (Collectors.toList ());
        for (int i = 0; i < values.size (); bst.insert (list.get (i++)))
            ;

        return bst;
    }
}
