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

/**
 * Suite de pruebas unitarias para {@link InitialAdminLoader}.
 * 
 * <p>Esta clase verifica el correcto funcionamiento del loader que crea automáticamente
 * el usuario administrador por defecto al iniciar la aplicación, incluyendo la lógica
 * de detección de existencia y encriptación de contraseña.</p>
 * 
 * <p><strong>Configuración de pruebas:</strong></p>
 * <ul>
 *   <li>Usa {@code @ExtendWith(MockitoExtension.class)} para inyección de mocks</li>
 *   <li>Mockea {@link UserRepository} para verificar operaciones de persistencia</li>
 *   <li>Mockea {@link PasswordEncoder} para validar encriptación de contraseña</li>
 *   <li>Clock fijo para timestamps determinísticos</li>
 * </ul>
 * 
 * <p><strong>Escenarios probados:</strong></p>
 * <ul>
 *   <li><strong>Creación inicial:</strong> cuando no existe admin, se crea con datos por defecto</li>
 *   <li><strong>Idempotencia:</strong> si ya existe admin, no se crea duplicado</li>
 *   <li><strong>Encriptación:</strong> contraseña se encripta antes de guardar</li>
 *   <li><strong>Valores por defecto:</strong> email, nombre, rol ADMIN, estado activo</li>
 *   <li><strong>Timestamps:</strong> createdAt se establece correctamente</li>
 * </ul>
 * 
 * <p><strong>Datos de administrador por defecto:</strong></p>
 * <ul>
 *   <li>Email: admin@sirha.local</li>
 *   <li>Contraseña: Admin123! (encriptada con BCrypt)</li>
 *   <li>Nombre: Administrador del Sistema</li>
 *   <li>Rol: ADMIN</li>
 *   <li>Estado: activo (true)</li>
 * </ul>
 * 
 * @see InitialAdminLoader
 * @see org.springframework.boot.CommandLineRunner
 */
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
