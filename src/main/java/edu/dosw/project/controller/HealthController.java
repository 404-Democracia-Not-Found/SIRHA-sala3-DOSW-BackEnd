package edu.dosw.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para endpoints de monitoreo y validación del sistema
 */
@RestController
@Tag(name = "SISTEMA", description = "Endpoints para validación de funcionamiento y monitoreo del sistema SIRHA")
public class HealthController {

    @GetMapping("/health")
    @Operation(
        summary = "Health Check del Sistema",
        description = "Verifica que Spring Boot esté inicializado, Tomcat respondiendo y el sistema SIRHA operativo. " +
                     "Útil para testing automatizado, monitoreo de infraestructura y validación post-deployment."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sistema SIRHA completamente operativo")
    })
    public String healthCheck() {
        return "SIRHA SISTEMA OPERATIVO - Todos los servicios funcionando correctamente";
    }
}