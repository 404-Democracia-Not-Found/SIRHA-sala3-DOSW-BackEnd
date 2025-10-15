package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.enums.SolicitudEstado;

import java.time.Instant;
import java.util.List;

/**
 * Servicio central de gestión de solicitudes de cambio (CORE de SIRHA).
 * 
 * <p>Proporciona operaciones CRUD para solicitudes, cambios de estado,
 * filtrado por estudiante/periodo/estado y estadísticas.</p>
 * 
 * <p>Este servicio orquesta la lógica de negocio principal del sistema:</p>
 * <ul>
 *   <li>Validación de prerequisitos y conflictos al crear solicitudes</li>
 *   <li>Gestión del ciclo de vida: PENDIENTE → EN_REVISION → APROBADA/RECHAZADA</li>
 *   <li>Registro de historial automático de cambios</li>
 *   <li>Aplicación de cambios a inscripciones al aprobar</li>
 * </ul>
 * 
 * @see edu.dosw.sirha.model.Solicitud
 * @see SolicitudRequest
 * @see SolicitudResponse
 */
public interface SolicitudService {

	/**
	 * Crea una nueva solicitud de cambio.
	 * 
	 * <p>Valida prerequisitos, conflictos de horario, cupos disponibles
	 * y genera código único de solicitud.</p>
	 * 
	 * @param request Datos de la solicitud a crear
	 * @return La solicitud creada con ID y código asignados
	 * @throws edu.dosw.sirha.exception.ValidationException si falla alguna validación
	 */
	SolicitudResponse create(SolicitudRequest request);

	/**
	 * Actualiza una solicitud existente (solo si está en PENDIENTE).
	 * 
	 * @param id ID de la solicitud a actualizar
	 * @param request Nuevos datos de la solicitud
	 * @return La solicitud actualizada
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 * @throws edu.dosw.sirha.exception.ValidationException si ya no es editable
	 */
	SolicitudResponse update(String id, SolicitudRequest request);

	/**
	 * Cambia el estado de una solicitud.
	 * 
	 * <p>Al cambiar a APROBADA, aplica los cambios a las inscripciones.
	 * Registra el cambio en el historial automáticamente.</p>
	 * 
	 * @param id ID de la solicitud
	 * @param nuevoEstado Nuevo estado
	 * @param observaciones Justificación del cambio
	 * @return La solicitud con estado actualizado
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 * @throws edu.dosw.sirha.exception.ValidationException si transición no válida
	 */
	SolicitudResponse changeEstado(String id, SolicitudEstado nuevoEstado, String observaciones);

	/**
	 * Busca una solicitud por ID.
	 * 
	 * @param id ID de la solicitud
	 * @return La solicitud encontrada
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 */
	SolicitudResponse findById(String id);

	/**
	 * Elimina una solicitud (solo si está en PENDIENTE).
	 * 
	 * @param id ID de la solicitud a eliminar
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 * @throws edu.dosw.sirha.exception.ValidationException si ya no es eliminable
	 */
	void delete(String id);

	/**
	 * Obtiene todas las solicitudes del sistema.
	 * 
	 * @return Lista de todas las solicitudes
	 */
	List<SolicitudResponse> findAll();

	/**
	 * Busca solicitudes de un estudiante específico.
	 * 
	 * @param estudianteId ID del estudiante
	 * @return Lista de solicitudes del estudiante
	 */
	List<SolicitudResponse> findByEstudiante(String estudianteId);

	/**
	 * Busca solicitudes por estados.
	 * 
	 * <p>Útil para coordinadores: ver solo PENDIENTES o EN_REVISION.</p>
	 * 
	 * @param estados Lista de estados a buscar
	 * @return Lista de solicitudes con alguno de esos estados
	 */
	List<SolicitudResponse> findByEstados(List<SolicitudEstado> estados);

	/**
	 * Cuenta solicitudes por estado.
	 * 
	 * <p>Para dashboards y estadísticas.</p>
	 * 
	 * @param estado Estado a contar
	 * @return Cantidad de solicitudes en ese estado
	 */
	long countByEstado(SolicitudEstado estado);

	/**
	 * Busca solicitudes de un periodo en un rango de fechas.
	 * 
	 * <p>Para reportes y análisis histórico.</p>
	 * 
	 * @param periodoId ID del periodo
	 * @param inicio Fecha inicial del rango
	 * @param fin Fecha final del rango
	 * @return Lista de solicitudes que cumplen criterios
	 */
	List<SolicitudResponse> findByPeriodoAndRango(String periodoId, Instant inicio, Instant fin);
}