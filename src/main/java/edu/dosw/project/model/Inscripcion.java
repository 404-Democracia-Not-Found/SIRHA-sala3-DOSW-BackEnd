package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "inscripciones")
public class Inscripcion {
    @Id
    private String id;
    
    @Field("estudiante_id")
    private String estudianteId;
    
    @Field("grupo_id")
    private String grupoId;
    
    @Field("periodo_academico_id")
    private String periodoAcademicoId;
    
    private EstadoMateria estado; // APROBADA, EN_CURSO, NO_APROBADA
    
    @Field("fecha_inscripcion")
    private LocalDateTime fechaInscripcion;
    
    @Field("fecha_retiro")
    private LocalDateTime fechaRetiro;
    
    @Field("nota_final")
    private Double notaFinal;
    
    @Field("numero_intentos")
    private Integer numeroIntentos;
    
    private Boolean activa;

    // Enum para el semáforo académico
    public enum EstadoMateria {
        APROBADA,    // Verde - materia aprobada
        EN_CURSO,    // Azul - materia en progreso
        NO_APROBADA  // Rojo - materia no aprobada/perdida
    }

    // Constructor por defecto
    public Inscripcion() {
        this.fechaInscripcion = LocalDateTime.now();
        this.activa = true;
        this.numeroIntentos = 1;
        this.estado = EstadoMateria.EN_CURSO;
    }

    // Constructor con parámetros principales
    public Inscripcion(String estudianteId, String grupoId, String periodoAcademicoId) {
        this();
        this.estudianteId = estudianteId;
        this.grupoId = grupoId;
        this.periodoAcademicoId = periodoAcademicoId;
    }

    // Métodos de negocio
    public boolean estaAprobada() {
        return estado == EstadoMateria.APROBADA;
    }

    public boolean estaEnCurso() {
        return estado == EstadoMateria.EN_CURSO;
    }

    public boolean noAprobada() {
        return estado == EstadoMateria.NO_APROBADA;
    }

    public boolean puedeGenerarSolicitudCambio() {
        return activa && (estado == EstadoMateria.EN_CURSO);
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEstudianteId() { return estudianteId; }
    public void setEstudianteId(String estudianteId) { this.estudianteId = estudianteId; }

    public String getGrupoId() { return grupoId; }
    public void setGrupoId(String grupoId) { this.grupoId = grupoId; }

    public String getPeriodoAcademicoId() { return periodoAcademicoId; }
    public void setPeriodoAcademicoId(String periodoAcademicoId) { this.periodoAcademicoId = periodoAcademicoId; }

    public EstadoMateria getEstado() { return estado; }
    public void setEstado(EstadoMateria estado) { this.estado = estado; }
    
    public EstadoMateria getEstadoMateria() { return estado; }

    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public LocalDateTime getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(LocalDateTime fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public Double getNotaFinal() { return notaFinal; }
    public void setNotaFinal(Double notaFinal) { this.notaFinal = notaFinal; }

    public Integer getNumeroIntentos() { return numeroIntentos; }
    public void setNumeroIntentos(Integer numeroIntentos) { this.numeroIntentos = numeroIntentos; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}