package edu.dosw.sirha.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Horario de clase dentro de un grupo.
 * 
 * <p>Representa un bloque de tiempo específico en el que se imparte clase para un grupo.
 * Un grupo puede tener múltiples horarios (ej: lunes y miércoles 8:00-10:00).</p>
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Validación de rangos permitidos institucionales</li>
 *   <li>Detección de conflictos de solapamiento entre horarios</li>
 *   <li>Reglas especiales para sábados (horario reducido)</li>
 *   <li>Prohibición de clases domingos</li>
 * </ul>
 * 
 * @see Grupo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Horario {
    /**
     * Día de la semana en que se imparte la clase.
     * 
     * <p>Valores permitidos: MONDAY a SATURDAY (no SUNDAY).</p>
     */
    @NotNull
    private DayOfWeek dia;
    
    /**
     * Hora de inicio de la clase.
     * 
     * <p>No puede ser antes de {@link #HORA_INICIO_SEMANAL} (7:00 AM).</p>
     */
    @NotNull
    private LocalTime horaInicio;
    
    /**
     * Hora de finalización de la clase.
     * 
     * <p>Límite: {@link #HORA_FIN_SEMANAL} (7:00 PM) lunes-viernes,
     * {@link #HORA_FIN_SABADO} (1:00 PM) sábados.</p>
     */
    @NotNull
    private LocalTime horaFin;
    
    /**
     * Tipo de clase (teoría, práctica, laboratorio, etc.).
     * 
     * <p>Ejemplo: "Teoría", "Laboratorio", "Tutoría".</p>
     */
    private String tipoClase;
    
    /**
     * Salón o aula donde se imparte la clase.
     * 
     * <p>Ejemplo: "A101", "Lab Sistemas", "Auditorio Central".</p>
     */
    private String salon;
    
    /**
     * Hora más temprana permitida para clases (7:00 AM).
     */
    public static final LocalTime HORA_INICIO_SEMANAL = LocalTime.of(7, 0);
    
    /**
     * Hora límite para clases lunes-viernes (7:00 PM).
     */
    public static final LocalTime HORA_FIN_SEMANAL = LocalTime.of(19, 0);
    
    /**
     * Hora límite para clases sábados (1:00 PM).
     */
    public static final LocalTime HORA_FIN_SABADO = LocalTime.of(13, 0);
    
    /**
     * Valida si el horario está dentro de los rangos permitidos institucionales.
     * 
     * <p>Reglas de validación:</p>
     * <ul>
     *   <li>No domingos</li>
     *   <li>No antes de las 7:00 AM</li>
     *   <li>Sábados: hasta 1:00 PM máximo</li>
     *   <li>Lunes-Viernes: hasta 7:00 PM máximo</li>
     * </ul>
     * 
     * @return true si cumple todas las reglas, false si viola alguna
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
     * Verifica si hay conflicto de solapamiento con otro horario.
     * 
     * <p>Dos horarios tienen conflicto si son el mismo día y sus rangos de tiempo
     * se solapan (aunque sea parcialmente).</p>
     * 
     * <p>Ejemplos de conflicto:</p>
     * <ul>
     *   <li>Lunes 8:00-10:00 vs Lunes 9:00-11:00 → Conflicto</li>
     *   <li>Lunes 8:00-10:00 vs Martes 8:00-10:00 → Sin conflicto</li>
     *   <li>Lunes 8:00-10:00 vs Lunes 10:00-12:00 → Sin conflicto (exactamente consecutivos)</li>
     * </ul>
     * 
     * @param otro El horario con el que se compara
     * @return true si hay solapamiento, false si no hay conflicto
     */
    public boolean tieneConflictoCon(Horario otro) {
        if (!this.dia.equals(otro.dia)) {
            return false; // Diferentes días, no hay conflicto
        }
        
        // Verificar solapamiento de horarios
        return this.horaInicio.isBefore(otro.horaFin) && this.horaFin.isAfter(otro.horaInicio);
    }
}