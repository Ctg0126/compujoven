package com.compujoven.compujoven.repository;

import com.compujoven.compujoven.model.HojaDeVida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface HojaDeVidaRepository extends JpaRepository<HojaDeVida, Long> {

    /** Hoja de vida completa (incluye el archivo). */
    Optional<HojaDeVida> findByUsuarioId(Long usuarioId);

    /** Solo nombre, tamaño y fecha: no trae el contenido del archivo. */
    Optional<HojaDeVidaResumen> findResumenByUsuarioId(Long usuarioId);

    /** IDs de los usuarios que ya subieron su hoja de vida. */
    @Query("select h.usuario.id from HojaDeVida h")
    Set<Long> idsUsuariosConHojaDeVida();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from HojaDeVida h where h.usuario.id = :usuarioId")
    void eliminarPorUsuarioId(@Param("usuarioId") Long usuarioId);
}
