package com.compujoven.compujoven.service;

import com.compujoven.compujoven.dto.PerfilEdicionDTO;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.repository.HojaDeVidaRepository;
import com.compujoven.compujoven.repository.PostulacionRepository;
import com.compujoven.compujoven.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PostulacionRepository postulacionRepository;
    private final HojaDeVidaRepository hojaDeVidaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PostulacionRepository postulacionRepository,
                          HojaDeVidaRepository hojaDeVidaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.postulacionRepository = postulacionRepository;
        this.hojaDeVidaRepository = hojaDeVidaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Devuelve el usuario guardado, o null si el correo ya estaba registrado. */
    @Transactional
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
            return null;
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario login(String email, String password) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            return usuario;
        }
        return null;
    }

    public boolean passwordCoincide(Usuario usuario, String password) {
        return password != null && passwordEncoder.matches(password, usuario.getPassword());
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Actualiza los datos del perfil.
     * @return null si todo salió bien, o el mensaje de error para mostrar al usuario.
     */
    @Transactional
    public String actualizarPerfil(Long id, PerfilEdicionDTO dto) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "No se encontró tu cuenta.";
        }

        String nombre = dto.getNombre() == null ? "" : dto.getNombre().trim();
        String telefono = dto.getTelefono() == null ? "" : dto.getTelefono().trim();
        String documento = dto.getNumeroDocumento() == null ? "" : dto.getNumeroDocumento().trim();
        String email = dto.getEmail() == null ? "" : dto.getEmail().trim();
        String tipo = dto.getTipoDocumento() == null ? "" : dto.getTipoDocumento().trim();
        String nuevaPassword = dto.getNuevaPassword() == null ? "" : dto.getNuevaPassword();

        if (nombre.isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (!telefono.matches("\\d{7,15}")) {
            return "El teléfono debe tener entre 7 y 15 números.";
        }
        if (tipo.isEmpty()) {
            return "Selecciona el tipo de documento.";
        }
        if (!documento.matches("\\d{5,15}")) {
            return "El número de documento debe tener solo números (5 a 15 dígitos).";
        }
        if (dto.getEdad() < 15 || dto.getEdad() > 120) {
            return "Ingresa una edad válida (entre 15 y 120 años).";
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Ingresa un correo electrónico válido.";
        }
        Usuario otro = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
        if (otro != null && !otro.getId().equals(usuario.getId())) {
            return "Ese correo ya está registrado en otra cuenta.";
        }
        if (!nuevaPassword.isEmpty() && nuevaPassword.length() < 6) {
            return "La nueva contraseña debe tener al menos 6 caracteres.";
        }

        usuario.setNombre(nombre);
        usuario.setTelefono(telefono);
        usuario.setTipoDocumento(tipo);
        usuario.setNumeroDocumento(documento);
        usuario.setEdad(dto.getEdad());
        usuario.setEmail(email);
        if (!nuevaPassword.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        }
        return null;
    }

    /** Elimina la cuenta junto con sus postulaciones y su hoja de vida. */
    @Transactional
    public void eliminarCuenta(Long id) {
        postulacionRepository.eliminarPorUsuarioId(id);
        hojaDeVidaRepository.eliminarPorUsuarioId(id);
        usuarioRepository.deleteById(id);
    }
}
