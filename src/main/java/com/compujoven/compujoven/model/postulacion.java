package com.compujoven.compujoven.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Relación Usuario - Formacion (muchos a muchos) modelada como entidad propia,
 * para guardar fecha y estado de cada postulación.
 */
@Entity
@Table(name = "postulaciones",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "formacion_id"}))
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "formacion_id")
    private Formacion formacion;

    @Column(name = "fecha_postulacion", nullable = false)
    private LocalDateTime fechaPostulacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPostulacion estado = EstadoPostulacion.PENDIENTE;

    public Postulacion() {
    }

    public Postulacion(Usuario usuario, Formacion formacion) {
        this.usuario = usuario;
        this.formacion = formacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Formacion getFormacion() { return formacion; }
    public void setFormacion(Formacion formacion) { this.formacion = formacion; }

    public LocalDateTime getFechaPostulacion() { return fechaPostulacion; }
    public void setFechaPostulacion(LocalDateTime fechaPostulacion) { this.fechaPostulacion = fechaPostulacion; }

    public EstadoPostulacion getEstado() { return estado; }
    public void setEstado(EstadoPostulacion estado) { this.estado = estado; }
}
