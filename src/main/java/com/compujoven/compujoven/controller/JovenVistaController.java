package com.compujoven.compujoven.controller;

import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.service.FormacionService;
import com.compujoven.compujoven.service.PostulacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
public class JovenVistaController {

    private final FormacionService formacionService;
    private final PostulacionService postulacionService;

    public JovenVistaController(FormacionService formacionService, PostulacionService postulacionService) {
        this.formacionService = formacionService;
        this.postulacionService = postulacionService;
    }

    @GetMapping("/joven/formaciones")
    public String verFormacionesJoven(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("formaciones", formacionService.listarTodas());
        model.addAttribute("postuladas", postulacionService.idsFormacionesPostuladas(usuario.getId()));
        return "joven-formaciones";
    }

    @PostMapping("/joven/postular/{id}")
    public String postularAFormacion(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login";
        }

        PostulacionService.Resultado resultado = postulacionService.postular(usuario.getId(), id);

        switch (resultado) {
            case OK -> {
                Formacion formacion = formacionService.buscarPorId(id);
                redirectAttributes.addFlashAttribute("mensajeExito",
                        "¡Postulación exitosa a la formación: " + formacion.getNombre() + "!");
            }
            case YA_POSTULADO -> redirectAttributes.addFlashAttribute("mensajeError",
                    "Ya te postulaste a esta formación.");
            case CERRADA -> redirectAttributes.addFlashAttribute("mensajeError",
                    "La convocatoria está cerrada.");
            default -> redirectAttributes.addFlashAttribute("mensajeError",
                    "No fue posible realizar la postulación.");
        }

        return "redirect:/joven/formaciones";
    }
}
