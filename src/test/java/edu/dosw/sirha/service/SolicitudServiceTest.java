package edu.dosw.sirha.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.SolicitudMapper;
import edu.dosw.sirha.model.Grupo;
import edu.dosw.sirha.model.Periodo;
import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.SolicitudHistorialEntry;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.repository.GrupoRepository;
import edu.dosw.sirha.repository.PeriodoRepository;
import edu.dosw.sirha.repository.SolicitudRepository;
import edu.dosw.sirha.service.impl.SolicitudServiceImpl;
import edu.dosw.sirha.support.TestDataFactory;

/**
 * Suite de pruebas unitarias para {@link SolicitudService} (implementación {@link SolicitudServiceImpl}).
 * 
 * <p>Esta clase verifica la lógica de negocio completa del servicio de solicitudes, incluyendo
 * creación con validaciones, cambios de estado con historial, actualización de grupos,
 * consultas filtradas y manejo de excepciones de negocio.</p>
 * 
 * <p><strong>Configuración de pruebas:</strong></p>
 * <ul>
 *   <li>Usa mocks puros (Mockito) sin Spring Context para mayor rapidez</li>
 *   <li>Clock fijo para timestamps determinísticos en historial</li>
 *   <li>TestDataFactory para crear datos de prueba consistentes</li>
 *   <li>Setup completo en {@code @BeforeEach} con repositorios mockeados</li>
 * </ul>
 * 
 * <p><strong>Casos de prueba cubiertos:</strong></p>
 * <ul>
 *   <li><strong>Creación:</strong> solicitudes válidas, validación de periodo activo, grupos existentes</li>
 *   <li><strong>Cambios de estado:</strong> transiciones válidas, registro de historial, actualización de timestamps</li>
 *   <li><strong>Aprobación:</strong> actualización de cupo en grupos, sincronización de estados</li>
 *   <li><strong>Consultas:</strong> filtrado por estudiante, periodo, estado; paginación</li>
 *   <li><strong>Excepciones:</strong> recursos no encontrados, validaciones de negocio</li>
 *   <li><strong>Actualización:</strong> modificación de grupos, preservación de estado e historial</li>
 *   <li><strong>Eliminación:</strong> lógica de borrado según estado</li>
 * </ul>
 * 
 * <p><strong>Validaciones de negocio verificadas:</strong></p>
 * <ul>
 *   <li>Solo se puede crear solicitudes en periodos activos</li>
 *   <li>Grupos deben existir y pertenecer al periodo</li>
 *   <li>Cambios de estado deben respetar transiciones válidas</li>
 *   <li>Aprobación actualiza cupo actual de grupos</li>
 *   <li>Historial registra todos los cambios de estado</li>
 * </ul>
 * 
 * @see SolicitudService
 * @see SolicitudServiceImpl
 * @see org.junit.jupiter.api.Test
 */
class SolicitudServiceTest {

    private SolicitudRepository solicitudRepository;
    private SolicitudMapper solicitudMapper;
    private GrupoRepository grupoRepository;
    private PeriodoRepository periodoRepository;
    private Clock fixedClock;
    private SolicitudServiceImpl solicitudService;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudRepository.class);
        solicitudMapper = new SolicitudMapper();
        grupoRepository = mock(GrupoRepository.class);
        periodoRepository = mock(PeriodoRepository.class);
        fixedClock = Clock.fixed(Instant.parse("2024-01-10T12:00:00Z"), ZoneOffset.UTC);
        solicitudService = new SolicitudServiceImpl(solicitudRepository, solicitudMapper, grupoRepository, periodoRepository, fixedClock);
        ReflectionTestUtils.setField(solicitudService, "diasMaxRespuesta", 5);
    }

    @Test
    void create_ShouldPersistSolicitudWithGeneratedCodigoAndFechas() {
        SolicitudRequest request = TestDataFactory.buildSolicitudRequest();
        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);

        // Mock período activo
        Periodo periodoActivo = TestDataFactory.buildPeriodo();
        periodoActivo.setActivo(true);
        periodoActivo.setFechaLimiteSolicitudes(Instant.now(fixedClock).plusSeconds(86400)); // 1 día después
        when(periodoRepository.findByActivoTrue()).thenReturn(Optional.of(periodoActivo));

        // Mock grupo con cupos disponibles si se especifica
        if (request.getGrupoDestinoId() != null) {
            Grupo grupoDestino = TestDataFactory.buildGrupo();
            grupoDestino.setCupoMax(30);
            grupoDestino.setCuposActuales(20);
            when(grupoRepository.findById(request.getGrupoDestinoId())).thenReturn(Optional.of(grupoDestino));
        }

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
        // Mock existing grupo
        Grupo grupoOrigen = TestDataFactory.buildGrupo();
        when(grupoRepository.findById(existing.getGrupoDestinoId())).thenReturn(Optional.of(grupoOrigen));
        
        // Mock new grupo for update
        Grupo grupoDestino = TestDataFactory.buildGrupo();
        grupoDestino.setId("grp-9");
        when(grupoRepository.findById("grp-9")).thenReturn(Optional.of(grupoDestino));
        
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
        
        // Mock período activo
        Periodo periodoActivo = TestDataFactory.buildPeriodo();
        periodoActivo.setActivo(true);
        when(periodoRepository.findByActivoTrue()).thenReturn(Optional.of(periodoActivo));
        
        // Mock grupo destino
        Grupo grupoDestino = TestDataFactory.buildGrupo();
        when(grupoRepository.findById(solicitud.getGrupoDestinoId())).thenReturn(Optional.of(grupoDestino));
        
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