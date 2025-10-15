
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

@RestController
@RequestMapping("/api/solicitudes")
@Validated
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "API para gestión de solicitudes de cambio de horarios")
@SecurityRequirement(name = "JWT")
public class SolicitudController {

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
