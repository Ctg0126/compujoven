package com.compujoven.compujoven.model;

public enum EstadoPostulacion {
    PENDIENTE("Pendiente de revisión", "insignia-pendiente"),
    ACEPTADA("¡Aceptada!", "insignia-ok"),
    RECHAZADA("No seleccionada", "insignia-cerrada");

    private final String etiqueta;
    private final String claseCss;

    EstadoPostulacion(String etiqueta, String claseCss) {
        this.etiqueta = etiqueta;
        this.claseCss = claseCss;
    }

    public String getEtiqueta() { return etiqueta; }

    public String getClaseCss() { return claseCss; }
}
