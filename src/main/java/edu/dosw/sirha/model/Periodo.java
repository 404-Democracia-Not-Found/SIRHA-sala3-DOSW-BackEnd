package edu.dosw.sirha.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "periodos_academicos")
public class Periodo {

    @Id
    private String id;

    @NotNull
    private Instant fechaInicio;

    @NotNull
    private Instant fechaFin;

    @NotNull
    private Instant fechaInscripcionInicio;

    @NotNull
    private Instant fechaLimiteSolicitudes;

    private int ano;
    private int semestre;
    private boolean activo;

    private PeriodoConfiguracion configuracion;
}