package edu.dosw.sirha.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Inicializador de contexto para cargar variables de entorno desde archivo .env.
 * 
 * <p>Esta clase implementa {@link ApplicationContextInitializer} para cargar variables
 * de entorno desde un archivo .env antes de que Spring Boot inicialice completamente
 * el contexto de la aplicación. Esto permite mantener credenciales sensibles y
 * configuraciones específicas del entorno fuera del código fuente.</p>
 * 
 * <p><strong>Ventajas de usar archivos .env:</strong></p>
 * <ul>
 *   <li><strong>Seguridad:</strong> Las credenciales no se almacenan en el repositorio</li>
 *   <li><strong>Flexibilidad:</strong> Diferentes configuraciones por entorno sin cambiar código</li>
 *   <li><strong>Portabilidad:</strong> Fácil configuración en diferentes máquinas de desarrollo</li>
 *   <li><strong>Estándar de industria:</strong> Patrón ampliamente adoptado (12-factor app)</li>
 * </ul>
 * 
 * <p><strong>Variables de entorno soportadas:</strong></p>
 * <ul>
 *   <li><strong>MONGODB_URI:</strong> URI completa de conexión a MongoDB Atlas</li>
 *   <li><strong>MONGODB_DATABASE:</strong> Nombre de la base de datos</li>
 *   <li><strong>JWT_SECRET:</strong> Clave secreta para firma de tokens JWT</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de archivo .env:</strong></p>
 * <pre>{@code
 * MONGODB_URI=mongodb+srv://usuario:password@cluster.mongodb.net/
 * MONGODB_DATABASE=SIRHA
 * JWT_SECRET=mi-clave-secreta-super-segura-de-al-menos-256-bits
 * }</pre>
 * 
 * <p><strong>Configuración en application.yml:</strong></p>
 * <p>Para que Spring Boot utilice este inicializador, debe estar declarado en
 * META-INF/spring.factories o configurado en application.yml:</p>
 * <pre>{@code
 * spring:
 *   application:
 *     context:
 *       initializer:
 *         classes: edu.dosw.sirha.config.DotenvApplicationContextInitializer
 * }</pre>
 * 
 * <p><strong>Comportamiento ante errores:</strong></p>
 * <p>Si el archivo .env no existe o está malformado, la aplicación continuará su
 * ejecución normalmente pero mostrará una advertencia. Esto permite que la aplicación
 * funcione con variables de entorno del sistema operativo o valores por defecto.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0.0
 * @since 2025-10-14
 * @see ApplicationContextInitializer
 * @see Dotenv
 * @see MapPropertySource
 */
public class DotenvApplicationContextInitializer 
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    /**
     * Inicializa el contexto de la aplicación cargando variables desde el archivo .env.
     * 
     * <p>Este método se ejecuta muy temprano en el ciclo de vida de Spring Boot,
     * antes de que se procesen los archivos de configuración application.yml/properties.
     * Esto permite que las variables del .env sobrescriban valores por defecto.</p>
     * 
     * <p><strong>Proceso de carga:</strong></p>
     * <ol>
     *   <li>Busca el archivo .env en el directorio raíz del proyecto</li>
     *   <li>Lee y parsea las variables definidas en el archivo</li>
     *   <li>Filtra y mapea solo las variables relevantes para SIRHA</li>
     *   <li>Inyecta las variables en el entorno de Spring con máxima prioridad</li>
     *   <li>En caso de error, registra advertencia pero no detiene la aplicación</li>
     * </ol>
     * 
     * <p><strong>Prioridad de propiedades:</strong></p>
     * <p>Las variables cargadas desde .env tienen la máxima prioridad (addFirst),
     * lo que significa que sobrescribirán cualquier valor definido en application.yml
     * o variables de entorno del sistema operativo.</p>
     * 
     * <p><strong>Seguridad:</strong></p>
     * <ul>
     *   <li>El archivo .env debe estar en .gitignore para no commitear credenciales</li>
     *   <li>En producción, usar variables de entorno del sistema es más seguro</li>
     *   <li>Las credenciales nunca deben estar hardcodeadas en el código</li>
     * </ul>
     * 
     * @param applicationContext el contexto configurable de la aplicación Spring
     * @throws RuntimeException si hay un error crítico durante la inicialización
     *         (capturado y convertido en advertencia)
     * @see Dotenv#configure()
     * @see ConfigurableEnvironment#getPropertySources()
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            // Cargar archivo .env si existe
            Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
            
            Map<String, Object> envProperties = new HashMap<>();
            
            // Mapear variables de entorno relevantes
            String mongoUri = dotenv.get("MONGODB_URI");
            if (mongoUri != null) {
                envProperties.put("MONGODB_URI", mongoUri);
            }
            
            String mongoDatabase = dotenv.get("MONGODB_DATABASE");
            if (mongoDatabase != null) {
                envProperties.put("MONGODB_DATABASE", mongoDatabase);
            }
            
            String jwtSecret = dotenv.get("JWT_SECRET");
            if (jwtSecret != null) {
                envProperties.put("JWT_SECRET", jwtSecret);
            }
            
            // Agregar propiedades al entorno de Spring
            if (!envProperties.isEmpty()) {
                environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenv", envProperties));
            }
            
        } catch (Exception e) {
            // Log de advertencia pero no falla la aplicación
            System.out.println("Advertencia: No se pudo cargar archivo .env: " + e.getMessage());
        }
    }
}