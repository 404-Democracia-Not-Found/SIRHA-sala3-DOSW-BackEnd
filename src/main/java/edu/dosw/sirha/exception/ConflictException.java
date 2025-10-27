package edu.dosw.sirha.exception;

/**
 * Excepción lanzada cuando ocurre un conflicto con recursos existentes.
 * 
 * <p>Casos de uso típicos:</p>
 * <ul>
 *   <li>Email ya registrado en el sistema</li>
 *   <li>Código de estudiante duplicado</li>
 *   <li>Intentos de crear recursos que ya existen</li>
 * </ul>
 * 
 * <p>Esta excepción debe retornar un HTTP 409 Conflict.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2025-10-26
 */
public class ConflictException extends RuntimeException {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param message Descripción del conflicto
     */
    public ConflictException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Descripción del conflicto
     * @param cause Causa original del error
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}