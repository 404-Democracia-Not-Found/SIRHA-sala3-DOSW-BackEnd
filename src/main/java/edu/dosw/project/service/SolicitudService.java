package edu.dosw.project.service;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.model.Solicitud;
import java.util.List;
import java.util.Optional;

public interface SolicitudService {
    Solicitud createSolicitud(SolicitudCreateDto dto);
    List<Solicitud> findByStudent(String studentId);
    Solicitud approveSolicitud(String solicitudId, String approverId);
    Solicitud rejectSolicitud(String solicitudId, String approverId, String reason);
    List<Solicitud> findAll();
    Optional<Solicitud> findById(String id);
    List<Solicitud> findByEstado(String estado);
}