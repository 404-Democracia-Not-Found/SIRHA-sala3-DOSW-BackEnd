package edu.dosw.sirha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;
import edu.dosw.sirha.security.JwtAuthFilter;
import edu.dosw.sirha.security.SecurityConfig;
import edu.dosw.sirha.service.MateriaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Suite de pruebas unitarias para {@link MateriaController}.
 * 
 * <p>Verifica el correcto funcionamiento de los endpoints REST del catálogo de materias,
 * incluyendo creación, listado general, búsqueda filtrada por facultad/semestre,
 * y consulta individual de materias.</p>
 * 
 * <p><strong>Endpoints probados:</strong></p>
 * <ul>
 *   <li>POST /api/materias - Creación de materias en el catálogo</li>
 *   <li>GET /api/materias - Listado completo de materias</li>
 *   <li>GET /api/materias/search - Búsqueda por facultad y/o semestre</li>
 *   <li>GET /api/materias/{id} - Consulta de materia específica</li>
 * </ul>
 * 
 * @see MateriaController
 * @see MateriaService
 */
@WebMvcTest(controllers = MateriaController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class MateriaControllerTest {

    private static final String BASE_URL = "/api/materias";
        private static final String FACULTAD_ID = "fac-1";
        private static final String MATERIA_ID_PRIMARY = "mat-1";
        private static final String MATERIA_ID_SECONDARY = "mat-2";
        private static final String MNEMONICO_PRIMARY = "MAT101";
        private static final String MNEMONICO_SECONDARY = "MAT202";
        private static final String NAME_PRIMARY = "Programación Básica";
        private static final String NAME_SECONDARY = "Álgebra Lineal";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MateriaService materiaService;

    @Test
    void createShouldReturnCreatedMateria() throws Exception {
        MateriaRequest request = MateriaRequest.builder()
                .mnemonico(MNEMONICO_PRIMARY)
                .nombre(NAME_PRIMARY)
                .creditos(4)
                .horasPresenciales(3)
                .horasIndependientes(2)
                .nivel(1)
                .laboratorio(Boolean.FALSE)
                .facultadId(FACULTAD_ID)
                .activo(Boolean.TRUE)
                .build();

        MateriaResponse response = MateriaResponse.builder()
                .id(MATERIA_ID_PRIMARY)
                .mnemonico(MNEMONICO_PRIMARY)
                .nombre(NAME_PRIMARY)
                .creditos(4)
                .laboratorio(false)
                .activo(true)
                .build();

        when(materiaService.create(any(MateriaRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(MATERIA_ID_PRIMARY)))
                .andExpect(jsonPath("$.mnemonico", is(MNEMONICO_PRIMARY)));

        Mockito.verify(materiaService).create(any(MateriaRequest.class));
    }

    @Test
    void searchShouldReturnMatchingMaterias() throws Exception {
        MateriaResponse response = MateriaResponse.builder()
                .id(MATERIA_ID_SECONDARY)
                .mnemonico(MNEMONICO_SECONDARY)
                .nombre(NAME_SECONDARY)
                .activo(true)
                .build();

        when(materiaService.search(" alg "))
                .thenReturn(List.of(response));

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("term", " alg "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(MATERIA_ID_SECONDARY)))
                .andExpect(jsonPath("$[0].mnemonico", is(MNEMONICO_SECONDARY)));
    }

    @Test
    void findAllShouldReturnAllMaterias() throws Exception {
        when(materiaService.findAll()).thenReturn(List.of(
                MateriaResponse.builder().id(MATERIA_ID_PRIMARY).mnemonico(MNEMONICO_PRIMARY).nombre(NAME_PRIMARY).build(),
                MateriaResponse.builder().id(MATERIA_ID_SECONDARY).mnemonico(MNEMONICO_SECONDARY).nombre(NAME_SECONDARY).build()
        ));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(MATERIA_ID_PRIMARY)))
                .andExpect(jsonPath("$[1].id", is(MATERIA_ID_SECONDARY)));
    }

    @Test
    void findByFacultadShouldDelegateToService() throws Exception {
        when(materiaService.findByFacultad(FACULTAD_ID))
                .thenReturn(List.of(MateriaResponse.builder().id("mat-3").facultadId(FACULTAD_ID).build()));

        mockMvc.perform(get(BASE_URL + "/facultad/{id}", FACULTAD_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].facultadId", is(FACULTAD_ID)));
    }

    @Test
    void searchWithoutTermShouldReturnAll() throws Exception {
        when(materiaService.search(null)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/search"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
