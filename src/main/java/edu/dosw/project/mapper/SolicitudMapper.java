package edu.dosw.project.mapper;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.model.Solicitud;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {
    public Solicitud toDocument(SolicitudCreateDto dto) {
        Solicitud s = new Solicitud();
        s.setStudentId(dto.getStudentId());
        s.setMateriaId(dto.getMateriaId());
        s.setHorarioActualId(dto.getHorarioActualId());
        s.setHorarioPropuestoId(dto.getHorarioPropuestoId());
        s.setComments(dto.getComments());
        s.setStatus(Solicitud.Status.PENDING);
        s.setCreatedAt(java.time.Instant.now());
        return s;
    }
}