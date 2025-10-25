package edu.dosw.sirha.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Conflicto detectado en el sistema.
 * 
 * <p>Representa problemas o inconsistencias detectadas automáticamente que requieren
 * atención, como solapamiento de horarios, violación de prerrequisitos, exceso de créditos, etc.</p>
 * 
 * <p>Los conflictos se generan al procesar solicitudes y validar inscripciones,
 * y deben ser resueltos antes de aprobar cambios.</p>
 * 
 * <p>Tipos comunes de conflictos:</p>
 * <ul>
 *   <li>SOLAPAMIENTO_HORARIOS: Dos materias al mismo tiempo</li>
 *   <li>PREREQUISITO_FALTANTE: Requisito no cumplido</li>
 *   <li>EXCESO_CREDITOS: Sobrepasa límite de créditos permitidos</li>
 *   <li>GRUPO_LLENO: No hay cupos disponibles</li>
 * </ul>
 * 
 * @see Solicitud
 * @see Grupo
 * @see ConflictDetectionService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conflictos")
public class Conflict {

    /**
     * ID único del conflicto.
     */
    @Id
    private String id;

    /**
     * Tipo de conflicto detectado.
     * 
     * <p>Ejemplos: "SOLAPAMIENTO_HORARIOS", "PREREQUISITO_FALTANTE", "EXCESO_CREDITOS".</p>
     */
    @NotBlank
    private String tipo;

    /**
     * Descripción detallada del conflicto.
     * 
     * <p>Explica qué causó el conflicto y qué debe corregirse.</p>
     * <p>Ejemplo: "El grupo SIST-101-A tiene horario lunes 8-10 que solapa con MATE-101-B lunes 9-11".</p>
     */
    private String descripcion;

    /**
     * ID del estudiante afectado por el conflicto.
     */
    @NotBlank
    private String estudianteId;

    /**
     * ID de la solicitud que generó el conflicto (si aplica).
     * 
     * <p>null si el conflicto fue detectado en validación de inscripciones existentes.</p>
     */
    private String solicitudId;

    /**
     * ID del grupo relacionado con el conflicto (si aplica).
     */
    private String grupoId;

    /**
     * Fecha y hora en que se detectó el conflicto.
     */
    private Instant fechaDeteccion;

    /**
     * Indica si el conflicto ya fue resuelto.
     * 
     * <p>true si el estudiante/coordinador tomó acción correctiva,
     * false si aún está pendiente de resolución.</p>
     */
    private boolean resuelto;

    /**
     * Observaciones adicionales sobre la resolución o seguimiento del conflicto.
     * 
     * <p>Ejemplo: "Estudiante cambió de grupo", "Prerrequisito aprobado en periodo anterior".</p>
     */
    private String observaciones;
}