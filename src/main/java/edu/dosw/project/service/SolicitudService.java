package edu.dosw.project.service;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.model.Solicitud;
import java.util.List;

public interface SolicitudService {
    Solicitud createSolicitud(SolicitudCreateDto dto);
    List<Solicitud> findByStudent(String studentId);
    Solicitud approveSolicitud(String solicitudId, String approverId);
    Solicitud rejectSolicitud(String solicitudId, String approverId, String reason);
    List<Solicitud> findAll();
    Solicitud findById(String id);
    List<Solicitud> findByEstado(String estado);
}