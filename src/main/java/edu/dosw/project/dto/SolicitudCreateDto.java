package edu.dosw.project.dto;

import jakarta.validation.constraints.NotBlank;

public class SolicitudCreateDto {
    @NotBlank
    private String studentId;
    @NotBlank
    private String materiaId;
    @NotBlank
    private String horarioActualId;
    @NotBlank
    private String horarioPropuestoId;
    private String comments;

    public SolicitudCreateDto() {}

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getMateriaId() { return materiaId; }
    public void setMateriaId(String materiaId) { this.materiaId = materiaId; }

    public String getHorarioActualId() { return horarioActualId; }
    public void setHorarioActualId(String horarioActualId) { this.horarioActualId = horarioActualId; }

    public String getHorarioPropuestoId() { return horarioPropuestoId; }
    public void setHorarioPropuestoId(String horarioPropuestoId) { this.horarioPropuestoId = horarioPropuestoId; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}