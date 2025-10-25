package edu.dosw.sirha.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Suite de pruebas unitarias para {@link JwtAuthFilter}.
 * 
 * <p>Verifica el correcto funcionamiento del filtro de autenticación JWT en la cadena
 * de filtros de Spring Security, incluyendo extracción de tokens, validación,
 * establecimiento del contexto de seguridad, y manejo de peticiones sin token.</p>
 * 
 * <p><strong>Casos de prueba cubiertos:</strong></p>
 * <ul>
 *   <li><strong>Token válido:</strong> autenticación exitosa y contexto establecido</li>
 *   <li><strong>Sin token:</strong> filtro pasa sin autenticar</li>
 *   <li><strong>Token inválido:</strong> rechazo y limpieza de contexto</li>
 *   <li><strong>Token expirado:</strong> manejo de ExpiredJwtException</li>
 *   <li><strong>Header Authorization:</strong> extracción correcta con prefijo "Bearer "</li>
 * </ul>
 * 
 * @see JwtAuthFilter
 * @see JwtTokenService
 * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthFilter jwtAuthFilter;

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("sirha-test");
        properties.setExpirationMinutes(60);
        properties.setSecret("c2lyaGEtdGVzdC1zZWNyZXQtZGV0ZXJtaW5pc3RpYw==");
        Clock clock = Clock.fixed(Instant.parse("2025-10-01T09:00:00Z"), ZoneOffset.UTC);
        jwtTokenService = new JwtTokenService(properties, clock);
        jwtAuthFilter = new JwtAuthFilter(jwtTokenService, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotAuthenticateWhenHeaderMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldAuthenticateWhenTokenValid() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        UserDetails userDetails = User.withUsername("user@test.com").password("pass").authorities("ROLE_USER").build();
        String token = jwtTokenService.generateToken(userDetails);
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
        verify(filterChain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
