package edu.dosw.sirha.model.enums;

/**
 * Enumeración que define los tipos de solicitudes de cambio académico en el sistema SIRHA.
 * 
 * <p>Cada tipo de solicitud tiene validaciones específicas, campos requeridos diferentes
 * y flujos de aprobación particulares. El tipo determina qué información debe proporcionar
 * el estudiante y qué validaciones automáticas se aplican.</p>
 * 
 * <h2>Campos Requeridos por Tipo:</h2>
 * <ul>
 *   <li><b>CAMBIO_GRUPO:</b> inscripcionOrigenId, grupoDestinoId</li>
 *   <li><b>CAMBIO_MATERIA:</b> materiaDestinoId, grupoDestinoId</li>
 *   <li><b>AJUSTE_HORARIO:</b> descripción detallada del ajuste</li>
 *   <li><b>RETIRO_ASIGNATURA:</b> inscripcionOrigenId, justificación</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see edu.dosw.sirha.model.Solicitud
 */
public enum SolicitudTipo {
    /**
     * Solicitud para cambiar de un grupo a otro dentro de la misma materia.
     * <p>El estudiante desea cambiar de horario pero permanecer en la misma asignatura.</p>
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>Estudiante debe estar inscrito en el grupo actual</li>
     *   <li>Grupo destino debe tener cupos disponibles</li>
     *   <li>No debe haber conflictos de horario con otras materias</li>
     *   <li>Ambos grupos deben pertenecer a la misma materia</li>
     * </ul>
     * <p><b>Caso de uso típico:</b> Conflicto de horario con trabajo o transporte</p>
     */
    CAMBIO_GRUPO,
    
    /**
     * Solicitud para cambiar una materia inscrita por otra diferente.
     * <p>El estudiante desea retirar una materia y matricular otra en su lugar.</p>
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>Debe cumplir prerrequisitos de la nueva materia</li>
     *   <li>No debe estar ya inscrito en la materia destino</li>
     *   <li>La nueva materia debe tener grupos con cupos disponibles</li>
     *   <li>No debe exceder el número máximo de créditos permitidos</li>
     *   <li>No debe haber conflictos de horario</li>
     * </ul>
     * <p><b>Caso de uso típico:</b> Cambio de electiva o ajuste de carga académica</p>
     */
    CAMBIO_MATERIA,
    
    /**
     * Solicitud para realizar ajustes menores de horario.
     * <p>Cambios que no implican cambio de grupo ni materia, solo ajustes administrativos.</p>
     * <p><b>Ejemplos:</b></p>
     * <ul>
     *   <li>Cambio de modalidad (presencial ↔ virtual)</li>
     *   <li>Ajuste de salón asignado</li>
     *   <li>Modificación de horario de laboratorio</li>
     * </ul>
     * <p><b>Caso de uso típico:</b> Ajustes administrativos menores</p>
     */
    AJUSTE_HORARIO,
    
    /**
     * Solicitud formal de retiro de una asignatura.
     * <p>El estudiante desea retirarse oficialmente de una materia inscrita.</p>
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>Debe estar dentro del período de retiro permitido</li>
     *   <li>Debe mantener el número mínimo de créditos inscritos</li>
     *   <li>Puede afectar el semáforo académico del estudiante</li>
     * </ul>
     * <p><b>Consecuencias:</b> Puede generar marca de "W" (Withdrawn) en el historial académico</p>
     * <p><b>Caso de uso típico:</b> Sobrecarga académica, problemas personales, enfermedad</p>
     */
    RETIRO_ASIGNATURA
}
