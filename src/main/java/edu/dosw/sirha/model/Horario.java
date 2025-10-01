package edu.dosw.sirha.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Horario {
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipoClase;
    private String salon;
}