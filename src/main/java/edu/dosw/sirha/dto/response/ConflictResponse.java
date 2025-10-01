package edu.dosw.sirha.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ConflictResponse {
    String id;
    String tipo;
    String descripcion;
    String estudianteId;
    String solicitudId;
    String grupoId;
    Instant fechaDeteccion;
    boolean resuelto;
    String observaciones;
}