package edu.dosw.project.repository;

import edu.dosw.project.model.Facultad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface FacultadRepository extends MongoRepository<Facultad, String> {
    
    /**
     * Encuentra facultad por código
     */
    Optional<Facultad> findByCodigo(String codigo);
    
    /**
     * Encuentra facultad por coordinador
     */
    Optional<Facultad> findByCoordinadorId(String coordinadorId);
    
    /**
     * Encuentra todas las facultades activas
     */
    List<Facultad> findByActivaTrue();
    
    /**
     * Encuentra facultades por nombre (búsqueda parcial, case insensitive)
     */
    List<Facultad> findByNombreContainingIgnoreCase(String nombre);
}