package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document("solicitudes")
public class Solicitud {
    @Id
    private String id;
    private String studentId;
    private String materiaId;
    private String horarioActualId;
    private String horarioPropuestoId;
    private Status status;
    private String comments;
    private Instant createdAt;
    private Instant updatedAt;

    public enum Status { PENDING, APPROVED, REJECTED, CANCELLED }

    public Solicitud() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getMateriaId() { return materiaId; }
    public void setMateriaId(String materiaId) { this.materiaId = materiaId; }

    public String getHorarioActualId() { return horarioActualId; }
    public void setHorarioActualId(String horarioActualId) { this.horarioActualId = horarioActualId; }

    public String getHorarioPropuestoId() { return horarioPropuestoId; }
    public void setHorarioPropuestoId(String horarioPropuestoId) { this.horarioPropuestoId = horarioPropuestoId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}