package edu.dosw.sirha.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import edu.dosw.sirha.model.enums.EstadoInscripcion;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inscripciones")
public class Inscripcion {

    @Id
    private String id;

    @NotBlank
    private String estudianteId;

    @NotBlank
    private String grupoId;

    @NotBlank
    private String periodoId;

    @Field("fecha_inscripcion")
    private Instant fechaInscripcion;

    private EstadoInscripcion estado;

    private BigDecimal calificacionFinal;

    @Field("fecha_cambio_estado")
    private Instant fechaCambioEstado;

    private String observaciones;

    private boolean esPrimeraVez;

    private int intentosPrevios;

    private String solicitudOrigenId;
}
