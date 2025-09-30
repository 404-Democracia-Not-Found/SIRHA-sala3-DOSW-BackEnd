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
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para funcionalidades de administradores en SIRHA
 * Implementa la gestión completa del sistema y configuraciones globales
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Gestión de Administradores", description = "API para funcionalidades de administración en SIRHA")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final SolicitudService solicitudService;
    private final UserService userService;
    private final CalendarioAcademicoService calendarioService;
    private final ProgramaAcademicoService programaService;
    private final SystemConfigService systemConfigService;
    private final ReportService reportService;
    private final AuditService auditService;

    @Operation(summary = "Estado del servicio", description = "Verificar el estado del servicio de administración")
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    @GetMapping("/status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "SIRHA Admin API",
            "message", "Servicio funcionando correctamente"
        ));
    }

    // === GESTIÓN DE USUARIOS ===
    
    @Operation(summary = "Listar todos los usuarios",
               description = "Obtiene una lista paginada de todos los usuarios del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    @GetMapping("/usuarios")
    public ResponseEntity<Page<User>> listarUsuarios(
            @RequestParam(required = false) User.Rol rol,
            @RequestParam(required = false) String programaId,
            @RequestParam(required = false) String busqueda,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<User> usuarios = userService.findAllWithFilters(rol, programaId, busqueda, pageable);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Crear usuario",
               description = "Crea un nuevo usuario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos"),
        @ApiResponse(responseCode = "409", description = "Usuario ya existe")
    })
    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody User usuario) {
        try {
            User usuarioCreado = userService.createUser(usuario);
            return ResponseEntity.status(201).body(usuarioCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar usuario",
               description = "Actualiza los datos de un usuario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/usuarios/{userId}")
    public ResponseEntity<?> actualizarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable String userId,
            @Valid @RequestBody User usuario) {
        
        return userService.updateUser(userId, usuario)
            .map(usuarioActualizado -> ResponseEntity.ok(usuarioActualizado))
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar usuario",
               description = "Elimina un usuario del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar - usuario tiene dependencias")
    })
    @DeleteMapping("/usuarios/{userId}")
    public ResponseEntity<?> eliminarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable String userId) {
        
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // === GESTIÓN DEL CALENDARIO ACADÉMICO ===
    
    @Operation(summary = "Configurar calendario académico",
               description = "Configura los periodos y fechas del calendario académico")
    @ApiResponse(responseCode = "200", description = "Calendario configurado exitosamente")
    @PostMapping("/calendario")
    public ResponseEntity<Map<String, String>> configurarCalendario(
            @Valid @RequestBody CalendarioAcademico calendario) {
        
        calendarioService.configurarCalendario(calendario);
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Calendario académico configurado exitosamente"
        ));
    }

    @Operation(summary = "Habilitar/deshabilitar periodo de solicitudes",
               description = "Controla el periodo en el que los estudiantes pueden hacer solicitudes")
    @ApiResponse(responseCode = "200", description = "Periodo actualizado exitosamente")
    @PostMapping("/calendario/periodo-solicitudes")
    public ResponseEntity<Map<String, String>> configurarPeriodoSolicitudes(
            @RequestBody Map<String, Object> configuracion) {
        
        boolean habilitado = (boolean) configuracion.get("habilitado");
        String fechaInicio = (String) configuracion.get("fechaInicio");
        String fechaFin = (String) configuracion.get("fechaFin");
        
        calendarioService.configurarPeriodoSolicitudes(habilitado, fechaInicio, fechaFin);
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Periodo de solicitudes " + (habilitado ? "habilitado" : "deshabilitado")
        ));
    }

    // === GESTIÓN DE SOLICITUDES GLOBALES ===
    
    @Operation(summary = "Dashboard global de solicitudes",
               description = "Obtiene estadísticas globales de todas las solicitudes del sistema")
    @ApiResponse(responseCode = "200", description = "Dashboard generado exitosamente")
    @GetMapping("/dashboard/solicitudes")
    public ResponseEntity<Map<String, Object>> dashboardGlobalSolicitudes() {
        
        Map<String, Object> dashboard = solicitudService.getDashboardGlobal();
        return ResponseEntity.ok(dashboard);
    }

    @Operation(summary = "Consultar todas las solicitudes del sistema",
               description = "Obtiene todas las solicitudes con filtros avanzados")
    @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas exitosamente")
    @GetMapping("/solicitudes")
    public ResponseEntity<Page<SolicitudResponse>> consultarTodasSolicitudes(
            @RequestParam(required = false) Solicitud.EstadoSolicitud estado,
            @RequestParam(required = false) String programaId,
            @RequestParam(required = false) String estudianteId,
            @RequestParam(required = false) String coordinadorId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<SolicitudResponse> solicitudes = solicitudService
            .findAllWithAdvancedFilters(estado, programaId, estudianteId, coordinadorId, 
                                       fechaInicio, fechaFin, pageable);
        
        return ResponseEntity.ok(solicitudes);
    }

    // === GESTIÓN DE PROGRAMAS ACADÉMICOS ===
    
    @Operation(summary = "Crear programa académico",
               description = "Crea un nuevo programa académico")
    @ApiResponse(responseCode = "201", description = "Programa creado exitosamente")
    @PostMapping("/programas")
    public ResponseEntity<ProgramaAcademico> crearPrograma(
            @Valid @RequestBody ProgramaAcademico programa) {
        
        ProgramaAcademico programaCreado = programaService.createPrograma(programa);
        return ResponseEntity.status(201).body(programaCreado);
    }

    @Operation(summary = "Listar programas académicos",
               description = "Obtiene todos los programas académicos")
    @ApiResponse(responseCode = "200", description = "Programas obtenidos exitosamente")
    @GetMapping("/programas")
    public ResponseEntity<List<ProgramaAcademico>> listarProgramas() {
        
        List<ProgramaAcademico> programas = programaService.findAll();
        return ResponseEntity.ok(programas);
    }

    // === REPORTES Y ANALÍTICAS ===
    
    @Operation(summary = "Generar reporte de uso del sistema",
               description = "Genera un reporte completo del uso del sistema")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    @GetMapping("/reportes/uso-sistema")
    public ResponseEntity<Map<String, Object>> generarReporteUsoSistema(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        
        Map<String, Object> reporte = reportService
            .generarReporteUsoSistema(fechaInicio, fechaFin);
        
        return ResponseEntity.ok(reporte);
    }

    @Operation(summary = "Generar reporte de rendimiento por programa",
               description = "Genera métricas de rendimiento por programa académico")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    @GetMapping("/reportes/rendimiento-programas")
    public ResponseEntity<List<Map<String, Object>>> generarReporteRendimientoProgramas() {
        
        List<Map<String, Object>> reporte = reportService
            .generarReporteRendimientoProgramas();
        
        return ResponseEntity.ok(reporte);
    }

    // === CONFIGURACIÓN DEL SISTEMA ===
    
    @Operation(summary = "Obtener configuración del sistema",
               description = "Obtiene toda la configuración actual del sistema")
    @ApiResponse(responseCode = "200", description = "Configuración obtenida exitosamente")
    @GetMapping("/configuracion")
    public ResponseEntity<Map<String, Object>> obtenerConfiguracion() {
        
        Map<String, Object> configuracion = systemConfigService.getConfiguracion();
        return ResponseEntity.ok(configuracion);
    }

    @Operation(summary = "Actualizar configuración del sistema",
               description = "Actualiza la configuración global del sistema")
    @ApiResponse(responseCode = "200", description = "Configuración actualizada exitosamente")
    @PutMapping("/configuracion")
    public ResponseEntity<Map<String, String>> actualizarConfiguracion(
            @RequestBody Map<String, Object> nuevaConfiguracion) {
        
        systemConfigService.actualizarConfiguracion(nuevaConfiguracion);
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Configuración actualizada exitosamente"
        ));
    }

    // === AUDITORÍA Y LOGS ===
    
    @Operation(summary = "Consultar logs de auditoría",
               description = "Obtiene los logs de auditoría del sistema")
    @ApiResponse(responseCode = "200", description = "Logs obtenidos exitosamente")
    @GetMapping("/auditoria")
    public ResponseEntity<Page<AuditLog>> consultarLogsAuditoria(
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String usuarioId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @PageableDefault(size = 50) Pageable pageable) {
        
        Page<AuditLog> logs = auditService
            .findLogsWithFilters(accion, usuarioId, fechaInicio, fechaFin, pageable);
        
        return ResponseEntity.ok(logs);
    }

    // === MANTENIMIENTO DEL SISTEMA ===
    
    @Operation(summary = "Estado de salud del sistema",
               description = "Obtiene el estado de salud de todos los componentes del sistema")
    @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente")
    @GetMapping("/salud-sistema")
    public ResponseEntity<Map<String, Object>> estadoSaludSistema() {
        
        Map<String, Object> estadoSalud = systemConfigService.getEstadoSalud();
        return ResponseEntity.ok(estadoSalud);
    }

    @Operation(summary = "Limpiar datos temporales",
               description = "Ejecuta la limpieza de datos temporales y logs antiguos")
    @ApiResponse(responseCode = "200", description = "Limpieza ejecutada exitosamente")
    @PostMapping("/mantenimiento/limpiar")
    public ResponseEntity<Map<String, String>> limpiarDatosTemporales() {
        
        systemConfigService.limpiarDatosTemporales();
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Limpieza de datos temporales ejecutada exitosamente"
        ));
    }

    @Operation(summary = "Backup de la base de datos",
               description = "Inicia un backup de la base de datos")
    @ApiResponse(responseCode = "202", description = "Backup iniciado exitosamente")
    @PostMapping("/mantenimiento/backup")
    public ResponseEntity<Map<String, String>> iniciarBackup() {
        
        String backupId = systemConfigService.iniciarBackup();
        
        return ResponseEntity.accepted().body(Map.of(
            "mensaje", "Backup iniciado exitosamente",
            "backupId", backupId
        ));
    }

    // === EXPORTACIÓN MASIVA ===
    
    @Operation(summary = "Exportar datos completos",
               description = "Exporta todos los datos del sistema en formato Excel")
    @ApiResponse(responseCode = "200", description = "Datos exportados exitosamente")
    @GetMapping("/export/completo")
    public ResponseEntity<?> exportarDatosCompletos(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        
        try {
            byte[] excelData = reportService
                .exportarDatosCompletos(fechaInicio, fechaFin);
            
            return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=sirha_datos_completos.xlsx")
                .body(excelData);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al generar el archivo: " + e.getMessage()));
        }
    }
}