package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "grupos")
public class Grupo {
    @Id
    private String id;
    
    private String codigo;
    
    @Field("materia_id")
    private String materiaId;
    
    @Field("profesor_id")
    private String profesorId;
    
    @Field("periodo_academico_id")
    private String periodoAcademicoId;
    
    @Field("cupo_maximo")
    private Integer cupoMaximo;
    
    @Field("estudiantes_inscritos")
    private Integer estudiantesInscritos;
    
    @Field("lista_espera")
    private List<String> listaEspera; // IDs de estudiantes en lista de espera
    
    @Field("horarios_ids")
    private List<String> horariosIds;
    
    private Boolean activo;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Constructor por defecto
    public Grupo() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
        this.estudiantesInscritos = 0;
    }

    // Constructor con parámetros principales
    public Grupo(String codigo, String materiaId, Integer cupoMaximo) {
        this();
        this.codigo = codigo;
        this.materiaId = materiaId;
        this.cupoMaximo = cupoMaximo;
    }

    // Métodos de negocio
    public boolean estaLleno() {
        return estudiantesInscritos >= cupoMaximo;
    }

    public boolean tieneCupoDisponible() {
        return estudiantesInscritos < cupoMaximo;
    }

    public int getCuposDisponibles() {
        return cupoMaximo - estudiantesInscritos;
    }

    public boolean estaEnCapacidadCritica() {
        return estudiantesInscritos >= (cupoMaximo * 0.9);
    }

    public double getPorcentajeOcupacion() {
        return cupoMaximo > 0 ? (double) estudiantesInscritos / cupoMaximo * 100 : 0;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getMateriaId() { return materiaId; }
    public void setMateriaId(String materiaId) { this.materiaId = materiaId; }

    public String getProfesorId() { return profesorId; }
    public void setProfesorId(String profesorId) { this.profesorId = profesorId; }

    public String getPeriodoAcademicoId() { return periodoAcademicoId; }
    public void setPeriodoAcademicoId(String periodoAcademicoId) { this.periodoAcademicoId = periodoAcademicoId; }

    public Integer getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public Integer getEstudiantesInscritos() { return estudiantesInscritos; }
    public void setEstudiantesInscritos(Integer estudiantesInscritos) { this.estudiantesInscritos = estudiantesInscritos; }

    public List<String> getListaEspera() { return listaEspera; }
    public void setListaEspera(List<String> listaEspera) { this.listaEspera = listaEspera; }

    public List<String> getHorariosIds() { return horariosIds; }
    public void setHorariosIds(List<String> horariosIds) { this.horariosIds = horariosIds; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}