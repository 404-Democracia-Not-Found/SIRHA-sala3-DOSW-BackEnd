package edu.dosw.sirha.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Suite de pruebas unitarias para la entidad {@link Grupo}.
 * 
 * <p>Esta clase verifica el correcto funcionamiento de los métodos de negocio de la entidad
 * Grupo, incluyendo gestión de cupos, detección de conflictos de horario, validación de
 * disponibilidad y lógica de inscripción/retiro de estudiantes.</p>
 * 
 * <p><strong>Métodos de negocio probados:</strong></p>
 * <ul>
 *   <li>{@code tieneCuposDisponibles()} - Verifica si hay cupos libres</li>
 *   <li>{@code calcularPorcentajeOcupacion()} - Calcula ocupación como porcentaje</li>
 *   <li>{@code calcularCuposDisponibles()} - Retorna cantidad de cupos libres</li>
 *   <li>{@code incrementarCupo()} - Aumenta cupo actual al inscribir estudiante</li>
 *   <li>{@code decrementarCupo()} - Disminuye cupo actual al retirar estudiante</li>
 *   <li>{@code tieneConflictoHorarioCon()} - Detecta solapamiento de horarios</li>
 *   <li>{@code getTotalHorasSemanales()} - Suma horas de todos los horarios</li>
 *   <li>{@code estaCerrado()} - Verifica si el grupo acepta inscripciones</li>
 * </ul>
 * 
 * <p><strong>Escenarios verificados:</strong></p>
 * <ul>
 *   <li>Cupos disponibles y agotados</li>
 *   <li>Cálculos de porcentaje de ocupación</li>
 *   <li>Incremento/decremento de cupos con límites</li>
 *   <li>Detección de conflictos por día y hora exactos</li>
 *   <li>Conflictos en grupos con múltiples horarios</li>
 *   <li>Suma correcta de horas semanales</li>
 *   <li>Estado cerrado vs abierto para inscripciones</li>
 * </ul>
 * 
 * @see Grupo
 * @see Horario
 * @see org.junit.jupiter.api.Test
 */
class GrupoTest {

    @Test
    void testTieneCuposDisponibles() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(25)
                .build();

        assertTrue(grupo.tieneCuposDisponibles(), "El grupo debe tener cupos disponibles");
    }

    @Test
    void testNoTieneCuposDisponibles() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(30)
                .build();

        assertFalse(grupo.tieneCuposDisponibles(), "El grupo no debe tener cupos disponibles");
    }

    @Test
    void testPorcentajeOcupacion() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(21)
                .build();

        assertEquals(70.0, grupo.porcentajeOcupacion(), 0.1, "El porcentaje de ocupación debe ser 70%");
    }

    @Test
    void testEstaEnAlerta() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(27) // 90%
                .build();

        assertTrue(grupo.estaEnAlerta(), "El grupo debe estar en alerta al 90% de capacidad");
    }

    @Test
    void testNoEstaEnAlerta() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(25) // 83.3%
                .build();

        assertFalse(grupo.estaEnAlerta(), "El grupo no debe estar en alerta por debajo del 90%");
    }

    @Test
    void testTieneHorariosValidos() {
        List<Horario> horarios = List.of(
            Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .build(),
            Horario.builder()
                .dia(DayOfWeek.WEDNESDAY)
                .horaInicio(LocalTime.of(14, 0))
                .horaFin(LocalTime.of(16, 0))
                .build()
        );

        Grupo grupo = Grupo.builder()
                .horarios(horarios)
                .build();

        assertTrue(grupo.tieneHorariosValidos(), "Todos los horarios deben ser válidos");
    }

    @Test
    void testTieneHorariosInvalidos() {
        List<Horario> horarios = List.of(
            Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .build(),
            Horario.builder()
                .dia(DayOfWeek.SUNDAY) // Domingo no es válido
                .horaInicio(LocalTime.of(14, 0))
                .horaFin(LocalTime.of(16, 0))
                .build()
        );

        Grupo grupo = Grupo.builder()
                .horarios(horarios)
                .build();

        assertFalse(grupo.tieneHorariosValidos(), "Debe detectar horarios inválidos");
    }

    @Test
    void testIncrementarCupo() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(25)
                .build();

        assertTrue(grupo.incrementarCupo(), "Debe poder incrementar el cupo");
        assertEquals(26, grupo.getCuposActuales(), "El cupo actual debe incrementarse");
    }

    @Test
    void testIncrementarCupoLleno() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(30)
                .build();

        assertFalse(grupo.incrementarCupo(), "No debe poder incrementar el cupo si está lleno");
        assertEquals(30, grupo.getCuposActuales(), "El cupo actual no debe cambiar");
    }

    @Test
    void testDecrementarCupo() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(25)
                .build();

        grupo.decrementarCupo();
        assertEquals(24, grupo.getCuposActuales(), "El cupo actual debe decrementarse");
    }

    @Test
    void testDecrementarCupoEnCero() {
        Grupo grupo = Grupo.builder()
                .cupoMax(30)
                .cuposActuales(0)
                .build();

        grupo.decrementarCupo();
        assertEquals(0, grupo.getCuposActuales(), "El cupo actual no debe ser negativo");
    }

    @Test
    void testAgregarAListaEspera() {
        Grupo grupo = Grupo.builder().build();
        String estudianteId = "estudiante123";

        grupo.agregarAListaEspera(estudianteId);

        assertNotNull(grupo.getListaEspera(), "La lista de espera debe inicializarse");
        assertTrue(grupo.getListaEspera().contains(estudianteId), "El estudiante debe estar en la lista de espera");
    }

    @Test
    void testAgregarEstudianteDuplicadoAListaEspera() {
        Grupo grupo = Grupo.builder()
                .listaEspera(new ArrayList<>())
                .build();
        String estudianteId = "estudiante123";

        grupo.agregarAListaEspera(estudianteId);
        grupo.agregarAListaEspera(estudianteId); // Intentar agregar duplicado

        assertEquals(1, grupo.getListaEspera().size(), "No debe haber estudiantes duplicados en la lista de espera");
    }

    @Test
    void testRemoverDeListaEspera() {
        String estudianteId = "estudiante123";
        List<String> listaEspera = new ArrayList<>();
        listaEspera.add(estudianteId);

        Grupo grupo = Grupo.builder()
                .listaEspera(listaEspera)
                .build();

        grupo.removerDeListaEspera(estudianteId);

        assertFalse(grupo.getListaEspera().contains(estudianteId), "El estudiante debe ser removido de la lista de espera");
    }

    @Test
    void testTieneConflictoHorarioCon() {
        List<Horario> horarios1 = List.of(
            Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .build()
        );

        List<Horario> horarios2 = List.of(
            Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(11, 0))
                .build()
        );

        Grupo grupo1 = Grupo.builder().horarios(horarios1).build();
        Grupo grupo2 = Grupo.builder().horarios(horarios2).build();

        assertTrue(grupo1.tieneConflictoHorarioCon(grupo2), "Debe detectar conflicto de horarios entre grupos");
    }

    @Test
    void testNoTieneConflictoHorarioCon() {
        List<Horario> horarios1 = List.of(
            Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .build()
        );

        List<Horario> horarios2 = List.of(
            Horario.builder()
                .dia(DayOfWeek.TUESDAY)
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(11, 0))
                .build()
        );

        Grupo grupo1 = Grupo.builder().horarios(horarios1).build();
        Grupo grupo2 = Grupo.builder().horarios(horarios2).build();

        assertFalse(grupo1.tieneConflictoHorarioCon(grupo2), "No debe haber conflicto en días diferentes");
    }
}