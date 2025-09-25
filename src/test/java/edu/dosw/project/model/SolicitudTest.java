package edu.dosw.project.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudTest {

    @Test
    void constructor_setsDefaults() {
        // When
        Solicitud solicitud = new Solicitud();

        // Then
        assertNotNull(solicitud.getFechaSolicitud());
        assertEquals("PENDIENTE", solicitud.getEstado());
    }

    @Test
    void settersAndGetters_work() {
        // Given
        Solicitud solicitud = new Solicitud();
        LocalDateTime fecha = LocalDateTime.now();

        // When
        solicitud.setId("test-id");
        solicitud.setCodigoSolicitud("SOL-001");
        solicitud.setEstado("APROBADA");
        solicitud.setFechaSolicitud(fecha);
        solicitud.setTipo("CAMBIO_GRUPO");
        solicitud.setDescripcion("Test description");
        solicitud.setEstudianteId("student-123");
        solicitud.setInscripcionOrigenId("inscripcion-456");
        solicitud.setGrupoDestinoId("grupo-789");
        solicitud.setPeriodoId("periodo-2024");
        solicitud.setPrioridad(1);
        solicitud.setFechaLimiteRespuesta(fecha.plusDays(7));

        // Then
        assertEquals("test-id", solicitud.getId());
        assertEquals("SOL-001", solicitud.getCodigoSolicitud());
        assertEquals("APROBADA", solicitud.getEstado());
        assertEquals(fecha, solicitud.getFechaSolicitud());
        assertEquals("CAMBIO_GRUPO", solicitud.getTipo());
        assertEquals("Test description", solicitud.getDescripcion());
        assertEquals("student-123", solicitud.getEstudianteId());
        assertEquals("inscripcion-456", solicitud.getInscripcionOrigenId());
        assertEquals("grupo-789", solicitud.getGrupoDestinoId());
        assertEquals("periodo-2024", solicitud.getPeriodoId());
        assertEquals(1, solicitud.getPrioridad());
        assertEquals(fecha.plusDays(7), solicitud.getFechaLimiteRespuesta());
    }

    @Test
    void historial_settersAndGetters_work() {
        // Given
        Solicitud solicitud = new Solicitud();
        List<Solicitud.HistorialSolicitud> historial = new ArrayList<>();
        
        Solicitud.HistorialSolicitud item = new Solicitud.HistorialSolicitud();
        LocalDateTime fecha = LocalDateTime.now();
        
        // When
        item.setFecha(fecha);
        item.setAccion("CREADA");
        item.setUsuarioId("user-123");
        item.setComentario("Solicitud creada");
        historial.add(item);
        
        solicitud.setHistorial(historial);

        // Then
        assertEquals(1, solicitud.getHistorial().size());
        Solicitud.HistorialSolicitud retrievedItem = solicitud.getHistorial().get(0);
        assertEquals(fecha, retrievedItem.getFecha());
        assertEquals("CREADA", retrievedItem.getAccion());
        assertEquals("user-123", retrievedItem.getUsuarioId());
        assertEquals("Solicitud creada", retrievedItem.getComentario());
    }
}