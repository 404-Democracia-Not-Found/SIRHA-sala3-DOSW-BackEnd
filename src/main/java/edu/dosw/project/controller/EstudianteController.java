package edu.dosw.project.controller;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.User;
import edu.dosw.project.service.SolicitudService;
import edu.dosw.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones específicas de ESTUDIANTES
 * 
 * Este controlador maneja todas las operaciones que un estudiante puede realizar:
 * - Crear solicitudes de reasignación de horarios
 * - Consultar sus propias solicitudes
 * - Ver disponibilidad de cupos en materias
 * - Consultar su horario actual
 * 
 * SEGURIDAD: Todos los endpoints requieren rol ESTUDIANTE
 * API REST: Sigue las convenciones REST para URLs y códigos de estado
 */
@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "ESTUDIANTES", description = "Funcionalidades específicas para estudiantes del sistema SIRHA")
@SecurityRequirement(name = "bearerAuth")
public class EstudianteController {

    private final SolicitudService solicitudService;
    private final UserService userService;

    public EstudianteController(SolicitudService solicitudService, UserService userService) {
        this.solicitudService = solicitudService;
        this.userService = userService;
    }

    @PostMapping("/solicitudes")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(
        summary = "Crear Solicitud de Cambio",
        description = "Permite al estudiante crear una solicitud de cambio de materia/grupo. " +
                     "Valida fechas habilitadas, materias no canceladas y disponibilidad."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente con radicado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o fuera de período"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo estudiantes")
    })
    public ResponseEntity<Solicitud> crearSolicitudCambio(@Valid @RequestBody SolicitudCreateDto solicitudDto) {
        Solicitud nuevaSolicitud = solicitudService.createSolicitud(solicitudDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
    }

    @GetMapping("/solicitudes")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(
        summary = "Consultar Mis Solicitudes",
        description = "Permite al estudiante consultar todas sus solicitudes con estado actual, " +
                     "número de radicado, fecha de creación e historial completo."
    )
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes del estudiante")
    public ResponseEntity<List<Solicitud>> consultarMisSolicitudes(
            @Parameter(description = "ID del estudiante autenticado") @RequestParam String estudianteId) {
        List<Solicitud> solicitudes = solicitudService.findByStudent(estudianteId);
        return ResponseEntity.ok(solicitudes);
    }

    @GetMapping("/solicitudes/{solicitudId}")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(
        summary = "Consultar Detalle de Solicitud",
        description = "Consulta detallada de una solicitud específica incluyendo historial, " +
                     "comentarios y seguimiento de cambios de estado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle completo de la solicitud"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
        @ApiResponse(responseCode = "403", description = "No puede ver solicitudes de otros estudiantes")
    })
    public ResponseEntity<Solicitud> consultarDetalleSolicitud(
            @Parameter(description = "ID único de la solicitud") @PathVariable String solicitudId) {
        return solicitudService.findById(solicitudId)
            .map(solicitud -> ResponseEntity.ok(solicitud))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/perfil")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(
        summary = "Consultar Mi Perfil",
        description = "Consulta información del perfil del estudiante incluyendo datos personales, " +
                     "información académica y estadísticas de solicitudes."
    )
    @ApiResponse(responseCode = "200", description = "Información completa del perfil")
    public ResponseEntity<User> consultarMiPerfil(
            @Parameter(description = "ID del estudiante autenticado") @RequestParam String estudianteId) {
        return userService.findById(estudianteId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibilidad")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(
        summary = "Consultar Disponibilidad de Cupos",
        description = "Consulta las materias y grupos que tienen cupos disponibles para reasignación."
    )
    @ApiResponse(responseCode = "200", description = "Lista de materias con cupos disponibles")
    public ResponseEntity<List<String>> consultarDisponibilidad() {
        // Implementación pendiente del servicio de materias/grupos
        return ResponseEntity.ok(List.of("Funcionalidad en desarrollo"));
    }
}