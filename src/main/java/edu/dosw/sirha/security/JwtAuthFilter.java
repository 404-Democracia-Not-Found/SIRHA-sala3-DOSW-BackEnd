package edu.dosw.sirha.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT para Spring Security.
 * 
 * <p>Intercepta cada request HTTP y valida el token JWT en el header Authorization.
 * Si el token es válido, establece la autenticación en el SecurityContext.</p>
 * 
 * <p>Proceso de validación:</p>
 * <ol>
 *   <li>Extrae el token del header "Authorization: Bearer {token}"</li>
 *   <li>Valida el token usando {@link JwtTokenService}</li>
 *   <li>Carga el usuario desde {@link UserDetailsService}</li>
 *   <li>Establece autenticación en {@link SecurityContextHolder}</li>
 * </ol>
 * 
 * <p>Requests sin token válido continúan sin autenticación (depende de SecurityConfig
 * decidir si bloquear o permitir endpoints públicos).</p>
 * 
 * @see JwtTokenService
 * @see UserPrincipal
 * @see SecurityConfig
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtTokenService jwtTokenService;
	private final UserDetailsService userDetailsService;

	/**
	 * Procesa cada request HTTP validando el token JWT.
	 * 
	 * <p>Flujo de ejecución:</p>
	 * <ul>
	 *   <li>Si no hay header Authorization o no es Bearer → continúa sin autenticar</li>
	 *   <li>Si token inválido o expirado → continúa sin autenticar</li>
	 *   <li>Si token válido → establece autenticación y continúa</li>
	 * </ul>
	 * 
	 * @param request Request HTTP entrante
	 * @param response Response HTTP saliente
	 * @param filterChain Cadena de filtros de Spring Security
	 * @throws ServletException si ocurre error de servlet
	 * @throws IOException si ocurre error de I/O
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
				HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = authHeader.substring(7);
		String username = jwtTokenService.extractUsername(token);
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (jwtTokenService.isTokenValid(token, userDetails)) {
				UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}