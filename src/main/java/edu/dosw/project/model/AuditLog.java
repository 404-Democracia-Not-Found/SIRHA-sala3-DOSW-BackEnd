package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo para logs de auditoría
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog {
    
    @Id
    private String id;
    
    private String accion;
    
    private String usuarioId;
    
    private String detalles;
    
    private LocalDateTime fechaHora;
    
    private String ip;
    
    private String resultado;
}