package edu.dosw.sirha.dto.response;

import edu.dosw.sirha.model.SolicitudHistorialEntry;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.model.enums.SolicitudTipo;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * DTO de respuesta con información completa de una solicitud.
 * 
 * <p>Retorna todos los datos de la solicitud incluyendo su historial completo
 * de cambios de estado para trazabilidad.</p>
 * 
 * @see SolicitudRequest
 * @see edu.dosw.sirha.model.Solicitud
 * @see edu.dosw.sirha.controller.SolicitudController
 */
@Value
@Builder
public class SolicitudResponse {
	/** ID único de la solicitud. */
	String id;
	
	/** Código de solicitud legible (ej: "SOL-2024-001"). */
	String codigoSolicitud;
	
	/** Estado actual (PENDIENTE, EN_REVISION, APROBADA, etc.). */
	SolicitudEstado estado;
	
	/** Tipo de solicitud (CAMBIO_GRUPO, CAMBIO_MATERIA, etc.). */
	SolicitudTipo tipo;
	
	/** Descripción detallada de la solicitud. */
	String descripcion;
	
	/** Observaciones adicionales. */
	String observaciones;
	
	/** ID del estudiante solicitante. */
	String estudianteId;
	
	/** ID de inscripción origen (si aplica). */
	String inscripcionOrigenId;
	
	/** ID del grupo destino (si aplica). */
	String grupoDestinoId;
	
	/** ID de materia destino (si aplica). */
	String materiaDestinoId;
	
	/** ID del periodo académico. */
	String periodoId;
	
	/** Prioridad de la solicitud (0 = normal). */
	int prioridad;
	
	/** Fecha y hora de creación de la solicitud. */
	Instant fechaSolicitud;
	
	/** Fecha límite para responder. */
	Instant fechaLimiteRespuesta;
	
	/** Última actualización. */
	Instant fechaActualizacion;
	
	/** Historial completo de cambios de estado. */
	List<SolicitudHistorialEntry> historial;
}