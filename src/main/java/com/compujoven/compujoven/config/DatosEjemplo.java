package com.compujoven.compujoven.config;

import com.compujoven.compujoven.model.EstadoPostulacion;
import com.compujoven.compujoven.model.Formacion;
import com.compujoven.compujoven.model.Postulacion;
import com.compujoven.compujoven.model.Rol;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.repository.FormacionRepository;
import com.compujoven.compujoven.repository.PostulacionRepository;
import com.compujoven.compujoven.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Datos de ejemplo que siempre están disponibles: en cada arranque se crea lo que falte
 * (se busca por nombre o correo, así que nunca se duplican ni se pisan cambios existentes).
 * Funciona igual en cualquier computador y con una base de datos recién creada.
 */
@Component
@Order(2)
public class DatosEjemplo implements CommandLineRunner {

    private static final String CLAVE_EJEMPLO = "joven123";

    private final FormacionRepository formacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PostulacionRepository postulacionRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosEjemplo(FormacionRepository formacionRepository,
                        UsuarioRepository usuarioRepository,
                        PostulacionRepository postulacionRepository,
                        PasswordEncoder passwordEncoder) {
        this.formacionRepository = formacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.postulacionRepository = postulacionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // ----- Formaciones -----
        Formacion web = formacion("Desarrollo Web Básico",
                "HTML, CSS y JavaScript desde cero para crear tu primera página web.",
                "Tener entre 16 y 28 años y acceso a un computador.", "3 meses", 30, true);
        Formacion excel = formacion("Excel para el Trabajo",
                "Fórmulas, tablas dinámicas y gráficos aplicados a casos reales de oficina.",
                "Conocimientos básicos de computación.", "6 semanas", 25, true);
        Formacion java = formacion("Programación en Java",
                "Fundamentos de programación orientada a objetos con Java y Spring Boot.",
                "Haber terminado el bachillerato y tener lógica matemática básica.", "4 meses", 20, true);
        formacion("Inglés para el Empleo",
                "Vocabulario y conversación para entrevistas y trabajo en equipo.",
                "Nivel básico de inglés (A1).", "2 meses", 40, true);
        formacion("Marketing Digital",
                "Redes sociales, contenido y publicidad para pequeños negocios.",
                "Tener un emprendimiento o interés en crear uno.", "2 meses", 15, false);

        // ----- Jóvenes de ejemplo (clave: joven123) -----
        Usuario maria = joven("María Fernanda Pérez", "3001112233", "CC", "1001234567", 22, "maria@compujoven.com");
        Usuario carlos = joven("Carlos Andrés Gómez", "3012223344", "CC", "1002345678", 24, "carlos@compujoven.com");
        Usuario laura = joven("Laura Sofía Martínez", "3023334455", "TI", "1103456789", 17, "laura@compujoven.com");

        // ----- Postulaciones de ejemplo -----
        postulacion(maria, web, EstadoPostulacion.PENDIENTE);
        postulacion(maria, excel, EstadoPostulacion.ACEPTADA);
        postulacion(carlos, web, EstadoPostulacion.PENDIENTE);
        postulacion(carlos, excel, EstadoPostulacion.RECHAZADA);
        postulacion(laura, java, EstadoPostulacion.PENDIENTE);
    }

    private Formacion formacion(String nombre, String descripcion, String requisitos,
                                String duracion, int capacidad, boolean abierta) {
        return formacionRepository.findFirstByNombreIgnoreCaseOrderByIdAsc(nombre).orElseGet(() -> {
            Formacion f = new Formacion(nombre, descripcion, duracion);
            f.setRequisitos(requisitos);
            f.setCapacidad(capacidad);
            f.setAbierta(abierta);
            return formacionRepository.save(f);
        });
    }

    private Usuario joven(String nombre, String telefono, String tipoDoc, String numeroDoc,
                          int edad, String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).orElseGet(() ->
                usuarioRepository.save(new Usuario(nombre, telefono, tipoDoc, numeroDoc, edad,
                        email, passwordEncoder.encode(CLAVE_EJEMPLO), Rol.JOVEN)));
    }

    private void postulacion(Usuario usuario, Formacion formacion, EstadoPostulacion estado) {
        if (!postulacionRepository.existsByUsuarioIdAndFormacionId(usuario.getId(), formacion.getId())) {
            Postulacion p = new Postulacion(usuario, formacion);
            p.setEstado(estado);
            postulacionRepository.save(p);
        }
    }
}
