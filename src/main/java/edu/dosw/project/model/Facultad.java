package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "facultades")
public class Facultad {
    @Id
    private String id;
    
    private String codigo;
    private String nombre;
    private String descripcion;
    
    @Field("coordinador_id")
    private String coordinadorId;
    
    @Field("materias_ids")
    private List<String> materiasIds;
    
    private Boolean activa;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Constructor por defecto
    public Facultad() {
        this.fechaCreacion = LocalDateTime.now();
        this.activa = true;
    }

    // Constructor con parámetros principales
    public Facultad(String codigo, String nombre) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCoordinadorId() { return coordinadorId; }
    public void setCoordinadorId(String coordinadorId) { this.coordinadorId = coordinadorId; }

    public List<String> getMateriasIds() { return materiasIds; }
    public void setMateriasIds(List<String> materiasIds) { this.materiasIds = materiasIds; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}