package edu.dosw.sirha.config;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialAdminLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    private static final String DEFAULT_EMAIL = "admin@sirha.local";
    private static final String DEFAULT_PASSWORD = "Admin123!";

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(DEFAULT_EMAIL)) {
            return;
        }
        User admin = User.builder()
                .nombre("Administrador")
                .email(DEFAULT_EMAIL)
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .rol(Rol.ADMIN)
                .activo(true)
                .creadoEn(Instant.now(clock))
                .actualizadoEn(Instant.now(clock))
                .build();
        userRepository.save(admin);
        log.info("Se creó el usuario administrador por defecto con email {}", DEFAULT_EMAIL);
    }
}
