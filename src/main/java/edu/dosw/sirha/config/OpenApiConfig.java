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

/**
 * Configuración de OpenAPI/Swagger para la documentación interactiva de la API REST.
 * 
 * <p>Esta clase configura la especificación OpenAPI 3.0 para SIRHA, proporcionando
 * una interfaz web interactiva (Swagger UI) donde los desarrolladores pueden explorar,
 * probar y comprender todos los endpoints disponibles en la API.</p>
 * 
 * <p><strong>Características principales:</strong></p>
 * <ul>
 *   <li><strong>Autenticación JWT:</strong> Configuración del esquema de seguridad Bearer Token</li>
 *   <li><strong>Respuestas estandarizadas:</strong> Definición de formatos de error comunes</li>
 *   <li><strong>Validación de datos:</strong> Documentación de errores de validación</li>
 *   <li><strong>Información del proyecto:</strong> Metadatos, versión y contacto</li>
 * </ul>
 * 
 * <p><strong>Acceso a la documentación:</strong></p>
 * <ul>
 *   <li>Swagger UI: {@code http://localhost:8081/swagger-ui.html}</li>
 *   <li>Especificación JSON: {@code http://localhost:8081/v3/api-docs}</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0.0
 * @since 2025-10-14
 * @see OpenAPI
 * @see io.swagger.v3.oas.annotations.Operation
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configura la especificación OpenAPI para la documentación de la API de SIRHA.
     * 
     * <p>Este método define todos los aspectos de la documentación OpenAPI incluyendo:</p>
     * <ul>
     *   <li>Información general del proyecto y contacto</li>
     *   <li>Esquemas de autenticación (JWT Bearer Token)</li>
     *   <li>Respuestas HTTP estandarizadas para errores comunes</li>
     *   <li>Formatos de error con códigos personalizados SIRHA</li>
     * </ul>
     * 
     * <p><strong>Esquema de autenticación:</strong></p>
     * <p>La API utiliza tokens JWT que deben incluirse en el header Authorization
     * con el formato: {@code Bearer <token>}. El token se obtiene del endpoint
     * {@code POST /api/v1/auth/login}.</p>
     * 
     * <p><strong>Códigos de error estandarizados:</strong></p>
     * <ul>
     *   <li><strong>400 Bad Request:</strong> Errores de validación o reglas de negocio (SIRHA-400-xxx)</li>
     *   <li><strong>401 Unauthorized:</strong> Token inválido, expirado o faltante (SIRHA-401-xxx)</li>
     *   <li><strong>403 Forbidden:</strong> Sin permisos para acceder al recurso (SIRHA-403-xxx)</li>
     *   <li><strong>404 Not Found:</strong> Recurso solicitado no encontrado (SIRHA-404-xxx)</li>
     * </ul>
     * 
     * <p><strong>Formato de respuesta de error:</strong></p>
     * <pre>{@code
     * {
     *   "timestamp": "2025-10-14T10:30:00Z",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "errorCode": "SIRHA-400-001",
     *   "message": "Descripción detallada del error",
     *   "path": "/api/solicitudes"
     * }
     * }</pre>
     * 
     * <p><strong>Ejemplo de uso en controladores:</strong></p>
     * <pre>{@code
     * @Operation(
     *     summary = "Obtener solicitud por ID",
     *     responses = {
     *         @ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
     *         @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
     *         @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
     *     }
     * )
     * public ResponseEntity<SolicitudResponse> obtenerSolicitud(@PathVariable String id)
     * }</pre>
     * 
     * @return configuración completa de OpenAPI para la API de SIRHA
     * @see SecurityScheme.Type#HTTP
     * @see ApiResponse
     */
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