package edu.dosw.sirha.dto.request;

import edu.dosw.sirha.model.PeriodoConfiguracion;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO de solicitud para crear o actualizar un periodo académico.
 * 
 * <p>Usado en {@code POST /api/periodos} y {@code PUT /api/periodos/{id}}
 * por coordinadores o admins para gestionar semestres.</p>
 * 
 * @see PeriodoResponse
 * @see edu.dosw.sirha.model.Periodo
 * @see edu.dosw.sirha.controller.PeriodoController
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoRequest {

    /**
     * Fecha de inicio del periodo académico.
     */
    @NotNull
    private Instant fechaInicio;

    /**
     * Fecha de fin del periodo académico.
     */
    @NotNull
    private Instant fechaFin;

    /**
     * Fecha de inicio de inscripciones.
     */
    @NotNull
    private Instant fechaInscripcionInicio;

    /**
     * Fecha límite para presentar solicitudes de cambio.
     */
    @NotNull
    private Instant fechaLimiteSolicitudes;

    /**
     * Año del periodo (ej: 2024).
     */
    @NotNull
    private Integer ano;

    /**
     * Semestre del periodo (1, 2 o 3 para intersemestral).
     */
    @NotNull
    private Integer semestre;

    /**
     * Indica si el periodo está activo (solo uno puede estar activo).
     */
    @NotNull
    private Boolean activo;

    /**
     * Configuración específica del periodo (políticas y límites).
     */
    private PeriodoConfiguracion configuracion;
}