package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.SolicitudMapper;
import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.SolicitudHistorialEntry;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.repository.SolicitudRepository;
import edu.dosw.sirha.service.impl.SolicitudServiceImpl;
import edu.dosw.sirha.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitudServiceTest {

    private SolicitudRepository solicitudRepository;
    private SolicitudMapper solicitudMapper;
    private Clock fixedClock;
    private SolicitudServiceImpl solicitudService;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudRepository.class);
        solicitudMapper = new SolicitudMapper();
        fixedClock = Clock.fixed(Instant.parse("2024-01-10T12:00:00Z"), ZoneOffset.UTC);
        solicitudService = new SolicitudServiceImpl(solicitudRepository, solicitudMapper, fixedClock);
        ReflectionTestUtils.setField(solicitudService, "diasMaxRespuesta", 5);
    }

    @Test
    void create_ShouldPersistSolicitudWithGeneratedCodigoAndFechas() {
        SolicitudRequest request = TestDataFactory.buildSolicitudRequest();
        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);

        when(solicitudRepository.save(captor.capture())).thenAnswer(invocation -> {
            Solicitud saved = captor.getValue();
            saved.setId("sol-1");
            return saved;
        });

        SolicitudResponse response = solicitudService.create(request);

        Solicitud stored = captor.getValue();
        assertThat(stored.getCodigoSolicitud()).startsWith("SOL-20240110120000-");
        assertThat(stored.getFechaSolicitud()).isEqualTo(Instant.parse("2024-01-10T12:00:00Z"));
        assertThat(stored.getFechaLimiteRespuesta()).isEqualTo(Instant.parse("2024-01-15T12:00:00Z"));
        assertThat(stored.getHistorial()).hasSize(1);
        assertThat(response.getId()).isEqualTo("sol-1");
        assertThat(response.getEstado()).isEqualTo(SolicitudEstado.PENDIENTE);

        verify(solicitudRepository).save(stored);
    }

    @Test
    void update_ShouldMergeChangesAndCreateHistorialEntryWhenMissing() {
        Solicitud existing = TestDataFactory.buildSolicitud();
        existing.setHistorial(null);
        when(solicitudRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(solicitudRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

    SolicitudRequest request = SolicitudRequest.builder()
        .tipo(TestDataFactory.buildSolicitudRequest().getTipo())
        .estudianteId("est-123")
        .descripcion("Actualizada")
        .observaciones("Nueva observación")
        .inscripcionOrigenId("ins-1")
        .grupoDestinoId("grp-9")
        .materiaDestinoId("mat-9")
        .periodoId("per-2024")
        .prioridad(5)
        .build();

        SolicitudResponse response = solicitudService.update(existing.getId(), request);

        assertThat(existing.getDescripcion()).isEqualTo("Actualizada");
        assertThat(existing.getHistorial()).hasSize(1);
        assertThat(existing.getHistorial().get(0).getAccion()).isEqualTo("ACTUALIZADA");
        assertThat(response.getPrioridad()).isEqualTo(5);
        verify(solicitudRepository).save(existing);
    }

    @Test
    void changeEstado_ShouldPersistWhenStateDifferent() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        solicitud.setEstado(SolicitudEstado.PENDIENTE);
    solicitud.setHistorial(new ArrayList<>(solicitud.getHistorial()));
        when(solicitudRepository.findById(solicitud.getId())).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(solicitud)).thenAnswer(invocation -> invocation.getArgument(0));

        SolicitudResponse response = solicitudService.changeEstado(solicitud.getId(), SolicitudEstado.APROBADA, "Listo");

        assertThat(solicitud.getEstado()).isEqualTo(SolicitudEstado.APROBADA);
        assertThat(solicitud.getHistorial()).extracting(SolicitudHistorialEntry::getAccion)
                .contains("ESTADO:APROBADA");
        assertThat(response.getEstado()).isEqualTo(SolicitudEstado.APROBADA);
    }

    @Test
    void changeEstado_ShouldThrowWhenStateEqual() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        when(solicitudRepository.findById(solicitud.getId())).thenReturn(Optional.of(solicitud));

    BusinessException exception = assertThrows(BusinessException.class,
        () -> solicitudService.changeEstado(solicitud.getId(), solicitud.getEstado(), null));

    assertThat(exception.getMessage()).contains("La solicitud ya se encuentra en estado");
    }

    @Test
    void findById_ShouldReturnResponse() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        when(solicitudRepository.findById(solicitud.getId())).thenReturn(Optional.of(solicitud));

        SolicitudResponse response = solicitudService.findById(solicitud.getId());

        assertThat(response.getId()).isEqualTo(solicitud.getId());
    }

    @Test
    void findById_ShouldThrowWhenMissing() {
        when(solicitudRepository.findById("missing")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> solicitudService.findById("missing"));
    }

    @Test
    void delete_ShouldRemoveSolicitud() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        when(solicitudRepository.findById(solicitud.getId())).thenReturn(Optional.of(solicitud));

        solicitudService.delete(solicitud.getId());

        verify(solicitudRepository).delete(solicitud);
    }

    @Test
    void findAll_ShouldReturnResponses() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        when(solicitudRepository.findAll()).thenReturn(List.of(solicitud));

        List<SolicitudResponse> responses = solicitudService.findAll();

        assertThat(responses).extracting(SolicitudResponse::getId).containsExactly(solicitud.getId());
    }

    @Test
    void findByEstudiante_ShouldDelegateToRepository() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        when(solicitudRepository.findByEstudianteIdOrderByFechaSolicitudDesc("est-123"))
                .thenReturn(List.of(solicitud));

        List<SolicitudResponse> responses = solicitudService.findByEstudiante("est-123");

        verify(solicitudRepository).findByEstudianteIdOrderByFechaSolicitudDesc("est-123");
        assertThat(responses).hasSize(1);
    }

    @Test
    void findByEstados_ShouldRequestAllWhenInputNull() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        when(solicitudRepository.findByEstadoInOrderByPrioridadAsc(List.of(SolicitudEstado.values())))
                .thenReturn(List.of(solicitud));

        List<SolicitudResponse> responses = solicitudService.findByEstados(null);

        verify(solicitudRepository).findByEstadoInOrderByPrioridadAsc(List.of(SolicitudEstado.values()));
        assertThat(responses).hasSize(1);
    }

    @Test
    void findByEstados_ShouldUseProvidedList() {
        Solicitud solicitud = TestDataFactory.buildSolicitud();
        List<SolicitudEstado> estados = List.of(SolicitudEstado.APROBADA, SolicitudEstado.RECHAZADA);
        when(solicitudRepository.findByEstadoInOrderByPrioridadAsc(estados)).thenReturn(List.of(solicitud));

        List<SolicitudResponse> responses = solicitudService.findByEstados(estados);

        verify(solicitudRepository).findByEstadoInOrderByPrioridadAsc(estados);
        assertThat(responses).hasSize(1);
    }

    @Test
    void countByEstado_ShouldDelegate() {
        when(solicitudRepository.countByEstado(SolicitudEstado.PENDIENTE)).thenReturn(7L);

        long count = solicitudService.countByEstado(SolicitudEstado.PENDIENTE);

        assertThat(count).isEqualTo(7L);
    }

    @Test
    void findByPeriodoAndRango_ShouldUseDefaultWindowWhenNull() {
        when(solicitudRepository.findByPeriodoIdAndFechaSolicitudBetween("per-1",
                Instant.parse("2023-12-11T12:00:00Z"),
                Instant.parse("2024-01-10T12:00:00Z")))
                .thenReturn(List.of(TestDataFactory.buildSolicitud()));

        List<SolicitudResponse> responses = solicitudService.findByPeriodoAndRango("per-1", null, null);

        assertThat(responses).hasSize(1);
    }

    @Test
    void findByPeriodoAndRango_ShouldRespectProvidedRange() {
        Instant inicio = Instant.parse("2024-01-01T00:00:00Z");
        Instant fin = Instant.parse("2024-01-31T00:00:00Z");
        when(solicitudRepository.findByPeriodoIdAndFechaSolicitudBetween("per-1", inicio, fin))
                .thenReturn(List.of(TestDataFactory.buildSolicitud()));

        solicitudService.findByPeriodoAndRango("per-1", inicio, fin);

        verify(solicitudRepository).findByPeriodoIdAndFechaSolicitudBetween("per-1", inicio, fin);
    }
}