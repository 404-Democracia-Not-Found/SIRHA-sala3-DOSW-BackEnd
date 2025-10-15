package edu.dosw.sirha.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Horario {
    @NotNull
    private DayOfWeek dia;
    
    @NotNull
    private LocalTime horaInicio;
    
    @NotNull
    private LocalTime horaFin;
    
    private String tipoClase;
    private String salon;
    
    // Constantes para horarios permitidos
    public static final LocalTime HORA_INICIO_SEMANAL = LocalTime.of(7, 0);
    public static final LocalTime HORA_FIN_SEMANAL = LocalTime.of(19, 0);
    public static final LocalTime HORA_FIN_SABADO = LocalTime.of(13, 0);
    
    /**
     * Valida si el horario está dentro de los rangos permitidos
     */
    public boolean esHorarioValido() {
        if (dia == DayOfWeek.SUNDAY) {
            return false; // No hay clases los domingos
        }
        
        if (horaInicio.isBefore(HORA_INICIO_SEMANAL)) {
            return false; // No antes de las 7:00 AM
        }
        
        if (dia == DayOfWeek.SATURDAY) {
            return !horaFin.isAfter(HORA_FIN_SABADO); // Sábados hasta 1:00 PM
        } else {
            return !horaFin.isAfter(HORA_FIN_SEMANAL); // Lunes a viernes hasta 7:00 PM
        }
    }
    
    /**
     * Verifica si hay conflicto con otro horario
     */
    public boolean tieneConflictoCon(Horario otro) {
        if (!this.dia.equals(otro.dia)) {
            return false; // Diferentes días, no hay conflicto
        }
        
        // Verificar solapamiento de horarios
        return this.horaInicio.isBefore(otro.horaFin) && this.horaFin.isAfter(otro.horaInicio);
    }
}