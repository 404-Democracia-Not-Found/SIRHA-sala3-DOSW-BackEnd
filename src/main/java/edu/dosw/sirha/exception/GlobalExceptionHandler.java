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

/**
 * Manejador global de excepciones para el sistema SIRHA.
 * 
 * <p>Esta clase centraliza el manejo de todas las excepciones lanzadas en la aplicación,
 * proporcionando respuestas HTTP consistentes y estandarizadas con códigos de error específicos
 * del dominio SIRHA. Utiliza la anotación {@code @ControllerAdvice} para interceptar excepciones
 * en todos los controladores REST y transformarlas en respuestas JSON estructuradas.</p>
 * 
 * <p><strong>Tipos de excepciones manejadas:</strong></p>
 * <ul>
 *   <li><strong>ResourceNotFoundException:</strong> recursos no encontrados (HTTP 404)</li>
 *   <li><strong>BusinessException:</strong> violaciones de reglas de negocio (HTTP 400)</li>
 *   <li><strong>MethodArgumentNotValidException:</strong> errores de validación Jakarta (HTTP 400)</li>
 *   <li><strong>BadCredentialsException:</strong> credenciales inválidas (HTTP 401)</li>
 *   <li><strong>AccessDeniedException:</strong> acceso denegado por permisos (HTTP 403)</li>
 *   <li><strong>Exception:</strong> errores internos no controlados (HTTP 500)</li>
 * </ul>
 * 
 * <p><strong>Estructura de respuesta estándar:</strong></p>
 * <pre>
 * {
 *   "timestamp": "2025-01-15T10:30:00Z",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "errorCode": "SIRHA-400-003",
 *   "message": "Conflicto de horario detectado",
 *   "path": "/api/solicitudes",
 *   "validationErrors": {...}  // solo para errores de validación
 * }
 * </pre>
 * 
 * <p><strong>Códigos de error SIRHA:</strong></p>
 * <ul>
 *   <li>SIRHA-404-001: Recurso no encontrado</li>
 *   <li>SIRHA-400-001: Regla de negocio genérica violada</li>
 *   <li>SIRHA-400-002: Errores de validación de datos</li>
 *   <li>SIRHA-400-003: Conflicto de horarios</li>
 *   <li>SIRHA-400-004: Cupo de grupo lleno</li>
 *   <li>SIRHA-400-005: Periodo académico cerrado</li>
 *   <li>SIRHA-401-001: Autenticación fallida</li>
 *   <li>SIRHA-403-001: Permisos insuficientes</li>
 *   <li>SIRHA-500-001: Error interno del servidor</li>
 * </ul>
 * 
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 * 
 * @author Sistema SIRHA
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Códigos de error específicos para SIRHA
    
    /** Código de error para recursos no encontrados en la base de datos. */
    public static final String ERROR_RESOURCE_NOT_FOUND = "SIRHA-404-001";
    
    /** Código de error genérico para violaciones de reglas de negocio. */
    public static final String ERROR_BUSINESS_RULE = "SIRHA-400-001";
    
    /** Código de error para validaciones de datos de entrada fallidas. */
    public static final String ERROR_VALIDATION = "SIRHA-400-002";
    
    /** Código de error específico para conflictos de horario entre materias. */
    public static final String ERROR_HORARIO_CONFLICT = "SIRHA-400-003";
    
    /** Código de error para intentos de inscripción en grupos sin cupos. */
    public static final String ERROR_CUPO_LLENO = "SIRHA-400-004";
    
    /** Código de error para operaciones fuera de ventanas de tiempo permitidas. */
    public static final String ERROR_PERIODO_CERRADO = "SIRHA-400-005";
    
    /** Código de error para credenciales de autenticación inválidas. */
    public static final String ERROR_UNAUTHORIZED = "SIRHA-401-001";
    
    /** Código de error para acceso denegado por falta de permisos. */
    public static final String ERROR_FORBIDDEN = "SIRHA-403-001";
    
    /** Código de error para excepciones no controladas del servidor. */
    public static final String ERROR_INTERNAL = "SIRHA-500-001";

    /**
     * Maneja excepciones de recursos no encontrados.
     * 
     * <p>Este método captura todas las instancias de {@link ResourceNotFoundException} lanzadas
     * en la aplicación y las transforma en respuestas HTTP 404 estandarizadas con el código de
     * error SIRHA-404-001.</p>
     * 
     * @param ex la excepción de recurso no encontrado capturada
     * @return respuesta HTTP 404 con detalles del error en formato JSON
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ERROR_RESOURCE_NOT_FOUND, ex.getMessage());
    }

    /**
     * Maneja excepciones de reglas de negocio.
     * 
     * <p>Este método captura {@link BusinessException} y analiza el mensaje de error para
     * determinar un código de error más específico (conflicto de horario, cupo lleno, periodo
     * cerrado) basado en palabras clave. Retorna una respuesta HTTP 400 con el código apropiado.</p>
     * 
     * @param ex la excepción de negocio capturada
     * @return respuesta HTTP 400 con código de error específico y detalles
     * @see #determineBusinessErrorCode(String)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        String errorCode = determineBusinessErrorCode(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, errorCode, ex.getMessage());
    }

    /**
     * Maneja errores de validación de Jakarta Bean Validation.
     * 
     * <p>Este método captura excepciones lanzadas cuando los datos de entrada no cumplen con
     * las anotaciones de validación ({@code @NotBlank}, {@code @Email}, {@code @Min}, etc.).
     * Extrae todos los errores de campo y los incluye en un mapa {@code validationErrors} dentro
     * de la respuesta JSON para facilitar la corrección por parte del cliente.</p>
     * 
     * <p><strong>Ejemplo de respuesta:</strong></p>
     * <pre>
     * {
     *   "errorCode": "SIRHA-400-002",
     *   "message": "Errores de validación",
     *   "validationErrors": {
     *     "email": "debe ser una dirección de correo válida",
     *     "nombre": "no debe estar vacío"
     *   }
     * }
     * </pre>
     * 
     * @param ex la excepción de validación capturada con los errores de campo
     * @return respuesta HTTP 400 con mapa detallado de errores de validación
     */
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

    /**
     * Maneja errores de autenticación por credenciales incorrectas.
     * 
     * <p>Este método captura excepciones lanzadas por Spring Security cuando las credenciales
     * de login (usuario/contraseña) no son válidas. Retorna HTTP 401 Unauthorized con el código
     * de error SIRHA-401-001.</p>
     * 
     * @param ex la excepción de credenciales inválidas
     * @return respuesta HTTP 401 indicando fallo de autenticación
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ERROR_UNAUTHORIZED, "Credenciales inválidas");
    }

    /**
     * Maneja errores de acceso denegado por falta de permisos.
     * 
     * <p>Este método captura excepciones lanzadas cuando un usuario autenticado intenta acceder
     * a un recurso para el cual no tiene los permisos necesarios (por ejemplo, un estudiante
     * intentando acceder a funciones de administrador). Retorna HTTP 403 Forbidden con el código
     * de error SIRHA-403-001.</p>
     * 
     * @param ex la excepción de acceso denegado
     * @return respuesta HTTP 403 indicando permisos insuficientes
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ERROR_FORBIDDEN, "Acceso denegado");
    }

    /**
     * Maneja todas las excepciones no controladas (catch-all).
     * 
     * <p>Este método actúa como red de seguridad para capturar cualquier excepción no manejada
     * específicamente por otros handlers. Previene que detalles internos del servidor sean
     * expuestos al cliente, retornando un mensaje genérico con HTTP 500 y código SIRHA-500-001.
     * Los detalles completos de la excepción se registran en los logs del servidor.</p>
     * 
     * @param ex la excepción no controlada capturada
     * @return respuesta HTTP 500 con mensaje genérico de error interno
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNAL, 
                "Error interno del servidor");
    }

    /**
     * Determina el código de error específico basado en el mensaje de la excepción de negocio.
     * 
     * <p>Este método analiza el mensaje de error buscando palabras clave para clasificar el tipo
     * de violación de regla de negocio y asignar un código de error más granular. Esto permite
     * que los clientes identifiquen el tipo específico de problema y presenten mensajes más
     * apropiados al usuario.</p>
     * 
     * <p><strong>Palabras clave y códigos resultantes:</strong></p>
     * <ul>
     *   <li>"horario" o "conflicto" → SIRHA-400-003 (conflicto de horario)</li>
     *   <li>"cupo" o "lleno" → SIRHA-400-004 (cupo lleno)</li>
     *   <li>"periodo" o "fecha" → SIRHA-400-005 (periodo cerrado)</li>
     *   <li>Otros casos → SIRHA-400-001 (regla de negocio genérica)</li>
     * </ul>
     * 
     * @param message mensaje de la excepción de negocio a analizar
     * @return código de error específico correspondiente al tipo de violación
     */
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

    /**
     * Construye una respuesta HTTP con el cuerpo de error estandarizado.
     * 
     * <p>Método auxiliar que combina el estado HTTP, código de error y mensaje en una
     * respuesta {@link ResponseEntity} con el formato JSON estándar de SIRHA.</p>
     * 
     * @param status el estado HTTP a retornar (404, 400, 401, 403, 500, etc.)
     * @param errorCode el código de error específico SIRHA
     * @param message el mensaje descriptivo del error
     * @return entidad de respuesta con cuerpo de error completo
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String errorCode, String message) {
        return ResponseEntity.status(status).body(defaultBody(status, errorCode, message));
    }

    /**
     * Crea el mapa de datos estándar para el cuerpo de respuesta de error.
     * 
     * <p>Este método genera la estructura JSON base que incluye timestamp, estado HTTP,
     * código de error, mensaje y ruta de la petición. Este formato consistente facilita
     * el manejo de errores por parte de los clientes de la API.</p>
     * 
     * @param status el estado HTTP del error
     * @param errorCode el código de error SIRHA específico
     * @param message el mensaje descriptivo del error
     * @return mapa con los campos estándar de la respuesta de error
     */
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

    /**
     * Obtiene la ruta de la petición HTTP actual.
     * 
     * <p>Intenta recuperar la ruta de la petición desde el contexto de Spring. Si no está
     * disponible (por ejemplo, en contextos de prueba o peticiones asíncronas), retorna
     * un valor por defecto "/api/unknown" para evitar excepciones.</p>
     * 
     * @return la ruta de la petición actual o "/api/unknown" si no está disponible
     */
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
