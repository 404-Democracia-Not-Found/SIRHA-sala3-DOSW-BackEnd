package edu.dosw.project.repository;

import edu.dosw.project.model.Inscripcion;
import edu.dosw.project.model.Inscripcion.EstadoMateria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends MongoRepository<Inscripcion, String> {
    
    /**
     * Encuentra inscripciones por estudiante
     */
    List<Inscripcion> findByEstudianteId(String estudianteId);
    
    /**
     * Encuentra inscripciones por estudiante y período académico
     */
    List<Inscripcion> findByEstudianteIdAndPeriodoAcademicoId(String estudianteId, String periodoAcademicoId);
    
    /**
     * Encuentra inscripción específica por estudiante y grupo
     */
    Optional<Inscripcion> findByEstudianteIdAndGrupoId(String estudianteId, String grupoId);
    
    /**
     * Encuentra inscripciones activas de un estudiante
     */
    List<Inscripcion> findByEstudianteIdAndActivaTrue(String estudianteId);
    
    /**
     * Encuentra inscripciones por estado
     */
    List<Inscripcion> findByEstudianteIdAndEstado(String estudianteId, EstadoMateria estado);
    
    /**
     * Encuentra inscripciones en curso de un estudiante
     */
    List<Inscripcion> findByEstudianteIdAndEstadoAndActivaTrue(String estudianteId, EstadoMateria estado);
    
    /**
     * Encuentra inscripciones por grupo
     */
    List<Inscripcion> findByGrupoId(String grupoId);
    
    /**
     * Cuenta inscripciones activas en un grupo
     */
    Long countByGrupoIdAndActivaTrue(String grupoId);
}