package com.cinefan.actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cinefan.R;
import com.cinefan.adaptadores.AdaptadorPeliculas;
import com.cinefan.datos.PeliculaDAO;
import com.cinefan.modelo.Pelicula;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad que muestra la lista de películas
 */
public class ActividadListaPeliculas extends AppCompatActivity {

    private static final int SOLICITUD_NUEVA_PELICULA = 100;
    private static final int SOLICITUD_EDITAR_PELICULA = 101;
    private static final int SOLICITUD_DETALLE_PELICULA = 102;

    private ListView listaPeliculas;
    private TextView txtSinPeliculas;
    private EditText editBuscar;
    private FloatingActionButton fabNuevaPelicula;
    
    private PeliculaDAO peliculaDAO;
    private AdaptadorPeliculas adaptador;
    private List<Pelicula> peliculas;
    private boolean modoBusqueda = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_lista_peliculas);
        
        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Inicializar vistas
        listaPeliculas = findViewById(R.id.listaPeliculas);
        txtSinPeliculas = findViewById(R.id.txtSinPeliculas);
        editBuscar = findViewById(R.id.editBuscar);
        fabNuevaPelicula = findViewById(R.id.fabNuevaPelicula);
        
        // Comprobar si estamos en modo búsqueda
        Intent intent = getIntent();
        modoBusqueda = intent.getBooleanExtra("modo_busqueda", false);
        
        if (modoBusqueda) {
            // Configurar modo búsqueda
            setTitle(R.string.titulo_buscar);
            editBuscar.setVisibility(View.VISIBLE);
        } else {
            // Configurar modo catálogo
            setTitle(R.string.titulo_lista_peliculas);
            editBuscar.setVisibility(View.GONE);
        }
        
        // Inicializar DAO y datos
        peliculaDAO = new PeliculaDAO(this);
        peliculaDAO.abrir();
        
        // Inicializar lista de películas
        peliculas = new ArrayList<>();
        adaptador = new AdaptadorPeliculas(this, peliculas);
        listaPeliculas.setAdapter(adaptador);
        
        // Configurar eventos
        configurarEventos();
        
        // Cargar datos
        cargarPeliculas();
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        // Click en una película de la lista
        listaPeliculas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pelicula pelicula = peliculas.get(position);
                abrirDetallePelicula(pelicula.getId());
            }
        });
        
        // Click largo en una película de la lista (menú contextual)
        listaPeliculas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Pelicula pelicula = peliculas.get(position);
                mostrarMenuContextual(pelicula);
                return true;
            }
        });
        
        // Búsqueda de películas
        editBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Realizar búsqueda
                String consulta = s.toString().trim();
                if (consulta.isEmpty()) {
                    cargarPeliculas();
                } else {
                    buscarPeliculas(consulta);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        
        // Botón para añadir nueva película
        fabNuevaPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNuevaPelicula();
            }
        });
    }
    
    /**
     * Carga todas las películas
     */
    private void cargarPeliculas() {
        peliculas.clear();
        peliculas.addAll(peliculaDAO.obtenerTodas());
        adaptador.notifyDataSetChanged();
        
        // Mostrar mensaje si no hay películas
        if (peliculas.isEmpty()) {
            txtSinPeliculas.setVisibility(View.VISIBLE);
            listaPeliculas.setVisibility(View.GONE);
        } else {
            txtSinPeliculas.setVisibility(View.GONE);
            listaPeliculas.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Busca películas por título
     * @param consulta Texto a buscar
     */
    private void buscarPeliculas(String consulta) {
        peliculas.clear();
        peliculas.addAll(peliculaDAO.buscarPorTitulo(consulta));
        adaptador.notifyDataSetChanged();
        
        // Mostrar mensaje si no hay resultados
        if (peliculas.isEmpty()) {
            txtSinPeliculas.setText(R.string.msg_sin_resultados);
            txtSinPeliculas.setVisibility(View.VISIBLE);
            listaPeliculas.setVisibility(View.GONE);
        } else {
            txtSinPeliculas.setVisibility(View.GONE);
            listaPeliculas.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Muestra el menú contextual para una película
     * @param pelicula Película seleccionada
     */
    private void mostrarMenuContextual(final Pelicula pelicula) {
        String[] opciones = {
                getString(R.string.btn_editar),
                getString(R.string.btn_eliminar)
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(pelicula.getTitulo());
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Editar
                        abrirEditarPelicula(pelicula.getId());
                        break;
                    case 1: // Eliminar
                        confirmarEliminarPelicula(pelicula);
                        break;
                }
            }
        });
        builder.show();
    }
    
    /**
     * Muestra diálogo de confirmación para eliminar una película
     * @param pelicula Película a eliminar
     */
    private void confirmarEliminarPelicula(final Pelicula pelicula) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(pelicula.getTitulo());
        builder.setMessage(R.string.msg_confirmacion_eliminar);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarPelicula(pelicula.getId());
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }
    
    /**
     * Elimina una película
     * @param id ID de la película a eliminar
     */
    private void eliminarPelicula(int id) {
        int filasEliminadas = peliculaDAO.eliminar(id);
        
        if (filasEliminadas > 0) {
            Toast.makeText(this, R.string.msg_pelicula_eliminada, Toast.LENGTH_SHORT).show();
            cargarPeliculas();
        } else {
            Toast.makeText(this, R.string.error_eliminar_pelicula, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Abre la actividad de detalle de película
     * @param id ID de la película
     */
    private void abrirDetallePelicula(int id) {
        Intent intent = new Intent(this, ActividadDetallePelicula.class);
        intent.putExtra("pelicula_id", id);
        startActivityForResult(intent, SOLICITUD_DETALLE_PELICULA);
    }
    
    /**
     * Abre la actividad para añadir una nueva película
     */
    private void abrirNuevaPelicula() {
        Intent intent = new Intent(this, ActividadNuevaPelicula.class);
        startActivityForResult(intent, SOLICITUD_NUEVA_PELICULA);
    }
    
    /**
     * Abre la actividad para editar una película
     * @param id ID de la película
     */
    private void abrirEditarPelicula(int id) {
        Intent intent = new Intent(this, ActividadNuevaPelicula.class);
        intent.putExtra("pelicula_id", id);
        startActivityForResult(intent, SOLICITUD_EDITAR_PELICULA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Recargar películas si volvemos de una actividad relacionada
        if (requestCode == SOLICITUD_NUEVA_PELICULA || 
            requestCode == SOLICITUD_EDITAR_PELICULA || 
            requestCode == SOLICITUD_DETALLE_PELICULA) {
            if (resultCode == RESULT_OK) {
                cargarPeliculas();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_peliculas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_ordenar_titulo) {
            // Ordenar por título
            // Implementación pendiente
            return true;
        } else if (id == R.id.action_ordenar_anio) {
            // Ordenar por año
            // Implementación pendiente
            return true;
        } else if (id == R.id.action_ordenar_valoracion) {
            // Ordenar por valoración
            // Implementación pendiente
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar conexión a la base de datos
        peliculaDAO.cerrar();
    }
}
