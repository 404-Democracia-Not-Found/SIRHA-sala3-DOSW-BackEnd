package edu.dosw.project.service;

import java.util.List;
import java.util.Map;

/**
 * Servicio para generación de reportes
 */
public interface ReportService {
    
    /**
     * Genera reporte de uso del sistema
     */
    Map<String, Object> generarReporteUsoSistema(String fechaInicio, String fechaFin);
    
    /**
     * Genera reporte de rendimiento por programas
     */
    List<Map<String, Object>> generarReporteRendimientoProgramas();
    
    /**
     * Exporta datos completos del sistema
     */
    byte[] exportarDatosCompletos(String fechaInicio, String fechaFin);
}