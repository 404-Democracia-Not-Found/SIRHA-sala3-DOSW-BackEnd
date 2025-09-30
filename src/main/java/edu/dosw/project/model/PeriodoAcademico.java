package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "periodos_academicos")
public class PeriodoAcademico {
    @Id
    private String id;
    
    private String codigo; // "2025-1", "2025-2"
    private String nombre; // "2025-1", "2025-2"
    private Integer anio;
    private Integer semestre; // 1 o 2
    
    @Field("fecha_inicio")
    private LocalDate fechaInicio;
    
    @Field("fecha_fin")
    private LocalDate fechaFin;
    
    @Field("fecha_inicio_solicitudes")
    private LocalDate fechaInicioSolicitudes;
    
    @Field("fecha_fin_solicitudes")
    private LocalDate fechaFinSolicitudes;
    
    private Boolean activo;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Constructor por defecto
    public PeriodoAcademico() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    // Constructor con parámetros principales
    public PeriodoAcademico(String nombre, Integer anio, Integer semestre, 
                           LocalDate fechaInicio, LocalDate fechaFin) {
        this();
        this.nombre = nombre;
        this.codigo = anio + "-" + semestre;
        this.anio = anio;
        this.semestre = semestre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Métodos de negocio
    public boolean estaEnPeriodoSolicitudes() {
        LocalDate hoy = LocalDate.now();
        return fechaInicioSolicitudes != null && fechaFinSolicitudes != null &&
               !hoy.isBefore(fechaInicioSolicitudes) && !hoy.isAfter(fechaFinSolicitudes);
    }

    public boolean estaVigente() {
        LocalDate hoy = LocalDate.now();
        return activo && !hoy.isBefore(fechaInicio) && !hoy.isAfter(fechaFin);
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { 
        this.anio = anio; 
    }

    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public LocalDate getFechaInicioSolicitudes() { return fechaInicioSolicitudes; }
    public void setFechaInicioSolicitudes(LocalDate fechaInicioSolicitudes) { 
        this.fechaInicioSolicitudes = fechaInicioSolicitudes; 
    }

    public LocalDate getFechaFinSolicitudes() { return fechaFinSolicitudes; }
    public void setFechaFinSolicitudes(LocalDate fechaFinSolicitudes) { 
        this.fechaFinSolicitudes = fechaFinSolicitudes; 
    }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}