package edu.dosw.project.service;

import edu.dosw.project.model.ProgramaAcademico;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestión de programas académicos
 */
public interface ProgramaAcademicoService {
    
    /**
     * Busca programa por ID
     */
    Optional<ProgramaAcademico> findById(String programaId);
    
    /**
     * Obtiene todos los programas
     */
    List<ProgramaAcademico> findAll();
    
    /**
     * Crea un nuevo programa
     */
    ProgramaAcademico createPrograma(ProgramaAcademico programa);
    
    /**
     * Configura auto-aprobación para un programa
     */
    Map<String, Object> configurarAutoAprobacion(String programaId, Map<String, Object> configuracion);
}