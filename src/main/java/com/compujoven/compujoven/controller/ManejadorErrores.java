package com.compujoven.compujoven.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class ManejadorErrores {

    /** El archivo supera el límite configurado en application.properties (5 MB). */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String archivoMuyGrande() {
        return "redirect:/joven/perfil?errorArchivo=tamano";
    }
}
