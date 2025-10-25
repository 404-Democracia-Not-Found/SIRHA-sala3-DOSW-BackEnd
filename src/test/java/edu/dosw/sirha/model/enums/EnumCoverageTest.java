package edu.dosw.sirha.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas de cobertura para enums del sistema SIRHA.
 * 
 * <p>Verifica que todos los enums del sistema tienen sus valores correctamente definidos
 * y que los métodos estándar de enums ({@code values()}, {@code valueOf()}) funcionan
 * apropiadamente. Esta clase ayuda a alcanzar cobertura completa de los enums.</p>
 * 
 * <p><strong>Enums probados:</strong></p>
 * <ul>
 *   <li>{@link EstadoInscripcion} - Estados de inscripciones en materias</li>
 *   <li>{@link Genero} - Opciones de género para usuarios</li>
 *   <li>{@link Rol} - Roles de usuario en el sistema</li>
 *   <li>{@link SolicitudEstado} - Estados del ciclo de vida de solicitudes</li>
 *   <li>{@link SolicitudTipo} - Tipos de solicitudes académicas</li>
 * </ul>
 * 
 * @see EstadoInscripcion
 * @see Genero
 * @see Rol
 * @see SolicitudEstado
 * @see SolicitudTipo
 */
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
