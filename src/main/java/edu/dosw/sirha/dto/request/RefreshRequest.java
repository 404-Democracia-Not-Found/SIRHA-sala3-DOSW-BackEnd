package edu.dosw.sirha.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank
        String refreshToken
) {
}
