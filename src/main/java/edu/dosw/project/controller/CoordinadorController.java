package edu.dosw.project.controller;

import edu.dosw.project.model.Solicitud;
import edu.dosw.project.service.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones específicas de COORDINADORES
 * 
 * Este controlador maneja todas las operaciones que un coordinador puede realizar:
 * - Aprobar o rechazar solicitudes de reasignación
 * - Generar reportes de solicitudes
 * - Ver conflictos de horarios en su programa académico
 * - Gestionar períodos de solicitudes
 * 
 * SEGURIDAD: Todos los endpoints requieren rol COORDINADOR
 * API REST: Sigue las convenciones REST para URLs y códigos de estado
 */
@RestController
@RequestMapping("/api/coordinadores")
@Tag(name = "COORDINADORES", description = "Operaciones disponibles para usuarios con rol COORDINADOR")
@SecurityRequirement(name = "bearerAuth")
public class CoordinadorController {

    private final SolicitudService solicitudService;

    public CoordinadorController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PutMapping("/solicitudes/{solicitudId}/aprobar")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(
        summary = "Aprobar Solicitud de Reasignación",
        description = "Permite al coordinador aprobar una solicitud de reasignación de horarios"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud aprobada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
        @ApiResponse(responseCode = "400", description = "Solicitud no puede ser aprobada en su estado actual")
    })
    public ResponseEntity<Solicitud> aprobarSolicitud(
            @Parameter(description = "ID de la solicitud") @PathVariable String solicitudId,
            @Parameter(description = "ID del coordinador") @RequestParam String coordinadorId,
            @RequestBody(required = false) String observaciones) {
        
        Solicitud solicitudAprobada = solicitudService.approveSolicitud(solicitudId, coordinadorId);
        return ResponseEntity.ok(solicitudAprobada);
    }

    @PutMapping("/solicitudes/{solicitudId}/rechazar")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(
        summary = "Rechazar Solicitud de Reasignación",
        description = "Permite al coordinador rechazar una solicitud con motivo específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud rechazada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
        @ApiResponse(responseCode = "400", description = "Motivo de rechazo requerido")
    })
    public ResponseEntity<Solicitud> rechazarSolicitud(
            @Parameter(description = "ID de la solicitud") @PathVariable String solicitudId,
            @Parameter(description = "ID del coordinador") @RequestParam String coordinadorId,
            @Parameter(description = "Motivo del rechazo") @RequestBody String motivo) {
        
        Solicitud solicitudRechazada = solicitudService.rejectSolicitud(solicitudId, coordinadorId, motivo);
        return ResponseEntity.ok(solicitudRechazada);
    }

    @GetMapping("/solicitudes")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(
        summary = "Listar Todas las Solicitudes",
        description = "Obtiene todas las solicitudes de reasignación para revisión del coordinador"
    )
    @ApiResponse(responseCode = "200", description = "Lista de todas las solicitudes")
    public ResponseEntity<List<Solicitud>> listarTodasSolicitudes() {
        List<Solicitud> solicitudes = solicitudService.findAll();
        return ResponseEntity.ok(solicitudes);
    }

    @GetMapping("/solicitudes/pendientes")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(
        summary = "Listar Solicitudes Pendientes",
        description = "Obtiene todas las solicitudes en estado PENDIENTE que requieren revisión"
    )
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes pendientes")
    public ResponseEntity<List<Solicitud>> listarSolicitudesPendientes() {
        List<Solicitud> solicitudesPendientes = solicitudService.findByEstado("PENDIENTE");
        return ResponseEntity.ok(solicitudesPendientes);
    }

    @GetMapping("/reportes")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(
        summary = "Generar Reportes de Solicitudes",
        description = "Genera reportes estadísticos de solicitudes por período, estado y tipo"
    )
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    public ResponseEntity<Object> generarReportes(
            @Parameter(description = "Tipo de reporte") @RequestParam(defaultValue = "general") String tipoReporte) {
        // Implementación pendiente del servicio de reportes
        return ResponseEntity.ok("Reporte " + tipoReporte + " (funcionalidad en desarrollo)");
    }

    @GetMapping("/conflictos")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(
        summary = "Ver Conflictos de Horarios",
        description = "Consulta conflictos de horarios detectados en el programa académico"
    )
    @ApiResponse(responseCode = "200", description = "Lista de conflictos detectados")
    public ResponseEntity<List<String>> verConflictosHorarios() {
        // Implementación pendiente del servicio de conflictos
        return ResponseEntity.ok(List.of("Funcionalidad en desarrollo"));
    }

    @GetMapping("/solicitudes/{solicitudId}")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(
        summary = "Consultar Detalle de Solicitud",
        description = "Obtiene información detallada de una solicitud específica para su evaluación"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle de la solicitud"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    public ResponseEntity<Solicitud> consultarDetalleSolicitud(
            @Parameter(description = "ID de la solicitud") @PathVariable String solicitudId) {
        return solicitudService.findById(solicitudId)
            .map(solicitud -> ResponseEntity.ok(solicitud))
            .orElse(ResponseEntity.notFound().build());
    }
}