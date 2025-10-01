package edu.dosw.sirha.security;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

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
