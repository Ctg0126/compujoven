package com.compujoven.compujoven.service;

import com.compujoven.compujoven.model.Formacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FormacionService {

    private final List<Formacion> formaciones = new ArrayList<>();
    private Long siguienteId = 1L;

    public List<Formacion> listarTodas() {
        return formaciones;
    }

    public Formacion publicar(Formacion formacion) {
        formacion.setId(siguienteId++);
        formaciones.add(formacion);
        return formacion;
    }

    public boolean eliminar(Long id) {
        return formaciones.removeIf(f -> f.getId().equals(id));
    }

    public Formacion buscarPorId(Long id) {
        for (Formacion f : formaciones) {
            if (f.getId().equals(id)) {
                return f;
            }
        }
        return null;
    }
}