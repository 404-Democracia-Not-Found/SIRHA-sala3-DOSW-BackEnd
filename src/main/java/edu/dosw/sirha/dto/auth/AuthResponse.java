package edu.dosw.sirha.dto.auth;

import java.time.Instant;

/**
 * DTO de respuesta de autenticación exitosa.
 * 
 * <p>Retorna token JWT y información básica del usuario autenticado.</p>
 * <p>El token debe incluirse en header {@code Authorization: Bearer <token>}
 * para requests protegidos.</p>
 * 
 * @param token Token JWT firmado (válido por el tiempo configurado)
 * @param expiresAt Fecha y hora de expiración del token
 * @param user Información básica del usuario autenticado
 * 
 * @see AuthRequest
 * @see edu.dosw.sirha.security.JwtTokenService
 */
public record AuthResponse(
        String token,
        Instant expiresAt,
        String refreshToken,
        Instant refreshExpiresAt,
        UserInfo user
) {
    /**
     * Información básica del usuario autenticado.
     * 
     * @param id ID del usuario en base de datos
     * @param nombre Nombre completo
     * @param email Email (username)
     * @param rol Rol del usuario (ESTUDIANTE, DOCENTE, COORDINADOR, ADMIN)
     */
    public record UserInfo(
            String id,
            String nombre,
            String email,
            String rol
    ) {
    }
}
