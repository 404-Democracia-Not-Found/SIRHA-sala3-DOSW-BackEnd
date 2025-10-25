package edu.dosw.sirha.dto.response;

import edu.dosw.sirha.model.PeriodoConfiguracion;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * DTO de respuesta con información completa de un periodo académico.
 * 
 * @see PeriodoRequest
 * @see edu.dosw.sirha.model.Periodo
 */
@Value
@Builder
public class PeriodoResponse {
	/** ID único del periodo. */
	String id;
	
	/** Fecha de inicio. */
	Instant fechaInicio;
	
	/** Fecha de fin. */
	Instant fechaFin;
	
	/** Inicio de inscripciones. */
	Instant fechaInscripcionInicio;
	
	/** Límite para solicitudes. */
	Instant fechaLimiteSolicitudes;
	
	/** Año del periodo. */
	int ano;
	
	/** Semestre (1, 2, 3). */
	int semestre;
	
	/** Está activo. */
	boolean activo;
	
	/** Configuración del periodo. */
	PeriodoConfiguracion configuracion;
}