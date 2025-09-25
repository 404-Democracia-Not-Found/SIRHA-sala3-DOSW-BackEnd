package edu.dosw.project.service.impl;

import edu.dosw.project.exception.ResourceNotFoundException;
import edu.dosw.project.model.User;
import edu.dosw.project.repository.UserRepository;
import edu.dosw.project.service.UserService;
import edu.dosw.project.service.validation.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación optimizada del servicio de usuarios siguiendo principios SOLID
 * - SRP: Se enfoca únicamente en la lógica de negocio de usuarios
 * - OCP: Abierto para extensión a través de interfaces
 * - DIP: Depende de abstracciones (UserValidationService)
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidationService validationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder,
                          UserValidationService validationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    @Override
    public User createUser(User user) {
        // Delegamos la validación al servicio especializado (SRP)
        validationService.validateCreateUser(user);
        
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Encriptar contraseña si existe
        if (StringUtils.hasText(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        // Establecer metadatos de auditoría
        setupUserMetadata(user);
        
        // Configurar roles si existen
        if (user.getRoles() != null) {
            setupUserRoles(user);
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(String id, User updatedUser) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }

        User existingUser = findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Delegamos la validación al servicio especializado (SRP)
        validationService.validateUpdateUser(updatedUser, existingUser);

        // Aplicar actualizaciones específicas
        applyUserUpdates(existingUser, updatedUser);

        return userRepository.save(existingUser);
    }

    /**
     * Configura los metadatos del usuario (SRP - Responsabilidad Única)
     */
    private void setupUserMetadata(User user) {
        user.setFechaCreacion(LocalDateTime.now());
        
        // Configurar términos de búsqueda para optimizar consultas
        if (StringUtils.hasText(user.getNombre()) && StringUtils.hasText(user.getEmail())) {
            user.setSearchTerms(List.of(
                user.getNombre().toLowerCase(),
                user.getEmail().toLowerCase(),
                user.getNombre().toLowerCase().split(" ")[0] // Primer nombre
            ));
        }
    }

    /**
     * Configura los roles del usuario (SRP - Responsabilidad Única)
     */
    private void setupUserRoles(User user) {
        user.getRoles().forEach(rol -> {
            if (rol.getFechaAsignacion() == null) {
                rol.setFechaAsignacion(LocalDateTime.now());
            }
            // Activar rol por defecto si no se especifica
            if (rol.getActivo() == null) {
                rol.setActivo(true);
            }
        });
    }

    /**
     * Aplica las actualizaciones al usuario existente (SRP - Responsabilidad Única)
     */
    private void applyUserUpdates(User existingUser, User updatedUser) {
        // Actualizar campos básicos
        updateBasicFields(existingUser, updatedUser);
        
        // Actualizar contraseña si se proporciona
        updatePassword(existingUser, updatedUser);
        
        // Actualizar roles si se proporcionan
        updateRoles(existingUser, updatedUser);
    }

    private void updateBasicFields(User existing, User updated) {
        if (StringUtils.hasText(updated.getNombre())) {
            existing.setNombre(updated.getNombre());
        }
        if (StringUtils.hasText(updated.getGenero())) {
            existing.setGenero(updated.getGenero());
        }
        if (StringUtils.hasText(updated.getPaisNacimiento())) {
            existing.setPaisNacimiento(updated.getPaisNacimiento());
        }
        if (updated.getActivo() != null) {
            existing.setActivo(updated.getActivo());
        }
    }

    private void updatePassword(User existing, User updated) {
        if (StringUtils.hasText(updated.getPasswordHash())) {
            existing.setPasswordHash(passwordEncoder.encode(updated.getPasswordHash()));
        }
    }

    private void updateRoles(User existing, User updated) {
        if (updated.getRoles() != null && !updated.getRoles().isEmpty()) {
            updated.getRoles().forEach(rol -> {
                if (rol.getFechaAsignacion() == null) {
                    rol.setFechaAsignacion(LocalDateTime.now());
                }
            });
            existing.setRoles(updated.getRoles());
        }
    }

    // Métodos de consulta optimizados (Query methods)
    @Override
    public Optional<User> findById(String id) {
        return StringUtils.hasText(id) ? userRepository.findById(id) : Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return StringUtils.hasText(email) ? userRepository.findByEmail(email) : Optional.empty();
    }

    @Override
    public List<User> findByRoleType(String roleType) {
        return StringUtils.hasText(roleType) ? userRepository.findByRolesTipo(roleType) : List.of();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findActiveUsers() {
        return userRepository.findByActivo(true);
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        return StringUtils.hasText(searchTerm) ? 
            userRepository.findBySearchTermsContainingIgnoreCase(searchTerm) : List.of();
    }

    // Métodos de utilidad y validación
    @Override
    public boolean existsByEmail(String email) {
        return StringUtils.hasText(email) && userRepository.existsByEmail(email);
    }

    @Override
    public boolean hasRole(User user, String roleType) {
        if (user == null || !StringUtils.hasText(roleType) || user.getRoles() == null) {
            return false;
        }
        return user.getRoles().stream()
            .anyMatch(rol -> roleType.equals(rol.getTipo()) && Boolean.TRUE.equals(rol.getActivo()));
    }

    @Override
    public void deleteUser(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }
}