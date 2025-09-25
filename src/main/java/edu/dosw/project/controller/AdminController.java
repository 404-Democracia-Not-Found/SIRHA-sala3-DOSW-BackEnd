package edu.dosw.project.controller;

import edu.dosw.project.model.User;
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
 * Controlador REST para operaciones específicas de ADMINISTRADORES
 * 
 * Este controlador maneja todas las operaciones administrativas del sistema:
 * - Gestión completa de usuarios (CRUD)
 * - Creación y gestión de materias
 * - Gestión de aulas y espacios físicos
 * - Configuración de períodos académicos
 * - Reportes administrativos generales
 * 
 * SEGURIDAD: Todos los endpoints requieren rol ADMIN
 * API REST: Sigue las convenciones REST para URLs y códigos de estado
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "ADMINISTRADORES", description = "Operaciones disponibles para usuarios con rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // ==================== GESTIÓN DE USUARIOS ====================
    
    @PostMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear Usuario",
        description = "Permite al administrador crear un nuevo usuario en el sistema con cualquier rol"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado en el sistema")
    })
    public ResponseEntity<User> crearUsuario(@Valid @RequestBody User user) {
        User nuevoUsuario = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar Todos los Usuarios",
        description = "Obtiene la lista completa de usuarios del sistema con todos los roles"
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    public ResponseEntity<List<User>> listarTodosUsuarios() {
        List<User> usuarios = userService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Consultar Usuario por ID",
        description = "Obtiene información detallada de un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> consultarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable String userId) {
        return userService.findById(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuarios/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Actualizar Usuario",
        description = "Permite al administrador actualizar información de cualquier usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos")
    })
    public ResponseEntity<User> actualizarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable String userId,
            @Valid @RequestBody User user) {
        User usuarioActualizado = userService.updateUser(userId, user);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/usuarios/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar Usuario",
        description = "Elimina permanentemente un usuario del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar usuario con dependencias")
    })
    public ResponseEntity<String> eliminarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }

    @GetMapping("/usuarios/por-rol")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar Usuarios por Rol",
        description = "Obtiene usuarios filtrados por tipo de rol específico"
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios por rol")
    public ResponseEntity<List<User>> listarUsuariosPorRol(
            @Parameter(description = "Tipo de rol (ESTUDIANTE, PROFESOR, COORDINADOR, ADMIN)") 
            @RequestParam String tipoRol) {
        List<User> usuarios = userService.findByRoleType(tipoRol);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Buscar Usuarios",
        description = "Busca usuarios por nombre, email u otros criterios"
    )
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda")
    public ResponseEntity<List<User>> buscarUsuarios(
            @Parameter(description = "Término de búsqueda") @RequestParam String termino) {
        List<User> usuarios = userService.searchUsers(termino);
        return ResponseEntity.ok(usuarios);
    }

    // ==================== GESTIÓN DE MATERIAS ====================
    
    @PostMapping("/materias")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear Materia",
        description = "Permite crear una nueva materia en el catálogo académico"
    )
    @ApiResponse(responseCode = "201", description = "Materia creada exitosamente (funcionalidad en desarrollo)")
    public ResponseEntity<String> crearMateria(@RequestBody Object materia) {
        // Implementación pendiente del servicio de materias
        return ResponseEntity.status(HttpStatus.CREATED).body("Materia creada (funcionalidad en desarrollo)");
    }

    @GetMapping("/materias")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar Todas las Materias",
        description = "Obtiene el catálogo completo de materias del sistema"
    )
    @ApiResponse(responseCode = "200", description = "Lista de materias")
    public ResponseEntity<List<String>> listarMaterias() {
        // Implementación pendiente del servicio de materias
        return ResponseEntity.ok(List.of("Funcionalidad en desarrollo"));
    }

    // ==================== GESTIÓN DE AULAS ====================
    
    @PostMapping("/aulas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear Aula",
        description = "Registra una nueva aula o espacio físico en el sistema"
    )
    @ApiResponse(responseCode = "201", description = "Aula creada exitosamente (funcionalidad en desarrollo)")
    public ResponseEntity<String> crearAula(@RequestBody Object aula) {
        // Implementación pendiente del servicio de aulas
        return ResponseEntity.status(HttpStatus.CREATED).body("Aula creada (funcionalidad en desarrollo)");
    }

    @GetMapping("/aulas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar Todas las Aulas",
        description = "Obtiene la lista completa de aulas y espacios físicos"
    )
    @ApiResponse(responseCode = "200", description = "Lista de aulas")
    public ResponseEntity<List<String>> listarAulas() {
        // Implementación pendiente del servicio de aulas
        return ResponseEntity.ok(List.of("Funcionalidad en desarrollo"));
    }

    // ==================== REPORTES ADMINISTRATIVOS ====================
    
    @GetMapping("/reportes/sistema")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Generar Reportes del Sistema",
        description = "Genera reportes administrativos completos del sistema"
    )
    @ApiResponse(responseCode = "200", description = "Reporte del sistema generado")
    public ResponseEntity<Object> generarReportesSistema() {
        // Implementación pendiente del servicio de reportes
        return ResponseEntity.ok("Reportes del sistema (funcionalidad en desarrollo)");
    }
}