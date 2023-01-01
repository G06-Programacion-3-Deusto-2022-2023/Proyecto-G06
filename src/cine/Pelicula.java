package cine;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import internals.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import internals.HasID;
import internals.bst.BST;
import internals.bst.Filter;
import internals.bst.Treeable;

public class Pelicula implements Comparable <Pelicula>, Treeable <Pelicula>, HasID {
    // Media y desviación estándar de la distribución de las valoraciones de
    // las películas sacadas de los primeros 2²⁰ elementos
    // de la columna averageRating fis.readdel archivo
    // title.ratings.tsv/data.tsv
    // del dataset de Kaggle
    // https://www.kaggle.com/datasets/ashirwadsangwan/imdb-dataset.
    private static final Random random = new Random ();
    private static final double RANDOMAVG = 6.91973762512207D;
    private static final double RANDOMSTDEV = 1.38597026838328D;

    // Lo que se considera como la primera película, The Galloping Horse salió
    // en 1878.
    private static final Year MIN_FECHA = Year.of (1878);
    // El año actual
    private static final Year DEFAULT_FECHA = Year.of (Year.now (ZoneId.systemDefault ()).getValue ());
    // El año actual
    private static final Year MAX_FECHA = Year.of (Year.now (ZoneId.systemDefault ()).getValue ());

    // Una duración estándar de una hora y media
    private static final Duration DEFAULT_DURACION = Duration.ofMinutes (90);

    // URLs de las imágenes de las películas por defecto
    private static final List <URL> DEFAULT_MOVIE_IMAGE_URLS = new ArrayList <URL> (
            Arrays.asList (
                    new String [] {
                            "https://pics.filmaffinity.com/torrente_el_brazo_tonto_de_la_ley-769153589-large.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/c/ca/Ali_G_Indahouse.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/1/19/Minions_%282015_film%29.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/0/0c/American_Psycho.png",
                            "https://upload.wikimedia.org/wikipedia/en/c/c3/Inglourious_Basterds_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/f/fc/Fight_Club_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/3/39/American_History_X_poster.png",
                            "https://upload.wikimedia.org/wikipedia/en/a/a9/Karate_kid.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/f/f9/The_Angry_Birds_Movie_poster.png",
                            "https://upload.wikimedia.org/wikipedia/en/3/31/The_SpongeBob_SquarePants_Movie_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/d/d1/Cloudy_with_a_chance_of_meatballs_theataposter.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/4/49/The_Dark_Tower_teaser_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/2/2f/Gods_of_Egypt_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/b/b9/Shrek_2_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/0/00/Spider-Man_No_Way_Home_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/3/37/Logan_2017_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/e/e9/The_Nice_Guys_poster.png",
                            "https://upload.wikimedia.org/wikipedia/en/2/2e/Inception_%282010%29_theatrical_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/1/13/Bullet_Train_%28poster%29.jpeg",
                            "https://upload.wikimedia.org/wikipedia/en/c/c7/Nobody_2021_Film_Poster.jpeg",
                            "https://upload.wikimedia.org/wikipedia/en/b/bc/Interstellar_film_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/1/17/Limitless_Poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/f/ff/Coyote_ugly_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/0/0b/Your_Name_poster.png",
                            "https://upload.wikimedia.org/wikipedia/en/6/61/FromParisWithLovePoster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/1/1a/LoveAndMonstersPoster.jpeg",
                            "https://upload.wikimedia.org/wikipedia/en/b/b6/The_Revenant_2015_film_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/e/e3/Book_of_eli_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/8/81/The_Equalizer_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/d/df/I_am_legend_teaser.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/3/34/Cars_2006.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/1/14/Tenet_movie_poster.jpg",
                            "https://upload.wikimedia.org/wikipedia/en/9/9f/Blade_Runner_%281982_poster%29.png",
                            "https://upload.wikimedia.org/wikipedia/en/4/4f/Poster_-_Fast_and_Furious_Tokyo_Drift.jpg"
                    }).stream ().map (e -> {
                        URL url;
                        try {
                            url = new URL (e);
                        }

                        catch (MalformedURLException ex) {
                            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                                    String.format ("No se pudo crear una URL a partir de %s.",
                                            e));

                            url = null;
                        }

                        return url;
                    }).collect (Collectors.toList ()));

    // Ruta de las imágenes de las películas por defecto
    private static final String DEFAULT_MOVIE_IMAGE_PATH = "data/default/img";

    private static final List <String> DEFAULT_MOVIE_IMAGE_FILES = new ArrayList <String> (
            DEFAULT_MOVIE_IMAGE_URLS.stream ().map (e -> {
                return e == null ? ""
                        : String.format ("%s%c%s", Pelicula.DEFAULT_MOVIE_IMAGE_PATH, File.separatorChar,
                                new File (e.getFile ()).getName ());
            }).collect (Collectors.toList ()));

    // Flag que especifica si se han descargado o no las imágenes de las
    // películas por defecto
    private static boolean DEFAULT_IMAGES_DOWNLOADED = false;

    // Flag que especifica si se ha llamado ya al hilo que se encarga de
    // descargar las imágenes de las películas por
    // defecto.DEFAULT_IMAGES_DOWNLOADED
    private static boolean DEFAULT_IMAGES_THREAD_RUNNING = false;

    // Las películas por defecto (soy consciente de que esto es una guarrada
    // pero vamos a dejarlo así por el momento)
    private static final int NDEFAULT_PELICULAS = 35;
    private static long SET_DEFAULT_PELICULAS = 0;
    protected static final SortedSet <Pelicula> DEFAULT_PELICULAS = Arrays.asList (
            new Pelicula [] {
                    new Pelicula (new UUID (0L, 0L),
                            "Torrente, el brazo tonto de la ley",
                            DEFAULT_MOVIE_IMAGE_FILES.get (0), 6.8,
                            Year.of (1998),
                            "Santiago Segura",
                            Duration.ofMinutes (97),
                            EdadRecomendada.DIECIOCHO,
                            Genero.Nombre.COMEDIA),
                    new Pelicula (new UUID (0L, 1L), "Ali G anda suelto",
                            DEFAULT_MOVIE_IMAGE_FILES.get (1),
                            6.2, Year.of (2002), "Mark Mylod",
                            Duration.ofMinutes (85),
                            EdadRecomendada.DIECIOCHO,
                            Genero.Nombre.COMEDIA),
                    new Pelicula (new UUID (0L, 2L), "Los minions",
                            DEFAULT_MOVIE_IMAGE_FILES.get (2), 6.4,
                            Year.of (2015),
                            "Kyle Balda, Pierre Coffin",
                            Duration.ofMinutes (91),
                            EdadRecomendada.TODOS, Genero.Nombre.COMEDIA,
                            Genero.Nombre.CIENCIA_FICCION),
                    new Pelicula (new UUID (0L, 3L), "American Psycho",
                            DEFAULT_MOVIE_IMAGE_FILES.get (3), 7.6,
                            Year.of (2000), "Mary Harron",
                            Duration.ofMinutes (102),
                            EdadRecomendada.DIECIOCHO, Genero.Nombre.DRAMA,
                            Genero.Nombre.TERROR),
                    new Pelicula (new UUID (0L, 4L), "Malditos Bastardos",
                            DEFAULT_MOVIE_IMAGE_FILES.get (4),
                            8.3, Year.of (2009),
                            "Quentin Tarantino",
                            Duration.ofMinutes (153),
                            EdadRecomendada.DIECIOCHO, Genero.Nombre.DRAMA),
                    new Pelicula (new UUID (0L, 5L), "El club de la lucha",
                            DEFAULT_MOVIE_IMAGE_FILES.get (5),
                            8.8, Year.of (1999),
                            "David Fincher",
                            Duration.ofMinutes (139),
                            EdadRecomendada.DIECIOCHO, Genero.Nombre.DRAMA),
                    new Pelicula (new UUID (0L, 6L), "American History X",
                            DEFAULT_MOVIE_IMAGE_FILES.get (6),
                            8.5, Year.of (1998), "Tony Kaye",
                            Duration.ofMinutes (119),
                            EdadRecomendada.DIECISEIS, Genero.Nombre.DRAMA),
                    new Pelicula (new UUID (0L, 7L), "Karate Kid",
                            DEFAULT_MOVIE_IMAGE_FILES.get (7), 7.3,
                            Year.of (1984), "John G. Avildsen",
                            Duration.ofMinutes (126),
                            EdadRecomendada.TODOS, Genero.Nombre.ACCION,
                            Genero.Nombre.DRAMA),
                    new Pelicula (new UUID (0L, 8L), "Angry Birds",
                            DEFAULT_MOVIE_IMAGE_FILES.get (8), 6.3,
                            Year.of (2016),
                            "Clay Kaytis, Fergal Reilly",
                            Duration.ofMinutes (97), EdadRecomendada.TODOS,
                            Genero.Nombre.ACCION, Genero.Nombre.COMEDIA),
                    new Pelicula (new UUID (0L, 9L), "Bob Esponja",
                            DEFAULT_MOVIE_IMAGE_FILES.get (9), 7.1,
                            Year.of (2004),
                            "Stephen Hillenburg, Mark Osborne",
                            Duration.ofMinutes (87), EdadRecomendada.TODOS,
                            Genero.Nombre.COMEDIA),
                    new Pelicula (new UUID (0L, 10L), "Lluvia de Albóndigas",
                            DEFAULT_MOVIE_IMAGE_FILES.get (10), 6.9,
                            Year.of (2009),
                            "Phil Lord, Christopher Miller",
                            Duration.ofMinutes (90), EdadRecomendada.TODOS,
                            Genero.Nombre.COMEDIA,
                            Genero.Nombre.CIENCIA_FICCION),
                    new Pelicula (new UUID (0L, 11L), "La Torre Oscura",
                            DEFAULT_MOVIE_IMAGE_FILES.get (11),
                            5.6, Year.of (2017),
                            "Nikolaj Arcel", Duration.ofMinutes (95),
                            EdadRecomendada.DOCE,
                            Genero.Nombre.ACCION,
                            Genero.Nombre.CIENCIA_FICCION,
                            Genero.Nombre.FANTASIA),
                    new Pelicula (new UUID (0L, 12L), "Dioses de Egipto",
                            DEFAULT_MOVIE_IMAGE_FILES.get (12),
                            5.4, Year.of (2016),
                            "Alex Proyas", Duration.ofMinutes (127),
                            EdadRecomendada.SIETE,
                            Genero.Nombre.ACCION, Genero.Nombre.FANTASIA),
                    new Pelicula (new UUID (0L, 13L), "Shrek 2",
                            DEFAULT_MOVIE_IMAGE_FILES.get (13), 7.3,
                            Year.of (2004),
                            "Andrew Adamson, Kelly Asbury, Conrad Vernon",
                            Duration.ofMinutes (93),
                            EdadRecomendada.TODOS, Genero.Nombre.COMEDIA,
                            Genero.Nombre.ROMANCE,
                            Genero.Nombre.FANTASIA),
                    new Pelicula (new UUID (0L, 14L), "Spider-Man: No Way Home",
                            DEFAULT_MOVIE_IMAGE_FILES.get (14), 8.3,
                            Year.of (2021),
                            "Jon Watts", Duration.ofMinutes (148),
                            EdadRecomendada.SIETE, Genero.Nombre.ACCION,
                            Genero.Nombre.FANTASIA,
                            Genero.Nombre.CIENCIA_FICCION),
                    new Pelicula (new UUID (0L, 15L), "Logan",
                            DEFAULT_MOVIE_IMAGE_FILES.get (15), 8.1,
                            Year.of (2017), "James Mangold",
                            Duration.ofMinutes (137),
                            EdadRecomendada.DIECISEIS, Genero.Nombre.ACCION,
                            Genero.Nombre.DRAMA,
                            Genero.Nombre.CIENCIA_FICCION,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 16L), "Dos buenos tipos",
                            DEFAULT_MOVIE_IMAGE_FILES.get (16),
                            7.3, Year.of (2016), "Shane Black",
                            Duration.ofMinutes (116),
                            EdadRecomendada.DIECISEIS, Genero.Nombre.ACCION,
                            Genero.Nombre.COMEDIA, Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 17L), "Origen",
                            DEFAULT_MOVIE_IMAGE_FILES.get (17), 8.8,
                            Year.of (2010), "James Mangold",
                            Duration.ofMinutes (248), EdadRecomendada.DOCE,
                            Genero.Nombre.ACCION,
                            Genero.Nombre.CIENCIA_FICCION,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 18L), "Bullet Train",
                            DEFAULT_MOVIE_IMAGE_FILES.get (18), 7.3,
                            Year.of (2022), "David Leitch",
                            Duration.ofMinutes (127),
                            EdadRecomendada.DIECISEIS,
                            Genero.Nombre.ACCION, Genero.Nombre.COMEDIA,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 19L), "Nadie",
                            DEFAULT_MOVIE_IMAGE_FILES.get (19), 7.4,
                            Year.of (2021), "Ilya Naishuller",
                            Duration.ofMinutes (152),
                            EdadRecomendada.DIECIOCHO, Genero.Nombre.ACCION,
                            Genero.Nombre.DRAMA,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 20L), "Interstellar",
                            DEFAULT_MOVIE_IMAGE_FILES.get (20), 8.6,
                            Year.of (2014), "Christopher Nolan",
                            Duration.ofMinutes (169), EdadRecomendada.DOCE,
                            Genero.Nombre.DRAMA,
                            Genero.Nombre.CIENCIA_FICCION),
                    new Pelicula (new UUID (0L, 21L), "Sin Límites",
                            DEFAULT_MOVIE_IMAGE_FILES.get (21), 7.4,
                            Year.of (2011), "Neil Burger",
                            Duration.ofMinutes (105), EdadRecomendada.DOCE,
                            Genero.Nombre.CIENCIA_FICCION,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 22L), "El Bar Coyote",
                            DEFAULT_MOVIE_IMAGE_FILES.get (22), 5.7,
                            Year.of (2000), "David McNally",
                            Duration.ofMinutes (100), EdadRecomendada.DOCE,
                            Genero.Nombre.COMEDIA, Genero.Nombre.DRAMA,
                            Genero.Nombre.ROMANCE),
                    new Pelicula (new UUID (0L, 23L), "Your Name.",
                            DEFAULT_MOVIE_IMAGE_FILES.get (23), 8.4,
                            Year.of (2016), "Makoto Shinkai",
                            Duration.ofMinutes (106), EdadRecomendada.TODOS,
                            Genero.Nombre.DRAMA, Genero.Nombre.FANTASIA,
                            Genero.Nombre.ROMANCE),
                    new Pelicula (new UUID (0L, 24L), "Desde París con amor",
                            DEFAULT_MOVIE_IMAGE_FILES.get (24), 6.4,
                            Year.of (2010),
                            "Pierre Morel",
                            Duration.ofMinutes (92),
                            EdadRecomendada.DIECIOCHO, Genero.Nombre.ACCION,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 25L), "De amor y monstruos",
                            DEFAULT_MOVIE_IMAGE_FILES.get (25), 6.9,
                            Year.of (2020),
                            "Michael Matthews",
                            Duration.ofMinutes (109), EdadRecomendada.DOCE,
                            Genero.Nombre.ACCION,
                            Genero.Nombre.COMEDIA, Genero.Nombre.FANTASIA,
                            Genero.Nombre.CIENCIA_FICCION),
                    new Pelicula (new UUID (0L, 26L), "El Renacido",
                            DEFAULT_MOVIE_IMAGE_FILES.get (26), 8,
                            Year.of (2015),
                            "Alejandro G. Iñárritu",
                            Duration.ofMinutes (156),
                            EdadRecomendada.DIECISEIS, Genero.Nombre.ACCION,
                            Genero.Nombre.DRAMA),
                    new Pelicula (new UUID (0L, 27L), "El Libro de Eli",
                            DEFAULT_MOVIE_IMAGE_FILES.get (27), 6.8,
                            Year.of (2013),
                            "Albert Hughes, Allen Hughes",
                            Duration.ofMinutes (118), EdadRecomendada.DOCE,
                            Genero.Nombre.ACCION, Genero.Nombre.DRAMA,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 28L), "The Equalizer",
                            DEFAULT_MOVIE_IMAGE_FILES.get (28), 7.2,
                            Year.of (2014),
                            "Antoine Fuqua",
                            Duration.ofMinutes (132),
                            EdadRecomendada.DIECIOCHO, Genero.Nombre.ACCION,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 29L), "Soy Leyenda",
                            DEFAULT_MOVIE_IMAGE_FILES.get (29), 7.2,
                            Year.of (2007),
                            "Francis Lawrence",
                            Duration.ofMinutes (101), EdadRecomendada.DOCE,
                            Genero.Nombre.ACCION, Genero.Nombre.DRAMA,
                            Genero.Nombre.CIENCIA_FICCION,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 30L), "Frozen: El reino del hielo",
                            DEFAULT_MOVIE_IMAGE_FILES.get (30), 7.4,
                            Year.of (2013),
                            "Chris Buck, Jennifer Lee",
                            Duration.ofMinutes (102), EdadRecomendada.TODOS,
                            Genero.Nombre.COMEDIA, Genero.Nombre.FANTASIA,
                            Genero.Nombre.MUSICAL),
                    new Pelicula (new UUID (0L, 31L), "Cars",
                            DEFAULT_MOVIE_IMAGE_FILES.get (31), 7.2,
                            Year.of (2006),
                            "John Lasseter, Joe Ranft",
                            Duration.ofMinutes (117), EdadRecomendada.TODOS,
                            Genero.Nombre.COMEDIA),
                    new Pelicula (new UUID (0L, 32L), "Tenet",
                            DEFAULT_MOVIE_IMAGE_FILES.get (32), 7.3,
                            Year.of (2003),
                            "Christopher Nolan",
                            Duration.ofMinutes (150), EdadRecomendada.DOCE,
                            Genero.Nombre.ACCION,
                            Genero.Nombre.CIENCIA_FICCION,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 33L), "Blade Runner",
                            DEFAULT_MOVIE_IMAGE_FILES.get (33), 8.1,
                            Year.of (1982),
                            "Ridley Scott",
                            Duration.ofMinutes (117), EdadRecomendada.DOCE,
                            Genero.Nombre.ACCION, Genero.Nombre.DRAMA,
                            Genero.Nombre.CIENCIA_FICCION,
                            Genero.Nombre.SUSPENSE),
                    new Pelicula (new UUID (0L, 34L), "A todo gas: Tokyo Race",
                            DEFAULT_MOVIE_IMAGE_FILES.get (34), 6,
                            Year.of (2006),
                            "Justin Lin",
                            Duration.ofMinutes (104),
                            EdadRecomendada.DIECIOCHO, Genero.Nombre.ACCION,
                            Genero.Nombre.SUSPENSE)
            }).stream ().collect (Collectors.toCollection (TreeSet <Pelicula>::new));

    private UUID id;
    private String nombre;
    private String rutaImagen;
    private double valoracion;
    private Year fecha;
    private String director;
    private Duration duracion;
    private EdadRecomendada edad;
    private SortedSet <Genero.Nombre> generos;
    private SortedSet <SetPeliculas> sets;

    public Pelicula () {
        this ("");
    }

    public Pelicula (String nombre) {
        this (nombre, Genero.Nombre.NADA);
    }

    public Pelicula (String nombre, Genero.Nombre... generos) {
        this (nombre, new ArrayList <Genero.Nombre> (Arrays.asList (generos)));
    }

    public Pelicula (String nombre, Collection <Genero.Nombre> generos) {
        this (nombre, Double.NaN, EdadRecomendada.TODOS, generos);
    }

    public Pelicula (String nombre, double valoracion, EdadRecomendada edad, Genero.Nombre... generos) {
        this (nombre, valoracion, edad,
                new ArrayList <Genero.Nombre> (
                        generos == null ? Collections.emptyList () : Arrays.asList (generos)));
    }

    public Pelicula (String nombre, double valoracion, EdadRecomendada edad, Collection <Genero.Nombre> generos) {
        this (nombre, "", valoracion, DEFAULT_FECHA, "", DEFAULT_DURACION,
                edad, generos);
    }

    public Pelicula (String nombre, String rutaImagen, double valoracion, Year fecha, String director,
            Duration duracion,
            EdadRecomendada edad, Genero.Nombre... generos) {
        this (UUID.randomUUID (), nombre, rutaImagen, valoracion, fecha, director, duracion, edad,
                new ArrayList <Genero.Nombre> (Arrays.asList (generos)));
    }

    public Pelicula (String nombre, String rutaImagen, double valoracion, Year fecha, String director,
            Duration duracion,
            EdadRecomendada edad, Collection <Genero.Nombre> generos) {
        this (UUID.randomUUID (), nombre, rutaImagen, valoracion, fecha, director, duracion, edad, generos);
    }

    public Pelicula (UUID id, String nombre, String rutaImagen, double valoracion, Year fecha, String director,
            Duration duracion,
            EdadRecomendada edad, Genero.Nombre... generos) {
        this (id, nombre, rutaImagen, valoracion, fecha, director, duracion, edad,
                new ArrayList <Genero.Nombre> (Arrays.asList (generos)));
    }

    public Pelicula (UUID id, String nombre, String rutaImagen, double valoracion, Year fecha, String director,
            Duration duracion,
            EdadRecomendada edad, Collection <Genero.Nombre> generos) {
        this (id, nombre, rutaImagen, valoracion, fecha, director, duracion, edad,
                generos, null);
    }

    public Pelicula (UUID id, String nombre, String rutaImagen, double valoracion, Year fecha, String director,
            Duration duracion,
            EdadRecomendada edad, Collection <Genero.Nombre> generos, Collection <SetPeliculas> sets) {
        super ();

        this.id = id != null && ((Pelicula.isDefault (id)
                && Utils.isAmongstCallers ("cine.Pelicula")
                && (!Pelicula.isDefaultSet (id.getLeastSignificantBits ())
                        || (Pelicula.isDefaultSet (id.getLeastSignificantBits ())
                                && Utils.isAmongstCallers ("internals.GestorBD"))))
                || !Pelicula.isDefault (id))
                        ? id
                        : UUID.randomUUID ();
        this.setNombre (nombre);
        this.setRutaImagen (rutaImagen);
        this.setValoracion (valoracion);
        this.setFecha (fecha);
        this.setDirector (director);
        this.setDuracion (duracion);
        this.setEdad (edad);
        this.setGeneros (generos);
        this.setSets (sets);

        if (this.isDefault ()) {
            Pelicula.SET_DEFAULT_PELICULAS |= 1L << this.id.getLeastSignificantBits ();

            if (!Pelicula.DEFAULT_IMAGES_DOWNLOADED && !Pelicula.DEFAULT_IMAGES_THREAD_RUNNING) {
                Pelicula.DEFAULT_IMAGES_THREAD_RUNNING = true;

                new Thread () {
                    @Override
                    public void run () {
                        Thread t;
                        (t = new Thread () {
                            @Override
                            public void run () {
                                Pelicula.downloadDefaultImages ();
                            }
                        }).start ();

                        try {
                            t.join ();
                        }

                        catch (InterruptedException e) {
                            e.printStackTrace ();
                        }

                        Pelicula.DEFAULT_IMAGES_THREAD_RUNNING = false;
                    }
                }.start ();
            }
        }
    }

    public Pelicula (Pelicula pelicula) throws NullPointerException {
        this (pelicula.id, pelicula.nombre, pelicula.rutaImagen, pelicula.valoracion, pelicula.fecha,
                pelicula.director,
                pelicula.duracion,
                pelicula.edad, pelicula.generos, pelicula.sets);
    }

    public UUID getId () {
        return this.id;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.nombre = nombre == null || nombre.equals ("") ? this.id.toString ()
                : nombre;
    }

    public String getRutaImagen () {
        return this.rutaImagen;
    }

    public void setRutaImagen (String rutaImagen) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.rutaImagen = rutaImagen == null ? "" : rutaImagen;
    }

    public double getValoracion () {
        return this.valoracion;
    }

    public void setValoracion (double valoracion) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.valoracion = ((Double) valoracion).isNaN () || valoracion < 1
                || valoracion > 10 ? Double.NaN : Math.floor (valoracion * 10) / 10;
    }

    public Year getFecha () {
        return this.fecha;
    }

    public static Year minFecha () {
        return Pelicula.MIN_FECHA;
    }

    public static Year defaultFecha () {
        return Pelicula.DEFAULT_FECHA;
    }

    public static Year maxFecha () {
        return Pelicula.MAX_FECHA;
    }

    public void setFecha (int fecha) {
        this.setFecha (Year.of (fecha));
    }

    public void setFecha (Year fecha) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.fecha = fecha == null || fecha.compareTo (MIN_FECHA) < 0 || fecha.compareTo (MAX_FECHA) > 0 ? null
                : fecha;
    }

    public String getDirector () {
        return this.director;
    }

    public void setDirector (String director) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.director = director == null ? "" : director;
    }

    public Duration getDuracion () {
        return this.duracion;
    }

    public void setDuracion (Duration duracion) {
        // En estos momentos echo de menos la extensión backtrace () de GNU para
        // C

        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.duracion = duracion == null || duracion.isNegative () || duracion.isZero ()
                ? (this.duracion == null ? Pelicula.DEFAULT_DURACION : this.duracion)
                : (Utils.isAmongstCallers ("cine.Pelicula")
                        ? Pelicula.DEFAULT_DURACION
                        : Duration.ofMinutes (duracion.toMinutes ()));
    }

    public EdadRecomendada getEdad () {
        return this.edad;
    }

    public void setEdad (EdadRecomendada edad) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.edad = edad == null ? EdadRecomendada.TODOS : edad;
    }

    public SortedSet <Genero.Nombre> getGeneros () {
        return this.isDefault () ? new TreeSet <Genero.Nombre> (this.generos) : this.generos;
    }

    public void setGeneros (Collection <Genero.Nombre> generos) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.generos = generos == null || generos.isEmpty () || generos.contains (Genero.Nombre.NADA)
                ? new TreeSet <Genero.Nombre> (Collections.singleton (Genero.Nombre.NADA))
                : new TreeSet <Genero.Nombre> (generos);
    }

    public SortedSet <SetPeliculas> getSets () {
        return new TreeSet <SetPeliculas> (this.sets);
    }

    public void setSets (Collection <SetPeliculas> sets) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                && !Utils.isAmongstCallers ("cine.Pelicula") && !Utils.isAmongstCallers ("cine.SetPeliculas")
                && !Utils.isAmongstCallers ("internals.GestorBD"))
            return;

        this.sets = new TreeSet <SetPeliculas> ((Comparator <SetPeliculas>) ( (a, b) -> {
            return a.getId ().compareTo (b.getId ());
        }));

        if (sets != null)
            this.sets.addAll (sets);
    }

    public boolean isInSet (SetPeliculas set) {
        return set.contains (this);
    }

    public boolean addSet (SetPeliculas set) {
        if (set == null)
            return false;

        if (this.sets == null)
            this.setSets (null);

        return this.sets.contains (set) || (this.sets.add (set) && set.add (this));
    }

    public boolean addSets (Collection <SetPeliculas> sets) {
        if (this.sets == null || sets == null)
            return false;

        SetPeliculas array[] = sets.toArray (new SetPeliculas [0]);

        boolean all = true;
        for (int i = 0; i < array.length; all = all && this.addSet (array [i++]))
            ;

        return all;
    }

    public boolean removeSet (SetPeliculas set) {
        if (this.sets == null || set == null
                || (this.isDefault () && set.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ())
                        && !Utils.isAmongstCallers ("cine.Pelicula")
                        && !Utils.isAmongstCallers ("cine.SetPeliculas")
                        && !Utils.isAmongstCallers ("internals.GestorBD")))
            return false;

        return !this.sets.contains (set) || (this.sets.remove (set) && set.remove (this));
    }

    public boolean removeSets (Collection <SetPeliculas> sets) {
        if (this.sets == null || sets == null)
            return false;

        SetPeliculas array[] = sets.toArray (new SetPeliculas [0]);

        boolean all = true;
        for (int i = 0; i < array.length; all = all && this.removeSet (array [i++]))
            ;

        return all;
    }

    public void removeFromSets () {
        if (this.sets == null && (this.isDefault () && !Utils.isAmongstCallers ("cine.Pelicula")
                && !Utils.isAmongstCallers ("cine.SetPeliculas")
                && !Utils.isAmongstCallers ("internals.GestorBD")))
            return;

        this.removeSets (this.sets);
    }

    @Override
    public int hashCode () {
        return Objects.hash (director, duracion, edad, fecha, generos, id, nombre, rutaImagen, sets, valoracion);
    }

    @Override
    public boolean equals (Object obj) {
        return obj instanceof Pelicula && this.id.equals (((Pelicula) obj).id);
    }

    @Override
    public int compareTo (Pelicula pelicula) {
        if (pelicula == null)
            return 1;

        if (this.nombre.equals (this.id.toString ()) && !pelicula.nombre.equals (pelicula.id.toString ()))
            return 1;

        if (!this.nombre.equals (this.id.toString ()) && pelicula.nombre.equals (pelicula.id.toString ()))
            return -1;

        if (this.nombre.equals (this.id.toString ()) && pelicula.nombre.equals (pelicula.id.toString ()))
            return this.id.compareTo (pelicula.id);

        int comp;
        if ((comp = this.nombre.toLowerCase ().compareTo (pelicula.nombre.toLowerCase ())) != 0)
            return comp;

        return this.id.compareTo (pelicula.id);
    }

    @Override
    public String toString () {
        return "Película (hash: " + this.hashCode () + ") {\n\tID: " + this.id
                + (this.isDefault () ? " (película predeterminada)" : "") + "\n\tNombre: " + this.nombre
                + "\n\tRuta de la imagen: " + this.rutaImagen + "\n\tValoración: "
                + (((Double) this.valoracion).isNaN () ? "-" : this.valoracion) + "\n\tFecha: " + this.fecha
                + "\n\tDirector: " + this.director + "\n\tDuración: " + this.duracionToString () + "\n\tEdad: "
                + this.edad + "\n\tGéneros: " + ((Supplier <String>) ( () -> {
                    StringBuilder str = new StringBuilder ();

                    List <Genero.Nombre> g = this.generos.stream ().collect (Collectors.toList ());
                    for (int i = 0; i < g.size (); i++)
                        str.append (String.format ("%s%s",
                                g.get (i).toString (),
                                i != g.size () - 1
                                        ? " · "
                                        : ""));

                    return str.toString ();
                })).get () + "\n\tSets: "
                + (this.sets.isEmpty () ? "-" : ((Supplier <String>) ( () -> {
                    StringBuilder str = new StringBuilder ();

                    List <SetPeliculas> s = this.sets.stream ().collect (Collectors.toList ());
                    for (int i = 0; i < s.size (); i++)
                        str.append (String.format ("%s%s%s",
                                s.get (i).getId ().toString (),
                                s.get (i).isDefault () ? " (set predeterminado)" : "",
                                i != s.size () - 1
                                        ? " · "
                                        : ""));

                    return str.toString ();
                })).get ()) + "\n}";
    }

    public String duracionToString () {
        return (this.duracion.toMinutes () >= 60
                ? String.format ("%d hora%s", (long) (this.duracion.toMinutes () / 60),
                        (long) (this.duracion.toMinutes () / 60) > 1 ? "s" : "")
                : "")
                + (this.duracion.toMinutes () % 60 != 0
                        ? String.format ("%s%d minuto%s",
                                this.duracion.toMinutes () >= 60 ? " y " : "",
                                this.duracion.toMinutes () % 60,
                                this.duracion.toMinutes () % 60 > 1 ? "s" : "")
                        : "");
    }

    public boolean isDefault () {
        return Pelicula.isDefault (this.id);
    }

    public static boolean isDefault (UUID id) {
        return id != null && id.getMostSignificantBits () == 0
                && id.getLeastSignificantBits () >= 0
                && id.getLeastSignificantBits () < Pelicula.NDEFAULT_PELICULAS;
    }

    public static SortedSet <Pelicula> getDefault () {
        return new TreeSet <Pelicula> (SetPeliculas.getDefault ().getPeliculas ());
    }

    public static Pelicula getDefault (int id) {
        return Pelicula.getDefault ((long) id);
    }

    public static SortedSet <Pelicula> getDefault (Integer... ids) {
        return getDefault (Arrays.asList (ids).stream ().map (Integer::longValue).collect (Collectors.toList ()));
    }

    public static Pelicula getDefault (long id) {
        return Pelicula.getDefault (new Long [] { id }).first ();
    }

    public static SortedSet <Pelicula> getDefault (Long... ids) {
        return getDefault (Arrays.asList (ids));
    }

    public static SortedSet <Pelicula> getDefault (Collection <Long> ids) {
        return Pelicula.DEFAULT_PELICULAS.stream ().filter (p -> ids.contains (p.id.getLeastSignificantBits ()))
                .collect (Collectors.toCollection (TreeSet::new));
    }

    public static void deleteDefault (int n) throws IllegalArgumentException {
        if (n < 0 || n >= 35)
            throw new IllegalArgumentException (String.format (
                    "El número %d no es un número de película por defecto válido ya que no está en el intervalo [0, 35)."));

        Pelicula p;
        try {
            p = Pelicula.DEFAULT_PELICULAS.stream ().filter (e -> e.getId ().getLeastSignificantBits () == n)
                    .findFirst ().get ();
        }

        catch (NoSuchElementException e) {
            throw new IllegalArgumentException (String.format (
                    "La película con ID %s no se encuentra en la lista de películas por defecto", new UUID (0, n)));
        }

        if (new File (Pelicula.DEFAULT_MOVIE_IMAGE_FILES
                .get (n))
                        .delete ())
            Logger.getLogger (Pelicula.class.getName ()).log (Level.INFO,
                    String.format ("Eliminado el archivo %s.", Pelicula.DEFAULT_MOVIE_IMAGE_FILES
                            .get (n)));

        else
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    String.format ("No se pudo eliminar el archivo %s.",
                            Pelicula.DEFAULT_MOVIE_IMAGE_FILES
                                    .get (n)));

        File f;
        if ((f = new File (Pelicula.DEFAULT_MOVIE_IMAGE_PATH)).list () == null && f.delete ())
            Logger.getLogger (Pelicula.class.getName ()).log (Level.INFO,
                    String.format ("Eliminadada la carpeta %s.",
                            Pelicula.DEFAULT_MOVIE_IMAGE_PATH));

        Pelicula.DEFAULT_IMAGES_DOWNLOADED = false;
        Pelicula.SET_DEFAULT_PELICULAS &= ~(1L << n);
    }

    public static void deleteDefault (long n) {
        Pelicula.deleteDefault ((int) n);
    }

    public static void deleteDefault () {
        Pelicula [] peliculas = Pelicula.DEFAULT_PELICULAS.toArray (new Pelicula [0]);

        for (int i = 0; i < peliculas.length; Pelicula.DEFAULT_IMAGES_DOWNLOADED = true)
            Pelicula.deleteDefault (i++);
    }

    public static boolean defaultImagesDownloaded () {
        return Pelicula.DEFAULT_IMAGES_DOWNLOADED;
    }

    protected static boolean downloadDefaultImages () {
        File fp = new File (Pelicula.DEFAULT_MOVIE_IMAGE_PATH);

        if (fp.exists () && fp.isDirectory () && Arrays.asList (fp.list ())
                .containsAll (Pelicula.DEFAULT_MOVIE_IMAGE_FILES.stream ()
                        .map (e -> e.replace (Pelicula.DEFAULT_MOVIE_IMAGE_PATH + File.separator, ""))
                        .collect (Collectors.toList ()))) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.INFO,
                    "Todas las imágenes de las películas por defecto estaban ya descargadas.");

            return Pelicula.DEFAULT_IMAGES_DOWNLOADED = true;
        }

        fp.mkdirs ();

        List <String> errors = new ArrayList <String> ();

        for (int i = 0; i < Pelicula.DEFAULT_MOVIE_IMAGE_FILES.size (); i++) {
            try {
                Utils.downloadFile (Pelicula.DEFAULT_MOVIE_IMAGE_PATH, Pelicula.DEFAULT_MOVIE_IMAGE_URLS.get (i));
            }

            catch (Exception e) {
                errors.add (String.format (
                        "%s: %s", Pelicula.DEFAULT_MOVIE_IMAGE_FILES.get (i)
                                .replace (Pelicula.DEFAULT_MOVIE_IMAGE_PATH + File.separator,
                                        ""),
                        e.getMessage ()));
            }
        }

        Logger.getLogger (Pelicula.class.getName ()).log (
                errors.isEmpty () ? Level.INFO : Level.WARNING,
                String.format ("%sas mágenes de las películas por defecto fueron descargadas.%s",
                        errors.isEmpty () ? "L" : "No todas l",
                        errors.isEmpty () ? ""
                                : String.format ("\nFaltaron: %s",
                                        errors.toString ())));

        return Pelicula.DEFAULT_IMAGES_DOWNLOADED = !errors.isEmpty ();
    }

    protected static boolean isDefaultSet (long n) throws UnsupportedOperationException {
        if (n < 0 || n >= Pelicula.NDEFAULT_PELICULAS)
            throw new UnsupportedOperationException ();

        return (Pelicula.SET_DEFAULT_PELICULAS & (1L << n)) != 0;
    }

    public static Pelicula random () {
        return Pelicula.random ("");
    }

    public static Pelicula random (String nombre) {
        return new Pelicula (nombre, "", Pelicula.random.nextGaussian (Pelicula.RANDOMAVG, Pelicula.RANDOMSTDEV),
                Year.of (Pelicula.random.nextInt (Pelicula.MIN_FECHA.getValue (), Pelicula.MAX_FECHA.getValue () + 1)),
                "", Duration.ofMinutes (Pelicula.random.nextLong (Pelicula.DEFAULT_DURACION.toMinutes (), 180)),
                EdadRecomendada.random (), Genero.randomGeneros ());
    }

    public static List <String> getNombres (Collection <Pelicula> peliculas) {
        ArrayList <String> nombres = new ArrayList <String> ();

        List <Pelicula> list = (peliculas.stream ().collect (Collectors.toCollection (TreeSet <Pelicula>::new)))
                .stream ().collect (Collectors.toList ());
        for (int i = 0; i < list.size (); nombres.add (list.get (i++).getNombre ()))
            ;

        return nombres;
    }

    public static BST <Pelicula> tree (Collection <Pelicula> values) {
        return Pelicula.tree (values, null, null);
    }

    public static BST <Pelicula> tree (Collection <Pelicula> values, Comparator <Pelicula> comp) {
        return Pelicula.tree (values, comp, null);
    }

    public static BST <Pelicula> tree (Collection <Pelicula> values, Filter <Pelicula> filter) {
        return Pelicula.tree (values, null, filter);
    }

    public static BST <Pelicula> tree (Collection <Pelicula> values, Comparator <Pelicula> comp,
            Filter <Pelicula> filter) {
        return new Pelicula ().bst (values, comp, filter);
    }

    public static List <Pelicula> orderBy (Collection <Pelicula> peliculas, Comparator <Pelicula> comp) {
        return Pelicula.orderBy (peliculas, comp, false);
    }

    public static List <Pelicula> orderBy (Collection <Pelicula> peliculas, Comparator <Pelicula> comp, boolean desc) {
        return Pelicula.tree (peliculas,
                desc ? comp.reversed () : comp)
                .getValues ();
    }

    public static List <Pelicula> filterBy (Collection <Pelicula> peliculas, Filter <Pelicula> filter) {
        return Pelicula.filterBy (peliculas, filter, false);
    }

    public static List <Pelicula> filterBy (Collection <Pelicula> peliculas, Filter <Pelicula> filter, boolean neg) {
        return Pelicula.tree (peliculas, neg ? (Filter <Pelicula>) (p -> !filter.filter (p)) : filter).getValues ();
    }

    public static List <Pelicula> orderFilterBy (Collection <Pelicula> peliculas, Comparator <Pelicula> comp,
            Filter <Pelicula> filter) {
        return Pelicula.orderFilterBy (peliculas, comp, filter, false, false);
    }

    public static List <Pelicula> orderFilterBy (Collection <Pelicula> peliculas, Comparator <Pelicula> comp,
            Filter <Pelicula> filter, boolean desc, boolean neg) {
        return Pelicula.tree (peliculas,
                desc ? comp.reversed () : comp,
                neg ? (Filter <Pelicula>) (p -> !filter.filter (p)) : filter).getValues ();
    }

    public static List <Pelicula> fromJSON (File file) throws NullPointerException, IOException, JSONException {
        if (file == null)
            throw new NullPointerException (String.format ("No se puede pasar un archivo nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        if (!file.exists ())
            throw new IOException (
                    String.format ("No se pudo encontrar el archivo especificado (%s).", file.getAbsolutePath ()));

        if (!Files.probeContentType (file.toPath ()).equals ("application/json"))
            throw new JSONException (
                    String.format ("El archivo especificado, %s, no es un archivo JSON válido.",
                            file.getAbsolutePath ()));

        try {
            return Pelicula.fromJSON (Files.readString (file.toPath ()));
        }

        catch (IOException e) {
            throw new IOException (
                    String.format ("No se pudo abrir el archivo %s para recoger los datos.", file.getAbsolutePath ()));
        }

        catch (JSONException e) {
            throw new JSONException (String.format (
                    "El archivo especificado, %s, no es un archivo JSON válido que contenga un JSON Array.",
                    file.getAbsolutePath ()));
        }
    }

    public static List <Pelicula> fromJSON (String jstr) throws NullPointerException, JSONException {
        if (jstr == null)
            throw new NullPointerException (String.format ("No se puede pasar un string nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        JSONArray json;
        try {
            json = new JSONArray (jstr);
        }

        catch (JSONException e) {
            throw new JSONException (Utils.isAmongstCallers ("cine.SetPeliculas.fromJSON") ? ""
                    : "No se puede extraer un JSONArray válido de esta cadena de carácteres");
        }

        List <Pelicula> list = new ArrayList <Pelicula> ();
        SortedSet <Integer> errors = new TreeSet <Integer> ();
        for (int i = 0; i < json.length (); i++)
            try {
                list.add (Pelicula.fromJSONObject (json.getJSONObject (i)));
            }

            catch (JSONException e) {
                errors.add (i);
            }

        Logger.getLogger (Pelicula.class.getName ()).log (errors.isEmpty () ? Level.INFO : Level.WARNING,
                errors.isEmpty () ? "Se importaron todas las películas."
                        : String.format ("Hubo errores tratando de importar %d de las películas (con índice %s).",
                                errors.size (), ((Supplier <String>) ( () -> {
                                    StringBuilder str = new StringBuilder ();

                                    Integer errorsArray[] = errors.toArray (new Integer [0]);
                                    for (int i = 0; i < errorsArray.length; i++) {
                                        str.append (errorsArray [i]);

                                        if (i != errorsArray.length - 1)
                                            str.append (", ");
                                    }

                                    return str.toString ();
                                })).get ()));

        return list;
    }

    protected static Pelicula fromJSONObject (JSONObject o) throws NullPointerException, JSONException {
        if (o == null)
            throw new NullPointerException (String.format ("No se puede pasar un JSONObject nulo al método %s.",
                    Thread.currentThread ().getStackTrace () [0].getMethodName ()));

        final Set <String> fields = new HashSet <String> (
                Arrays.asList (new String [] { "id", "nombre", "rutaimagen", "valoracion",
                        "fecha", "director", "duracion", "edad", "generos" }));

        final Map <String, EdadRecomendada> edadMap = ((Supplier <Map <String, EdadRecomendada>>) ( () -> {
            Map <String, EdadRecomendada> map = new HashMap <String, EdadRecomendada> ();

            map.put ("Todas las edades", EdadRecomendada.TODOS);
            map.put ("+7", EdadRecomendada.SIETE);
            map.put ("+12", EdadRecomendada.DOCE);
            map.put ("+16", EdadRecomendada.DIECISEIS);
            map.put ("+18", EdadRecomendada.DIECIOCHO);

            return map;
        })).get ();

        final Map <String, Genero.Nombre> generosMap = ((Supplier <Map <String, Genero.Nombre>>) ( () -> {
            Map <String, Genero.Nombre> map = new HashMap <String, Genero.Nombre> ();

            map.put ("ACCION", Genero.Nombre.ACCION);
            map.put ("CIENCIA_FICCION", Genero.Nombre.CIENCIA_FICCION);
            map.put ("COMEDIA", Genero.Nombre.COMEDIA);
            map.put ("DOCUMENTAL", Genero.Nombre.DOCUMENTAL);
            map.put ("DRAMA", Genero.Nombre.DRAMA);
            map.put ("FANTASIA", Genero.Nombre.FANTASIA);
            map.put ("MELODRAMA", Genero.Nombre.MELODRAMA);
            map.put ("MUSICAL", Genero.Nombre.MUSICAL);
            map.put ("ROMANCE", Genero.Nombre.ROMANCE);
            map.put ("SUSPENSE", Genero.Nombre.SUSPENSE);
            map.put ("TERROR", Genero.Nombre.TERROR);

            return map;
        })).get ();

        String keys[] = o.keySet ().toArray (new String [0]);
        for (int i = 0; i < keys.length; i++)
            if (!fields.contains (keys [i]))
                throw new JSONException (String.format ("JSONObject inválido: clave %s desconocida.", keys [i]));

        UUID id = null;
        String nombre = "";
        String rutaImagen = "";
        double valoracion = 0;
        Year fecha = null;
        String director = "";
        Duration duracion = null;
        EdadRecomendada edad = null;
        Set <Genero.Nombre> generos = new HashSet <Genero.Nombre> ();

        if (o.has ("id"))
            try {
                id = UUID.fromString (o.getString ("id"));
            }

            catch (JSONException | IllegalArgumentException e) {
                Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                        "No se pudo encontrar un ID válido para la película.");
            }

        try {
            nombre = o.getString ("nombre");
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar un nombre válido para la película.");
        }

        try {
            rutaImagen = o.getString ("rutaimagen");
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar una ruta de imágen válida para la película.");
        }

        try {
            valoracion = o.getDouble ("valoracion");
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar una valoración válida para la película.");
        }

        if (o.has ("valoracion") && (((Double) valoracion).isNaN () || valoracion < 1 || valoracion > 10))
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    String.format ("%f no es una valoración válida en el intervalo [1, 10].", valoracion));

        try {
            fecha = Year.of (o.getInt ("fecha"));
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar un año válido para la película.");
        }

        if (o.has ("fecha") && fecha != null && (fecha.compareTo (MIN_FECHA) < 0 || fecha.compareTo (MAX_FECHA) > 0))
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    String.format ("%d no es un año válido en el intervalo [%d, %d].", fecha.getValue (),
                            Pelicula.MIN_FECHA.getValue (), Pelicula.MAX_FECHA.getValue ()));

        try {
            director = o.getString ("director");
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar un director válido para la película.");
        }

        try {
            duracion = Duration.ofMinutes (o.getLong ("duracion"));
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar una duración válida para la película.");
        }

        if (o.has ("duracion") && duracion != null && duracion.toMinutes () <= 0)
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    String.format ("%d no es una duración válida para la película: tiene que ser mayor que cero.",
                            duracion.toMinutes ()));

        try {
            edad = edadMap.get (o.getString ("edad"));
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar una edad recomendada válida para la película.");
        }

        if (o.has ("edad") && edad == null)
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    String.format ("%s no se puede convertir a una edad recomendada válida.", o.get ("edad")));

        try {
            JSONArray generosjson = o.getJSONArray ("generos");

            Genero.Nombre genero;
            String str;
            for (int i = 0; i < generosjson.length ();) {
                if ((genero = generosMap.get (str = generosjson.get (i++).toString ())) == null) {
                    Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                            String.format ("%s no se puede convertir a un género válido.", str));

                    continue;
                }

                generos.add (genero);
            }
        }

        catch (JSONException e) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    "No se pudo encontrar una lista de géneros válida para la película.");
        }

        return new Pelicula (id, nombre, rutaImagen, valoracion, fecha, director, duracion, edad, generos);
    }

    public static String toJSON (Pelicula pelicula) throws NullPointerException {
        return Pelicula.toJSON (Collections.singleton (pelicula), false);
    }

    public static String toJSON (Pelicula pelicula, boolean extra) throws NullPointerException {
        return Pelicula.toJSON (Collections.singleton (pelicula), extra);
    }

    public static String toJSON (Collection <Pelicula> peliculas) throws NullPointerException {
        return Pelicula.toJSON (peliculas, false);
    }

    public static String toJSON (Collection <Pelicula> peliculas, boolean extra) throws NullPointerException {
        if (peliculas == null)
            throw new NullPointerException ("No se puede convertir una coleción nula de películas a JSON.");

        JSONArray json = new JSONArray ();

        Pelicula array[] = new TreeSet <Pelicula> (peliculas).toArray (new Pelicula [0]);
        for (int i = 0; i < array.length; json.put (array [i++].toJSONObject (extra)))
            ;

        StringBuilder str = new StringBuilder (json.toString ().replace ("[", "[\n    ")
                .replace ("[\n    \"", "[\n        \"").replace ("{\"", "{\n\"").replace ("}", "\n    }")
                .replace ("},", "},\n    ").replace ("]", "\n]").replace ("],", "        ],\n")
                .replace (",\"", ",\n\"").replace ("\":", "\" : ").replace ("\n\"", "\n        \""));

        for (int i = 0; (i = str.indexOf ("[\n        ", i)) != -1;)
            for (; i < str.length () && str.charAt (i) != ']'; i++)
                if (str.charAt (i) == '\n' && str.charAt (i + 9) == '"')
                    str.insert (i + 9, "    ");

        return str.toString ();
    }

    private JSONObject toJSONObject () {
        return this.toJSONObject (false);
    }

    private JSONObject toJSONObject (boolean extra) {
        JSONObject o = new JSONObject ().put ("nombre", this.nombre).put ("rutaimagen", this.rutaImagen)
                .put ("valoracion", this.valoracion).put ("fecha", this.fecha.getValue ())
                .put ("director", this.director).put ("duracion", this.duracion.toMinutes ())
                .put ("edad", this.edad.toString ()).put ("generos", this.generos);

        if (extra)
            o.put ("id", this.id.toString ());

        return o;
    }
}
