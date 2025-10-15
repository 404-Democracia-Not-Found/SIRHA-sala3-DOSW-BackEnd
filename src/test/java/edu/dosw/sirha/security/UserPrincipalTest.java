package edu.dosw.sirha.security;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para {@link UserPrincipal}.
 * 
 * <p>Verifica el correcto funcionamiento del adaptador entre la entidad {@link User} de SIRHA
 * y la interfaz {@link org.springframework.security.core.userdetails.UserDetails} de Spring Security,
 * incluyendo conversión de roles, manejo de estados de cuenta, y mapeo de credenciales.</p>
 * 
 * <p><strong>Casos de prueba cubiertos:</strong></p>
 * <ul>
 *   <li><strong>Authorities:</strong> conversión de {@link Rol} a GrantedAuthority con prefijo "ROLE_"</li>
 *   <li><strong>Rol por defecto:</strong> asignación de ROLE_USER cuando rol es null</li>
 *   <li><strong>Username:</strong> mapeo de email como username</li>
 *   <li><strong>Password:</strong> mapeo de passwordHash</li>
 *   <li><strong>Enabled:</strong> mapeo del campo activo</li>
 *   <li><strong>Account flags:</strong> verificación de expired, locked, credentials expired</li>
 * </ul>
 * 
 * @see UserPrincipal
 * @see org.springframework.security.core.userdetails.UserDetails
 */
class UserPrincipalTest {

    @Test
    void getAuthoritiesShouldReturnRolePrefix() {
        User user = User.builder()
                .email("user@test.com")
                .passwordHash("secret")
                .rol(Rol.ADMIN)
                .activo(true)
                .build();

        UserPrincipal principal = new UserPrincipal(user);

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void getAuthoritiesShouldDefaultToUserWhenRoleNull() {
        User user = User.builder()
                .email("user@test.com")
                .passwordHash("secret")
                .activo(true)
                .build();

        UserPrincipal principal = new UserPrincipal(user);

        assertThat(principal.getAuthorities()).singleElement()
                .extracting(GrantedAuthority::getAuthority)
                .isEqualTo("ROLE_USER");
    }

    @Test
    void accountFlagsShouldReflectUserState() {
        User user = User.builder()
                .email("user@test.com")
                .passwordHash("secret")
                .rol(Rol.ESTUDIANTE)
                .activo(false)
                .build();

        UserPrincipal principal = new UserPrincipal(user);

        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
        assertThat(principal.isEnabled()).isFalse();
        assertThat(principal.getUsername()).isEqualTo("user@test.com");
        assertThat(principal.getPassword()).isEqualTo("secret");
        assertThat(principal.getUser()).isSameAs(user);
    }
}
