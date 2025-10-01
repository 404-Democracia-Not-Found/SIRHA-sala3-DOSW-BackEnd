package edu.dosw.sirha.config;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitialAdminLoaderTest {

    private static final String DEFAULT_EMAIL = "admin@sirha.local";
    private static final String RAW_PASSWORD = "Admin123!";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Clock clock;

    private InitialAdminLoader loader;

    private Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.parse("2025-10-01T15:00:00Z");
        clock = Clock.fixed(now, ZoneOffset.UTC);
        loader = new InitialAdminLoader(userRepository, passwordEncoder, clock);
    }

    @Test
    void shouldCreateDefaultAdminWhenDoesNotExist() {
        when(userRepository.existsByEmail(DEFAULT_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn("hashed-password");

        loader.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertThat(savedUser.getNombre()).isEqualTo("Administrador");
        assertThat(savedUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(savedUser.getRol()).isEqualTo(Rol.ADMIN);
        assertThat(savedUser.isActivo()).isTrue();
        assertThat(savedUser.getCreadoEn()).isEqualTo(now);
        assertThat(savedUser.getActualizadoEn()).isEqualTo(now);
    }

    @Test
    void shouldNotCreateAdminIfAlreadyExists() {
        when(userRepository.existsByEmail(DEFAULT_EMAIL)).thenReturn(true);

        loader.run();

        verify(userRepository, never()).save(any(User.class));
    }
}
