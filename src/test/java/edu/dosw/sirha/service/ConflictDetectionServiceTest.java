package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;
import edu.dosw.sirha.mapper.ConflictMapper;
import edu.dosw.sirha.model.Conflict;
import edu.dosw.sirha.repository.ConflictRepository;
import edu.dosw.sirha.service.impl.ConflictDetectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConflictDetectionServiceTest {

    private static final String CONFLICT_ID = "conf-1";
    private static final String ESTUDIANTE_ID = "est-1";
    private static final String UPDATED_ESTUDIANTE_ID = "est-9";
    private static final String SOLICITUD_ID = "sol-1";
    private static final String UPDATED_SOLICITUD_ID = "sol-3";
    private static final String GRUPO_ID = "grp-1";
    private static final String UPDATED_GRUPO_ID = "grp-3";
    private static final String CONFLICT_TYPE = "HORARIO";
    private static final Instant FIXED_NOW = Instant.parse("2024-02-01T12:00:00Z");

    @Mock
    private ConflictRepository conflictRepository;

    private ConflictDetectionServiceImpl conflictService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        conflictService = new ConflictDetectionServiceImpl(conflictRepository, new ConflictMapper(), clock);
    }

    @Test
    void registrarShouldSetDetectionDateAndPersistConflict() {
        ConflictRequest request = buildRequest();
        when(conflictRepository.save(any(Conflict.class))).thenAnswer(invocation -> {
            Conflict conflict = invocation.getArgument(0);
            conflict.setId(CONFLICT_ID);
            return conflict;
        });

        ConflictResponse response = conflictService.registrar(request);

        ArgumentCaptor<Conflict> captor = ArgumentCaptor.forClass(Conflict.class);
        verify(conflictRepository).save(captor.capture());
        Conflict stored = captor.getValue();

        assertThat(stored.getFechaDeteccion()).isEqualTo(FIXED_NOW);
        assertThat(stored.isResuelto()).isFalse();
        assertThat(response.getId()).isEqualTo(CONFLICT_ID);
    }

    @Test
    void actualizarShouldOverwriteConflictFields() {
        Conflict existing = Conflict.builder()
                .id(CONFLICT_ID)
                .tipo("ORIGINAL")
                .descripcion("desc")
        .estudianteId(ESTUDIANTE_ID)
                .build();

        when(conflictRepository.findById(CONFLICT_ID)).thenReturn(Optional.of(existing));
        when(conflictRepository.save(any(Conflict.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ConflictRequest request = ConflictRequest.builder()
        .tipo(CONFLICT_TYPE)
        .descripcion("Conflicto actualizado")
        .estudianteId(UPDATED_ESTUDIANTE_ID)
        .solicitudId(UPDATED_SOLICITUD_ID)
        .grupoId(UPDATED_GRUPO_ID)
        .observaciones("Nueva observación")
        .build();

        ConflictResponse response = conflictService.actualizar(CONFLICT_ID, request);

    assertThat(existing.getTipo()).isEqualTo(CONFLICT_TYPE);
    assertThat(existing.getSolicitudId()).isEqualTo(UPDATED_SOLICITUD_ID);
        assertThat(response.getDescripcion()).isEqualTo("Conflicto actualizado");
    }

    @Test
    void marcarResueltoShouldUpdateResolutionFields() {
        Conflict existing = Conflict.builder().id(CONFLICT_ID).resuelto(false).build();
        when(conflictRepository.findById(CONFLICT_ID)).thenReturn(Optional.of(existing));
        when(conflictRepository.save(any(Conflict.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConflictResponse response = conflictService.marcarResuelto(CONFLICT_ID, true, "resuelto");

        assertThat(existing.isResuelto()).isTrue();
        assertThat(existing.getObservaciones()).isEqualTo("resuelto");
        assertThat(response.isResuelto()).isTrue();
    }

    @Test
    void findersShouldDelegateToRepository() {
        Conflict conflict = Conflict.builder().id(CONFLICT_ID).build();
        when(conflictRepository.findById(CONFLICT_ID)).thenReturn(Optional.of(conflict));
        when(conflictRepository.findAll()).thenReturn(List.of(conflict));
    when(conflictRepository.findByEstudianteId(ESTUDIANTE_ID)).thenReturn(List.of(conflict));
    when(conflictRepository.findBySolicitudId(SOLICITUD_ID)).thenReturn(List.of(conflict));

        assertThat(conflictService.findById(CONFLICT_ID).getId()).isEqualTo(CONFLICT_ID);
        assertThat(conflictService.findAll()).hasSize(1);
    assertThat(conflictService.findByEstudiante(ESTUDIANTE_ID)).hasSize(1);
    assertThat(conflictService.findBySolicitud(SOLICITUD_ID)).hasSize(1);
    }

    @Test
    void deleteShouldRemoveConflict() {
        Conflict conflict = Conflict.builder().id(CONFLICT_ID).build();
        when(conflictRepository.findById(CONFLICT_ID)).thenReturn(Optional.of(conflict));

        conflictService.delete(CONFLICT_ID);

        verify(conflictRepository).delete(conflict);
    }

    private ConflictRequest buildRequest() {
        return ConflictRequest.builder()
        .tipo(CONFLICT_TYPE)
                .descripcion("Cruce de horarios")
        .estudianteId(ESTUDIANTE_ID)
        .solicitudId(SOLICITUD_ID)
        .grupoId(GRUPO_ID)
                .observaciones("Revise disponibilidad")
                .build();
    }
}
