package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Facultad;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para {@link Facultad}.
 * 
 * <p>Proporciona operaciones CRUD estándar de MongoDB y consultas
 * personalizadas para gestionar facultades.</p>
 * 
 * @see Facultad
 */
public interface FacultadRepository extends MongoRepository<Facultad, String> {
    
    /**
     * Busca una facultad por su nombre exacto.
     * 
     * @param nombre nombre de la facultad
     * @return facultad si existe
     */
    Optional<Facultad> findByNombre(String nombre);
    
    /**
     * Busca todas las facultades activas.
     * 
     * @return lista de facultades activas
     */
    List<Facultad> findByActivoTrue();
}
