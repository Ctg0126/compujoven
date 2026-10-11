package com.compujoven.compujoven.controller;

import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.service.FormacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FormacionVistaController {

    private final FormacionService formacionService;

    public FormacionVistaController(FormacionService formacionService) {
        this.formacionService = formacionService;
    }

    @GetMapping("/formaciones")
    public String listar(Model model) {
        model.addAttribute("formaciones", formacionService.listarTodas());
        return "formaciones";
    }

    @GetMapping("/formaciones/nueva")
    public String nueva(Model model) {
        model.addAttribute("formacion", new Formacion());
        return "nueva-formacion";
    }

    @PostMapping("/formaciones")
    public String guardar(@ModelAttribute Formacion formacion) {
        formacionService.publicar(formacion);
        return "redirect:/formaciones";
    }

    @GetMapping("/formaciones/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id) {
        Formacion formacion = formacionService.buscarPorId(id);
        if (formacion != null) {
            formacion.setAbierta(!formacion.isAbierta());
        }
        return "redirect:/formaciones";
    }

    @GetMapping("/formaciones/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        formacionService.eliminar(id);
        return "redirect:/formaciones";
    }
}