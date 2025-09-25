package edu.dosw.project.service.validation;

import edu.dosw.project.model.User;

/**
 * Interface para validaciones de usuarios siguiendo el principio de Responsabilidad Única
 * Permite diferentes implementaciones de validación (ISP - Interface Segregation Principle)
 */
public interface UserValidationService {
    void validateCreateUser(User user);
    void validateUpdateUser(User updatedUser, User existingUser);
    void validateEmail(String email);
    void validateUserRoles(User user);
    boolean isValidEmail(String email);
}