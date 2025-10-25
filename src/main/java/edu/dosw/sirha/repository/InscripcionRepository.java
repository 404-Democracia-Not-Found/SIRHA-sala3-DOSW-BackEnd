package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Inscripcion;
import edu.dosw.sirha.model.enums.EstadoInscripcion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio de acceso a datos para {@link Inscripcion}.
 * 
 * <p>Proporciona consultas para:</p>
 * <ul>
 *   <li>Inscripciones de un estudiante en un periodo</li>
 *   <li>Verificar inscripciones existentes (evitar duplicados)</li>
 * </ul>
 * 
 * @see Inscripcion
 */
public interface InscripcionRepository extends MongoRepository<Inscripcion, String> {
    /**
     * Busca inscripciones de un estudiante en un periodo.
     * 
     * @param estudianteId ID del estudiante
     * @param periodoId ID del periodo
     * @return Lista de inscripciones del estudiante en ese periodo
     */
    List<Inscripcion> findByEstudianteIdAndPeriodoId(String estudianteId, String periodoId);
    
    /**
     * Verifica si existe una inscripción activa de un estudiante en un grupo.
     * 
     * <p>Útil para evitar inscripciones duplicadas.</p>
     * 
     * @param estudianteId ID del estudiante
     * @param grupoId ID del grupo
     * @param estado Estado de la inscripción (normalmente INSCRITO)
     * @return true si existe, false si no
     */
    boolean existsByEstudianteIdAndGrupoIdAndEstado(String estudianteId, String grupoId, EstadoInscripcion estado);
}
