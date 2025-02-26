package com.cinefan.modelo;

import java.io.Serializable;

/**
 * Clase que representa una lista de películas
 */
public class Lista implements Serializable {
    private int id;
    private String nombre;
    private String descripcion;
    private String fechaCreacion;

    /**
     * Constructor vacío
     */
    public Lista() {
    }

    /**
     * Constructor completo
     * @param id ID de la lista
     * @param nombre Nombre de la lista
     * @param descripcion Descripción de la lista
     * @param fechaCreacion Fecha de creación de la lista
     */
    public Lista(int id, String nombre, String descripcion, String fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Constructor sin ID (para nuevas listas)
     * @param nombre Nombre de la lista
     * @param descripcion Descripción de la lista
     * @param fechaCreacion Fecha de creación de la lista
     */
    public Lista(String nombre, String descripcion, String fechaCreacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
