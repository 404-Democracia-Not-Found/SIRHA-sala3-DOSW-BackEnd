package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Grupo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para {@link Grupo}.
 * 
 * <p>Proporciona consultas para buscar grupos activos de una materia
 * y buscar grupos por código único dentro de un periodo.</p>
 * 
 * @see Grupo
 */
public interface GrupoRepository extends MongoRepository<Grupo, String> {
    /**
     * Busca grupos activos de una materia.
     * 
     * @param materiaId ID de la materia
     * @return Lista de grupos activos de la materia
     */
    List<Grupo> findByMateriaIdAndActivoTrue(String materiaId);
    
    /**
     * Busca un grupo por código dentro de un periodo.
     * 
     * <p>El código es único dentro de un periodo académico.</p>
     * 
     * @param codigo Código del grupo (ej: "SIST-101-A")
     * @param periodoId ID del periodo
     * @return Optional con el grupo si existe
     */
    Optional<Grupo> findByCodigoAndPeriodoId(String codigo, String periodoId);
}
