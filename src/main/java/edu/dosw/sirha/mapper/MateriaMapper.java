package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;
import edu.dosw.sirha.model.Materia;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MateriaMapper {

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

    private List<String> safeCopy(List<String> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }
}