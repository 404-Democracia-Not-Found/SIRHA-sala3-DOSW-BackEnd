package edu.dosw.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/sistema")
@Tag(name = "Sistema", description = "Endpoints del sistema para monitoreo y estado")
public class SystemController {
    
    @GetMapping("/salud")
    @Operation(summary = "Verificación de salud del sistema", 
               description = "Verifica el estado operativo del sistema SIRHA")
    @ApiResponse(responseCode = "200", description = "Sistema operativo")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = Map.of(
            "estado", "OPERATIVO",
            "servicio", "SIRHA Backend",
            "timestamp", LocalDateTime.now().toString(),
            "version", "1.0.0",
            "baseDatos", "MongoDB Atlas",
            "documentacion", "/swagger-ui.html"
        );
        return ResponseEntity.ok(response);
    }
}
