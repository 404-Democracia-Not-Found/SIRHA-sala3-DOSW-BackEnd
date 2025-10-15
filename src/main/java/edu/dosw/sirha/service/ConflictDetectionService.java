package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;

import java.util.List;

/**
 * Servicio de detección y gestión de conflictos académicos.
 * 
 * <p>Identifica automáticamente conflictos al procesar solicitudes:</p>
 * <ul>
 *   <li>Solapamiento de horarios entre materias</li>
 *   <li>Prerequisitos faltantes</li>
 *   <li>Exceso de créditos permitidos</li>
 *   <li>Grupos llenos sin cupos</li>
 * </ul>
 * 
 * <p>También permite registro manual de conflictos no detectados automáticamente.</p>
 * 
 * @see edu.dosw.sirha.model.Conflict
 * @see ConflictRequest
 * @see ConflictResponse
 */
public interface ConflictDetectionService {

    /**
     * Registra un nuevo conflicto (manual o automático).
     * 
     * @param request Datos del conflicto a registrar
     * @return El conflicto registrado con ID asignado
     */
    ConflictResponse registrar(ConflictRequest request);

    /**
     * Actualiza un conflicto existente.
     * 
     * @param id ID del conflicto a actualizar
     * @param request Nuevos datos del conflicto
     * @return El conflicto actualizado
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
     */
    ConflictResponse actualizar(String id, ConflictRequest request);

    /**
     * Marca un conflicto como resuelto o no resuelto.
     * 
     * @param id ID del conflicto
     * @param resuelto true para marcar resuelto, false para marcar pendiente
     * @param observaciones Notas sobre la resolución
     * @return El conflicto actualizado
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
     */
    ConflictResponse marcarResuelto(String id, boolean resuelto, String observaciones);

    /**
     * Busca un conflicto por ID.
     * 
     * @param id ID del conflicto
     * @return El conflicto encontrado
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
     */
    ConflictResponse findById(String id);

    /**
     * Obtiene todos los conflictos registrados.
     * 
     * @return Lista de todos los conflictos
     */
    List<ConflictResponse> findAll();

    /**
     * Busca conflictos de un estudiante específico.
     * 
     * @param estudianteId ID del estudiante
     * @return Lista de conflictos del estudiante
     */
    List<ConflictResponse> findByEstudiante(String estudianteId);

    /**
     * Busca conflictos asociados a una solicitud.
     * 
     * @param solicitudId ID de la solicitud
     * @return Lista de conflictos detectados en esa solicitud
     */
    List<ConflictResponse> findBySolicitud(String solicitudId);

    /**
     * Elimina un conflicto.
     * 
     * @param id ID del conflicto a eliminar
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
     */
    void delete(String id);
}