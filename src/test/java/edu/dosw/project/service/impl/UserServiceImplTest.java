package edu.dosw.project.service.impl;

import edu.dosw.project.exception.ResourceNotFoundException;
import edu.dosw.project.model.User;
import edu.dosw.project.repository.UserRepository;
import edu.dosw.project.service.validation.UserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidationService validationService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("test-id");
        testUser.setNombre("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("password");
        testUser.setActivo(true);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(validationService).validateCreateUser(testUser);
        verify(userRepository).save(testUser);
    }

    @Test
    void testCreateUser_EmailExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(testUser);
        });
    }

    @Test
    void testFindById_Success() {
        // Arrange
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findById("test-id");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findById("non-existent-id");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void testFindAll() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
    }

    @Test
    void testExistsByEmail_True() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail("test@example.com");

        // Assert
        assertTrue(result);
    }

    @Test
    void testExistsByEmail_False() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.existsById(anyString())).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> {
            userService.deleteUser("test-id");
        });
        verify(userRepository).deleteById("test-id");
    }

    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        when(userRepository.existsById(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser("non-existent-id");
        });
    }
}