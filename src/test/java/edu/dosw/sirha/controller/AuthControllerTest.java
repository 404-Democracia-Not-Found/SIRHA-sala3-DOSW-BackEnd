package edu.dosw.sirha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.sirha.dto.auth.AuthRequest;
import edu.dosw.sirha.dto.auth.AuthResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Suite de pruebas unitarias para {@link AuthController}.
 * 
 * <p>Esta clase verifica el correcto funcionamiento del endpoint de autenticación,
 * incluyendo login exitoso, manejo de usuarios inactivos, credenciales inválidas,
 * generación de tokens JWT y validación de la estructura de respuestas.</p>
 * 
 * <p><strong>Configuración de pruebas:</strong></p>
 * <ul>
 *   <li>Usa {@code @WebMvcTest} para cargar solo el controlador bajo prueba</li>
 *   <li>Deshabilita filtros de seguridad con {@code addFilters = false}</li>
 *   <li>Mockea AuthService para evitar dependencias reales</li>
 * </ul>
 * 
 * <p><strong>Casos de prueba cubiertos:</strong></p>
 * <ul>
 *   <li>Login exitoso con credenciales válidas</li>
 *   <li>Rechazo de usuarios inactivos</li>
 *   <li>Manejo de credenciales incorrectas</li>
 *   <li>Generación y formato de tokens JWT</li>
 *   <li>Validación de campos en respuestas</li>
 * </ul>
 * 
 * @see AuthController
 * @see org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String INACTIVE_EMAIL = "inactive@test.com";
    private static final Instant FIXED_NOW = Instant.parse("2025-10-01T10:15:30Z");
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.test.token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void loginShouldReturnToken() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(ADMIN_EMAIL, DEFAULT_PASSWORD);
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                "user-1",
                "Admin Test",
                ADMIN_EMAIL,
                "ADMIN"
        );
        AuthResponse expectedResponse = new AuthResponse(
                TEST_TOKEN,
                FIXED_NOW.plusSeconds(3600),
                userInfo
        );

        when(authService.login(any(AuthRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.token").value(TEST_TOKEN))
                .andExpect(jsonPath("$.expiresAt").value(FIXED_NOW.plusSeconds(3600).toString()))
                .andExpect(jsonPath("$.user.id").value("user-1"))
                .andExpect(jsonPath("$.user.email").value(ADMIN_EMAIL))
                .andExpect(jsonPath("$.user.rol").value("ADMIN"));
    }

    @Test
    void loginShouldReturnBadRequestWhenUserInactive() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(INACTIVE_EMAIL, DEFAULT_PASSWORD);
        
        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new BusinessException("Tu cuenta está inactiva"));

        // Act & Assert
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tu cuenta está inactiva"));
    }

    @Test
    void loginShouldReturnBadRequestWhenPrincipalIsNotUserPrincipal() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest("unknown@test.com", DEFAULT_PASSWORD);
        
        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new BusinessException("No fue posible autenticar al usuario"));

        // Act & Assert
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No fue posible autenticar al usuario"));
    }
}
