package edu.dosw.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para las solicitudes de cambio de materia/grupo
 * Cumple con los requerimientos SIRHA para solicitudes de estudiantes
 */
@Data
public class SolicitudCambioRequest {
    
    @NotBlank(message = "La materia actual es obligatoria")
    private String materiaActualId;
    
    @NotBlank(message = "El grupo actual es obligatorio") 
    private String grupoActualId;
    
    @NotBlank(message = "La materia destino es obligatoria")
    private String materiaDestinoId;
    
    @NotBlank(message = "El grupo destino es obligatorio")
    private String grupoDestinoId;
    
    @NotNull(message = "El motivo es obligatorio")
    private String motivo;
    
    private String observaciones;
    
    @NotBlank(message = "El periodo académico es obligatorio")
    private String periodoAcademicoId;
}