package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;
import edu.dosw.sirha.service.PeriodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * Controlador REST para la gestión de períodos académicos en el sistema SIRHA.
 * 
 * <p>Este controlador proporciona los endpoints necesarios para administrar los períodos académicos
 * (semestres) de la institución. Los períodos académicos son fundamentales en el sistema SIRHA
 * porque definen las ventanas de tiempo en las que los estudiantes pueden realizar solicitudes
 * de cambio de materia o grupo.</p>
 * 
 * <h2>Concepto de Período Académico:</h2>
 * <p>Un período académico representa un ciclo lectivo (generalmente un semestre) con fechas
 * de inicio y fin específicas. Durante estas fechas, el sistema permite la gestión de solicitudes
 * de cambio de horario.</p>
 * 
 * <h2>Información de Período:</h2>
 * <ul>
 *   <li><b>Nombre:</b> Identificación del período (ej: "2024-1", "2024-2")</li>
 *   <li><b>Fecha de inicio:</b> Cuándo comienza el período académico</li>
 *   <li><b>Fecha de fin:</b> Cuándo finaliza el período académico</li>
 *   <li><b>Fecha inicio solicitudes:</b> Cuándo se abren las solicitudes de cambio</li>
 *   <li><b>Fecha fin solicitudes:</b> Cuándo se cierran las solicitudes de cambio</li>
 *   <li><b>Estado activo:</b> Indica si es el período académico actual</li>
 * </ul>
 * 
 * <h2>Concepto de Período Activo:</h2>
 * <p>Solo puede existir un período activo a la vez. El período activo es el que actualmente
 * está vigente y en el cual se permiten operaciones de gestión de horarios. Cuando se marca
 * un período como activo, automáticamente se desactivan todos los demás períodos.</p>
 * 
 * <h2>Operaciones Principales:</h2>
 * <ol>
 *   <li><b>Crear período:</b> Registrar nuevo período académico con sus fechas límite</li>
 *   <li><b>Actualizar período:</b> Modificar fechas o información del período</li>
 *   <li><b>Activar período:</b> Marcar un período como activo (desactiva los demás)</li>
 *   <li><b>Consultar período activo:</b> Obtener el período académico en curso</li>
 *   <li><b>Validar vigencia:</b> Verificar si una fecha está dentro del período de solicitudes</li>
 * </ol>
 * 
 * <h2>Flujo de Gestión de Períodos:</h2>
 * <pre>
 * 1. Administrador crea nuevo período (POST /api/periodos) → Estado: inactivo
 * 2. Configura fechas de inicio/fin de solicitudes
 * 3. Al comenzar semestre → Activa el período (POST /api/periodos/{id}/activar)
 * 4. Sistema valida que todas las solicitudes estén dentro del rango de fechas
 * 5. Al finalizar semestre → Se crea y activa el siguiente período
 * </pre>
 * 
 * <h2>Validaciones de Negocio:</h2>
 * <ul>
 *   <li>La fecha de fin debe ser posterior a la fecha de inicio</li>
 *   <li>El período de solicitudes debe estar dentro del período académico</li>
 *   <li>No puede haber solapamiento de períodos activos</li>
 *   <li>Solo un período puede estar activo a la vez</li>
 *   <li>No se pueden eliminar períodos con solicitudes asociadas</li>
 * </ul>
 * 
 * <h2>Seguridad y Permisos:</h2>
 * <ul>
 *   <li><b>Consulta:</b> Todos los usuarios autenticados pueden consultar períodos</li>
 *   <li><b>Creación/Actualización/Activación:</b> Solo administradores</li>
 *   <li><b>Eliminación:</b> Solo administradores (si no hay dependencias)</li>
 * </ul>
 * 
 * <p><b>Ejemplo de creación de período académico:</b></p>
 * <pre>
 * POST /api/periodos
 * Authorization: Bearer eyJhbGc...
 * Content-Type: application/json
 * 
 * {
 *   "nombre": "2024-2",
 *   "fechaInicio": "2024-08-01T00:00:00Z",
 *   "fechaFin": "2024-12-15T23:59:59Z",
 *   "fechaInicioSolicitudes": "2024-07-15T00:00:00Z",
 *   "fechaFinSolicitudes": "2024-08-31T23:59:59Z"
 * }
 * 
 * Respuesta (201 CREATED):
 * {
 *   "id": "6661bd4e5678901234ef0123",
 *   "nombre": "2024-2",
 *   "fechaInicio": "2024-08-01T00:00:00Z",
 *   "fechaFin": "2024-12-15T23:59:59Z",
 *   "fechaInicioSolicitudes": "2024-07-15T00:00:00Z",
 *   "fechaFinSolicitudes": "2024-08-31T23:59:59Z",
 *   "activo": false
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see PeriodoRequest
 * @see PeriodoResponse
 * @see PeriodoService
 */
@RestController
@RequestMapping("/api/periodos")
@Validated
@RequiredArgsConstructor
public class PeriodoController {

    /**
     * Servicio de lógica de negocio para gestión de períodos académicos.
     * Contiene las operaciones CRUD y validaciones relacionadas con períodos.
     */
    private final PeriodoService periodoService;

    /**
     * Crea un nuevo período académico en el sistema.
     * 
     * <p>Este endpoint permite registrar un nuevo período académico (semestre) con sus fechas
     * de inicio, fin y ventana de solicitudes. El período se crea en estado inactivo por defecto;
     * debe activarse explícitamente mediante el endpoint {@code /api/periodos/{id}/activar}.</p>
     * 
     * <h3>Validaciones Aplicadas:</h3>
     * <ul>
     *   <li><b>Nombre único:</b> No puede haber dos períodos con el mismo nombre</li>
     *   <li><b>Fechas coherentes:</b> fechaFin debe ser posterior a fechaInicio</li>
     *   <li><b>Ventana de solicitudes válida:</b> El período de solicitudes debe estar dentro del período académico</li>
     *   <li><b>No solapamiento:</b> Las fechas no deben solaparse con otros períodos activos</li>
     * </ul>
     * 
     * @param request Objeto {@link PeriodoRequest} con los datos del nuevo período.
     * 
     * @return {@link ResponseEntity} con status 201 CREATED y body {@link PeriodoResponse}
     *         conteniendo el período creado con su ID generado.
     * 
     * @throws edu.dosw.sirha.exception.BusinessException Si las fechas son inconsistentes o el nombre ya existe.
     */
    @PostMapping
    public ResponseEntity<PeriodoResponse> create(@Valid @RequestBody PeriodoRequest request) {
        PeriodoResponse response = periodoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza la información de un período académico existente.
     * 
     * <p>Permite modificar las fechas y configuración de un período académico. Es útil para
     * extender plazos de solicitudes, corregir fechas o ajustar la configuración del período.</p>
     * 
     * <h3>Campos Actualizables:</h3>
     * <ul>
     *   <li>Nombre del período</li>
     *   <li>Fechas de inicio y fin del período académico</li>
     *   <li>Fechas de inicio y fin de la ventana de solicitudes</li>
     * </ul>
     * 
     * <p><b>Restricciones:</b></p>
     * <ul>
     *   <li>No se puede cambiar el estado activo mediante este endpoint (usar {@code /activar})</li>
     *   <li>Las validaciones de fechas coherentes se aplican también en actualización</li>
     *   <li>No se puede modificar un período si tiene solicitudes procesadas (evitar inconsistencias)</li>
     * </ul>
     * 
     * @param id ID del período a actualizar (formato ObjectId de MongoDB).
     * @param request Objeto {@link PeriodoRequest} con los nuevos datos del período.
     * 
     * @return {@link PeriodoResponse} con la información actualizada del período.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si el período no existe.
     * @throws edu.dosw.sirha.exception.BusinessException Si las nuevas fechas son inconsistentes.
     */
    @PutMapping("/{id}")
    public PeriodoResponse update(@PathVariable String id, @Valid @RequestBody PeriodoRequest request) {
        return periodoService.update(id, request);
    }

    /**
     * Elimina un período académico del sistema.
     * 
     * <p>Este endpoint permite eliminar períodos académicos. La eliminación solo es posible
     * si el período no tiene dependencias activas (solicitudes asociadas, grupos creados, etc.).</p>
     * 
     * <h3>Restricciones de Eliminación:</h3>
     * <ul>
     *   <li>No se puede eliminar el período activo (primero desactivarlo)</li>
     *   <li>No se puede eliminar si tiene solicitudes asociadas</li>
     *   <li>No se puede eliminar si tiene grupos académicos creados</li>
     *   <li>Solo administradores pueden eliminar períodos</li>
     * </ul>
     * 
     * <p><b>Recomendación:</b> En lugar de eliminar, considerar mantener todos los períodos
     * históricos para trazabilidad académica y generación de reportes históricos.</p>
     * 
     * @param id ID del período a eliminar (formato ObjectId de MongoDB).
     * 
     * @return {@link ResponseEntity} con status 204 NO CONTENT si la eliminación fue exitosa.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si el período no existe.
     * @throws edu.dosw.sirha.exception.BusinessException Si el período tiene dependencias.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        periodoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene la lista completa de todos los períodos académicos registrados.
     * 
     * <p>Retorna todos los períodos del sistema ordenados por fecha de inicio descendente
     * (más recientes primero), permitiendo visualizar el histórico completo de períodos académicos.</p>
     * 
     * <h3>Usos Comunes:</h3>
     * <ul>
     *   <li><b>Administración:</b> Gestión de catálogo de períodos académicos</li>
     *   <li><b>Reportes históricos:</b> Análisis de solicitudes por período</li>
     *   <li><b>Selección de período:</b> Dropdown para filtrar información por período</li>
     * </ul>
     * 
     * @return Lista de {@link PeriodoResponse} con todos los períodos del sistema.
     *         Retorna lista vacía si no hay períodos registrados.
     */
    @GetMapping
    public List<PeriodoResponse> findAll() {
        return periodoService.findAll();
    }

    /**
     * Obtiene los detalles de un período académico específico por su ID.
     * 
     * <p>Retorna la información completa de un período incluyendo todas sus fechas,
     * nombre y estado de activación.</p>
     * 
     * @param id ID del período a consultar (formato ObjectId de MongoDB).
     * 
     * @return {@link PeriodoResponse} con los datos completos del período.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si el período no existe.
     */
    @GetMapping("/{id}")
    public PeriodoResponse findById(@PathVariable String id) {
        return periodoService.findById(id);
    }

    /**
     * Obtiene el período académico actualmente activo en el sistema.
     * 
     * <p>Este es uno de los endpoints más importantes del sistema, ya que muchas operaciones
     * dependen de conocer cuál es el período académico actual. El sistema solo permite un
     * período activo a la vez.</p>
     * 
     * <h3>Casos de Uso:</h3>
     * <ul>
     *   <li><b>Validación de solicitudes:</b> Verificar que se esté dentro del período de solicitudes activo</li>
     *   <li><b>Creación de grupos:</b> Los grupos se crean para el período activo</li>
     *   <li><b>Dashboard:</b> Mostrar información del período académico en curso</li>
     *   <li><b>Inscripciones:</b> Determinar qué materias y grupos están disponibles</li>
     * </ul>
     * 
     * <h3>Respuestas Posibles:</h3>
     * <ul>
     *   <li><b>200 OK:</b> Retorna el período activo con su información completa</li>
     *   <li><b>204 NO CONTENT:</b> No hay período activo configurado (situación transitoria)</li>
     * </ul>
     * 
     * <p><b>Nota:</b> Si no hay período activo, las operaciones de gestión de solicitudes
     * estarán bloqueadas hasta que un administrador active un período.</p>
     * 
     * @return {@link ResponseEntity} con {@link PeriodoResponse} del período activo (200 OK),
     *         o status 204 NO CONTENT si no hay período activo.
     */
    @GetMapping("/activo")
    public ResponseEntity<PeriodoResponse> findActive() {
        PeriodoResponse response = periodoService.findActive();
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Marca un período académico como activo y desactiva todos los demás.
     * 
     * <p>Este endpoint es crítico para la gestión del ciclo académico. Al activar un período,
     * automáticamente se desactivan todos los demás períodos del sistema, garantizando que
     * solo exista un período activo a la vez.</p>
     * 
     * <h3>Proceso de Activación:</h3>
     * <ol>
     *   <li>Se busca el período por ID</li>
     *   <li>Se verifica que el período exista y esté en estado inactivo</li>
     *   <li>Se desactivan todos los períodos actualmente activos</li>
     *   <li>Se marca el período especificado como activo</li>
     *   <li>Se persisten los cambios en base de datos</li>
     * </ol>
     * 
     * <h3>Implicaciones de Activar un Período:</h3>
     * <ul>
     *   <li><b>Ventana de solicitudes:</b> Se habilita/deshabilita según fechas del nuevo período activo</li>
     *   <li><b>Grupos disponibles:</b> Se filtran grupos del período activo</li>
     *   <li><b>Validaciones:</b> Las reglas de negocio se aplican según el período activo</li>
     *   <li><b>Reportes:</b> Los dashboards muestran información del período activo</li>
     * </ul>
     * 
     * <p><b>Ejemplo de flujo de cambio de semestre:</b></p>
     * <pre>
     * 1. Finaliza semestre 2024-1 (activo)
     * 2. Administrador crea período 2024-2 (inactivo)
     * 3. POST /api/periodos/{id-2024-2}/activar
     * 4. Sistema desactiva 2024-1 y activa 2024-2
     * 5. Estudiantes ahora pueden hacer solicitudes para 2024-2
     * </pre>
     * 
     * <p><b>Restricciones:</b></p>
     * <ul>
     *   <li>Solo administradores pueden activar períodos</li>
     *   <li>El período debe existir en el sistema</li>
     *   <li>Se recomienda activar solo cuando se esté listo para recibir solicitudes</li>
     * </ul>
     * 
     * @param id ID del período a marcar como activo (formato ObjectId de MongoDB).
     * 
     * @return {@link PeriodoResponse} con el período recién activado (campo {@code activo = true}).
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si el período no existe.
     */
    @PostMapping("/{id}/activar")
    public PeriodoResponse markAsActive(@PathVariable String id) {
        return periodoService.markAsActive(id);
    }

    /**
     * Verifica si una fecha específica está dentro del período de solicitudes activo.
     * 
     * <p>Este endpoint es fundamental para validaciones de negocio. Permite determinar si una fecha
     * dada está dentro de la ventana de tiempo en la que se permiten solicitudes de cambio de materia
     * o grupo en el período activo.</p>
     * 
     * <h3>Criterios de Validación:</h3>
     * <ul>
     *   <li>Existe un período activo en el sistema</li>
     *   <li>La fecha está entre {@code fechaInicioSolicitudes} y {@code fechaFinSolicitudes} del período activo</li>
     *   <li>Se compara usando UTC para evitar problemas de zona horaria</li>
     * </ul>
     * 
     * <h3>Casos de Uso:</h3>
     * <ul>
     *   <li><b>Crear solicitud:</b> Validar que el estudiante esté dentro del período permitido</li>
     *   <li><b>Aprobar solicitud:</b> Verificar que la aprobación se haga dentro del período</li>
     *   <li><b>UI/UX:</b> Mostrar/ocultar botones según si el período está vigente</li>
     *   <li><b>Notificaciones:</b> Alertar cuando se aproxima el cierre del período de solicitudes</li>
     * </ul>
     * 
     * <p><b>Ejemplos de validación:</b></p>
     * <pre>
     * Período activo: 2024-2
     * fechaInicioSolicitudes: 2024-07-15T00:00:00Z
     * fechaFinSolicitudes: 2024-08-31T23:59:59Z
     * 
     * GET /api/periodos/vigencia?fecha=2024-08-15T10:00:00Z
     * → Respuesta: true (está dentro del rango)
     * 
     * GET /api/periodos/vigencia?fecha=2024-09-05T10:00:00Z
     * → Respuesta: false (ya pasó la fecha de fin)
     * 
     * GET /api/periodos/vigencia?fecha=2024-07-01T10:00:00Z
     * → Respuesta: false (aún no ha iniciado el período)
     * </pre>
     * 
     * <p><b>Nota sobre formato de fecha:</b> El parámetro {@code fecha} debe enviarse en formato ISO-8601
     * con zona horaria (ej: "2024-08-15T10:30:00Z" o "2024-08-15T10:30:00-05:00"). Si no se especifica
     * zona horaria, se asume UTC.</p>
     * 
     * @param fecha Fecha a validar en formato ISO-8601 (ej: "2024-08-15T10:30:00Z").
     *              Debe ser un {@link Instant} para garantizar precisión temporal con UTC.
     * 
     * @return {@link ResponseEntity} con {@code true} si la fecha está dentro del período de solicitudes
     *         activo, {@code false} en caso contrario o si no hay período activo.
     */
    @GetMapping("/vigencia")
    public ResponseEntity<Boolean> isWithinPeriodo(
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fecha) {
        return ResponseEntity.ok(periodoService.isWithinPeriodo(fecha));
    }
}