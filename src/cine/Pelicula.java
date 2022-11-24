package cine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Duration;
import java.time.Year;
import java.time.ZoneId;

public class Pelicula implements Comparable <Pelicula> {
    // Media y desviación estándar de la distribución de las valoraciones de
    // las películas sacadas de los primeros 2²⁰ elementos
    // de la columna averageRating del archivo title.ratings.tsv/data.tsv
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
                        : String.format ("%s%c%s", DEFAULT_MOVIE_IMAGE_PATH, File.separatorChar,
                                new File (e.getFile ()).getName ());
            }).collect (Collectors.toList ()));

    // Flag que especifica si se han descargado o no las imágenes de las
    // películas por defecto
    private static boolean DEFAULT_IMAGES_DOWNLOADED = false;

    // Las películas por defecto (soy consciente de que esto es una guarrada
    // pero vamos a dejarlo así por el momento)
    private static final int NDEFAULT_PELICULAS = 35;
    private static long SET_DEFAULT_PELICULAS = 0;
    private static final SortedSet <Pelicula> DEFAULT_PELICULAS = new TreeSet <Pelicula> (
            Arrays.asList (
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
                                    DEFAULT_MOVIE_IMAGE_FILES.get (32), 8.1,
                                    Year.of (1982),
                                    "Ridley Scott",
                                    Duration.ofMinutes (117), EdadRecomendada.DOCE,
                                    Genero.Nombre.ACCION, Genero.Nombre.DRAMA,
                                    Genero.Nombre.CIENCIA_FICCION,
                                    Genero.Nombre.SUSPENSE),
                            new Pelicula (new UUID (0L, 34L), "A todo gas: Tokyo Race",
                                    DEFAULT_MOVIE_IMAGE_FILES.get (33), 6,
                                    Year.of (2006),
                                    "Justin Lin",
                                    Duration.ofMinutes (104),
                                    EdadRecomendada.DIECIOCHO, Genero.Nombre.ACCION,
                                    Genero.Nombre.SUSPENSE)
                    }));

    protected UUID id;
    protected String nombre;
    protected String rutaImagen;
    protected double valoracion;
    protected Year fecha;
    protected String director;
    protected Duration duracion;
    protected EdadRecomendada edad;
    protected Set <Genero.Nombre> generos;
    protected SortedSet <SetPeliculas> sets;

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

        interface GetPreviousClassName {
            String show (StackTraceElement stackTrace[]);
        }

        this.id = id != null && ((id.getMostSignificantBits () == 0
                && id.getLeastSignificantBits () < Pelicula.NDEFAULT_PELICULAS
                && ((GetPreviousClassName) (st -> {
                    return String.format ("%s", st [1].getClassName ());
                })).show (Thread.currentThread ().getStackTrace ()).equals ("cine.Pelicula"))
                || id.getMostSignificantBits () != 0 || id.getLeastSignificantBits () >= Pelicula.NDEFAULT_PELICULAS)
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

            if (!Pelicula.DEFAULT_IMAGES_DOWNLOADED)
                Pelicula.DEFAULT_IMAGES_DOWNLOADED = Pelicula.downloadDefaultImages ();
        }

        System.out.println (Pelicula.SET_DEFAULT_PELICULAS);
    }

    public Pelicula (Pelicula pelicula) throws NullPointerException {
        this (pelicula.id, pelicula.nombre, pelicula.rutaImagen, pelicula.valoracion, pelicula.fecha,
                pelicula.director,
                pelicula.duracion,
                pelicula.edad, pelicula.generos, pelicula.sets);
    }

    @Override
    public void finalize () throws Throwable {
        this.removeFromSets ();

        if (!this.isDefault ()) {
            super.finalize ();
            return;
        }

        if (new File (DEFAULT_MOVIE_IMAGE_FILES
                .get ((int) this.id.getLeastSignificantBits ()))
                        .delete ())
            Logger.getLogger (Pelicula.class.getName ()).log (Level.INFO,
                    String.format ("Eliminado el archivo %s.", DEFAULT_MOVIE_IMAGE_FILES
                            .get ((int) this.id.getLeastSignificantBits ())));

        else
            Logger.getLogger (Pelicula.class.getName ()).log (Level.WARNING,
                    String.format ("No se pudo eliminar el archivo %s.",
                            DEFAULT_MOVIE_IMAGE_FILES
                                    .get ((int) this.id
                                            .getLeastSignificantBits ())));

        File f;
        if ((f = new File (DEFAULT_MOVIE_IMAGE_PATH)).list () == null && f.delete ())
            Logger.getLogger (Pelicula.class.getName ()).log (Level.INFO,
                    String.format ("Eliminadada la carpeta %s.",
                            DEFAULT_MOVIE_IMAGE_PATH));

        Pelicula.DEFAULT_IMAGES_DOWNLOADED = false;
        Pelicula.SET_DEFAULT_PELICULAS &= ~(1 << this.id.getLeastSignificantBits ());

        super.finalize ();
    }

    public UUID getId () {
        return this.id;
    }

    public String getNombre () {
        return this.nombre;
    }

    public void setNombre (String nombre) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
            return;

        this.nombre = nombre == null || nombre.equals ("") ? this.id.toString ()
                : nombre;
    }

    public String getRutaImagen () {
        return this.rutaImagen;
    }

    public void setRutaImagen (String rutaImagen) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
            return;

        this.rutaImagen = rutaImagen == null ? "" : rutaImagen;
    }

    public double getValoracion () {
        return this.valoracion;
    }

    public void setValoracion (double valoracion) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
            return;

        this.valoracion = ((Double) valoracion).isNaN () || valoracion < 1
                || valoracion > 10 ? Double.NaN : Math.floor (valoracion * 10) / 10;
    }

    public Year getFecha () {
        return this.fecha;
    }

    public void setFecha (int fecha) {
        this.setFecha (Year.of (fecha));
    }

    public void setFecha (Year fecha) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
            return;

        this.fecha = fecha == null || fecha.compareTo (MIN_FECHA) < 0 || fecha.compareTo (MAX_FECHA) > 0 ? null
                : fecha;
    }

    public String getDirector () {
        return this.director;
    }

    public void setDirector (String director) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
            return;

        this.director = director == null ? "" : director;
    }

    public Duration getDuracion () {
        return this.duracion;
    }

    public void setDuracion (Duration duracion) {
        // En estos momentos echo de menos la extensión backtrace () de GNU para
        // C
        interface GetPreviousMethodName {
            String show (StackTraceElement stackTrace[]);
        }

        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
            return;

        this.duracion = duracion == null || duracion.isNegative () || duracion.isZero ()
                ? (this.duracion == null ? Pelicula.DEFAULT_DURACION : this.duracion)
                : ((GetPreviousMethodName) (st ->

                {
                    return String.format ("%s.%s", st [1].getClassName (), st [1].getMethodName ());
                })).show (Thread.currentThread ().getStackTrace ()).equals ("Pelicula")
                        ? Pelicula.DEFAULT_DURACION
                        : Duration.ofMinutes (duracion.toMinutes ());
    }

    public EdadRecomendada getEdad () {
        return this.edad;
    }

    public void setEdad (EdadRecomendada edad) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
            return;

        this.edad = edad == null ? EdadRecomendada.TODOS : edad;
    }

    public Set <Genero.Nombre> getGeneros () {
        return this.generos;
    }

    public void setGeneros (Collection <Genero.Nombre> generos) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
            return;

        this.generos = generos == null || generos.contains (Genero.Nombre.NADA)
                ? new TreeSet <Genero.Nombre> (Collections.singleton (Genero.Nombre.NADA))
                : new TreeSet <Genero.Nombre> (generos);
    }

    public Set <SetPeliculas> getSets () {
        return this.sets;
    }

    public void setSets (Collection <SetPeliculas> sets) {
        if (this.isDefault () && Pelicula.isDefaultSet (this.id.getLeastSignificantBits ()))
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
        if (this.sets == null || set == null)
            return false;

        return this.sets.add (set);
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
        if (this.sets == null || set == null || !this.isInSet (set))
            return false;

        return this.sets.remove (set);
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
        if (this.sets == null)
            return;

        SetPeliculas array[] = this.sets.toArray (new SetPeliculas [0]);
        for (int i = 0; i < array.length; array [i++].remove (this))
            ;
    }

    @Override
    public int hashCode () {
        return Objects.hash (fecha, director, duracion, edad, generos, id, nombre, rutaImagen, valoracion);
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
        if ((comp = this.nombre.compareTo (pelicula.nombre)) != 0)
            return comp;

        return this.id.compareTo (pelicula.id);
    }

    @Override
    public String toString () {
        interface GenerosToString {
            String str (List <Genero.Nombre> generos);
        }

        interface IDSets {
            String str (List <SetPeliculas> sets);
        }

        return "Película (hash: " + this.hashCode () + ") {\n\tID: " + this.id
                + (this.isDefault () ? " (película por defecto)" : "")
                + "\n\tNombre: "
                + this.nombre + "\n\tRuta de la imagen: "
                + this.rutaImagen + "\n\tValoración: " + (((Double) this.valoracion).isNaN ()
                        ? "-"
                        : this.valoracion)
                + "\n\tFecha: " + this.fecha
                + "\n\tDirector: "
                + this.director + "\n\tDuración: "
                + this.duracionToString () + "\n\tEdad: " + this.edad
                + "\n\tGéneros: " + ((GenerosToString) (g -> {
                    StringBuilder str = new StringBuilder ();
                    for (int i = 0; i < g.size (); i++)
                        str.append (String.format ("%s%s",
                                g.get (i).toString (),
                                i != g.size () - 1
                                        ? " · "
                                        : ""));
                    return str.toString ();
                })).str (this.generos.stream ()
                        .collect (Collectors.toList ()))
                + "\n\tSets: " + (this.sets.isEmpty () ? "-" : ((IDSets) (s -> {
                    StringBuilder str = new StringBuilder ();
                    for (int i = 0; i < s.size (); i++)
                        str.append (String.format ("%s%s",
                                s.get (i).getId ().toString (),
                                i != s.size () - 1
                                        ? " · "
                                        : ""));
                    return str.toString ();
                })).str (this.sets.stream ().collect (Collectors.toList ())))
                + "\n}";
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
        return this.id != null && this.id.getMostSignificantBits () == 0
                && this.id.getLeastSignificantBits () < Pelicula.NDEFAULT_PELICULAS;
    }

    public static SortedSet <Pelicula> getDefault () {
        return new TreeSet <Pelicula> (Pelicula.DEFAULT_PELICULAS);
    }

    protected static boolean downloadDefaultImages () {
        File fp = new File (DEFAULT_MOVIE_IMAGE_PATH);

        if (fp.exists () && Arrays.asList (fp.list ())
                .containsAll (DEFAULT_MOVIE_IMAGE_FILES.stream ().map (e -> {
                    return e.replace (DEFAULT_MOVIE_IMAGE_PATH + File.separator, "");
                }).collect (Collectors.toList ()))) {
            Logger.getLogger (Pelicula.class.getName ()).log (Level.INFO,
                    "Todas las imágenes de las películas por defecto estaban ya descargadas.");

            return true;
        }

        fp.mkdirs ();

        List <String> errors = new ArrayList <String> ();

        for (int i = 0; i < DEFAULT_MOVIE_IMAGE_FILES.size (); i++) {
            if ((fp = new File (DEFAULT_MOVIE_IMAGE_FILES.get (i))).exists ())
                continue;

            try (FileOutputStream f = new FileOutputStream (fp);
                    ReadableByteChannel c = Channels
                            .newChannel (DEFAULT_MOVIE_IMAGE_URLS.get (i).openStream ())) {
                f.getChannel ().transferFrom (c, 0, Long.MAX_VALUE);
            }

            catch (Exception e) {
                errors.add (String.format (
                        "%s: %s", DEFAULT_MOVIE_IMAGE_FILES.get (i)
                                .replace (DEFAULT_MOVIE_IMAGE_PATH + File.separator,
                                        ""),
                        e.getMessage ()));
            }
        }

        Logger.getLogger (Pelicula.class.getName ()).log (
                errors.isEmpty () ? Level.FINE : Level.WARNING,
                String.format ("%sas mágenes de las películas por defecto fueron descargadas.%s",
                        errors.isEmpty () ? "L" : "No todas l",
                        errors.isEmpty () ? ""
                                : String.format ("\nFaltaron: %s",
                                        errors.toString ())));

        return !errors.isEmpty ();
    }

    public static void deleteDefaultPeliculasData () throws Throwable {
        Pelicula [] peliculas = DEFAULT_PELICULAS.toArray (new Pelicula [0]);

        for (int i = 0; i < peliculas.length; peliculas [i++].finalize ())
            ;
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
        return new Pelicula (nombre, Pelicula.random.nextGaussian (Pelicula.RANDOMAVG, Pelicula.RANDOMSTDEV),
                EdadRecomendada.random (), Genero.randomGeneros ());
    }

    public static List <String> getNombres (Collection <Pelicula> peliculas) {
        ArrayList <String> nombres = new ArrayList <String> ();

        List <Pelicula> list = peliculas.stream ().collect (Collectors.toList ());
        for (int i = 0; i < list.size (); nombres.add (list.get (i++).getNombre ()))
            ;

        return nombres;
    }
}
