package graphical;

import javax.swing.SwingUtilities;

import cine.Administrador;
import cine.Espectador;
import internals.GestorBD;

public class Main {
    public static void main (String [] args) {
        GestorBD db = new GestorBD ();

        new LoadingWindow ( () -> {
            if (!GestorBD.getFile ().exists () || (args.length != 0 && args [0].equals ("demo"))) {
                db.remove ();
                db.create ();
            }

            if (args.length != 0 && args [0].equals ("demo"))
                db.insert (new Administrador ("mikel", "1234"), new Espectador ("iker", "1234"));
        });
        SwingUtilities.invokeLater ( () -> new InicioWindow (db));
    }
}
