package com.compujoven.compujoven.repository;

import com.compujoven.compujoven.model.Formacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FormacionRepository extends JpaRepository<Formacion, Long> {

    Optional<Formacion> findFirstByNombreIgnoreCaseOrderByIdAsc(String nombre);

    /** Búsqueda por texto en nombre, descripción o requisitos (sin distinguir mayúsculas). */
    @Query("select f from Formacion f " +
           "where lower(f.nombre) like lower(concat('%', :texto, '%')) " +
           "   or lower(f.descripcion) like lower(concat('%', :texto, '%')) " +
           "   or lower(f.requisitos) like lower(concat('%', :texto, '%')) " +
           "order by f.nombre")
    List<Formacion> buscar(@Param("texto") String texto);
}
