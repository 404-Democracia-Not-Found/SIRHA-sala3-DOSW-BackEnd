package edu.dosw.project.service;

import edu.dosw.project.model.PeriodoAcademico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión del calendario académico y períodos habilitados
 */
public interface CalendarioAcademicoService {
    
    /**
     * Obtiene el período académico activo actual
     * 
     * @return Período académico activo, si existe
     */
    Optional<PeriodoAcademico> getPeriodoActivo();
    
    /**
     * Obtiene todos los períodos vigentes para una fecha
     * 
     * @return Lista de períodos vigentes
     */
    List<PeriodoAcademico> getPeriodosVigentes();
    
    /**
     * Verifica si actualmente está en período habilitado para solicitudes
     * 
     * @return true si se pueden hacer solicitudes, false si no
     */
    boolean estaEnPeriodoSolicitudes();
    
    /**
     * Verifica si actualmente está en período habilitado para solicitudes
     * 
     * @return true si se pueden hacer solicitudes, false si no
     */
    boolean esPeriodoSolicitudesActivo();
    
    /**
     * Obtiene el período académico por año y semestre
     * 
     * @param anio Año del período
     * @param semestre Semestre (1 o 2)
     * @return Período académico encontrado
     */
    Optional<PeriodoAcademico> getPeriodoPorAnioYSemestre(Integer anio, Integer semestre);
    
    /**
     * Crea un nuevo período académico
     * 
     * @param anio Año del período
     * @param semestre Semestre (1 o 2)
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Período académico creado
     */
    PeriodoAcademico crearPeriodoAcademico(Integer anio, Integer semestre, 
                                          LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Configura las fechas habilitadas para solicitudes en un período
     * 
     * @param periodoId ID del período académico
     * @param fechaInicio Fecha de inicio para solicitudes
     * @param fechaFin Fecha de fin para solicitudes
     * @return Período académico actualizado
     */
    PeriodoAcademico configurarFechasSolicitudes(String periodoId, 
                                                LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Obtiene todos los períodos académicos de un año específico
     * 
     * @param anio Año a consultar
     * @return Lista de períodos del año
     */
    List<PeriodoAcademico> getPeriodosPorAnio(Integer anio);
    
    /**
     * Activa un período académico y desactiva los demás
     * 
     * @param periodoId ID del período a activar
     * @return Período académico activado
     */
    PeriodoAcademico activarPeriodo(String periodoId);
    
    /**
     * Inicializa los períodos académicos para el año 2025
     * con las fechas especificadas en los requerimientos
     */
    void inicializarPeriodos2025();
    
    /**
     * Valida si una fecha está dentro del período académico
     * 
     * @param fecha Fecha a validar
     * @param periodoId ID del período académico
     * @return true si está dentro del período
     */
    boolean fechaEnPeriodo(LocalDate fecha, String periodoId);
    
    /**
     * Obtiene la fecha límite para responder solicitudes
     * basada en la fecha actual más los días hábiles configurados
     * 
     * @param diasHabiles Número de días hábiles para responder
     * @return Fecha límite para respuesta
     */
    LocalDate calcularFechaLimiteRespuesta(int diasHabiles);
}