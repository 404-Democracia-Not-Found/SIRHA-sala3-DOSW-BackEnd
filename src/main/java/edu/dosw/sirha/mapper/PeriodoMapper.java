package edu.dosw.sirha.mapper;

import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;
import edu.dosw.sirha.model.Periodo;
import org.springframework.stereotype.Component;

@Component
public class PeriodoMapper {

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