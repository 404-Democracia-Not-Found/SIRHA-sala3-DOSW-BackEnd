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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
