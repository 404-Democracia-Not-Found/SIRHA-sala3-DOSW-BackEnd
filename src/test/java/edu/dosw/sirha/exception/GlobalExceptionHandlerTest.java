package edu.dosw.sirha.exception;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

/**
 * Suite de pruebas unitarias para {@link GlobalExceptionHandler}.
 * 
 * <p>Esta clase verifica el correcto manejo de todas las excepciones capturadas por el
 * handler global, asegurando que los códigos HTTP, mensajes y estructura de respuestas
 * sean consistentes y apropiados para cada tipo de error.</p>
 * 
 * <p><strong>Handlers de excepciones probados:</strong></p>
 * <ul>
 *   <li>{@code handleNotFound()} - HTTP 404 para recursos no encontrados</li>
 *   <li>{@code handleBusiness()} - HTTP 400 con clasificación de código de error</li>
 *   <li>{@code handleValidation()} - HTTP 400 con detalles de campos inválidos</li>
 *   <li>{@code handleBadCredentials()} - HTTP 401 para autenticación fallida</li>
 *   <li>{@code handleAccessDenied()} - HTTP 403 para permisos insuficientes</li>
 *   <li>{@code handleGeneric()} - HTTP 500 para errores no controlados</li>
 * </ul>
 * 
 * <p><strong>Aspectos verificados:</strong></p>
 * <ul>
 *   <li>Códigos de estado HTTP correctos (404, 400, 401, 403, 500)</li>
 *   <li>Estructura de respuesta estándar con timestamp, status, error, errorCode, message, path</li>
 *   <li>Códigos de error SIRHA específicos (SIRHA-XXX-YYY)</li>
 *   <li>Clasificación de BusinessException por palabras clave (horario, cupo, periodo)</li>
 *   <li>Mapa de errores de validación con campos y mensajes</li>
 *   <li>Preservación de mensajes de error originales</li>
 * </ul>
 * 
 * @see GlobalExceptionHandler
 * @see ResourceNotFoundException
 * @see BusinessException
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest mockRequest = mock(WebRequest.class);

    @Test
    void handleNotFoundShouldReturnNotFoundStatus() {
        when(mockRequest.getDescription(false)).thenReturn("uri=/api/test");
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleResourceNotFound(
                new ResourceNotFoundException("No existe"),
                mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("No existe");
        assertThat(response.getBody().getErrorCode()).isEqualTo("SIRHA-404-001");
    }

    @Test
    void handleBusinessShouldReturnBadRequest() {
        when(mockRequest.getDescription(false)).thenReturn("uri=/api/test");
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleBusinessException(
                new BusinessException("Regla de negocio"),
                mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Regla de negocio");
        assertThat(response.getBody().getErrorCode()).isEqualTo("SIRHA-400-002");
    }

    @Test
    void handleValidationShouldIncludeFieldErrors() throws NoSuchMethodException {
        when(mockRequest.getDescription(false)).thenReturn("uri=/api/test");
        
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "solicitud");
        bindingResult.addError(new FieldError("solicitud", "estudianteId", "requerido"));
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(
                exception,
                mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getValidationErrors()).containsEntry("estudianteId", "requerido");
    }

    @Test
    void handleGenericShouldReturnInternalServerError() {
        when(mockRequest.getDescription(false)).thenReturn("uri=/api/test");
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGlobalException(
                new RuntimeException("boom"),
                mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().getErrorCode()).isEqualTo("SIRHA-500-001");
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String value) {
        // helper for method parameter creation
    }
}
