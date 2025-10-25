package edu.dosw.sirha.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import edu.dosw.sirha.model.enums.EstadoInscripcion;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inscripción de un estudiante a un grupo en un periodo académico.
 * 
 * <p>Representa la matrícula formal de un estudiante en un grupo específico de una materia.
 * Rastrea el ciclo de vida completo: desde inscripción inicial hasta calificación final.</p>
 * 
 * <p>Una inscripción puede pasar por varios estados:</p>
 * <ul>
 *   <li>INSCRITO: Estudiante activo en el grupo</li>
 *   <li>CANCELADO: Estudiante retiró la materia</li>
 *   <li>APROBADO: Finalizó exitosamente</li>
 *   <li>REPROBADO: No cumplió requisitos mínimos</li>
 * </ul>
 * 
 * @see Grupo
 * @see User
 * @see Periodo
 * @see EstadoInscripcion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inscripciones")
public class Inscripcion {

    /**
     * ID único de la inscripción.
     */
    @Id
    private String id;

    /**
     * ID del estudiante inscrito.
     * 
     * <p>Referencia a {@link User} con rol ESTUDIANTE.</p>
     */
    @NotBlank
    private String estudianteId;

    /**
     * ID del grupo al que está inscrito.
     * 
     * <p>Referencia a {@link Grupo}.</p>
     */
    @NotBlank
    private String grupoId;

    /**
     * ID del periodo académico en que se inscribió.
     * 
     * <p>Referencia a {@link Periodo}.</p>
     */
    @NotBlank
    private String periodoId;

    /**
     * Fecha y hora en que se realizó la inscripción.
     */
    @Field("fecha_inscripcion")
    private Instant fechaInscripcion;

    /**
     * Estado actual de la inscripción.
     * 
     * <p>INSCRITO, CANCELADO, APROBADO o REPROBADO.</p>
     * 
     * @see EstadoInscripcion
     */
    private EstadoInscripcion estado;

    /**
     * Calificación final obtenida (0.0 a 5.0).
     * 
     * <p>null si aún no se ha calificado o está cancelada.</p>
     */
    private BigDecimal calificacionFinal;

    /**
     * Fecha del último cambio de estado.
     * 
     * <p>Se actualiza cuando pasa de INSCRITO a CANCELADO/APROBADO/REPROBADO.</p>
     */
    @Field("fecha_cambio_estado")
    private Instant fechaCambioEstado;

    /**
     * Observaciones adicionales sobre la inscripción.
     * 
     * <p>Ejemplo: "Canceló por problemas de salud", "Retiró para cursar en otro periodo".</p>
     */
    private String observaciones;

    /**
     * Indica si es la primera vez que el estudiante cursa esta materia.
     * 
     * <p>false si está repitiendo la materia.</p>
     */
    private boolean esPrimeraVez;

    /**
     * Número de intentos previos (veces que cursó la materia antes).
     * 
     * <p>0 si {@link #esPrimeraVez} es true, >0 si está repitiendo.</p>
     */
    private int intentosPrevios;

    private String solicitudOrigenId;
}
