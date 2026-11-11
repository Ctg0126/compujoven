package com.compujoven.compujoven.dto;

import com.compujoven.compujoven.model.Usuario;

/** Datos editables del perfil del joven. */
public class PerfilEdicionDTO {

    private String nombre;
    private String telefono;
    private String tipoDocumento;
    private String numeroDocumento;
    private int edad;
    private String email;
    private String nuevaPassword; // opcional: vacío = no se cambia

    public PerfilEdicionDTO() {
    }

    public static PerfilEdicionDTO desde(Usuario u) {
        PerfilEdicionDTO dto = new PerfilEdicionDTO();
        dto.nombre = u.getNombre();
        dto.telefono = u.getTelefono();
        dto.tipoDocumento = u.getTipoDocumento();
        dto.numeroDocumento = u.getNumeroDocumento();
        dto.edad = u.getEdad();
        dto.email = u.getEmail();
        return dto;
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

    public String getNuevaPassword() { return nuevaPassword; }
    public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }
}
