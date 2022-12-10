package VentanaGrafica;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import java.util.Locale;
import java.util.function.Supplier;

import cine.Administrador;
import cine.GestorBD;

public class MiscOptionsWindow extends JFrame {
    public MiscOptionsWindow (GestorBD db) {
        this (db, null, null);
    }

    public MiscOptionsWindow (GestorBD db, Administrador admin) {
        this (db, admin, null);
    }

    public MiscOptionsWindow (GestorBD db, Administrador admin, AdministradorWindow w) throws NullPointerException {
        super ();

        if (db == null)
            throw new NullPointerException ("No se puede pasar una base de datos nula a la ventana de opciones.");

        MiscOptionsWindow f = this;

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (w == null)
                    return;

                w.setVisible (true);
            }
        });

        if (admin != null && db.obtenerDatosAdministradores ().contains (admin))
            this.add (((Supplier <JLabel>) ( () -> {
                JLabel l = new JLabel (admin.getNombre ());
                l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                return l;
            })).get (), BorderLayout.PAGE_START);


        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Opciones");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("FileView.floppyDriveIcon", new Locale ("es-ES"))).getImage ());
        this.pack ();
        this.setLocationRelativeTo (w);
        this.setVisible (true);
    }
}