package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.FacultadRequest;
import edu.dosw.sirha.dto.response.FacultadResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.FacultadMapper;
import edu.dosw.sirha.model.Facultad;
import edu.dosw.sirha.model.User;
import edu.dosw.sirha.repository.FacultadRepository;
import edu.dosw.sirha.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link FacultadService}.
 * 
 * <p>Valida la lógica de negocio y las reglas de validación del servicio.</p>
 */
@ExtendWith(MockitoExtension.class)
class FacultadServiceTest {

    @Mock
    private FacultadRepository facultadRepository;

    @Mock
    private UserRepository userRepository;

    // Usar instancia real del mapper (no mockear)
    private FacultadMapper facultadMapper = new FacultadMapper();

    private FacultadService facultadService;

    private Facultad facultad;
    private FacultadRequest request;
    private User decano;

    @BeforeEach
    void setUp() {
        // Crear servicio con mapper real
        facultadService = new FacultadService(facultadRepository, userRepository, facultadMapper);
        // Datos de prueba
        decano = User.builder()
                .id("decano-1")
                .nombre("Dr. Juan Pérez")
                .email("juan.perez@universidad.edu")
                .build();

        facultad = Facultad.builder()
                .id("fac-1")
                .nombre("Facultad de Ingeniería")
                .creditosTotales(160)
                .numeroMaterias(50)
                .activo(true)
                .decanoId("decano-1")
                .build();

        request = FacultadRequest.builder()
                .nombre("Facultad de Ingeniería")
                .creditosTotales(160)
                .numeroMaterias(0)
                .activo(true)
                .decanoId("decano-1")
                .build();
    }

    @Test
    void findAll_shouldReturnAllFacultades() {
        // Given
        List<Facultad> facultades = Arrays.asList(facultad);
        when(facultadRepository.findAll()).thenReturn(facultades);
        when(userRepository.findById("decano-1")).thenReturn(Optional.of(decano));

        // When
        List<FacultadResponse> result = facultadService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Facultad de Ingeniería");
        verify(facultadRepository).findAll();
    }

    @Test
    void findAllActive_shouldReturnOnlyActiveFacultades() {
        // Given
        List<Facultad> facultades = Arrays.asList(facultad);
        when(facultadRepository.findByActivoTrue()).thenReturn(facultades);
        when(userRepository.findById("decano-1")).thenReturn(Optional.of(decano));

        // When
        List<FacultadResponse> result = facultadService.findAllActive();

        // Then
        assertThat(result).hasSize(1);
        verify(facultadRepository).findByActivoTrue();
    }

    @Test
    void findById_shouldReturnFacultad_whenExists() {
        // Given
        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultad));
        when(userRepository.findById("decano-1")).thenReturn(Optional.of(decano));

        // When
        FacultadResponse result = facultadService.findById("fac-1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("fac-1");
        verify(facultadRepository).findById("fac-1");
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(facultadRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> facultadService.findById("invalid-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Facultad no encontrada");
    }

    @Test
    void create_shouldCreateFacultad_whenValid() {
        // Given
        when(facultadRepository.findByNombre(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById("decano-1")).thenReturn(Optional.of(decano));
        when(facultadRepository.save(any(Facultad.class))).thenAnswer(i -> {
            Facultad saved = i.getArgument(0);
            saved.setId("fac-new");
            return saved;
        });

        // When
        FacultadResponse result = facultadService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Facultad de Ingeniería");
        verify(facultadRepository).save(any(Facultad.class));
    }

    @Test
    void create_shouldThrowException_whenDuplicateName() {
        // Given
        when(facultadRepository.findByNombre("Facultad de Ingeniería"))
                .thenReturn(Optional.of(facultad));

        // When & Then
        assertThatThrownBy(() -> facultadService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe una facultad con el nombre");
    }

    @Test
    void create_shouldThrowException_whenDecanoNotFound() {
        // Given
        when(facultadRepository.findByNombre(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById("decano-1")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> facultadService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("El usuario decano no existe");
    }

    @Test
    void update_shouldUpdateFacultad_whenValid() {
        // Given
        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultad));
        when(facultadRepository.findByNombre(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById("decano-1")).thenReturn(Optional.of(decano));
        when(facultadRepository.save(any(Facultad.class))).thenAnswer(i -> i.getArgument(0));

        // When
        FacultadResponse result = facultadService.update("fac-1", request);

        // Then
        assertThat(result).isNotNull();
        verify(facultadRepository).save(any(Facultad.class));
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        // Given
        when(facultadRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> facultadService.update("invalid-id", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Facultad no encontrada");
    }

    @Test
    void update_shouldThrowException_whenDuplicateNameOnDifferentFacultad() {
        // Given
        Facultad otherFacultad = Facultad.builder()
                .id("fac-2")
                .nombre("Facultad de Ingeniería")
                .build();

        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultad));
        when(facultadRepository.findByNombre("Facultad de Ingeniería"))
                .thenReturn(Optional.of(otherFacultad));

        // When & Then
        assertThatThrownBy(() -> facultadService.update("fac-1", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe otra facultad con el nombre");
    }

    @Test
    void delete_shouldDeleteFacultad_whenNoMateriasAssociated() {
        // Given
        Facultad facultadSinMaterias = Facultad.builder()
                .id("fac-1")
                .nombre("Facultad Test")
                .numeroMaterias(0)
                .build();

        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultadSinMaterias));

        // When
        facultadService.delete("fac-1");

        // Then
        verify(facultadRepository).deleteById("fac-1");
    }

    @Test
    void delete_shouldThrowException_whenHasMateriasAssociated() {
        // Given
        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultad));

        // When & Then
        assertThatThrownBy(() -> facultadService.delete("fac-1"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No se puede eliminar la facultad porque tiene")
                .hasMessageContaining("materia(s) asociada(s)");

        verify(facultadRepository, never()).deleteById(anyString());
    }

    @Test
    void toggleActive_shouldChangeActiveStatus() {
        // Given
        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultad));
        when(facultadRepository.save(any(Facultad.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById("decano-1")).thenReturn(Optional.of(decano));

        // When
        FacultadResponse result = facultadService.toggleActive("fac-1", false);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActivo()).isFalse();
        verify(facultadRepository).save(argThat(f -> !f.isActivo()));
    }

    @Test
    void incrementNumeroMaterias_shouldIncrementCounter() {
        // Given
        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultad));
        when(facultadRepository.save(any(Facultad.class))).thenAnswer(i -> i.getArgument(0));

        // When
        facultadService.incrementNumeroMaterias("fac-1");

        // Then
        verify(facultadRepository).save(argThat(f -> f.getNumeroMaterias() == 51));
    }

    @Test
    void decrementNumeroMaterias_shouldDecrementCounter() {
        // Given
        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultad));
        when(facultadRepository.save(any(Facultad.class))).thenAnswer(i -> i.getArgument(0));

        // When
        facultadService.decrementNumeroMaterias("fac-1");

        // Then
        verify(facultadRepository).save(argThat(f -> f.getNumeroMaterias() == 49));
    }

    @Test
    void decrementNumeroMaterias_shouldNotGoNegative() {
        // Given
        Facultad facultadSinMaterias = Facultad.builder()
                .id("fac-1")
                .numeroMaterias(0)
                .build();

        when(facultadRepository.findById("fac-1")).thenReturn(Optional.of(facultadSinMaterias));

        // When
        facultadService.decrementNumeroMaterias("fac-1");

        // Then
        verify(facultadRepository, never()).save(any(Facultad.class));
    }
}
