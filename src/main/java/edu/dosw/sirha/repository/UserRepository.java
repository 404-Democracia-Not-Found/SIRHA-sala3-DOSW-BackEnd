package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos para {@link User}.
 * 
 * <p>Proporciona consultas para gestionar usuarios del sistema SIRHA,
 * incluyendo autenticación, búsqueda por rol y validaciones de unicidad.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 2.0
 * @since 2025-10-26
 * 
 * @see User
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Busca un usuario por su email.
     * 
     * <p>El email es único en el sistema y se usa como username para autenticación.</p>
     * 
     * @param email Email del usuario (case-insensitive)
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el email dado.
     * 
     * <p>Útil para validar unicidad antes de crear nuevos usuarios.</p>
     * 
     * @param email Email a verificar
     * @return true si el email ya está registrado, false en caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe al menos un usuario con el rol especificado.
     * 
     * <p>Útil para verificar si ya existe un usuario ADMIN antes de crear uno inicial.</p>
     * 
     * @param rol Rol a buscar
     * @return true si existe al menos un usuario con ese rol
     */
    boolean existsByRol(Rol rol);
}