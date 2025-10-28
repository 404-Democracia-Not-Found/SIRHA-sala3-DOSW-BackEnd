package edu.dosw.sirha.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración para JWT (JSON Web Tokens) en el sistema SIRHA.
 * 
 * <p>Esta clase centraliza todas las configuraciones relacionadas con la generación,
 * validación y gestión de tokens JWT utilizados para autenticación y autorización
 * en la aplicación. Las propiedades se cargan desde archivos de configuración
 * (application.properties o application.yml) bajo el prefijo {@code sirha.security.jwt}.</p>
 * 
 * <h2>Propiedades Configurables:</h2>
 * <ul>
 *   <li><b>issuer:</b> Identificador del emisor de los tokens (por defecto: "sirha")</li>
 *   <li><b>secret:</b> Clave secreta para firmar y validar tokens (OBLIGATORIA)</li>
 *   <li><b>expirationMinutes:</b> Tiempo de expiración del access token en minutos (por defecto: 60)</li>
 *   <li><b>refreshExpirationMinutes:</b> Tiempo de expiración del refresh token en minutos (por defecto: 10080 = 7 días)</li>
 * </ul>
 * 
 * <h2>Configuración en application.properties:</h2>
 * <pre>
 * # JWT Configuration
 * sirha.security.jwt.issuer=sirha
 * sirha.security.jwt.secret=${JWT_SECRET:your-256-bit-secret-key-here}
 * sirha.security.jwt.expiration-minutes=60
 * sirha.security.jwt.refresh-expiration-minutes=10080
 * </pre>
 * 
 * <h2>Configuración en application.yml:</h2>
 * <pre>
 * sirha:
 *   security:
 *     jwt:
 *       issuer: sirha
 *       secret: ${JWT_SECRET:your-256-bit-secret-key-here}
 *       expiration-minutes: 60
 *       refresh-expiration-minutes: 10080
 * </pre>
 * 
 * <h2>Tipos de Tokens:</h2>
 * <ul>
 *   <li><b>Access Token:</b> Token de corta duración para acceder a recursos protegidos
 *       <ul>
 *         <li>Tiempo de vida: Configurado por {@code expirationMinutes}</li>
 *         <li>Uso: Incluido en header Authorization de cada request</li>
 *         <li>Recomendación: Entre 15-60 minutos</li>
 *       </ul>
 *   </li>
 *   <li><b>Refresh Token:</b> Token de larga duración para renovar el access token
 *       <ul>
 *         <li>Tiempo de vida: Configurado por {@code refreshExpirationMinutes}</li>
 *         <li>Uso: Enviado al endpoint /api/auth/refresh para obtener nuevo access token</li>
 *         <li>Recomendación: Entre 7-30 días</li>
 *       </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Consideraciones de Seguridad:</h2>
 * <ul>
 *   <li><b>Secret:</b> NUNCA hardcodear el secret en el código. Usar variables de entorno.</li>
 *   <li><b>Longitud del Secret:</b> Debe tener al menos 256 bits (32 caracteres) para HS256.</li>
 *   <li><b>Rotación:</b> Considerar rotación periódica del secret en producción.</li>
 *   <li><b>Access Token corto:</b> Limitar el tiempo de exposición en caso de compromiso.</li>
 *   <li><b>Refresh Token largo:</b> Balancear conveniencia del usuario con seguridad.</li>
 * </ul>
 * 
 * <h2>Ejemplo de Uso:</h2>
 * <pre>
 * {@code
 * @Service
 * @RequiredArgsConstructor
 * public class JwtTokenService {
 *     private final JwtProperties jwtProperties;
 *     
 *     public String generateAccessToken(UserPrincipal user) {
 *         return Jwts.builder()
 *             .setIssuer(jwtProperties.getIssuer())
 *             .setSubject(user.getUsername())
 *             .setExpiration(Date.from(Instant.now()
 *                 .plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES)))
 *             .signWith(getSigningKey())
 *             .compact();
 *     }
 *     
 *     public String generateRefreshToken(UserPrincipal user) {
 *         return Jwts.builder()
 *             .setIssuer(jwtProperties.getIssuer())
 *             .setSubject(user.getUsername())
 *             .setExpiration(Date.from(Instant.now()
 *                 .plus(jwtProperties.getRefreshExpirationMinutes(), ChronoUnit.MINUTES)))
 *             .signWith(getSigningKey())
 *             .compact();
 *     }
 * }
 * }
 * </pre>
 * 
 * <h2>Configuración por Entorno:</h2>
 * <ul>
 *   <li><b>Desarrollo:</b> Access: 60 min, Refresh: 7 días, Secret: valor por defecto</li>
 *   <li><b>Testing:</b> Access: 30 min, Refresh: 1 día, Secret: valor de prueba</li>
 *   <li><b>Producción:</b> Access: 15 min, Refresh: 7 días, Secret: desde variable de entorno</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see JwtTokenService
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sirha.security.jwt")
public class JwtProperties {

    /**
     * Identificador del emisor de los tokens JWT.
     * 
     * <p>Este valor se incluye en el claim "iss" (issuer) del token y se utiliza
     * para validar que el token fue generado por esta aplicación específica.</p>
     * 
     * <p><b>Valor por defecto:</b> "sirha"</p>
     * <p><b>Configuración:</b> {@code sirha.security.jwt.issuer}</p>
     */
    private String issuer = "sirha";

    /**
     * Tiempo de expiración del access token en minutos.
     * 
     * <p>Define cuánto tiempo permanecerá válido un token de acceso después de su emisión.
     * Un valor más corto mejora la seguridad pero requiere renovaciones más frecuentes.</p>
     * 
     * <p><b>Valor por defecto:</b> 60 minutos (1 hora)</p>
     * <p><b>Rango recomendado:</b> 15-60 minutos</p>
     * <p><b>Configuración:</b> {@code sirha.security.jwt.expiration-minutes}</p>
     * 
     * @see JwtTokenService#generateToken(edu.dosw.sirha.security.UserPrincipal)
     */
    private int expirationMinutes = 60;

    /**
     * Tiempo de expiración del refresh token en minutos.
     * 
     * <p>Define cuánto tiempo permanecerá válido un refresh token después de su emisión.
     * Este token permite al usuario obtener nuevos access tokens sin reautenticarse.</p>
     * 
     * <p><b>Valor por defecto:</b> 10080 minutos (7 días)</p>
     * <p><b>Rango recomendado:</b> 7-30 días (10080-43200 minutos)</p>
     * <p><b>Configuración:</b> {@code sirha.security.jwt.refresh-expiration-minutes}</p>
     * 
     * <p><b>Cálculo de días a minutos:</b></p>
     * <ul>
     *   <li>1 día = 1440 minutos</li>
     *   <li>7 días = 10080 minutos</li>
     *   <li>14 días = 20160 minutos</li>
     *   <li>30 días = 43200 minutos</li>
     * </ul>
     * 
     * @see JwtTokenService#generateToken(edu.dosw.sirha.security.UserPrincipal, long)
     */
    private int refreshExpirationMinutes = 10080; // 7 días

    /**
     * Clave secreta para firmar y validar tokens JWT.
     * 
     * <p>Esta clave se utiliza con el algoritmo HS256 (HMAC-SHA256) para:
     * <ul>
     *   <li>Firmar tokens al generarlos (garantiza autenticidad)</li>
     *   <li>Validar la firma de tokens recibidos (detecta manipulación)</li>
     * </ul>
     * </p>
     * 
     * <p><b>⚠️ CRÍTICO PARA SEGURIDAD:</b></p>
     * <ul>
     *   <li>NUNCA debe estar hardcodeada en el código fuente</li>
     *   <li>DEBE cargarse desde variables de entorno en producción</li>
     *   <li>DEBE tener al menos 256 bits (32 caracteres) de longitud</li>
     *   <li>DEBE ser criptográficamente aleatoria y única por entorno</li>
     *   <li>DEBE rotarse periódicamente en producción</li>
     * </ul>
     * 
     * <p><b>Generación de secret seguro (ejemplo con OpenSSL):</b></p>
     * <pre>
     * # Generar secret de 256 bits en base64
     * openssl rand -base64 32
     * 
     * # Generar secret de 256 bits en hexadecimal
     * openssl rand -hex 32
     * </pre>
     * 
     * <p><b>Configuración por entorno:</b></p>
     * <pre>
     * # Desarrollo (application-dev.properties)
     * sirha.security.jwt.secret=dev-secret-key-not-for-production-use-only
     * 
     * # Producción (variables de entorno)
     * export JWT_SECRET="tu-clave-secreta-generada-de-forma-segura-y-aleatoria"
     * sirha.security.jwt.secret=${JWT_SECRET}
     * </pre>
     * 
     * <p><b>Configuración:</b> {@code sirha.security.jwt.secret}</p>
     * 
     * @see JwtTokenService#getSigningKey()
     */
    private String secret;
}