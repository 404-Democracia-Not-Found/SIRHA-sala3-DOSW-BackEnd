package edu.dosw.sirha.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "grupos")
public class Grupo {

    @Id
    private String id;

    @NotBlank
    private String codigo;

    @NotBlank
    private String materiaId;

    @NotBlank
    private String periodoId;

    private String profesorId;

    @Min(0)
    private int cupoMax;

    @Min(0)
    private int cuposActuales;

    private String salon;

    private Instant fechaInicio;
    private Instant fechaFin;

    private List<Horario> horarios;

    private List<String> listaEspera;

    private boolean activo;
    
    /**
     * Verifica si el grupo tiene cupos disponibles
     */
    public boolean tieneCuposDisponibles() {
        return cuposActuales < cupoMax;
    }
    
    /**
     * Calcula el porcentaje de ocupación del grupo
     */
    public double porcentajeOcupacion() {
        if (cupoMax == 0) return 0.0;
        return (double) cuposActuales / cupoMax * 100;
    }
    
    /**
     * Verifica si el grupo está llegando al 90% de capacidad
     */
    public boolean estaEnAlerta() {
        return porcentajeOcupacion() >= 90.0;
    }
    
    /**
     * Valida que todos los horarios sean válidos
     */
    public boolean tieneHorariosValidos() {
        if (horarios == null || horarios.isEmpty()) {
            return false;
        }
        return horarios.stream().allMatch(Horario::esHorarioValido);
    }
    
    /**
     * Verifica si hay conflicto de horarios con otro grupo
     */
    public boolean tieneConflictoHorarioCon(Grupo otroGrupo) {
        if (this.horarios == null || otroGrupo.getHorarios() == null) {
            return false;
        }
        
        return this.horarios.stream()
                .anyMatch(horario1 -> otroGrupo.getHorarios().stream()
                        .anyMatch(horario1::tieneConflictoCon));
    }
    
    /**
     * Incrementa el cupo actual (para inscripciones)
     */
    public boolean incrementarCupo() {
        if (tieneCuposDisponibles()) {
            cuposActuales++;
            return true;
        }
        return false;
    }
    
    /**
     * Decrementa el cupo actual (para cancelaciones)
     */
    public void decrementarCupo() {
        if (cuposActuales > 0) {
            cuposActuales--;
        }
    }
    
    /**
     * Agrega un estudiante a la lista de espera
     */
    public void agregarAListaEspera(String estudianteId) {
        if (listaEspera == null) {
            listaEspera = new ArrayList<>();
        }
        if (!listaEspera.contains(estudianteId)) {
            listaEspera.add(estudianteId);
        }
    }
    
    /**
     * Remueve un estudiante de la lista de espera
     */
    public void removerDeListaEspera(String estudianteId) {
        if (listaEspera != null) {
            listaEspera.remove(estudianteId);
        }
    }
}
