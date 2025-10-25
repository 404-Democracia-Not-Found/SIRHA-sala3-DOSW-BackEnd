package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;
import edu.dosw.sirha.model.Materia;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper para convertir entre {@link Materia}, {@link MateriaRequest} y {@link MateriaResponse}.
 * 
 * @see Materia
 * @see MateriaRequest
 * @see MateriaResponse
 */
@Component
public class MateriaMapper {

    /**
     * Convierte un MateriaRequest a una nueva entidad Materia.
     * 
     * @param request DTO con datos de la materia
     * @return Nueva entidad Materia (sin ID)
     */
    public Materia toEntity(MateriaRequest request) {
        if (request == null) {
            return null;
        }

        return Materia.builder()
                .mnemonico(request.getMnemonico())
                .nombre(request.getNombre())
                .creditos(request.getCreditos())
                .horasPresenciales(request.getHorasPresenciales())
                .horasIndependientes(request.getHorasIndependientes())
                .nivel(request.getNivel())
                .laboratorio(Boolean.TRUE.equals(request.getLaboratorio()))
                .facultadId(request.getFacultadId())
                .prerequisitos(safeCopy(request.getPrerequisitos()))
                .desbloquea(safeCopy(request.getDesbloquea()))
                .activo(Boolean.TRUE.equals(request.getActivo()))
                .searchTerms(safeCopy(request.getSearchTerms()))
                .build();
    }

    /**
     * Actualiza una entidad Materia existente con datos de un request.
     * 
     * @param entity Entidad existente a actualizar
     * @param request DTO con nuevos datos
     */
    public void updateEntity(Materia entity, MateriaRequest request) {
        if (entity == null || request == null) {
            return;
        }
        entity.setMnemonico(request.getMnemonico());
        entity.setNombre(request.getNombre());
        entity.setCreditos(request.getCreditos());
        entity.setHorasPresenciales(request.getHorasPresenciales());
        entity.setHorasIndependientes(request.getHorasIndependientes());
        entity.setNivel(request.getNivel());
        entity.setLaboratorio(Boolean.TRUE.equals(request.getLaboratorio()));
        entity.setFacultadId(request.getFacultadId());
        entity.setPrerequisitos(safeCopy(request.getPrerequisitos()));
        entity.setDesbloquea(safeCopy(request.getDesbloquea()));
        entity.setActivo(Boolean.TRUE.equals(request.getActivo()));
        entity.setSearchTerms(safeCopy(request.getSearchTerms()));
    }

    /**
     * Convierte una entidad Materia a MateriaResponse.
     * 
     * @param materia Entidad de dominio
     * @return DTO para respuesta HTTP
     */
    public MateriaResponse toResponse(Materia materia) {
        if (materia == null) {
            return null;
        }

        return MateriaResponse.builder()
                .id(materia.getId())
                .mnemonico(materia.getMnemonico())
                .nombre(materia.getNombre())
                .creditos(materia.getCreditos())
                .horasPresenciales(materia.getHorasPresenciales())
                .horasIndependientes(materia.getHorasIndependientes())
                .nivel(materia.getNivel())
                .laboratorio(materia.isLaboratorio())
                .facultadId(materia.getFacultadId())
                .prerequisitos(safeCopy(materia.getPrerequisitos()))
                .desbloquea(safeCopy(materia.getDesbloquea()))
                .activo(materia.isActivo())
                .searchTerms(safeCopy(materia.getSearchTerms()))
                .build();
    }

    /**
     * Crea una copia segura de una lista de Strings.
     * 
     * <p>Evita NullPointerException y referencias compartidas.</p>
     * 
     * @param list Lista a copiar
     * @return Nueva lista con mismo contenido, o lista vacía si null
     */
    private List<String> safeCopy(List<String> list) {
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }
}