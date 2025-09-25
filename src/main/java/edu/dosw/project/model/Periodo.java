package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Modelo de Período Académico para el sistema SIRHA
 * Representa los períodos académicos (semestres) disponibles
 */
@Document(collection = "periodos")
public class Periodo {
    @Id
    private String id;
    
    private String codigo; // 2024-1, 2024-2
    private String nombre; // Primer Semestre 2024
    
    @Field("fecha_inicio")
    private LocalDateTime fechaInicio;
    
    @Field("fecha_fin") 
    private LocalDateTime fechaFin;
    
    @Field("fecha_inicio_solicitudes")
    private LocalDateTime fechaInicioSolicitudes;
    
    @Field("fecha_fin_solicitudes")
    private LocalDateTime fechaFinSolicitudes;
    
    private String estado; // PROGRAMACION, ACTIVO, FINALIZADO
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    private Boolean activo;

    public Periodo() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
        this.estado = "PROGRAMACION";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public LocalDateTime getFechaInicioSolicitudes() { return fechaInicioSolicitudes; }
    public void setFechaInicioSolicitudes(LocalDateTime fechaInicioSolicitudes) { 
        this.fechaInicioSolicitudes = fechaInicioSolicitudes; 
    }

    public LocalDateTime getFechaFinSolicitudes() { return fechaFinSolicitudes; }
    public void setFechaFinSolicitudes(LocalDateTime fechaFinSolicitudes) { 
        this.fechaFinSolicitudes = fechaFinSolicitudes; 
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    // Métodos de negocio
    public boolean isWithinSolicitudesPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return fechaInicioSolicitudes != null && fechaFinSolicitudes != null &&
               !now.isBefore(fechaInicioSolicitudes) && !now.isAfter(fechaFinSolicitudes);
    }
}