package com.cinefan.actividades;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cinefan.R;
import com.cinefan.datos.PeliculaDAO;
import com.cinefan.modelo.Pelicula;
import com.cinefan.utilidades.GestorMultimedia;
import com.cinefan.utilidades.ValidadorDatos;

/**
 * Actividad para añadir una nueva película o editar una existente
 */
public class ActividadNuevaPelicula extends AppCompatActivity {

    private static final int SOLICITUD_SELECCIONAR_IMAGEN = 100;
    private static final int SOLICITUD_SELECCIONAR_AUDIO = 101;
    
    private EditText editTitulo;
    private EditText editAnio;
    private EditText editDirector;
    private Spinner spinnerGenero;
    private EditText editSinopsis;
    private RatingBar ratingValoracion;
    private ImageView imagenPelicula;
    private Button btnSeleccionarImagen;
    private Button btnSeleccionarAudio;
    private Button btnGuardar;
    private Button btnCancelar;
    
    private PeliculaDAO peliculaDAO;
    private Pelicula peliculaActual;
    private String rutaImagen;
    private String rutaAudio;
    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_nueva_pelicula);
        
        // Inicializar componentes UI
        inicializarComponentes();
        
        // Inicializar DAO
        peliculaDAO = new PeliculaDAO(this);
        peliculaDAO.abrir();
        
        // Comprobar si estamos en modo edición
        Intent intent = getIntent();
        int peliculaId = intent.getIntExtra("pelicula_id", -1);
        
        if (peliculaId != -1) {
            // Modo edición
            modoEdicion = true;
            setTitle(R.string.editar_pelicula);
            cargarDatosPelicula(peliculaId);
        } else {
            // Modo nueva película
            setTitle(R.string.nueva_pelicula);
            peliculaActual = new Pelicula();
        }
        
        // Configurar eventos
        configurarEventos();
    }
    
    /**
     * Inicializa los componentes de la UI
     */
    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.editTitulo);
        editAnio = findViewById(R.id.editAnio);
        editDirector = findViewById(R.id.editDirector);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        editSinopsis = findViewById(R.id.editSinopsis);
        ratingValoracion = findViewById(R.id.ratingValoracion);
        imagenPelicula = findViewById(R.id.imagenPelicula);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarAudio = findViewById(R.id.btnSeleccionarAudio);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        
        // Configurar el spinner de géneros
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.generos_peliculas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapter);
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });
        
        btnSeleccionarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarAudio();
            }
        });
        
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPelicula();
            }
        });
        
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    /**
     * Carga los datos de una película existente
     * @param peliculaId ID de la película a cargar
     */
    private void cargarDatosPelicula(int peliculaId) {
        peliculaActual = peliculaDAO.obtenerPorId(peliculaId);
        
        if (peliculaActual != null) {
            // Llenar formulario con datos existentes
            editTitulo.setText(peliculaActual.getTitulo());
            editAnio.setText(String.valueOf(peliculaActual.getAnio()));
            editDirector.setText(peliculaActual.getDirector());
            
            // Seleccionar género en el spinner
            String genero = peliculaActual.getGenero();
            String[] generos = getResources().getStringArray(R.array.generos_peliculas);
            for (int i = 0; i < generos.length; i++) {
                if (generos[i].equals(genero)) {
                    spinnerGenero.setSelection(i);
                    break;
                }
            }
            
            editSinopsis.setText(peliculaActual.getSinopsis());
            ratingValoracion.setRating(peliculaActual.getValoracion() / 2); // Escala 0-10 a 0-5
            
            // Cargar imagen si existe
            rutaImagen = peliculaActual.getImagenRuta();
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                GestorMultimedia.cargarImagen(this, rutaImagen, imagenPelicula);
            }
            
            // Guardar ruta del audio
            rutaAudio = peliculaActual.getArchivoAudio();
        }
    }
    
    /**
     * Abre selector de imágenes
     */
    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SOLICITUD_SELECCIONAR_IMAGEN);
    }
    
    /**
     * Abre selector de audio
     */
    private void seleccionarAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, SOLICITUD_SELECCIONAR_AUDIO);
    }
    
    /**
     * Guarda los datos de la película
     */
    private void guardarPelicula() {
        // Validar datos
        if (!validarDatos()) {
            return;
        }
        
        // Obtener datos del formulario
        String titulo = editTitulo.getText().toString().trim();
        int anio = Integer.parseInt(editAnio.getText().toString().trim());
        String director = editDirector.getText().toString().trim();
        String genero = spinnerGenero.getSelectedItem().toString();
        String sinopsis = editSinopsis.getText().toString().trim();
        float valoracion = ratingValoracion.getRating() * 2; // Escala 0-5 a 0-10
        
        // Actualizar objeto película
        peliculaActual.setTitulo(titulo);
        peliculaActual.setAnio(anio);
        peliculaActual.setDirector(director);
        peliculaActual.setGenero(genero);
        peliculaActual.setSinopsis(sinopsis);
        peliculaActual.setValoracion(valoracion);
        peliculaActual.setImagenRuta(rutaImagen);
        peliculaActual.setArchivoAudio(rutaAudio);
        
        // Guardar en la base de datos
        boolean exito;
        if (modoEdicion) {
            // Actualizar película existente
            exito = peliculaDAO.actualizar(peliculaActual) > 0;
            if (exito) {
                Toast.makeText(this, R.string.pelicula_actualizada, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_actualizar_pelicula, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Insertar nueva película
            Pelicula resultado = peliculaDAO.insertar(peliculaActual);
            exito = (resultado != null);
            if (exito) {
                Toast.makeText(this, R.string.pelicula_guardada, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_guardar_pelicula, Toast.LENGTH_SHORT).show();
            }
        }
        
        if (exito) {
            // Volver a la actividad anterior
            finish();
        }
    }
    
    /**
     * Valida los datos del formulario
     * @return true si los datos son válidos, false en caso contrario
     */
    private boolean validarDatos() {
        // Validar título
        String titulo = editTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            editTitulo.setError(getString(R.string.error_campo_requerido));
            return false;
        }
        
        // Validar año
        String anioStr = editAnio.getText().toString().trim();
        if (anioStr.isEmpty()) {
            editAnio.setError(getString(R.string.error_campo_requerido));
            return false;
        }
        
        if (!ValidadorDatos.esAnioValido(anioStr)) {
            editAnio.setError(getString(R.string.error_anio_invalido));
            return false;
        }
        
        // Validar director
        String director = editDirector.getText().toString().trim();
        if (director.isEmpty()) {
            editDirector.setError(getString(R.string.error_campo_requerido));
            return false;
        }
        
        // Todos los datos son válidos
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == SOLICITUD_SELECCIONAR_IMAGEN) {
                // Procesar selección de imagen
                Uri uri = data.getData();
                rutaImagen = GestorMultimedia.obtenerRutaDesdeUri(this, uri);
                GestorMultimedia.cargarImagen(this, rutaImagen, imagenPelicula);
            } else if (requestCode == SOLICITUD_SELECCIONAR_AUDIO) {
                // Procesar selección de audio
                Uri uri = data.getData();
                rutaAudio = GestorMultimedia.obtenerRutaDesdeUri(this, uri);
                Toast.makeText(this, R.string.audio_seleccionado, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar conexión a la base de datos
        peliculaDAO.cerrar();
    }
}
