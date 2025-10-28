package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.auth.AuthRequest;
import edu.dosw.sirha.dto.auth.AuthResponse;
import edu.dosw.sirha.dto.request.RegisterRequest;
import edu.dosw.sirha.dto.response.RegisterResponse;
import edu.dosw.sirha.dto.request.RefreshRequest;
import edu.dosw.sirha.dto.response.RefreshResponse;
import edu.dosw.sirha.service.AuthService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y registro de usuarios en SIRHA.
 * 
 * <p>Endpoints disponibles:</p>
 * <ul>
 *   <li><b>POST /api/auth/login</b> - Autenticación con JWT</li>
 *   <li><b>POST /api/auth/register</b> - Registro de nuevos usuarios</li>
 * </ul>
 * 
 * <p>El endpoint de registro es público y permite crear usuarios con roles:
 * ESTUDIANTE, DOCENTE y COORDINADOR. Los usuarios ADMIN deben crearse mediante
 * otro mecanismo seguro.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 2.0
 * @since 2025-10-26
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro de usuarios")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Autentica un usuario y retorna un token JWT.
     * 
     * <p>El token JWT debe incluirse en el header Authorization de requests subsecuentes:
     * <code>Authorization: Bearer {token}</code></p>
     * 
     * @param request Credenciales del usuario (email y password)
     * @return AuthResponse con token JWT e información del usuario
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario con email y contraseña, retorna un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "Cuenta inactiva")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Request de login recibido para: {}", request.email());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Registra un nuevo usuario en el sistema SIRHA.
     * 
     * <p>Este endpoint permite registrar usuarios con los siguientes roles:</p>
     * <ul>
     *   <li><b>ESTUDIANTE:</b> Requiere email @mail.escuelaing.edu.co</li>
     *   <li><b>DOCENTE:</b> Requiere email @escuelaing.edu.co</li>
     *   <li><b>COORDINADOR:</b> Requiere email @escuelaing.edu.co</li>
     * </ul>
     * 
     * <p><b>Validaciones automáticas:</b></p>
     * <ul>
     *   <li>Nombre: 2-100 caracteres, solo letras</li>
     *   <li>Email: Formato válido y dominio institucional</li>
     *   <li>Password: Mínimo 8 caracteres con mayúscula, minúscula y número</li>
     *   <li>Email único (no duplicados)</li>
     * </ul>
     * 
     * @param request Datos del usuario a registrar
     * @return RegisterResponse con información del usuario creado
     */
    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario (Estudiante, Docente o Coordinador) en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = RegisterResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos (validación fallida)"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "El email ya está registrado en el sistema"
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Dominio de email inválido para el rol especificado"
        )
    })
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Request de registro recibido para email: {} con rol: {}", 
                 request.getEmail(), request.getRol());
        
        RegisterResponse response = authService.register(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/usuarios/" + response.getUser().getId())
                .body(response);
    }
}
