package com.compujoven.compujoven.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Datos de la hoja de vida sin el contenido del archivo (no carga el BLOB). */
public interface HojaDeVidaResumen {

    String getNombreArchivo();

    long getTamano();

    LocalDateTime getFechaCarga();

    default String getTamanoTexto() {
        long t = getTamano();
        if (t < 1024 * 1024) {
            return Math.max(1, t / 1024) + " KB";
        }
        return String.format("%.1f MB", t / (1024.0 * 1024.0));
    }

    default String getFechaTexto() {
        return getFechaCarga() == null ? "" :
                getFechaCarga().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
