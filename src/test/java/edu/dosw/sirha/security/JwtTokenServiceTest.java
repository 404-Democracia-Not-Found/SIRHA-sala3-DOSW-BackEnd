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
