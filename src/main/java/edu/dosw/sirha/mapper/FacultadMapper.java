package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.FacultadRequest;
import edu.dosw.sirha.dto.response.FacultadResponse;
import edu.dosw.sirha.model.Facultad;
import edu.dosw.sirha.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Facultad y sus DTOs.
 * 
 * <p>Proporciona métodos para transformar entre la entidad de dominio
 * y los objetos de transferencia de datos utilizados en la API REST.</p>
 * 
 * <p><strong>Responsabilidades:</strong></p>
 * <ul>
 *   <li>Convertir FacultadRequest a Facultad</li>
 *   <li>Convertir Facultad a FacultadResponse</li>
 *   <li>Actualizar una Facultad existente con datos de FacultadRequest</li>
 *   <li>Incluir información del decano cuando está disponible</li>
 * </ul>
 * 
 * @see Facultad
 * @see FacultadRequest
 * @see FacultadResponse
 */
@Component
public class FacultadMapper {

    /**
     * Convierte un FacultadRequest a una entidad Facultad.
     * 
     * @param request DTO con los datos de la facultad
     * @return nueva instancia de Facultad
     */
    public Facultad toEntity(FacultadRequest request) {
        return Facultad.builder()
                .nombre(request.getNombre())
                .creditosTotales(request.getCreditosTotales())
                .numeroMaterias(request.getNumeroMaterias())
                .activo(request.getActivo())
                .decanoId(request.getDecanoId())
                .build();
    }

    /**
     * Convierte una entidad Facultad a FacultadResponse.
     * 
     * <p>El campo decanoNombre será null si no se proporciona el usuario decano.</p>
     * 
     * @param facultad entidad a convertir
     * @return DTO de respuesta
     */
    public FacultadResponse toResponse(Facultad facultad) {
        return toResponse(facultad, null);
    }

    /**
     * Convierte una entidad Facultad a FacultadResponse incluyendo el nombre del decano.
     * 
     * @param facultad entidad a convertir
     * @param decano usuario que es decano de la facultad (puede ser null)
     * @return DTO de respuesta con información completa
     */
    public FacultadResponse toResponse(Facultad facultad, User decano) {
        return FacultadResponse.builder()
                .id(facultad.getId())
                .nombre(facultad.getNombre())
                .creditosTotales(facultad.getCreditosTotales())
                .numeroMaterias(facultad.getNumeroMaterias())
                .activo(facultad.isActivo())
                .decanoId(facultad.getDecanoId())
                .decanoNombre(decano != null ? decano.getNombre() : null)
                .build();
    }

    /**
     * Actualiza una Facultad existente con los datos de un FacultadRequest.
     * 
     * <p>Mantiene el ID de la facultad original y solo actualiza los campos
     * proporcionados en el request.</p>
     * 
     * @param facultad entidad existente a actualizar
     * @param request DTO con los nuevos datos
     */
    public void updateEntity(Facultad facultad, FacultadRequest request) {
        facultad.setNombre(request.getNombre());
        facultad.setCreditosTotales(request.getCreditosTotales());
        facultad.setNumeroMaterias(request.getNumeroMaterias());
        facultad.setActivo(request.getActivo());
        facultad.setDecanoId(request.getDecanoId());
    }
}
