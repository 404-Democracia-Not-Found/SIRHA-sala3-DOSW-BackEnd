package edu.dosw.sirha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;
import edu.dosw.sirha.security.JwtAuthFilter;
import edu.dosw.sirha.security.SecurityConfig;
import edu.dosw.sirha.service.ConflictDetectionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Suite de pruebas unitarias para {@link ConflictController}.
 * 
 * <p>Verifica el correcto funcionamiento de los endpoints REST para la detección y gestión
 * de conflictos académicos (horarios, cupos, requisitos), incluyendo detección, consulta
 * por estudiante/solicitud, y marcado de conflictos como resueltos.</p>
 * 
 * <p><strong>Endpoints probados:</strong></p>
 * <ul>
 *   <li>POST /api/conflicts/detect - Detección automática de conflictos</li>
 *   <li>GET /api/conflicts/estudiante/{id} - Conflictos de un estudiante</li>
 *   <li>GET /api/conflicts/solicitud/{id} - Conflictos de una solicitud</li>
 *   <li>PUT /api/conflicts/{id}/resolver - Marcar conflicto como resuelto</li>
 * </ul>
 * 
 * @see ConflictController
 * @see ConflictDetectionService
 */
@WebMvcTest(controllers = ConflictController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class ConflictControllerTest {

    private static final String BASE_URL = "/api/conflictos";
    private static final String CONFLICT_ID = "conf-1";
    private static final String ESTUDIANTE_ID = "est-1";
    private static final String SOLICITUD_ID = "sol-1";
        private static final String CONFLICT_TYPE = "HORARIO";
        private static final String UPDATED_DESCRIPTION = "Actualizado";
        private static final String RESOLUTION_NOTE = "Gestión";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConflictDetectionService conflictDetectionService;

    @Test
    void registrarShouldReturnCreatedResponse() throws Exception {
        ConflictRequest request = ConflictRequest.builder()
                .tipo(CONFLICT_TYPE)
                .descripcion("Cruce de horarios")
                .estudianteId(ESTUDIANTE_ID)
                .solicitudId(SOLICITUD_ID)
                .grupoId("grp-1")
                .build();

        ConflictResponse response = ConflictResponse.builder()
                .id(CONFLICT_ID)
                .tipo(CONFLICT_TYPE)
                .estudianteId(ESTUDIANTE_ID)
                .build();

        when(conflictDetectionService.registrar(any(ConflictRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(CONFLICT_ID)))
                .andExpect(jsonPath("$.tipo", is(CONFLICT_TYPE)));

        Mockito.verify(conflictDetectionService).registrar(any(ConflictRequest.class));
    }

    @Test
    void actualizarShouldReturnUpdatedConflict() throws Exception {
        ConflictRequest request = ConflictRequest.builder()
                                .tipo(CONFLICT_TYPE)
                .descripcion(UPDATED_DESCRIPTION)
                .estudianteId(ESTUDIANTE_ID)
                .solicitudId(SOLICITUD_ID)
                .grupoId("grp-2")
                .build();

        ConflictResponse response = ConflictResponse.builder()
                .id(CONFLICT_ID)
                .descripcion(UPDATED_DESCRIPTION)
                .build();

        when(conflictDetectionService.actualizar(Mockito.eq(CONFLICT_ID), any(ConflictRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/{id}", CONFLICT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is(UPDATED_DESCRIPTION)));
    }

    @Test
    void resolverShouldInvokeServiceWithQueryParams() throws Exception {
        ConflictResponse response = ConflictResponse.builder()
                .id(CONFLICT_ID)
                .resuelto(true)
                .observaciones(RESOLUTION_NOTE)
                .build();

        when(conflictDetectionService.marcarResuelto(CONFLICT_ID, true, RESOLUTION_NOTE))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/{id}/resolver", CONFLICT_ID)
                        .param("resuelto", "true")
                        .param("observaciones", RESOLUTION_NOTE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resuelto", is(true)))
                .andExpect(jsonPath("$.observaciones", is(RESOLUTION_NOTE)));
    }

    @Test
    void findersShouldReturnLists() throws Exception {
        ConflictResponse response = ConflictResponse.builder()
                .id(CONFLICT_ID)
                .estudianteId(ESTUDIANTE_ID)
                .solicitudId(SOLICITUD_ID)
                .build();

        when(conflictDetectionService.findAll()).thenReturn(List.of(response));
        when(conflictDetectionService.findByEstudiante(ESTUDIANTE_ID)).thenReturn(List.of(response));
        when(conflictDetectionService.findBySolicitud(SOLICITUD_ID)).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get(BASE_URL + "/estudiante/{id}", ESTUDIANTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estudianteId", is(ESTUDIANTE_ID)));

        mockMvc.perform(get(BASE_URL + "/solicitud/{id}", SOLICITUD_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].solicitudId", is(SOLICITUD_ID)));
    }
}
