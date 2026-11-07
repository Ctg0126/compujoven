package com.compujoven.compujoven.repository;

import com.compujoven.compujoven.model.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    boolean existsByUsuarioIdAndFormacionId(Long usuarioId, Long formacionId);

    List<Postulacion> findByUsuarioId(Long usuarioId);
}
