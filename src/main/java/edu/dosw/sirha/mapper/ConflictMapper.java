package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;
import edu.dosw.sirha.model.Conflict;
import org.springframework.stereotype.Component;

@Component
public class ConflictMapper {

	public Conflict toNewEntity(ConflictRequest request) {
		if (request == null) {
			return null;
		}

		return Conflict.builder()
				.tipo(request.getTipo())
				.descripcion(request.getDescripcion())
				.estudianteId(request.getEstudianteId())
				.solicitudId(request.getSolicitudId())
				.grupoId(request.getGrupoId())
				.observaciones(request.getObservaciones())
				.resuelto(false)
				.build();
	}

	public ConflictResponse toResponse(Conflict conflict) {
		if (conflict == null) {
			return null;
		}

		return ConflictResponse.builder()
				.id(conflict.getId())
				.tipo(conflict.getTipo())
				.descripcion(conflict.getDescripcion())
				.estudianteId(conflict.getEstudianteId())
				.solicitudId(conflict.getSolicitudId())
				.grupoId(conflict.getGrupoId())
				.fechaDeteccion(conflict.getFechaDeteccion())
				.resuelto(conflict.isResuelto())
				.observaciones(conflict.getObservaciones())
				.build();
	}
}