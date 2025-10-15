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

@ControllerAdvice
public class GlobalExceptionHandler {

    // Códigos de error específicos para SIRHA
    public static final String ERROR_RESOURCE_NOT_FOUND = "SIRHA-404-001";
    public static final String ERROR_BUSINESS_RULE = "SIRHA-400-001";
    public static final String ERROR_VALIDATION = "SIRHA-400-002";
    public static final String ERROR_HORARIO_CONFLICT = "SIRHA-400-003";
    public static final String ERROR_CUPO_LLENO = "SIRHA-400-004";
    public static final String ERROR_PERIODO_CERRADO = "SIRHA-400-005";
    public static final String ERROR_UNAUTHORIZED = "SIRHA-401-001";
    public static final String ERROR_FORBIDDEN = "SIRHA-403-001";
    public static final String ERROR_INTERNAL = "SIRHA-500-001";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ERROR_RESOURCE_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        String errorCode = determineBusinessErrorCode(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, errorCode, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = defaultBody(HttpStatus.BAD_REQUEST, ERROR_VALIDATION, "Errores de validación");
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        body.put("validationErrors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ERROR_UNAUTHORIZED, "Credenciales inválidas");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ERROR_FORBIDDEN, "Acceso denegado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNAL, 
                "Error interno del servidor");
    }

    private String determineBusinessErrorCode(String message) {
        if (message.toLowerCase().contains("horario") || message.toLowerCase().contains("conflicto")) {
            return ERROR_HORARIO_CONFLICT;
        }
        if (message.toLowerCase().contains("cupo") || message.toLowerCase().contains("lleno")) {
            return ERROR_CUPO_LLENO;
        }
        if (message.toLowerCase().contains("periodo") || message.toLowerCase().contains("fecha")) {
            return ERROR_PERIODO_CERRADO;
        }
        return ERROR_BUSINESS_RULE;
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String errorCode, String message) {
        return ResponseEntity.status(status).body(defaultBody(status, errorCode, message));
    }

    private Map<String, Object> defaultBody(HttpStatus status, String errorCode, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("errorCode", errorCode);
        body.put("message", message);
        body.put("path", getCurrentPath());
        return body;
    }

    private String getCurrentPath() {
        try {
            return org.springframework.web.context.request.RequestContextHolder
                    .currentRequestAttributes()
                    .getAttribute(org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST, 
                            org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST)
                    .toString();
        } catch (Exception e) {
            return "/api/unknown";
        }
    }
}
