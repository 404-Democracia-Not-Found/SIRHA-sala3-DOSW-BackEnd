package edu.dosw.sirha.model.enums;

/**
 * Enumeración que define los estados posibles de una solicitud en el sistema SIRHA.
 * 
 * <p>Cada solicitud atraviesa un ciclo de vida controlado por estos estados. Las transiciones
 * entre estados están reguladas por reglas de negocio y permisos de usuario.</p>
 * 
 * <h2>Flujo de Estados:</h2>
 * <pre>
 * PENDIENTE → EN_REVISION → APROBADA
 *                ↓
 *           INFORMACION_ADICIONAL → (vuelve a PENDIENTE tras actualización)
 *                ↓
 *           RECHAZADA
 * </pre>
 * 
 * <h2>Permisos por Estado:</h2>
 * <ul>
 *   <li><b>PENDIENTE:</b> Estudiante puede actualizar/cancelar, coordinación puede revisar</li>
 *   <li><b>EN_REVISION:</b> Solo coordinación puede modificar</li>
 *   <li><b>INFORMACION_ADICIONAL:</b> Estudiante puede actualizar con info solicitada</li>
 *   <li><b>APROBADA/RECHAZADA:</b> Estados finales, no se permiten modificaciones</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see edu.dosw.sirha.model.Solicitud
 */
public enum SolicitudEstado {
    /**
     * Estado inicial de una solicitud recién creada por el estudiante.
     * <p>Esperando revisión por parte de coordinación o decanatura.</p>
     * <p><b>Transiciones permitidas:</b> EN_REVISION, RECHAZADA</p>
     */
    PENDIENTE,
    
    /**
     * Solicitud siendo analizada por coordinación.
     * <p>Un coordinador o personal de decanatura está revisando la solicitud.</p>
     * <p><b>Transiciones permitidas:</b> APROBADA, RECHAZADA, INFORMACION_ADICIONAL</p>
     */
    EN_REVISION,
    
    /**
     * Solicitud aprobada, cambio aplicado al sistema académico.
     * <p>Estado final. La solicitud fue aprobada y el cambio se realizó automáticamente
     * en inscripciones/grupos del estudiante.</p>
     * <p><b>Transiciones permitidas:</b> Ninguna (estado final)</p>
     */
    APROBADA,
    
    /**
     * Solicitud rechazada con observaciones explicativas.
     * <p>Estado final. La solicitud fue rechazada por coordinación. El motivo del rechazo
     * debe estar documentado en el campo {@code observaciones} de la solicitud.</p>
     * <p><b>Transiciones permitidas:</b> Ninguna (estado final)</p>
     */
    RECHAZADA,
    
    /**
     * Se requiere información adicional del estudiante.
     * <p>Coordinación solicita más datos o aclaraciones antes de aprobar/rechazar.
     * El estudiante puede actualizar la solicitud con la información solicitada.</p>
     * <p><b>Transiciones permitidas:</b> PENDIENTE (tras actualización del estudiante)</p>
     */
    INFORMACION_ADICIONAL
}
