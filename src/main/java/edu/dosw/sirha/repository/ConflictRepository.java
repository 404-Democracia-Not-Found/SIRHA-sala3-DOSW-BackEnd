package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Conflict;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio de acceso a datos para {@link Conflict}.
 * 
 * <p>Proporciona consultas para buscar conflictos por estudiante o solicitud.</p>
 * 
 * @see Conflict
 */
public interface ConflictRepository extends MongoRepository<Conflict, String> {
    /**
     * Busca conflictos de un estudiante específico.
     * 
     * @param estudianteId ID del estudiante
     * @return Lista de conflictos del estudiante
     */
    List<Conflict> findByEstudianteId(String estudianteId);
    
    /**
     * Busca conflictos asociados a una solicitud.
     * 
     * @param solicitudId ID de la solicitud
     * @return Lista de conflictos de la solicitud
     */
    List<Conflict> findBySolicitudId(String solicitudId);
}