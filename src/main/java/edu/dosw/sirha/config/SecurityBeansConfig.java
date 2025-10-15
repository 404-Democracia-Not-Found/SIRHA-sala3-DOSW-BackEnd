package edu.dosw.sirha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuración de beans relacionados con la seguridad y serialización JSON.
 * 
 * <p>Esta clase define los componentes esenciales para la gestión de seguridad
 * en SIRHA, incluyendo el codificador de contraseñas y el mapeador de objetos JSON.</p>
 * 
 * <p><strong>Componentes principales:</strong></p>
 * <ul>
 *   <li><strong>PasswordEncoder:</strong> Utiliza BCrypt para hash seguro de contraseñas</li>
 *   <li><strong>ObjectMapper:</strong> Maneja la serialización/deserialización de objetos Java a JSON</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0.0
 * @since 2025-10-14
 * @see BCryptPasswordEncoder
 * @see ObjectMapper
 */
@Configuration
public class SecurityBeansConfig {

    /**
     * Proporciona un codificador de contraseñas basado en el algoritmo BCrypt.
     * 
     * <p>BCrypt es un algoritmo de hash adaptativo que incluye un "salt" aleatorio
     * para proteger contra ataques de rainbow tables y fuerza bruta. El algoritmo
     * utiliza un factor de coste que hace que cada operación sea computacionalmente
     * intensiva, lo que dificulta significativamente los intentos de crackeo.</p>
     * 
     * <p><strong>Características de seguridad:</strong></p>
     * <ul>
     *   <li>Salt aleatorio único para cada contraseña</li>
     *   <li>Factor de coste ajustable (por defecto: 10 rondas)</li>
     *   <li>Resistente a ataques de timing</li>
     *   <li>Diseñado para ser lento y dificultar fuerza bruta</li>
     * </ul>
     * 
     * <p><strong>Uso en la aplicación:</strong></p>
     * <ul>
     *   <li>Codificación de contraseñas durante el registro de usuarios</li>
     *   <li>Verificación de contraseñas durante la autenticación</li>
     *   <li>Actualización segura de contraseñas</li>
     * </ul>
     * 
     * @return una instancia de {@link BCryptPasswordEncoder} con configuración por defecto
     * @see BCryptPasswordEncoder
     * @see org.springframework.security.crypto.password.PasswordEncoder#encode(CharSequence)
     * @see org.springframework.security.crypto.password.PasswordEncoder#matches(CharSequence, String)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proporciona un {@link ObjectMapper} configurado para la serialización y deserialización JSON.
     * 
     * <p>Este ObjectMapper está configurado con el módulo JavaTimeModule que permite
     * el manejo correcto de los tipos de fecha y hora de Java 8+ (Instant, LocalDateTime, etc.)
     * sin necesidad de configuración adicional.</p>
     * 
     * <p><strong>Configuraciones incluidas:</strong></p>
     * <ul>
     *   <li><strong>JavaTimeModule:</strong> Soporte para java.time.* (Instant, LocalDate, etc.)</li>
     *   <li>Serialización de fechas en formato ISO-8601</li>
     *   <li>Deserialización automática desde formatos estándar</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Conversión automática de entidades a JSON en respuestas REST</li>
     *   <li>Parsing de requests JSON a objetos Java</li>
     *   <li>Serialización de fechas y timestamps en formato estándar</li>
     *   <li>Manejo de DTOs complejos con tipos de fecha</li>
     * </ul>
     * 
     * @return una instancia de {@link ObjectMapper} configurada con soporte para tipos de tiempo de Java 8+
     * @see ObjectMapper
     * @see JavaTimeModule
     * @see java.time.Instant
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}