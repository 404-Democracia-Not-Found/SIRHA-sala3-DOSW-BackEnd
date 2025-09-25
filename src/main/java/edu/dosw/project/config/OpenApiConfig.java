package edu.dosw.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SIRHA API")
                        .version("1.0.0")
                        .description("Sistema de Reserva de Horarios Académicos - API REST para gestión de usuarios, solicitudes y horarios")
                        .contact(new Contact()
                                .name("Equipo SIRHA")
                                .email("sirha@dosw.edu.co")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo")
                ));
    }
}