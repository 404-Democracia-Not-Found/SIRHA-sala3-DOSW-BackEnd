package edu.dosw.sirha.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conflictos")
public class Conflict {

    @Id
    private String id;

    @NotBlank
    private String tipo;

    private String descripcion;

    @NotBlank
    private String estudianteId;

    private String solicitudId;

    private String grupoId;

    private Instant fechaDeteccion;

    private boolean resuelto;

    private String observaciones;
}