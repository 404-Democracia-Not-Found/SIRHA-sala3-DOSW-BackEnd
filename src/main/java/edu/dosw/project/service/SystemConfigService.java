package edu.dosw.project.service;

import java.util.Map;

/**
 * Servicio para configuración del sistema
 */
public interface SystemConfigService {
    
    /**
     * Obtiene la configuración del sistema
     */
    Map<String, Object> getConfiguracion();
    
    /**
     * Actualiza la configuración del sistema
     */
    void actualizarConfiguracion(Map<String, Object> nuevaConfiguracion);
    
    /**
     * Obtiene el estado de salud del sistema
     */
    Map<String, Object> getEstadoSalud();
    
    /**
     * Limpia datos temporales
     */
    void limpiarDatosTemporales();
    
    /**
     * Inicia un backup
     */
    String iniciarBackup();
}