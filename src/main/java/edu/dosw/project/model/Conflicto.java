package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Document("conflictos")
public class Conflicto {
    @Id
    private String id;
    private String descripcion;
    private List<String> involucrados;
    private Instant detectedAt;
    private boolean resolved;

    public Conflicto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<String> getInvolucrados() { return involucrados; }
    public void setInvolucrados(List<String> involucrados) { this.involucrados = involucrados; }

    public Instant getDetectedAt() { return detectedAt; }
    public void setDetectedAt(Instant detectedAt) { this.detectedAt = detectedAt; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}