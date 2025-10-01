package edu.dosw.sirha.support;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.SolicitudHistorialEntry;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.model.enums.SolicitudTipo;

import java.time.Instant;
import java.util.List;

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
}