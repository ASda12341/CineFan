package com.cinefan.utilidades;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.cinefan.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Clase utilitaria para gestionar elementos multimedia (imágenes, audio)
 */
public class GestorMultimedia {

    private static final String TAG = "GestorMultimedia";
    private static MediaPlayer reproductor;

    /**
     * Carga una imagen en un ImageView desde una ruta
     * @param contexto Contexto de la aplicación
     * @param ruta Ruta de la imagen
     * @param imageView ImageView donde cargar la imagen
     */
    public static void cargarImagen(Context contexto, String ruta, ImageView imageView) {
        if (ruta == null || ruta.isEmpty()) {
            // Imagen por defecto
            imageView.setImageResource(R.drawable.pelicula_default);
            return;
        }
        
        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                // Cargar desde archivo
                Bitmap bitmap = BitmapFactory.decodeFile(ruta);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    // Si no se puede cargar, usar imagen por defecto
                    imageView.setImageResource(R.drawable.pelicula_default);
                }
            } else {
                // Intentar cargar desde URI
                Uri uri = Uri.parse(ruta);
                imageView.setImageURI(uri);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar imagen: " + e.getMessage());
            // En caso de error, usar imagen por defecto
            imageView.setImageResource(R.drawable.pelicula_default);
        }
    }

    /**
     * Obtiene la ruta de archivo desde un URI
     * @param contexto Contexto de la aplicación
     * @param uri URI del archivo
     * @return Ruta del archivo o null si hay error
     */
    public static String obtenerRutaDesdeUri(Context contexto, Uri uri) {
        // Comprobar si es un archivo del sistema de archivos
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        
        // Obtener ruta de archivo para imágenes
        if (uri.getAuthority().contains("media")) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = contexto.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    return cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al obtener ruta desde URI: " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        
        // Si no se puede determinar la ruta, copiar el archivo a una ubicación interna
        return copiarArchivoDesdeUri(contexto, uri);
    }

    /**
     * Copia un archivo desde un URI a una ubicación interna de la aplicación
     * @param contexto Contexto de la aplicación
     * @param uri URI del archivo a copiar
     * @return Ruta del archivo copiado o null si hay error
     */
    private static String copiarArchivoDesdeUri(Context contexto, Uri uri) {
        String nombreArchivo = obtenerNombreArchivoDesdeUri(contexto, uri);
        if (nombreArchivo == null) {
            nombreArchivo = "archivo_" + System.currentTimeMillis();
        }
        
        File archivoDestino = new File(contexto.getFilesDir(), nombreArchivo);
        
        try (InputStream entrada = contexto.getContentResolver().openInputStream(uri);
             OutputStream salida = new FileOutputStream(archivoDestino)) {
            
            if (entrada == null) {
                Log.e(TAG, "Error al abrir stream de entrada desde URI");
                return null;
            }
            
            byte[] buffer = new byte[4096];
            int bytesLeidos;
            while ((bytesLeidos = entrada.read(buffer)) != -1) {
                salida.write(buffer, 0, bytesLeidos);
            }
            
            return archivoDestino.getAbsolutePath();
            
        } catch (IOException e) {
            Log.e(TAG, "Error al copiar archivo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el nombre del archivo desde un URI
     * @param contexto Contexto de la aplicación
     * @param uri URI del archivo
     * @return Nombre del archivo o null si no se puede determinar
     */
    private static String obtenerNombreArchivoDesdeUri(Context contexto, Uri uri) {
        String resultado = null;
        
        if (uri.getScheme().equals("content")) {
            Cursor cursor = contexto.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int indice = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    if (indice != -1) {
                        resultado = cursor.getString(indice);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        
        if (resultado == null) {
            resultado = uri.getLastPathSegment();
        }
        
        return resultado;
    }
    
    /**
     * Reproduce un archivo de audio
     * @param contexto Contexto de la aplicación
     * @param rutaAudio Ruta del archivo de audio
     * @param repetir Indica si se debe reproducir en bucle
     */
    public static void reproducirAudio(Context contexto, String rutaAudio, boolean repetir) {
        // Detener reproducción previa si existe
        detenerAudio();
        
        if (rutaAudio == null || rutaAudio.isEmpty()) {
            return;
        }
        
        try {
            File archivo = new File(rutaAudio);
            if (archivo.exists()) {
                // Reproducir desde archivo
                reproductor = new MediaPlayer();
                reproductor.setDataSource(rutaAudio);
                reproductor.prepare();
                reproductor.setLooping(repetir);
                reproductor.start();
            } else {
                // Intentar reproducir desde Uri
                Uri uri = Uri.parse(rutaAudio);
                reproductor = MediaPlayer.create(contexto, uri);
                if (reproductor != null) {
                    reproductor.setLooping(repetir);
                    reproductor.start();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al reproducir audio: " + e.getMessage());
        }
    }
    
    /**
     * Detiene la reproducción de audio
     */
    public static void detenerAudio() {
        if (reproductor != null) {
            if (reproductor.isPlaying()) {
                reproductor.stop();
            }
            reproductor.release();
            reproductor = null;
        }
    }
}
