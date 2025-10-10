package edu.dosw.sirha.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class HorarioTest {

    @Test
    void testHorarioValidoEntreSemana() {
        Horario horario = Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .tipoClase("TEORIA")
                .salon("A101")
                .build();

        assertTrue(horario.esHorarioValido(), "El horario entre semana debe ser válido");
    }

    @Test
    void testHorarioValidoSabado() {
        Horario horario = Horario.builder()
                .dia(DayOfWeek.SATURDAY)
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(12, 0))
                .tipoClase("LABORATORIO")
                .salon("LAB1")
                .build();

        assertTrue(horario.esHorarioValido(), "El horario del sábado hasta las 1 PM debe ser válido");
    }

    @Test
    void testHorarioInvalidoDomingo() {
        Horario horario = Horario.builder()
                .dia(DayOfWeek.SUNDAY)
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(11, 0))
                .tipoClase("TEORIA")
                .salon("A101")
                .build();

        assertFalse(horario.esHorarioValido(), "No debe haber clases los domingos");
    }

    @Test
    void testHorarioInvalidoMuyTemprano() {
        Horario horario = Horario.builder()
                .dia(DayOfWeek.TUESDAY)
                .horaInicio(LocalTime.of(6, 0))
                .horaFin(LocalTime.of(8, 0))
                .tipoClase("TEORIA")
                .salon("A101")
                .build();

        assertFalse(horario.esHorarioValido(), "No debe haber clases antes de las 7:00 AM");
    }

    @Test
    void testHorarioInvalidoMuyTarde() {
        Horario horario = Horario.builder()
                .dia(DayOfWeek.WEDNESDAY)
                .horaInicio(LocalTime.of(18, 0))
                .horaFin(LocalTime.of(20, 0))
                .tipoClase("TEORIA")
                .salon("A101")
                .build();

        assertFalse(horario.esHorarioValido(), "No debe haber clases después de las 7:00 PM entre semana");
    }

    @Test
    void testHorarioInvalidoSabadoTarde() {
        Horario horario = Horario.builder()
                .dia(DayOfWeek.SATURDAY)
                .horaInicio(LocalTime.of(12, 0))
                .horaFin(LocalTime.of(14, 0))
                .tipoClase("LABORATORIO")
                .salon("LAB1")
                .build();

        assertFalse(horario.esHorarioValido(), "No debe haber clases los sábados después de la 1:00 PM");
    }

    @Test
    void testConflictoHorarios() {
        Horario horario1 = Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .build();

        Horario horario2 = Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(11, 0))
                .build();

        assertTrue(horario1.tieneConflictoCon(horario2), "Debe detectar conflicto en horarios superpuestos");
        assertTrue(horario2.tieneConflictoCon(horario1), "El conflicto debe ser bidireccional");
    }

    @Test
    void testNoConflictoHorariosDiferentesDias() {
        Horario horario1 = Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .build();

        Horario horario2 = Horario.builder()
                .dia(DayOfWeek.TUESDAY)
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(11, 0))
                .build();

        assertFalse(horario1.tieneConflictoCon(horario2), "No debe haber conflicto en días diferentes");
    }

    @Test
    void testNoConflictoHorariosConsecutivos() {
        Horario horario1 = Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .build();

        Horario horario2 = Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(12, 0))
                .build();

        assertFalse(horario1.tieneConflictoCon(horario2), "No debe haber conflicto en horarios consecutivos");
    }
}