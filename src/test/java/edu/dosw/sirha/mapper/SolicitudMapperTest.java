package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudMapperTest {

    private SolicitudMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SolicitudMapper();
    }

    @Test
    void toNewEntity_shouldPopulateFieldsAndDefaultEstado() {
        SolicitudRequest request = TestDataFactory.buildSolicitudRequest();

        Solicitud solicitud = mapper.toNewEntity(request);

        assertThat(solicitud).isNotNull();
        assertThat(solicitud.getTipo()).isEqualTo(request.getTipo());
        assertThat(solicitud.getEstudianteId()).isEqualTo(request.getEstudianteId());
        assertThat(solicitud.getEstado()).isEqualTo(SolicitudEstado.PENDIENTE);
        assertThat(solicitud.getDescripcion()).isEqualTo(request.getDescripcion());
        assertThat(solicitud.getObservaciones()).isEqualTo(request.getObservaciones());
    }

    @Test
    void updateEntity_shouldOverrideMutableFields() {
        SolicitudRequest request = TestDataFactory.buildSolicitudRequest();
        Solicitud solicitud = mapper.toNewEntity(request);
        solicitud.setDescripcion("Anterior");
        solicitud.setPrioridad(1);

        SolicitudRequest updated = SolicitudRequest.builder()
                .tipo(request.getTipo())
                .estudianteId(request.getEstudianteId())
                .descripcion("Nuevo")
                .observaciones("Obs nueva")
                .inscripcionOrigenId("ins-2")
                .grupoDestinoId("grp-5")
                .materiaDestinoId("mat-8")
                .periodoId("per-2025")
                .prioridad(7)
                .build();

        mapper.updateEntity(solicitud, updated);

        assertThat(solicitud.getDescripcion()).isEqualTo("Nuevo");
        assertThat(solicitud.getObservaciones()).isEqualTo("Obs nueva");
        assertThat(solicitud.getPrioridad()).isEqualTo(7);
        assertThat(solicitud.getInscripcionOrigenId()).isEqualTo("ins-2");
    }

    @Test
    void toResponse_shouldMapAllFields() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        SolicitudResponse response = mapper.toResponse(solicitud);

        assertThat(response.getId()).isEqualTo(solicitud.getId());
        assertThat(response.getCodigoSolicitud()).isEqualTo(solicitud.getCodigoSolicitud());
        assertThat(response.getEstado()).isEqualTo(solicitud.getEstado());
        assertThat(response.getHistorial()).hasSize(1);
        assertThat(response.getFechaSolicitud()).isEqualTo(Instant.parse("2024-01-10T12:00:00Z"));
        assertThat(response.getHistorial()).isNotSameAs(solicitud.getHistorial());
    }

    @Test
    void mapperShouldHandleNullInputsGracefully() {
        assertThat(mapper.toNewEntity(null)).isNull();

        Solicitud solicitud = TestDataFactory.buildSolicitud();
        mapper.updateEntity(null, TestDataFactory.buildSolicitudRequest());
        mapper.updateEntity(solicitud, null);

        assertThat(mapper.toResponse(null)).isNull();
    }
}