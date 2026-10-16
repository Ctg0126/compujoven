package com.compujoven.compujoven.controller;

import com.compujoven.compujoven.dto.LoginRequest;
import com.compujoven.compujoven.dto.UsuarioRegistroDTO;
import com.compujoven.compujoven.model.Rol;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthVistaController {

    private final UsuarioService usuarioService;

    public AuthVistaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("usuarioRegistro", new UsuarioRegistroDTO());
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/";
    }

    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute("loginRequest") LoginRequest request, HttpSession session, Model model) {
        Usuario usuario = usuarioService.login(request.getEmail(), request.getPassword());
        
        if (usuario == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            model.addAttribute("usuarioRegistro", new UsuarioRegistroDTO());
            return "login";
        }

        
        session.setAttribute("usuarioLogueado", usuario);

        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            return "redirect:/formaciones";
        } else {
            return "redirect:/joven/formaciones";
        }
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("usuarioRegistro") UsuarioRegistroDTO dto, Model model) {
        // Convertir DTO con los datos extendidos a Entidad Usuario
        Usuario usuario = new Usuario(
            null,
            dto.getNombre(),
            dto.getTelefono(),
            dto.getTipoDocumento(),
            dto.getNumeroDocumento(),
            dto.getEdad(),
            dto.getEmail(),
            dto.getPassword(),
            dto.getRol() != null ? dto.getRol() : Rol.JOVEN
        );

        Usuario nuevo = usuarioService.registrar(usuario);
        if (nuevo == null) {
            model.addAttribute("errorRegistro", "El correo ya se encuentra registrado");
            model.addAttribute("loginRequest", new LoginRequest());
            return "login";
        }
        return "redirect:/?registroExitoso=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}