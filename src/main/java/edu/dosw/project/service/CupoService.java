package edu.dosw.project.service;

import edu.dosw.project.model.Grupo;

import java.util.List;

/**
 * Servicio para la gestión de cupos y disponibilidad en grupos
 */
public interface CupoService {
    
    /**
     * Verifica si un grupo tiene cupo disponible
     * 
     * @param grupoId ID del grupo
     * @return true si tiene cupo disponible, false si está lleno
     */
    boolean tieneCupoDisponible(String grupoId);
    
    /**
     * Obtiene el número de cupos disponibles en un grupo
     * 
     * @param grupoId ID del grupo
     * @return cantidad de cupos disponibles
     */
    int getCuposDisponibles(String grupoId);
    
    /**
     * Incrementa el contador de estudiantes inscritos en un grupo
     * 
     * @param grupoId ID del grupo
     * @return true si se pudo incrementar, false si el grupo está lleno
     */
    boolean incrementarInscritos(String grupoId);
    
    /**
     * Decrementa el contador de estudiantes inscritos en un grupo
     * 
     * @param grupoId ID del grupo
     * @return true si se pudo decrementar, false si ya está en 0
     */
    boolean decrementarInscritos(String grupoId);
    
    /**
     * Obtiene todos los grupos con cupo disponible para una materia
     * 
     * @param materiaId ID de la materia
     * @param periodoAcademicoId ID del período académico
     * @return Lista de grupos con cupo disponible
     */
    List<Grupo> getGruposConCupo(String materiaId, String periodoAcademicoId);
    
    /**
     * Verifica si un grupo está en capacidad crítica (90% o más ocupado)
     * 
     * @param grupoId ID del grupo
     * @return true si está en capacidad crítica
     */
    boolean estaEnCapacidadCritica(String grupoId);
    
    /**
     * Obtiene todos los grupos en capacidad crítica
     * 
     * @return Lista de grupos en capacidad crítica
     */
    List<Grupo> getGruposEnCapacidadCritica();
    
    /**
     * Calcula el porcentaje de ocupación de un grupo
     * 
     * @param grupoId ID del grupo
     * @return porcentaje de ocupación (0-100)
     */
    double calcularPorcentajeOcupacion(String grupoId);
    
    /**
     * Agrega un estudiante a la lista de espera de un grupo
     * 
     * @param grupoId ID del grupo
     * @param estudianteId ID del estudiante
     * @return true si se agregó exitosamente
     */
    boolean agregarAListaEspera(String grupoId, String estudianteId);
    
    /**
     * Remueve un estudiante de la lista de espera de un grupo
     * 
     * @param grupoId ID del grupo
     * @param estudianteId ID del estudiante
     * @return true si se removió exitosamente
     */
    boolean removerDeListaEspera(String grupoId, String estudianteId);
    
    /**
     * Obtiene la posición de un estudiante en la lista de espera
     * 
     * @param grupoId ID del grupo
     * @param estudianteId ID del estudiante
     * @return posición en la lista (1-indexed), -1 si no está en la lista
     */
    int getPosicionEnListaEspera(String grupoId, String estudianteId);
}