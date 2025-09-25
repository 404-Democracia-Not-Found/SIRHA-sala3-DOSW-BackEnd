package edu.dosw.project.controller;

import edu.dosw.project.model.User;
import edu.dosw.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones específicas de DOCENTES/PROFESORES
 * 
 * Este controlador maneja todas las operaciones que un docente puede realizar:
 * - Actualizar su disponibilidad horaria
 * - Consultar materias asignadas
 * - Reportar conflictos de horarios
 * - Consultar estudiantes de sus grupos
 * 
 * SEGURIDAD: Todos los endpoints requieren rol PROFESOR
 * API REST: Sigue las convenciones REST para URLs y códigos de estado
 */
@RestController
@RequestMapping("/api/docentes")
@Tag(name = "DOCENTES", description = "Operaciones disponibles para usuarios con rol PROFESOR")
@SecurityRequirement(name = "bearerAuth")
public class DocenteController {

    private final UserService userService;

    public DocenteController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/disponibilidad")
    @PreAuthorize("hasRole('PROFESOR')")
    @Operation(
        summary = "Actualizar Disponibilidad Horaria",
        description = "Permite al docente actualizar sus horarios disponibles para dictar clases"
    )
    @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente")
    public ResponseEntity<String> actualizarDisponibilidad(
            @Parameter(description = "ID del docente") @RequestParam String docenteId,
            @RequestBody Object disponibilidad) {
        // Implementación pendiente del servicio de disponibilidad
        return ResponseEntity.ok("Disponibilidad actualizada (funcionalidad en desarrollo)");
    }

    @GetMapping("/materias")
    @PreAuthorize("hasRole('PROFESOR')")
    @Operation(
        summary = "Consultar Materias Asignadas",
        description = "Obtiene la lista de materias y grupos asignados al docente en el período académico actual"
    )
    @ApiResponse(responseCode = "200", description = "Lista de materias asignadas")
    public ResponseEntity<List<String>> consultarMateriasAsignadas(
            @Parameter(description = "ID del docente") @RequestParam String docenteId) {
        // Implementación pendiente del servicio de materias/grupos
        return ResponseEntity.ok(List.of("Funcionalidad en desarrollo"));
    }

    @PostMapping("/conflictos")
    @PreAuthorize("hasRole('PROFESOR')")
    @Operation(
        summary = "Reportar Conflicto de Horarios",
        description = "Permite al docente reportar conflictos en su horario de clases"
    )
    @ApiResponse(responseCode = "201", description = "Conflicto reportado exitosamente")
    public ResponseEntity<String> reportarConflicto(
            @Parameter(description = "ID del docente") @RequestParam String docenteId,
            @RequestBody Object conflicto) {
        // Implementación pendiente del servicio de conflictos
        return ResponseEntity.ok("Conflicto reportado (funcionalidad en desarrollo)");
    }

    @GetMapping("/estudiantes")
    @PreAuthorize("hasRole('PROFESOR')")
    @Operation(
        summary = "Consultar Estudiantes de Grupos",
        description = "Obtiene la lista de estudiantes inscritos en los grupos del docente"
    )
    @ApiResponse(responseCode = "200", description = "Lista de estudiantes")
    public ResponseEntity<List<User>> consultarEstudiantes(
            @Parameter(description = "ID del docente") @RequestParam String docenteId) {
        List<User> estudiantes = userService.findByRoleType("ESTUDIANTE");
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/perfil")
    @PreAuthorize("hasRole('PROFESOR')")
    @Operation(
        summary = "Consultar Mi Perfil de Docente",
        description = "Consulta información del perfil del docente incluyendo materias asignadas y horarios"
    )
    @ApiResponse(responseCode = "200", description = "Información completa del perfil")
    public ResponseEntity<User> consultarMiPerfil(
            @Parameter(description = "ID del docente autenticado") @RequestParam String docenteId) {
        return userService.findById(docenteId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}