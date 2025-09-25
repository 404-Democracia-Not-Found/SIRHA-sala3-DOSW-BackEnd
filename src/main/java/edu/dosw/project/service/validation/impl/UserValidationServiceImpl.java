package edu.dosw.project.service.validation.impl;

import edu.dosw.project.model.User;
import edu.dosw.project.service.validation.UserValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementación del servicio de validación de usuarios
 */
@Service
public class UserValidationServiceImpl implements UserValidationService {

    @Override
    public void validateCreateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("El email es requerido");
        }
        if (!StringUtils.hasText(user.getNombre())) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        validateEmail(user.getEmail());
    }

    @Override
    public void validateUpdateUser(User updatedUser, User existingUser) {
        if (updatedUser == null) {
            throw new IllegalArgumentException("Los datos del usuario no pueden ser nulos");
        }
        if (existingUser == null) {
            throw new IllegalArgumentException("Usuario existente no encontrado");
        }
        if (StringUtils.hasText(updatedUser.getEmail())) {
            validateEmail(updatedUser.getEmail());
        }
    }

    private void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }
}
