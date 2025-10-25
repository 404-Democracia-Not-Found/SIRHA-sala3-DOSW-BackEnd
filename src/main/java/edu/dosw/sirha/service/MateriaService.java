package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;

import java.util.List;

/**
 * Servicio de gestión del catálogo de materias.
 * 
 * <p>Proporciona operaciones CRUD para materias, búsqueda por texto,
 * filtrado por facultad y validación de prerequisitos.</p>
 * 
 * @see edu.dosw.sirha.model.Materia
 * @see MateriaRequest
 * @see MateriaResponse
 */
public interface MateriaService {

	/**
	 * Crea una nueva materia en el catálogo.
	 * 
	 * @param request Datos de la materia a crear
	 * @return La materia creada con ID asignado
	 */
	MateriaResponse create(MateriaRequest request);

	/**
	 * Actualiza una materia existente.
	 * 
	 * @param id ID de la materia a actualizar
	 * @param request Nuevos datos de la materia
	 * @return La materia actualizada
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 */
	MateriaResponse update(String id, MateriaRequest request);

	/**
	 * Elimina una materia del catálogo.
	 * 
	 * @param id ID de la materia a eliminar
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 */
	void delete(String id);

	/**
	 * Busca una materia por ID.
	 * 
	 * @param id ID de la materia
	 * @return La materia encontrada
	 * @throws edu.dosw.sirha.exception.ResourceNotFoundException si no existe
	 */
	MateriaResponse findById(String id);

	/**
	 * Obtiene todas las materias del catálogo.
	 * 
	 * @return Lista de todas las materias
	 */
	List<MateriaResponse> findAll();

	/**
	 * Busca materias de una facultad específica.
	 * 
	 * @param facultadId ID de la facultad
	 * @return Lista de materias de la facultad
	 */
	List<MateriaResponse> findByFacultad(String facultadId);

	/**
	 * Busca materias por término de búsqueda.
	 * 
	 * <p>Busca en mnemonico, nombre y términos de búsqueda adicionales.</p>
	 * 
	 * @param term Término de búsqueda
	 * @return Lista de materias que coinciden
	 */
	List<MateriaResponse> search(String term);
}