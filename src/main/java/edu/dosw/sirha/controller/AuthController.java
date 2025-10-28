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
 * en el sistema mediante credenciales (correo electrónico y contraseña), así como renovar sus tokens
 * de acceso mediante refresh tokens. Utiliza autenticación basada en tokens JWT (JSON Web Tokens) 
 * para garantizar la seguridad de las sesiones de usuario.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final Clock clock;

    /**
     * Endpoint de inicio de sesión que autentica usuarios y genera tokens JWT.
     * 
     * @param request Objeto AuthRequest que contiene email y password del usuario
     * @return ResponseEntity con AuthResponse que contiene tokens y datos del usuario
     * @throws BusinessException Si el usuario no es válido o la cuenta está inactiva
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
     * @param request Objeto RefreshRequest que contiene el refresh token
     * @return ResponseEntity con RefreshResponse que contiene el nuevo token de acceso
     * @throws BusinessException Si el refresh token es inválido o el usuario no existe
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();
        try {
            String username = jwtTokenService.extractUsername(refreshToken);
            
            var user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
            
            if (!jwtTokenService.isTokenValid(refreshToken, new UserPrincipal(user))) {
                throw new BusinessException("Refresh token inválido");
            }
            
            String newToken = jwtTokenService.generateToken(new UserPrincipal(user));
            Instant issuedAt = Instant.now(clock);
            Instant expiresAt = issuedAt.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
            
            return ResponseEntity.ok(new RefreshResponse(newToken, expiresAt));
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
            throw new BusinessException("Refresh token inválido");
        }
    }
}