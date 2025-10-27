package edu.dosw.sirha.dto.auth;

import java.time.Instant;

public record RefreshResponse(
        String token,
        Instant expiresAt
) {
}
