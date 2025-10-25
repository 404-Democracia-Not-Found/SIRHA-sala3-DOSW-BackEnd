package edu.dosw.sirha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;
import edu.dosw.sirha.security.JwtAuthFilter;
import edu.dosw.sirha.security.SecurityConfig;
import edu.dosw.sirha.service.PeriodoService;
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

import java.time.Instant;
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
 * Suite de pruebas unitarias para {@link PeriodoController}.
 * 
 * <p>Verifica el correcto funcionamiento de los endpoints REST para la gestión de periodos
 * académicos, incluyendo creación, activación, listado y consulta del periodo activo actual.</p>
 * 
 * <p><strong>Endpoints probados:</strong></p>
 * <ul>
 *   <li>POST /api/periodos - Creación de nuevos periodos académicos</li>
 *   <li>GET /api/periodos - Listado de todos los periodos</li>
 *   <li>GET /api/periodos/activo - Consulta del periodo activo actual</li>
 *   <li>POST /api/periodos/{id}/activar - Activación de periodo específico</li>
 * </ul>
 * 
 * @see PeriodoController
 * @see PeriodoService
 */
@WebMvcTest(controllers = PeriodoController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class PeriodoControllerTest {

    private static final String BASE_URL = "/api/periodos";
    private static final Instant FECHA_INICIO = Instant.parse("2024-01-01T00:00:00Z");
    private static final Instant FECHA_FIN = Instant.parse("2024-06-30T23:59:59Z");
        private static final String PERIODO_ID_PRIMARY = "per-1";
        private static final String PERIODO_ID_SECONDARY = "per-2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PeriodoService periodoService;

    @Test
    void createShouldReturnCreatedPeriodo() throws Exception {
        PeriodoRequest request = PeriodoRequest.builder()
                .fechaInicio(FECHA_INICIO)
                .fechaFin(FECHA_FIN)
                .fechaInscripcionInicio(Instant.parse("2023-12-01T08:00:00Z"))
                .fechaLimiteSolicitudes(Instant.parse("2024-06-15T23:59:59Z"))
                .ano(2024)
                .semestre(1)
                .activo(true)
                .build();

        PeriodoResponse response = PeriodoResponse.builder()
                .id(PERIODO_ID_PRIMARY)
                .fechaInicio(FECHA_INICIO)
                .fechaFin(FECHA_FIN)
                .ano(2024)
                .semestre(1)
                .activo(true)
                .build();

        when(periodoService.create(any(PeriodoRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id", is(PERIODO_ID_PRIMARY)))
                .andExpect(jsonPath("$.ano", is(2024)));

        Mockito.verify(periodoService).create(any(PeriodoRequest.class));
    }

    @Test
    void findActiveShouldReturnNoContentWhenServiceReturnsNull() throws Exception {
        when(periodoService.findActive()).thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/activo"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findActiveShouldReturnOkWhenPeriodoExists() throws Exception {
        PeriodoResponse response = PeriodoResponse.builder()
                .id(PERIODO_ID_SECONDARY)
                .fechaInicio(FECHA_INICIO)
                .fechaFin(FECHA_FIN)
                .activo(true)
                .build();

        when(periodoService.findActive()).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/activo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(PERIODO_ID_SECONDARY)));
    }

    @Test
    void findAllShouldReturnLista() throws Exception {
        when(periodoService.findAll()).thenReturn(List.of(
                PeriodoResponse.builder().id(PERIODO_ID_PRIMARY).ano(2024).semestre(1).build(),
                PeriodoResponse.builder().id(PERIODO_ID_SECONDARY).ano(2024).semestre(2).build()
        ));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(PERIODO_ID_PRIMARY)));
    }

    @Test
    void isWithinPeriodoShouldReturnBoolean() throws Exception {
        Instant consulta = Instant.parse("2024-03-01T00:00:00Z");
        when(periodoService.isWithinPeriodo(consulta)).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/vigencia")
                        .param("fecha", "2024-03-01T00:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
