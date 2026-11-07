package com.compujoven.compujoven.service;

import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.repository.FormacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FormacionService {

    private final FormacionRepository formacionRepository;

    public FormacionService(FormacionRepository formacionRepository) {
        this.formacionRepository = formacionRepository;
    }

    public List<Formacion> listarTodas() {
        return formacionRepository.findAll();
    }

    @Transactional
    public Formacion publicar(Formacion formacion) {
        formacion.setId(null); // siempre se crea una nueva
        return formacionRepository.save(formacion);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!formacionRepository.existsById(id)) {
            return false;
        }
        formacionRepository.deleteById(id);
        return true;
    }

    public Formacion buscarPorId(Long id) {
        return formacionRepository.findById(id).orElse(null);
    }

    @Transactional
    public void cambiarEstado(Long id) {
        formacionRepository.findById(id).ifPresent(f -> f.setAbierta(!f.isAbierta()));
    }
}
