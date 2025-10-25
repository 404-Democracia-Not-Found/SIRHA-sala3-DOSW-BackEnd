package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;
import edu.dosw.sirha.service.ConflictDetectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

/**
 * Controlador REST para la gestión de conflictos académicos en el sistema SIRHA.
 * 
 * <p>Este controlador proporciona los endpoints necesarios para registrar, consultar, actualizar
 * y resolver conflictos detectados en solicitudes de cambio de materia o grupo. Los conflictos
 * pueden incluir cruces de horarios, materias ya inscritas, cupos agotados, entre otros.</p>
 * 
 * <h2>Tipos de Conflictos Detectables:</h2>
 * <ul>
 *   <li><b>CRUCE_HORARIO:</b> Dos materias tienen horarios que se solapan</li>
 *   <li><b>MATERIA_YA_INSCRITA:</b> El estudiante ya está inscrito en la materia solicitada</li>
 *   <li><b>CUPO_AGOTADO:</b> El grupo solicitado no tiene cupos disponibles</li>
 *   <li><b>PREREQUISITO_FALTANTE:</b> No se cumple con los prerrequisitos de la materia</li>
 *   <li><b>CARGA_ACADEMICA_EXCEDIDA:</b> Sobrepasa el número máximo de créditos permitidos</li>
 * </ul>
 * 
 * <h2>Funcionalidades Principales:</h2>
 * <ol>
 *   <li><b>Registro de conflictos:</b> Crear nuevos registros de conflictos detectados</li>
 *   <li><b>Actualización:</b> Modificar información de conflictos existentes</li>
 *   <li><b>Resolución:</b> Marcar conflictos como resueltos con observaciones</li>
 *   <li><b>Consulta:</b> Obtener conflictos por ID, estudiante o solicitud</li>
 *   <li><b>Eliminación:</b> Eliminar registros de conflictos (solo administrativo)</li>
 * </ol>
 * 
 * <h2>Flujo de Gestión de Conflictos:</h2>
 * <pre>
 * 1. Sistema detecta conflicto en solicitud → Registra conflicto (POST /api/conflictos)
 * 2. Decanatura revisa conflicto → Consulta detalles (GET /api/conflictos/{id})
 * 3. Se realiza corrección → Actualiza conflicto (PUT /api/conflictos/{id})
 * 4. Conflicto solucionado → Marca como resuelto (POST /api/conflictos/{id}/resolver)
 * </pre>
 * 
 * <h2>Seguridad y Permisos:</h2>
 * <ul>
 *   <li><b>Consulta de conflictos propios:</b> Estudiantes pueden ver sus conflictos</li>
 *   <li><b>Gestión completa:</b> Decanatura y administradores pueden gestionar todos los conflictos</li>
 *   <li><b>Autenticación JWT:</b> Todos los endpoints requieren token válido</li>
 * </ul>
 * 
 * <h2>Validaciones:</h2>
 * <ul>
 *   <li>Los datos de entrada se validan automáticamente con {@code @Valid}</li>
 *   <li>Se verifica la existencia de estudiantes, solicitudes y grupos referenciados</li>
 *   <li>Se valida que los conflictos pertenezcan al usuario que los consulta (excepto admin/decanatura)</li>
 * </ul>
 * 
 * <p><b>Ejemplo de registro de conflicto de cruce de horario:</b></p>
 * <pre>
 * POST /api/conflictos
 * Authorization: Bearer eyJhbGc...
 * Content-Type: application/json
 * 
 * {
 *   "estudianteId": "665d7f9a1234567890abcdef",
 *   "solicitudId": "665e8a1b2345678901bcdef0",
 *   "tipoConflicto": "CRUCE_HORARIO",
 *   "descripcion": "Cruce entre Cálculo II (L-M 08:00-10:00) y Física I (M 09:00-11:00)",
 *   "severidad": "ALTA",
 *   "grupoActualId": "665f9b2c3456789012cdef01",
 *   "grupoSolicitadoId": "6660ac3d4567890123def012"
 * }
 * 
 * Respuesta (201 CREATED):
 * {
 *   "id": "6661bd4e5678901234ef0123",
 *   "estudianteId": "665d7f9a1234567890abcdef",
 *   "solicitudId": "665e8a1b2345678901bcdef0",
 *   "tipoConflicto": "CRUCE_HORARIO",
 *   "descripcion": "Cruce entre Cálculo II (L-M 08:00-10:00) y Física I (M 09:00-11:00)",
 *   "severidad": "ALTA",
 *   "resuelto": false,
 *   "fechaDeteccion": "2024-06-15T14:30:00Z"
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see ConflictRequest
 * @see ConflictResponse
 * @see ConflictDetectionService
 */
@RestController
@RequestMapping("/api/conflictos")
@Validated
@RequiredArgsConstructor
public class ConflictController {

    /**
     * Servicio de detección y gestión de conflictos académicos.
     * Contiene la lógica de negocio para validar, registrar y resolver conflictos.
     */
    private final ConflictDetectionService conflictDetectionService;

    /**
     * Registra un nuevo conflicto académico en el sistema.
     * 
     * <p>Este endpoint permite crear un registro de conflicto cuando se detecta una situación
     * problemática en una solicitud de cambio de materia o grupo. El conflicto se persiste
     * en la base de datos con estado "no resuelto" por defecto.</p>
     * 
     * <h3>Validaciones Realizadas:</h3>
     * <ul>
     *   <li>El estudiante referenciado debe existir en el sistema</li>
     *   <li>La solicitud referenciada debe existir y pertenecer al estudiante</li>
     *   <li>El tipo de conflicto debe ser uno de los enumerados válidos</li>
     *   <li>La severidad debe ser BAJA, MEDIA o ALTA</li>
     *   <li>Grupos referenciados (si aplica) deben existir</li>
     * </ul>
     * 
     * @param request Objeto {@link ConflictRequest} con los datos del conflicto a registrar.
     *                Incluye estudianteId, solicitudId, tipoConflicto, descripción, severidad
     *                y opcionalmente grupoActualId y grupoSolicitadoId.
     * 
     * @return {@link ResponseEntity} con status 201 CREATED y body {@link ConflictResponse}
     *         conteniendo el conflicto registrado con su ID generado y fecha de detección.
     */
    @PostMapping
    public ResponseEntity<ConflictResponse> registrar(@Valid @RequestBody ConflictRequest request) {
        ConflictResponse response = conflictDetectionService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza la información de un conflicto académico existente.
     * 
     * <p>Permite modificar los datos de un conflicto que ya ha sido registrado. Útil para
     * corregir información o agregar detalles adicionales conforme se investiga el conflicto.</p>
     * 
     * <h3>Campos Actualizables:</h3>
     * <ul>
     *   <li>Descripción del conflicto</li>
     *   <li>Severidad (puede escalar o descender según nueva información)</li>
     *   <li>Grupos referenciados</li>
     *   <li>Tipo de conflicto (si se reclasifica)</li>
     * </ul>
     * 
     * <p><b>Nota:</b> No se puede actualizar un conflicto marcado como resuelto.
     * Para cambiar el estado de resolución, usar el endpoint {@code /api/conflictos/{id}/resolver}.</p>
     * 
     * @param id ID del conflicto a actualizar (formato ObjectId de MongoDB).
     * @param request Objeto {@link ConflictRequest} con los nuevos datos del conflicto.
     * 
     * @return {@link ConflictResponse} con la información actualizada del conflicto.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si el conflicto no existe.
     * @throws edu.dosw.sirha.exception.BusinessException Si el conflicto ya está resuelto.
     */
    @PutMapping("/{id}")
    public ConflictResponse actualizar(@PathVariable String id, @Valid @RequestBody ConflictRequest request) {
        return conflictDetectionService.actualizar(id, request);
    }

    /**
     * Marca un conflicto como resuelto o no resuelto con observaciones opcionales.
     * 
     * <p>Este endpoint es utilizado por la decanatura para gestionar el ciclo de vida de los
     * conflictos. Permite marcar un conflicto como resuelto cuando se ha solucionado el problema,
     * o desmarcarlo si era una resolución prematura.</p>
     * 
     * <h3>Casos de Uso:</h3>
     * <ul>
     *   <li><b>Marcar como resuelto:</b> Cuando el estudiante ajusta su solicitud o se soluciona el conflicto</li>
     *   <li><b>Agregar observaciones:</b> Documentar las acciones tomadas para resolver el conflicto</li>
     *   <li><b>Reabrir conflicto:</b> Si se determina que la solución fue incorrecta (resuelto=false)</li>
     * </ul>
     * 
     * <h3>Seguimiento de Auditoría:</h3>
     * <p>Cada vez que se marca como resuelto, se registra la fecha de resolución y las observaciones
     * proporcionadas, permitiendo trazabilidad completa del proceso de gestión de conflictos.</p>
     * 
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * POST /api/conflictos/6661bd4e5678901234ef0123/resolver?resuelto=true&amp;observaciones=Estudiante%20cambió%20de%20grupo
     * </pre>
     * 
     * @param id ID del conflicto a resolver (formato ObjectId de MongoDB).
     * @param resuelto {@code true} para marcar como resuelto, {@code false} para reabrir. Por defecto {@code true}.
     * @param observaciones Texto opcional explicando cómo se resolvió el conflicto o por qué se reabre.
     *                      Útil para mantener histórico de gestión del conflicto.
     * 
     * @return {@link ConflictResponse} con el conflicto actualizado, incluyendo estado de resolución
     *         y fecha de resolución (si aplica).
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si el conflicto no existe.
     */
    @PostMapping("/{id}/resolver")
    public ConflictResponse resolver(@PathVariable String id,
            @RequestParam(name = "resuelto", defaultValue = "true") boolean resuelto,
            @RequestParam(name = "observaciones", required = false) String observaciones) {
        return conflictDetectionService.marcarResuelto(id, resuelto, observaciones);
    }

    /**
     * Elimina un conflicto del sistema.
     * 
     * <p>Este endpoint permite eliminar permanentemente un registro de conflicto de la base de datos.
     * Debe usarse con precaución, ya que la eliminación es irreversible.</p>
     * 
     * <h3>Restricciones:</h3>
     * <ul>
     *   <li>Solo administradores pueden eliminar conflictos</li>
     *   <li>No se recomienda eliminar conflictos ya resueltos (mejor mantener el histórico)</li>
     *   <li>Considerar marcar como resuelto en lugar de eliminar para mantener trazabilidad</li>
     * </ul>
     * 
     * @param id ID del conflicto a eliminar (formato ObjectId de MongoDB).
     * 
     * @return {@link ResponseEntity} con status 204 NO CONTENT si la eliminación fue exitosa.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si el conflicto no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        conflictDetectionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los conflictos registrados en el sistema.
     * 
     * <p>Este endpoint retorna una lista completa de todos los conflictos académicos, independientemente
     * de su estado (resueltos o no resueltos). Es útil para reportes y análisis globales.</p>
     * 
     * <h3>Permisos Requeridos:</h3>
     * <ul>
     *   <li>Administradores: Acceso completo a todos los conflictos</li>
     *   <li>Decanatura: Acceso a conflictos de su facultad</li>
     *   <li>Estudiantes: Solo pueden ver sus propios conflictos (usar /estudiante/{id})</li>
     * </ul>
     * 
     * <p><b>Nota:</b> Para grandes volúmenes de datos, considerar implementar paginación.</p>
     * 
     * @return Lista de {@link ConflictResponse} con todos los conflictos del sistema.
     *         Retorna lista vacía si no hay conflictos registrados.
     */
    @GetMapping
    public List<ConflictResponse> findAll() {
        return conflictDetectionService.findAll();
    }

    /**
     * Obtiene los detalles de un conflicto específico por su ID.
     * 
     * <p>Retorna toda la información detallada de un conflicto particular, incluyendo estudiante,
     * solicitud relacionada, tipo, severidad, estado de resolución y observaciones.</p>
     * 
     * @param id ID del conflicto a consultar (formato ObjectId de MongoDB).
     * 
     * @return {@link ConflictResponse} con los datos completos del conflicto.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si el conflicto no existe.
     */
    @GetMapping("/{id}")
    public ConflictResponse findById(@PathVariable String id) {
        return conflictDetectionService.findById(id);
    }

    /**
     * Obtiene todos los conflictos asociados a un estudiante específico.
     * 
     * <p>Este endpoint es particularmente útil para que los estudiantes puedan ver todos sus
     * conflictos académicos en un solo lugar, facilitando la gestión de sus solicitudes de cambio.</p>
     * 
     * <h3>Información Retornada:</h3>
     * <ul>
     *   <li>Conflictos activos (no resueltos)</li>
     *   <li>Conflictos históricos (resueltos)</li>
     *   <li>Ordenados por fecha de detección (más recientes primero)</li>
     * </ul>
     * 
     * <p><b>Ejemplo de uso:</b> Un estudiante puede consultar sus conflictos para saber qué
     * ajustes debe realizar en sus solicitudes de cambio de horario.</p>
     * 
     * @param estudianteId ID del estudiante (formato ObjectId de MongoDB).
     * 
     * @return Lista de {@link ConflictResponse} con todos los conflictos del estudiante.
     *         Retorna lista vacía si el estudiante no tiene conflictos.
     */
    @GetMapping("/estudiante/{estudianteId}")
    public List<ConflictResponse> findByEstudiante(@PathVariable String estudianteId) {
        return conflictDetectionService.findByEstudiante(estudianteId);
    }

    /**
     * Obtiene todos los conflictos detectados en una solicitud específica.
     * 
     * <p>Una solicitud de cambio de materia o grupo puede tener múltiples conflictos asociados
     * (por ejemplo, cruce de horario con dos materias diferentes, o cupo agotado y prerrequisito
     * faltante simultáneamente). Este endpoint permite obtener todos esos conflictos juntos.</p>
     * 
     * <h3>Casos de Uso:</h3>
     * <ul>
     *   <li><b>Revisión de solicitud:</b> Decanatura puede ver todos los problemas de una solicitud</li>
     *   <li><b>Validación automática:</b> Sistema puede verificar si una solicitud tiene conflictos sin resolver</li>
     *   <li><b>Notificaciones:</b> Enviar al estudiante lista completa de conflictos que debe solucionar</li>
     * </ul>
     * 
     * @param solicitudId ID de la solicitud (formato ObjectId de MongoDB).
     * 
     * @return Lista de {@link ConflictResponse} con todos los conflictos de la solicitud.
     *         Retorna lista vacía si la solicitud no tiene conflictos detectados.
     */
    @GetMapping("/solicitud/{solicitudId}")
    public List<ConflictResponse> findBySolicitud(@PathVariable String solicitudId) {
        return conflictDetectionService.findBySolicitud(solicitudId);

    }
}