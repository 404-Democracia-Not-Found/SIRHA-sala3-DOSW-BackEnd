package edu.dosw.sirha.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para indicar violaciones de reglas de negocio en el sistema SIRHA.
 * 
 * <p>Esta excepción se lanza cuando una operación no puede completarse debido a la violación de
 * alguna regla de negocio específica del dominio académico, como conflictos de horario, cupos llenos,
 * periodos cerrados, o incumplimiento de requisitos académicos. Al estar anotada con
 * {@code @ResponseStatus(HttpStatus.BAD_REQUEST)}, automáticamente genera una respuesta HTTP 400.</p>
 * 
 * <p><strong>Reglas de negocio que generan esta excepción:</strong></p>
 * <ul>
 *   <li><strong>Conflictos de horario:</strong> intento de inscribir materias con horarios solapados</li>
 *   <li><strong>Cupos agotados:</strong> inscripción en grupos que alcanzaron capacidad máxima</li>
 *   <li><strong>Periodos cerrados:</strong> operaciones fuera de ventanas de tiempo permitidas</li>
 *   <li><strong>Requisitos no cumplidos:</strong> inscripción sin prerrequisitos aprobados</li>
 *   <li><strong>Estado inválido:</strong> cambios de estado no permitidos en solicitudes</li>
 *   <li><strong>Límites académicos:</strong> exceso de créditos permitidos por periodo</li>
 * </ul>
 * 
 * <p>El {@link GlobalExceptionHandler} captura estas excepciones y determina un código de error
 * específico basado en el mensaje, permitiendo respuestas más granulares al cliente.</p>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>
 * if (grupo.getCupoActual() &gt;= grupo.getCupoMaximo()) {
 *     throw new BusinessException(
 *         "El grupo " + grupo.getCodigo() + " no tiene cupos disponibles");
 * }
 * </pre>
 * 
 * @see GlobalExceptionHandler#handleBusiness(BusinessException)
 * @see GlobalExceptionHandler#determineBusinessErrorCode(String)
 * 
 * @author Sistema SIRHA
 * @version 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {
    
    /**
     * Construye una nueva excepción de negocio con el mensaje especificado.
     * 
     * <p>El mensaje debe describir claramente la regla de negocio violada y el contexto
     * del error, incluyendo identificadores y valores relevantes. Este mensaje será analizado
     * por el handler para determinar el código de error específico (conflicto de horario,
     * cupo lleno, periodo cerrado, etc.).</p>
     * 
     * @param message mensaje descriptivo que explica la regla de negocio violada.
     *                Ejemplo: "Conflicto de horario detectado: la materia se solapa con otra inscripción"
     */
    public BusinessException(String message) {
        super(message);
    }
}
