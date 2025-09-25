package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Modelo de Aula para el sistema SIRHA
 * Representa las aulas disponibles para asignación de clases
 */
@Document(collection = "aulas")
public class Aula {
    @Id
    private String id;
    
    private String codigo;
    private String nombre;
    private String ubicacion;
    
    @Field("capacidad_maxima")
    private Integer capacidadMaxima;
    
    private String tipo; // LABORATORIO, AULA_REGULAR, AUDITORIO
    
    @Field("recursos_disponibles")
    private String[] recursosDisponibles; // PROYECTOR, COMPUTADORES, AIRE_ACONDICIONADO
    
    private String edificio;
    private String facultad;
    private Boolean activa;

    public Aula() {
        this.activa = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Integer getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(Integer capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String[] getRecursosDisponibles() { return recursosDisponibles; }
    public void setRecursosDisponibles(String[] recursosDisponibles) { this.recursosDisponibles = recursosDisponibles; }

    public String getEdificio() { return edificio; }
    public void setEdificio(String edificio) { this.edificio = edificio; }

    public String getFacultad() { return facultad; }
    public void setFacultad(String facultad) { this.facultad = facultad; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}