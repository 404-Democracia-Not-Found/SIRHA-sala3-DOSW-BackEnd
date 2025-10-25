package edu.dosw.sirha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entrada de historial de una solicitud.
 * 
 * <p>Representa un evento o cambio de estado en el ciclo de vida de una {@link Solicitud}.
 * Se usa para trazabilidad y auditoría, permitiendo ver quién hizo qué y cuándo.</p>
 * 
 * <p>Ejemplos de acciones:</p>
 * <ul>
 *   <li>"CREADA": Solicitud enviada por estudiante</li>
 *   <li>"EN_REVISION": Coordinador comenzó revisión</li>
 *   <li>"APROBADA": Solicitud aprobada</li>
 *   <li>"RECHAZADA": Solicitud rechazada con comentario</li>
 *   <li>"INFORMACION_ADICIONAL": Se solicitó más información</li>
 * </ul>
 * 
 * @see Solicitud#historial
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudHistorialEntry {
    /**
     * Fecha y hora del evento.
     */
    private Instant fecha;
    
    /**
     * Acción o cambio realizado.
     * 
     * <p>Generalmente corresponde a cambios de {@link edu.dosw.sirha.model.enums.SolicitudEstado}.</p>
     */
    private String accion;
    
    /**
     * ID del usuario que realizó la acción.
     * 
     * <p>Referencia a {@link User} (estudiante, coordinador, admin).</p>
     */
    private String usuarioId;
    
    /**
     * Comentario o justificación de la acción.
     * 
     * <p>Ejemplo: "Aprobado por cumplir requisitos", "Rechazado: grupo lleno".</p>
     */
    private String comentario;
}
