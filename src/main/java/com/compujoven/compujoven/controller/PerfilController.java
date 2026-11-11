package com.compujoven.compujoven.controller;

import com.compujoven.compujoven.dto.PerfilEdicionDTO;
import com.compujoven.compujoven.model.HojaDeVida;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.service.HojaDeVidaService;
import com.compujoven.compujoven.service.PostulacionService;
import com.compujoven.compujoven.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Perfil del joven: ver, editar, subir hoja de vida y eliminar la cuenta. */
@Controller
public class PerfilController {

    private final UsuarioService usuarioService;
    private final PostulacionService postulacionService;
    private final HojaDeVidaService hojaDeVidaService;

    public PerfilController(UsuarioService usuarioService,
                            PostulacionService postulacionService,
                            HojaDeVidaService hojaDeVidaService) {
        this.usuarioService = usuarioService;
        this.postulacionService = postulacionService;
        this.hojaDeVidaService = hojaDeVidaService;
    }

    private Usuario usuarioSesion(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioLogueado");
    }

    // ---------- Ver perfil ----------

    @GetMapping("/joven/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Usuario sesion = usuarioSesion(session);
        if (sesion == null) {
            return "redirect:/";
        }
        Usuario usuario = usuarioService.buscarPorId(sesion.getId());
        if (usuario == null) {
            session.invalidate();
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("postulaciones", postulacionService.listarPorUsuario(usuario.getId()));
        model.addAttribute("hojaDeVida", hojaDeVidaService.buscarResumen(usuario.getId()).orElse(null));
        return "perfil";
    }

    // ---------- Editar perfil ----------

    @GetMapping("/joven/perfil/editar")
    public String formularioEditar(HttpSession session, Model model) {
        Usuario sesion = usuarioSesion(session);
        if (sesion == null) {
            return "redirect:/";
        }
        Usuario usuario = usuarioService.buscarPorId(sesion.getId());
        model.addAttribute("perfilForm", PerfilEdicionDTO.desde(usuario));
        return "perfil-editar";
    }

    @PostMapping("/joven/perfil/editar")
    public String guardarEdicion(@ModelAttribute("perfilForm") PerfilEdicionDTO dto,
                                 BindingResult binding,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Usuario sesion = usuarioSesion(session);
        if (sesion == null) {
            return "redirect:/";
        }

        String error = binding.hasErrors()
                ? "Revisa los datos: hay valores con formato inválido."
                : usuarioService.actualizarPerfil(sesion.getId(), dto);

        if (error != null) {
            model.addAttribute("error", error);
            return "perfil-editar";
        }

        session.setAttribute("usuarioLogueado", usuarioService.buscarPorId(sesion.getId()));
        redirectAttributes.addFlashAttribute("mensajeExito", "Perfil actualizado correctamente.");
        return "redirect:/joven/perfil";
    }

    // ---------- Hoja de vida ----------

    @PostMapping("/joven/perfil/hoja-de-vida")
    public String subirHojaDeVida(@RequestParam("archivo") MultipartFile archivo,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario sesion = usuarioSesion(session);
        if (sesion == null) {
            return "redirect:/";
        }
        try {
            hojaDeVidaService.guardar(sesion.getId(), archivo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Hoja de vida guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo leer el archivo. Intenta de nuevo.");
        }
        return "redirect:/joven/perfil";
    }

    @GetMapping("/joven/perfil/hoja-de-vida")
    public ResponseEntity<byte[]> descargarHojaDeVida(HttpSession session) {
        Usuario sesion = usuarioSesion(session);
        if (sesion == null) {
            return ResponseEntity.status(401).build();
        }
        return hojaDeVidaService.buscar(sesion.getId())
                .map(PerfilController::respuestaArchivo)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/joven/perfil/hoja-de-vida/eliminar")
    public String eliminarHojaDeVida(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario sesion = usuarioSesion(session);
        if (sesion == null) {
            return "redirect:/";
        }
        hojaDeVidaService.eliminar(sesion.getId());
        redirectAttributes.addFlashAttribute("mensajeExito", "Hoja de vida eliminada.");
        return "redirect:/joven/perfil";
    }

    // ---------- Eliminar cuenta ----------

    @PostMapping("/joven/perfil/eliminar")
    public String eliminarCuenta(@RequestParam("password") String password,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario sesion = usuarioSesion(session);
        if (sesion == null) {
            return "redirect:/";
        }
        Usuario usuario = usuarioService.buscarPorId(sesion.getId());
        if (usuario == null || !usuarioService.passwordCoincide(usuario, password)) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "La contraseña no es correcta. Tu cuenta no fue eliminada.");
            return "redirect:/joven/perfil";
        }
        usuarioService.eliminarCuenta(usuario.getId());
        session.invalidate();
        return "redirect:/?cuentaEliminada=true";
    }

    // ---------- utilidad compartida con la vista del administrador ----------

    public static ResponseEntity<byte[]> respuestaArchivo(HojaDeVida hoja) {
        ContentDisposition disposicion = ContentDisposition.attachment()
                .filename(hoja.getNombreArchivo(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                .contentType(MediaType.parseMediaType(hoja.getTipoContenido()))
                .contentLength(hoja.getDatos().length)
                .body(hoja.getDatos());
    }
}
