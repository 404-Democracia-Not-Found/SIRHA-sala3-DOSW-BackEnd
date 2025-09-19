package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document("materias")
public class Materia {
    @Id
    private String id;
    private String codigo;
    private String nombre;
    private int creditos;
    private List<String> aulasPosibles;

    public Materia() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    public List<String> getAulasPosibles() { return aulasPosibles; }
    public void setAulasPosibles(List<String> aulasPosibles) { this.aulasPosibles = aulasPosibles; }
}