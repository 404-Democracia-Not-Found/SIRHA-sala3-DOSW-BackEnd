package edu.dosw.project.repository;

import edu.dosw.project.model.Grupo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends MongoRepository<Grupo, String> {
    
    /**
     * Encuentra grupos por materia
     */
    List<Grupo> findByMateriaId(String materiaId);
    
    /**
     * Encuentra grupos por materia y período académico
     */
    List<Grupo> findByMateriaIdAndPeriodoAcademicoId(String materiaId, String periodoAcademicoId);
    
    /**
     * Encuentra grupo por código
     */
    Optional<Grupo> findByCodigo(String codigo);
    
    /**
     * Encuentra grupos con cupo disponible para una materia específica
     */
    @Query("{'materiaId': ?0, 'periodoAcademicoId': ?1, 'activo': true, $expr: {$lt: ['$estudiantesInscritos', '$cupoMaximo']}}")
    List<Grupo> findGruposConCupoDisponible(String materiaId, String periodoAcademicoId);
    
    /**
     * Encuentra grupos en capacidad crítica (más del 90% ocupados)
     */
    @Query("{'activo': true, $expr: {$gte: ['$estudiantesInscritos', {$multiply: ['$cupoMaximo', 0.9]}]}}")
    List<Grupo> findGruposEnCapacidadCritica();
    
    /**
     * Encuentra grupos por profesor y período
     */
    List<Grupo> findByProfesorIdAndPeriodoAcademicoId(String profesorId, String periodoAcademicoId);
    
    /**
     * Encuentra todos los grupos activos de un período
     */
    List<Grupo> findByPeriodoAcademicoIdAndActivoTrue(String periodoAcademicoId);
}