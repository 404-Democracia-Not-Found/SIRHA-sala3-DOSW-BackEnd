package edu.dosw.project.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitarios para SolicitudCreateDto
 * Aumenta cobertura significativamente
 */
public class SolicitudCreateDtoModelTest {

    private SolicitudCreateDto dto;
    
    @BeforeEach
    public void setUp() {
        dto = new SolicitudCreateDto();
    }

    @Test
    public void testDefaultConstructor() {
        SolicitudCreateDto newDto = new SolicitudCreateDto();
        assertNotNull(newDto);
    }

    @Test
    public void testGettersAndSetters() {
        // Test tipo
        String tipo = "CAMBIO_GRUPO";
        dto.setTipo(tipo);
        assertEquals(tipo, dto.getTipo());

        // Test descripcion
        String descripcion = "Solicitud de cambio de grupo por motivos personales";
        dto.setDescripcion(descripcion);
        assertEquals(descripcion, dto.getDescripcion());

        // Test inscripcion origen ID
        String inscripcionOrigenId = "inscripcion123";
        dto.setInscripcionOrigenId(inscripcionOrigenId);
        assertEquals(inscripcionOrigenId, dto.getInscripcionOrigenId());

        // Test grupo destino ID
        String grupoDestinoId = "grupo456";
        dto.setGrupoDestinoId(grupoDestinoId);
        assertEquals(grupoDestinoId, dto.getGrupoDestinoId());

        // Test periodo ID
        String periodoId = "2024-1";
        dto.setPeriodoId(periodoId);
        assertEquals(periodoId, dto.getPeriodoId());
    }

    @Test
    public void testCambioGrupoScenario() {
        // Scenario completo para cambio de grupo
        dto.setTipo("CAMBIO_GRUPO");
        dto.setDescripcion("Solicito cambio de grupo por horario laboral");
        dto.setInscripcionOrigenId("inscr_001");
        dto.setGrupoDestinoId("grupo_tarde_001");
        dto.setPeriodoId("2024-I");

        assertEquals("CAMBIO_GRUPO", dto.getTipo());
        assertEquals("Solicito cambio de grupo por horario laboral", dto.getDescripcion());
        assertEquals("inscr_001", dto.getInscripcionOrigenId());
        assertEquals("grupo_tarde_001", dto.getGrupoDestinoId());
        assertEquals("2024-I", dto.getPeriodoId());
    }

    @Test
    public void testCambioMateriaScenario() {
        // Scenario completo para cambio de materia
        dto.setTipo("CAMBIO_MATERIA");
        dto.setDescripcion("Cambio de materia por prerrequisitos");
        dto.setInscripcionOrigenId("inscr_mat_001");
        dto.setGrupoDestinoId("materia_destino_001");
        dto.setPeriodoId("2024-II");

        assertEquals("CAMBIO_MATERIA", dto.getTipo());
        assertEquals("Cambio de materia por prerrequisitos", dto.getDescripcion());
        assertEquals("inscr_mat_001", dto.getInscripcionOrigenId());
        assertEquals("materia_destino_001", dto.getGrupoDestinoId());
        assertEquals("2024-II", dto.getPeriodoId());
    }

    @Test
    public void testNullValues() {
        // Test comportamiento con valores null
        dto.setTipo(null);
        dto.setDescripcion(null);
        dto.setInscripcionOrigenId(null);
        dto.setGrupoDestinoId(null);
        dto.setPeriodoId(null);

        assertNull(dto.getTipo());
        assertNull(dto.getDescripcion());
        assertNull(dto.getInscripcionOrigenId());
        assertNull(dto.getGrupoDestinoId());
        assertNull(dto.getPeriodoId());
    }

    @Test
    public void testEmptyStringValues() {
        // Test comportamiento con strings vacíos
        dto.setTipo("");
        dto.setDescripcion("");
        dto.setInscripcionOrigenId("");
        dto.setGrupoDestinoId("");
        dto.setPeriodoId("");

        assertEquals("", dto.getTipo());
        assertEquals("", dto.getDescripcion());
        assertEquals("", dto.getInscripcionOrigenId());
        assertEquals("", dto.getGrupoDestinoId());
        assertEquals("", dto.getPeriodoId());
    }

    @Test
    public void testLongStrings() {
        // Test con strings largos para verificar robustez
        String longString = "a".repeat(500);
        
        dto.setDescripcion(longString);
        assertEquals(longString, dto.getDescripcion());
        assertEquals(500, dto.getDescripcion().length());
    }
}