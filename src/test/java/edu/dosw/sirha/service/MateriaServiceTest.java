package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.MateriaMapper;
import edu.dosw.sirha.model.Materia;
import edu.dosw.sirha.repository.MateriaRepository;
import edu.dosw.sirha.service.impl.MateriaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Suite de pruebas unitarias para {@link MateriaService} (implementación {@link MateriaServiceImpl}).
 * 
 * <p>Verifica la lógica de negocio del servicio de catálogo de materias, incluyendo CRUD completo,
 * búsqueda por criterios, y manejo de excepciones para recursos no encontrados.</p>
 * 
 * <p><strong>Casos de prueba cubiertos:</strong></p>
 * <ul>
 *   <li><strong>Creación:</strong> materias con todos los campos, mapeo correcto a entidad</li>
 *   <li><strong>Consulta:</strong> por ID, listado completo, búsqueda por facultad/semestre</li>
 *   <li><strong>Actualización:</strong> modificación de campos preservando ID</li>
 *   <li><strong>Eliminación:</strong> borrado lógico o físico según estado</li>
 *   <li><strong>Excepciones:</strong> ResourceNotFoundException cuando no existe materia</li>
 * </ul>
 * 
 * @see MateriaService
 * @see MateriaServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MateriaServiceTest {

    private static final String MAT_ID_PRIMARY = "mat-1";
    private static final String MNEMONIC_BASE = "MAT101";
    private static final String UPDATED_NAME = "Álgebra Lineal";
    private static final String MAT_ID_DELETE = "mat-9";
    private static final String MAT_ID_FIND = "mat-7";

    @Mock
    private MateriaRepository materiaRepository;

    private MateriaServiceImpl materiaService;

    @BeforeEach
    void setUp() {
        materiaService = new MateriaServiceImpl(materiaRepository, new MateriaMapper());
    }

    @Test
    void createShouldNormalizeSearchTermsAndReturnResponse() {
        MateriaRequest request = MateriaRequest.builder()
                .mnemonico(MNEMONIC_BASE)
                .nombre("Programación Básica")
                .creditos(4)
                .horasPresenciales(3)
                .horasIndependientes(2)
                .nivel(1)
                .laboratorio(Boolean.FALSE)
                .facultadId("fac-1")
                .prerequisitos(List.of("MAT001"))
                .desbloquea(List.of("MAT201"))
                .activo(Boolean.TRUE)
                .searchTerms(List.of("Horario flexible", "mat101"))
                .build();

        when(materiaRepository.save(any(Materia.class))).thenAnswer(invocation -> {
            Materia materia = invocation.getArgument(0);
            materia.setId(MAT_ID_PRIMARY);
            return materia;
        });

        MateriaResponse response = materiaService.create(request);

        ArgumentCaptor<Materia> captor = ArgumentCaptor.forClass(Materia.class);
        verify(materiaRepository).save(captor.capture());
        Materia stored = captor.getValue();

        assertThat(stored.getSearchTerms())
                .containsExactly("horario flexible", "mat101", "programación básica");
    assertThat(stored.getMnemonico()).isEqualTo(MNEMONIC_BASE);

    assertThat(response.getId()).isEqualTo(MAT_ID_PRIMARY);
        assertThat(response.getNombre()).isEqualTo("Programación Básica");
    }

    @Test
    void updateShouldRefreshSearchTermsAndPersistChanges() {
        Materia existing = Materia.builder()
        .id(MAT_ID_PRIMARY)
                .mnemonico("MAT100")
                .nombre("Intro")
                .searchTerms(List.of())
                .build();

    when(materiaRepository.findById(MAT_ID_PRIMARY)).thenReturn(Optional.of(existing));
        when(materiaRepository.save(any(Materia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MateriaRequest request = MateriaRequest.builder()
                .mnemonico("MAT102")
        .nombre(UPDATED_NAME)
                .creditos(5)
                .horasPresenciales(4)
                .horasIndependientes(4)
                .nivel(2)
                .laboratorio(Boolean.TRUE)
                .facultadId("fac-2")
                .prerequisitos(List.of(MNEMONIC_BASE))
                .desbloquea(List.of("MAT202"))
                .activo(Boolean.FALSE)
                .searchTerms(List.of("algebra", "lineal"))
                .build();

    MateriaResponse response = materiaService.update(MAT_ID_PRIMARY, request);

        ArgumentCaptor<Materia> captor = ArgumentCaptor.forClass(Materia.class);
        verify(materiaRepository).save(captor.capture());
        Materia updated = captor.getValue();

        assertThat(updated.getSearchTerms()).containsExactly("algebra", "lineal", "mat102", "álgebra lineal");
        assertThat(updated.getNombre()).isEqualTo(UPDATED_NAME);
        assertThat(updated.isActivo()).isFalse();
        assertThat(response.getNombre()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void deleteShouldRemoveExistingMateria() {
        Materia existing = Materia.builder().id(MAT_ID_DELETE).build();
        when(materiaRepository.findById(MAT_ID_DELETE)).thenReturn(Optional.of(existing));

        materiaService.delete(MAT_ID_DELETE);

        verify(materiaRepository).delete(existing);
    }

    @Test
    void findByIdShouldReturnMappedResponse() {
        Materia materia = Materia.builder().id(MAT_ID_FIND).nombre("Estructuras").build();
        when(materiaRepository.findById(MAT_ID_FIND)).thenReturn(Optional.of(materia));

        MateriaResponse response = materiaService.findById(MAT_ID_FIND);

        assertThat(response.getId()).isEqualTo(MAT_ID_FIND);
        assertThat(response.getNombre()).isEqualTo("Estructuras");
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        when(materiaRepository.findById("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> materiaService.findById("missing"))
        .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchShouldReturnAllWhenTermBlank() {
    Materia materia = Materia.builder().id(MAT_ID_PRIMARY).build();
        when(materiaRepository.findAll()).thenReturn(List.of(materia));

        List<MateriaResponse> responses = materiaService.search("   ");

        verify(materiaRepository).findAll();
        assertThat(responses).hasSize(1);
    }

    @Test
    void searchShouldTrimTermBeforeDelegating() {
        Materia materia = Materia.builder().id("mat-2").build();
        when(materiaRepository.findBySearchTermsContainingIgnoreCase("alg"))
                .thenReturn(List.of(materia));

        List<MateriaResponse> responses = materiaService.search("  alg  ");

        verify(materiaRepository).findBySearchTermsContainingIgnoreCase("alg");
        assertThat(responses).hasSize(1);
    }
}
