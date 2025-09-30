package edu.dosw.project.service;

import edu.dosw.project.model.Grupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Servicio para gestión de grupos académicos
 */
public interface GrupoService {
    
    /**
     * Busca grupos por docente y periodo
     */
    List<Grupo> findByDocenteIdAndPeriodo(String docenteId, String periodoId);
    
    /**
     * Busca grupos por docente y periodo con paginación
     */
    Page<Grupo> findByDocenteIdAndPeriodo(String docenteId, String periodoId, Pageable pageable);
    
    /**
     * Verifica si un grupo pertenece a un docente
     */
    boolean perteneceADocente(String grupoId, String docenteId);
    
    /**
     * Busca historial de grupos de un docente
     */
    Page<Grupo> findHistorialByDocente(String docenteId, Pageable pageable);
    
    /**
     * Exporta lista de estudiantes de un grupo
     */
    String exportarListaEstudiantes(String grupoId);
}