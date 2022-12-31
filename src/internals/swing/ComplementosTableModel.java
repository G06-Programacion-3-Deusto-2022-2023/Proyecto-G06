package internals.swing;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.swing.table.DefaultTableModel;

import VentanaGrafica.LoadingWindow;
import cine.Complemento;
import internals.GestorBD;
import internals.Pair;

public class ComplementosTableModel extends DefaultTableModel {
    public enum OrderCriteria {
        UNORDERED ((byte) 0),
        NAME ((byte) 1),
        PRICE ((byte) 2),
        DISCOUNT ((byte) 3);

        private byte value;

        private OrderCriteria (byte value) {
            this.value = value;
        }

        public byte getValue () {
            return this.value;
        }
    }

    private GestorBD db;
    private List <Complemento> values;
    private Pair <OrderCriteria, Boolean> order;
    private boolean orders[];

    public ComplementosTableModel (GestorBD db) {
        super ();

        new LoadingWindow ( () -> {
            this.db = db;

            this.orders = new boolean [3];
            this.orderBy (this.order = new Pair <OrderCriteria, Boolean> (OrderCriteria.UNORDERED, false));
        });
    }

    public Complemento get (int row) {
        return new Complemento (this.values.get (row).getId (),
                (String) this.dataVector.get (row).get (0),
                new BigDecimal (((String) this.dataVector.get (row).get (1)).replace (",", ".").replace (" ", "")
                        .replace ("€", "")),
                Integer.parseInt (((String) this.dataVector.get (row).get (2)).replace (" %", "")));
    }

    public boolean [] getOrders () {
        return new boolean [] { this.orders [0], this.orders [1], this.orders [2] };
    }

    public void update () {
        this.orderBy (this.order);
    }

    public void orderBy (OrderCriteria criteria, boolean desc) {
        this.orderBy (new Pair <OrderCriteria, Boolean> (criteria, desc));
    }

    public void orderBy (Pair <OrderCriteria, Boolean> order) {
        this.order = order;

        List <Complemento> l = this.db.obtenerDatosComplementos ();
        this.setDataVector ((this.values = Complemento
                .tree (l, (Comparator <Complemento>) new Comparator [] {
                        (Comparator <Complemento>) ( (Complemento x,
                                Complemento y) -> l.indexOf (x)
                                        - l.indexOf (y)),
                        (Comparator <Complemento>) ( (Complemento x, Complemento y) -> x.compareTo (y)),
                        (Comparator <Complemento>) ( (Complemento x, Complemento y) -> x.getPrecio ()
                                .compareTo (y.getPrecio ())),
                        (Comparator <Complemento>) ( (Complemento x, Complemento y) -> ((Integer) x.getDescuento ())
                                .compareTo ((Integer) y.getDescuento ())),
                        (Comparator <Complemento>) ( (Complemento x, Complemento y) -> y.compareTo (x)),
                        (Comparator <Complemento>) ( (Complemento x, Complemento y) -> y.getPrecio ()
                                .compareTo (x.getPrecio ())),
                        (Comparator <Complemento>) ( (Complemento x, Complemento y) -> ((Integer) y.getDescuento ())
                                .compareTo ((Integer) x.getDescuento ()))
                } [this.order.x.getValue ()
                        + ((this.order.x != OrderCriteria.UNORDERED
                                && (this.orders [this.order.x.getValue () - 1] = this.order.y)) ? 3 : 0)])
                .getValues ())
                        .stream ().map (
                                e -> new Object [] { e.getNombre (),
                                        String.format ("%.2f €", e.getPrecio ().doubleValue ()),
                                        String.format ("%d %%", e.getDescuento ()) })
                        .collect (Collectors.toList ()).toArray (new Object [0] [0]),
                new String [] { "Nombre", "Precio", "Descuento" });
    }

    @Override
    public int getColumnCount () {
        return 3;
    }

    @Override
    public int getRowCount () {
        return this.dataVector.size ();
    }

    @Override
    public String getColumnName (int col) {
        return new String [] { "Nombre", "Precio", "Descuento" } [col];
    }

    @Override
    public Object getValueAt (int row, int col) {
        if (this.getDataVector ().isEmpty ())
            return new Object [] { "-", null, 0 } [col];

        return new Object [] { this.dataVector.get (row).get (0),
                this.dataVector.get (row).get (1),
                this.dataVector.get (row).get (2) } [col];
    }

    @Override
    public void setValueAt (Object aValue, int row, int col) {
        if (col == 0)
            try {
                if (((String) aValue).replace (" ", "").equals (""))
                    throw new NamingException ("El nombre del complemento no puede estar vacío");

                if (this.values.stream ()
                        .anyMatch (e -> e.getNombre ().equals ((String) aValue)))
                    throw new NameAlreadyBoundException ("El nombre especificado ya se encuentra en la base de datos");

                this.dataVector.get (row).set (0, (String) aValue);
            }

            catch (NameAlreadyBoundException e) {
                Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                        String.format ("Nombre duplicado: ya hay un complemento de nombre %s en la base de datos.",
                                (String) aValue));

                return;
            }

            catch (NamingException e) {
                Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                        "No se puede asignar un nombre vacío a un complemento.");

                return;
            }

        else if (col == 1)
            try {
                BigDecimal p;
                if ((p = new BigDecimal (((String) aValue).replace (",", ".").replace (" ", "").replace ("€", "")))
                        .signum () != 1)
                    throw new ArithmeticException ("El precio debe ser un número positivo.");

                this.dataVector.get (row).set (1,
                        String.format ("%.2f €", p.doubleValue ()));
            }

            catch (NumberFormatException e) {
                Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                        String.format (
                                "Formato incorrecto: no pudo cambiarse el precio porque %s no es un número válido.",
                                ((String) aValue).replace (" ", "").replace ("€", "")));

                return;
            }

            catch (ArithmeticException e) {
                Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                        String.format (
                                "Precio no válido: el precio especificado, %s €, no es positivo.",
                                (String) aValue));

                return;
            }

        else
            try {
                int d;
                if ((d = Integer.valueOf (((String) aValue).replace (" %", ""))) < 0 || d >= 100)
                    throw new ArithmeticException (String.format (
                            "Descuento no válido: el descuento especificado, %d %%, es %s.", d,
                            d >= 100 ? "mayor de 100" : "negativo"));

                this.dataVector.get (row).set (2, String.format ("%d %%", d));
            }

            catch (NumberFormatException e) {
                Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                        String.format (
                                "Formato incorrecto: no pudo cambiarse el descuento porque %s no es un número entero válido.",
                                ((String) aValue).replace (" %", "")));

                return;
            }

            catch (ArithmeticException e) {
                Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                        String.format (
                                "Descuento no válido: el descuento especificado, %s %%, es negativo.",
                                (String) aValue));

                return;
            }

        db.update (this.values.get (row));
    }

    @Override
    public Class getColumnClass (int col) {
        return String.class;
    }

    @Override
    public boolean isCellEditable (int row, int col) {
        return !(this.dataVector.isEmpty ()
                || Complemento.isDefault (this.values.get (row).getId ()));
    }

    @Override
    public void addRow (Object [] rowData) {
        try {
            if (rowData.length != 3)
                throw new UnsupportedOperationException (
                        "Debe pasarse un vector de tamaño 3 al método ComplementosTableModel.addRow().");
        }

        catch (UnsupportedOperationException e) {
            Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING, String.format (
                    "Debe pasarse un vector de tamaño 3 al método ComplementosTableModel.addRow(), no de tamaño %d.",
                    rowData.length));

            return;
        }

        try {
            if (((String) rowData [0]).replace (" ", "").equals (""))
                throw new NamingException ("El nombre del complemento no puede estar vacío");

            if (this.values.stream ()
                    .anyMatch (e -> e.getNombre ().equals ((String) rowData [0])))
                throw new NameAlreadyBoundException ("El nombre especificado ya se encuentra en la base de datos");
        }

        catch (NameAlreadyBoundException e) {
            Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                    String.format ("Nombre duplicado: ya hay un complemento de nombre %s en la base de datos.",
                            rowData [0]));

            return;
        }

        catch (NamingException e) {
            Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                    "No se puede asignar un nombre vacío a un complemento.");

            return;
        }

        BigDecimal p;
        try {
            if ((p = new BigDecimal (((String) rowData [1]).replace (",", "."))).signum () != 1)
                throw new ArithmeticException ("El precio debe ser un número positivo.");
        }

        catch (NumberFormatException e) {
            Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                    String.format (
                            "Formato incorrecto: no pudo establecerse un precio porque %s no es un número válido.",
                            ((String) rowData [1]).replace (" ", "").replace ("€", "")));

            return;
        }

        catch (ArithmeticException e) {
            Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                    String.format (
                            "Precio no válido: el precio especificado, %s €, no es positivo.",
                            (String) rowData [1]));

            return;
        }

        int d = -1;
        try {
            if ((d = Integer.valueOf (((String) rowData [2]).replace (" %", ""))) < 0 || d >= 100)
                throw new ArithmeticException (
                        String.format ("El descuento no puede ser %s.", d >= 100 ? "mayor de 100" : "negativo"));
        }

        catch (NumberFormatException e) {
            Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                    String.format (
                            "Formato incorrecto: no pudo establecerse un precio porque %s no es un número válido.",
                            ((String) rowData [2]).replace (" ", "").replace ("€", "")));

            return;
        }

        catch (ArithmeticException e) {
            Logger.getLogger (ComplementosTableModel.class.getName ()).log (Level.WARNING,
                    String.format (
                            "Descuento no válido: el descuento especificado, %d %%, es %s.", d,
                            d >= 100 ? "mayor de 100" : "negativo"));

            return;
        }

        super.addRow (rowData);

        this.db.insert (new Complemento ((String) rowData [0], p, d));
    }

    @Override
    public void removeRow (int row) {
        db.delete (this.values.get (row));
        this.values.remove (row);

        super.removeRow (row);
    }
}
