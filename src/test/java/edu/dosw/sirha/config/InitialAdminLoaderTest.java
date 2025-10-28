package edu.dosw.sirha.config;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas para {@link InitialAdminLoader}.
 * 
 * <p>Verifica el comportamiento de creación del usuario ADMIN inicial
 * desde variables de entorno.</p>
 * 
 * <p><strong>Escenarios probados:</strong></p>
 * <ul>
 *   <li>Creación exitosa cuando no existe ADMIN y variables están configuradas</li>
 *   <li>No creación cuando ya existe un usuario ADMIN</li>
 *   <li>No creación cuando faltan variables de entorno</li>
 *   <li>Hasheo correcto de contraseña</li>
 *   <li>Configuración correcta de timestamps</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class InitialAdminLoaderTest {

    private static final Instant FIXED_NOW = Instant.parse("2025-01-15T12:00:00Z");
    private static final String TEST_ADMIN_EMAIL = "admin@test.local";
    private static final String TEST_ADMIN_PASSWORD = "TestPassword123!";
    private static final String TEST_ADMIN_NAME = "Test Admin";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Clock clock;
    private InitialAdminLoader initialAdminLoader;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        initialAdminLoader = new InitialAdminLoader(userRepository, passwordEncoder, clock);
    }

    @Test
    void runShouldCreateAdminUserWhenNoneExistsAndEnvVarsAreSet() {
        // Arrange
        ReflectionTestUtils.setField(initialAdminLoader, "adminEmail", TEST_ADMIN_EMAIL);
        ReflectionTestUtils.setField(initialAdminLoader, "adminPassword", TEST_ADMIN_PASSWORD);
        ReflectionTestUtils.setField(initialAdminLoader, "adminName", TEST_ADMIN_NAME);

        when(userRepository.existsByRol(Rol.ADMIN)).thenReturn(false);
        when(passwordEncoder.encode(TEST_ADMIN_PASSWORD)).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        initialAdminLoader.run();

        // Assert
        verify(userRepository).existsByRol(Rol.ADMIN);
        verify(passwordEncoder).encode(TEST_ADMIN_PASSWORD);
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(TEST_ADMIN_EMAIL.toLowerCase()) &&
                user.getNombre().equals(TEST_ADMIN_NAME) &&
                user.getRol().equals(Rol.ADMIN) &&
                user.isActivo() &&
                user.getCreadoEn().equals(FIXED_NOW) &&
                user.getActualizadoEn().equals(FIXED_NOW) &&
                user.getPasswordHash().equals("encoded-password")
        ));
    }

    @Test
    void runShouldNotCreateAdminWhenOneAlreadyExists() {
        // Arrange
        ReflectionTestUtils.setField(initialAdminLoader, "adminEmail", TEST_ADMIN_EMAIL);
        ReflectionTestUtils.setField(initialAdminLoader, "adminPassword", TEST_ADMIN_PASSWORD);
        ReflectionTestUtils.setField(initialAdminLoader, "adminName", TEST_ADMIN_NAME);

        when(userRepository.existsByRol(Rol.ADMIN)).thenReturn(true);

        // Act
        initialAdminLoader.run();

        // Assert
        verify(userRepository).existsByRol(Rol.ADMIN);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void runShouldNotCreateAdminWhenEmailIsNull() {
        // Arrange
        ReflectionTestUtils.setField(initialAdminLoader, "adminEmail", null);
        ReflectionTestUtils.setField(initialAdminLoader, "adminPassword", TEST_ADMIN_PASSWORD);
        ReflectionTestUtils.setField(initialAdminLoader, "adminName", TEST_ADMIN_NAME);

        when(userRepository.existsByRol(Rol.ADMIN)).thenReturn(false);

        // Act
        initialAdminLoader.run();

        // Assert
        verify(userRepository).existsByRol(Rol.ADMIN);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void runShouldNotCreateAdminWhenPasswordIsBlank() {
        // Arrange
        ReflectionTestUtils.setField(initialAdminLoader, "adminEmail", TEST_ADMIN_EMAIL);
        ReflectionTestUtils.setField(initialAdminLoader, "adminPassword", "   ");
        ReflectionTestUtils.setField(initialAdminLoader, "adminName", TEST_ADMIN_NAME);

        when(userRepository.existsByRol(Rol.ADMIN)).thenReturn(false);

        // Act
        initialAdminLoader.run();

        // Assert
        verify(userRepository).existsByRol(Rol.ADMIN);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void runShouldNotCreateAdminWhenNameIsNull() {
        // Arrange
        ReflectionTestUtils.setField(initialAdminLoader, "adminEmail", TEST_ADMIN_EMAIL);
        ReflectionTestUtils.setField(initialAdminLoader, "adminPassword", TEST_ADMIN_PASSWORD);
        ReflectionTestUtils.setField(initialAdminLoader, "adminName", null);

        when(userRepository.existsByRol(Rol.ADMIN)).thenReturn(false);

        // Act
        initialAdminLoader.run();

        // Assert
        verify(userRepository).existsByRol(Rol.ADMIN);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}
