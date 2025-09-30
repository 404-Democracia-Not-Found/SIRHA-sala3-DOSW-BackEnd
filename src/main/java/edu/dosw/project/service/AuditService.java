package edu.dosw.project.service;

import edu.dosw.project.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para auditoría del sistema
 */
public interface AuditService {
    
    /**
     * Busca logs con filtros
     */
    Page<AuditLog> findLogsWithFilters(String accion, String usuarioId, 
                                      String fechaInicio, String fechaFin, 
                                      Pageable pageable);
}