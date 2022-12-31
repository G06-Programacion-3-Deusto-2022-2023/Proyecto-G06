package VentanaGrafica;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.function.Supplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class LoadingWindow {
    private class ILoadingWindow extends JWindow {
        public ILoadingWindow () {
            super ();

            this.add (((Supplier <JPanel>) ( () -> {
                JPanel p = new JPanel ();
                p.setLayout (new BoxLayout (p, BoxLayout.X_AXIS));

                p.add (Box.createRigidArea (new Dimension (15, 0)));

                p.add (((Supplier <JPanel>) ( () -> {
                    JPanel q = new JPanel ();
                    q.setLayout (new BoxLayout (q, BoxLayout.Y_AXIS));

                    q.add (Box.createRigidArea (new Dimension (0, 15)));

                    q.add (((Supplier <JPanel>) ( () -> {
                        JPanel r = new JPanel (new FlowLayout (FlowLayout.CENTER, 20, 0));

                        r.add (new JLabel (new ImageIcon (
                                new ImageIcon ("data/assets/loading.gif").getImage ().getScaledInstance (32, 32, 0))));

                        r.add (((Supplier <JLabel>) ( () -> {
                            JLabel l = new JLabel ("Cargando...");
                            l.setFont (l.getFont ().deriveFont (Font.BOLD, 16f));

                            return l;
                        })).get ());

                        return r;
                    })).get ());

                    q.add (Box.createRigidArea (new Dimension (0, 15)));

                    return q;
                })).get ());

                p.add (Box.createRigidArea (new Dimension (15, 0)));

                return p;
            })).get ());

            this.pack ();
            this.setLocationRelativeTo (null);
            this.setAlwaysOnTop (true);

            this.setVisible (true);
        }
    }

    public LoadingWindow (Runnable r) {
        this (r, Thread.currentThread ());
    }

    public LoadingWindow (Thread t) {
        this (t, Thread.currentThread ());
    }

    public LoadingWindow (Runnable r, Thread s) {
        this (new Thread (r), s);
    }

    public LoadingWindow (Thread t, Thread s) throws NullPointerException {
        super ();

        if (t == null || s == null)
            throw new NullPointerException ("No se puede pasar un hilo nulo a la ventana de carga.");

        if (SwingUtilities.isEventDispatchThread ()) {
            new WLoadingWindow (t, javax.swing.FocusManager.getCurrentManager ().getActiveWindow ());

            return;
        }

        new TLoadingWindow (t, s);
    }

    private class TLoadingWindow {
        public TLoadingWindow (Thread t, Thread s) {
            super ();

            ILoadingWindow w = new ILoadingWindow ();

            Thread nt;
            (nt = new Thread ( () -> {
                t.start ();

                try {
                    t.join ();
                }

                catch (InterruptedException e) {
                    e.printStackTrace ();
                }
            })).start ();

            try {
                nt.join ();
            }

            catch (InterruptedException e) {
                e.printStackTrace ();
            }

            w.setVisible (false);
        }
    }

    private class WLoadingWindow {
        public WLoadingWindow (Thread t, Window w) {
            super ();

            ILoadingWindow g[] = new ILoadingWindow [1];

            Thread nt;
            (nt = new Thread ( () -> {
                WLoadingWindow.disableComponents (w);

                g [0] = new ILoadingWindow ();
                g [0].setLocationRelativeTo (w);

                Thread nt2;
                (nt2 = new Thread ( () -> {
                    t.start ();

                    try {
                        t.join ();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                })).start ();

                try {
                    nt2.join ();
                }
                catch (InterruptedException e) {
                    e.printStackTrace ();
                }

                g [0].dispose ();

                WLoadingWindow.enableComponents (w);
            })).start ();
        }

        private static void disableComponents (Container c) {
            WLoadingWindow.toggleComponents (c, false);
        }

        private static void enableComponents (Container c) {
            WLoadingWindow.toggleComponents (c, true);
        }

        private static void toggleComponents (Container c, boolean enable) {
            Component comps[] = c.getComponents ();
            for (int i = 0; i < comps.length; i++) {
                comps [i].setEnabled (enable);

                if (comps [i] instanceof Container)
                    WLoadingWindow.toggleComponents ((Container) comps [i], enable);
            }
        }
    }
}
