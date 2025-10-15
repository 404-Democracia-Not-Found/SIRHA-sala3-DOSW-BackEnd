package edu.dosw.sirha.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.model.enums.SolicitudTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que representa una solicitud de cambio académico en el sistema SIRHA.
 * 
 * <p>Esta es la entidad central del sistema SIRHA. Una solicitud representa la petición formal
 * de un estudiante para realizar un cambio en su horario académico, ya sea cambiando de grupo
 * en una materia, cambiando de materia completamente, o realizando ajustes de horario.</p>
 * 
 * <h2>Tipos de Solicitud:</h2>
 * <ul>
 *   <li><b>CAMBIO_GRUPO:</b> Cambiar de un grupo a otro dentro de la misma materia</li>
 *   <li><b>CAMBIO_MATERIA:</b> Cambiar una materia por otra diferente</li>
 *   <li><b>AJUSTE_HORARIO:</b> Ajustar horarios sin cambiar materias ni grupos</li>
 *   <li><b>RETIRO_ASIGNATURA:</b> Solicitar retiro formal de una asignatura</li>
 * </ul>
 * 
 * <h2>Ciclo de Vida de una Solicitud:</h2>
 * <pre>
 * PENDIENTE
 *    ↓
 * EN_REVISION (Decanatura analiza)
 *    ↓
 *    ├──> INFORMACION_ADICIONAL (Requiere datos del estudiante)
 *    │         ↓
 *    │    Estudiante actualiza solicitud
 *    │         ↓
 *    │    Vuelve a PENDIENTE
 *    │
 *    ├──> APROBADA (Se aplica cambio automáticamente)
 *    │
 *    └──> RECHAZADA (Con observaciones explicativas)
 * </pre>
 * 
 * <h2>Estados de Solicitud:</h2>
 * <ul>
 *   <li><b>PENDIENTE:</b> Recién creada, esperando revisión de coordinación</li>
 *   <li><b>EN_REVISION:</b> Siendo analizada por coordinador/decanatura</li>
 *   <li><b>INFORMACION_ADICIONAL:</b> Requiere más información del estudiante</li>
 *   <li><b>APROBADA:</b> Aprobada, cambio aplicado al sistema académico</li>
 *   <li><b>RECHAZADA:</b> Rechazada con observaciones del motivo</li>
 * </ul>
 * 
 * <h2>Sistema de Prioridad:</h2>
 * <p>Las solicitudes tienen un campo {@code prioridad} (int) que determina el orden de atención:</p>
 * <ul>
 *   <li><b>Alta prioridad (1-3):</b> Casos urgentes, estudiantes con necesidades especiales</li>
 *   <li><b>Prioridad normal (4-7):</b> Solicitudes estándar</li>
 *   <li><b>Baja prioridad (8-10):</b> Solicitudes no urgentes</li>
 * </ul>
 * 
 * <h2>Historial de Cambios:</h2>
 * <p>Cada solicitud mantiene un historial completo de todos los cambios de estado, actualizaciones
 * y comentarios realizados. Esto permite trazabilidad completa del proceso de aprobación.</p>
 * 
 * <h2>Validaciones Automáticas:</h2>
 * <p>Al crear/actualizar una solicitud, el sistema valida automáticamente:</p>
 * <ul>
 *   <li>Que el período académico esté activo y dentro del rango de fechas</li>
 *   <li>Que el grupo destino tenga cupos disponibles</li>
 *   <li>Que no existan conflictos de horario con otras materias inscritas</li>
 *   <li>Que el estudiante cumpla con prerrequisitos (para cambio de materia)</li>
 *   <li>Que el estudiante esté inscrito en el grupo actual (para cambio de grupo)</li>
 * </ul>
 * 
 * <h2>Relaciones con Otras Entidades:</h2>
 * <pre>
 * Solicitud ──┬──> User (estudianteId)
 *             ├──> Periodo (periodoId)
 *             ├──> Inscripcion (inscripcionOrigenId) - inscripción actual
 *             ├──> Grupo (grupoDestinoId) - grupo al que desea cambiarse
 *             ├──> Materia (materiaDestinoId) - materia nueva (si aplica)
 *             └──> Conflict[] (conflictos detectados automáticamente)
 * </pre>
 * 
 * <h2>Código de Solicitud:</h2>
 * <p>Cada solicitud tiene un {@code codigoSolicitud} único generado automáticamente con formato:
 * {@code SOL-YYYYMMDD-XXXXX} donde XXXXX es un consecutivo numérico. Ejemplo: "SOL-20240615-00123"</p>
 * 
 * <h2>Fechas Importantes:</h2>
 * <ul>
 *   <li><b>fechaSolicitud:</b> Cuándo se creó la solicitud (automático)</li>
 *   <li><b>fechaLimiteRespuesta:</b> Fecha máxima para que se procese la solicitud</li>
 *   <li><b>fechaActualizacion:</b> Última modificación de la solicitud (automático)</li>
 * </ul>
 * 
 * <p><b>Ejemplo de solicitud de cambio de grupo:</b></p>
 * <pre>
 * {
 *   "id": "6662ce5f6789012345f01234",
 *   "codigoSolicitud": "SOL-20240615-00123",
 *   "estado": "PENDIENTE",
 *   "tipo": "CAMBIO_GRUPO",
 *   "descripcion": "Solicito cambio de grupo por conflicto laboral",
 *   "observaciones": null,
 *   "estudianteId": "665d7f9a1234567890abcdef",
 *   "inscripcionOrigenId": "665f9b2c3456789012cdef01",
 *   "grupoDestinoId": "6661bd4e5678901234ef0123",
 *   "materiaDestinoId": null,
 *   "periodoId": "665e8a1b2345678901bcdef0",
 *   "prioridad": 5,
 *   "fechaSolicitud": "2024-06-15T14:30:00Z",
 *   "fechaLimiteRespuesta": "2024-06-22T23:59:59Z",
 *   "fechaActualizacion": "2024-06-15T14:30:00Z",
 *   "historial": [
 *     {
 *       "fecha": "2024-06-15T14:30:00Z",
 *       "estadoAnterior": null,
 *       "estadoNuevo": "PENDIENTE",
 *       "usuario": "maria.gonzalez@mail.escuelaing.edu.co",
 *       "comentario": "Solicitud creada por el estudiante"
 *     }
 *   ]
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see SolicitudEstado
 * @see SolicitudTipo
 * @see SolicitudHistorialEntry
 * @see User
 * @see Periodo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "solicitudes")
public class Solicitud {

    /**
     * Identificador único de la solicitud en MongoDB.
     * Generado automáticamente como ObjectId.
     */
    @Id
    private String id;

    /**
     * Código único de la solicitud para referencia humana.
     * <p>Generado automáticamente con formato: {@code SOL-YYYYMMDD-XXXXX}</p>
     * <p><b>Ejemplo:</b> "SOL-20240615-00123"</p>
     * <ul>
     *   <li><b>SOL:</b> Prefijo que identifica como solicitud</li>
     *   <li><b>YYYYMMDD:</b> Fecha de creación (año, mes, día)</li>
     *   <li><b>XXXXX:</b> Consecutivo numérico del día</li>
     * </ul>
     * <p>Este código se usa en comunicaciones con estudiantes y para búsquedas rápidas.</p>
     */
    @NotBlank
    private String codigoSolicitud;

    /**
     * Estado actual de la solicitud en su ciclo de vida.
     * <p>Campo obligatorio que determina en qué etapa del proceso se encuentra la solicitud:</p>
     * <ul>
     *   <li>{@code PENDIENTE} - Recién creada, esperando revisión</li>
     *   <li>{@code EN_REVISION} - Siendo analizada por coordinación</li>
     *   <li>{@code INFORMACION_ADICIONAL} - Requiere información del estudiante</li>
     *   <li>{@code APROBADA} - Aprobada y cambio aplicado</li>
     *   <li>{@code RECHAZADA} - Rechazada con motivo en observaciones</li>
     * </ul>
     * <p>Cada cambio de estado se registra en el {@link #historial}.</p>
     * 
     * @see SolicitudEstado
     */
    @NotNull
    private SolicitudEstado estado;

    /**
     * Tipo de cambio académico solicitado.
     * <p>Campo obligatorio que define la naturaleza del cambio:</p>
     * <ul>
     *   <li>{@code CAMBIO_GRUPO} - Cambiar de grupo en la misma materia</li>
     *   <li>{@code CAMBIO_MATERIA} - Cambiar una materia por otra</li>
     *   <li>{@code AJUSTE_HORARIO} - Ajustar horarios sin cambiar materias</li>
     *   <li>{@code RETIRO_ASIGNATURA} - Retiro formal de asignatura</li>
     * </ul>
     * <p>El tipo determina qué validaciones y campos son obligatorios.</p>
     * 
     * @see SolicitudTipo
     */
    @NotNull
    private SolicitudTipo tipo;

    /**
     * Descripción detallada de la solicitud proporcionada por el estudiante.
     * <p>Explica la razón del cambio, situación particular, o cualquier contexto relevante
     * que ayude a la coordinación a tomar una decisión informada.</p>
     * <p><b>Ejemplo:</b> "Solicito cambio de grupo por conflicto con mi horario laboral.
     * Trabajo de 8:00 AM a 12:00 PM y el grupo actual tiene clase a las 10:00 AM."</p>
     */
    private String descripcion;

    /**
     * Observaciones o comentarios agregados por coordinadores/administradores.
     * <p>Este campo es utilizado por el personal administrativo para:</p>
     * <ul>
     *   <li>Explicar motivos de rechazo</li>
     *   <li>Solicitar información adicional específica</li>
     *   <li>Agregar notas sobre la aprobación</li>
     *   <li>Documentar decisiones tomadas</li>
     * </ul>
     * <p><b>Ejemplo de rechazo:</b> "Solicitud rechazada. El grupo solicitado no tiene cupos
     * disponibles y no hay alternativas en ese horario."</p>
     */
    private String observaciones;

    /**
     * Referencia al ID del estudiante que realiza la solicitud.
     * <p>Campo obligatorio. Identifica al usuario de tipo {@code ESTUDIANTE} que creó
     * la solicitud. Se usa para validaciones de permisos y para filtrar solicitudes
     * por estudiante.</p>
     * <p><b>Relación:</b> Referencia a documento en colección "usuarios"</p>
     * 
     * @see User
     */
    @NotBlank
    private String estudianteId;

    /**
     * Referencia a la inscripción actual del estudiante (para cambios de grupo).
     * <p>Para solicitudes de tipo {@code CAMBIO_GRUPO}, este campo apunta a la inscripción
     * actual que el estudiante desea cambiar. El sistema valida que esta inscripción
     * exista y pertenezca al estudiante.</p>
     * <p><b>Relación:</b> Referencia a documento en colección "inscripciones"</p>
     * <p><b>Opcional:</b> Solo requerido para CAMBIO_GRUPO</p>
     * 
     * @see Inscripcion
     */
    private String inscripcionOrigenId;

    /**
     * Referencia al grupo destino al que el estudiante desea cambiarse.
     * <p>Para solicitudes de {@code CAMBIO_GRUPO}, este es el nuevo grupo al que el
     * estudiante quiere inscribirse. El sistema valida cupos disponibles y conflictos
     * de horario automáticamente.</p>
     * <p><b>Relación:</b> Referencia a documento en colección "grupos"</p>
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>Debe tener cupos disponibles</li>
     *   <li>No debe tener conflictos de horario con otras materias del estudiante</li>
     *   <li>Debe pertenecer al período académico activo</li>
     * </ul>
     * 
     * @see Grupo
     */
    private String grupoDestinoId;

    /**
     * Referencia a la nueva materia solicitada (para cambios de materia).
     * <p>Para solicitudes de tipo {@code CAMBIO_MATERIA}, este campo identifica la nueva
     * materia que el estudiante desea cursar. El sistema valida prerrequisitos y
     * disponibilidad automáticamente.</p>
     * <p><b>Relación:</b> Referencia a documento en colección "materias"</p>
     * <p><b>Opcional:</b> Solo requerido para CAMBIO_MATERIA</p>
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>El estudiante debe cumplir con prerrequisitos</li>
     *   <li>No debe estar ya inscrito en esa materia</li>
     *   <li>Debe tener grupos disponibles en el período</li>
     * </ul>
     * 
     * @see Materia
     */
    private String materiaDestinoId;

    /**
     * Referencia al período académico en el que se realiza la solicitud.
     * <p>Campo que identifica el semestre/período académico al que aplica la solicitud.
     * Todas las solicitudes deben estar asociadas a un período académico activo.</p>
     * <p><b>Relación:</b> Referencia a documento en colección "periodos_academicos"</p>
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>El período debe estar activo al momento de crear la solicitud</li>
     *   <li>La fecha de solicitud debe estar dentro de las fechas permitidas del período</li>
     * </ul>
     * 
     * @see Periodo
     */
    private String periodoId;

    /**
     * Nivel de prioridad de la solicitud para ordenamiento de atención.
     * <p>Valor numérico que determina la urgencia con la que debe atenderse la solicitud:</p>
     * <ul>
     *   <li><b>1-3:</b> Alta prioridad (casos urgentes, necesidades especiales)</li>
     *   <li><b>4-7:</b> Prioridad normal (solicitudes estándar)</li>
     *   <li><b>8-10:</b> Baja prioridad (solicitudes no urgentes)</li>
     * </ul>
     * <p>Las solicitudes se atienden en orden de prioridad (menor número = mayor prioridad)
     * y luego por fecha de solicitud.</p>
     * <p><b>Por defecto:</b> 5 (prioridad normal)</p>
     */
    private int prioridad;

    /**
     * Fecha y hora de creación de la solicitud.
     * <p>Campo de auditoría gestionado automáticamente por Spring Data MongoDB.
     * Registra el momento exacto en que el estudiante creó la solicitud.</p>
     * <p><b>Formato:</b> Instant UTC (ISO-8601)</p>
     * <p><b>Ejemplo:</b> "2024-06-15T14:30:00Z"</p>
     * <p>Se usa para ordenar solicitudes por antigüedad y calcular tiempos de respuesta.</p>
     */
    @CreatedDate
    @Field("fecha_solicitud")
    private Instant fechaSolicitud;

    /**
     * Fecha límite para que la coordinación responda a la solicitud.
     * <p>Fecha máxima establecida para procesar la solicitud. Si se excede este plazo,
     * el sistema puede generar alertas o aplicar políticas automáticas.</p>
     * <p><b>Formato:</b> Instant UTC (ISO-8601)</p>
     * <p><b>Cálculo típico:</b> fechaSolicitud + 7 días (configurable por período)</p>
     * <p><b>Ejemplo:</b> Si la solicitud se crea el 15/06, la fecha límite sería el 22/06</p>
     */
    @Field("fecha_limite_respuesta")
    private Instant fechaLimiteRespuesta;

    /**
     * Fecha y hora de la última actualización de la solicitud.
     * <p>Campo de auditoría gestionado automáticamente por Spring Data MongoDB.
     * Se actualiza cada vez que se modifica cualquier campo de la solicitud, incluyendo
     * cambios de estado, actualizaciones de descripción, o adición de observaciones.</p>
     * <p><b>Formato:</b> Instant UTC (ISO-8601)</p>
     * <p>Útil para detectar cuándo fue la última interacción con la solicitud.</p>
     */
    @Field("fecha_actualizacion")
    @LastModifiedDate
    private Instant fechaActualizacion;

    /**
     * Historial completo de cambios y eventos de la solicitud.
     * <p>Lista ordenada cronológicamente de todas las modificaciones realizadas a la solicitud.
     * Cada entrada registra:</p>
     * <ul>
     *   <li>Fecha y hora del cambio</li>
     *   <li>Estado anterior y nuevo estado</li>
     *   <li>Usuario que realizó el cambio</li>
     *   <li>Comentarios o notas del cambio</li>
     * </ul>
     * <p>Esto proporciona trazabilidad completa del proceso de aprobación y permite
     * auditar quién tomó qué decisiones y cuándo.</p>
     * <p><b>Por defecto:</b> Lista vacía inicializada automáticamente por Lombok @Builder.Default</p>
     * 
     * @see SolicitudHistorialEntry
     */
    @Builder.Default
    private List<SolicitudHistorialEntry> historial = new ArrayList<>();

    /**
     * Agrega una nueva entrada al historial de la solicitud.
     * <p>Método de conveniencia para registrar eventos en el historial. Cada vez que se
     * modifica el estado o se agrega información relevante, se debe agregar una entrada
     * al historial para mantener la trazabilidad.</p>
     * 
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * SolicitudHistorialEntry entrada = SolicitudHistorialEntry.builder()
     *     .fecha(Instant.now())
     *     .estadoAnterior(SolicitudEstado.PENDIENTE)
     *     .estadoNuevo(SolicitudEstado.EN_REVISION)
     *     .usuario("coordinador@escuelaing.edu.co")
     *     .comentario("Solicitud tomada para revisión")
     *     .build();
     * 
     * solicitud.agregarEventoHistorial(entrada);
     * </pre>
     * 
     * @param evento La entrada de historial a agregar, conteniendo fecha, estados, usuario y comentario.
     * 
     * @see SolicitudHistorialEntry
     */
    public void agregarEventoHistorial(SolicitudHistorialEntry evento) {
        historial.add(evento);
    }
}