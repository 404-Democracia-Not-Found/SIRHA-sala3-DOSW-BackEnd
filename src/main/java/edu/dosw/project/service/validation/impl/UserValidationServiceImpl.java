package edu.dosw.project.service.validation.impl;

import edu.dosw.project.model.User;
import edu.dosw.project.repository.UserRepository;
import edu.dosw.project.service.validation.UserValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Implementación del servicio de validación siguiendo SRP
 * Se enfoca únicamente en validar usuarios y sus datos
 */
@Service
public class UserValidationServiceImpl implements UserValidationService {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private final UserRepository userRepository;

    public UserValidationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validateCreateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        validateEmail(user.getEmail());
        
        if (!StringUtils.hasText(user.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Validación específica para SIRHA: nombre debe tener al menos 2 palabras
        if (user.getNombre().trim().split("\\s+").length < 2) {
            throw new IllegalArgumentException("El nombre debe incluir al menos nombre y apellido");
        }

        validateUserRoles(user);
    }

    @Override
    public void validateUpdateUser(User updatedUser, User existingUser) {
        if (updatedUser == null) {
            throw new IllegalArgumentException("Los datos de actualización no pueden ser nulos");
        }

        // Validar email si se está cambiando
        if (StringUtils.hasText(updatedUser.getEmail()) && 
            !updatedUser.getEmail().equals(existingUser.getEmail())) {
            
            validateEmail(updatedUser.getEmail());
            
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado por otro usuario");
            }
            
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Validar roles si se están actualizando
        if (updatedUser.getRoles() != null) {
            validateUserRoles(updatedUser);
        }
    }

    @Override
    public void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        // Validación específica para SIRHA: debe ser email institucional
        if (!email.toLowerCase().contains("@dosw.edu.co") && 
            !email.toLowerCase().contains("@estudiante.dosw.edu.co")) {
            throw new IllegalArgumentException("Debe usar un email institucional (@dosw.edu.co)");
        }
    }

    @Override
    public void validateUserRoles(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("El usuario debe tener al menos un rol asignado");
        }

        // Validar que los roles sean válidos para SIRHA
        String[] validRoles = {"ESTUDIANTE", "DOCENTE", "COORDINADOR", "ADMIN", "ADMINISTRATIVO"};
        
        for (User.Rol rol : user.getRoles()) {
            if (!StringUtils.hasText(rol.getTipo())) {
                throw new IllegalArgumentException("El tipo de rol no puede estar vacío");
            }

            boolean isValidRole = false;
            for (String validRole : validRoles) {
                if (validRole.equals(rol.getTipo().toUpperCase())) {
                    isValidRole = true;
                    break;
                }
            }

            if (!isValidRole) {
                throw new IllegalArgumentException("Rol inválido: " + rol.getTipo() + 
                    ". Los roles válidos son: ESTUDIANTE, DOCENTE, COORDINADOR, ADMIN, ADMINISTRATIVO");
            }

            // Solo puede haber un rol activo por usuario en SIRHA
            if (user.getRoles().stream().filter(r -> Boolean.TRUE.equals(r.getActivo())).count() > 1) {
                throw new IllegalArgumentException("Un usuario solo puede tener un rol activo en SIRHA");
            }
        }
    }

    @Override
    public boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && EMAIL_PATTERN.matcher(email).matches();
    }
}