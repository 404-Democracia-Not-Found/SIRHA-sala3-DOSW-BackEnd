package edu.dosw.sirha.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnumCoverageTest {

    @Test
    void estadoInscripcionValuesShouldBeAccessible() {
        assertThat(EstadoInscripcion.values()).containsExactly(
                EstadoInscripcion.INSCRITO,
                EstadoInscripcion.CANCELADO,
                EstadoInscripcion.APROBADO,
                EstadoInscripcion.REPROBADO);

        assertThat(EstadoInscripcion.valueOf("INSCRITO")).isEqualTo(EstadoInscripcion.INSCRITO);
    }

    @Test
    void generoValuesShouldBeAccessible() {
        assertThat(Genero.values()).containsExactly(
                Genero.FEMENINO,
                Genero.MASCULINO,
                Genero.NO_BINARIO,
                Genero.PREFIERE_NO_RESPONDER);

        assertThat(Genero.valueOf("NO_BINARIO")).isEqualTo(Genero.NO_BINARIO);
    }
}
