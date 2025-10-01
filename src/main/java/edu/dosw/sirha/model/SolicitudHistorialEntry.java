package edu.dosw.sirha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudHistorialEntry {
    private Instant fecha;
    private String accion;
    private String usuarioId;
    private String comentario;
}
