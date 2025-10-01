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

@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private final JwtProperties properties;
	private final Clock clock;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = parseClaims(token);
		return claimsResolver.apply(claims);
	}

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

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		Instant expiration = extractClaim(token, claims -> claims.getExpiration().toInstant());
		return expiration.isBefore(Instant.now(clock));
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.setClock(() -> Date.from(Instant.now(clock)))
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

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