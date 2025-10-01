package edu.dosw.sirha.dto.request;

import edu.dosw.sirha.model.enums.SolicitudTipo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequest {

    @NotNull
    private SolicitudTipo tipo;

    @NotBlank
    private String estudianteId;

    private String descripcion;

    private String observaciones;

    private String inscripcionOrigenId;

    private String grupoDestinoId;

    private String materiaDestinoId;

    private String periodoId;

    @Min(0)
    private int prioridad;
}