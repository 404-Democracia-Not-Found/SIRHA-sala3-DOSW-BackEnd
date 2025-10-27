package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.request.FacultadRequest;
import edu.dosw.sirha.dto.response.FacultadResponse;
import edu.dosw.sirha.service.FacultadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar Facultades.
 * 
 * <p>Proporciona endpoints para operaciones CRUD sobre facultades,
 * con control de acceso basado en roles.</p>
 * 
 * <p><strong>Permisos requeridos:</strong></p>
 * <ul>
 *   <li>Consultar (GET): Cualquier usuario autenticado</li>
 *   <li>Crear (POST): ADMIN o COORDINADOR</li>
 *   <li>Actualizar (PUT): ADMIN o COORDINADOR</li>
 *   <li>Eliminar (DELETE): Solo ADMIN</li>
 * </ul>
 * 
 * @see FacultadService
 */
@Slf4j
@RestController
@RequestMapping("/api/facultades")
@RequiredArgsConstructor
@Tag(name = "Facultades", description = "Gestión de facultades y unidades académicas")
public class FacultadController {

    private final FacultadService facultadService;

    /**
     * Obtiene todas las facultades del sistema.
     * 
     * @return lista de todas las facultades
     */
    @Operation(summary = "Obtener todas las facultades", 
               description = "Retorna la lista completa de facultades, activas e inactivas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                     content = @Content(schema = @Schema(implementation = FacultadResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<FacultadResponse>> getAll() {
        log.info("Request GET /api/facultades - Obtener todas las facultades");
        List<FacultadResponse> facultades = facultadService.findAll();
        return ResponseEntity.ok(facultades);
    }

    /**
     * Obtiene solo las facultades activas.
     * 
     * @return lista de facultades activas
     */
    @Operation(summary = "Obtener facultades activas", 
               description = "Retorna solo las facultades que están activas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                     content = @Content(schema = @Schema(implementation = FacultadResponse.class)))
    })
    @GetMapping("/activas")
    public ResponseEntity<List<FacultadResponse>> getAllActive() {
        log.info("Request GET /api/facultades/activas - Obtener facultades activas");
        List<FacultadResponse> facultades = facultadService.findAllActive();
        return ResponseEntity.ok(facultades);
    }

    /**
     * Obtiene una facultad específica por su ID.
     * 
     * @param id ID de la facultad
     * @return facultad encontrada
     */
    @Operation(summary = "Obtener facultad por ID", 
               description = "Retorna los detalles de una facultad específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Facultad encontrada",
                     content = @Content(schema = @Schema(implementation = FacultadResponse.class))),
        @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FacultadResponse> getById(@PathVariable String id) {
        log.info("Request GET /api/facultades/{} - Obtener facultad", id);
        FacultadResponse facultad = facultadService.findById(id);
        return ResponseEntity.ok(facultad);
    }

    /**
     * Crea una nueva facultad.
     * 
     * @param request datos de la nueva facultad
     * @return facultad creada
     */
    @Operation(summary = "Crear nueva facultad", 
               description = "Crea una nueva facultad en el sistema. Requiere rol ADMIN o COORDINADOR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Facultad creada exitosamente",
                     content = @Content(schema = @Schema(implementation = FacultadResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado",
                     content = @Content),
        @ApiResponse(responseCode = "403", description = "No tiene permisos",
                     content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<FacultadResponse> create(@Valid @RequestBody FacultadRequest request) {
        log.info("Request POST /api/facultades - Crear facultad: {}", request.getNombre());
        FacultadResponse facultad = facultadService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(facultad);
    }

    /**
     * Actualiza una facultad existente.
     * 
     * @param id ID de la facultad a actualizar
     * @param request nuevos datos
     * @return facultad actualizada
     */
    @Operation(summary = "Actualizar facultad", 
               description = "Actualiza los datos de una facultad existente. Requiere rol ADMIN o COORDINADOR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Facultad actualizada exitosamente",
                     content = @Content(schema = @Schema(implementation = FacultadResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado",
                     content = @Content),
        @ApiResponse(responseCode = "403", description = "No tiene permisos",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                     content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<FacultadResponse> update(
            @PathVariable String id,
            @Valid @RequestBody FacultadRequest request) {
        log.info("Request PUT /api/facultades/{} - Actualizar facultad", id);
        FacultadResponse facultad = facultadService.update(id, request);
        return ResponseEntity.ok(facultad);
    }

    /**
     * Elimina una facultad.
     * 
     * @param id ID de la facultad a eliminar
     * @return respuesta sin contenido
     */
    @Operation(summary = "Eliminar facultad", 
               description = "Elimina una facultad del sistema. Solo ADMIN. No se puede eliminar si tiene materias asociadas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Facultad eliminada exitosamente"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar porque tiene materias asociadas",
                     content = @Content),
        @ApiResponse(responseCode = "403", description = "No tiene permisos",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Request DELETE /api/facultades/{} - Eliminar facultad", id);
        facultadService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activa o desactiva una facultad.
     * 
     * @param id ID de la facultad
     * @param activo nuevo estado (true = activar, false = desactivar)
     * @return facultad con el nuevo estado
     */
    @Operation(summary = "Activar/Desactivar facultad", 
               description = "Cambia el estado activo de una facultad. Requiere rol ADMIN o COORDINADOR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente",
                     content = @Content(schema = @Schema(implementation = FacultadResponse.class))),
        @ApiResponse(responseCode = "403", description = "No tiene permisos",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Facultad no encontrada",
                     content = @Content)
    })
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<FacultadResponse> toggleActive(
            @PathVariable String id,
            @RequestParam boolean activo) {
        log.info("Request PATCH /api/facultades/{}/toggle-active?activo={}", id, activo);
        FacultadResponse facultad = facultadService.toggleActive(id, activo);
        return ResponseEntity.ok(facultad);
    }
}
