package VentanaGrafica;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import internals.GestorBD;

public class SeeKeysWindow extends JFrame {
    public SeeKeysWindow (GestorBD db) {
        this (db, null);
    }

    public SeeKeysWindow (GestorBD db, GestionarUsuariosWindow w) throws NullPointerException {
        if (db == null)
            throw new NullPointerException (
                    "No se puede pasar una base de datos nula a la ventana de visualización de llaves.");

        this.addWindowListener (new WindowAdapter () {
            @Override
            public void windowClosed (WindowEvent e) {
                if (w == null)
                    return;

                w.setVisible (true);
            }
        });

        this.add (((Supplier <JPanel>) ( () -> {
            JPanel p = new JPanel ();
            p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            p.add (((Supplier <JPanel>) ( () -> {
                JPanel q = new JPanel ();
                q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                List <String> keys = new ArrayList <String> (db.getAdminKeys ());
                for (; keys.size () < 10; keys.add ("-"))
                    ;

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                q.add (((Supplier <JPanel>) ( () -> {
                    JPanel r = new JPanel ();
                    r.setLayout (new BoxLayout (r, BoxLayout.Y_AXIS));

                    r.add (((Supplier <JLabel>) ( () -> {
                        JLabel l = new JLabel ("Llaves");

                        l.setFont (l.getFont ().deriveFont (Font.BOLD, 18f));

                        return l;
                    })).get ());
                    r.add (Box.createRigidArea (new Dimension (0, 15)));

                    r.add (((Supplier <JPanel>) ( () -> {
                        JPanel s = new JPanel (new GridLayout (5, 2, 25, 25));

                        for (int i[] = new int [1]; i [0] < 10;s.add (((Supplier <JTextPane>) ( () -> {
                            JTextPane l = new JTextPane ();

                            l.setText (keys.get (i [0]++));
                            l.setEditable (false);
                            l.setBackground (null);
                            l.setBorder (null);

                            if (l.getText ().equals ("-"))
                                return l;

                            l.setToolTipText ("Haz clic para copiar la llave.");
                            l.addMouseListener (new MouseAdapter () {
                                @Override
                                public void mouseClicked (MouseEvent e) {
                                    StringSelection selection = new StringSelection (l.getText ());
                                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents (selection, selection);
                                }
                            });

                            return l;
                        })).get ()))
                            ;

                        return s;
                    })).get ());

                    return r;
                })).get ());

                q.add (Box.createRigidArea (new Dimension (0, 25)));

                return q;
            })).get ());

            p.add (Box.createRigidArea (new Dimension (25, 0)));

            return p;
        })).get ());

        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle ("Llaves");
        this.setIconImage (
                ((ImageIcon) UIManager.getIcon ("OptionPane.questionIcon", new Locale ("es-ES"))).getImage ().getScaledInstance (64, 64, Image.SCALE_SMOOTH));
        this.pack ();
        this.setResizable (false);
        this.setLocationRelativeTo (null);
        this.setVisible (true);
    }
}
