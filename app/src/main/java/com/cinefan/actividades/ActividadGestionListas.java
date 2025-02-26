package com.cinefan.actividades;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cinefan.R;
import com.cinefan.modelo.Lista;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Actividad para gestionar listas de películas
 */
public class ActividadGestionListas extends AppCompatActivity {

    private ListView listaListas;
    private TextView txtSinListas;
    private FloatingActionButton fabNuevaLista;
    
    private List<Lista> listas;
    private ArrayAdapter<Lista> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_listas);
        
        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.titulo_listas);
        
        // Inicializar vistas
        listaListas = findViewById(R.id.listaListas);
        txtSinListas = findViewById(R.id.txtSinListas);
        fabNuevaLista = findViewById(R.id.fabNuevaLista);
        
        // Inicializar datos
        listas = new ArrayList<>();
        
        // Por ahora usamos un adaptador simple, pero debería crearse un adaptador personalizado
        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listas);
        listaListas.setAdapter(adaptador);
        
        // Cargar listas existentes
        cargarListas();
        
        // Configurar eventos
        configurarEventos();
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        // Click en una lista
        listaListas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lista lista = listas.get(position);
                // Abrir actividad de detalle de lista (pendiente de implementar)
                // Intent intent = new Intent(ActividadGestionListas.this, ActividadDetalleLista.class);
                // intent.putExtra("lista_id", lista.getId());
                // startActivity(intent);
                
                // Por ahora solo mostramos un mensaje
                Toast.makeText(ActividadGestionListas.this, 
                        "Lista seleccionada: " + lista.getNombre(), 
                        Toast.LENGTH_SHORT).show();
            }
        });
        
        // Click largo en una lista (menú contextual)
        listaListas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Lista lista = listas.get(position);
                mostrarMenuContextual(lista, position);
                return true;
            }
        });
        
        // Botón para crear nueva lista
        fabNuevaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoNuevaLista();
            }
        });
    }
    
    /**
     * Carga las listas existentes
     */
    private void cargarListas() {
        // Aquí se deberían cargar las listas desde la base de datos
        
        // Por ahora agregamos algunas listas de ejemplo
        if (listas.isEmpty()) {
            listas.add(new Lista(1, "Favoritas", "Mis películas favoritas", "2023-01-15"));
            listas.add(new Lista(2, "Por ver", "Películas que quiero ver", "2023-02-20"));
            adaptador.notifyDataSetChanged();
        }
        
        // Mostrar mensaje si no hay listas
        actualizarVistaSinListas();
    }
    
    /**
     * Muestra u oculta el mensaje de "No hay listas"
     */
    private void actualizarVistaSinListas() {
        if (listas.isEmpty()) {
            txtSinListas.setVisibility(View.VISIBLE);
            listaListas.setVisibility(View.GONE);
        } else {
            txtSinListas.setVisibility(View.GONE);
            listaListas.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Muestra el diálogo para crear una nueva lista
     */
    private void mostrarDialogoNuevaLista() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.titulo_nueva_lista);
        
        // Inflar layout del diálogo
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialogo_nueva_lista, null);
        builder.setView(view);
        
        final EditText editNombre = view.findViewById(R.id.editNombre);
        final EditText editDescripcion = view.findViewById(R.id.editDescripcion);
        
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = editNombre.getText().toString().trim();
                String descripcion = editDescripcion.getText().toString().trim();
                
                if (!nombre.isEmpty()) {
                    crearNuevaLista(nombre, descripcion);
                }
            }
        });
        
        builder.setNegativeButton(android.R.string.cancel, null);
        
        builder.show();
    }
    
    /**
     * Crea una nueva lista
     * @param nombre Nombre de la lista
     * @param descripcion Descripción de la lista
     */
    private void crearNuevaLista(String nombre, String descripcion) {
        // Generar fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaCreacion = sdf.format(new Date());
        
        // Crear nueva lista (en una implementación real se guardaría en la base de datos)
        int nuevoId = listas.size() + 1;
        Lista nuevaLista = new Lista(nuevoId, nombre, descripcion, fechaCreacion);
        
        // Agregar a la lista y notificar al adaptador
        listas.add(nuevaLista);
        adaptador.notifyDataSetChanged();
        
        // Actualizar vista
        actualizarVistaSinListas();
        
        Toast.makeText(this, R.string.lista_creada, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Muestra el menú contextual para una lista
     * @param lista Lista seleccionada
     * @param posicion Posición en la lista
     */
    private void mostrarMenuContextual(final Lista lista, final int posicion) {
        String[] opciones = {"Editar", "Eliminar"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(lista.getNombre());
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Editar
                        mostrarDialogoEditarLista(lista, posicion);
                        break;
                    case 1: // Eliminar
                        confirmarEliminarLista(lista, posicion);
                        break;
                }
            }
        });
        builder.show();
    }
    
    /**
     * Muestra el diálogo para editar una lista
     * @param lista Lista a editar
     * @param posicion Posición en la lista
     */
    private void mostrarDialogoEditarLista(final Lista lista, final int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar lista");
        
        // Inflar layout del diálogo
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialogo_nueva_lista, null);
        builder.setView(view);
        
        final EditText editNombre = view.findViewById(R.id.editNombre);
        final EditText editDescripcion = view.findViewById(R.id.editDescripcion);
        
        // Cargar datos actuales
        editNombre.setText(lista.getNombre());
        editDescripcion.setText(lista.getDescripcion());
        
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = editNombre.getText().toString().trim();
                String descripcion = editDescripcion.getText().toString().trim();
                
                if (!nombre.isEmpty()) {
                    // Actualizar lista
                    lista.setNombre(nombre);
                    lista.setDescripcion(descripcion);
                    
                    // Notificar al adaptador
                    adaptador.notifyDataSetChanged();
                    
                    Toast.makeText(ActividadGestionListas.this, 
                            "Lista actualizada", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        builder.setNegativeButton(android.R.string.cancel, null);
        
        builder.show();
    }
    
    /**
     * Muestra diálogo de confirmación para eliminar una lista
     * @param lista Lista a eliminar
     * @param posicion Posición en la lista
     */
    private void confirmarEliminarLista(final Lista lista, final int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(lista.getNombre());
        builder.setMessage(R.string.confirmacion_eliminar_lista);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarLista(posicion);
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }
    
    /**
     * Elimina una lista
     * @param posicion Posición de la lista a eliminar
     */
    private void eliminarLista(int posicion) {
        // Eliminar lista (en una implementación real se eliminaría de la base de datos)
        listas.remove(posicion);
        adaptador.notifyDataSetChanged();
        
        // Actualizar vista
        actualizarVistaSinListas();
        
        Toast.makeText(this, R.string.lista_eliminada, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
