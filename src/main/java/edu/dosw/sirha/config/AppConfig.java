package edu.dosw.sirha.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración general de la aplicación SIRHA.
 * 
 * <p>Esta clase proporciona beans de configuración comunes que son utilizados
 * en toda la aplicación, incluyendo la gestión del tiempo y otras utilidades generales.</p>
 * 
 * <p>El uso de un Clock centralizado permite realizar pruebas más fácilmente, ya que
 * podemos simular diferentes zonas horarias y momentos específicos del tiempo.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0.0
 * @since 2025-10-14
 */
@Configuration
public class AppConfig {

    /**
     * Proporciona una instancia de {@link Clock} configurada con la zona horaria UTC.
     * 
     * <p>Este bean es utilizado en toda la aplicación para obtener la fecha y hora actual,
     * garantizando consistencia en el manejo de timestamps y facilitando las pruebas
     * unitarias al poder inyectar un Clock mock cuando sea necesario.</p>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Registro de fechas de creación y actualización de entidades</li>
     *   <li>Validación de períodos académicos activos</li>
     *   <li>Generación de tokens JWT con expiración</li>
     *   <li>Auditoría de operaciones con timestamp preciso</li>
     * </ul>
     * 
     * @return una instancia de {@link Clock} configurada con UTC como zona horaria
     * @see Clock#systemUTC()
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
