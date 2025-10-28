package edu.dosw.sirha.exception;

/**
 * Excepción lanzada cuando una validación de negocio falla.
 * 
 * <p>Se usa para errores de validación que no son capturados por las anotaciones
 * de Bean Validation, como validaciones de dominio de email, reglas de negocio
 * específicas, etc.</p>
 * 
 * <p>Esta excepción debe retornar un HTTP 422 Unprocessable Entity.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2025-10-26
 */
public class ValidationException extends RuntimeException {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param message Descripción del error de validación
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Descripción del error
     * @param cause Causa original del error
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}