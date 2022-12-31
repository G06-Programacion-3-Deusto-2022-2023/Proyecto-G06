package VentanaGrafica;

import javax.swing.SwingUtilities;

import cine.Administrador;
import internals.GestorBD;

public class Principal {
    public static void main (String [] args) {
        GestorBD db = new GestorBD ();
        new LoadingWindow ( () -> {
            if (GestorBD.getDBFile ().exists ())
                db.borrarBBDD ();
            db.crearBBDD();

            db.insert (new Administrador ("mikel",
                    "1234"));
        });
        SwingUtilities.invokeLater ( () -> new VentanaInicio (db));
    }
}
