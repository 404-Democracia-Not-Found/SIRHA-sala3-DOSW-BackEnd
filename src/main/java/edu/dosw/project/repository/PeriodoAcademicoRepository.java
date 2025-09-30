package edu.dosw.project.repository;

import edu.dosw.project.model.PeriodoAcademico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoAcademicoRepository extends MongoRepository<PeriodoAcademico, String> {
    
    /**
     * Encuentra el período académico activo actual
     */
    Optional<PeriodoAcademico> findByActivoTrue();
    
    /**
     * Encuentra múltiples períodos activos (para control)
     */
    List<PeriodoAcademico> findAllByActivoTrue();
    
    /**
     * Encuentra período académico por año y semestre
     */
    Optional<PeriodoAcademico> findByAnioAndSemestre(Integer anio, Integer semestre);
    
    /**
     * Encuentra período por código
     */
    Optional<PeriodoAcademico> findByCodigo(String codigo);
    
    /**
     * Encuentra períodos por año
     */
    List<PeriodoAcademico> findByAnio(Integer anio);
    
    /**
     * Encuentra todos los períodos ordenados por fecha de inicio descendente
     */
    List<PeriodoAcademico> findAllByOrderByFechaInicioDesc();
    
    /**
     * Encuentra períodos vigentes para una fecha
     */
    @Query("{ 'fechaInicio': { $lte: ?0 }, 'fechaFin': { $gte: ?0 } }")
    List<PeriodoAcademico> findPeriodosVigentes(LocalDate fecha);
    
    /**
     * Encuentra el período que está en rango de solicitudes actualmente
     */
    @Query("{'activo': true, 'fechaInicioSolicitudes': {$lte: ?0}, 'fechaFinSolicitudes': {$gte: ?0}}")
    Optional<PeriodoAcademico> findPeriodoEnRangoSolicitudes(String fechaActual);
    
    /**
     * Encuentra todos los períodos por año ordenados por semestre
     */
    List<PeriodoAcademico> findByAnioOrderBySemestreAsc(Integer anio);
}