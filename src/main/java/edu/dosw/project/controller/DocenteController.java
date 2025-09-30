package edu.dosw.project.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para funcionalidades de docentes en SIRHA
 * Implementa la visualización de solicitudes y gestión de grupos asignados
 */
@RestController
@RequestMapping("/api/docentes")
@Tag(name = "Gestión de Docentes", description = "API para funcionalidades de docentes en SIRHA")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCENTE')")
public class DocenteController {

    private final SolicitudService solicitudService;
    private final GrupoService grupoService;
    private final InscripcionRepository inscripcionRepository;
    private final CalendarioAcademicoService calendarioService;
    private final UserService userService;

    @Operation(summary = "Estado del servicio", description = "Verificar el estado del servicio de docentes")
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    @GetMapping("/status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "SIRHA Docentes API",
            "message", "Servicio funcionando correctamente"
        ));
    }

    @Operation(summary = "Consultar grupos asignados",
               description = "Obtiene todos los grupos académicos asignados al docente")
    @ApiResponse(responseCode = "200", description = "Grupos obtenidos exitosamente")
    @GetMapping("/grupos")
    public ResponseEntity<List<Grupo>> consultarGruposAsignados(Authentication authentication) {
        
        String docenteId = authentication.getName();
        PeriodoAcademico periodoActivo = calendarioService.getPeriodoActivo();
        
        if (periodoActivo == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Grupo> grupos = grupoService.findByDocenteIdAndPeriodo(docenteId, periodoActivo.getId());
        
        return ResponseEntity.ok(grupos);
    }

    @Operation(summary = "Consultar estudiantes por grupo",
               description = "Obtiene la lista de estudiantes inscritos en un grupo específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de estudiantes obtenida"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - grupo no asignado al docente"),
        @ApiResponse(responseCode = "404", description = "Grupo no encontrado")
    })
    @GetMapping("/grupos/{grupoId}/estudiantes")
    public ResponseEntity<?> consultarEstudiantesPorGrupo(
            @Parameter(description = "ID del grupo") @PathVariable String grupoId,
            Authentication authentication,
            @PageableDefault(size = 50) Pageable pageable) {
        
        String docenteId = authentication.getName();
        
        // Verificar que el grupo pertenece al docente
        if (!grupoService.perteneceADocente(grupoId, docenteId)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Acceso denegado - grupo no asignado"));
        }
        
        Page<Inscripcion> inscripciones = inscripcionRepository
            .findByGrupoIdOrderByEstudianteNombre(grupoId, pageable);
        
        return ResponseEntity.ok(inscripciones);
    }

    @Operation(summary = "Consultar solicitudes de grupos",
               description = "Obtiene las solicitudes de cambio relacionadas con los grupos del docente")
    @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas exitosamente")
    @GetMapping("/solicitudes")
    public ResponseEntity<Page<SolicitudResponse>> consultarSolicitudesGrupos(
            Authentication authentication,
            @RequestParam(required = false) Solicitud.EstadoSolicitud estado,
            @PageableDefault(size = 10) Pageable pageable) {
        
        String docenteId = authentication.getName();
        Page<SolicitudResponse> solicitudes = solicitudService
            .findSolicitudesByDocente(docenteId, estado, pageable);
        
        return ResponseEntity.ok(solicitudes);
    }

    @Operation(summary = "Consultar detalle de solicitud",
               description = "Obtiene el detalle de una solicitud específica relacionada con grupos del docente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle obtenido exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @GetMapping("/solicitudes/{solicitudId}")
    public ResponseEntity<?> consultarDetalleSolicitud(
            @Parameter(description = "ID de la solicitud") @PathVariable String solicitudId,
            Authentication authentication) {
        
        String docenteId = authentication.getName();
        
        return solicitudService.findByIdForDocente(solicitudId, docenteId)
            .map(solicitud -> ResponseEntity.ok(solicitud))
            .orElse(ResponseEntity.status(403)
                .body(Map.of("error", "Solicitud no encontrada o acceso denegado")));
    }

    @Operation(summary = "Estadísticas de grupos",
               description = "Obtiene estadísticas de ocupación y solicitudes de los grupos del docente")
    @ApiResponse(responseCode = "200", description = "Estadísticas calculadas exitosamente")
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> consultarEstadisticas(Authentication authentication) {
        
        String docenteId = authentication.getName();
        PeriodoAcademico periodoActivo = calendarioService.getPeriodoActivo();
        
        if (periodoActivo == null) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay periodo académico activo"));
        }
        
        List<Grupo> grupos = grupoService.findByDocenteIdAndPeriodo(docenteId, periodoActivo.getId());
        
        // Calcular estadísticas generales
        int totalGrupos = grupos.size();
        int totalEstudiantes = grupos.stream()
            .mapToInt(Grupo::getInscritos)
            .sum();
        int totalCupos = grupos.stream()
            .mapToInt(Grupo::getCupoMaximo)
            .sum();
        double porcentajeOcupacion = totalCupos > 0 ? 
            (totalEstudiantes * 100.0) / totalCupos : 0;
        
        // Obtener solicitudes pendientes
        Page<SolicitudResponse> solicitudesPendientes = solicitudService
            .findSolicitudesByDocente(docenteId, Solicitud.EstadoSolicitud.PENDIENTE, 
                Pageable.ofSize(1));
        
        // Estadísticas por grupo
        List<Map<String, Object>> estadisticasPorGrupo = grupos.stream()
            .map(grupo -> Map.of(
                "grupoId", grupo.getId(),
                "grupoNombre", grupo.getNombre(),
                "materia", grupo.getMateria().getNombre(),
                "inscritos", grupo.getInscritos(),
                "cupoMaximo", grupo.getCupoMaximo(),
                "porcentajeOcupacion", (grupo.getInscritos() * 100.0) / grupo.getCupoMaximo()
            ))
            .toList();
        
        Map<String, Object> estadisticas = Map.of(
            "resumen", Map.of(
                "totalGrupos", totalGrupos,
                "totalEstudiantes", totalEstudiantes,
                "totalCupos", totalCupos,
                "porcentajeOcupacionGeneral", Math.round(porcentajeOcupacion * 100) / 100.0,
                "solicitudesPendientes", solicitudesPendientes.getTotalElements()
            ),
            "gruposDetalle", estadisticasPorGrupo
        );
        
        return ResponseEntity.ok(estadisticas);
    }

    @Operation(summary = "Consultar horario del docente",
               description = "Obtiene el horario completo del docente con todos sus grupos")
    @ApiResponse(responseCode = "200", description = "Horario obtenido exitosamente")
    @GetMapping("/horario")
    public ResponseEntity<Map<String, Object>> consultarHorario(Authentication authentication) {
        
        String docenteId = authentication.getName();
        PeriodoAcademico periodoActivo = calendarioService.getPeriodoActivo();
        
        if (periodoActivo == null) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay periodo académico activo"));
        }
        
        List<Grupo> grupos = grupoService.findByDocenteIdAndPeriodo(docenteId, periodoActivo.getId());
        
        // Organizar horarios por día de la semana
        Map<String, List<Map<String, Object>>> horarioSemanal = Map.of(
            "LUNES", List.of(),
            "MARTES", List.of(),
            "MIERCOLES", List.of(),
            "JUEVES", List.of(),
            "VIERNES", List.of(),
            "SABADO", List.of()
        );
        
        // Agrupar clases por día
        for (Grupo grupo : grupos) {
            for (Horario horario : grupo.getHorarios()) {
                String dia = horario.getDiaSemana().name();
                if (horarioSemanal.containsKey(dia)) {
                    List<Map<String, Object>> clasesDelDia = new java.util.ArrayList<>(horarioSemanal.get(dia));
                    clasesDelDia.add(Map.of(
                        "grupoId", grupo.getId(),
                        "grupoNombre", grupo.getNombre(),
                        "materia", grupo.getMateria().getNombre(),
                        "horaInicio", horario.getHoraInicio(),
                        "horaFin", horario.getHoraFin(),
                        "aula", horario.getAula(),
                        "inscritos", grupo.getInscritos()
                    ));
                    horarioSemanal.put(dia, clasesDelDia);
                }
            }
        }
        
        Map<String, Object> resultado = Map.of(
            "periodo", periodoActivo.getNombre(),
            "docenteId", docenteId,
            "horarioSemanal", horarioSemanal,
            "totalGrupos", grupos.size()
        );
        
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Consultar historial de periodos",
               description = "Obtiene los grupos asignados en periodos anteriores")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    @GetMapping("/historial")
    public ResponseEntity<Page<Grupo>> consultarHistorial(
            Authentication authentication,
            @RequestParam(required = false) String periodoId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        String docenteId = authentication.getName();
        Page<Grupo> historial;
        
        if (periodoId != null) {
            historial = grupoService.findByDocenteIdAndPeriodo(docenteId, periodoId, pageable);
        } else {
            historial = grupoService.findHistorialByDocente(docenteId, pageable);
        }
        
        return ResponseEntity.ok(historial);
    }

    @Operation(summary = "Exportar lista de estudiantes",
               description = "Exporta la lista de estudiantes de un grupo en formato CSV")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista exportada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Grupo no encontrado")
    })
    @GetMapping("/grupos/{grupoId}/estudiantes/export")
    public ResponseEntity<?> exportarListaEstudiantes(
            @Parameter(description = "ID del grupo") @PathVariable String grupoId,
            Authentication authentication) {
        
        String docenteId = authentication.getName();
        
        // Verificar permisos
        if (!grupoService.perteneceADocente(grupoId, docenteId)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Acceso denegado - grupo no asignado"));
        }
        
        try {
            String csvData = grupoService.exportarListaEstudiantes(grupoId);
            
            return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=estudiantes_grupo_" + grupoId + ".csv")
                .body(csvData);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al generar el archivo: " + e.getMessage()));
        }
    }
}