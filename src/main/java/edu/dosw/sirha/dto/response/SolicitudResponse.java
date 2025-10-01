package edu.dosw.sirha.dto.response;

import edu.dosw.sirha.model.SolicitudHistorialEntry;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.model.enums.SolicitudTipo;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class SolicitudResponse {
	String id;
	String codigoSolicitud;
	SolicitudEstado estado;
	SolicitudTipo tipo;
	String descripcion;
	String observaciones;
	String estudianteId;
	String inscripcionOrigenId;
	String grupoDestinoId;
	String materiaDestinoId;
	String periodoId;
	int prioridad;
	Instant fechaSolicitud;
	Instant fechaLimiteRespuesta;
	Instant fechaActualizacion;
	List<SolicitudHistorialEntry> historial;
}