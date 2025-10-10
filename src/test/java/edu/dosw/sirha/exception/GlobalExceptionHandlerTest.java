package edu.dosw.sirha.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundShouldReturnNotFoundStatus() {
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(
                new ResourceNotFoundException("No existe"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "No existe");
    }

    @Test
    void handleBusinessShouldReturnBadRequest() {
        ResponseEntity<Map<String, Object>> response = handler.handleBusiness(
                new BusinessException("Regla de negocio"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Regla de negocio");
    }

    @Test
    void handleValidationShouldIncludeFieldErrors() throws NoSuchMethodException {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "solicitud");
        bindingResult.addError(new FieldError("solicitud", "estudianteId", "requerido"));
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("validationErrors");
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("validationErrors");
        assertThat(errors).containsEntry("estudianteId", "requerido");
    }

    @Test
    void handleGenericShouldReturnInternalServerError() {
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("message", "Error interno del servidor");
        assertThat(response.getBody()).containsEntry("errorCode", "SIRHA-500-001");
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String value) {
        // helper for method parameter creation
    }
}
