package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;
import edu.dosw.sirha.model.Periodo;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre {@link Periodo}, {@link PeriodoRequest} y {@link PeriodoResponse}.
 * 
 * @see Periodo
 * @see PeriodoRequest
 * @see PeriodoResponse
 */
@Component
public class PeriodoMapper {

    /**
     * Convierte un PeriodoRequest a una nueva entidad Periodo.
     * 
     * @param request DTO con datos del periodo
     * @return Nueva entidad Periodo (sin ID)
     */
    public Periodo toEntity(PeriodoRequest request) {
        if (request == null) {
            return null;
        }

        return Periodo.builder()
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .fechaInscripcionInicio(request.getFechaInscripcionInicio())
                .fechaLimiteSolicitudes(request.getFechaLimiteSolicitudes())
                .ano(request.getAno())
                .semestre(request.getSemestre())
                .activo(Boolean.TRUE.equals(request.getActivo()))
                .configuracion(request.getConfiguracion())
                .build();
    }

    /**
     * Actualiza una entidad Periodo existente con datos de un request.
     * 
     * @param entity Entidad existente a actualizar
     * @param request DTO con nuevos datos
     */
    public void updateEntity(Periodo entity, PeriodoRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setFechaInicio(request.getFechaInicio());
        entity.setFechaFin(request.getFechaFin());
        entity.setFechaInscripcionInicio(request.getFechaInscripcionInicio());
        entity.setFechaLimiteSolicitudes(request.getFechaLimiteSolicitudes());
        entity.setAno(request.getAno());
        entity.setSemestre(request.getSemestre());
        entity.setActivo(Boolean.TRUE.equals(request.getActivo()));
        entity.setConfiguracion(request.getConfiguracion());
    }

    /**
     * Convierte una entidad Periodo a PeriodoResponse.
     * 
     * @param periodo Entidad de dominio
     * @return DTO para respuesta HTTP
     */
    public PeriodoResponse toResponse(Periodo periodo) {
        if (periodo == null) {
            return null;
        }

        return PeriodoResponse.builder()
                .id(periodo.getId())
                .fechaInicio(periodo.getFechaInicio())
                .fechaFin(periodo.getFechaFin())
                .fechaInscripcionInicio(periodo.getFechaInscripcionInicio())
                .fechaLimiteSolicitudes(periodo.getFechaLimiteSolicitudes())
                .ano(periodo.getAno())
                .semestre(periodo.getSemestre())
                .activo(periodo.isActivo())
                .configuracion(periodo.getConfiguracion())
                .build();
    }
}