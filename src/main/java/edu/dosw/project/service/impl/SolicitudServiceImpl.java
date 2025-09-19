package edu.dosw.project.service.impl;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.mapper.SolicitudMapper;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.Horario;
import edu.dosw.project.repository.SolicitudRepository;
import edu.dosw.project.repository.HorarioRepository;
import edu.dosw.project.service.SolicitudService;
import edu.dosw.project.service.ConflictDetectionService;
import edu.dosw.project.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitudMapper mapper;
    private final HorarioRepository horarioRepository;
    private final ConflictDetectionService conflictDetectionService;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                SolicitudMapper mapper,
                                HorarioRepository horarioRepository,
                                ConflictDetectionService conflictDetectionService) {
        this.solicitudRepository = solicitudRepository;
        this.mapper = mapper;
        this.horarioRepository = horarioRepository;
        this.conflictDetectionService = conflictDetectionService;
    }

    @Override
    public Solicitud createSolicitud(SolicitudCreateDto dto) {
        Horario propuesto = horarioRepository.findById(dto.getHorarioPropuestoId())
                .orElseThrow(() -> new ResourceNotFoundException("Horario propuesto no encontrado"));
        if (propuesto.isFull()) {
            throw new IllegalStateException("El horario propuesto no tiene cupos.");
        }
        Solicitud s = mapper.toDocument(dto);
        s = solicitudRepository.save(s);

        conflictDetectionService.detectConflictsForSolicitud(s);
        return s;
    }

    @Override
    public List<Solicitud> findByStudent(String studentId) {
        return solicitudRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional
    public Solicitud approveSolicitud(String solicitudId, String approverId) {
        Solicitud s = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        s.setStatus(Solicitud.Status.APPROVED);
        s.setUpdatedAt(java.time.Instant.now());
        horarioRepository.findById(s.getHorarioPropuestoId()).ifPresent(h -> { h.setInscritos(h.getInscritos()+1); horarioRepository.save(h); });
        horarioRepository.findById(s.getHorarioActualId()).ifPresent(h -> { h.setInscritos(Math.max(0, h.getInscritos()-1)); horarioRepository.save(h); });
        return solicitudRepository.save(s);
    }

    @Override
    @Transactional
    public Solicitud rejectSolicitud(String solicitudId, String approverId, String reason) {
        Solicitud s = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        s.setStatus(Solicitud.Status.REJECTED);
        s.setComments((s.getComments()==null?"":s.getComments()) + "\nRechazo: "+reason);
        s.setUpdatedAt(java.time.Instant.now());
        return solicitudRepository.save(s);
    }
}