package internals.bst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import cine.Pelicula;

public class BST <T extends Comparable <T>> {
    private T value;
    private Comparator <T> comp;
    private Filter <T> filter;
    private BST <T> left;
    private BST <T> right;

    private BST (T value, Comparator <T> comp, Filter <T> filter) {
        super ();

        this.setFilter (filter);
        this.setComp (comp);
        this.setValue (value);
    }

    public Filter <T> getFilter () {
        return this.filter;
    }

    private void setFilter (Filter <T> filter) {
        this.filter = filter == null ? ((Filter <T>) ( (T e) -> true)) : filter;
    }

    public Comparator <T> getComp () {
        return this.comp;
    }

    private void setComp (Comparator <T> comp) throws UnsupportedOperationException {
        this.comp = comp == null ? (Comparator <T>) ( (T a, T b) -> (a.compareTo (b))) : comp;
    }

    private boolean setValue (T value) {
        if (value == null || !this.filter.filter (value))
            return false;

        this.value = value;

        return true;
    }

    private boolean isEmpty () {
        return this.value == null && this.left == null && this.right == null;
    }

    private boolean insert (T value) {
        if (this.value == null)
            return this.setValue (value);

        if (this.value.equals (value))
            return false;

        if (this.comp.compare (value, this.value) < 0) {
            if (this.left == null)
                this.left = new BST <T> (null, this.comp, this.filter);

            return this.left.insert (value);
        }

        if (this.right == null)
            this.right = new BST <T> (null, this.comp, this.filter);

        return this.right.insert (value);
    }

    private void inorder (Collection <T> list) {
        if (this.isEmpty ())
            return;

        if (this.left != null)
            this.left.inorder (list);

        list.add (this.value);

        if (this.right != null)
            this.right.inorder (list);
    }

    public static BST <Pelicula> fromValues (Collection <Pelicula> values) {
        return fromValues (values, null, null);
    }

    public static BST <Pelicula> fromValues (Collection <Pelicula> values, Comparator <Pelicula> comp) {
        return fromValues (values, comp, null);
    }

    public static BST <Pelicula> fromValues (Collection <Pelicula> values, Filter <Pelicula> filter) {
        return BST.fromValues (values, null, filter);
    }

    public static BST <Pelicula> fromValues (Collection <Pelicula> values, Comparator <Pelicula> comp,
            Filter <Pelicula> filter)
            throws NullPointerException {
        if (values == null)
            throw new NullPointerException ("Cannot build a binary search tree from a null collection of values.");

        BST <Pelicula> bst = new BST <Pelicula> (null, comp, filter);

        List <Pelicula> list = values.stream ().collect (Collectors.toList ());
        for (int i = 0; i < values.size (); bst.insert (list.get (i++)))
            ;

        return bst;
    }

    public List <T> getValues () {
        return this.getValuesList ();
    }

    public SortedSet <T> getValuesSet () {
        SortedSet <T> set = new TreeSet <T> (this.comp);
        this.inorder (set);

        return set;
    }

    public List <T> getValuesList () {
        List <T> list = new ArrayList <T> ();
        this.inorder (list);

        return list;
    }

    public Collection <T> getValuesAs (Collection <T> collection) {
        collection.clear ();
        collection.addAll (this.getValuesList ());

        return collection;
    }
}
