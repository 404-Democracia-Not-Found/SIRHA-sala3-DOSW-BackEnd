package edu.dosw.sirha.dto.auth;

import java.time.Instant;

public record AuthResponse(
        String token,
        Instant expiresAt,
        UserInfo user
) {
    public record UserInfo(
            String id,
            String nombre,
            String email,
            String rol
    ) {
    }
}
