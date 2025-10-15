package edu.dosw.sirha.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.enums.SolicitudEstado;

/**
 * Repositorio de acceso a datos para {@link Solicitud}.
 * 
 * <p>Proporciona consultas optimizadas para:</p>
 * <ul>
 *   <li>Solicitudes por estudiante (ordenadas por fecha descendente)</li>
 *   <li>Solicitudes por estados múltiples (ordenadas por prioridad)</li>
 *   <li>Conteo por estado (para dashboards)</li>
 *   <li>Solicitudes por periodo y rango de fechas (para reportes)</li>
 * </ul>
 * 
 * @see Solicitud
 */
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    /**
     * Busca solicitudes de un estudiante ordenadas por fecha (más recientes primero).
     * 
     * @param estudianteId ID del estudiante
     * @return Lista de solicitudes del estudiante
     */
    List<Solicitud> findByEstudianteIdOrderByFechaSolicitudDesc(String estudianteId);
    
    /**
     * Busca solicitudes con estados específicos ordenadas por prioridad.
     * 
     * <p>Útil para coordinadores: ver solo PENDIENTES y EN_REVISION por prioridad.</p>
     * 
     * @param estados Lista de estados a buscar
     * @return Lista de solicitudes ordenadas por prioridad ascendente
     */
    List<Solicitud> findByEstadoInOrderByPrioridadAsc(List<SolicitudEstado> estados);
    
    /**
     * Cuenta solicitudes por estado.
     * 
     * @param estado Estado a contar
     * @return Cantidad de solicitudes en ese estado
     */
    long countByEstado(SolicitudEstado estado);
    
    /**
     * Busca solicitudes de un periodo en un rango de fechas.
     * 
     * @param periodoId ID del periodo
     * @param inicio Fecha inicial (inclusive)
     * @param fin Fecha final (inclusive)
     * @return Lista de solicitudes que cumplen criterios
     */
    List<Solicitud> findByPeriodoIdAndFechaSolicitudBetween(String periodoId, Instant inicio, Instant fin);
}