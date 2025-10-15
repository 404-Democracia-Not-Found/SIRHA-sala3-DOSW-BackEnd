package edu.dosw.sirha.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de solicitud para autenticación (login).
 * 
 * <p>Usado en {@code POST /api/auth/login} para validar credenciales
 * y generar token JWT.</p>
 * 
 * @param email Email del usuario (debe ser válido)
 * @param password Contraseña en texto plano (se hashea en servidor)
 * 
 * @see edu.dosw.sirha.controller.AuthController#login(AuthRequest)
 * @see AuthResponse
 */
public record AuthRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
