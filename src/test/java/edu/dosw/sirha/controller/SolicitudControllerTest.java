package edu.dosw.sirha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.security.JwtAuthFilter;
import edu.dosw.sirha.security.SecurityConfig;
import edu.dosw.sirha.service.SolicitudService;
import edu.dosw.sirha.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SolicitudController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
    })
@AutoConfigureMockMvc(addFilters = false)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SolicitudService solicitudService;

    @Test
    void createShouldReturnCreatedSolicitud() throws Exception {
        SolicitudRequest request = TestDataFactory.buildSolicitudRequest();
        SolicitudResponse response = TestDataFactory.buildSolicitudResponse();

        when(solicitudService.create(any(SolicitudRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is("sol-1")))
            .andExpect(jsonPath("$.estado", is(SolicitudEstado.PENDIENTE.name())));

        Mockito.verify(solicitudService).create(any(SolicitudRequest.class));
    }

    @Test
    void updateShouldReturnUpdatedSolicitud() throws Exception {
    SolicitudRequest request = TestDataFactory.buildSolicitudRequest();
    SolicitudResponse response = TestDataFactory.buildSolicitudResponse();

    when(solicitudService.update(any(), any(SolicitudRequest.class))).thenReturn(response);

    mockMvc.perform(put("/api/solicitudes/{id}", "sol-1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is("sol-1")));

    ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(solicitudService).update(idCaptor.capture(), any(SolicitudRequest.class));
    assertThat(idCaptor.getValue()).isEqualTo("sol-1");
    }

    @Test
    void changeEstadoShouldReturnUpdatedSolicitud() throws Exception {
    Map<String, Object> payload = Map.of(
        "estado", SolicitudEstado.APROBADA.name(),
        "observaciones", "Listo");
    SolicitudResponse response = SolicitudResponse.builder()
        .id("sol-1")
        .codigoSolicitud("SOL-1")
        .estado(SolicitudEstado.APROBADA)
        .tipo(TestDataFactory.buildSolicitudResponse().getTipo())
        .descripcion("Cambio")
        .estudianteId("est-123")
        .periodoId("per-1")
        .prioridad(1)
        .build();

    when(solicitudService.changeEstado(any(), any(), any())).thenReturn(response);

    mockMvc.perform(patch("/api/solicitudes/{id}/estado", "sol-1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado", is(SolicitudEstado.APROBADA.name())));

    ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<SolicitudEstado> estadoCaptor = ArgumentCaptor.forClass(SolicitudEstado.class);
    ArgumentCaptor<String> obsCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(solicitudService).changeEstado(idCaptor.capture(), estadoCaptor.capture(), obsCaptor.capture());
    assertThat(idCaptor.getValue()).isEqualTo("sol-1");
    assertThat(estadoCaptor.getValue()).isEqualTo(SolicitudEstado.APROBADA);
    assertThat(obsCaptor.getValue()).isEqualTo("Listo");
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/solicitudes/{id}", "sol-1"))
        .andExpect(status().isNoContent());

    Mockito.verify(solicitudService).delete("sol-1");
    }

    @Test
    void findAllShouldReturnArray() throws Exception {
    when(solicitudService.findAll()).thenReturn(List.of(TestDataFactory.buildSolicitudResponse()));

    mockMvc.perform(get("/api/solicitudes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findByIdShouldReturnResponse() throws Exception {
    when(solicitudService.findById("sol-1")).thenReturn(TestDataFactory.buildSolicitudResponse());

    mockMvc.perform(get("/api/solicitudes/{id}", "sol-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is("sol-1")));
    }

    @Test
    void findByEstudianteShouldReturnList() throws Exception {
    when(solicitudService.findByEstudiante("est-123"))
        .thenReturn(List.of(TestDataFactory.buildSolicitudResponse()));

    mockMvc.perform(get("/api/solicitudes/estudiante/{id}", "est-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findByEstadosShouldDelegateToService() throws Exception {
    when(solicitudService.findByEstados(List.of(SolicitudEstado.APROBADA, SolicitudEstado.RECHAZADA)))
        .thenReturn(List.of(TestDataFactory.buildSolicitudResponse()));

    mockMvc.perform(get("/api/solicitudes/estados")
            .param("estado", SolicitudEstado.APROBADA.name(), SolicitudEstado.RECHAZADA.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void countByEstadoShouldReturnNumericValue() throws Exception {
    when(solicitudService.countByEstado(SolicitudEstado.PENDIENTE)).thenReturn(5L);

    mockMvc.perform(get("/api/solicitudes/estados/{estado}/conteo", SolicitudEstado.PENDIENTE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(5)));
    }

    @Test
    void findByPeriodoAndRangoShouldForwardParameters() throws Exception {
    Instant inicio = Instant.parse("2024-01-01T00:00:00Z");
    Instant fin = Instant.parse("2024-01-31T23:59:59Z");
    when(solicitudService.findByPeriodoAndRango("per-1", inicio, fin))
        .thenReturn(List.of(TestDataFactory.buildSolicitudResponse()));

    mockMvc.perform(get("/api/solicitudes/periodo/{id}", "per-1")
            .param("inicio", "2024-01-01T00:00:00Z")
            .param("fin", "2024-01-31T23:59:59Z"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
    }
}