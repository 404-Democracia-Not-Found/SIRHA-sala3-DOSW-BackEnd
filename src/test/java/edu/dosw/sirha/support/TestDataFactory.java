package edu.dosw.sirha.support;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.Grupo;
import edu.dosw.sirha.model.Horario;
import edu.dosw.sirha.model.Periodo;
import edu.dosw.sirha.model.PeriodoConfiguracion;
import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.SolicitudHistorialEntry;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.model.enums.SolicitudTipo;

public final class TestDataFactory {

    private TestDataFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static SolicitudRequest buildSolicitudRequest() {
        return SolicitudRequest.builder()
                .tipo(SolicitudTipo.CAMBIO_GRUPO)
                .estudianteId("est-123")
                .descripcion("Cambio de grupo por conflicto de horario")
                .observaciones("Observación inicial")
                .inscripcionOrigenId("ins-1")
                .grupoDestinoId("grp-2")
                .materiaDestinoId("mat-5")
                .periodoId("per-2024")
                .prioridad(3)
                .build();
    }

    public static Solicitud buildSolicitud() {
        return Solicitud.builder()
                .id("sol-1")
                .codigoSolicitud("SOL-20240101000000-ABCD1234")
                .estado(SolicitudEstado.PENDIENTE)
                .tipo(SolicitudTipo.CAMBIO_GRUPO)
                .descripcion("Cambio de grupo por conflicto de horario")
                .observaciones("Observación inicial")
                .estudianteId("est-123")
                .inscripcionOrigenId("ins-1")
                .grupoDestinoId("grp-2")
                .materiaDestinoId("mat-5")
                .periodoId("per-2024")
                .prioridad(3)
                .fechaSolicitud(Instant.parse("2024-01-10T12:00:00Z"))
                .fechaActualizacion(Instant.parse("2024-01-10T12:00:00Z"))
                .fechaLimiteRespuesta(Instant.parse("2024-01-15T12:00:00Z"))
                .historial(List.of(SolicitudHistorialEntry.builder()
                        .fecha(Instant.parse("2024-01-10T12:00:00Z"))
                        .accion("CREADA")
                        .usuarioId("usr-1")
                        .comentario("Observación inicial")
                        .build()))
                .build();
    }

    public static SolicitudResponse buildSolicitudResponse() {
        return SolicitudResponse.builder()
                .id("sol-1")
                .codigoSolicitud("SOL-20240101000000-ABCD1234")
                .estado(SolicitudEstado.PENDIENTE)
                .tipo(SolicitudTipo.CAMBIO_GRUPO)
                .descripcion("Cambio de grupo por conflicto de horario")
                .observaciones("Observación inicial")
                .estudianteId("est-123")
                .inscripcionOrigenId("ins-1")
                .grupoDestinoId("grp-2")
                .materiaDestinoId("mat-5")
                .periodoId("per-2024")
                .prioridad(3)
                .fechaSolicitud(Instant.parse("2024-01-10T12:00:00Z"))
                .fechaActualizacion(Instant.parse("2024-01-10T12:00:00Z"))
                .fechaLimiteRespuesta(Instant.parse("2024-01-15T12:00:00Z"))
                .historial(List.of(SolicitudHistorialEntry.builder()
                        .fecha(Instant.parse("2024-01-10T12:00:00Z"))
                        .accion("CREADA")
                        .usuarioId("usr-1")
                        .comentario("Observación inicial")
                        .build()))
                .build();
    }

    public static Periodo buildPeriodo() {
        return Periodo.builder()
                .id("per-2024")
                .fechaInicio(Instant.parse("2024-01-01T00:00:00Z"))
                .fechaFin(Instant.parse("2024-06-30T23:59:59Z"))
                .fechaInscripcionInicio(Instant.parse("2023-12-01T00:00:00Z"))
                .fechaLimiteSolicitudes(Instant.parse("2024-02-28T23:59:59Z"))
                .ano(2024)
                .semestre(1)
                .activo(true)
                .configuracion(PeriodoConfiguracion.builder()
                        .permitirCambios(true)
                        .diasMaxRespuesta(5)
                        .build())
                .build();
    }

    public static Grupo buildGrupo() {
        List<Horario> horarios = new ArrayList<>();
        horarios.add(Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .tipoClase("TEORIA")
                .salon("A101")
                .build());
        horarios.add(Horario.builder()
                .dia(DayOfWeek.WEDNESDAY)
                .horaInicio(LocalTime.of(14, 0))
                .horaFin(LocalTime.of(16, 0))
                .tipoClase("LABORATORIO")
                .salon("LAB1")
                .build());

        return Grupo.builder()
                .id("grp-1")
                .codigo("A01")
                .materiaId("mat-123")
                .periodoId("per-2024")
                .profesorId("prof-123")
                .cupoMax(30)
                .cuposActuales(25)
                .salon("A101")
                .fechaInicio(Instant.parse("2024-01-15T00:00:00Z"))
                .fechaFin(Instant.parse("2024-06-15T00:00:00Z"))
                .horarios(horarios)
                .listaEspera(new ArrayList<>())
                .activo(true)
                .build();
    }

    public static Horario buildHorario() {
        return Horario.builder()
                .dia(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(10, 0))
                .tipoClase("TEORIA")
                .salon("A101")
                .build();
    }
}