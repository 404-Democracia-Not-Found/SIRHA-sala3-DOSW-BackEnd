package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Facultad;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio de acceso a datos para {@link Facultad}.
 * 
 * <p>Proporciona solo operaciones CRUD estándar de MongoDB.
 * No requiere consultas personalizadas adicionales.</p>
 * 
 * @see Facultad
 */
public interface FacultadRepository extends MongoRepository<Facultad, String> {
}
