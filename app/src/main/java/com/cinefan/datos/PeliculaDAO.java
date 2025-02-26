package com.cinefan.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cinefan.modelo.Pelicula;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para el acceso a datos de películas
 */
public class PeliculaDAO {
    private static final String TAG = "PeliculaDAO";
    
    private SQLiteDatabase database;
    private BaseDatosHelper dbHelper;
    
    // Columnas seleccionadas en consultas
    private String[] todasLasColumnas = {
            BaseDatosHelper.COLUMNA_ID,
            BaseDatosHelper.COLUMNA_TITULO,
            BaseDatosHelper.COLUMNA_ANIO,
            BaseDatosHelper.COLUMNA_DIRECTOR,
            BaseDatosHelper.COLUMNA_GENERO,
            BaseDatosHelper.COLUMNA_SINOPSIS,
            BaseDatosHelper.COLUMNA_VALORACION,
            BaseDatosHelper.COLUMNA_IMAGEN_RUTA,
            BaseDatosHelper.COLUMNA_ARCHIVO_AUDIO
    };

    public PeliculaDAO(Context context) {
        dbHelper = new BaseDatosHelper(context);
    }

    public void abrir() {
        database = dbHelper.getWritableDatabase();
    }

    public void cerrar() {
        dbHelper.close();
    }

    /**
     * Inserta una nueva película en la base de datos
     * @param pelicula Objeto Pelicula a insertar
     * @return Pelicula con ID asignado
     */
    public Pelicula insertar(Pelicula pelicula) {
        try {
            ContentValues valores = new ContentValues();
            valores.put(BaseDatosHelper.COLUMNA_TITULO, pelicula.getTitulo());
            valores.put(BaseDatosHelper.COLUMNA_ANIO, pelicula.getAnio());
            valores.put(BaseDatosHelper.COLUMNA_DIRECTOR, pelicula.getDirector());
            valores.put(BaseDatosHelper.COLUMNA_GENERO, pelicula.getGenero());
            valores.put(BaseDatosHelper.COLUMNA_SINOPSIS, pelicula.getSinopsis());
            valores.put(BaseDatosHelper.COLUMNA_VALORACION, pelicula.getValoracion());
            valores.put(BaseDatosHelper.COLUMNA_IMAGEN_RUTA, pelicula.getImagenRuta());
            valores.put(BaseDatosHelper.COLUMNA_ARCHIVO_AUDIO, pelicula.getArchivoAudio());
            
            long insertId = database.insert(BaseDatosHelper.TABLA_PELICULAS, null, valores);
            pelicula.setId((int) insertId);
            
            return pelicula;
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar película", e);
            return null;
        }
    }

    /**
     * Actualiza los datos de una película existente
     * @param pelicula Objeto Pelicula con datos actualizados
     * @return Número de filas afectadas
     */
    public int actualizar(Pelicula pelicula) {
        try {
            ContentValues valores = new ContentValues();
            valores.put(BaseDatosHelper.COLUMNA_TITULO, pelicula.getTitulo());
            valores.put(BaseDatosHelper.COLUMNA_ANIO, pelicula.getAnio());
            valores.put(BaseDatosHelper.COLUMNA_DIRECTOR, pelicula.getDirector());
            valores.put(BaseDatosHelper.COLUMNA_GENERO, pelicula.getGenero());
            valores.put(BaseDatosHelper.COLUMNA_SINOPSIS, pelicula.getSinopsis());
            valores.put(BaseDatosHelper.COLUMNA_VALORACION, pelicula.getValoracion());
            valores.put(BaseDatosHelper.COLUMNA_IMAGEN_RUTA, pelicula.getImagenRuta());
            valores.put(BaseDatosHelper.COLUMNA_ARCHIVO_AUDIO, pelicula.getArchivoAudio());
            
            String whereClause = BaseDatosHelper.COLUMNA_ID + " = ?";
            String[] whereArgs = { String.valueOf(pelicula.getId()) };
            
            return database.update(BaseDatosHelper.TABLA_PELICULAS, valores, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar película", e);
            return 0;
        }
    }

    /**
     * Elimina una película de la base de datos
     * @param id ID de la película a eliminar
     * @return Número de filas afectadas
     */
    public int eliminar(int id) {
        try {
            String whereClause = BaseDatosHelper.COLUMNA_ID + " = ?";
            String[] whereArgs = { String.valueOf(id) };
            
            return database.delete(BaseDatosHelper.TABLA_PELICULAS, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar película", e);
            return 0;
        }
    }

    /**
     * Obtiene una película por su ID
     * @param id ID de la película
     * @return Objeto Pelicula o null si no existe
     */
    public Pelicula obtenerPorId(int id) {
        try {
            String whereClause = BaseDatosHelper.COLUMNA_ID + " = ?";
            String[] whereArgs = { String.valueOf(id) };
            
            Cursor cursor = database.query(
                    BaseDatosHelper.TABLA_PELICULAS,
                    todasLasColumnas,
                    whereClause,
                    whereArgs,
                    null, null, null);
            
            Pelicula pelicula = null;
            if (cursor.moveToFirst()) {
                pelicula = cursorAPelicula(cursor);
            }
            
            cursor.close();
            return pelicula;
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener película por ID", e);
            return null;
        }
    }

    /**
     * Obtiene todas las películas
     * @return Lista de objetos Pelicula
     */
    public List<Pelicula> obtenerTodas() {
        try {
            List<Pelicula> peliculas = new ArrayList<>();
            
            Cursor cursor = database.query(
                    BaseDatosHelper.TABLA_PELICULAS,
                    todasLasColumnas,
                    null, null, null, null,
                    BaseDatosHelper.COLUMNA_TITULO + " ASC");
            
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Pelicula pelicula = cursorAPelicula(cursor);
                peliculas.add(pelicula);
                cursor.moveToNext();
            }
            
            cursor.close();
            return peliculas;
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener todas las películas", e);
            return new ArrayList<>();
        }
    }

    /**
     * Busca películas por título
     * @param consulta Texto a buscar en el título
     * @return Lista de objetos Pelicula que coinciden
     */
    public List<Pelicula> buscarPorTitulo(String consulta) {
        try {
            List<Pelicula> peliculas = new ArrayList<>();
            
            String whereClause = BaseDatosHelper.COLUMNA_TITULO + " LIKE ?";
            String[] whereArgs = { "%" + consulta + "%" };
            
            Cursor cursor = database.query(
                    BaseDatosHelper.TABLA_PELICULAS,
                    todasLasColumnas,
                    whereClause,
                    whereArgs,
                    null, null,
                    BaseDatosHelper.COLUMNA_TITULO + " ASC");
            
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Pelicula pelicula = cursorAPelicula(cursor);
                peliculas.add(pelicula);
                cursor.moveToNext();
            }
            
            cursor.close();
            return peliculas;
        } catch (Exception e) {
            Log.e(TAG, "Error al buscar películas por título", e);
            return new ArrayList<>();
        }
    }

    /**
     * Busca películas por género
     * @param genero Género a buscar
     * @return Lista de objetos Pelicula que coinciden
     */
    public List<Pelicula> buscarPorGenero(String genero) {
        try {
            List<Pelicula> peliculas = new ArrayList<>();
            
            String whereClause = BaseDatosHelper.COLUMNA_GENERO + " = ?";
            String[] whereArgs = { genero };
            
            Cursor cursor = database.query(
                    BaseDatosHelper.TABLA_PELICULAS,
                    todasLasColumnas,
                    whereClause,
                    whereArgs,
                    null, null,
                    BaseDatosHelper.COLUMNA_TITULO + " ASC");
            
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Pelicula pelicula = cursorAPelicula(cursor);
                peliculas.add(pelicula);
                cursor.moveToNext();
            }
            
            cursor.close();
            return peliculas;
        } catch (Exception e) {
            Log.e(TAG, "Error al buscar películas por género", e);
            return new ArrayList<>();
        }
    }

    /**
     * Convierte un cursor a un objeto Pelicula
     * @param cursor Cursor de la base de datos
     * @return Objeto Pelicula con datos del cursor
     */
    private Pelicula cursorAPelicula(Cursor cursor) {
        Pelicula pelicula = new Pelicula();
        
        pelicula.setId(cursor.getInt(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_ID)));
        pelicula.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_TITULO)));
        pelicula.setAnio(cursor.getInt(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_ANIO)));
        pelicula.setDirector(cursor.getString(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_DIRECTOR)));
        pelicula.setGenero(cursor.getString(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_GENERO)));
        pelicula.setSinopsis(cursor.getString(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_SINOPSIS)));
        pelicula.setValoracion(cursor.getFloat(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_VALORACION)));
        pelicula.setImagenRuta(cursor.getString(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_IMAGEN_RUTA)));
        pelicula.setArchivoAudio(cursor.getString(cursor.getColumnIndexOrThrow(BaseDatosHelper.COLUMNA_ARCHIVO_AUDIO)));
        
        return pelicula;
    }
}
