package edu.dosw.sirha.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
