package com.compujoven.compujoven.service;

import com.compujoven.compujoven.model.Rol;
import com.compujoven.compujoven.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();
    private Long siguienteId = 1L;

    public UsuarioService() {
        registrar(new Usuario(
            null, 
            "Administrador", 
            0.0, 
            0.0, 
            "CC", 
            12345678, 
            30, 
            "admin@compujoven.com", 
            "admin123", 
            Rol.ADMINISTRADOR
        ));
    }

    public Usuario registrar(Usuario usuario) {
        if (buscarPorEmail(usuario.getEmail()) != null) {
            return null;
        }
        usuario.setId(siguienteId++);
        usuarios.add(usuario);
        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public Usuario login(String email, String password) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        return usuarios;
    }
}