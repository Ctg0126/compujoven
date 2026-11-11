package com.compujoven.compujoven.service;

import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.repository.FormacionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FormacionService {

    private final FormacionRepository formacionRepository;

    public FormacionService(FormacionRepository formacionRepository) {
        this.formacionRepository = formacionRepository;
    }

    public List<Formacion> listarTodas() {
        return formacionRepository.findAll();
    }

    /** Búsqueda para los jóvenes: texto libre y, opcionalmente, solo convocatorias abiertas. */
    @Transactional(readOnly = true)
    public List<Formacion> buscar(String texto, boolean soloAbiertas) {
        String t = texto == null ? "" : texto.trim();
        if (t.length() > 100) {
            t = t.substring(0, 100);
        }
        List<Formacion> lista = t.isEmpty()
                ? formacionRepository.findAll(Sort.by("nombre"))
                : formacionRepository.buscar(t);
        return soloAbiertas ? lista.stream().filter(Formacion::isAbierta).toList() : lista;
    }

    /** @throws IllegalArgumentException con un mensaje listo para mostrar si los datos no son válidos */
    @Transactional
    public Formacion publicar(Formacion datos) {
        validar(datos, null);
        Formacion nueva = new Formacion(datos.getNombre().trim(),
                limpiar(datos.getDescripcion()), limpiar(datos.getDuracion()));
        nueva.setRequisitos(limpiar(datos.getRequisitos()));
        nueva.setCapacidad(datos.getCapacidad());
        return formacionRepository.save(nueva);
    }

    /**
     * Edita una formación ya publicada (para corregir errores).
     * @throws IllegalArgumentException con un mensaje listo para mostrar si los datos no son válidos
     */
    @Transactional
    public Formacion actualizar(Long id, Formacion datos) {
        Formacion f = formacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la formación."));
        validar(datos, id);

        int ocupados = f.getCuposOcupados();
        if (datos.getCapacidad() > 0 && datos.getCapacidad() < ocupados) {
            throw new IllegalArgumentException("La capacidad no puede ser menor a los cupos ya ocupados ("
                    + ocupados + ").");
        }

        f.setNombre(datos.getNombre().trim());
        f.setDescripcion(limpiar(datos.getDescripcion()));
        f.setRequisitos(limpiar(datos.getRequisitos()));
        f.setDuracion(limpiar(datos.getDuracion()));
        f.setCapacidad(datos.getCapacidad());

        // Si con la nueva capacidad ya no quedan cupos, la convocatoria se cierra sola
        if (f.isAbierta() && f.isLlena()) {
            f.setAbierta(false);
        }
        return f;
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!formacionRepository.existsById(id)) {
            return false;
        }
        formacionRepository.deleteById(id);
        return true;
    }

    public Formacion buscarPorId(Long id) {
        return formacionRepository.findById(id).orElse(null);
    }

    /**
     * Abre o cierra la convocatoria. Devuelve false si no se pudo reabrir
     * porque la formación ya alcanzó su capacidad.
     */
    @Transactional
    public boolean cambiarEstado(Long id) {
        Formacion f = formacionRepository.findById(id).orElse(null);
        if (f == null) {
            return false;
        }
        if (!f.isAbierta() && f.isLlena()) {
            return false;
        }
        f.setAbierta(!f.isAbierta());
        return true;
    }

    // ---------- validaciones ----------

    private String limpiar(String texto) {
        return texto == null ? null : texto.trim();
    }

    private void validar(Formacion d, Long idExcluir) {
        String nombre = d.getNombre() == null ? "" : d.getNombre().trim();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (nombre.length() > 150) {
            throw new IllegalArgumentException("El nombre no puede superar los 150 caracteres.");
        }
        if (d.getDescripcion() != null && d.getDescripcion().length() > 1000) {
            throw new IllegalArgumentException("La descripción no puede superar los 1000 caracteres.");
        }
        if (d.getRequisitos() != null && d.getRequisitos().length() > 1000) {
            throw new IllegalArgumentException("Los requisitos no pueden superar los 1000 caracteres.");
        }
        if (d.getDuracion() != null && d.getDuracion().length() > 50) {
            throw new IllegalArgumentException("La duración no puede superar los 50 caracteres.");
        }
        if (d.getCapacidad() < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa.");
        }
        formacionRepository.findFirstByNombreIgnoreCaseOrderByIdAsc(nombre).ifPresent(otra -> {
            if (idExcluir == null || !otra.getId().equals(idExcluir)) {
                throw new IllegalArgumentException("Ya existe una formación con ese nombre.");
            }
        });
    }
}
