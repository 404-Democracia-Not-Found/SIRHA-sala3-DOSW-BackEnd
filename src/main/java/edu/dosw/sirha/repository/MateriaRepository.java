package edu.dosw.sirha.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.dosw.sirha.model.Materia;

/**
 * Repositorio de acceso a datos para {@link Materia}.
 * 
 * <p>Proporciona consultas para filtrar materias activas por facultad
 * y búsqueda por términos (mnemónico, nombre, aliases).</p>
 * 
 * @see Materia
 */
public interface MateriaRepository extends MongoRepository<Materia, String> {
    /**
     * Busca materias activas de una facultad.
     * 
     * @param facultadId ID de la facultad
     * @return Lista de materias activas de la facultad
     */
    List<Materia> findByFacultadIdAndActivoTrue(String facultadId);
    
    /**
     * Busca materias por término de búsqueda (case-insensitive).
     * 
     * <p>Busca en el campo searchTerms que contiene mnemonico, nombre y aliases.</p>
     * 
     * @param term Término de búsqueda
     * @return Lista de materias que contienen el término
     */
    List<Materia> findBySearchTermsContainingIgnoreCase(String term);
}