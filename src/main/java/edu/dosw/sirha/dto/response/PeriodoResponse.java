package edu.dosw.sirha.dto.response;

import edu.dosw.sirha.model.PeriodoConfiguracion;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class PeriodoResponse {
	String id;
	Instant fechaInicio;
	Instant fechaFin;
	Instant fechaInscripcionInicio;
	Instant fechaLimiteSolicitudes;
	int ano;
	int semestre;
	boolean activo;
	PeriodoConfiguracion configuracion;
}