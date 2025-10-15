package edu.dosw.sirha.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud para registrar manualmente un conflicto.
 * 
 * <p>Usado en {@code POST /api/conflicts} cuando se detecta un problema
 * que el sistema no identificó automáticamente.</p>
 * 
 * @see ConflictResponse
 * @see edu.dosw.sirha.model.Conflict
 * @see edu.dosw.sirha.controller.ConflictController
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConflictRequest {

    /**
     * Tipo de conflicto (SOLAPAMIENTO_HORARIOS, PREREQUISITO_FALTANTE, etc.).
     */
    @NotBlank
    private String tipo;

    /**
     * Descripción detallada del conflicto.
     */
    private String descripcion;

    /**
     * ID del estudiante afectado.
     */
    @NotBlank
    private String estudianteId;

    /**
     * ID de la solicitud relacionada (si aplica).
     */
    private String solicitudId;

    /**
     * ID del grupo relacionado (si aplica).
     */
    private String grupoId;

    /**
     * Observaciones adicionales.
     */
    private String observaciones;
}