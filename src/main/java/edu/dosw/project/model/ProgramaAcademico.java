package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Modelo para programas académicos
 */
@Document(collection = "programas_academicos")
public class ProgramaAcademico {
    
    @Id
    private String id;
    
    private String nombre;
    
    private String codigo;
    
    private String facultadId;
    
    private String coordinadorId;
    
    private boolean activo;
    
    // Campos para auto-aprobación
    private boolean autoAprobacionHabilitada;
    
    private Map<String, Object> criteriosAutoAprobacion;
    
    private Integer limiteCapacidadAutoAprobacion;

    // Constructors
    public ProgramaAcademico() {}

    public ProgramaAcademico(String id, String nombre, String codigo, String facultadId, 
                            String coordinadorId, boolean activo, boolean autoAprobacionHabilitada,
                            Map<String, Object> criteriosAutoAprobacion, Integer limiteCapacidadAutoAprobacion) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.facultadId = facultadId;
        this.coordinadorId = coordinadorId;
        this.activo = activo;
        this.autoAprobacionHabilitada = autoAprobacionHabilitada;
        this.criteriosAutoAprobacion = criteriosAutoAprobacion;
        this.limiteCapacidadAutoAprobacion = limiteCapacidadAutoAprobacion;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFacultadId() {
        return facultadId;
    }

    public void setFacultadId(String facultadId) {
        this.facultadId = facultadId;
    }

    public String getCoordinadorId() {
        return coordinadorId;
    }

    public void setCoordinadorId(String coordinadorId) {
        this.coordinadorId = coordinadorId;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isAutoAprobacionHabilitada() {
        return autoAprobacionHabilitada;
    }

    public void setAutoAprobacionHabilitada(boolean autoAprobacionHabilitada) {
        this.autoAprobacionHabilitada = autoAprobacionHabilitada;
    }

    public Map<String, Object> getCriteriosAutoAprobacion() {
        return criteriosAutoAprobacion;
    }

    public void setCriteriosAutoAprobacion(Map<String, Object> criteriosAutoAprobacion) {
        this.criteriosAutoAprobacion = criteriosAutoAprobacion;
    }

    public Integer getLimiteCapacidadAutoAprobacion() {
        return limiteCapacidadAutoAprobacion;
    }

    public void setLimiteCapacidadAutoAprobacion(Integer limiteCapacidadAutoAprobacion) {
        this.limiteCapacidadAutoAprobacion = limiteCapacidadAutoAprobacion;
    }
}