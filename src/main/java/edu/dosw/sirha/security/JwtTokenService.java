package edu.dosw.sirha.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

/**
 * Servicio de gestión de tokens JWT (JSON Web Token).
 * 
 * <p>Proporciona operaciones para:</p>
 * <ul>
 *   <li>Generar tokens JWT firmados con HS256</li>
 *   <li>Validar tokens (firma, expiración, usuario)</li>
 *   <li>Extraer información (username, claims) de tokens</li>
 * </ul>
 * 
 * <p>Configuración mediante {@link JwtProperties}:</p>
 * <ul>
 *   <li>secret: Clave secreta para firmar tokens (BASE64)</li>
 *   <li>expirationMinutes: Tiempo de vida del token</li>
 *   <li>issuer: Identificador del emisor</li>
 * </ul>
 * 
 * <p>Usa {@link Clock} inyectado para permitir control del tiempo en tests.</p>
 * 
 * @see JwtProperties
 * @see JwtAuthFilter
 */
@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private final JwtProperties properties;
	private final Clock clock;

	/**
	 * Extrae el username (subject) del token JWT.
	 * 
	 * @param token Token JWT
	 * @return Username extraído del claim "sub"
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extrae un claim específico del token usando una función resolver.
	 * 
	 * @param token Token JWT
	 * @param claimsResolver Función para extraer el claim deseado
	 * @param <T> Tipo del claim
	 * @return Valor del claim extraído
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = parseClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Genera un nuevo token JWT para un usuario autenticado.
	 * 
	 * <p>Token incluye:</p>
	 * <ul>
	 *   <li>Subject: username del usuario</li>
	 *   <li>Issuer: configurado en properties</li>
	 *   <li>IssuedAt: timestamp actual</li>
	 *   <li>Expiration: tiempo actual + expirationMinutes</li>
	 * </ul>
	 * 
	 * @param userDetails Detalles del usuario autenticado
	 * @return Token JWT firmado
	 */
	public String generateToken(UserDetails userDetails) {
		Instant now = Instant.now(clock);
		Instant expiration = now.plus(properties.getExpirationMinutes(), ChronoUnit.MINUTES);
		return Jwts.builder()
				.setSubject(userDetails.getUsername())
				.setIssuer(properties.getIssuer())
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(expiration))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Genera un token JWT con expiración personalizada (en minutos).
	 */
	public String generateToken(UserDetails userDetails, int expirationMinutes) {
		Instant now = Instant.now(clock);
		Instant expiration = now.plus(expirationMinutes, ChronoUnit.MINUTES);
		return Jwts.builder()
				.setSubject(userDetails.getUsername())
				.setIssuer(properties.getIssuer())
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(expiration))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Valida un token JWT contra un usuario.
	 * 
	 * <p>Verifica que:</p>
	 * <ul>
	 *   <li>El username del token coincida con el del usuario</li>
	 *   <li>El token no esté expirado</li>
	 * </ul>
	 * 
	 * @param token Token JWT a validar
	 * @param userDetails Usuario contra el que validar
	 * @return true si token es válido para ese usuario, false si no
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	/**
	 * Verifica si un token está expirado.
	 * 
	 * @param token Token JWT
	 * @return true si expirado, false si aún válido
	 */
	private boolean isTokenExpired(String token) {
		Instant expiration = extractClaim(token, claims -> claims.getExpiration().toInstant());
		return expiration.isBefore(Instant.now(clock));
	}

	/**
	 * Obtiene la fecha de expiración de un token JWT.
	 * 
	 * @param token Token JWT
	 * @return Instant con la fecha de expiración del token
	 */
	public Instant getExpirationFromToken(String token) {
		return extractClaim(token, claims -> claims.getExpiration().toInstant());
	}

	/**
	 * Parsea y valida un token JWT extrayendo sus claims.
	 * 
	 * @param token Token JWT a parsear
	 * @return Claims del token
	 * @throws io.jsonwebtoken.JwtException si token inválido o firma incorrecta
	 */
	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.setClock(() -> Date.from(Instant.now(clock)))
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * Obtiene la clave de firma para tokens JWT.
	 * 
	 * <p>Proceso de obtención:</p>
	 * <ol>
	 *   <li>Lee secret de properties (BASE64)</li>
	 *   <li>Si no hay secret, usa uno por defecto</li>
	 *   <li>Decodifica de BASE64 a bytes</li>
	 *   <li>Si menos de 32 bytes, hace padding</li>
	 *   <li>Crea clave HMAC-SHA256</li>
	 * </ol>
	 * 
	 * @return Clave de firma HMAC-SHA256
	 */
	private Key getSigningKey() {
		String secret = properties.getSecret();
		if (secret == null || secret.isBlank()) {
			secret = Encoders.BASE64.encode("sirha-default-secret".getBytes(StandardCharsets.UTF_8));
		}
		byte[] keyBytes;
		try {
			keyBytes = Decoders.BASE64.decode(secret);
		} catch (RuntimeException ex) {
			if (!(ex instanceof IllegalArgumentException) && !(ex instanceof DecodingException)) {
				throw ex;
			}
			keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		}
		if (keyBytes.length < 32) {
			byte[] padded = new byte[32];
			System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
			keyBytes = padded;
		}
		return Keys.hmacShaKeyFor(keyBytes);
	}
}