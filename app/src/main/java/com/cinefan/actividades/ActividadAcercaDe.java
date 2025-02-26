package com.cinefan.actividades;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cinefan.R;

/**
 * Actividad que muestra información acerca de la aplicación
 */
public class ActividadAcercaDe extends AppCompatActivity {

    private TextView txtAcercaDe;
    private MediaPlayer reproductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_acerca_de);
        
        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.titulo_acerca_de);
        
        // Inicializar vistas
        txtAcercaDe = findViewById(R.id.txtAcercaDe);
        
        // Cargar texto de acerca de
        txtAcercaDe.setText(R.string.acerca_de_contenido);
        
        // Reproducir música de fondo
        iniciarMusicaFondo();
    }
    
    /**
     * Inicia la reproducción de música de fondo
     */
    private void iniciarMusicaFondo() {
        // Reproducir música de fondo (R.raw.tema_acerca_de)
        reproductor = MediaPlayer.create(this, R.raw.tema_acerca_de);
        reproductor.setLooping(true); // Reproducción en bucle
        reproductor.setVolume(0.5f, 0.5f); // Volumen al 50%
        reproductor.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (reproductor != null && reproductor.isPlaying()) {
            reproductor.pause();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (reproductor != null && !reproductor.isPlaying()) {
            reproductor.start();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }
    }
}
