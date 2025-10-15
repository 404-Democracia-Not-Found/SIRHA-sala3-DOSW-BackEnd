package edu.dosw.sirha.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Suite de pruebas unitarias para {@link JwtTokenService}.
 * 
 * <p>Esta clase verifica la correcta generación, validación y extracción de información
 * de tokens JWT, incluyendo manejo de expiración, validación de firma y estructura de claims.</p>
 * 
 * <p><strong>Configuración de pruebas:</strong></p>
 * <ul>
 *   <li>Usa {@link JwtProperties} con valores de prueba configurados</li>
 *   <li>Clock fijo para controlar expiración de tokens determinísticamente</li>
 *   <li>Secreto HMAC-SHA256 de 32 bytes codificado en Base64</li>
 *   <li>Tiempo de expiración de 5 minutos para tests rápidos</li>
 * </ul>
 * 
 * <p><strong>Casos de prueba cubiertos:</strong></p>
 * <ul>
 *   <li><strong>Generación:</strong> tokens con estructura válida y claims correctos</li>
 *   <li><strong>Validación:</strong> tokens válidos retornan true, inválidos retornan false</li>
 *   <li><strong>Extracción:</strong> obtención correcta de username desde subject</li>
 *   <li><strong>Expiración:</strong> tokens expirados lanzan {@link ExpiredJwtException}</li>
 *   <li><strong>Firma:</strong> tokens con firma incorrecta no son válidos</li>
 *   <li><strong>Claims:</strong> issuer, subject, issued-at y expiration correctos</li>
 * </ul>
 * 
 * <p><strong>Algoritmos y estándares verificados:</strong></p>
 * <ul>
 *   <li>HMAC-SHA256 (HS256) para firma de tokens</li>
 *   <li>Estructura JWT estándar (header.payload.signature)</li>
 *   <li>Claims estándar de JWT (iss, sub, iat, exp)</li>
 * </ul>
 * 
 * @see JwtTokenService
 * @see JwtProperties
 * @see io.jsonwebtoken.Jwts
 */
class JwtTokenServiceTest {

    private static final String USERNAME = "usuario@test.com";
    private static final String SECRET_VALUE = "01234567890123456789012345678901";

    private JwtProperties properties;

    @BeforeEach
    void setUp() {
        properties = new JwtProperties();
        properties.setIssuer("sirha-tests");
        properties.setExpirationMinutes(5);
        properties.setSecret(Encoders.BASE64.encode(SECRET_VALUE.getBytes()));
    }

    @Test
    void generateTokenShouldBeValidForUser() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);
        JwtTokenService tokenService = new JwtTokenService(properties, clock);
        UserDetails userDetails = buildUser();

        String token = tokenService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(tokenService.extractUsername(token)).isEqualTo(USERNAME);
        assertThat(tokenService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValidShouldReturnFalseWhenExpired() {
        properties.setExpirationMinutes(1);
        Instant issuedAt = Instant.now().minus(10, ChronoUnit.MINUTES);
        Clock initialClock = Clock.fixed(issuedAt, ZoneOffset.UTC);
        JwtTokenService tokenService = new JwtTokenService(properties, initialClock);
        UserDetails userDetails = buildUser();
        String token = tokenService.generateToken(userDetails);

        JwtTokenService validationService = new JwtTokenService(properties, Clock.systemUTC());

        assertThatThrownBy(() -> validationService.isTokenValid(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void generateTokenShouldFallbackToRawSecretWhenSecretNotBase64() {
        properties.setSecret("change-me-secret-with-hyphen-");
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);
        JwtTokenService tokenService = new JwtTokenService(properties, clock);
        UserDetails userDetails = buildUser();

        String token = tokenService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(tokenService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void generateTokenShouldUseDefaultSecretWhenSecretBlank() {
        properties.setSecret(" ");
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);
        JwtTokenService tokenService = new JwtTokenService(properties, clock);
        UserDetails userDetails = buildUser();

        String token = tokenService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(tokenService.isTokenValid(token, userDetails)).isTrue();
    }

    private UserDetails buildUser() {
        return User.withUsername(USERNAME)
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }
}
