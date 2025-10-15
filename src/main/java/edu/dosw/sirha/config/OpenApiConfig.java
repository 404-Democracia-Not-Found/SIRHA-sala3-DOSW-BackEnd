package edu.dosw.sirha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SIRHA API")
                        .version("1.0.0")
                        .description("Sistema de Reasignación de Horarios Académicos - API REST para la gestión de solicitudes de cambio de materias y grupos")
                        .contact(new Contact()
                                .name("Equipo SIRHA")
                                .email("sirha@escuelaing.edu.co")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido del endpoint /api/auth/login")
                        )
                        .addResponses("BadRequest", new ApiResponse()
                                .description("Error de validación o regla de negocio")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>()
                                                .addProperty("timestamp", new Schema<>().type("string").format("date-time"))
                                                .addProperty("status", new Schema<>().type("integer").example(400))
                                                .addProperty("error", new Schema<>().type("string").example("Bad Request"))
                                                .addProperty("errorCode", new Schema<>().type("string").example("SIRHA-400-001"))
                                                .addProperty("message", new Schema<>().type("string").example("Descripción del error"))
                                                .addProperty("path", new Schema<>().type("string").example("/api/solicitudes"))
                                        )))
                        )
                        .addResponses("NotFound", new ApiResponse()
                                .description("Recurso no encontrado")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>()
                                                .addProperty("timestamp", new Schema<>().type("string").format("date-time"))
                                                .addProperty("status", new Schema<>().type("integer").example(404))
                                                .addProperty("error", new Schema<>().type("string").example("Not Found"))
                                                .addProperty("errorCode", new Schema<>().type("string").example("SIRHA-404-001"))
                                                .addProperty("message", new Schema<>().type("string").example("Recurso no encontrado"))
                                                .addProperty("path", new Schema<>().type("string").example("/api/solicitudes/123"))
                                        )))
                        )
                        .addResponses("Unauthorized", new ApiResponse()
                                .description("No autorizado - Token inválido o faltante")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>()
                                                .addProperty("timestamp", new Schema<>().type("string").format("date-time"))
                                                .addProperty("status", new Schema<>().type("integer").example(401))
                                                .addProperty("error", new Schema<>().type("string").example("Unauthorized"))
                                                .addProperty("errorCode", new Schema<>().type("string").example("SIRHA-401-001"))
                                                .addProperty("message", new Schema<>().type("string").example("Credenciales inválidas"))
                                        )))
                        )
                        .addResponses("Forbidden", new ApiResponse()
                                .description("Acceso prohibido - Sin permisos suficientes")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>()
                                                .addProperty("timestamp", new Schema<>().type("string").format("date-time"))
                                                .addProperty("status", new Schema<>().type("integer").example(403))
                                                .addProperty("error", new Schema<>().type("string").example("Forbidden"))
                                                .addProperty("errorCode", new Schema<>().type("string").example("SIRHA-403-001"))
                                                .addProperty("message", new Schema<>().type("string").example("Acceso denegado"))
                                        )))
                        )
                        .addResponses("ValidationError", new ApiResponse()
                                .description("Errores de validación en los datos de entrada")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>()
                                                .addProperty("timestamp", new Schema<>().type("string").format("date-time"))
                                                .addProperty("status", new Schema<>().type("integer").example(400))
                                                .addProperty("error", new Schema<>().type("string").example("Bad Request"))
                                                .addProperty("errorCode", new Schema<>().type("string").example("SIRHA-400-002"))
                                                .addProperty("message", new Schema<>().type("string").example("Errores de validación"))
                                                .addProperty("validationErrors", new Schema<>().type("object")
                                                        .additionalProperties(new Schema<>().type("string"))
                                                        .example(java.util.Map.of("campo1", "Error en campo1", "campo2", "Error en campo2"))
                                                )
                                        )))
                        )
                );
    }
}