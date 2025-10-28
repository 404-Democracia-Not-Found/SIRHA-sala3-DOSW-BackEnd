package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.auth.AuthRequest;
import edu.dosw.sirha.dto.auth.AuthResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.repository.UserRepository;
import edu.dosw.sirha.security.JwtProperties;
import edu.dosw.sirha.security.JwtTokenService;
import edu.dosw.sirha.security.UserPrincipal;
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
     * <h3>Respuestas HTTP:</h3>
     * <ul>
     *   <li><b>200 OK:</b> Autenticación exitosa, retorna token JWT y datos del usuario</li>
     *   <li><b>400 Bad Request:</b> Credenciales vacías o formato inválido</li>
     *   <li><b>401 Unauthorized:</b> Credenciales incorrectas (email o password inválidos)</li>
     *   <li><b>403 Forbidden:</b> Cuenta de usuario inactiva</li>
     * </ul>
     * 
     * <h3>Excepciones Lanzadas:</h3>
     * <ul>
     *   <li><b>{@link BusinessException}:</b> Si el principal no es {@link UserPrincipal} (error interno)</li>
     *   <li><b>{@link BusinessException}:</b> Si la cuenta del usuario está inactiva</li>
     *   <li><b>{@code BadCredentialsException}:</b> Si las credenciales son incorrectas (manejada por Spring Security)</li>
     * </ul>
     * 
     * <p><b>Ejemplo de petición válida:</b></p>
     * <pre>
     * POST /api/auth/login
     * Content-Type: application/json
     * 
     * {
     *   "email": "estudiante@mail.escuelaing.edu.co",
     *   "password": "MiPassword123!"
     * }
     * </pre>
     * 
     * <p><b>Ejemplo de respuesta exitosa:</b></p>
     * <pre>
     * HTTP/1.1 200 OK
     * Content-Type: application/json
     * 
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3R1ZGlhbnRlQG1haWwuZXNjdWVsYWluZy5lZHUuY28iLCJpYXQiOjE3MTg0NzQ0MDAsImV4cCI6MTcxODQ3ODAwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
     *   "expiresAt": "2024-06-15T18:00:00Z",
     *   "userInfo": {
     *     "id": "665d7f9a1234567890abcdef",
     *     "nombre": "Juan Pérez García",
     *     "email": "estudiante@mail.escuelaing.edu.co",
     *     "rol": "ESTUDIANTE"
     *   }
     * }
     * </pre>
     * 
     * <p><b>Nota de seguridad:</b> El token JWT debe ser almacenado de forma segura en el cliente
     * (preferiblemente en memoria o localStorage con precauciones contra XSS) y enviado en el header
     * {@code Authorization: Bearer <token>} en subsiguientes peticiones a endpoints protegidos.</p>
     * 
     * @param request Objeto {@link AuthRequest} que contiene email y password del usuario.
     *                Validado automáticamente por {@code @Valid} según las restricciones definidas
     *                en la clase {@link AuthRequest} (email válido, password no vacío).
     * 
     * @return {@link ResponseEntity} con status 200 OK y body {@link AuthResponse} que contiene:
     *         <ul>
     *           <li><b>token:</b> JWT firmado para autenticación de subsiguientes peticiones</li>
     *           <li><b>expiresAt:</b> Timestamp ISO-8601 de expiración del token en UTC</li>
     *           <li><b>userInfo:</b> Objeto con id, nombre, email y rol del usuario autenticado</li>
     *         </ul>
     * 
     * @throws BusinessException Si el usuario autenticado no es instancia de {@link UserPrincipal}
     *                          o si la cuenta del usuario está marcada como inactiva
     * 
     * @see AuthRequest
     * @see AuthResponse
     * @see UserPrincipal
     * @see JwtTokenService#generateToken(UserPrincipal)
     * @see AuthenticationManager#authenticate(Authentication)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new BusinessException("No fue posible autenticar al usuario");
        }
        if (!userPrincipal.getUser().isActivo()) {
            throw new BusinessException("Tu cuenta está inactiva");
        }
        Instant issuedAt = Instant.now(clock);
        Instant expiresAt = issuedAt.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
        String token = jwtTokenService.generateToken(userPrincipal);
        userPrincipal.getUser().setUltimoAcceso(issuedAt);
        userRepository.save(userPrincipal.getUser());

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                userPrincipal.getUser().getId(),
                userPrincipal.getUser().getNombre(),
                userPrincipal.getUser().getEmail(),
                userPrincipal.getUser().getRol() != null ? userPrincipal.getUser().getRol().name() : null
        );

        return ResponseEntity.ok(new AuthResponse(token, expiresAt, userInfo));
    }
}