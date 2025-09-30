package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalTime;
import java.time.DayOfWeek;

@Document("horarios")
public class Horario {
    @Id
    private String id;
    private String materiaId;
    private DayOfWeek dia;
    private LocalTime inicio;
    private LocalTime fin;
    private String aulaId;
    private int cupos;
    private int inscritos;

    public Horario() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMateriaId() { return materiaId; }
    public void setMateriaId(String materiaId) { this.materiaId = materiaId; }

    public DayOfWeek getDia() { return dia; }
    public void setDia(DayOfWeek dia) { this.dia = dia; }

    public LocalTime getInicio() { return inicio; }
    public void setInicio(LocalTime inicio) { this.inicio = inicio; }

    public LocalTime getFin() { return fin; }
    public void setFin(LocalTime fin) { this.fin = fin; }

    public String getAulaId() { return aulaId; }
    public void setAulaId(String aulaId) { this.aulaId = aulaId; }

    public int getCupos() { return cupos; }
    public void setCupos(int cupos) { this.cupos = cupos; }

    public int getInscritos() { return inscritos; }
    public void setInscritos(int inscritos) { this.inscritos = inscritos; }

    public boolean isFull() {
        return inscritos >= cupos;
    }
    
    // Métodos adicionales para compatibilidad con controladores
    public String getDiaSemana() {
        return dia != null ? dia.name() : null;
    }
    
    public LocalTime getHoraInicio() {
        return inicio;
    }
    
    public LocalTime getHoraFin() {
        return fin;
    }
    
    public String getAula() {
        return aulaId;
    }
}