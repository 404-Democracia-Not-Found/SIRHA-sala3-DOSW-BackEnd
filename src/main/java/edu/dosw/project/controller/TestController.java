package edu.dosw.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para endpoints de testing y verificación del sistema
 */
@RestController
@Tag(name = "SISTEMA", description = "Endpoints para testing y verificación de funcionamiento del sistema SIRHA")
public class TestController {

    @GetMapping("/hello")
    @Operation(
        summary = "Verificar Estado del Sistema",
        description = "Endpoint público para verificar que SIRHA está operativo. " +
                     "Usado para monitoreo CI/CD, health checks, validación de estudiantes y diagnóstico de problemas. " +
                     "No requiere autenticación para facilitar monitoreo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sistema SIRHA operativo - Todos los servicios funcionando correctamente"
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Sistema temporalmente no disponible - Verificar logs del servidor"
        )
    })
    public String hello() {
        return "Servidor SIRHA funcionando - Sistema de Reasignación de Horarios Académicos operativo";
    }
}
