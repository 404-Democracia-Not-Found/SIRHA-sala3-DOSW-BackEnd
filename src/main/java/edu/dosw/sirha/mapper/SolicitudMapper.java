package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SolicitudMapper {

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