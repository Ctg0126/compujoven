package com.compujoven.compujoven.model;

import java.util.ArrayList;
import java.util.List;

public class Formacion {

    private Long id;
    private String nombre;
    private String descripcion;
    private String duracion;
    private boolean abierta = true; // Por defecto inicia abierta
    private List<Usuario> postulaciones = new ArrayList<>();

    public Formacion() {
    }

    public Formacion(Long id, String nombre, String descripcion, String duracion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.abierta = true;
    }

    
    public void agregarPostulacion(Usuario usuario) {
        if (this.postulaciones == null) {
            this.postulaciones = new ArrayList<>();
        }
        if (!this.postulaciones.contains(usuario)) {
            this.postulaciones.add(usuario);
        }
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public boolean isAbierta() { return abierta; }
    public void setAbierta(boolean abierta) { this.abierta = abierta; }

    public List<Usuario> getPostulaciones() { return postulaciones; }
    public void setPostulaciones(List<Usuario> postulaciones) { this.postulaciones = postulaciones; }
}