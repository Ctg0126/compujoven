package com.compujoven.compujoven.service;

import com.compujoven.compujoven.model.EstadoPostulacion;
import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.model.Postulacion;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.repository.FormacionRepository;
import com.compujoven.compujoven.repository.PostulacionRepository;
import com.compujoven.compujoven.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostulacionService {

    public enum Resultado { OK, YA_POSTULADO, CERRADA, SIN_CUPOS, NO_ENCONTRADA }

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

        long ocupados = postulacionRepository.countByFormacionIdAndEstadoNot(formacionId, EstadoPostulacion.RECHAZADA);
        if (formacion.getCapacidad() > 0 && ocupados >= formacion.getCapacidad()) {
            formacion.setAbierta(false);   // se llenó: se cierra sola
            return Resultado.SIN_CUPOS;
        }

        postulacionRepository.save(new Postulacion(usuario, formacion));

        // Si con esta postulación se completó la capacidad, la convocatoria se cierra
        if (formacion.getCapacidad() > 0 && ocupados + 1 >= formacion.getCapacidad()) {
            formacion.setAbierta(false);
        }
        return Resultado.OK;
    }

    /** Postulaciones del usuario, para la pantalla "Mi perfil". */
    @Transactional(readOnly = true)
    public List<Postulacion> listarPorUsuario(Long usuarioId) {
        List<Postulacion> lista = postulacionRepository.findByUsuarioId(usuarioId);
        lista.forEach(p -> p.getFormacion().getNombre()); // carga la formación
        return lista;
    }

    /** Estado de cada postulación del usuario, por id de formación. */
    @Transactional(readOnly = true)
    public Map<Long, EstadoPostulacion> estadosPorFormacion(Long usuarioId) {
        Map<Long, EstadoPostulacion> estados = new HashMap<>();
        postulacionRepository.findByUsuarioId(usuarioId)
                .forEach(p -> estados.put(p.getFormacion().getId(), p.getEstado()));
        return estados;
    }

    /**
     * El administrador acepta, rechaza o vuelve a dejar pendiente una postulación.
     * @return null si todo salió bien, o el mensaje de error para mostrar.
     */
    @Transactional
    public String cambiarEstado(Long postulacionId, EstadoPostulacion nuevo) {
        Postulacion p = postulacionRepository.findById(postulacionId).orElse(null);
        if (p == null) {
            return "No se encontró la postulación.";
        }
        Formacion f = p.getFormacion();
        boolean vuelveAOcuparCupo = p.getEstado() == EstadoPostulacion.RECHAZADA
                && nuevo != EstadoPostulacion.RECHAZADA;
        if (vuelveAOcuparCupo && f.getCapacidad() > 0) {
            long ocupados = postulacionRepository.countByFormacionIdAndEstadoNot(f.getId(), EstadoPostulacion.RECHAZADA);
            if (ocupados >= f.getCapacidad()) {
                return "No hay cupos disponibles para volver a considerar a este joven.";
            }
        }
        p.setEstado(nuevo);
        return null;
    }
}
