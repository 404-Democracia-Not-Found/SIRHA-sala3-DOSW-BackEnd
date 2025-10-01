package edu.dosw.sirha.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SemaforoAcademicoTest {

    @Test
    void porcentajeAvanceShouldReturnZeroWhenNoMaterias() {
        SemaforoAcademico semaforo = SemaforoAcademico.builder().build();

        assertThat(semaforo.porcentajeAvance()).isZero();
    }

    @Test
    void porcentajeAvanceShouldComputeRatio() {
        SemaforoAcademico semaforo = SemaforoAcademico.builder()
                .materiasAprobadas(8)
                .materiasEnProgreso(1)
                .materiasPerdidas(1)
                .build();

        assertThat(semaforo.porcentajeAvance()).isEqualTo(0.8);
    }

    @Test
    void settersAndGettersShouldWorkWithBuilder() {
        SemaforoAcademico semaforo = new SemaforoAcademico();
        semaforo.setMateriasAprobadas(5);
        semaforo.setMateriasEnProgreso(3);
        semaforo.setMateriasPerdidas(2);

        assertThat(semaforo.getMateriasAprobadas()).isEqualTo(5);
        assertThat(semaforo.getMateriasEnProgreso()).isEqualTo(3);
        assertThat(semaforo.getMateriasPerdidas()).isEqualTo(2);
    }
}
