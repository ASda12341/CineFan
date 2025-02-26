package com.cinefan.modelo;

import java.io.Serializable;

/**
 * Clase que representa una película en la aplicación
 */
public class Pelicula implements Serializable {
    private int id;
    private String titulo;
    private int anio;
    private String director;
    private String genero;
    private String sinopsis;
    private float valoracion;
    private String imagenRuta;
    private String archivoAudio;

    // Constructor vacío
    public Pelicula() {
    }

    // Constructor completo
    public Pelicula(int id, String titulo, int anio, String director, String genero,
                    String sinopsis, float valoracion, String imagenRuta, String archivoAudio) {
        this.id = id;
        this.titulo = titulo;
        this.anio = anio;
        this.director = director;
        this.genero = genero;
        this.sinopsis = sinopsis;
        this.valoracion = valoracion;
        this.imagenRuta = imagenRuta;
        this.archivoAudio = archivoAudio;
    }

    // Constructor sin ID (para nuevas películas)
    public Pelicula(String titulo, int anio, String director, String genero,
                    String sinopsis, float valoracion, String imagenRuta, String archivoAudio) {
        this.titulo = titulo;
        this.anio = anio;
        this.director = director;
        this.genero = genero;
        this.sinopsis = sinopsis;
        this.valoracion = valoracion;
        this.imagenRuta = imagenRuta;
        this.archivoAudio = archivoAudio;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getImagenRuta() {
        return imagenRuta;
    }

    public void setImagenRuta(String imagenRuta) {
        this.imagenRuta = imagenRuta;
    }

    public String getArchivoAudio() {
        return archivoAudio;
    }

    public void setArchivoAudio(String archivoAudio) {
        this.archivoAudio = archivoAudio;
    }

    @Override
    public String toString() {
        return titulo + " (" + anio + ")";
    }
}
