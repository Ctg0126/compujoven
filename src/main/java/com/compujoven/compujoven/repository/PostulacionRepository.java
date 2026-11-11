package com.compujoven.compujoven.repository;

import com.compujoven.compujoven.model.EstadoPostulacion;
import com.compujoven.compujoven.model.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    boolean existsByUsuarioIdAndFormacionId(Long usuarioId, Long formacionId);

    List<Postulacion> findByUsuarioId(Long usuarioId);

    /** Cupos ocupados de una formación: todas las postulaciones menos las rechazadas. */
    long countByFormacionIdAndEstadoNot(Long formacionId, EstadoPostulacion estado);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Postulacion p where p.usuario.id = :usuarioId")
    void eliminarPorUsuarioId(@Param("usuarioId") Long usuarioId);
}
