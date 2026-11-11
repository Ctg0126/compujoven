package com.compujoven.compujoven.controller;

import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.service.FormacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final FormacionService formacionService;

    public AdminController(FormacionService formacionService) {
        this.formacionService = formacionService;
    }

    @PostMapping("/formaciones")
    public ResponseEntity<Object> publicar(@RequestBody Formacion formacion) {
        try {
            return ResponseEntity.ok(formacionService.publicar(formacion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/formaciones")
    public List<Formacion> listar() {
        return formacionService.listarTodas();
    }

    @DeleteMapping("/formaciones/{id}")
    public String eliminar(@PathVariable Long id) {
        if (formacionService.eliminar(id)) {
            return "Formación eliminada";
        }
        return "Formación no encontrada";
    }
}