package com.compujoven.compujoven.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private Long id;
    private String nombre;
    private double peso;
    private double altura;
    private String tipoDocumento;
    private int numeroDocumento;
    private int edad;
    private String email;
    private String password;
    private Rol rol;
    private List<Formacion> postulaciones = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(Long id, String nombre, double peso, double altura, String tipoDocumento, int numeroDocumento, int edad, String email, String password, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.peso = peso;
        this.altura = altura;
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

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
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