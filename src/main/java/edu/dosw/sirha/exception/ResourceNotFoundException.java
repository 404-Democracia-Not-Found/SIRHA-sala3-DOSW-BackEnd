package edu.dosw.sirha.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para indicar que un recurso solicitado no fue encontrado en el sistema SIRHA.
 * 
 * <p>Esta excepción se lanza cuando se intenta acceder a una entidad que no existe en la base de datos,
 * como una solicitud, materia, periodo, usuario o grupo específico. Al estar anotada con
 * {@code @ResponseStatus(HttpStatus.NOT_FOUND)}, automáticamente genera una respuesta HTTP 404
 * cuando no es capturada por el {@link GlobalExceptionHandler}.</p>
 * 
 * <p><strong>Escenarios de uso comunes:</strong></p>
 * <ul>
 *   <li>Búsqueda de solicitud por ID que no existe</li>
 *   <li>Intento de acceder a un periodo académico inexistente</li>
 *   <li>Consulta de materia o grupo no registrado en el sistema</li>
 *   <li>Acceso a usuario con correo no encontrado</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>
 * Solicitud solicitud = solicitudRepository.findById(id)
 *     .orElseThrow(() -&gt; new ResourceNotFoundException(
 *         "Solicitud no encontrada con ID: " + id));
 * </pre>
 * 
 * @see GlobalExceptionHandler#handleNotFound(ResourceNotFoundException)
 * @see org.springframework.web.bind.annotation.ResponseStatus
 * 
 * @author Sistema SIRHA
 * @version 1.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Construye una nueva excepción de recurso no encontrado con el mensaje especificado.
     * 
     * <p>El mensaje debe ser descriptivo e indicar claramente qué recurso no fue encontrado,
     * incluyendo identificadores relevantes cuando sea posible para facilitar la depuración.</p>
     * 
     * @param message mensaje descriptivo que explica qué recurso no fue encontrado.
     *                Ejemplo: "Solicitud no encontrada con ID: 507f1f77bcf86cd799439011"
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
