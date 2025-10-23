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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Tests unitarios para {@link InitialAdminLoader}.
 * 
 * <p>Verifica el comportamiento del componente de inicialización de usuarios por defecto,
 * incluyendo la creación de múltiples usuarios para diferentes roles.</p>
 */
@ExtendWith(MockitoExtension.class)
class InitialAdminLoaderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Clock clock;
    private InitialAdminLoader loader;
    private static final Instant FIXED_NOW = Instant.parse("2024-06-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        loader = new InitialAdminLoader(userRepository, passwordEncoder, clock);
        
        // Configurar comportamiento por defecto del passwordEncoder con lenient
        lenient().when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "hashed_" + invocation.getArgument(0));
    }

    @Test
    void runShouldCreateAllDefaultUsersWhenNoneExist() throws Exception {
        // Arrange: Simular que ningún usuario existe
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act
        loader.run();

        // Assert: Verificar que se intentó crear 4 usuarios (uno por cada rol)
        verify(userRepository, times(4)).save(any(User.class));
        
        // Capturar todos los usuarios guardados
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(4)).save(userCaptor.capture());
        
        var savedUsers = userCaptor.getAllValues();
        assertThat(savedUsers).hasSize(4);
        
        // Verificar que se crearon usuarios para cada rol
        assertThat(savedUsers).extracting(User::getRol)
                .containsExactlyInAnyOrder(Rol.ADMIN, Rol.COORDINADOR, Rol.DOCENTE, Rol.ESTUDIANTE);
        
        // Verificar que todos están activos
        assertThat(savedUsers).allMatch(User::isActivo);
    }

    @Test
    void runShouldCreateAdminUserWithCorrectCredentials() throws Exception {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act
        loader.run();

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeast(1)).save(userCaptor.capture());
        
        User adminUser = userCaptor.getAllValues().stream()
                .filter(u -> u.getRol() == Rol.ADMIN)
                .findFirst()
                .orElseThrow();

        assertThat(adminUser.getEmail()).isEqualTo("admin@sirha.local");
        assertThat(adminUser.getNombre()).isEqualTo("Administrador Sistema");
        assertThat(adminUser.getPasswordHash()).startsWith("hashed_");
        assertThat(adminUser.isActivo()).isTrue();
        assertThat(adminUser.getCreadoEn()).isEqualTo(FIXED_NOW);
    }

    @Test
    void runShouldNotCreateUsersWhenTheyAlreadyExist() throws Exception {
        // Arrange: Simular que todos los usuarios ya existen
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        loader.run();

        // Assert: No se debe guardar ningún usuario
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void runShouldCreateOnlyMissingUsers() throws Exception {
        // Arrange: Simular que solo existe el admin
        when(userRepository.existsByEmail("admin@sirha.local")).thenReturn(true);
        when(userRepository.existsByEmail(argThat(email -> !email.equals("admin@sirha.local"))))
                .thenReturn(false);

        // Act
        loader.run();

        // Assert: Solo se deben crear 3 usuarios (todos excepto admin)
        verify(userRepository, times(3)).save(any(User.class));
        
        // Verificar que no se creó el admin
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(3)).save(userCaptor.capture());
        
        assertThat(userCaptor.getAllValues())
                .extracting(User::getRol)
                .doesNotContain(Rol.ADMIN)
                .containsExactlyInAnyOrder(Rol.COORDINADOR, Rol.DOCENTE, Rol.ESTUDIANTE);
    }

    @Test
    void runShouldHashPasswordsBeforeSaving() throws Exception {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("Admin123!")).thenReturn("bcrypt_hash_admin");

        // Act
        loader.run();

        // Assert
        verify(passwordEncoder, atLeastOnce()).encode(anyString());
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeast(1)).save(userCaptor.capture());
        
        // Verificar que ninguna contraseña está en texto plano
        assertThat(userCaptor.getAllValues())
                .allMatch(user -> user.getPasswordHash().startsWith("hashed_") || user.getPasswordHash().startsWith("bcrypt_"));
    }
}
