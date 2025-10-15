package edu.dosw.sirha.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para {@link SemaforoAcademico}.
 * 
 * <p>Verifica el correcto cálculo del porcentaje de avance académico del estudiante
 * basado en materias aprobadas, en progreso y perdidas.</p>
 * 
 * <p><strong>Métodos probados:</strong></p>
 * <ul>
 *   <li>{@code porcentajeAvance()} - Calcula ratio de materias aprobadas sobre el total</li>
 * </ul>
 * 
 * <p><strong>Escenarios verificados:</strong></p>
 * <ul>
 *   <li>Cálculo con múltiples materias en diferentes estados</li>
 *   <li>Caso especial: sin materias (retorna 0)</li>
 *   <li>Setters y getters de Lombok funcionan correctamente</li>
 * </ul>
 * 
 * @see SemaforoAcademico
 */
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
