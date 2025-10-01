package edu.dosw.sirha.dto.request;

import edu.dosw.sirha.model.PeriodoConfiguracion;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoRequest {

    @NotNull
    private Instant fechaInicio;

    @NotNull
    private Instant fechaFin;

    @NotNull
    private Instant fechaInscripcionInicio;

    @NotNull
    private Instant fechaLimiteSolicitudes;

    @NotNull
    private Integer ano;

    @NotNull
    private Integer semestre;

    @NotNull
    private Boolean activo;

    private PeriodoConfiguracion configuracion;
}