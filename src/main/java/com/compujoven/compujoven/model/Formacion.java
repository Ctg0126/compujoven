package com.compujoven.compujoven.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "formaciones")
public class Formacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 50)
    private String duracion;

    @Column(length = 1000)
    private String requisitos;

    /** Cupos máximos de la formación. 0 (o menos) significa sin límite. */
    @Column(nullable = false)
    private int capacidad = 0;

    @Column(nullable = false)
    private boolean abierta = true; // Por defecto inicia abierta

    @OneToMany(mappedBy = "formacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Postulacion> postulaciones = new ArrayList<>();

    public Formacion() {
    }

    public Formacion(String nombre, String descripcion, String duracion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.abierta = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getRequisitos() { return requisitos; }
    public void setRequisitos(String requisitos) { this.requisitos = requisitos; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    /** Cupos ocupados: postulaciones pendientes o aceptadas (las rechazadas liberan el cupo). */
    @JsonIgnore
    public int getCuposOcupados() {
        if (postulaciones == null) {
            return 0;
        }
        return (int) postulaciones.stream()
                .filter(p -> p.getEstado() != EstadoPostulacion.RECHAZADA)
                .count();
    }

    @JsonIgnore
    public boolean isSinLimite() {
        return capacidad <= 0;
    }

    @JsonIgnore
    public boolean isLlena() {
        return !isSinLimite() && getCuposOcupados() >= capacidad;
    }

    @JsonIgnore
    public int getCuposDisponibles() {
        return isSinLimite() ? 0 : Math.max(0, capacidad - getCuposOcupados());
    }

    public boolean isAbierta() { return abierta; }
    public void setAbierta(boolean abierta) { this.abierta = abierta; }

    public List<Postulacion> getPostulaciones() { return postulaciones; }
    public void setPostulaciones(List<Postulacion> postulaciones) { this.postulaciones = postulaciones; }
}
