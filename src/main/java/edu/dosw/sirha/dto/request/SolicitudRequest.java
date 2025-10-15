package edu.dosw.sirha.dto.request;

import edu.dosw.sirha.model.enums.SolicitudTipo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud para crear una nueva solicitud de cambio.
 * 
 * <p>Usado en {@code POST /api/solicitudes} por estudiantes para solicitar
 * cambios de grupo, materia, ajustes de horario o retiros.</p>
 * 
 * <p>Campos requeridos varían según el tipo:</p>
 * <ul>
 *   <li>CAMBIO_GRUPO: requiere inscripcionOrigenId, grupoDestinoId</li>
 *   <li>CAMBIO_MATERIA: requiere inscripcionOrigenId, materiaDestinoId</li>
 *   <li>RETIRO_ASIGNATURA: requiere inscripcionOrigenId</li>
 * </ul>
 * 
 * @see edu.dosw.sirha.controller.SolicitudController
 * @see SolicitudResponse
 * @see edu.dosw.sirha.model.Solicitud
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequest {

    /**
     * Tipo de solicitud (CAMBIO_GRUPO, CAMBIO_MATERIA, AJUSTE_HORARIO, RETIRO_ASIGNATURA).
     */
    @NotNull
    private SolicitudTipo tipo;

    /**
     * ID del estudiante que hace la solicitud.
     */
    @NotBlank
    private String estudianteId;

    /**
     * Descripción detallada de la solicitud.
     * 
     * <p>Ejemplo: "Necesito cambiar porque el horario solapa con otra materia obligatoria".</p>
     */
    private String descripcion;

    /**
     * Observaciones adicionales (opcional).
     */
    private String observaciones;

    /**
     * ID de la inscripción origen (grupo actual) si aplica.
     */
    private String inscripcionOrigenId;

    /**
     * ID del grupo destino (a donde se quiere cambiar) si aplica.
     */
    private String grupoDestinoId;

    /**
     * ID de la materia destino si es cambio de materia.
     */
    private String materiaDestinoId;

    /**
     * ID del periodo académico en que se hace la solicitud.
     */
    private String periodoId;

    /**
     * Prioridad de la solicitud (0 = normal, mayor = más urgente).
     */
    @Min(0)
    private int prioridad;
}