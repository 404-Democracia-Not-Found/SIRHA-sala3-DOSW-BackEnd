package edu.dosw.sirha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.sirha.dto.auth.AuthRequest;
import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import edu.dosw.sirha.security.JwtProperties;
import edu.dosw.sirha.security.JwtTokenService;
import edu.dosw.sirha.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.Clock;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AuthControllerTest.AuthControllerTestConfig.class)
class AuthControllerTest {

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String INACTIVE_EMAIL = "inactive@test.com";
    private static final Instant FIXED_NOW = Instant.parse("2025-10-01T10:15:30Z");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private AuthControllerTestConfig.JwtTestContext jwtTestContext;

    @Test
    void loginShouldReturnToken() throws Exception {
        User user = User.builder()
                .id("user-1")
                .nombre("Admin Test")
                .email(ADMIN_EMAIL)
                .passwordHash("encoded")
                .rol(Rol.ADMIN)
                .activo(true)
                .build();
        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthRequest request = new AuthRequest(ADMIN_EMAIL, DEFAULT_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.expiresAt").value(FIXED_NOW.plus(jwtTestContext.jwtProperties().getExpirationMinutes(), ChronoUnit.MINUTES).toString()))
                .andExpect(jsonPath("$.user.id").value("user-1"))
                .andExpect(jsonPath("$.user.email").value(ADMIN_EMAIL))
                .andExpect(jsonPath("$.user.rol").value("ADMIN"));

        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.getUltimoAcceso() != null && saved.getUltimoAcceso().equals(FIXED_NOW)));
    }

    @Test
    void loginShouldReturnBadRequestWhenUserInactive() throws Exception {
        User user = User.builder()
                .id("inactive-user")
                .nombre("Admin Inactive")
                .email(INACTIVE_EMAIL)
                .passwordHash("encoded")
                .rol(Rol.ADMIN)
                .activo(false)
                .build();
        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        AuthRequest request = new AuthRequest(INACTIVE_EMAIL, DEFAULT_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tu cuenta está inactiva"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldReturnBadRequestWhenPrincipalIsNotUserPrincipal() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("another-principal", null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        AuthRequest request = new AuthRequest("unknown@test.com", DEFAULT_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No fue posible autenticar al usuario"));

        verify(userRepository, never()).save(any(User.class));
    }

    @TestConfiguration
    static class AuthControllerTestConfig {

        @Bean
        JwtTestContext jwtTestContext() {
            JwtProperties properties = new JwtProperties();
            properties.setIssuer("sirha-test");
            properties.setExpirationMinutes(60);
            return new JwtTestContext(properties);
        }

        record JwtTestContext(JwtProperties jwtProperties) { }

        @Bean
        Clock testClock() {
            return Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        }

        @Bean
        JwtProperties jwtProperties(JwtTestContext context) {
            return context.jwtProperties();
        }

        @Bean
        JwtTokenService jwtTokenService(JwtProperties jwtProperties, Clock clock) {
            return new JwtTokenService(jwtProperties, clock);
        }
    }
}
