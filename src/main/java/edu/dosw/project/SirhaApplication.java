package edu.dosw.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class SirhaApplication {
    public static void main(String[] args) {
        // Configuraciones adicionales para asegurar que el servidor se mantenga activo
        System.setProperty("spring.main.web-application-type", "servlet");
        SpringApplication.run(SirhaApplication.class, args);
    }
}
