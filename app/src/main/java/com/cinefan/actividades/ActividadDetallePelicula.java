package com.cinefan.actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cinefan.R;
import com.cinefan.datos.PeliculaDAO;
import com.cinefan.modelo.Pelicula;
import com.cinefan.utilidades.GestorMultimedia;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Actividad que muestra los detalles de una película
 */
public class ActividadDetallePelicula extends AppCompatActivity {

    private static final int SOLICITUD_EDITAR_PELICULA = 100;

    private ImageView imagenPelicula;
    private TextView txtTitulo;
    private TextView txtAnio;
    private TextView txtDirector;
    private TextView txtGenero;
    private TextView txtSinopsis;
    private RatingBar ratingValoracion;
    private FloatingActionButton fabReproducir;
    private FloatingActionButton fabDetener;
    
    private PeliculaDAO peliculaDAO;
    private Pelicula pelicula;
    private int peliculaId;
    private MediaPlayer reproductor;
    private boolean reproduciendo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_detalle_pelicula);
        
        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Inicializar vistas
        imagenPelicula = findViewById(R.id.imagenPelicula);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtAnio = findViewById(R.id.txtAnio);
        txtDirector = findViewById(R.id.txtDirector);
        txtGenero = findViewById(R.id.txtGenero);
        txtSinopsis = findViewById(R.id.txtSinopsis);
        ratingValoracion = findViewById(R.id.ratingValoracion);
        fabReproducir = findViewById(R.id.fabReproducir);
        fabDetener = findViewById(R.id.fabDetener);
        
        // Obtener ID de la película
        Intent intent = getIntent();
        peliculaId = intent.getIntExtra("pelicula_id", -1);
        
        if (peliculaId == -1) {
            // Error: no se especificó ID
            Toast.makeText(this, R.string.error_cargar_pelicula, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Inicializar DAO
        peliculaDAO = new PeliculaDAO(this);
        peliculaDAO.abrir();
        
        // Cargar datos de la película
        cargarPelicula();
        
        // Configurar eventos
        configurarEventos();
    }
    
    /**
     * Carga los datos de la película
     */
    private void cargarPelicula() {
        pelicula = peliculaDAO.obtenerPorId(peliculaId);
        
        if (pelicula == null) {
            // Error: no se encontró la película
            Toast.makeText(this, R.string.error_cargar_pelicula, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Mostrar datos en la UI
        setTitle(pelicula.getTitulo());
        txtTitulo.setText(pelicula.getTitulo());
        txtAnio.setText(String.valueOf(pelicula.getAnio()));
        txtDirector.setText(pelicula.getDirector());
        txtGenero.setText(pelicula.getGenero());
        txtSinopsis.setText(pelicula.getSinopsis());
        ratingValoracion.setRating(pelicula.getValoracion() / 2); // Escala 0-10 a 0-5
        
        // Cargar imagen
        if (pelicula.getImagenRuta() != null && !pelicula.getImagenRuta().isEmpty()) {
            GestorMultimedia.cargarImagen(this, pelicula.getImagenRuta(), imagenPelicula);
        }
        
        // Configurar botones de audio según disponibilidad
        if (pelicula.getArchivoAudio() != null && !pelicula.getArchivoAudio().isEmpty()) {
            fabReproducir.setVisibility(View.VISIBLE);
            fabDetener.setVisibility(View.GONE);
        } else {
            fabReproducir.setVisibility(View.GONE);
            fabDetener.setVisibility(View.GONE);
        }
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        // Botón de reproducir audio
        fabReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
            }
        });
        
        // Botón de detener audio
        fabDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerAudio();
            }
        });
    }
    
    /**
     * Reproduce el audio de la película
     */
    private void reproducirAudio() {
        if (pelicula.getArchivoAudio() == null || pelicula.getArchivoAudio().isEmpty()) {
            return;
        }
        
        try {
            // Detener reproducción previa si existe
            if (reproductor != null) {
                reproductor.release();
                reproductor = null;
            }
            
            // Iniciar reproducción
            GestorMultimedia.reproducirAudio(this, pelicula.getArchivoAudio(), false);
            reproduciendo = true;
            
            // Mostrar botón de detener y ocultar botón de reproducir
            fabReproducir.setVisibility(View.GONE);
            fabDetener.setVisibility(View.VISIBLE);
            
            // Detectar cuando termina la reproducción
            reproductor = new MediaPlayer();
            reproductor.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    reproduciendo = false;
                    fabReproducir.setVisibility(View.VISIBLE);
                    fabDetener.setVisibility(View.GONE);
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Detiene la reproducción de audio
     */
    private void detenerAudio() {
        if (reproduciendo) {
            GestorMultimedia.detenerAudio();
            reproduciendo = false;
            
            // Mostrar botón de reproducir y ocultar botón de detener
            fabReproducir.setVisibility(View.VISIBLE);
            fabDetener.setVisibility(View.GONE);
        }
    }
    
    /**
     * Abre la actividad para editar la película
     */
    private void editarPelicula() {
        Intent intent = new Intent(this, ActividadNuevaPelicula.class);
        intent.putExtra("pelicula_id", peliculaId);
        startActivityForResult(intent, SOLICITUD_EDITAR_PELICULA);
    }
    
    /**
     * Muestra diálogo de confirmación para eliminar la película
     */
    private void confirmarEliminarPelicula() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(pelicula.getTitulo());
        builder.setMessage(R.string.msg_confirmacion_eliminar);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarPelicula();
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }
    
    /**
     * Elimina la película actual
     */
    private void eliminarPelicula() {
        int filasEliminadas = peliculaDAO.eliminar(peliculaId);
        
        if (filasEliminadas > 0) {
            Toast.makeText(this, R.string.msg_pelicula_eliminada, Toast.LENGTH_SHORT).show();
            
            // Establecer resultado y finalizar actividad
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, R.string.error_eliminar_pelicula, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_pelicula, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_editar) {
            editarPelicula();
            return true;
        } else if (id == R.id.action_eliminar) {
            confirmarEliminarPelicula();
            return true;
        } else if (id == R.id.action_compartir) {
            compartirPelicula();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Comparte información sobre la película
     */
    private void compartirPelicula() {
        String mensaje = pelicula.getTitulo() + " (" + pelicula.getAnio() + ")\n" +
                getString(R.string.lbl_director) + ": " + pelicula.getDirector() + "\n" +
                getString(R.string.lbl_valoracion) + ": " + pelicula.getValoracion() + "/10";
        
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, pelicula.getTitulo());
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);
        
        startActivity(Intent.createChooser(intent, getString(R.string.btn_compartir)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Recargar datos si volvemos de editar la película
        if (requestCode == SOLICITUD_EDITAR_PELICULA && resultCode == RESULT_OK) {
            cargarPelicula();
            setResult(RESULT_OK);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detener reproducción al salir de la actividad
        detenerAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }
        
        // Cerrar conexión a la base de datos
        peliculaDAO.cerrar();
    }
}
