package edu.dosw.sirha.dto.refresh;

import java.time.Instant;

public record RefreshResponse(
        String token,
        Instant expiresAt
) {
}
