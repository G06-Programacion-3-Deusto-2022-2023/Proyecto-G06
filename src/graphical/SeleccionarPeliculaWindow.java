package graphical;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cine.Espectador;
import cine.Pelicula;
import internals.GestorBD;

public class SeleccionarPeliculaWindow extends JFrame {
    boolean siguiente;
    int indice;
    Image imagenIcon1;
    Image imagenIcon2;
    Image imagenIcon3;
    JLabel imagen1;
    JLabel imagen2;
    JLabel imagen3;

    public SeleccionarPeliculaWindow (GestorBD db, Espectador espectador, EspectadorWindow v2) {
    	
    	EspectadorWindow pw[] = new EspectadorWindow [] { v2 };
        SeleccionarPeliculaWindow v = this;

        indice = 0;
        siguiente = true;

        SortedSet <Pelicula> peliculas = new TreeSet <Pelicula> ();

        JButton anteriores3 = new JButton ("Anteriores 3 peliculas");
        JButton siguientes3 = new JButton ("Siguientes 3 peliculas");
        JButton pelicula1 = new JButton ();
        JButton pelicula2 = new JButton ();
        JButton pelicula3 = new JButton ();
        JPanel foto1 = new JPanel ();
        JPanel foto2 = new JPanel ();
        JPanel foto3 = new JPanel ();
        JPanel centralSuperior = new JPanel (new GridLayout (1, 3));
        JPanel centralInferior = new JPanel (new GridLayout (1, 3));
        JPanel principal = new JPanel (new GridLayout (2, 1));
        JLabel imagen1 = new JLabel ();
        JLabel imagen2 = new JLabel ();
        JLabel imagen3 = new JLabel ();

        this.getContentPane ().setLayout (new BorderLayout ());
        ;
        this.getContentPane ().add (principal, BorderLayout.CENTER);
        this.getContentPane ().add (anteriores3, BorderLayout.NORTH);
        this.getContentPane ().add (siguientes3, BorderLayout.SOUTH);

        principal.add (centralSuperior);
        principal.add (centralInferior);
        centralInferior.add (pelicula1);
        centralInferior.add (pelicula2);
        centralInferior.add (pelicula3);
        foto1.add (imagen1);
        foto2.add (imagen2);
        foto3.add (imagen3);

        centralSuperior.add (foto1);
        centralSuperior.add (foto2);
        centralSuperior.add (foto3);

        this.setTitle (espectador == null ? "Invitado" : espectador.getNombre ());
        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);

        this.setSize (1000, 800);
        this.setLocationRelativeTo (null);
        this.setVisible (true);

        siguientes3.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                if (siguiente == true) {
                    indice = indice + 1;
                    if (indice >= peliculas.size ()) {
                        indice = indice - peliculas.size ();
                    }
                    pelicula1.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon1 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }
                    indice = indice + 1;
                    if (indice >= peliculas.size ()) {
                        indice = indice - peliculas.size ();
                    }
                    pelicula2.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon2 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }
                    indice = indice + 1;
                    if (indice >= peliculas.size ()) {
                        indice = indice - peliculas.size ();
                    }
                    pelicula3.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon3 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }

                    imagen1.setIcon (new ImageIcon (imagenIcon1));
                    imagen2.setIcon (new ImageIcon (imagenIcon2));
                    imagen3.setIcon (new ImageIcon (imagenIcon3));

                    centralSuperior.add (foto1);
                    centralSuperior.add (foto2);
                    centralSuperior.add (foto3);
                }
                else {
                    indice = indice + 3;
                    if (indice >= peliculas.size ()) {
                        indice = indice - peliculas.size ();
                    }
                    pelicula1.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon1 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }
                    indice = indice + 1;
                    if (indice >= peliculas.size ()) {
                        indice = indice - peliculas.size ();
                    }
                    pelicula2.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon2 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }
                    indice = indice + 1;
                    if (indice >= peliculas.size ()) {
                        indice = indice - peliculas.size ();
                    }
                    pelicula3.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon3 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }

                    imagen1.setIcon (new ImageIcon (imagenIcon1));
                    imagen2.setIcon (new ImageIcon (imagenIcon2));
                    imagen3.setIcon (new ImageIcon (imagenIcon3));

                    centralSuperior.add (foto1);
                    centralSuperior.add (foto2);
                    centralSuperior.add (foto3);
                    siguiente = true;
                }
            }
        });
        anteriores3.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                if (siguiente == false) {
                    indice = indice - 1;
                    if (indice < 0) {
                        indice = indice + peliculas.size ();
                    }
                    pelicula3.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon3 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }

                    indice = indice - 1;
                    if (indice < 0) {
                        indice = indice + peliculas.size ();
                    }
                    pelicula2.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon2 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }
                    indice = indice - 1;
                    if (indice < 0) {
                        indice = indice + peliculas.size ();
                    }
                    pelicula1.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon1 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }

                    imagen1.setIcon (new ImageIcon (imagenIcon1));
                    imagen2.setIcon (new ImageIcon (imagenIcon2));
                    imagen3.setIcon (new ImageIcon (imagenIcon3));

                    centralSuperior.add (foto1);
                    centralSuperior.add (foto2);
                    centralSuperior.add (foto3);
                }
                else {
                    indice = indice - 3;
                    if (indice < 0) {
                        indice = indice + peliculas.size ();
                    }
                    pelicula3.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon3 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }

                    indice = indice - 1;
                    if (indice < 0) {
                        indice = indice + peliculas.size ();
                    }
                    pelicula2.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon2 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }
                    indice = indice - 1;
                    if (indice < 0) {
                        indice = indice + peliculas.size ();
                    }
                    pelicula1.setText (ObtenerPelicula (peliculas, indice));
                    try {
                        imagenIcon1 = new ImageIcon (
                                ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice)))).getImage ()
                                        .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace ();
                    }

                    imagen1.setIcon (new ImageIcon (imagenIcon1));
                    imagen2.setIcon (new ImageIcon (imagenIcon2));
                    imagen3.setIcon (new ImageIcon (imagenIcon3));

                    centralSuperior.add (foto1);
                    centralSuperior.add (foto2);
                    centralSuperior.add (foto3);

                    siguiente = false;
                }
            }
        });
        pelicula1.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                for (Pelicula pelicula : peliculas) {
                    if (pelicula.getNombre ().equals (pelicula1.getText ())) {
                        SwingUtilities.invokeLater ( () -> new SalaCineWindow (db, espectador, pelicula, v));
                    }
                }

            }
        });
        pelicula2.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                for (Pelicula pelicula : peliculas) {
                    if (pelicula.getNombre ().equals (pelicula2.getText ())) {
                        SwingUtilities.invokeLater ( () -> new SalaCineWindow (db, espectador, pelicula, v));
                    }
                }

            }
        });
        pelicula3.addActionListener (new ActionListener () {

            @Override
            public void actionPerformed (ActionEvent e) {
                for (Pelicula pelicula : peliculas) {
                    if (pelicula.getNombre ().equals (pelicula3.getText ())) {
                        SwingUtilities.invokeLater ( () -> new SalaCineWindow (db, espectador, pelicula, v));
                    }
                }

            }
        });
        this.addWindowListener (new WindowAdapter () {

            @Override
            public void windowOpened (WindowEvent e) {

                peliculas.addAll (Pelicula.getDefault ());
                pelicula1.setText (ObtenerPelicula (peliculas, indice));
                pelicula2.setText (ObtenerPelicula (peliculas, indice + 1));
                pelicula3.setText (ObtenerPelicula (peliculas, indice + 2));
                try {
                    imagenIcon1 = new ImageIcon (ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice))))
                            .getImage ().getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    imagenIcon2 = new ImageIcon (
                            ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice + 1)))).getImage ()
                                    .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                    imagenIcon3 = new ImageIcon (
                            ImageIO.read (new File (ObtenerImagenPelicula (peliculas, indice + 2)))).getImage ()
                                    .getScaledInstance (300, 300, Image.SCALE_SMOOTH);
                }
                catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace ();
                }
                indice = indice + 2;

                imagen1.setIcon (new ImageIcon (imagenIcon1));
                imagen2.setIcon (new ImageIcon (imagenIcon2));
                imagen3.setIcon (new ImageIcon (imagenIcon3));

                v2.setVisible (false);
            }

            @Override
            public void windowClosed (WindowEvent e) {
            	if (pw [0] == null)
                    return;

                v2.setVisible (true);
            }
        });
    }

    public static String ObtenerPelicula (SortedSet <Pelicula> peliculas, Integer indice) {
        ArrayList <Pelicula> peliculasArray = new ArrayList <Pelicula> (peliculas);
        String pelicula = peliculasArray.get (indice).getNombre ();

        return pelicula;
    }

    public static String ObtenerImagenPelicula (SortedSet <Pelicula> peliculas, Integer indice) {
        ArrayList <Pelicula> peliculasArray = new ArrayList <Pelicula> (peliculas);
        String pelicula = peliculasArray.get (indice).getRutaImagen ();

        return pelicula;
    }
}
