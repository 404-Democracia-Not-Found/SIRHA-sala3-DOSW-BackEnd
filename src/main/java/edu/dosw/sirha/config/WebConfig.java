
package edu.dosw.sirha.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS para endpoints de autenticación (con acento)
        registry.addMapping("/Autenticación/**")
            .allowedOrigins(
                "https://sirha-sala3-dosw-frontend.vercel.app",
                "http://localhost:3000"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Content-Type", "Authorization", "Accept")
            .allowCredentials(true)
            .maxAge(3600);

        // CORS para endpoints sin acento (opcional, si usas /api/*)
        registry.addMapping("/api/**")
            .allowedOrigins(
                "https://sirha-sala3-dosw-frontend.vercel.app",
                "http://localhost:3000"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Content-Type", "Authorization", "Accept")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
