package com.compujoven.compujoven.service;

import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.model.Postulacion;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.repository.FormacionRepository;
import com.compujoven.compujoven.repository.PostulacionRepository;
import com.compujoven.compujoven.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class PostulacionService {

    public enum Resultado { OK, YA_POSTULADO, CERRADA, NO_ENCONTRADA }

    private final PostulacionRepository postulacionRepository;
    private final FormacionRepository formacionRepository;
    private final UsuarioRepository usuarioRepository;

    public PostulacionService(PostulacionRepository postulacionRepository,
                              FormacionRepository formacionRepository,
                              UsuarioRepository usuarioRepository) {
        this.postulacionRepository = postulacionRepository;
        this.formacionRepository = formacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Resultado postular(Long usuarioId, Long formacionId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        Formacion formacion = formacionRepository.findById(formacionId).orElse(null);

        if (usuario == null || formacion == null) {
            return Resultado.NO_ENCONTRADA;
        }
        if (!formacion.isAbierta()) {
            return Resultado.CERRADA;
        }
        if (postulacionRepository.existsByUsuarioIdAndFormacionId(usuarioId, formacionId)) {
            return Resultado.YA_POSTULADO;
        }
        postulacionRepository.save(new Postulacion(usuario, formacion));
        return Resultado.OK;
    }

    /** IDs de las formaciones a las que el usuario ya se postuló. */
    @Transactional(readOnly = true)
    public Set<Long> idsFormacionesPostuladas(Long usuarioId) {
        Set<Long> ids = new HashSet<>();
        postulacionRepository.findByUsuarioId(usuarioId)
                .forEach(p -> ids.add(p.getFormacion().getId()));
        return ids;
    }
}
