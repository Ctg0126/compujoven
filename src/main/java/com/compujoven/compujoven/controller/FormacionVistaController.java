package com.compujoven.compujoven.controller;

import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.service.FormacionService;
import com.compujoven.compujoven.model.EstadoPostulacion;
import com.compujoven.compujoven.service.HojaDeVidaService;
import com.compujoven.compujoven.service.PostulacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FormacionVistaController {

    private final FormacionService formacionService;
    private final HojaDeVidaService hojaDeVidaService;
    private final PostulacionService postulacionService;

    public FormacionVistaController(FormacionService formacionService,
                                    HojaDeVidaService hojaDeVidaService,
                                    PostulacionService postulacionService) {
        this.formacionService = formacionService;
        this.hojaDeVidaService = hojaDeVidaService;
        this.postulacionService = postulacionService;
    }

    @GetMapping("/formaciones")
    public String listar(Model model) {
        model.addAttribute("formaciones", formacionService.listarTodas());
        model.addAttribute("conHojaDeVida", hojaDeVidaService.idsUsuariosConHojaDeVida());
        return "formaciones";
    }

    /** El administrador acepta o rechaza a un postulado. */
    @PostMapping("/formaciones/postulaciones/{id}/estado")
    public String cambiarEstadoPostulacion(@PathVariable Long id,
                                           @RequestParam EstadoPostulacion estado,
                                           RedirectAttributes redirectAttributes) {
        String error = postulacionService.cambiarEstado(id, estado);
        if (error != null) {
            redirectAttributes.addFlashAttribute("mensajeError", error);
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Postulación actualizada: " + estado.getEtiqueta());
        }
        return "redirect:/formaciones";
    }

    /** El administrador descarga la hoja de vida de un postulado. */
    @GetMapping("/formaciones/hoja-de-vida/{usuarioId}")
    public ResponseEntity<byte[]> hojaDeVida(@PathVariable Long usuarioId) {
        return hojaDeVidaService.buscar(usuarioId)
                .map(PerfilController::respuestaArchivo)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/formaciones/nueva")
    public String nueva(Model model) {
        Formacion formacion = new Formacion();
        formacion.setCapacidad(30);
        model.addAttribute("formacion", formacion);
        return "nueva-formacion";
    }

    @PostMapping("/formaciones")
    public String guardar(@ModelAttribute("formacion") Formacion formacion,
                          BindingResult binding,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            model.addAttribute("error", "Revisa los datos: hay valores con formato inválido.");
            return "nueva-formacion";
        }
        try {
            formacionService.publicar(formacion);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "nueva-formacion";
        }
        redirectAttributes.addFlashAttribute("mensajeExito", "Formación publicada correctamente.");
        return "redirect:/formaciones";
    }

    /** Formulario para corregir una formación ya publicada. */
    @GetMapping("/formaciones/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Formacion formacion = formacionService.buscarPorId(id);
        if (formacion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontró la formación.");
            return "redirect:/formaciones";
        }
        model.addAttribute("formacion", formacion);
        model.addAttribute("cuposOcupados", formacion.getCuposOcupados());
        return "editar-formacion";
    }

    @PostMapping("/formaciones/editar/{id}")
    public String guardarEdicion(@PathVariable Long id,
                                 @ModelAttribute("formacion") Formacion datos,
                                 BindingResult binding,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Formacion original = formacionService.buscarPorId(id);
        if (original == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontró la formación.");
            return "redirect:/formaciones";
        }

        String error = null;
        if (binding.hasErrors()) {
            error = "Revisa los datos: hay valores con formato inválido.";
        } else {
            try {
                formacionService.actualizar(id, datos);
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
            }
        }

        if (error != null) {
            datos.setId(id);
            model.addAttribute("error", error);
            model.addAttribute("cuposOcupados", original.getCuposOcupados());
            return "editar-formacion";
        }

        redirectAttributes.addFlashAttribute("mensajeExito", "Formación actualizada correctamente.");
        return "redirect:/formaciones";
    }

    @PostMapping("/formaciones/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!formacionService.cambiarEstado(id)) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se puede reabrir: la formación ya alcanzó su capacidad.");
        }
        return "redirect:/formaciones";
    }

    @PostMapping("/formaciones/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        formacionService.eliminar(id);
        return "redirect:/formaciones";
    }
}