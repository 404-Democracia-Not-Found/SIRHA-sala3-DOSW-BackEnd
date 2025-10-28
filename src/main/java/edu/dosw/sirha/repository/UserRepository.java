package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos para {@link User}.
 * 
 * <p>Proporciona operaciones CRUD estándar de MongoDB y consultas personalizadas
 * por email (usado como username para autenticación).</p>
 * 
 * @see User
 */
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Busca un usuario por email.
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe, vacío si no
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el email dado.
     * 
     * <p>Útil para validar unicidad en registro.</p>
     * 
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
}
