package edu.dosw.project.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para solicitudes de cambio de horario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudResponse {
    
    private String id;
    private String estudianteId;
    private String estudianteNombre;
    private String estudianteCodigo;
    private String materiaId;
    private String materiaNombre;
    private String grupoActualId;
    private String grupoActualCodigo;
    private String grupoNuevoId;
    private String grupoNuevoCodigo;
    private String motivo;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaProcesamiento;
    private String coordinadorId;
    private String coordinadorNombre;
    private String observaciones;
    private List<ConflictoInfo> conflictos;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConflictoInfo {
        private String tipo;
        private String descripcion;
        private String nivel; // ALTO, MEDIO, BAJO
        private boolean bloqueante;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistorialInfo {
        private LocalDateTime fecha;
        private String accion;
        private String usuario;
        private String observaciones;
    }
}