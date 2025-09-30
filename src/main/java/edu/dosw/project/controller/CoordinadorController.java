package edu.dosw.project.controller;

import edu.dosw.project.dto.response.SolicitudResponse;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.User;
import edu.dosw.project.service.SolicitudService;
import edu.dosw.project.service.ConflictDetectionService;
import edu.dosw.project.service.ProgramaAcademicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para operaciones de coordinador en el sistema SIRHA
 * Gestiona aprobación/rechazo de solicitudes de reasignación horaria
 */
@RestController
@RequestMapping("/api/coordinador")
@RequiredArgsConstructor
@Tag(name = "Coordinador", description = "API para operaciones de coordinador")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('COORDINADOR')")
public class CoordinadorController {

    private final SolicitudService solicitudService;
    private final ConflictDetectionService conflictDetectionService;
    private final ProgramaAcademicoService programaAcademicoService;

    @Operation(summary = "Aprobar solicitud", description = "Aprueba una solicitud de reasignación horaria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud aprobada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud no válida"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/solicitud/{solicitudId}/aprobar")
    public ResponseEntity<SolicitudResponse> aprobarSolicitud(
            @Parameter(description = "ID de la solicitud") @PathVariable String solicitudId,
            @Parameter(description = "Observaciones del coordinador") @RequestParam(required = false) String observaciones,
            @AuthenticationPrincipal User coordinador) {
        
        var solicitudAprobada = solicitudService.aprobarSolicitud(solicitudId, coordinador.getId(), observaciones);
        return ResponseEntity.ok(solicitudAprobada);
    }

    @Operation(summary = "Rechazar solicitud", description = "Rechaza una solicitud de reasignación horaria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud rechazada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud no válida"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/solicitud/{solicitudId}/rechazar")
    public ResponseEntity<SolicitudResponse> rechazarSolicitud(
            @Parameter(description = "ID de la solicitud") @PathVariable String solicitudId,
            @Parameter(description = "Motivo del rechazo") @RequestParam String motivoRechazo,
            @AuthenticationPrincipal User coordinador) {
        
        var solicitudRechazada = solicitudService.rechazarSolicitud(solicitudId, coordinador.getId(), motivoRechazo);
        return ResponseEntity.ok(solicitudRechazada);
    }

    @Operation(summary = "Consultar solicitudes pendientes", description = "Consulta solicitudes pendientes de aprobación")
    @GetMapping("/solicitudes/pendientes")
    public ResponseEntity<Page<SolicitudResponse>> consultarSolicitudesPendientes(
            @Parameter(description = "Programa académico") @RequestParam(required = false) String programaId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal User coordinador) {
        
        Page<SolicitudResponse> solicitudesPendientes = solicitudService.findSolicitudesPendientes(programaId, pageable);
        return ResponseEntity.ok(solicitudesPendientes);
    }

    @Operation(summary = "Consultar todas las solicitudes", description = "Consulta todas las solicitudes con filtros")
    @GetMapping("/solicitudes")
    public ResponseEntity<Page<SolicitudResponse>> consultarTodasSolicitudes(
            @Parameter(description = "Estado de la solicitud") @RequestParam(required = false) Solicitud.EstadoSolicitud estado,
            @Parameter(description = "Programa académico") @RequestParam(required = false) String programaId,
            @Parameter(description = "Código del estudiante") @RequestParam(required = false) String codigoEstudiante,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal User coordinador) {
        
        var solicitudes = solicitudService.findSolicitudesConFiltros(estado, programaId, codigoEstudiante, pageable);
        return ResponseEntity.ok(solicitudes);
    }

    @Operation(summary = "Dashboard coordinador", description = "Obtiene métricas y estadísticas del dashboard")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> consultarDashboard(
            @Parameter(description = "Programa académico") @RequestParam(required = false) String programaId,
            @AuthenticationPrincipal User coordinador) {
        
        var dashboardData = solicitudService.getDashboardCoordinador(programaId, coordinador.getId());
        return ResponseEntity.ok(dashboardData);
    }

    @Operation(summary = "Generar reporte de gestión", description = "Genera reporte de solicitudes gestionadas")
    @GetMapping("/reportes/gestion")
    public ResponseEntity<Map<String, Object>> generarReporteGestion(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)") @RequestParam String fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)") @RequestParam String fechaFin,
            @Parameter(description = "Programa académico") @RequestParam(required = false) String programaId,
            @AuthenticationPrincipal User coordinador) {
        
        var reporte = solicitudService.generarReporteGestion(fechaInicio, fechaFin, programaId);
        return ResponseEntity.ok(reporte);
    }

    @Operation(summary = "Procesar solicitudes en lote", description = "Procesa múltiples solicitudes en una operación")
    @PostMapping("/solicitudes/lote")
    public ResponseEntity<Map<String, Object>> procesarSolicitudesLote(
            @Parameter(description = "IDs de solicitudes") @RequestBody List<String> solicitudIds,
            @Parameter(description = "Acción a realizar") @RequestParam String accion,
            @Parameter(description = "Observaciones") @RequestParam(required = false) String observaciones,
            @AuthenticationPrincipal User coordinador) {
        
        var resultado = solicitudService.procesarSolicitudesLote(solicitudIds, accion, observaciones, coordinador.getId());
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Configurar auto-aprobación", description = "Configura reglas de auto-aprobación para el programa")
    @PutMapping("/configuracion/auto-aprobacion")
    public ResponseEntity<Map<String, Object>> configurarAutoAprobacion(
            @Parameter(description = "Programa académico") @RequestParam String programaId,
            @RequestBody Map<String, Object> configuracion,
            @AuthenticationPrincipal User coordinador) {
        
        var resultado = programaAcademicoService.configurarAutoAprobacion(programaId, configuracion);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Validar conflictos masivo", description = "Valida conflictos para múltiples solicitudes")
    @PostMapping("/validar-conflictos")
    public ResponseEntity<Map<String, Object>> validarConflictosMasivo(
            @Parameter(description = "IDs de solicitudes") @RequestBody List<String> solicitudIds,
            @AuthenticationPrincipal User coordinador) {
        
        var resultadoValidacion = conflictDetectionService.validarConflictosMasivo(solicitudIds);
        return ResponseEntity.ok(resultadoValidacion);
    }

    @Operation(summary = "Exportar solicitudes", description = "Exporta solicitudes en formato Excel")
    @GetMapping("/solicitudes/exportar")
    public ResponseEntity<byte[]> exportarSolicitudes(
            @Parameter(description = "Estado de solicitud") @RequestParam(required = false) Solicitud.EstadoSolicitud estado,
            @Parameter(description = "Fecha inicio") @RequestParam(required = false) String fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam(required = false) String fechaFin,
            @Parameter(description = "Programa académico") @RequestParam(required = false) String programaId,
            @AuthenticationPrincipal User coordinador) {
        
        var excel = solicitudService.exportarSolicitudesExcel(estado, fechaInicio, fechaFin, programaId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"solicitudes_" + java.time.LocalDate.now() + ".xlsx\"")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excel);
    }
}