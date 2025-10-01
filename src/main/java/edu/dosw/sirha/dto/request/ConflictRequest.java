package edu.dosw.sirha.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConflictRequest {

    @NotBlank
    private String tipo;

    private String descripcion;

    @NotBlank
    private String estudianteId;

    private String solicitudId;

    private String grupoId;

    private String observaciones;
}