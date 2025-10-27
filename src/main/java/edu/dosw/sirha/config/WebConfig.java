package edu.dosw.sirha.config;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Configuración web de Spring MVC para la aplicación SIRHA.
 * 
 * <p>Esta clase configura aspectos fundamentales del comportamiento web de la aplicación,
 * incluyendo la gestión de localización (i18n) y las políticas de intercambio de recursos
 * de origen cruzado (CORS) para permitir que el frontend se comunique con el backend.</p>
 * 
 * <p><strong>Configuraciones principales:</strong></p>
 * <ul>
 *   <li><strong>Localización:</strong> Configura español de Colombia (es-CO) como idioma por defecto</li>
 *   <li><strong>CORS:</strong> Define políticas de acceso para peticiones desde diferentes orígenes</li>
 * </ul>
 * 
 * <p><strong>Integración con frontend:</strong></p>
 * <p>La configuración CORS permite que aplicaciones frontend (React, Angular, Vue, etc.)
 * ejecutándose en diferentes puertos o dominios puedan comunicarse con la API REST
 * de forma segura y controlada.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0.0
 * @since 2025-10-14
 * @see WebMvcConfigurer
 * @see LocaleResolver
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura el resolver de localización para la aplicación.
     * 
     * <p>Este bean define cómo se determina y almacena el locale (idioma y región)
     * para cada sesión de usuario. El locale afecta el formato de fechas, números
     * y mensajes mostrados al usuario.</p>
     * 
     * <p><strong>Configuración actual:</strong></p>
     * <ul>
     *   <li><strong>Locale por defecto:</strong> Español de Colombia (es-CO)</li>
     *   <li><strong>Almacenamiento:</strong> En la sesión HTTP del usuario</li>
     *   <li><strong>Persistencia:</strong> El locale se mantiene durante toda la sesión</li>
     * </ul>
     * 
     * <p><strong>Impacto en la aplicación:</strong></p>
     * <ul>
     *   <li>Mensajes de error y validación en español</li>
     *   <li>Formato de fechas: DD/MM/YYYY</li>
     *   <li>Separador decimal: coma (,)</li>
     *   <li>Separador de miles: punto (.)</li>
     * </ul>
     * 
     * <p><strong>Cambio dinámico de locale:</strong></p>
     * <p>Aunque el locale por defecto es es-CO, puede cambiarse dinámicamente
     * durante la ejecución mediante un {@code LocaleChangeInterceptor} si se
     * configura posteriormente.</p>
     * 
     * @return instancia configurada de {@link SessionLocaleResolver} con es-CO como locale por defecto
     * @see SessionLocaleResolver
     * @see Locale#forLanguageTag(String)
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.forLanguageTag("es-CO"));
        return slr;
    }

    /**
     * Configura las políticas CORS (Cross-Origin Resource Sharing) para la API.
     * 
     * <p>CORS es un mecanismo de seguridad del navegador que restringe las peticiones HTTP
     * desde un origen (dominio/puerto) hacia otro origen diferente. Esta configuración
     * define qué orígenes están autorizados para acceder a la API de SIRHA.</p>
     * 
     * <p><strong>Configuración actual:</strong></p>
     * <ul>
     *   <li><strong>Endpoints protegidos:</strong> Todos bajo el path /api/**</li>
     *   <li><strong>Orígenes permitidos:</strong> localhost:3000, 127.0.0.1:3000 (configurable vía ALLOWED_ORIGINS)</li>
     *   <li><strong>Métodos HTTP:</strong> GET, POST, PUT, DELETE, PATCH</li>
     *   <li><strong>Headers permitidos:</strong> Authorization, Content-Type, Accept</li>
     *   <li><strong>Credenciales:</strong> Habilitadas (permite cookies y headers de autenticación)</li>
     *   <li><strong>Cache preflight:</strong> 1 hora (3600 segundos)</li>
     * </ul>
     * 
     * <p><strong>Seguridad:</strong></p>
     * <ul>
     *   <li>Solo los orígenes explícitamente listados pueden acceder a la API</li>
     *   <li>Las credenciales (como tokens JWT) están habilitadas para autenticación</li>
     *   <li>Los headers están limitados a solo los necesarios para la aplicación</li>
     * </ul>
     * 
     * <p><strong>Configuración por entorno:</strong></p>
     * <p>Los orígenes permitidos pueden configurarse mediante la variable de entorno
     * {@code ALLOWED_ORIGINS}. Si no está definida, usa valores por defecto para
     * desarrollo local:</p>
     * <pre>{@code
     * # Desarrollo (por defecto)
     * http://localhost:3000,http://127.0.0.1:3000
     * 
     * # Producción (ejemplo)
     * ALLOWED_ORIGINS=https://sirha.escuelaing.edu.co,https://www.sirha.escuelaing.edu.co
     * }</pre>
     * 
     * <p><strong>Peticiones preflight:</strong></p>
     * <p>El navegador envía una petición OPTIONS antes de peticiones complejas (con
     * headers personalizados o métodos distintos a GET/POST simple). El maxAge de
     * 3600 segundos indica que el navegador puede cachear la respuesta preflight
     * durante 1 hora, reduciendo peticiones innecesarias.</p>
     * 
     * <p><strong>⚠️ Importante para producción:</strong></p>
     * <p>En producción, debe configurarse ALLOWED_ORIGINS con los dominios reales
     * de la aplicación frontend. No usar comodines (*) con allowCredentials(true)
     * por razones de seguridad.</p>
     * 
     * @param registry registro de configuración CORS de Spring MVC
     * @see CorsRegistry
     * @see org.springframework.web.cors.CorsConfiguration
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "${ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173}"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}