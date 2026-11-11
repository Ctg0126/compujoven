package com.compujoven.compujoven.dto;

/**
 * Datos del formulario de registro. El rol NO viene del formulario:
 * todo registro público crea un usuario JOVEN.
 */
public class UsuarioRegistroDTO {

    private String nombre;
    private String telefono;
    private String tipoDocumento;
    private String numeroDocumento;
    private int edad;
    private String email;
    private String password;

    public UsuarioRegistroDTO() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
