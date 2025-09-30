package edu.dosw.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la creación de solicitudes de cambio de grupo
 */
public class SolicitudCambioRequest {
    
    @NotBlank(message = "La inscripción origen es requerida")
    private String inscripcionOrigenId;
    
    @NotBlank(message = "El grupo destino es requerido")
    private String grupoDestinoId;
    
    private String observaciones;
    
    @NotNull(message = "El tipo de solicitud es requerido")
    private TipoSolicitud tipo;
    
    public enum TipoSolicitud {
        CAMBIO_GRUPO,
        CAMBIO_MATERIA
    }

    // Constructor por defecto
    public SolicitudCambioRequest() {}

    // Constructor con parámetros principales
    public SolicitudCambioRequest(String inscripcionOrigenId, String grupoDestinoId, TipoSolicitud tipo) {
        this.inscripcionOrigenId = inscripcionOrigenId;
        this.grupoDestinoId = grupoDestinoId;
        this.tipo = tipo;
    }

    // Getters y Setters
    public String getInscripcionOrigenId() { return inscripcionOrigenId; }
    public void setInscripcionOrigenId(String inscripcionOrigenId) { this.inscripcionOrigenId = inscripcionOrigenId; }

    public String getGrupoDestinoId() { return grupoDestinoId; }
    public void setGrupoDestinoId(String grupoDestinoId) { this.grupoDestinoId = grupoDestinoId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public TipoSolicitud getTipo() { return tipo; }
    public void setTipo(TipoSolicitud tipo) { this.tipo = tipo; }
}