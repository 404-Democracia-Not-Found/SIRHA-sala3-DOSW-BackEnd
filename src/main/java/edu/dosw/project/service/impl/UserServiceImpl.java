package edu.dosw.project.service.impl;

import edu.dosw.project.exception.ResourceNotFoundException;
import edu.dosw.project.model.User;
import edu.dosw.project.repository.UserRepository;
import edu.dosw.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Encriptar contraseña si existe
        if (StringUtils.hasText(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        // Establecer fecha de creación
        user.setFechaCreacion(LocalDateTime.now());
        
        // Validar y establecer roles activos
        if (user.getRoles() != null) {
            user.getRoles().forEach(rol -> {
                if (rol.getFechaAsignacion() == null) {
                    rol.setFechaAsignacion(LocalDateTime.now());
                }
            });
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        if (!StringUtils.hasText(id)) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findByRoleType(String roleType) {
        if (!StringUtils.hasText(roleType)) {
            return List.of();
        }
        return userRepository.findByRolesTipo(roleType);
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
    public User updateUser(String id, User updatedUser) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }

        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        validateUserUpdate(updatedUser, existingUser);

        // Actualizar campos permitidos
        if (StringUtils.hasText(updatedUser.getNombre())) {
            existingUser.setNombre(updatedUser.getNombre());
        }
        if (StringUtils.hasText(updatedUser.getGenero())) {
            existingUser.setGenero(updatedUser.getGenero());
        }
        if (StringUtils.hasText(updatedUser.getPaisNacimiento())) {
            existingUser.setPaisNacimiento(updatedUser.getPaisNacimiento());
        }
        if (updatedUser.getActivo() != null) {
            existingUser.setActivo(updatedUser.getActivo());
        }
        
        // Actualizar contraseña si se proporciona
        if (StringUtils.hasText(updatedUser.getPasswordHash())) {
            existingUser.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
        }

        // Actualizar roles si se proporcionan
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            updatedUser.getRoles().forEach(rol -> {
                if (rol.getFechaAsignacion() == null) {
                    rol.setFechaAsignacion(LocalDateTime.now());
                }
            });
            existingUser.setRoles(updatedUser.getRoles());
        }

        return userRepository.save(existingUser);
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

    @Override
    public boolean existsByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return List.of();
        }
        return userRepository.findBySearchTermsContainingIgnoreCase(searchTerm);
    }

    @Override
    public boolean hasRole(User user, String roleType) {
        if (user == null || !StringUtils.hasText(roleType) || user.getRoles() == null) {
            return false;
        }

        return user.getRoles().stream()
            .anyMatch(rol -> roleType.equals(rol.getTipo()) && Boolean.TRUE.equals(rol.getActivo()));
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        if (!StringUtils.hasText(user.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
    }

    private void validateUserUpdate(User updatedUser, User existingUser) {
        if (updatedUser == null) {
            throw new IllegalArgumentException("Los datos de actualización no pueden ser nulos");
        }

        // Validar email si se está cambiando
        if (StringUtils.hasText(updatedUser.getEmail()) && 
            !updatedUser.getEmail().equals(existingUser.getEmail())) {
            
            if (!isValidEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("El formato del email no es válido");
            }
            
            if (existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado por otro usuario");
            }
            
            existingUser.setEmail(updatedUser.getEmail());
        }
    }

    @Override
    public String getProgramaAcademico(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }
        
        User user = userOpt.get();
        
        // Buscar el programa académico según el rol del usuario
        if (user.getRoles() != null) {
            for (User.Rol rol : user.getRoles()) {
                if (rol.getProgramaAcademicoId() != null) {
                    return rol.getProgramaAcademicoId();
                }
            }
        }
        
        return null; // No tiene programa asignado
    }

    private boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && EMAIL_PATTERN.matcher(email).matches();
    }
}