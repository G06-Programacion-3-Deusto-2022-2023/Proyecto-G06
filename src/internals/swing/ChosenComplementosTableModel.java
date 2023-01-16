package internals.swing;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import cine.Complemento;
import graphical.ComplementosWindow;

public class ChosenComplementosTableModel extends DefaultTableModel {
    private ConcurrentMap <Complemento, BigInteger> c;
    private JLabel l;

    public ChosenComplementosTableModel (ConcurrentMap <Complemento, BigInteger> c) {
        super ();

        this.c = c;
        this.l = new JLabel ();
        this.update ();
    }

    public ConcurrentMap <Complemento, BigInteger> getComplementos () {
        return this.c;
    }

    public void setComplementos (ConcurrentMap <Complemento, BigInteger> c) {
        if (c == null)
            c = new ConcurrentHashMap <Complemento, BigInteger> ();

        this.c = c;

        this.setDataVector (
                this.c.keySet ().stream ().sorted ()
                        .map (e -> Arrays.asList (e, this.c.get (e)).stream ()
                                .collect (Collectors.toCollection (Vector::new)))
                        .collect (Collectors.toCollection (Vector::new)),
                Arrays.asList ("Nombre", "Unidades").stream ().collect (Collectors.toCollection (Vector::new)));
    }

    public JLabel getLabel () {
        return this.l;
    }

    public void add (Complemento c) {
        this.c.merge (c, BigInteger.ONE, (a, b) -> a.add (b));

        if (this.c.get (c).equals (BigInteger.ONE)) {
            this.addRow (new Object [] { c, this.c.get (c) });

            return;
        }

        this.setValueAt (this.c.get (c).add (BigInteger.ONE),
                this.dataVector.stream ().map (e -> e.get (0)).toList ().indexOf (c), 1);
    }

    public void update () {
        this.setComplementos (this.c);
        this.l.setText (String.format ("<html><b>Total:</b> %s €</html>", Complemento.sum (this.c)));
    }

    public void clear () {
        this.c.clear ();
        this.update ();
    }

    @Override
    public int getColumnCount () {
        return 2;
    }

    @Override
    public int getRowCount () {
        return this.dataVector.size ();
    }

    @Override
    public String getColumnName (int col) {
        return col == 0 ? "Nombre" : "Unidades";
    }

    @Override
    public Object getValueAt (int row, int col) {
        return this.dataVector.isEmpty () ? (col == 0 ? new Complemento ("-") : BigInteger.ZERO)
                : this.dataVector.get (row).get (col);
    }

    @Override
    public void setValueAt (Object aValue, int row, int col) {
        if (col != 1)
            return;

        BigInteger bi;
        try {
            if ((bi = new BigInteger (aValue.toString ())).signum () == 0)
                throw new ArithmeticException ();
        }

        catch (NumberFormatException | ArithmeticException e) {
            Logger.getLogger (ComplementosWindow.class.getName ()).log (Level.WARNING,
                    "El número de unidades debe ser un entero positivo.");

            return;
        }

        this.c.replace ((Complemento) this.dataVector.get (row).get (0), bi);
        this.update ();
    }

    @Override
    public Class <?> getColumnClass (int col) {
        return col == 0 ? Complemento.class : BigInteger.class;
    }

    @Override
    public boolean isCellEditable (int row, int col) {
        return !this.dataVector.isEmpty () && col == 1;
    }

    @Override
    public void addRow (Object rowData[]) {
        super.addRow (rowData);

        this.update ();
    }

    @Override
    public void removeRow (int row) {
        if (row == -1)
            return;

        c.remove (this.getValueAt (row, 0));

        super.removeRow (row);
    }
}
