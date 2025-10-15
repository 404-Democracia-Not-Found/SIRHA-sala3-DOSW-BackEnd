package edu.dosw.sirha.model.enums;

/**
 * Enumeración que define los estados posibles de una inscripción en el sistema SIRHA.
 * 
 * <p>Una inscripción representa la matrícula de un estudiante en un grupo específico
 * de una materia. El estado evoluciona desde la inscripción inicial hasta el resultado
 * final al terminar el semestre.</p>
 * 
 * <h2>Ciclo de Vida de Inscripción:</h2>
 * <pre>
 * INSCRITO (durante el semestre)
 *    ↓
 *    ├──> APROBADO (calificación ≥ 3.0)
 *    ├──> REPROBADO (calificación < 3.0)
 *    └──> CANCELADO (retiro oficial)
 * </pre>
 * 
 * <h2>Impacto en Semáforo Académico:</h2>
 * <ul>
 *   <li><b>INSCRITO:</b> Cuenta como materia en progreso</li>
 *   <li><b>APROBADO:</b> Suma a materias aprobadas</li>
 *   <li><b>REPROBADO:</b> Suma a materias perdidas</li>
 *   <li><b>CANCELADO:</b> No afecta el semáforo (retiro oficial)</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see edu.dosw.sirha.model.Inscripcion
 * @see edu.dosw.sirha.model.SemaforoAcademico
 */
public enum EstadoInscripcion {
    /**
     * Estudiante está actualmente inscrito en el grupo.
     * <p>Estado activo durante el transcurso del semestre. El estudiante está asistiendo
     * a clases y puede presentar evaluaciones.</p>
     * <p><b>Transiciones permitidas:</b> APROBADO, REPROBADO, CANCELADO</p>
     */
    INSCRITO,
    
    /**
     * Inscripción fue cancelada mediante retiro oficial.
     * <p>El estudiante se retiró formalmente de la materia antes de la fecha límite.
     * No afecta negativamente el promedio académico, pero puede generar marca "W".</p>
     * <p><b>Precondición:</b> Solicitud de retiro aprobada por coordinación</p>
     */
    CANCELADO,
    
    /**
     * Estudiante aprobó la materia satisfactoriamente.
     * <p>Estado final. La calificación final cumple con el mínimo aprobatorio (≥ 3.0).
     * La materia queda registrada en el historial académico como aprobada.</p>
     * <p><b>Impacto:</b> Suma a materias aprobadas en el semáforo académico</p>
     */
    APROBADO,
    
    /**
     * Estudiante reprobó la materia.
     * <p>Estado final. La calificación final está por debajo del mínimo aprobatorio (< 3.0).
     * El estudiante deberá cursar nuevamente la materia.</p>
     * <p><b>Impacto:</b> Suma a materias perdidas en el semáforo académico</p>
     * <p><b>Consecuencia:</b> Puede requerir aprobar en próximo intento para continuar en el programa</p>
     */
    REPROBADO
}
