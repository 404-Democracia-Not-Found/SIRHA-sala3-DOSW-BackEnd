package edu.dosw.project.service;

import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.Conflicto;
import edu.dosw.project.model.Horario;
import edu.dosw.project.model.Grupo;

import java.util.List;
import java.util.Map;

/**
 * Servicio para la detección automática de conflictos de horario
 * Implementa la lógica de negocio para validar solapamientos
 */
public interface ConflictDetectionService {
    
    /**
     * Detecta conflictos para una solicitud específica
     */
    void detectConflictsForSolicitud(Solicitud solicitud);
    
    /**
     * Detecta conflictos para un horario específico
     */
    Conflicto detectConflictsForHorario(String horarioId);
    
    /**
     * Verifica si un estudiante tiene conflicto de horario con un grupo
     */
    boolean tieneConflictoHorarioEstudiante(String estudianteId, String grupoDestinoId);
    
    /**
     * Analiza conflictos para una solicitud
     */
    Map<String, Object> analizarConflictos(String solicitudId);
    
    /**
     * Detecta conflictos de horario para un estudiante específico
     * al intentar inscribirse en un nuevo grupo
     * 
     * @param estudianteId ID del estudiante
     * @param nuevoGrupoId ID del grupo al que se quiere inscribir
     * @return Lista de conflictos encontrados
     */
    List<Conflicto> detectarConflictosHorario(String estudianteId, String nuevoGrupoId);
    
    /**
     * Detecta conflictos de horario entre dos grupos específicos
     * 
     * @param grupoActualId ID del grupo actual
     * @param grupoDestinoId ID del grupo destino
     * @return true si hay conflictos, false si no
     */
    boolean tieneConflictoHorarioGrupos(String grupoActualId, String grupoDestinoId);
    
    /**
     * Valida si una solicitud de cambio puede ser aprobada
     * considerando conflictos de horario
     * 
     * @param solicitudId ID de la solicitud
     * @return true si puede ser aprobada, false si hay conflictos
     */
    boolean puedeAprobarSolicitud(String solicitudId);
    
    /**
     * Obtiene todos los horarios de un estudiante en un período académico
     * 
     * @param estudianteId ID del estudiante
     * @param periodoAcademicoId ID del período académico
     * @return Lista de horarios del estudiante
     */
    List<Horario> obtenerHorariosEstudiante(String estudianteId, String periodoAcademicoId);
    
    /**
     * Valida si hay solapamiento entre dos horarios específicos
     * 
     * @param horario1 Primer horario
     * @param horario2 Segundo horario
     * @return true si hay solapamiento, false si no
     */
    boolean tieneSolapamiento(Horario horario1, Horario horario2);
    
    /**
     * Busca grupos alternativos sin conflictos para una materia
     * 
     * @param estudianteId ID del estudiante
     * @param materiaId ID de la materia
     * @param periodoAcademicoId ID del período académico
     * @return Lista de grupos disponibles sin conflictos
     */
    List<Grupo> buscarGruposSinConflictos(String estudianteId, String materiaId, String periodoAcademicoId);
    
    /**
     * Valida conflictos de manera masiva para múltiples solicitudes
     * 
     * @param solicitudIds Lista de IDs de solicitudes
     * @return Mapa con resultados de validación por solicitud
     */
    Map<String, Object> validarConflictosMasivo(List<String> solicitudIds);
}