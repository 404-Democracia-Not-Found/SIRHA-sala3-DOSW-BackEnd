package edu.dosw.project.controller;

import edu.dosw.project.model.User;
import edu.dosw.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema SIRHA")
public class UsuarioController {
    
    private final UserService userService;
    
    @Autowired
    public UsuarioController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", 
               description = "Devuelve la lista de todos los usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", 
               description = "Devuelve un usuario específico basado en su ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID del usuario") @PathVariable String id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuario por email", 
               description = "Busca un usuario por su dirección de correo electrónico")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email del usuario") @PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/rol/{roleType}")
    @Operation(summary = "Buscar usuarios por tipo de rol", 
               description = "Devuelve todos los usuarios que tienen un rol específico activo")
    @ApiResponse(responseCode = "200", description = "Usuarios encontrados")
    public ResponseEntity<List<User>> getUsersByRole(
            @Parameter(description = "Tipo de rol") @PathVariable String roleType) {
        List<User> users = userService.findByRoleType(roleType);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/activos")
    @Operation(summary = "Obtener usuarios activos", 
               description = "Devuelve todos los usuarios con estado activo")
    @ApiResponse(responseCode = "200", description = "Usuarios activos obtenidos")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = userService.findActiveUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuarios", 
               description = "Busca usuarios por términos de búsqueda")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda")
    public ResponseEntity<List<User>> searchUsers(
            @Parameter(description = "Término de búsqueda") @RequestParam String searchTerm) {
        List<User> users = userService.searchUsers(searchTerm);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping
    @Operation(summary = "Crear nuevo usuario", 
               description = "Crea un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", 
               description = "Actualiza un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID del usuario") @PathVariable String id, 
            @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", 
               description = "Elimina un usuario del sistema")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario") @PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}