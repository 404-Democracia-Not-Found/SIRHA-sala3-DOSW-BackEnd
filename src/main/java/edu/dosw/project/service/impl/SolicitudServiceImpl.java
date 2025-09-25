package edu.dosw.project.service.impl;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.mapper.SolicitudMapper;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.repository.SolicitudRepository;
import edu.dosw.project.service.SolicitudService;
import edu.dosw.project.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitudMapper mapper;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                SolicitudMapper mapper) {
        this.solicitudRepository = solicitudRepository;
        this.mapper = mapper;
    }

    @Override
    public Solicitud createSolicitud(SolicitudCreateDto dto) {
        String estudianteId = "CURRENT_USER_ID"; // En producción esto vendría del SecurityContext
        Solicitud solicitud = mapper.toDocument(dto, estudianteId);
        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<Solicitud> findByStudent(String studentId) {
        return solicitudRepository.findByEstudianteId(studentId);
    }

    @Override
    @Transactional
    public Solicitud approveSolicitud(String solicitudId, String approverId) {
        Optional<Solicitud> solicitudOpt = solicitudRepository.findById(solicitudId);
        if (solicitudOpt.isEmpty()) {
            throw new ResourceNotFoundException("Solicitud no encontrada");
        }
        
        Solicitud solicitud = solicitudOpt.get();
        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden aprobar solicitudes pendientes");
        }
        
        solicitud.setEstado("APROBADA");
        return solicitudRepository.save(solicitud);
    }

    @Override
    @Transactional
    public Solicitud rejectSolicitud(String solicitudId, String approverId, String reason) {
        Optional<Solicitud> solicitudOpt = solicitudRepository.findById(solicitudId);
        if (solicitudOpt.isEmpty()) {
            throw new ResourceNotFoundException("Solicitud no encontrada");
        }
        
        Solicitud solicitud = solicitudOpt.get();
        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden rechazar solicitudes pendientes");
        }
        
        solicitud.setEstado("RECHAZADA");
        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }

    @Override
    public Optional<Solicitud> findById(String id) {
        return solicitudRepository.findById(id);
    }

    @Override
    public List<Solicitud> findByEstado(String estado) {
        return solicitudRepository.findByEstado(estado);
    }
}