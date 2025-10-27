package edu.dosw.sirha.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ConflictException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Manejador global de excepciones para la aplicación SIRHA.
 * 
 * <p>Proporciona respuestas de error consistentes y bien estructuradas para todos
 * los endpoints REST del sistema.</p>
 * 
 * <h2>Códigos de Error SIRHA:</h2>
 * <ul>
 *   <li><b>SIRHA-400-001:</b> Email duplicado (409)</li>
 *   <li><b>SIRHA-400-002:</b> Datos de entrada inválidos (400)</li>
 *   <li><b>SIRHA-400-003:</b> Dominio de email inválido (422)</li>
 *   <li><b>SIRHA-401-001:</b> Credenciales inválidas (401)</li>
 *   <li><b>SIRHA-404-001:</b> Recurso no encontrado (404)</li>
 *   <li><b>SIRHA-500-001:</b> Error interno del servidor (500)</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 2.0
 * @since 2025-10-26
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de Bean Validation (@Valid).
     * Retorna HTTP 400 Bad Request con detalles de los campos inválidos.
     * 
     * @param ex Excepción de validación
     * @param request Detalles del request HTTP
     * @return ErrorResponse con validationErrors mapeados por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        log.warn("Error de validación en request a: {}", request.getDescription(false));
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .errorCode("SIRHA-400-002")
                .message("Datos de entrada inválidos")
                .path(extractPath(request))
                .validationErrors(validationErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja excepciones de validación de negocio (ValidationException).
     * Retorna HTTP 422 Unprocessable Entity.
     * 
     * @param ex Excepción de validación
     * @param request Detalles del request HTTP
     * @return ErrorResponse con mensaje descriptivo
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            WebRequest request) {
        
        log.warn("Error de validación de negocio: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error("Unprocessable Entity")
                .errorCode("SIRHA-400-003")
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Maneja excepciones de conflicto (ConflictException).
     * Retorna HTTP 409 Conflict.
     * 
     * @param ex Excepción de conflicto
     * @param request Detalles del request HTTP
     * @return ErrorResponse con mensaje descriptivo
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex,
            WebRequest request) {
        
        log.warn("Error de conflicto: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .errorCode("SIRHA-400-001")
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Maneja credenciales inválidas en el login.
     * Retorna HTTP 401 Unauthorized.
     * 
     * @param ex Excepción de credenciales inválidas
     * @param request Detalles del request HTTP
     * @return ErrorResponse con mensaje genérico de seguridad
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request) {
        
        log.warn("Intento de login fallido desde: {}", request.getDescription(false));
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .errorCode("SIRHA-401-001")
                .message("Email o contraseña incorrectos")
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Maneja recursos no encontrados (ResourceNotFoundException).
     * Retorna HTTP 404 Not Found.
     * 
     * @param ex Excepción de recurso no encontrado
     * @param request Detalles del request HTTP
     * @return ErrorResponse con mensaje descriptivo
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .errorCode("SIRHA-404-001")
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Maneja excepciones de negocio genéricas (BusinessException).
     * Retorna HTTP 400 Bad Request.
     * 
     * @param ex Excepción de negocio
     * @param request Detalles del request HTTP
     * @return ErrorResponse con mensaje descriptivo
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            WebRequest request) {
        
        log.warn("Error de negocio: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .errorCode("SIRHA-400-000")
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja excepciones no capturadas por otros handlers.
     * Retorna HTTP 500 Internal Server Error.
     * 
     * @param ex Excepción genérica
     * @param request Detalles del request HTTP
     * @return ErrorResponse con mensaje genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        log.error("Error no manejado: ", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .errorCode("SIRHA-500-001")
                .message("Error interno del servidor")
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Extrae el path del request HTTP.
     * 
     * @param request Request HTTP
     * @return Path del request
     */
    private String extractPath(WebRequest request) {
        String description = request.getDescription(false);
        return description.replace("uri=", "");
    }

    /**
     * Clase interna que representa la estructura de respuestas de error.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        
        /**
         * Timestamp del error en formato ISO-8601.
         */
        private Instant timestamp;
        
        /**
         * Código HTTP del error.
         */
        private int status;
        
        /**
         * Nombre del error HTTP (ej: "Bad Request", "Not Found").
         */
        private String error;
        
        /**
         * Código de error específico de SIRHA para tracking.
         */
        private String errorCode;
        
        /**
         * Mensaje descriptivo del error.
         */
        private String message;
        
        /**
         * Path del endpoint que generó el error.
         */
        private String path;
        
        /**
         * Mapa de errores de validación por campo (solo para errores 400).
         */
        private Map<String, String> validationErrors;
    }
}






