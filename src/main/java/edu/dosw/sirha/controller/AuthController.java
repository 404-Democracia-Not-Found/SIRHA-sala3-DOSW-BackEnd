package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.auth.AuthRequest;
import edu.dosw.sirha.dto.auth.AuthResponse;
<<<<<<< Updated upstream
import edu.dosw.sirha.dto.request.RegisterRequest;
import edu.dosw.sirha.dto.response.RegisterResponse;
import edu.dosw.sirha.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
=======
import edu.dosw.sirha.dto.auth.RefreshRequest;
import edu.dosw.sirha.dto.auth.RefreshResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.repository.UserRepository;
import edu.dosw.sirha.security.JwtProperties;
import edu.dosw.sirha.security.JwtTokenService;
import edu.dosw.sirha.security.UserPrincipal;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
 * <p>Este controlador proporciona los endpoints necesarios para que los usuarios puedan iniciar sesión
 * en el sistema mediante credenciales (correo electrónico y contraseña), así como renovar sus tokens
 * de acceso mediante refresh tokens. Utiliza autenticación basada en tokens JWT (JSON Web Tokens) 
 * para garantizar la seguridad de las sesiones de usuario.</p>
 * 
 * <h2>Flujo de Autenticación:</h2>
 * <ol>
 *   <li>El cliente envía credenciales (email y password) al endpoint {@code /api/auth/login}</li>
 *   <li>El sistema valida las credenciales utilizando {@link AuthenticationManager}</li>
 *   <li>Si son válidas, verifica que la cuenta del usuario esté activa</li>
 *   <li>Genera un token JWT de acceso y un refresh token con tiempo de expiración configurable</li>
 *   <li>Actualiza el campo {@code ultimoAcceso} del usuario en la base de datos</li>
 *   <li>Retorna ambos tokens junto con información del usuario autenticado</li>
 * </ol>
 * 
 * <h2>Flujo de Renovación de Token:</h2>
 * <ol>
 *   <li>El cliente envía el refresh token al endpoint {@code /api/auth/refresh}</li>
 *   <li>El sistema valida el refresh token y extrae el username</li>
 *   <li>Verifica que el usuario exista y que el token sea válido</li>
 *   <li>Genera un nuevo token de acceso con tiempo de expiración renovado</li>
 *   <li>Retorna el nuevo token de acceso</li>
 * </ol>
 * 
 * <h2>Seguridad:</h2>
 * <ul>
 *   <li><b>Autenticación:</b> Spring Security con {@code AuthenticationManager}</li>
 *   <li><b>Tokens JWT:</b> Generados por {@link JwtTokenService}, firmados y con expiración configurable</li>
 *   <li><b>Refresh Tokens:</b> Tokens de larga duración para renovar el acceso sin reautenticación</li>
 *   <li><b>Validación de cuentas:</b> Solo usuarios con cuentas activas pueden autenticarse</li>
 *   <li><b>CORS:</b> Habilitado mediante {@code @CrossOrigin} para permitir peticiones desde el frontend</li>
 * </ul>
 * 
 * <h2>Manejo de Errores:</h2>
 * <ul>
 *   <li><b>Credenciales incorrectas:</b> Spring Security lanza {@code BadCredentialsException}</li>
 *   <li><b>Cuenta inactiva:</b> Se lanza {@link BusinessException} con mensaje descriptivo</li>
 *   <li><b>Error en autenticación:</b> Se lanza {@link BusinessException} si el principal no es válido</li>
 *   <li><b>Refresh token inválido:</b> Se lanza {@link BusinessException} si el token no es válido o ha expirado</li>
 * </ul>
 * 
 * <p><b>Ejemplo de petición exitosa de login:</b></p>
 * <pre>
 * POST /api/auth/login
 * Content-Type: application/json
 * 
 * {
 *   "email": "admin@sirha.local",
 *   "password": "Admin123!"
 * }
 * 
 * Respuesta (200 OK):
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "expiresAt": "2024-06-15T18:30:00Z",
 *   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "refreshExpiresAt": "2024-06-22T17:30:00Z",
 *   "userInfo": {
 *     "id": "507f1f77bcf86cd799439011",
 *     "nombre": "Administrador SIRHA",
 *     "email": "admin@sirha.local",
 *     "rol": "ADMIN"
 *   }
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see AuthRequest
 * @see AuthResponse
 * @see RefreshRequest
 * @see RefreshResponse
 * @see JwtTokenService
 * @see AuthenticationManager
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
     * <p>El token JWT debe incluirse en el header Authorization de requests subsecuentes:
     * <code>Authorization: Bearer {token}</code></p>
     * 
     * @param request Credenciales del usuario (email y password)
     * @return AuthResponse con token JWT e información del usuario
=======
     * <p>Este método es el punto de entrada principal para la autenticación en el sistema SIRHA.
     * Recibe las credenciales del usuario (email y contraseña), las valida contra Spring Security,
     * verifica que la cuenta esté activa y genera un token JWT de acceso y un refresh token con 
     * información del usuario.</p>
     * 
     * <h3>Proceso de Autenticación:</h3>
     * <ol>
     *   <li><b>Validación de entrada:</b> Las credenciales se validan automáticamente por {@code @Valid}</li>
     *   <li><b>Autenticación:</b> Se delega a {@code AuthenticationManager} que valida email/password</li>
     *   <li><b>Contexto de seguridad:</b> Se establece la autenticación en {@code SecurityContextHolder}</li>
     *   <li><b>Validación de principal:</b> Se verifica que el objeto autenticado sea instancia de {@link UserPrincipal}</li>
     *   <li><b>Verificación de cuenta activa:</b> Se valida que {@code user.isActivo() == true}</li>
     *   <li><b>Generación de tokens:</b> Se crean un JWT de acceso y un refresh token firmados</li>
     *   <li><b>Actualización de acceso:</b> Se registra la fecha/hora del último acceso en base de datos</li>
     *   <li><b>Construcción de respuesta:</b> Se empaquetan los tokens con información del usuario</li>
     * </ol>
     * 
     * <h3>Cálculo de Expiración:</h3>
     * <ul>
     *   <li><b>Fecha de emisión (issuedAt):</b> Tiempo actual UTC obtenido de {@link Clock}</li>
     *   <li><b>Token de acceso (expiresAt):</b> issuedAt + minutos configurados en {@link JwtProperties}</li>
     *   <li><b>Refresh token (refreshExpiresAt):</b> issuedAt + minutos de refresh configurados</li>
     *   <li>La expiración se calcula usando {@link ChronoUnit#MINUTES} para precisión temporal</li>
     * </ul>
     * 
     * <h3>Información del Usuario Retornada:</h3>
     * <ul>
     *   <li><b>id:</b> Identificador único del usuario en MongoDB (ObjectId)</li>
     *   <li><b>nombre:</b> Nombre completo del usuario</li>
     *   <li><b>email:</b> Correo electrónico utilizado para autenticación</li>
     *   <li><b>rol:</b> Rol del usuario (ADMIN, DECANATURA, ESTUDIANTE, etc.)</li>
     * </ul>
     * 
     * <h3>Respuestas HTTP:</h3>
     * <ul>
     *   <li><b>200 OK:</b> Autenticación exitosa, retorna tokens JWT y datos del usuario</li>
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
     *   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3R1ZGlhbnRlQG1haWwuZXNjdWVsYWluZy5lZHUuY28iLCJpYXQiOjE3MTg0NzQ0MDAsImV4cCI6MTcxOTA3OTIwMH0.xyz123abc456...",
     *   "refreshExpiresAt": "2024-06-22T17:00:00Z",
     *   "userInfo": {
     *     "id": "665d7f9a1234567890abcdef",
     *     "nombre": "Juan Pérez García",
     *     "email": "estudiante@mail.escuelaing.edu.co",
     *     "rol": "ESTUDIANTE"
     *   }
     * }
     * </pre>
     * 
     * <p><b>Nota de seguridad:</b> El token JWT de acceso debe ser almacenado de forma segura en el cliente
     * (preferiblemente en memoria) y el refresh token en httpOnly cookies o almacenamiento seguro. Ambos deben 
     * enviarse en el header {@code Authorization: Bearer <token>} en subsiguientes peticiones a endpoints protegidos.</p>
     * 
     * @param request Objeto {@link AuthRequest} que contiene email y password del usuario.
     *                Validado automáticamente por {@code @Valid} según las restricciones definidas
     *                en la clase {@link AuthRequest} (email válido, password no vacío).
     * 
     * @return {@link ResponseEntity} con status 200 OK y body {@link AuthResponse} que contiene:
     *         <ul>
     *           <li><b>token:</b> JWT de acceso firmado para autenticación de subsiguientes peticiones</li>
     *           <li><b>expiresAt:</b> Timestamp ISO-8601 de expiración del token de acceso en UTC</li>
     *           <li><b>refreshToken:</b> JWT de refresh para renovar el token de acceso</li>
     *           <li><b>refreshExpiresAt:</b> Timestamp ISO-8601 de expiración del refresh token en UTC</li>
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
     * @see JwtTokenService#generateToken(UserPrincipal, long)
     * @see AuthenticationManager#authenticate(Authentication)
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
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
        
        // Generar refresh token con expiración más larga
        String refreshToken = jwtTokenService.generateToken(userPrincipal, jwtProperties.getRefreshExpirationMinutes());
        Instant refreshExpiresAt = issuedAt.plus(jwtProperties.getRefreshExpirationMinutes(), ChronoUnit.MINUTES);
        
        userPrincipal.getUser().setUltimoAcceso(issuedAt);
        userRepository.save(userPrincipal.getUser());

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                userPrincipal.getUser().getId(),
                userPrincipal.getUser().getNombre(),
                userPrincipal.getUser().getEmail(),
                userPrincipal.getUser().getRol() != null ? userPrincipal.getUser().getRol().name() : null
        );

        return ResponseEntity.ok(new AuthResponse(token, expiresAt, refreshToken, refreshExpiresAt, userInfo));
    }

    /**
     * Endpoint para renovar el token de acceso usando un refresh token válido.
     * 
     * <p>Este método permite a los clientes obtener un nuevo token de acceso sin necesidad de
     * reautenticarse con credenciales, siempre que tengan un refresh token válido. Esto mejora
     * la experiencia del usuario al mantener sesiones activas sin interrupciones.</p>
     * 
     * <h3>Proceso de Renovación:</h3>
     * <ol>
     *   <li><b>Validación del refresh token:</b> Se verifica que el token sea válido y no haya expirado</li>
     *   <li><b>Extracción del username:</b> Se obtiene el email del usuario del token</li>
     *   <li><b>Carga del usuario:</b> Se busca el usuario en la base de datos</li>
     *   <li><b>Validación contra usuario:</b> Se verifica que el token corresponda al usuario</li>
     *   <li><b>Generación de nuevo token:</b> Se crea un nuevo token de acceso con expiración renovada</li>
     *   <li><b>Retorno de respuesta:</b> Se envía el nuevo token con su tiempo de expiración</li>
     * </ol>
     * 
     * <h3>Seguridad:</h3>
     * <ul>
     *   <li>El refresh token debe estar firmado correctamente</li>
     *   <li>El refresh token no debe estar expirado</li>
     *   <li>El usuario asociado debe existir en el sistema</li>
     *   <li>El token debe ser válido para el usuario específico</li>
     * </ul>
     * 
     * <h3>Respuestas HTTP:</h3>
     * <ul>
     *   <li><b>200 OK:</b> Renovación exitosa, retorna nuevo token de acceso</li>
     *   <li><b>400 Bad Request:</b> Refresh token vacío o formato inválido</li>
     *   <li><b>401 Unauthorized:</b> Refresh token inválido o expirado</li>
     *   <li><b>404 Not Found:</b> Usuario no encontrado en el sistema</li>
     * </ul>
     * 
     * <h3>Excepciones Lanzadas:</h3>
     * <ul>
     *   <li><b>{@link BusinessException}:</b> Si el usuario no existe en el sistema</li>
     *   <li><b>{@link BusinessException}:</b> Si el refresh token no es válido para el usuario</li>
     *   <li><b>{@link BusinessException}:</b> Si el refresh token está malformado o ha expirado</li>
     * </ul>
     * 
     * <p><b>Ejemplo de petición válida:</b></p>
     * <pre>
     * POST /api/auth/refresh
     * Content-Type: application/json
     * 
     * {
     *   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * }
     * </pre>
     * 
     * <p><b>Ejemplo de respuesta exitosa:</b></p>
     * <pre>
     * HTTP/1.1 200 OK
     * Content-Type: application/json
     * 
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3R1ZGlhbnRlQG1haWwuZXNjdWVsYWluZy5lZHUuY28iLCJpYXQiOjE3MTg0ODA0MDAsImV4cCI6MTcxODQ4NDAwMH0.newTokenSignature...",
     *   "expiresAt": "2024-06-15T19:00:00Z"
     * }
     * </pre>
     * 
     * <p><b>Nota de implementación:</b> El refresh token original permanece válido hasta su expiración.
     * Si se desea implementar rotación de refresh tokens (refresh token rotation), debe modificarse
     * este método para invalidar el token anterior y generar uno nuevo.</p>
     * 
     * @param request Objeto {@link RefreshRequest} que contiene el refresh token.
     *                Validado automáticamente por {@code @Valid}.
     * 
     * @return {@link ResponseEntity} con status 200 OK y body {@link RefreshResponse} que contiene:
     *         <ul>
     *           <li><b>token:</b> Nuevo JWT de acceso firmado</li>
     *           <li><b>expiresAt:</b> Timestamp ISO-8601 de expiración del nuevo token en UTC</li>
     *         </ul>
     * 
     * @throws BusinessException Si el refresh token es inválido, si el usuario no existe,
     *                          o si ocurre algún error en la validación del token
     * 
     * @see RefreshRequest
     * @see RefreshResponse
     * @see JwtTokenService#extractUsername(String)
     * @see JwtTokenService#isTokenValid(String, UserPrincipal)
     * @see JwtTokenService#generateToken(UserPrincipal)
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();
        try {
            String username = jwtTokenService.extractUsername(refreshToken);
            
            // Cargar usuario desde la base de datos
            var user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
            
            // Validar el token contra el usuario
            if (!jwtTokenService.isTokenValid(refreshToken, new UserPrincipal(user))) {
                throw new BusinessException("Refresh token inválido");
            }
            
            // Generar nuevo token de acceso
            String newToken = jwtTokenService.generateToken(new UserPrincipal(user));
            Instant issuedAt = Instant.now(clock);
            Instant expiresAt = issuedAt.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
            
            return ResponseEntity.ok(new RefreshResponse(newToken, expiresAt));
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
            throw new BusinessException("Refresh token inválido");
        }
>>>>>>> Stashed changes
    }
}