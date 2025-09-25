package edu.dosw.project.service;

import edu.dosw.project.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findByRoleType(String roleType);
    List<User> findAll();
    List<User> findActiveUsers();
    User updateUser(String id, User user);
    void deleteUser(String id);
    boolean existsByEmail(String email);
    List<User> searchUsers(String searchTerm);
    boolean hasRole(User user, String roleType);
}