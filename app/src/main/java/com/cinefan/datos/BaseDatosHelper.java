package com.cinefan.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Clase auxiliar para la gestión de la base de datos SQLite
 */
public class BaseDatosHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "cinefan.db";
    private static final int VERSION_BD = 1;

    // Nombres de tablas
    public static final String TABLA_PELICULAS = "peliculas";
    public static final String TABLA_ACTORES = "actores";
    public static final String TABLA_LISTAS = "listas";
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_PELICULA_ACTOR = "pelicula_actor";
    public static final String TABLA_PELICULA_LISTA = "pelicula_lista";

    // Columnas comunes
    public static final String COLUMNA_ID = "id";
    
    // Columnas tabla PELICULAS
    public static final String COLUMNA_TITULO = "titulo";
    public static final String COLUMNA_ANIO = "anio";
    public static final String COLUMNA_DIRECTOR = "director";
    public static final String COLUMNA_GENERO = "genero";
    public static final String COLUMNA_SINOPSIS = "sinopsis";
    public static final String COLUMNA_VALORACION = "valoracion";
    public static final String COLUMNA_IMAGEN_RUTA = "imagen_ruta";
    public static final String COLUMNA_ARCHIVO_AUDIO = "archivo_audio";
    
    // Columnas tabla ACTORES
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_NACIONALIDAD = "nacionalidad";
    public static final String COLUMNA_FECHA_NACIMIENTO = "fecha_nacimiento";
    
    // Columnas tabla LISTAS
    public static final String COLUMNA_DESCRIPCION = "descripcion";
    public static final String COLUMNA_FECHA_CREACION = "fecha_creacion";
    
    // Columnas tabla USUARIOS
    public static final String COLUMNA_NOMBRE_USUARIO = "nombre_usuario";
    public static final String COLUMNA_CONTRASENA = "contrasena";
    public static final String COLUMNA_CORREO = "correo";
    
    // Columnas tabla PELICULA_ACTOR
    public static final String COLUMNA_PELICULA_ID = "pelicula_id";
    public static final String COLUMNA_ACTOR_ID = "actor_id";
    public static final String COLUMNA_PERSONAJE = "personaje";
    
    // Columnas tabla PELICULA_LISTA
    public static final String COLUMNA_LISTA_ID = "lista_id";
    public static final String COLUMNA_FECHA_AGREGADO = "fecha_agregado";

    // SQL para crear las tablas
    private static final String SQL_CREAR_TABLA_PELICULAS =
            "CREATE TABLE " + TABLA_PELICULAS + " (" +
                    COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMNA_TITULO + " TEXT NOT NULL," +
                    COLUMNA_ANIO + " INTEGER," +
                    COLUMNA_DIRECTOR + " TEXT," +
                    COLUMNA_GENERO + " TEXT," +
                    COLUMNA_SINOPSIS + " TEXT," +
                    COLUMNA_VALORACION + " REAL," +
                    COLUMNA_IMAGEN_RUTA + " TEXT," +
                    COLUMNA_ARCHIVO_AUDIO + " TEXT)";

    private static final String SQL_CREAR_TABLA_ACTORES =
            "CREATE TABLE " + TABLA_ACTORES + " (" +
                    COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMNA_NOMBRE + " TEXT NOT NULL," +
                    COLUMNA_NACIONALIDAD + " TEXT," +
                    COLUMNA_FECHA_NACIMIENTO + " TEXT)";

    private static final String SQL_CREAR_TABLA_LISTAS =
            "CREATE TABLE " + TABLA_LISTAS + " (" +
                    COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMNA_NOMBRE + " TEXT NOT NULL," +
                    COLUMNA_DESCRIPCION + " TEXT," +
                    COLUMNA_FECHA_CREACION + " TEXT)";

    private static final String SQL_CREAR_TABLA_USUARIOS =
            "CREATE TABLE " + TABLA_USUARIOS + " (" +
                    COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMNA_NOMBRE_USUARIO + " TEXT NOT NULL UNIQUE," +
                    COLUMNA_CONTRASENA + " TEXT NOT NULL," +
                    COLUMNA_CORREO + " TEXT UNIQUE)";

    private static final String SQL_CREAR_TABLA_PELICULA_ACTOR =
            "CREATE TABLE " + TABLA_PELICULA_ACTOR + " (" +
                    COLUMNA_PELICULA_ID + " INTEGER," +
                    COLUMNA_ACTOR_ID + " INTEGER," +
                    COLUMNA_PERSONAJE + " TEXT," +
                    "PRIMARY KEY (" + COLUMNA_PELICULA_ID + ", " + COLUMNA_ACTOR_ID + ")," +
                    "FOREIGN KEY (" + COLUMNA_PELICULA_ID + ") REFERENCES " + TABLA_PELICULAS + "(" + COLUMNA_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY (" + COLUMNA_ACTOR_ID + ") REFERENCES " + TABLA_ACTORES + "(" + COLUMNA_ID + ") ON DELETE CASCADE)";

    private static final String SQL_CREAR_TABLA_PELICULA_LISTA =
            "CREATE TABLE " + TABLA_PELICULA_LISTA + " (" +
                    COLUMNA_PELICULA_ID + " INTEGER," +
                    COLUMNA_LISTA_ID + " INTEGER," +
                    COLUMNA_FECHA_AGREGADO + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (" + COLUMNA_PELICULA_ID + ", " + COLUMNA_LISTA_ID + ")," +
                    "FOREIGN KEY (" + COLUMNA_PELICULA_ID + ") REFERENCES " + TABLA_PELICULAS + "(" + COLUMNA_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY (" + COLUMNA_LISTA_ID + ") REFERENCES " + TABLA_LISTAS + "(" + COLUMNA_ID + ") ON DELETE CASCADE)";

    public BaseDatosHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREAR_TABLA_PELICULAS);
            db.execSQL(SQL_CREAR_TABLA_ACTORES);
            db.execSQL(SQL_CREAR_TABLA_LISTAS);
            db.execSQL(SQL_CREAR_TABLA_USUARIOS);
            db.execSQL(SQL_CREAR_TABLA_PELICULA_ACTOR);
            db.execSQL(SQL_CREAR_TABLA_PELICULA_LISTA);
            
            // Insertar datos de ejemplo
            insertarDatosEjemplo(db);
            
        } catch (Exception e) {
            Log.e("BaseDatosHelper", "Error al crear las tablas", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En caso de actualización de la base de datos
        // Aquí se añadirían las migraciones necesarias
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PELICULA_LISTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PELICULA_ACTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_LISTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_ACTORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PELICULAS);
        onCreate(db);
    }
    
    private void insertarDatosEjemplo(SQLiteDatabase db) {
        // Insertar algunas películas de ejemplo
        db.execSQL("INSERT INTO " + TABLA_PELICULAS + " (" +
                COLUMNA_TITULO + ", " +
                COLUMNA_ANIO + ", " +
                COLUMNA_DIRECTOR + ", " +
                COLUMNA_GENERO + ", " +
                COLUMNA_SINOPSIS + ", " +
                COLUMNA_VALORACION + ", " +
                COLUMNA_IMAGEN_RUTA + ", " +
                COLUMNA_ARCHIVO_AUDIO + ") VALUES " +
                "('El Padrino', 1972, 'Francis Ford Coppola', 'Drama', " +
                "'La historia de la familia mafiosa Corleone.', 9.2, 'padrino.jpg', 'padrino_theme.mp3')");
                
        db.execSQL("INSERT INTO " + TABLA_PELICULAS + " (" +
                COLUMNA_TITULO + ", " +
                COLUMNA_ANIO + ", " +
                COLUMNA_DIRECTOR + ", " +
                COLUMNA_GENERO + ", " +
                COLUMNA_SINOPSIS + ", " +
                COLUMNA_VALORACION + ", " +
                COLUMNA_IMAGEN_RUTA + ", " +
                COLUMNA_ARCHIVO_AUDIO + ") VALUES " +
                "('Pulp Fiction', 1994, 'Quentin Tarantino', 'Crimen', " +
                "'Las vidas de dos mafiosos, un boxeador, la esposa de un gánster y un par de bandidos se entrelazan.', 8.9, 'pulp_fiction.jpg', 'pulp_fiction_theme.mp3')");
                
        // Insertar listas predeterminadas
        db.execSQL("INSERT INTO " + TABLA_LISTAS + " (" +
                COLUMNA_NOMBRE + ", " +
                COLUMNA_DESCRIPCION + ", " +
                COLUMNA_FECHA_CREACION + ") VALUES " +
                "('Favoritas', 'Mis películas favoritas', CURRENT_TIMESTAMP)");
                
        db.execSQL("INSERT INTO " + TABLA_LISTAS + " (" +
                COLUMNA_NOMBRE + ", " +
                COLUMNA_DESCRIPCION + ", " +
                COLUMNA_FECHA_CREACION + ") VALUES " +
                "('Por ver', 'Películas que quiero ver', CURRENT_TIMESTAMP)");
    }
}
