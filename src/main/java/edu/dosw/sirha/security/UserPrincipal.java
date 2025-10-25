package edu.dosw.sirha.security;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador de {@link User} a {@link UserDetails} de Spring Security.
 * 
 * <p>Permite que la entidad de dominio {@link User} sea usada directamente
 * por Spring Security sin acoplamiento directo.</p>
 * 
 * <p>Funcionalidades:</p>
 * <ul>
 *   <li>Convierte {@link Rol} a {@link GrantedAuthority} con prefijo "ROLE_"</li>
 *   <li>Usa email como username para autenticación</li>
 *   <li>Usa campo activo para habilitar/deshabilitar cuentas</li>
 *   <li>No implementa expiración de cuentas ni credenciales</li>
 * </ul>
 * 
 * @see User
 * @see UserDetails
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    /**
     * Crea un UserPrincipal envolviendo un User.
     * 
     * @param user Usuario de dominio
     */
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Retorna las autoridades (roles) del usuario.
     * 
     * <p>Convierte el {@link Rol} del usuario a GrantedAuthority con prefijo "ROLE_".</p>
     * <p>Ejemplo: Rol.ESTUDIANTE → "ROLE_ESTUDIANTE"</p>
     * 
     * @return Colección con un solo GrantedAuthority basado en el rol
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Rol rol = user.getRol();
        String authority = "ROLE_" + (rol != null ? rol.name() : "USER");
        return List.of(new SimpleGrantedAuthority(authority));
    }

    /**
     * Retorna la contraseña hasheada del usuario.
     * 
     * @return Password hash (bcrypt)
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Retorna el username (email) del usuario.
     * 
     * @return Email del usuario
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indica si la cuenta no está expirada.
     * 
     * <p>Siempre retorna true (no se implementa expiración de cuentas).</p>
     * 
     * @return true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta no está bloqueada.
     * 
     * <p>Siempre retorna true (no se implementa bloqueo de cuentas).</p>
     * 
     * @return true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales no están expiradas.
     * 
     * <p>Siempre retorna true (no se implementa expiración de contraseñas).</p>
     * 
     * @return true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta está habilitada.
     * 
     * <p>Usa el campo activo del usuario.</p>
     * 
     * @return true si usuario activo, false si inactivo
     */
    @Override
    public boolean isEnabled() {
        return user.isActivo();
    }

    /**
     * Obtiene el usuario de dominio envuelto.
     * 
     * @return Usuario original
     */
    public User getUser() {
        return user;
    }
}
