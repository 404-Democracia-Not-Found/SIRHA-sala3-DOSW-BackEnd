package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.FacultadRequest;
import edu.dosw.sirha.dto.response.FacultadResponse;
import edu.dosw.sirha.model.Facultad;
import edu.dosw.sirha.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link FacultadMapper}.
 * 
 * <p>Valida las conversiones entre entidades y DTOs.</p>
 */
class FacultadMapperTest {

    private FacultadMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FacultadMapper();
    }

    @Test
    void toEntity_shouldConvertRequestToEntity() {
        // Given
        FacultadRequest request = FacultadRequest.builder()
                .nombre("Facultad de Ingeniería")
                .creditosTotales(160)
                .numeroMaterias(0)
                .activo(true)
                .decanoId("decano-1")
                .build();

        // When
        Facultad facultad = mapper.toEntity(request);

        // Then
        assertNotNull(facultad);
        assertEquals("Facultad de Ingeniería", facultad.getNombre());
        assertEquals(160, facultad.getCreditosTotales());
        assertEquals(0, facultad.getNumeroMaterias());
        assertTrue(facultad.isActivo());
        assertEquals("decano-1", facultad.getDecanoId());
    }

    @Test
    void toEntity_shouldHandleNullDecanoId() {
        // Given
        FacultadRequest request = FacultadRequest.builder()
                .nombre("Facultad Test")
                .creditosTotales(150)
                .numeroMaterias(0)
                .activo(true)
                .decanoId(null)
                .build();

        // When
        Facultad facultad = mapper.toEntity(request);

        // Then
        assertNotNull(facultad);
        assertNull(facultad.getDecanoId());
    }

    @Test
    void toResponse_shouldConvertEntityToResponse_withDecano() {
        // Given
        Facultad facultad = new Facultad();
        facultad.setId("fac-1");
        facultad.setNombre("Facultad de Ingeniería");
        facultad.setCreditosTotales(160);
        facultad.setNumeroMaterias(50);
        facultad.setActivo(true);
        facultad.setDecanoId("decano-1");

        User decano = new User();
        decano.setId("decano-1");
        decano.setNombre("Dr. Juan Pérez");

        // When
        FacultadResponse response = mapper.toResponse(facultad, decano);

        // Then
        assertNotNull(response);
        assertEquals("fac-1", response.getId());
        assertEquals("Facultad de Ingeniería", response.getNombre());
        assertEquals(160, response.getCreditosTotales());
        assertEquals(50, response.getNumeroMaterias());
        assertTrue(response.isActivo());
        assertEquals("decano-1", response.getDecanoId());
        assertEquals("Dr. Juan Pérez", response.getDecanoNombre());
    }

    @Test
    void toResponse_shouldConvertEntityToResponse_withoutDecano() {
        // Given
        Facultad facultad = new Facultad();
        facultad.setId("fac-2");
        facultad.setNombre("Facultad de Ciencias");
        facultad.setCreditosTotales(150);
        facultad.setNumeroMaterias(40);
        facultad.setActivo(false);
        facultad.setDecanoId(null);

        // When
        FacultadResponse response = mapper.toResponse(facultad, null);

        // Then
        assertNotNull(response);
        assertEquals("fac-2", response.getId());
        assertEquals("Facultad de Ciencias", response.getNombre());
        assertEquals(150, response.getCreditosTotales());
        assertEquals(40, response.getNumeroMaterias());
        assertFalse(response.isActivo());
        assertNull(response.getDecanoId());
        assertNull(response.getDecanoNombre());
    }

    @Test
    void updateEntity_shouldUpdateAllFields() {
        // Given
        Facultad existing = new Facultad();
        existing.setId("fac-1");
        existing.setNombre("Facultad Original");
        existing.setCreditosTotales(150);
        existing.setNumeroMaterias(40);
        existing.setActivo(true);
        existing.setDecanoId("old-decano");

        FacultadRequest request = FacultadRequest.builder()
                .nombre("Facultad Actualizada")
                .creditosTotales(170)
                .numeroMaterias(55)
                .activo(false)
                .decanoId("new-decano")
                .build();

        // When
        mapper.updateEntity(existing, request);

        // Then
        assertEquals("fac-1", existing.getId()); // ID no cambia
        assertEquals("Facultad Actualizada", existing.getNombre());
        assertEquals(170, existing.getCreditosTotales());
        assertEquals(55, existing.getNumeroMaterias());
        assertFalse(existing.isActivo());
        assertEquals("new-decano", existing.getDecanoId());
    }

    @Test
    void updateEntity_shouldHandleNullDecanoId() {
        // Given
        Facultad existing = new Facultad();
        existing.setId("fac-1");
        existing.setNombre("Facultad Test");
        existing.setDecanoId("old-decano");

        FacultadRequest request = FacultadRequest.builder()
                .nombre("Facultad Updated")
                .creditosTotales(160)
                .numeroMaterias(50)
                .activo(true)
                .decanoId(null)
                .build();

        // When
        mapper.updateEntity(existing, request);

        // Then
        assertNull(existing.getDecanoId());
    }
}
