package edu.dosw.sirha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.sirha.dto.request.FacultadRequest;
import edu.dosw.sirha.dto.response.FacultadResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.security.JwtTokenService;
import edu.dosw.sirha.service.FacultadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para {@link FacultadController}.
 * 
 * <p>Valida el comportamiento de los endpoints REST y la seguridad.</p>
 */
@WebMvcTest(FacultadController.class)
@AutoConfigureMockMvc(addFilters = false)
class FacultadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultadService facultadService;

    @MockBean
    private JwtTokenService jwtTokenService;

    @Test
    @WithMockUser
    void getAll_shouldReturnAllFacultades() throws Exception {
        // Given
        FacultadResponse response1 = FacultadResponse.builder()
                .id("fac-1")
                .nombre("Facultad de Ingeniería")
                .creditosTotales(160)
                .numeroMaterias(50)
                .activo(true)
                .build();

        FacultadResponse response2 = FacultadResponse.builder()
                .id("fac-2")
                .nombre("Facultad de Ciencias")
                .creditosTotales(150)
                .numeroMaterias(40)
                .activo(true)
                .build();

        List<FacultadResponse> facultades = Arrays.asList(response1, response2);
        when(facultadService.findAll()).thenReturn(facultades);

        // When & Then
        mockMvc.perform(get("/api/facultades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Facultad de Ingeniería"))
                .andExpect(jsonPath("$[1].nombre").value("Facultad de Ciencias"));

        verify(facultadService).findAll();
    }

    @Test
    @WithMockUser
    void getAllActive_shouldReturnOnlyActiveFacultades() throws Exception {
        // Given
        FacultadResponse response = FacultadResponse.builder()
                .id("fac-1")
                .nombre("Facultad de Ingeniería")
                .activo(true)
                .build();

        when(facultadService.findAllActive()).thenReturn(Arrays.asList(response));

        // When & Then
        mockMvc.perform(get("/api/facultades/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].activo").value(true));

        verify(facultadService).findAllActive();
    }

    @Test
    @WithMockUser
    void getById_shouldReturnFacultad_whenExists() throws Exception {
        // Given
        FacultadResponse response = FacultadResponse.builder()
                .id("fac-1")
                .nombre("Facultad de Ingeniería")
                .creditosTotales(160)
                .numeroMaterias(50)
                .activo(true)
                .build();

        when(facultadService.findById("fac-1")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/facultades/fac-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("fac-1"))
                .andExpect(jsonPath("$.nombre").value("Facultad de Ingeniería"))
                .andExpect(jsonPath("$.creditosTotales").value(160));

        verify(facultadService).findById("fac-1");
    }

    @Test
    @WithMockUser
    void getById_shouldReturn404_whenNotFound() throws Exception {
        // Given
        when(facultadService.findById("invalid-id"))
                .thenThrow(new ResourceNotFoundException("Facultad no encontrada"));

        // When & Then
        mockMvc.perform(get("/api/facultades/invalid-id"))
                .andExpect(status().isNotFound());

        verify(facultadService).findById("invalid-id");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldCreateFacultad_whenValid() throws Exception {
        // Given
        FacultadRequest request = FacultadRequest.builder()
                .nombre("Facultad de Ingeniería")
                .creditosTotales(160)
                .numeroMaterias(0)
                .activo(true)
                .build();

        FacultadResponse response = FacultadResponse.builder()
                .id("fac-1")
                .nombre("Facultad de Ingeniería")
                .creditosTotales(160)
                .numeroMaterias(0)
                .activo(true)
                .build();

        when(facultadService.create(any(FacultadRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/facultades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("fac-1"))
                .andExpect(jsonPath("$.nombre").value("Facultad de Ingeniería"));

        verify(facultadService).create(any(FacultadRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn400_whenDuplicateName() throws Exception {
        // Given
        FacultadRequest request = FacultadRequest.builder()
                .nombre("Facultad Duplicada")
                .creditosTotales(160)
                .numeroMaterias(0)
                .activo(true)
                .build();

        when(facultadService.create(any(FacultadRequest.class)))
                .thenThrow(new BusinessException("Ya existe una facultad con el nombre"));

        // When & Then
        mockMvc.perform(post("/api/facultades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(facultadService).create(any(FacultadRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldUpdateFacultad_whenValid() throws Exception {
        // Given
        FacultadRequest request = FacultadRequest.builder()
                .nombre("Facultad Actualizada")
                .creditosTotales(170)
                .numeroMaterias(55)
                .activo(true)
                .build();

        FacultadResponse response = FacultadResponse.builder()
                .id("fac-1")
                .nombre("Facultad Actualizada")
                .creditosTotales(170)
                .numeroMaterias(55)
                .activo(true)
                .build();

        when(facultadService.update(anyString(), any(FacultadRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/facultades/fac-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Facultad Actualizada"))
                .andExpect(jsonPath("$.creditosTotales").value(170));

        verify(facultadService).update(eq("fac-1"), any(FacultadRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldDeleteFacultad_whenNoMateriasAssociated() throws Exception {
        // Given
        doNothing().when(facultadService).delete("fac-1");

        // When & Then
        mockMvc.perform(delete("/api/facultades/fac-1"))
                .andExpect(status().isNoContent());

        verify(facultadService).delete("fac-1");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturn400_whenHasMateriasAssociated() throws Exception {
        // Given
        doThrow(new BusinessException("No se puede eliminar la facultad porque tiene materias asociadas"))
                .when(facultadService).delete("fac-1");

        // When & Then
        mockMvc.perform(delete("/api/facultades/fac-1"))
                .andExpect(status().isBadRequest());

        verify(facultadService).delete("fac-1");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void toggleActive_shouldChangeActiveStatus() throws Exception {
        // Given
        FacultadResponse response = FacultadResponse.builder()
                .id("fac-1")
                .nombre("Facultad Test")
                .activo(false)
                .build();

        when(facultadService.toggleActive("fac-1", false)).thenReturn(response);

        // When & Then
        mockMvc.perform(patch("/api/facultades/fac-1/toggle-active")
                        .param("activo", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));

        verify(facultadService).toggleActive("fac-1", false);
    }
}
