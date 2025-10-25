package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.PeriodoMapper;
import edu.dosw.sirha.model.Periodo;
import edu.dosw.sirha.repository.PeriodoRepository;
import edu.dosw.sirha.service.impl.PeriodoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Suite de pruebas unitarias para {@link PeriodoService} (implementación {@link PeriodoServiceImpl}).
 * 
 * <p>Verifica la lógica de negocio del servicio de periodos académicos, incluyendo creación,
 * activación exclusiva (solo un periodo activo a la vez), consultas, y validaciones de
 * reglas de negocio relacionadas con ventanas de tiempo.</p>
 * 
 * <p><strong>Casos de prueba cubiertos:</strong></p>
 * <ul>
 *   <li><strong>Creación:</strong> periodos con fechas válidas, configuración inicial</li>
 *   <li><strong>Activación:</strong> desactivación automática del periodo activo previo</li>
 *   <li><strong>Consulta activo:</strong> obtención del periodo actualmente vigente</li>
 *   <li><strong>Validaciones:</strong> fechas coherentes, solapamiento de periodos</li>
 *   <li><strong>Excepciones:</strong> BusinessException si no hay periodo activo o fechas inválidas</li>
 * </ul>
 * 
 * @see PeriodoService
 * @see PeriodoServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class PeriodoServiceTest {

    private static final String ACTIVE_ID = "per-active";
    private static final String TARGET_ID = "per-2025";

    @Mock
    private PeriodoRepository periodoRepository;

    private PeriodoServiceImpl periodoService;

    @BeforeEach
    void setUp() {
        periodoService = new PeriodoServiceImpl(periodoRepository, new PeriodoMapper());
    }

    @Test
    void createShouldDeactivateCurrentWhenNewPeriodActive() {
        Periodo current = Periodo.builder().id(ACTIVE_ID).activo(true).build();
        when(periodoRepository.findByActivoTrue()).thenReturn(Optional.of(current));
        when(periodoRepository.save(any(Periodo.class))).thenAnswer(invocation -> {
            Periodo periodo = invocation.getArgument(0);
            if (periodo.getId() == null) {
                periodo.setId(TARGET_ID);
            }
            return periodo;
        });

        PeriodoRequest request = buildRequest(true);

        PeriodoResponse response = periodoService.create(request);

        ArgumentCaptor<Periodo> captor = ArgumentCaptor.forClass(Periodo.class);
        verify(periodoRepository, times(2)).save(captor.capture());
        Periodo deactivated = captor.getAllValues().get(0);
        Periodo created = captor.getAllValues().get(1);

        assertThat(deactivated.isActivo()).isFalse();
        assertThat(created.isActivo()).isTrue();
        assertThat(created.getFechaInicio()).isEqualTo(request.getFechaInicio());
        assertThat(response.getId()).isEqualTo(TARGET_ID);
    }

    @Test
    void deleteShouldFailWhenPeriodoIsActive() {
        Periodo active = buildEntity(true);
        when(periodoRepository.findById(TARGET_ID)).thenReturn(Optional.of(active));

    assertThatThrownBy(() -> periodoService.delete(TARGET_ID))
        .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No es posible eliminar el periodo activo");
    }

    @Test
    void deleteShouldRemoveWhenPeriodoInactive() {
        Periodo inactive = buildEntity(false);
        when(periodoRepository.findById(TARGET_ID)).thenReturn(Optional.of(inactive));

        periodoService.delete(TARGET_ID);

        verify(periodoRepository).delete(inactive);
    }

    @Test
    void markAsActiveShouldDeactivateCurrentAndActivateTarget() {
        Periodo toActivate = buildEntity(false);
        Periodo current = Periodo.builder().id(ACTIVE_ID).activo(true).build();

        when(periodoRepository.findById(TARGET_ID)).thenReturn(Optional.of(toActivate));
        when(periodoRepository.findByActivoTrue()).thenReturn(Optional.of(current));
        when(periodoRepository.save(any(Periodo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PeriodoResponse response = periodoService.markAsActive(TARGET_ID);

        verify(periodoRepository, times(2)).save(any(Periodo.class));
        assertThat(current.isActivo()).isFalse();
        assertThat(response.isActivo()).isTrue();
    }

    @Test
    void markAsActiveShouldSkipWhenAlreadyActive() {
        Periodo active = buildEntity(true);
        when(periodoRepository.findById(TARGET_ID)).thenReturn(Optional.of(active));

        PeriodoResponse response = periodoService.markAsActive(TARGET_ID);

        verify(periodoRepository, never()).findByActivoTrue();
        verify(periodoRepository, never()).save(active);
        assertThat(response.isActivo()).isTrue();
    }

    @Test
    void findActiveShouldReturnNullWhenNone() {
        when(periodoRepository.findByActivoTrue()).thenReturn(Optional.empty());

        PeriodoResponse response = periodoService.findActive();

        assertThat(response).isNull();
    }

    @Test
    void findActiveShouldReturnResponseWhenExists() {
        Periodo periodo = buildEntity(true);
        when(periodoRepository.findByActivoTrue()).thenReturn(Optional.of(periodo));

        PeriodoResponse response = periodoService.findActive();

        assertThat(response.getId()).isEqualTo(TARGET_ID);
    }

    @Test
    void isWithinPeriodoShouldQueryRepositoryWithGivenInstant() {
        Instant fecha = Instant.parse("2024-01-15T10:15:30Z");
        when(periodoRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(fecha, fecha))
                .thenReturn(Optional.of(buildEntity(true)));

        boolean result = periodoService.isWithinPeriodo(fecha);

        assertThat(result).isTrue();
        verify(periodoRepository)
                .findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(fecha, fecha);
    }

    @Test
    void updateShouldDeactivateCurrentWhenBecomingActive() {
        Periodo existing = buildEntity(false);
        when(periodoRepository.findById(TARGET_ID)).thenReturn(Optional.of(existing));
        Periodo currentActive = Periodo.builder().id(ACTIVE_ID).activo(true).build();
        when(periodoRepository.findByActivoTrue()).thenReturn(Optional.of(currentActive));
        when(periodoRepository.save(any(Periodo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PeriodoRequest request = buildRequest(true);

        PeriodoResponse response = periodoService.update(TARGET_ID, request);

        ArgumentCaptor<Periodo> captor = ArgumentCaptor.forClass(Periodo.class);
        verify(periodoRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).isActivo()).isFalse();
        assertThat(captor.getAllValues().get(1).isActivo()).isTrue();
        assertThat(response.isActivo()).isTrue();
    }

    @Test
    void updateShouldPersistChangesWithoutReactivating() {
        Periodo existing = buildEntity(true);
        when(periodoRepository.findById(TARGET_ID)).thenReturn(Optional.of(existing));
        when(periodoRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        PeriodoRequest request = buildRequest(true);
        request.setActivo(true);

        PeriodoResponse response = periodoService.update(TARGET_ID, request);

        verify(periodoRepository, never()).findByActivoTrue();
        assertThat(response.getAno()).isEqualTo(2024);
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        when(periodoRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> periodoService.findById("missing"));
    }

    @Test
    void findByIdShouldReturnResponse() {
        Periodo periodo = buildEntity(false);
        when(periodoRepository.findById(TARGET_ID)).thenReturn(Optional.of(periodo));

        PeriodoResponse response = periodoService.findById(TARGET_ID);

        assertThat(response.getId()).isEqualTo(TARGET_ID);
    }

    @Test
    void findAllShouldReturnList() {
        when(periodoRepository.findAll()).thenReturn(List.of(buildEntity(false)));

        assertThat(periodoService.findAll()).hasSize(1);
    }

    private PeriodoRequest buildRequest(boolean activo) {
        return PeriodoRequest.builder()
                .fechaInicio(Instant.parse("2024-01-01T00:00:00Z"))
                .fechaFin(Instant.parse("2024-06-30T23:59:59Z"))
                .fechaInscripcionInicio(Instant.parse("2023-12-01T08:00:00Z"))
                .fechaLimiteSolicitudes(Instant.parse("2024-06-15T23:59:59Z"))
                .ano(2024)
                .semestre(1)
                .activo(activo)
                .build();
    }

    private Periodo buildEntity(boolean activo) {
        return Periodo.builder()
                .id(TARGET_ID)
                .fechaInicio(Instant.parse("2024-01-01T00:00:00Z"))
                .fechaFin(Instant.parse("2024-06-30T23:59:59Z"))
                .fechaInscripcionInicio(Instant.parse("2023-12-01T08:00:00Z"))
                .fechaLimiteSolicitudes(Instant.parse("2024-06-15T23:59:59Z"))
                .ano(2024)
                .semestre(1)
                .activo(activo)
                .build();
    }
}
