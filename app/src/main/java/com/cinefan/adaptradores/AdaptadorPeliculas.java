package com.cinefan.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cinefan.R;
import com.cinefan.modelo.Pelicula;
import com.cinefan.utilidades.GestorMultimedia;

import java.util.List;

/**
 * Adaptador personalizado para mostrar películas en un ListView
 */
public class AdaptadorPeliculas extends ArrayAdapter<Pelicula> {

    private Context contexto;
    private List<Pelicula> peliculas;
    
    public AdaptadorPeliculas(Context contexto, List<Pelicula> peliculas) {
        super(contexto, R.layout.item_pelicula, peliculas);
        this.contexto = contexto;
        this.peliculas = peliculas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Crear o reutilizar la vista
        View item = convertView;
        ViewHolder holder;
        
        if (item == null) {
            // Si no existe, inflar el layout
            LayoutInflater inflater = LayoutInflater.from(contexto);
            item = inflater.inflate(R.layout.item_pelicula, parent, false);
            
            // Crear un ViewHolder y guardar referencias a las vistas
            holder = new ViewHolder();
            holder.imagenPelicula = item.findViewById(R.id.imagenPelicula);
            holder.txtTitulo = item.findViewById(R.id.txtTitulo);
            holder.txtAnio = item.findViewById(R.id.txtAnio);
            holder.txtDirector = item.findViewById(R.id.txtDirector);
            holder.txtGenero = item.findViewById(R.id.txtGenero);
            holder.ratingValoracion = item.findViewById(R.id.ratingValoracion);
            
            // Guardar el holder en la vista
            item.setTag(holder);
        } else {
            // Recuperar el holder guardado
            holder = (ViewHolder) item.getTag();
        }
        
        // Obtener la película actual
        Pelicula pelicula = peliculas.get(position);
        
        // Establecer los datos en las vistas
        holder.txtTitulo.setText(pelicula.getTitulo());
        holder.txtAnio.setText(String.valueOf(pelicula.getAnio()));
        holder.txtDirector.setText(pelicula.getDirector());
        holder.txtGenero.setText(pelicula.getGenero());
        holder.ratingValoracion.setRating(pelicula.getValoracion() / 2); // Escala 0-10 a 0-5
        
        // Cargar imagen de la película (si existe)
        if (pelicula.getImagenRuta() != null && !pelicula.getImagenRuta().isEmpty()) {
            // Usar la utilidad GestorMultimedia para cargar la imagen
            GestorMultimedia.cargarImagen(contexto, pelicula.getImagenRuta(), holder.imagenPelicula);
        } else {
            // Imagen por defecto
            holder.imagenPelicula.setImageResource(R.drawable.pelicula_default);
        }
        
        return item;
    }
    
    /**
     * Patrón ViewHolder para mejorar el rendimiento del ListView
     */
    static class ViewHolder {
        ImageView imagenPelicula;
        TextView txtTitulo;
        TextView txtAnio;
        TextView txtDirector;
        TextView txtGenero;
        RatingBar ratingValoracion;
    }
}
