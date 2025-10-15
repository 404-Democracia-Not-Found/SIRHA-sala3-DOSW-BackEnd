package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;

import java.time.Instant;
import java.util.List;

/**
 * Servicio de gestión de periodos académicos.
 * 
 * <p>Proporciona operaciones CRUD para periodos y lógica de activación.
 * Solo un periodo puede estar activo a la vez.</p>
 * 
 * @see edu.dosw.sirha.model.Periodo
 * @see PeriodoRequest
 * @see PeriodoResponse
 */
public interface PeriodoService {

	/**
	 * Crea un nuevo periodo académico.
	 * 
	 * @param request Datos del periodo a crear
	 * @return El periodo creado con ID asignado
	 */
	PeriodoResponse create(PeriodoRequest request);

	/**
	 * Actualiza un periodo existente.
	 * 
	 * @param id ID del periodo a actualizar
	 * @param request Nuevos datos del periodo
	 * @return El periodo actualizado
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 */
	PeriodoResponse update(String id, PeriodoRequest request);

	/**
	 * Elimina un periodo académico.
	 * 
	 * @param id ID del periodo a eliminar
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 */
	void delete(String id);

	/**
	 * Busca un periodo por ID.
	 * 
	 * @param id ID del periodo
	 * @return El periodo encontrado
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 */
	PeriodoResponse findById(String id);

	/**
	 * Obtiene todos los periodos académicos.
	 * 
	 * @return Lista de todos los periodos
	 */
	List<PeriodoResponse> findAll();

	/**
	 * Obtiene el periodo actualmente activo.
	 * 
	 * @return El periodo activo
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no hay periodo activo
	 */
	PeriodoResponse findActive();

	/**
	 * Marca un periodo como activo y desactiva los demás.
	 * 
	 * @param id ID del periodo a activar
	 * @return El periodo ahora activo
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 */
	PeriodoResponse markAsActive(String id);

	/**
	 * Verifica si una fecha está dentro del periodo activo.
	 * 
	 * @param fecha Fecha a verificar
	 * @return true si la fecha está dentro del periodo activo, false si no
	 */
	boolean isWithinPeriodo(Instant fecha);
}