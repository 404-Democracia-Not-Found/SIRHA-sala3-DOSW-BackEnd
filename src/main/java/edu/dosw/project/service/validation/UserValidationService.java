package edu.dosw.project.service.validation;

import edu.dosw.project.model.User;

public interface UserValidationService {
    void validateCreateUser(User user);
    void validateUpdateUser(User updatedUser, User existingUser);
}
