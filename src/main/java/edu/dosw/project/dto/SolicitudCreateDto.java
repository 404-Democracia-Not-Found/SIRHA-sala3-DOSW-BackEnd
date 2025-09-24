package edu.dosw.project.dto;

import jakarta.validation.constraints.NotBlank;

public class SolicitudCreateDto {
    @NotBlank(message = "Tipo de solicitud es requerido")
    private String tipo; // CAMBIO_GRUPO, CAMBIO_MATERIA
    
    @NotBlank(message = "Descripción es requerida")
    private String descripcion;
    
    @NotBlank(message = "ID de inscripción origen es requerido")
    private String inscripcionOrigenId;
    
    @NotBlank(message = "ID de grupo destino es requerido")
    private String grupoDestinoId;
    
    @NotBlank(message = "ID del período es requerido")
    private String periodoId;
    
    public SolicitudCreateDto() {}
    
    // Getters and Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getInscripcionOrigenId() { return inscripcionOrigenId; }
    public void setInscripcionOrigenId(String inscripcionOrigenId) { this.inscripcionOrigenId = inscripcionOrigenId; }
    
    public String getGrupoDestinoId() { return grupoDestinoId; }
    public void setGrupoDestinoId(String grupoDestinoId) { this.grupoDestinoId = grupoDestinoId; }
    
    public String getPeriodoId() { return periodoId; }
    public void setPeriodoId(String periodoId) { this.periodoId = periodoId; }
}