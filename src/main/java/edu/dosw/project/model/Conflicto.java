package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.List;

@Document("conflictos")
public class Conflicto {
    @Id
    private String id;
    
    @Field("descripcion")
    private String descripcion;
    
    @Field("involucrados")
    private List<String> involucrados;
    
    @Field("fechaDeteccion")
    private LocalDateTime detectedAt;
    
    @Field("resuelto")
    private boolean resolved;
    
    @Field("fechaResolucion")
    private LocalDateTime fechaResolucion;
    
    @Field("usuarioResolver")
    private String usuarioResolver;

    public Conflicto() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<String> getInvolucrados() { return involucrados; }
    public void setInvolucrados(List<String> involucrados) { this.involucrados = involucrados; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    
    public String getUsuarioResolver() { return usuarioResolver; }
    public void setUsuarioResolver(String usuarioResolver) { this.usuarioResolver = usuarioResolver; }
}