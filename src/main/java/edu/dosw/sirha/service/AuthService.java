package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.auth.AuthRequest;
import edu.dosw.sirha.dto.auth.AuthResponse;
import edu.dosw.sirha.dto.request.RegisterRequest;
import edu.dosw.sirha.dto.response.RegisterResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ConflictException;
import edu.dosw.sirha.exception.ValidationException;
import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Genero;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import edu.dosw.sirha.security.JwtProperties;
import edu.dosw.sirha.security.JwtTokenService;
import edu.dosw.sirha.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Servicio de autenticación y registro de usuarios en SIRHA.
 * 
 * <p>Gestiona:</p>
 * <ul>
 *   <li>Login con JWT</li>
 *   <li>Registro de nuevos usuarios (Estudiante, Docente, Coordinador)</li>
 *   <li>Validaciones de dominio de email según rol</li>
 *   <li>Encriptación de contraseñas con BCrypt</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 2.0
 * @since 2025-10-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * @param request Credenciales del usuario (email y password)
     * @return AuthResponse con token JWT e información del usuario
     * @throws BusinessException si las credenciales son inválidas o la cuenta está inactiva
     */
    public AuthResponse login(AuthRequest request) {
        log.info("Intento de login para usuario: {}", request.email());

        // Autenticar con Spring Security
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        // Obtener el UserPrincipal del resultado de autenticación
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        // Verificar que la cuenta esté activa
        if (!user.isActivo()) {
            log.warn("Intento de login con cuenta inactiva: {}", user.getEmail());
            throw new BusinessException("La cuenta de usuario está inactiva");
        }

        // Generar tokens JWT
        UserPrincipal principal = new UserPrincipal(user);
        Instant issuedAt = Instant.now(clock);
        
        String token = jwtTokenService.generateToken(principal);
        Instant expiresAt = issuedAt.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
        
        String refreshToken = jwtTokenService.generateToken(principal, jwtProperties.getRefreshExpirationMinutes());
        Instant refreshExpiresAt = issuedAt.plus(jwtProperties.getRefreshExpirationMinutes(), ChronoUnit.MINUTES);

        // Actualizar último acceso
        user.setUltimoAcceso(issuedAt);
        userRepository.save(user);

        log.info("Login exitoso para usuario: {} con rol: {}", user.getEmail(), user.getRol());

        // Construir respuesta (AuthResponse es un record)
        return new AuthResponse(
                token,
                expiresAt,
                refreshToken,
                refreshExpiresAt,
                new AuthResponse.UserInfo(
                        user.getId(),
                        user.getNombre(),
                        user.getEmail(),
                        user.getRol().name()
                )
        );
    }

    /**
     * Registra un nuevo usuario en el sistema SIRHA.
     * 
     * <p>Proceso de registro:</p>
     * <ol>
     *   <li>Valida el dominio del email según el rol</li>
     *   <li>Verifica que el email no esté duplicado</li>
     *   <li>Encripta la contraseña con BCrypt</li>
     *   <li>Crea el usuario con los datos proporcionados</li>
     *   <li>Guarda el usuario en MongoDB</li>
     *   <li>Retorna información del usuario creado</li>
     * </ol>
     * 
     * @param request Datos del usuario a registrar
     * @return RegisterResponse con información del usuario creado
     * @throws ValidationException si el email no cumple con el dominio requerido
     * @throws ConflictException si el email ya está registrado
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Iniciando registro de usuario con email: {} y rol: {}", 
                 request.getEmail(), request.getRol());
        
        // 1. Validar dominio de email según rol
        validateEmailDomain(request.getEmail(), request.getRol());
        
        // 2. Convertir email a minúsculas y verificar que no exista
        String emailLower = request.getEmail().toLowerCase();
        if (userRepository.existsByEmail(emailLower)) {
            log.warn("Intento de registro con email duplicado: {}", emailLower);
            throw new ConflictException("El email ya está registrado en el sistema");
        }
        
        // 3. Crear entidad User
        Instant now = Instant.now(clock);
        User user = User.builder()
                .nombre(request.getNombre())
                .email(emailLower)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.valueOf(request.getRol()))
                .activo(true)
                .creadoEn(now)
                .actualizadoEn(now)
                .build();
        
        // 4. Asignar género si aplica (principalmente para estudiantes)
        if (request.getGenero() != null && !request.getGenero().isBlank()) {
            user.setGenero(Genero.valueOf(request.getGenero()));
        }
        
        // 5. Guardar en base de datos
        User savedUser = userRepository.save(user);
        log.info("Usuario registrado exitosamente con ID: {} - Email: {} - Rol: {}", 
                 savedUser.getId(), savedUser.getEmail(), savedUser.getRol());
        
        // 6. Construir respuesta
        return RegisterResponse.builder()
                .success(true)
                .message("Usuario registrado exitosamente")
                .user(RegisterResponse.UserInfo.builder()
                        .id(savedUser.getId())
                        .nombre(savedUser.getNombre())
                        .email(savedUser.getEmail())
                        .rol(savedUser.getRol().name())
                        .genero(savedUser.getGenero() != null ? savedUser.getGenero().name() : null)
                        .activo(savedUser.isActivo())
                        .creadoEn(savedUser.getCreadoEn())
                        .build())
                .build();
    }

    /**
     * Valida que el email tenga el dominio correcto según el rol del usuario.
     * 
     * <p>Reglas de validación:</p>
     * <ul>
     *   <li><b>ESTUDIANTE:</b> Debe usar @mail.escuelaing.edu.co</li>
     *   <li><b>DOCENTE:</b> Debe usar @escuelaing.edu.co</li>
     *   <li><b>COORDINADOR:</b> Debe usar @escuelaing.edu.co</li>
     * </ul>
     * 
     * @param email Email a validar
     * @param rol Rol del usuario
     * @throws ValidationException si el dominio no es válido para el rol
     */
    private void validateEmailDomain(String email, String rol) {
        String emailLower = email.toLowerCase();
        
        if ("ESTUDIANTE".equals(rol)) {
            if (!emailLower.endsWith("@mail.escuelaing.edu.co")) {
                log.warn("Intento de registro de estudiante con email inválido: {}", emailLower);
                throw new ValidationException(
                    "Los estudiantes deben usar email institucional @mail.escuelaing.edu.co"
                );
            }
        } else if ("DOCENTE".equals(rol) || "COORDINADOR".equals(rol)) {
            if (!emailLower.endsWith("@escuelaing.edu.co")) {
                log.warn("Intento de registro de {} con email inválido: {}", rol, emailLower);
                throw new ValidationException(
                    "Docentes y coordinadores deben usar email institucional @escuelaing.edu.co"
                );
            }
        }
    }
}