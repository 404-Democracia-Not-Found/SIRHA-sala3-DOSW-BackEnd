package edu.dosw.sirha.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Cargador de variables de entorno desde archivo .env
 * Se ejecuta al inicio de la aplicación para cargar las credenciales de forma segura
 */
public class DotenvApplicationContextInitializer 
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

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