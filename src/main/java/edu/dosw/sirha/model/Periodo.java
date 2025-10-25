package edu.dosw.sirha.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que representa un período académico (semestre) en el sistema SIRHA.
 * 
 * <p>Un período académico es un ciclo lectivo (generalmente un semestre) con fechas de inicio
 * y fin definidas. Los períodos controlan las ventanas de tiempo en las que los estudiantes
 * pueden realizar solicitudes de cambio de horario y determinan qué grupos están disponibles.</p>
 * 
 * <h2>Concepto de Período Activo:</h2>
 * <p>Solo puede existir un período activo a la vez en el sistema. El período activo es el que
 * está vigente actualmente y en el cual se permiten operaciones de gestión de horarios.
 * Cuando se activa un nuevo período, todos los demás se desactivan automáticamente.</p>
 * 
 * <h2>Ventanas de Tiempo:</h2>
 * <ul>
 *   <li><b>Período académico:</b> Entre {@code fechaInicio} y {@code fechaFin} - duración del semestre</li>
 *   <li><b>Ventana de inscripción:</b> Desde {@code fechaInscripcionInicio} - cuándo se abren inscripciones</li>
 *   <li><b>Ventana de solicitudes:</b> Hasta {@code fechaLimiteSolicitudes} - cuándo se cierran solicitudes</li>
 * </ul>
 * 
 * <h2>Identificación de Períodos:</h2>
 * <p>Los períodos se identifican por año y semestre:</p>
 * <ul>
 *   <li><b>Año:</b> Año calendario (ej: 2024)</li>
 *   <li><b>Semestre:</b> 1 (primer semestre) o 2 (segundo semestre)</li>
 *   <li><b>Nombre común:</b> "2024-1", "2024-2", etc.</li>
 * </ul>
 * 
 * <h2>Configuración Adicional:</h2>
 * <p>El campo {@code configuracion} contiene parámetros específicos del período como:
 * número máximo de créditos permitidos, reglas de cambio, plazos especiales, etc.</p>
 * 
 * <p><b>Ejemplo de período académico:</b></p>
 * <pre>
 * {
 *   "id": "665e8a1b2345678901bcdef0",
 *   "fechaInicio": "2024-08-01T00:00:00Z",
 *   "fechaFin": "2024-12-15T23:59:59Z",
 *   "fechaInscripcionInicio": "2024-07-15T00:00:00Z",
 *   "fechaLimiteSolicitudes": "2024-08-31T23:59:59Z",
 *   "ano": 2024,
 *   "semestre": 2,
 *   "activo": true,
 *   "configuracion": {
 *     "creditosMaximos": 24,
 *     "creditosMinimos": 12,
 *     "diasLimiteRespuesta": 7
 *   }
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see PeriodoConfiguracion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "periodos_academicos")
public class Periodo {

    /**
     * Identificador único del período en MongoDB.
     * Generado automáticamente como ObjectId.
     */
    @Id
    private String id;

    /**
     * Fecha y hora de inicio del período académico.
     * <p>Marca el comienzo oficial del semestre. A partir de esta fecha se activan
     * los grupos, inscripciones y gestión académica del período.</p>
     * <p><b>Ejemplo:</b> "2024-08-01T00:00:00Z" (primer día del semestre)</p>
     */
    @NotNull
    private Instant fechaInicio;

    /**
     * Fecha y hora de finalización del período académico.
     * <p>Marca el cierre oficial del semestre. Después de esta fecha no se permiten
     * cambios relacionados con este período.</p>
     * <p><b>Ejemplo:</b> "2024-12-15T23:59:59Z" (último día del semestre)</p>
     */
    @NotNull
    private Instant fechaFin;

    /**
     * Fecha y hora de inicio del proceso de inscripciones.
     * <p>Momento en que se abren las inscripciones para el período. Los estudiantes
     * pueden inscribirse en materias y grupos a partir de esta fecha.</p>
     * <p><b>Típicamente:</b> 2-3 semanas antes del inicio del período académico</p>
     */
    @NotNull
    private Instant fechaInscripcionInicio;

    /**
     * Fecha límite para realizar solicitudes de cambio de horario.
     * <p>Última fecha en la que los estudiantes pueden crear solicitudes de cambio.
     * Después de este momento, el sistema rechaza nuevas solicitudes para este período.</p>
     * <p><b>Típicamente:</b> 2-4 semanas después del inicio de clases</p>
     */
    @NotNull
    private Instant fechaLimiteSolicitudes;

    /**
     * Año calendario del período académico.
     * <p>Identifica el año del semestre. Se usa junto con {@code semestre} para
     * nombrar y filtrar períodos.</p>
     * <p><b>Ejemplo:</b> 2024</p>
     */
    private int ano;
    
    /**
     * Número del semestre dentro del año.
     * <p>Identifica si es primer o segundo semestre del año académico.</p>
     * <p><b>Valores:</b> 1 (primer semestre) o 2 (segundo semestre)</p>
     * <p>En conjunto con {@code ano}, forma el nombre del período (ej: "2024-1")</p>
     */
    private int semestre;
    
    /**
     * Indica si este es el período académico activo actualmente.
     * <p><b>true:</b> Este es el período en curso, todas las operaciones se realizan sobre este período</p>
     * <p><b>false:</b> Período histórico o futuro, no permite operaciones activas</p>
     * <p><b>Restricción:</b> Solo puede haber un período activo a la vez en el sistema</p>
     */
    private boolean activo;

    /**
     * Configuración específica del período académico.
     * <p>Contiene parámetros y reglas específicas de este período, como:
     * créditos máximos/mínimos permitidos, días límite para respuestas,
     * reglas especiales de cambio, etc.</p>
     * <p>Permite personalizar el comportamiento del sistema por período sin cambiar código.</p>
     * 
     * @see PeriodoConfiguracion
     */
    private PeriodoConfiguracion configuracion;
}