
package edu.dosw.sirha.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.sirha.dto.request.SolicitudEstadoChangeRequest;
import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.service.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de solicitudes de cambio de materia o grupo en el sistema SIRHA.
 * 
 * <p>Este controlador es el núcleo del sistema SIRHA (Sistema Integral de Registro de Horarios Académicos).
 * Proporciona los endpoints necesarios para que estudiantes y administrativos gestionen solicitudes de
 * cambio de materia inscrita o cambio de grupo dentro de la misma materia.</p>
 * 
 * <h2>Tipos de Solicitudes:</h2>
 * <ul>
 *   <li><b>CAMBIO_MATERIA:</b> El estudiante solicita cambiar una materia por otra completamente diferente</li>
 *   <li><b>CAMBIO_GRUPO:</b> El estudiante solicita cambiar de grupo (horario) dentro de la misma materia</li>
 * </ul>
 * 
 * <h2>Estados de Solicitud:</h2>
 * <ol>
 *   <li><b>PENDIENTE:</b> Solicitud creada, esperando revisión de decanatura</li>
 *   <li><b>EN_REVISION:</b> La decanatura está analizando la solicitud</li>
 *   <li><b>INFORMACION_ADICIONAL:</b> Se requiere información extra del estudiante</li>
 *   <li><b>APROBADA:</b> Solicitud aprobada, cambio aplicado automáticamente al sistema académico</li>
 *   <li><b>RECHAZADA:</b> Solicitud rechazada con observaciones explicativas</li>
 * </ol>
 * 
 * <h2>Flujo Completo de Solicitud:</h2>
 * <pre>
 * 1. Estudiante crea solicitud (POST /api/solicitudes)
 *    ↓ Estado: PENDIENTE
 * 2. Sistema valida automáticamente:
 *    - Período académico activo y dentro del rango de fechas
 *    - Cupos disponibles en grupo solicitado
 *    - No conflictos de horario con otras materias
 *    - Estudiante inscrito en grupo actual (si es cambio de grupo)
 *    ↓
 * 3. Decanatura revisa solicitud (PATCH /api/solicitudes/{id}/estado)
 *    ↓ Estado: EN_REVISION
 * 4. Según análisis:
 *    a) Requiere más info → Estado: INFORMACION_ADICIONAL
 *       → Estudiante actualiza (PUT /api/solicitudes/{id})
 *       → Vuelve a PENDIENTE
 *    b) Aprueba → Estado: APROBADA
 *       → Sistema aplica cambio en inscripciones
 *    c) Rechaza → Estado: RECHAZADA
 *       → Se registran observaciones del rechazo
 * </pre>
 * 
 * <h2>Validaciones Automáticas del Sistema:</h2>
 * <ul>
 *   <li><b>Período activo:</b> Solo se permiten solicitudes dentro del período académico activo</li>
 *   <li><b>Rango de fechas:</b> La solicitud debe hacerse entre fechaInicioSolicitudes y fechaFinSolicitudes</li>
 *   <li><b>Cupos disponibles:</b> El grupo solicitado debe tener cupos disponibles</li>
 *   <li><b>Conflictos de horario:</b> No puede haber cruce de horarios con materias ya inscritas</li>
 *   <li><b>Inscripción previa:</b> Para cambio de grupo, el estudiante debe estar inscrito en la materia</li>
 *   <li><b>Prerrequisitos:</b> Para cambio de materia, debe cumplir prerrequisitos</li>
 * </ul>
 * 
 * <h2>Roles y Permisos:</h2>
 * <ul>
 *   <li><b>ESTUDIANTE:</b>
 *     <ul>
 *       <li>Crear solicitudes propias</li>
 *       <li>Consultar sus propias solicitudes</li>
 *       <li>Actualizar solicitudes en estado PENDIENTE o INFORMACION_ADICIONAL</li>
 *       <li>Eliminar solicitudes propias en estado PENDIENTE</li>
 *     </ul>
 *   </li>
 *   <li><b>DECANATURA:</b>
 *     <ul>
 *       <li>Consultar todas las solicitudes de su facultad</li>
 *       <li>Cambiar estado de solicitudes (aprobar, rechazar, solicitar info)</li>
 *       <li>Agregar observaciones a solicitudes</li>
 *     </ul>
 *   </li>
 *   <li><b>ADMIN:</b>
 *     <ul>
 *       <li>Acceso completo a todas las operaciones</li>
 *       <li>Gestión global de solicitudes de todas las facultades</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Integración con Otros Módulos:</h2>
 * <pre>
 * SolicitudController ──┬──> PeriodoService (validar período activo)
 *                       ├──> ConflictDetectionService (detectar conflictos)
 *                       ├──> MateriaService (validar materias y prerrequisitos)
 *                       ├──> GrupoService (validar cupos)
 *                       └──> InscripcionService (aplicar cambios al aprobar)
 * </pre>
 * 
 * <h2>Documentación OpenAPI:</h2>
 * <p>Este controlador está completamente documentado con anotaciones Swagger/OpenAPI 3.0,
 * proporcionando especificación detallada de cada endpoint, parámetros, respuestas y
 * códigos de estado. La documentación interactiva está disponible en {@code /swagger-ui/index.html}.</p>
 * 
 * <p><b>Ejemplo de creación de solicitud de cambio de grupo:</b></p>
 * <pre>
 * POST /api/solicitudes
 * Authorization: Bearer eyJhbGc...
 * Content-Type: application/json
 * 
 * {
 *   "estudianteId": "665d7f9a1234567890abcdef",
 *   "periodoId": "665e8a1b2345678901bcdef0",
 *   "tipoSolicitud": "CAMBIO_GRUPO",
 *   "materiaId": "665f9b2c3456789012cdef01",
 *   "grupoActualId": "6660ac3d4567890123def012",
 *   "grupoSolicitadoId": "6661bd4e5678901234ef0123",
 *   "justificacion": "Conflicto de horario con trabajo de medio tiempo"
 * }
 * 
 * Respuesta (201 CREATED):
 * {
 *   "id": "6662ce5f6789012345f01234",
 *   "estudianteId": "665d7f9a1234567890abcdef",
 *   "periodoId": "665e8a1b2345678901bcdef0",
 *   "tipoSolicitud": "CAMBIO_GRUPO",
 *   "estado": "PENDIENTE",
 *   "materiaId": "665f9b2c3456789012cdef01",
 *   "grupoActualId": "6660ac3d4567890123def012",
 *   "grupoSolicitadoId": "6661bd4e5678901234ef0123",
 *   "justificacion": "Conflicto de horario con trabajo de medio tiempo",
 *   "fechaSolicitud": "2024-06-15T14:30:00Z",
 *   "conflictos": []
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see SolicitudRequest
 * @see SolicitudResponse
 * @see SolicitudService
 * @see edu.dosw.sirha.model.enums.SolicitudEstado
 * @see edu.dosw.sirha.model.enums.TipoSolicitud
 */
@RestController
@RequestMapping("/api/solicitudes")
@Validated
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "API para gestión de solicitudes de cambio de horarios")
@SecurityRequirement(name = "JWT")
public class SolicitudController {

	/**
	 * Servicio de lógica de negocio para gestión de solicitudes.
	 * Contiene las operaciones CRUD, validaciones y flujos de aprobación/rechazo.
	 */
	private final SolicitudService solicitudService;

	@PostMapping
	@Operation(summary = "Crear nueva solicitud", 
			   description = "Crea una nueva solicitud de cambio de materia o grupo. Valida que esté en período activo y que haya cupos disponibles.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente"),
		@ApiResponse(responseCode = "400", description = "Error de validación o regla de negocio", 
					content = @Content(schema = @Schema(ref = "#/components/responses/BadRequest"))),
		@ApiResponse(responseCode = "401", description = "No autorizado", 
					content = @Content(schema = @Schema(ref = "#/components/responses/Unauthorized"))),
		@ApiResponse(responseCode = "403", description = "Acceso prohibido", 
					content = @Content(schema = @Schema(ref = "#/components/responses/Forbidden")))
	})
	public ResponseEntity<SolicitudResponse> create(@Valid @RequestBody SolicitudRequest request) {
		SolicitudResponse response = solicitudService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar solicitud", 
			   description = "Actualiza una solicitud existente. Solo permitido en estados PENDIENTE o INFORMACION_ADICIONAL.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Solicitud actualizada exitosamente"),
		@ApiResponse(responseCode = "400", description = "Error de validación o estado no permitido", 
					content = @Content(schema = @Schema(ref = "#/components/responses/BadRequest"))),
		@ApiResponse(responseCode = "404", description = "Solicitud no encontrada", 
					content = @Content(schema = @Schema(ref = "#/components/responses/NotFound")))
	})
	public SolicitudResponse update(
			@Parameter(description = "ID de la solicitud a actualizar") @PathVariable String id, 
			@Valid @RequestBody SolicitudRequest request) {
		return solicitudService.update(id, request);
	}

	@PatchMapping("/{id}/estado")
	@Operation(summary = "Cambiar estado de solicitud", 
			   description = "Cambia el estado de una solicitud. Solo usuarios con permisos de decanatura pueden aprobar/rechazar.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente"),
		@ApiResponse(responseCode = "400", description = "Estado inválido o transición no permitida", 
					content = @Content(schema = @Schema(ref = "#/components/responses/BadRequest"))),
		@ApiResponse(responseCode = "404", description = "Solicitud no encontrada", 
					content = @Content(schema = @Schema(ref = "#/components/responses/NotFound")))
	})
	public SolicitudResponse changeEstado(
			@Parameter(description = "ID de la solicitud") @PathVariable String id,
			@Valid @RequestBody SolicitudEstadoChangeRequest request) {
		return solicitudService.changeEstado(id, request.getEstado(), request.getObservaciones());
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar solicitud", 
			   description = "Elimina una solicitud. Solo permitido en estado PENDIENTE.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Solicitud eliminada exitosamente"),
		@ApiResponse(responseCode = "400", description = "No se puede eliminar en el estado actual", 
					content = @Content(schema = @Schema(ref = "#/components/responses/BadRequest"))),
		@ApiResponse(responseCode = "404", description = "Solicitud no encontrada", 
					content = @Content(schema = @Schema(ref = "#/components/responses/NotFound")))
	})
	public ResponseEntity<Void> delete(
			@Parameter(description = "ID de la solicitud a eliminar") @PathVariable String id) {
		solicitudService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	@Operation(summary = "Listar todas las solicitudes", 
			   description = "Obtiene todas las solicitudes del sistema. Requiere permisos de administrador.")
	@ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida exitosamente")
	public List<SolicitudResponse> findAll() {
		return solicitudService.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener solicitud por ID", 
			   description = "Obtiene los detalles de una solicitud específica.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
		@ApiResponse(responseCode = "404", description = "Solicitud no encontrada", 
					content = @Content(schema = @Schema(ref = "#/components/responses/NotFound")))
	})
	public SolicitudResponse findById(
			@Parameter(description = "ID de la solicitud") @PathVariable String id) {
		return solicitudService.findById(id);
	}

	@GetMapping("/estudiante/{estudianteId}")
	@Operation(summary = "Listar solicitudes por estudiante", 
			   description = "Obtiene todas las solicitudes de un estudiante específico, ordenadas por fecha descendente.")
	@ApiResponse(responseCode = "200", description = "Lista de solicitudes del estudiante")
	public List<SolicitudResponse> findByEstudiante(
			@Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
		return solicitudService.findByEstudiante(estudianteId);
	}

	@GetMapping("/estados")
	@Operation(summary = "Listar solicitudes por estados", 
			   description = "Obtiene solicitudes filtradas por uno o más estados, ordenadas por prioridad.")
	@ApiResponse(responseCode = "200", description = "Lista de solicitudes filtradas por estado")
	public List<SolicitudResponse> findByEstados(
			@Parameter(description = "Estados a filtrar (opcional)") 
			@RequestParam(name = "estado", required = false) List<SolicitudEstado> estados) {
		return solicitudService.findByEstados(estados);
	}

	@GetMapping("/estados/{estado}/conteo")
	@Operation(summary = "Contar solicitudes por estado", 
			   description = "Obtiene el número total de solicitudes en un estado específico.")
	@ApiResponse(responseCode = "200", description = "Conteo de solicitudes")
	public long countByEstado(
			@Parameter(description = "Estado a contar") @PathVariable SolicitudEstado estado) {
		return solicitudService.countByEstado(estado);
	}

	@GetMapping("/periodo/{periodoId}")
	@Operation(summary = "Listar solicitudes por período y rango de fechas", 
			   description = "Obtiene solicitudes de un período académico específico, opcionalmente filtradas por rango de fechas.")
	@ApiResponse(responseCode = "200", description = "Lista de solicitudes del período")
	public List<SolicitudResponse> findByPeriodoAndRango(
			@Parameter(description = "ID del período académico") @PathVariable String periodoId,
			@Parameter(description = "Fecha de inicio del rango (opcional)") 
			@RequestParam(name = "inicio", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
			@Parameter(description = "Fecha de fin del rango (opcional)") 
			@RequestParam(name = "fin", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fin) {
		return solicitudService.findByPeriodoAndRango(periodoId, inicio, fin);
	}
}
