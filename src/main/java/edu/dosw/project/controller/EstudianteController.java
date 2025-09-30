package edu.dosw.project.controller;

import edu.dosw.project.dto.request.SolicitudCambioRequest;
import edu.dosw.project.dto.response.SolicitudResponse;
import edu.dosw.project.model.*;
import edu.dosw.project.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para funcionalidades de estudiantes en SIRHA
 * Implementa todos los requerimientos del módulo de Gestión de Estudiantes
 */
@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Gestión de Estudiantes", description = "API para funcionalidades de estudiantes en SIRHA")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ESTUDIANTE')")
public class EstudianteController {

    private final SolicitudService solicitudService;
    private final UserService userService;
    private final InscripcionRepository inscripcionRepository;
    private final CalendarioAcademicoService calendarioService;
    private final ConflictDetectionService conflictDetectionService;
    private final CupoService cupoService;

    @Operation(summary = "Estado del servicio", description = "Verificar el estado del servicio de estudiantes")
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    @GetMapping("/status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "SIRHA Estudiantes API",
            "message", "Servicio funcionando correctamente"
        ));
    }

    @Operation(summary = "Crear solicitud de cambio", 
               description = "Permite a un estudiante crear una solicitud de cambio de materia/grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
        @ApiResponse(responseCode = "403", description = "Fuera del periodo de solicitudes"),
        @ApiResponse(responseCode = "409", description = "Conflicto de horarios o cupos agotados")
    })
    @PostMapping("/solicitudes")
    public ResponseEntity<?> crearSolicitud(
            @Valid @RequestBody SolicitudCambioRequest request,
            Authentication authentication) {
        
        try {
            // Verificar periodo de solicitudes
            if (!calendarioService.estaDentroPeriodoSolicitudes()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No está dentro del periodo habilitado para solicitudes"));
            }

            // Obtener información del estudiante autenticado
            String estudianteId = authentication.getName();
            
            // Crear la solicitud
            Solicitud solicitud = solicitudService.crearSolicitudCambio(estudianteId, request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(solicitud);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Consultar solicitudes del estudiante",
               description = "Obtiene todas las solicitudes realizadas por el estudiante autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida exitosamente")
    @GetMapping("/solicitudes")
    public ResponseEntity<Page<SolicitudResponse>> consultarSolicitudes(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        
        String estudianteId = authentication.getName();
        Page<SolicitudResponse> solicitudes = solicitudService.findByEstudianteId(estudianteId, pageable);
        
        return ResponseEntity.ok(solicitudes);
    }

    @Operation(summary = "Consultar detalle de solicitud",
               description = "Obtiene el detalle de una solicitud específica del estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle de solicitud obtenido"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado a solicitud de otro estudiante")
    })
    @GetMapping("/solicitudes/{solicitudId}")
    public ResponseEntity<?> consultarDetalleSolicitud(
            @Parameter(description = "ID de la solicitud") @PathVariable String solicitudId,
            Authentication authentication) {
        
        String estudianteId = authentication.getName();
        
        return solicitudService.findByIdAndEstudianteId(solicitudId, estudianteId)
            .map(solicitud -> ResponseEntity.ok(solicitud))
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Consultar horario actual",
               description = "Obtiene el horario actual del estudiante para el periodo académico activo")
    @ApiResponse(responseCode = "200", description = "Horario obtenido exitosamente")
    @GetMapping("/horario")
    public ResponseEntity<List<Inscripcion>> consultarHorarioActual(Authentication authentication) {
        
        String estudianteId = authentication.getName();
        PeriodoAcademico periodoActivo = calendarioService.getPeriodoActivo();
        
        if (periodoActivo == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Inscripcion> inscripciones = inscripcionRepository
            .findByEstudianteIdAndPeriodoAcademicoId(estudianteId, periodoActivo.getId());
        
        return ResponseEntity.ok(inscripciones);
    }

    @Operation(summary = "Consultar horarios anteriores",
               description = "Obtiene los horarios de semestres anteriores del estudiante")
    @ApiResponse(responseCode = "200", description = "Horarios históricos obtenidos")
    @GetMapping("/horario/historico")
    public ResponseEntity<Page<Inscripcion>> consultarHorarioHistorico(
            Authentication authentication,
            @RequestParam(required = false) String periodoId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        String estudianteId = authentication.getName();
        Page<Inscripcion> inscripciones;
        
        if (periodoId != null) {
            inscripciones = inscripcionRepository
                .findByEstudianteIdAndPeriodoAcademicoId(estudianteId, periodoId, pageable);
        } else {
            inscripciones = inscripcionRepository
                .findByEstudianteIdOrderByPeriodoAcademicoDesc(estudianteId, pageable);
        }
        
        return ResponseEntity.ok(inscripciones);
    }

    @Operation(summary = "Consultar semáforo académico",
               description = "Obtiene el estado académico del estudiante (verde/azul/rojo)")
    @ApiResponse(responseCode = "200", description = "Semáforo académico calculado")
    @GetMapping("/semaforo-academico")
    public ResponseEntity<Map<String, Object>> consultarSemaforoAcademico(Authentication authentication) {
        
        String estudianteId = authentication.getName();
        
        // Obtener todas las inscripciones del estudiante
        List<Inscripcion> todasInscripciones = inscripcionRepository.findByEstudianteId(estudianteId);
        
        // Calcular estadísticas
        long materiasAprobadas = todasInscripciones.stream()
            .filter(i -> i.getEstadoMateria() == Inscripcion.EstadoMateria.APROBADA)
            .count();
        
        long materiasEnCurso = todasInscripciones.stream()
            .filter(i -> i.getEstadoMateria() == Inscripcion.EstadoMateria.EN_CURSO)
            .count();
        
        long materiasReprobadas = todasInscripciones.stream()
            .filter(i -> i.getEstadoMateria() == Inscripcion.EstadoMateria.NO_APROBADA)
            .count();
        
        // Determinar color del semáforo
        String colorSemaforo;
        String descripcion;
        
        if (materiasReprobadas == 0 && materiasEnCurso == 0) {
            colorSemaforo = "VERDE";
            descripcion = "Avance normal aprobado";
        } else if (materiasReprobadas == 0) {
            colorSemaforo = "AZUL";
            descripcion = "En progreso";
        } else {
            colorSemaforo = "ROJO";
            descripcion = "Materias perdidas";
        }
        
        Map<String, Object> resultado = Map.of(
            "color", colorSemaforo,
            "descripcion", descripcion,
            "estadisticas", Map.of(
                "materiasAprobadas", materiasAprobadas,
                "materiasEnCurso", materiasEnCurso,
                "materiasReprobadas", materiasReprobadas,
                "totalMaterias", todasInscripciones.size()
            )
        );
        
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Consultar disponibilidad de grupos",
               description = "Obtiene la disponibilidad de cupos en grupos para una materia específica")
    @ApiResponse(responseCode = "200", description = "Disponibilidad consultada exitosamente")
    @GetMapping("/materias/{materiaId}/disponibilidad")
    public ResponseEntity<List<Map<String, Object>>> consultarDisponibilidad(
            @Parameter(description = "ID de la materia") @PathVariable String materiaId) {
        
        List<Grupo> grupos = cupoService.getGruposConDisponibilidad(materiaId);
        
        List<Map<String, Object>> disponibilidad = grupos.stream()
            .map(grupo -> Map.of(
                "grupoId", grupo.getId(),
                "grupoNombre", grupo.getNombre(),
                "cupoMaximo", grupo.getCupoMaximo(),
                "cupoOcupado", grupo.getInscritos(),
                "cupoDisponible", grupo.getCupoMaximo() - grupo.getInscritos(),
                "porcentajeOcupacion", (grupo.getInscritos() * 100.0) / grupo.getCupoMaximo(),
                "profesorNombre", grupo.getProfesorNombre(),
                "horarios", grupo.getHorarios()
            ))
            .toList();
        
        return ResponseEntity.ok(disponibilidad);
    }

    @Operation(summary = "Validar conflictos de horario",
               description = "Valida si existe conflicto de horarios para un cambio propuesto")
    @ApiResponse(responseCode = "200", description = "Validación realizada")
    @PostMapping("/validar-conflictos")
    public ResponseEntity<Map<String, Object>> validarConflictos(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        String estudianteId = authentication.getName();
        String grupoDestinoId = request.get("grupoDestinoId");
        
        boolean tieneConflicto = conflictDetectionService.tieneConflictoHorario(estudianteId, grupoDestinoId);
        
        Map<String, Object> resultado = Map.of(
            "tieneConflicto", tieneConflicto,
            "mensaje", tieneConflicto ? 
                "El grupo seleccionado tiene conflictos de horario con sus materias actuales" :
                "No se detectaron conflictos de horario"
        );
        
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Consultar historial de solicitudes",
               description = "Obtiene el historial completo de solicitudes del estudiante con filtros")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    @GetMapping("/solicitudes/historial")
    public ResponseEntity<Page<SolicitudResponse>> consultarHistorialSolicitudes(
            Authentication authentication,
            @RequestParam(required = false) Solicitud.EstadoSolicitud estado,
            @RequestParam(required = false) String periodoId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        String estudianteId = authentication.getName();
        Page<SolicitudResponse> historial = solicitudService
            .findHistorialByEstudiante(estudianteId, estado, periodoId, pageable);
        
        return ResponseEntity.ok(historial);
    }
}
