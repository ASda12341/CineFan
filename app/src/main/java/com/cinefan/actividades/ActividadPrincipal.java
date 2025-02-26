package com.cinefan.actividades;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cinefan.R;
import com.cinefan.utilidades.ReceptorEventos;

/**
 * Actividad principal de la aplicación
 */
public class ActividadPrincipal extends AppCompatActivity {

    private Button btnCatalogo;
    private Button btnNuevaPelicula;
    private Button btnListas;
    private Button btnBuscar;
    private Button btnAcercaDe;
    private TextView txtDescripcion;
    
    private MediaPlayer reproductorFondo;
    private BroadcastReceiver receptorEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        
        // Inicializar componentes UI
        btnCatalogo = findViewById(R.id.btnCatalogo);
        btnNuevaPelicula = findViewById(R.id.btnNuevaPelicula);
        btnListas = findViewById(R.id.btnListas);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAcercaDe = findViewById(R.id.btnAcercaDe);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        
        // Configurar evento click para los botones
        configurarBotones();
        
        // Inicializar reproductor de música
        iniciarMusicaFondo();
        
        // Registrar receptor de eventos (llamadas, mensajes)
        registrarReceptorEventos();
    }

    /**
     * Configura los eventos click de los botones
     */
    private void configurarBotones() {
        btnCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir actividad de lista de películas
                Intent intent = new Intent(ActividadPrincipal.this, ActividadListaPeliculas.class);
                startActivity(intent);
            }
        });
        
        btnNuevaPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir actividad para agregar nueva película
                Intent intent = new Intent(ActividadPrincipal.this, ActividadNuevaPelicula.class);
                startActivity(intent);
            }
        });
        
        btnListas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir actividad de gestión de listas
                Intent intent = new Intent(ActividadPrincipal.this, ActividadGestionListas.class);
                startActivity(intent);
            }
        });
        
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir actividad de búsqueda
                // Por simplicidad, podemos usar la misma actividad de lista con un parámetro
                Intent intent = new Intent(ActividadPrincipal.this, ActividadListaPeliculas.class);
                intent.putExtra("modo_busqueda", true);
                startActivity(intent);
            }
        });
        
        btnAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir actividad acerca de
                Intent intent = new Intent(ActividadPrincipal.this, ActividadAcercaDe.class);
                startActivity(intent);
            }
        });
    }
    
    /**
     * Inicia la reproducción de música de fondo
     */
    private void iniciarMusicaFondo() {
        // Reproducir música de fondo (R.raw.tema_principal)
        reproductorFondo = MediaPlayer.create(this, R.raw.tema_principal);
        reproductorFondo.setLooping(true); // Reproducción en bucle
        reproductorFondo.setVolume(0.5f, 0.5f); // Volumen al 50%
        reproductorFondo.start();
    }
    
    /**
     * Registra el receptor de eventos
     */
    private void registrarReceptorEventos() {
        receptorEventos = new ReceptorEventos();
        IntentFilter filtro = new IntentFilter();
        filtro.addAction("android.intent.action.PHONE_STATE"); // Llamadas entrantes
        filtro.addAction("android.provider.Telephony.SMS_RECEIVED"); // SMS recibidos
        registerReceiver(receptorEventos, filtro);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (reproductorFondo != null && reproductorFondo.isPlaying()) {
            reproductorFondo.pause();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (reproductorFondo != null && !reproductorFondo.isPlaying()) {
            reproductorFondo.start();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos
        if (reproductorFondo != null) {
            reproductorFondo.release();
            reproductorFondo = null;
        }
        
        // Desregistrar el receptor de eventos
        if (receptorEventos != null) {
            unregisterReceiver(receptorEventos);
        }
    }
}
