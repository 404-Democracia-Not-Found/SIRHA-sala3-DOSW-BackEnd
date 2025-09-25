package edu.dosw.project.controller;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.service.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@Tag(name = "Solicitudes", description = "API para gestión de solicitudes de cambio de grupo")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    @Operation(summary = "Crear nueva solicitud", 
               description = "Permite a un estudiante crear una nueva solicitud de cambio de grupo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "Solicitud creada exitosamente",
                    content = @Content(schema = @Schema(implementation = Solicitud.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Datos de entrada inválidos",
                    content = @Content),
        @ApiResponse(responseCode = "401", 
                    description = "No autorizado",
                    content = @Content),
        @ApiResponse(responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content)
    })
    public ResponseEntity<Solicitud> crearSolicitud(@Valid @RequestBody SolicitudCreateDto solicitudDto) {
        try {
            Solicitud nuevaSolicitud = solicitudService.createSolicitud(solicitudDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las solicitudes", 
               description = "Retrieve todas las solicitudes del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Lista de solicitudes obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Solicitud.class))),
        @ApiResponse(responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content)
    })
    public ResponseEntity<List<Solicitud>> obtenerTodasLasSolicitudes() {
        try {
            List<Solicitud> solicitudes = solicitudService.findAll();
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener solicitud por ID", 
               description = "Obtiene una solicitud específica por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Solicitud encontrada",
                    content = @Content(schema = @Schema(implementation = Solicitud.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Solicitud no encontrada",
                    content = @Content),
        @ApiResponse(responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content)
    })
    public ResponseEntity<Solicitud> obtenerSolicitudPorId(
            @Parameter(description = "ID de la solicitud", required = true)
            @PathVariable String id) {
        try {
            Solicitud solicitud = solicitudService.findById(id);
            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Obtener solicitudes de un estudiante", 
               description = "Obtiene todas las solicitudes creadas por un estudiante específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Lista de solicitudes del estudiante",
                    content = @Content(schema = @Schema(implementation = Solicitud.class))),
        @ApiResponse(responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content)
    })
    public ResponseEntity<List<Solicitud>> obtenerSolicitudesPorEstudiante(
            @Parameter(description = "ID del estudiante", required = true)
            @PathVariable String estudianteId) {
        try {
            List<Solicitud> solicitudes = solicitudService.findByStudent(estudianteId);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener solicitudes por estado", 
               description = "Filtra las solicitudes por su estado actual (PENDIENTE, APROBADA, RECHAZADA)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Lista de solicitudes filtradas por estado",
                    content = @Content(schema = @Schema(implementation = Solicitud.class))),
        @ApiResponse(responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content)
    })
    public ResponseEntity<List<Solicitud>> obtenerSolicitudesPorEstado(
            @Parameter(description = "Estado de las solicitudes (PENDIENTE, APROBADA, RECHAZADA)", required = true)
            @PathVariable String estado) {
        try {
            List<Solicitud> solicitudes = solicitudService.findByEstado(estado);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar solicitud", 
               description = "Permite a un coordinador aprobar una solicitud pendiente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Solicitud aprobada exitosamente",
                    content = @Content(schema = @Schema(implementation = Solicitud.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Estado de solicitud no válido para aprobación",
                    content = @Content),
        @ApiResponse(responseCode = "404", 
                    description = "Solicitud no encontrada",
                    content = @Content),
        @ApiResponse(responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content)
    })
    public ResponseEntity<Solicitud> aprobarSolicitud(
            @Parameter(description = "ID de la solicitud a aprobar", required = true)
            @PathVariable String id,
            @Parameter(description = "ID del coordinador que aprueba", required = true)
            @RequestParam String coordinadorId) {
        try {
            Solicitud solicitudAprobada = solicitudService.approveSolicitud(id, coordinadorId);
            return ResponseEntity.ok(solicitudAprobada);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar solicitud", 
               description = "Permite a un coordinador rechazar una solicitud pendiente con motivo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Solicitud rechazada exitosamente",
                    content = @Content(schema = @Schema(implementation = Solicitud.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Estado de solicitud no válido para rechazo o motivo faltante",
                    content = @Content),
        @ApiResponse(responseCode = "404", 
                    description = "Solicitud no encontrada",
                    content = @Content),
        @ApiResponse(responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content)
    })
    public ResponseEntity<Solicitud> rechazarSolicitud(
            @Parameter(description = "ID de la solicitud a rechazar", required = true)
            @PathVariable String id,
            @Parameter(description = "ID del coordinador que rechaza", required = true)
            @RequestParam String coordinadorId,
            @Parameter(description = "Motivo del rechazo", required = true)
            @RequestParam String motivo) {
        try {
            if (motivo == null || motivo.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Solicitud solicitudRechazada = solicitudService.rejectSolicitud(id, coordinadorId, motivo);
            return ResponseEntity.ok(solicitudRechazada);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}