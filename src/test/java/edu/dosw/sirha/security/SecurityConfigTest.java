package edu.dosw.sirha.security;

import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    private PasswordEncoder passwordEncoder;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        securityConfig = new SecurityConfig(passwordEncoder);
    }

    @Test
    void authenticationProviderShouldUseProvidedBeans() {
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        AuthenticationProvider provider = securityConfig.authenticationProvider(userDetailsService);

        assertThat(provider).isInstanceOf(DaoAuthenticationProvider.class);
        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) provider;
    assertThat(ReflectionTestUtils.getField(daoProvider, "passwordEncoder")).isEqualTo(passwordEncoder);
    assertThat(ReflectionTestUtils.getField(daoProvider, "userDetailsService")).isEqualTo(userDetailsService);
    }

    @Test
    void userDetailsServiceShouldReturnUserPrincipal() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = User.builder()
                .id("user-1")
                .email("user@test.com")
                .passwordHash("secret")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = securityConfig.userDetailsService(userRepository);
        UserDetails userDetails = userDetailsService.loadUserByUsername("user@test.com");

        assertThat(userDetails).isInstanceOf(UserPrincipal.class);
        assertThat(userDetails.getUsername()).isEqualTo("user@test.com");
    }

    @Test
    void userDetailsServiceShouldThrowWhenNotFound() {
        UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = securityConfig.userDetailsService(userRepository);

        assertThrows(ResourceNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@test.com"));
    }

}
