package edu.dosw.sirha.controller;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.sirha.dto.auth.AuthRequest;
import edu.dosw.sirha.dto.auth.AuthResponse;
import edu.dosw.sirha.dto.auth.RefreshRequest;
import edu.dosw.sirha.dto.auth.RefreshResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.repository.UserRepository;
import edu.dosw.sirha.security.JwtProperties;
import edu.dosw.sirha.security.JwtTokenService;
import edu.dosw.sirha.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de autenticación y autorización en el sistema SIRHA.
 * 
 * <p>Este controlador proporciona los endpoints necesarios para que los usuarios puedan iniciar sesión
 * en el sistema mediante credenciales (correo electrónico y contraseña). Utiliza autenticación basada en
 * tokens JWT (JSON Web Tokens) para garantizar la seguridad de las sesiones de usuario.</p>
 * 
 * <h2>Flujo de Autenticación:</h2>
 * <ol>
 *   <li>El cliente envía credenciales (email y password) al endpoint {@code /api/auth/login}</li>
 *   <li>El sistema valida las credenciales utilizando {@link AuthenticationManager}</li>
 *   <li>Si son válidas, verifica que la cuenta del usuario esté activa</li>
 *   <li>Genera un token JWT con tiempo de expiración configurable</li>
 *   <li>Actualiza el campo {@code ultimoAcceso} del usuario en la base de datos</li>
 *   <li>Retorna el token junto con información del usuario autenticado</li>
 * </ol>
 * 
 * <h2>Seguridad:</h2>
 * <ul>
 *   <li><b>Autenticación:</b> Spring Security con {@code AuthenticationManager}</li>
 *   <li><b>Tokens JWT:</b> Generados por {@link JwtTokenService}, firmados y con expiración configurable</li>
 *   <li><b>Validación de cuentas:</b> Solo usuarios con cuentas activas pueden autenticarse</li>
 *   <li><b>CORS:</b> Habilitado mediante {@code @CrossOrigin} para permitir peticiones desde el frontend</li>
 * </ul>
 * 
 * <h2>Manejo de Errores:</h2>
 * <ul>
 *   <li><b>Credenciales incorrectas:</b> Spring Security lanza {@code BadCredentialsException}</li>
 *   <li><b>Cuenta inactiva:</b> Se lanza {@link BusinessException} con mensaje descriptivo</li>
 *   <li><b>Error en autenticación:</b> Se lanza {@link BusinessException} si el principal no es válido</li>
 * </ul>
 * 
 * <p><b>Ejemplo de petición exitosa:</b></p>
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
 * @see JwtTokenService
 * @see AuthenticationManager
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    /**
     * Gestor de autenticación de Spring Security.
     * Valida credenciales de usuario contra el sistema de seguridad configurado.
     */
    private final AuthenticationManager authenticationManager;
    
    /**
     * Servicio para generación y validación de tokens JWT.
     * Maneja la creación de tokens seguros con información del usuario.
     */
    private final JwtTokenService jwtTokenService;
    
    /**
     * Propiedades de configuración de JWT.
     * Contiene información como tiempo de expiración y clave de firma.
     */
    private final JwtProperties jwtProperties;
    
    /**
     * Repositorio de usuarios para persistencia en MongoDB.
     * Utilizado para actualizar el campo {@code ultimoAcceso} tras login exitoso.
     */
    private final UserRepository userRepository;
    
    /**
     * Reloj para manejo de tiempo UTC consistente.
     * Permite mockear el tiempo en pruebas unitarias.
     */
    private final Clock clock;

    /**
     * Endpoint de inicio de sesión que autentica usuarios y genera tokens JWT.
     * 
     * <p>Este método es el punto de entrada principal para la autenticación en el sistema SIRHA.
     * Recibe las credenciales del usuario (email y contraseña), las valida contra Spring Security,
     * verifica que la cuenta esté activa y genera un token JWT con información del usuario.</p>
     * 
     * <h3>Proceso de Autenticación:</h3>
     * <ol>
     *   <li><b>Validación de entrada:</b> Las credenciales se validan automáticamente por {@code @Valid}</li>
     *   <li><b>Autenticación:</b> Se delega a {@code AuthenticationManager} que valida email/password</li>
     *   <li><b>Contexto de seguridad:</b> Se establece la autenticación en {@code SecurityContextHolder}</li>
     *   <li><b>Validación de principal:</b> Se verifica que el objeto autenticado sea instancia de {@link UserPrincipal}</li>
     *   <li><b>Verificación de cuenta activa:</b> Se valida que {@code user.isActivo() == true}</li>
     *   <li><b>Generación de token:</b> Se crea un JWT firmado con información del usuario</li>
     *   <li><b>Actualización de acceso:</b> Se registra la fecha/hora del último acceso en base de datos</li>
     *   <li><b>Construcción de respuesta:</b> Se empaqueta el token con información del usuario</li>
     * </ol>
     * 
     * <h3>Cálculo de Expiración:</h3>
     * <ul>
     *   <li><b>Fecha de emisión (issuedAt):</b> Tiempo actual UTC obtenido de {@link Clock}</li>
     *   <li><b>Fecha de expiración (expiresAt):</b> issuedAt + minutos configurados en {@link JwtProperties}</li>
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
    // generate refresh token with longer expiration
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

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();
        try {
            String username = jwtTokenService.extractUsername(refreshToken);
            // load user
            var user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
            // validate token against user
            if (!jwtTokenService.isTokenValid(refreshToken, new edu.dosw.sirha.security.UserPrincipal(user))) {
                throw new BusinessException("Refresh token inválido");
            }
            // generate new access token
            String newToken = jwtTokenService.generateToken(new edu.dosw.sirha.security.UserPrincipal(user));
            Instant issuedAt = Instant.now(clock);
            Instant expiresAt = issuedAt.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
            return ResponseEntity.ok(new RefreshResponse(newToken, expiresAt));
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
            throw new BusinessException("Refresh token inválido");
        }
    }
}
