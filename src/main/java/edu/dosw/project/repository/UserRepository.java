package edu.dosw.project.repository;

import edu.dosw.project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByActivo(Boolean activo);
    
    @Query("{'roles.tipo': ?0, 'roles.activo': true}")
    List<User> findByRolesTipo(String tipo);
    
    @Query("{'searchTerms': { '$regex': ?0, '$options': 'i' }}")
    List<User> findBySearchTermsContainingIgnoreCase(String searchTerm);
}