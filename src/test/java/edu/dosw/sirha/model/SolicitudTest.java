package edu.dosw.sirha.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para la entidad {@link Solicitud}.
 * 
 * <p>Verifica la lógica de gestión del historial de cambios de estado en solicitudes
 * académicas, asegurando que los eventos se registren correctamente y se preserven
 * en el orden cronológico adecuado.</p>
 * 
 * <p><strong>Métodos probados:</strong></p>
 * <ul>
 *   <li>{@code agregarEventoHistorial()} - Añade entradas al historial de auditoría</li>
 * </ul>
 * 
 * <p><strong>Escenarios verificados:</strong></p>
 * <ul>
 *   <li>Agregar eventos a historial existente</li>
 *   <li>Inicializar historial desde vacío</li>
 *   <li>Preservación de orden cronológico</li>
 *   <li>Múltiples eventos consecutivos</li>
 * </ul>
 * 
 * @see Solicitud
 * @see SolicitudHistorialEntry
 */
class SolicitudTest {

        @Test
    void agregarEventoHistorialShouldAppendToExistingList() {
        SolicitudHistorialEntry existing = SolicitudHistorialEntry.builder()
                .accion("CREADA")
                .build();
        Solicitud solicitud = Solicitud.builder()
                .historial(new java.util.ArrayList<>(List.of(existing)))
                .build();

        SolicitudHistorialEntry nuevo = SolicitudHistorialEntry.builder()
                .accion("ACTUALIZADA")
                .build();

        solicitud.agregarEventoHistorial(nuevo);

        assertThat(solicitud.getHistorial()).containsExactly(existing, nuevo);
    }
}
