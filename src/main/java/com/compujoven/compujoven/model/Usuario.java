package com.compujoven.compujoven.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private Long id;
    private String nombre;
    private String telefono;
    private String tipoDocumento;
    private int numeroDocumento;
    private int edad;
    private String email;
    private String password;
    private Rol rol;
    private List<Formacion> postulaciones = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(Long id, String nombre, String telefono, String tipoDocumento, int numeroDocumento, int edad, String email, String password, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.edad = edad;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public int getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(int numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Formacion> getPostulaciones() {
        return postulaciones;
    }

    public void setPostulaciones(List<Formacion> postulaciones) {
        this.postulaciones = postulaciones;
    }
}