package edu.dosw.sirha.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * DTO de respuesta con información de un conflicto detectado.
 * 
 * @see ConflictRequest
 * @see edu.dosw.sirha.model.Conflict
 */
@Value
@Builder
public class ConflictResponse {
    /** ID único del conflicto. */
    String id;
    
    /** Tipo de conflicto. */
    String tipo;
    
    /** Descripción detallada. */
    String descripcion;
    
    /** ID del estudiante afectado. */
    String estudianteId;
    
    /** ID de solicitud relacionada. */
    String solicitudId;
    
    /** ID de grupo relacionado. */
    String grupoId;
    
    /** Fecha de detección. */
    Instant fechaDeteccion;
    
    /** Está resuelto. */
    boolean resuelto;
    
    /** Observaciones. */
    String observaciones;
}