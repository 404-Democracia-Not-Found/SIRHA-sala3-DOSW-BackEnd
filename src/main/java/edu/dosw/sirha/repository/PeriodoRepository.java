package edu.dosw.sirha.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.dosw.sirha.model.Periodo;

/**
 * Repositorio de acceso a datos para {@link Periodo}.
 * 
 * <p>Proporciona consultas para:</p>
 * <ul>
 *   <li>Obtener el periodo activo (solo uno puede estar activo)</li>
 *   <li>Buscar periodo que contiene una fecha específica</li>
 * </ul>
 * 
 * @see Periodo
 */
public interface PeriodoRepository extends MongoRepository<Periodo, String> {
    /**
     * Busca el periodo actualmente activo.
     * 
     * <p>Solo debe haber uno activo a la vez.</p>
     * 
     * @return Optional con el periodo activo si existe
     */
    Optional<Periodo> findByActivoTrue();
    
    /**
     * Busca el periodo que contiene una fecha específica.
     * 
     * <p>Verifica que la fecha esté entre fechaInicio y fechaFin.</p>
     * 
     * @param inicio Fecha a verificar (se pasa dos veces para la consulta)
     * @param fin Fecha a verificar (se pasa dos veces para la consulta)
     * @return Optional con el periodo que contiene la fecha
     */
    Optional<Periodo> findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(Instant inicio, Instant fin);
}