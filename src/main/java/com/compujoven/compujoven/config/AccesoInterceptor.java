package com.compujoven.compujoven.config;

import com.compujoven.compujoven.model.Rol;
import com.compujoven.compujoven.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Control de acceso por rol (RF002):
 *  - /formaciones/** y /api/admin/**  -> solo ADMINISTRADOR
 *  - /joven/**                        -> cualquier usuario con sesión iniciada
 *  - /joven/perfil/**                 -> solo JOVEN
 */
public class AccesoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogueado");
        String ruta = request.getRequestURI().substring(request.getContextPath().length());

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        boolean zonaAdmin = ruta.startsWith("/formaciones") || ruta.startsWith("/api/admin");
        if (zonaAdmin && usuario.getRol() != Rol.ADMINISTRADOR) {
            response.sendRedirect(request.getContextPath() + "/joven/formaciones");
            return false;
        }

        // El perfil (editar, hoja de vida, eliminar cuenta) es solo para jóvenes
        if (ruta.startsWith("/joven/perfil") && usuario.getRol() != Rol.JOVEN) {
            response.sendRedirect(request.getContextPath() + "/formaciones");
            return false;
        }
        return true;
    }
}
