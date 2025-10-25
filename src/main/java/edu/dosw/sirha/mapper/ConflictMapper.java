package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;
import edu.dosw.sirha.model.Conflict;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre {@link Conflict}, {@link ConflictRequest} y {@link ConflictResponse}.
 * 
 * @see Conflict
 * @see ConflictRequest
 * @see ConflictResponse
 */
@Component
public class ConflictMapper {

	/**
	 * Convierte un ConflictRequest a una nueva entidad Conflict.
	 * 
	 * <p>Establece resuelto como false por defecto.</p>
	 * 
	 * @param request DTO con datos del conflicto
	 * @return Nueva entidad Conflict (sin ID ni fecha detección)
	 */
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

	/**
	 * Convierte una entidad Conflict a ConflictResponse.
	 * 
	 * @param conflict Entidad de dominio
	 * @return DTO para respuesta HTTP
	 */
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