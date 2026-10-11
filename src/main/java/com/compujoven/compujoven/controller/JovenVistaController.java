package com.compujoven.compujoven.controller;

import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.service.FormacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class JovenVistaController {

    private final FormacionService formacionService;

    public JovenVistaController(FormacionService formacionService) {
        this.formacionService = formacionService;
    }

    @GetMapping("/joven/formaciones")
    public String verFormacionesJoven(Model model) {
        model.addAttribute("formaciones", formacionService.listarTodas());
        return "joven-formaciones";
    }

    @GetMapping("/joven/postular/{id}")
    public String postularAFormacion(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Formacion formacion = formacionService.buscarPorId(id);

        if (formacion != null && formacion.isAbierta()) {
            formacion.agregarPostulacion(usuarioLogueado);
        
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Postulación exitosa a la formación: " + formacion.getNombre() + "!");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No fue posible realizar la postulación o la convocatoria está cerrada.");
        }

        return "redirect:/joven/formaciones";
    }
}