package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Mapper para convertir entre {@link Solicitud}, {@link SolicitudRequest} y {@link SolicitudResponse}.
 * 
 * <p>Proporciona conversión bidireccional desacoplando la capa de presentación
 * (DTOs) de la capa de dominio (entidades).</p>
 * 
 * @see Solicitud
 * @see SolicitudRequest
 * @see SolicitudResponse
 */
@Component
public class SolicitudMapper {

	/**
	 * Convierte un SolicitudRequest a una nueva entidad Solicitud.
	 * 
	 * <p>Establece estado inicial como PENDIENTE.</p>
	 * 
	 * @param request DTO con datos de la solicitud
	 * @return Nueva entidad Solicitud (sin ID)
	 */
	public Solicitud toNewEntity(SolicitudRequest request) {
		if (request == null) {
			return null;
		}

		return Solicitud.builder()
				.tipo(request.getTipo())
				.estado(SolicitudEstado.PENDIENTE)
				.descripcion(request.getDescripcion())
				.observaciones(request.getObservaciones())
				.estudianteId(request.getEstudianteId())
				.inscripcionOrigenId(request.getInscripcionOrigenId())
				.grupoDestinoId(request.getGrupoDestinoId())
				.materiaDestinoId(request.getMateriaDestinoId())
				.periodoId(request.getPeriodoId())
				.prioridad(request.getPrioridad())
				.build();
	}

	/**
	 * Actualiza una entidad Solicitud existente con datos de un request.
	 * 
	 * <p>No modifica ID ni estado (se gestionan por separado).</p>
	 * 
	 * @param entity Entidad existente a actualizar
	 * @param request DTO con nuevos datos
	 */
	public void updateEntity(Solicitud entity, SolicitudRequest request) {
		if (entity == null || request == null) {
			return;
		}

		entity.setTipo(request.getTipo());
		entity.setDescripcion(request.getDescripcion());
		entity.setObservaciones(request.getObservaciones());
		entity.setEstudianteId(request.getEstudianteId());
		entity.setInscripcionOrigenId(request.getInscripcionOrigenId());
		entity.setGrupoDestinoId(request.getGrupoDestinoId());
		entity.setMateriaDestinoId(request.getMateriaDestinoId());
		entity.setPeriodoId(request.getPeriodoId());
		entity.setPrioridad(request.getPrioridad());
	}

	/**
	 * Convierte una entidad Solicitud a SolicitudResponse.
	 * 
	 * @param solicitud Entidad de dominio
	 * @return DTO para respuesta HTTP
	 */
	public SolicitudResponse toResponse(Solicitud solicitud) {
		if (solicitud == null) {
			return null;
		}

		return SolicitudResponse.builder()
				.id(solicitud.getId())
				.codigoSolicitud(solicitud.getCodigoSolicitud())
				.estado(solicitud.getEstado())
				.tipo(solicitud.getTipo())
				.descripcion(solicitud.getDescripcion())
				.observaciones(solicitud.getObservaciones())
				.estudianteId(solicitud.getEstudianteId())
				.inscripcionOrigenId(solicitud.getInscripcionOrigenId())
				.grupoDestinoId(solicitud.getGrupoDestinoId())
				.materiaDestinoId(solicitud.getMateriaDestinoId())
				.periodoId(solicitud.getPeriodoId())
				.prioridad(solicitud.getPrioridad())
				.fechaSolicitud(solicitud.getFechaSolicitud())
				.fechaLimiteRespuesta(solicitud.getFechaLimiteRespuesta())
				.fechaActualizacion(solicitud.getFechaActualizacion())
				.historial(solicitud.getHistorial() == null ? null : new ArrayList<>(solicitud.getHistorial()))
				.build();
	}
}