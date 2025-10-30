package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.FacultadRequest;
import edu.dosw.sirha.dto.response.FacultadResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.FacultadMapper;
import edu.dosw.sirha.model.Facultad;
import edu.dosw.sirha.model.User;
import edu.dosw.sirha.repository.FacultadRepository;
import edu.dosw.sirha.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar Facultades.
 * 
 * <p>Implementa la lógica de negocio para operaciones CRUD sobre facultades,
 * incluyendo validaciones de integridad y reglas de negocio.</p>
 * 
 * <p><strong>Reglas de negocio implementadas:</strong></p>
 * <ul>
 *   <li>No pueden existir dos facultades con el mismo nombre</li>
 *   <li>Los créditos totales deben ser positivos</li>
 *   <li>El número de materias no puede ser negativo</li>
 *   <li>Si se asigna un decano, debe existir en el sistema</li>
 *   <li>No se puede eliminar una facultad con materias asociadas</li>
 *   <li>Una facultad inactiva no puede ser asignada a nuevas materias</li>
 * </ul>
 * 
 * @see Facultad
 * @see FacultadRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FacultadService {

    private final FacultadRepository facultadRepository;
    private final UserRepository userRepository;
    private final FacultadMapper facultadMapper;

    /**
     * Obtiene todas las facultades del sistema.
     * 
     * @return lista de todas las facultades
     */
    @Transactional(readOnly = true)
    public List<FacultadResponse> findAll() {
        log.info("Obteniendo todas las facultades");
        return facultadRepository.findAll().stream()
                .map(facultad -> {
                    User decano = facultad.getDecanoId() != null 
                            ? userRepository.findById(facultad.getDecanoId()).orElse(null)
                            : null;
                    return facultadMapper.toResponse(facultad, decano);
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo las facultades activas.
     * 
     * @return lista de facultades activas
     */
    @Transactional(readOnly = true)
    public List<FacultadResponse> findAllActive() {
        log.info("Obteniendo facultades activas");
        return facultadRepository.findByActivoTrue().stream()
                .map(facultad -> {
                    User decano = facultad.getDecanoId() != null 
                            ? userRepository.findById(facultad.getDecanoId()).orElse(null)
                            : null;
                    return facultadMapper.toResponse(facultad, decano);
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una facultad por su ID.
     * 
     * @param id ID de la facultad
     * @return facultad encontrada
     * @throws ResourceNotFoundException si la facultad no existe
     */
    @Transactional(readOnly = true)
    public FacultadResponse findById(String id) {
        log.info("Buscando facultad con ID: {}", id);
        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada con ID: " + id));
        
        User decano = facultad.getDecanoId() != null 
                ? userRepository.findById(facultad.getDecanoId()).orElse(null)
                : null;
        
        return facultadMapper.toResponse(facultad, decano);
    }

    /**
     * Crea una nueva facultad.
     * 
     * <p><strong>Validaciones:</strong></p>
     * <ul>
     *   <li>El nombre no debe estar duplicado</li>
     *   <li>Si se asigna decano, debe existir en el sistema</li>
     *   <li>Los créditos deben ser positivos</li>
     * </ul>
     * 
     * @param request datos de la nueva facultad
     * @return facultad creada
     * @throws BusinessException si el nombre está duplicado o el decano no existe
     */
    @Transactional
    public FacultadResponse create(FacultadRequest request) {
        log.info("Creando nueva facultad: {}", request.getNombre());
        
        // Validar que no exista otra facultad con el mismo nombre
        if (facultadRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new BusinessException("Ya existe una facultad con el nombre: " + request.getNombre());
        }
        
        // Validar que el decano exista si se proporciona
        User decano = null;
        if (request.getDecanoId() != null && !request.getDecanoId().isEmpty()) {
            decano = userRepository.findById(request.getDecanoId())
                    .orElseThrow(() -> new BusinessException("El usuario decano no existe con ID: " + request.getDecanoId()));
        }
        
        Facultad facultad = facultadMapper.toEntity(request);
        Facultad savedFacultad = facultadRepository.save(facultad);
        
        log.info("Facultad creada exitosamente con ID: {}", savedFacultad.getId());
        return facultadMapper.toResponse(savedFacultad, decano);
    }

    /**
     * Actualiza una facultad existente.
     * 
     * <p><strong>Validaciones:</strong></p>
     * <ul>
     *   <li>La facultad debe existir</li>
     *   <li>El nuevo nombre no debe estar duplicado (excepto para la misma facultad)</li>
     *   <li>Si se cambia el decano, el nuevo debe existir</li>
     * </ul>
     * 
     * @param id ID de la facultad a actualizar
     * @param request nuevos datos
     * @return facultad actualizada
     * @throws ResourceNotFoundException si la facultad no existe
     * @throws BusinessException si el nombre está duplicado o el decano no existe
     */
    @Transactional
    public FacultadResponse update(String id, FacultadRequest request) {
        log.info("Actualizando facultad con ID: {}", id);
        
        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada con ID: " + id));
        
        // Validar que no exista otra facultad con el mismo nombre
        facultadRepository.findByNombre(request.getNombre()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException("Ya existe otra facultad con el nombre: " + request.getNombre());
            }
        });
        
        // Validar que el decano exista si se proporciona
        User decano = null;
        if (request.getDecanoId() != null && !request.getDecanoId().isEmpty()) {
            decano = userRepository.findById(request.getDecanoId())
                    .orElseThrow(() -> new BusinessException("El usuario decano no existe con ID: " + request.getDecanoId()));
        }
        
        facultadMapper.updateEntity(facultad, request);
        Facultad updatedFacultad = facultadRepository.save(facultad);
        
        log.info("Facultad actualizada exitosamente con ID: {}", id);
        return facultadMapper.toResponse(updatedFacultad, decano);
    }

    /**
     * Elimina una facultad.
     * 
     * <p><strong>Validación importante:</strong> No se puede eliminar una facultad
     * que tiene materias asociadas. Esto debe verificarse antes de la eliminación.</p>
     * 
     * @param id ID de la facultad a eliminar
     * @throws ResourceNotFoundException si la facultad no existe
     * @throws BusinessException si la facultad tiene materias asociadas
     */
    @Transactional
    public void delete(String id) {
        log.info("Eliminando facultad con ID: {}", id);
        
        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada con ID: " + id));
        
        // Validar que no tenga materias asociadas
        if (facultad.getNumeroMaterias() > 0) {
            throw new BusinessException("No se puede eliminar la facultad porque tiene " + 
                    facultad.getNumeroMaterias() + " materia(s) asociada(s)");
        }
        
        facultadRepository.deleteById(id);
        log.info("Facultad eliminada exitosamente con ID: {}", id);
    }

    /**
     * Activa o desactiva una facultad.
     * 
     * @param id ID de la facultad
     * @param activo nuevo estado
     * @return facultad actualizada
     * @throws ResourceNotFoundException si la facultad no existe
     */
    @Transactional
    public FacultadResponse toggleActive(String id, boolean activo) {
        log.info("Cambiando estado de facultad {} a: {}", id, activo);
        
        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada con ID: " + id));
        
        facultad.setActivo(activo);
        Facultad updatedFacultad = facultadRepository.save(facultad);
        
        User decano = facultad.getDecanoId() != null 
                ? userRepository.findById(facultad.getDecanoId()).orElse(null)
                : null;
        
        log.info("Estado de facultad cambiado exitosamente");
        return facultadMapper.toResponse(updatedFacultad, decano);
    }

    /**
     * Incrementa el contador de materias de una facultad.
     * 
     * <p>Este método se llama cuando se crea una nueva materia asociada a la facultad.</p>
     * 
     * @param id ID de la facultad
     */
    @Transactional
    public void incrementNumeroMaterias(String id) {
        log.info("Incrementando número de materias para facultad: {}", id);
        
        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada con ID: " + id));
        
        facultad.setNumeroMaterias(facultad.getNumeroMaterias() + 1);
        facultadRepository.save(facultad);
    }

    /**
     * Decrementa el contador de materias de una facultad.
     * 
     * <p>Este método se llama cuando se elimina una materia asociada a la facultad.</p>
     * 
     * @param id ID de la facultad
     */
    @Transactional
    public void decrementNumeroMaterias(String id) {
        log.info("Decrementando número de materias para facultad: {}", id);
        
        Facultad facultad = facultadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada con ID: " + id));
        
        if (facultad.getNumeroMaterias() > 0) {
            facultad.setNumeroMaterias(facultad.getNumeroMaterias() - 1);
            facultadRepository.save(facultad);
        }
    }
}
