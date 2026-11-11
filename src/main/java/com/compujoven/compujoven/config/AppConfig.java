package com.compujoven.compujoven.config;

import com.compujoven.compujoven.model.Rol;
import com.compujoven.compujoven.model.Usuario;
import com.compujoven.compujoven.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Crea el administrador inicial si todavía no existe en la base de datos. */
    @Bean
    CommandLineRunner crearAdministrador(UsuarioRepository repo,
                                         PasswordEncoder encoder,
                                         @Value("${compujoven.admin.email}") String email,
                                         @Value("${compujoven.admin.password}") String password) {
        return args -> {
            if (!repo.existsByEmailIgnoreCase(email)) {
                repo.save(new Usuario("Administrador", "1234567890", "CC", "12345678", 30,
                        email, encoder.encode(password), Rol.ADMINISTRADOR));
            }
        };
    }
}
