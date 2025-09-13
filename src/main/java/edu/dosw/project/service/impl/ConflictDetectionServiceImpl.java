package edu.dosw.project.service.impl;

import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.Conflicto;
import edu.dosw.project.model.Horario;
import edu.dosw.project.repository.HorarioRepository;
import edu.dosw.project.repository.ConflictoRepository;
import edu.dosw.project.service.ConflictDetectionService;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class ConflictDetectionServiceImpl implements ConflictDetectionService {

    private final HorarioRepository horarioRepository;
    private final ConflictoRepository conflictoRepository;

    public ConflictDetectionServiceImpl(HorarioRepository horarioRepository,
                                        ConflictoRepository conflictoRepository) {
        this.horarioRepository = horarioRepository;
        this.conflictoRepository = conflictoRepository;
    }

    @Override
    public void detectConflictsForSolicitud(Solicitud solicitud) {
        Horario prop = horarioRepository.findById(solicitud.getHorarioPropuestoId()).orElse(null);
        if (prop == null) return;
        if (prop.isFull()) {
            Conflicto c = new Conflicto();
            c.setDescripcion("Cupo excedido en horario propuesto");
            c.setDetectedAt(Instant.now());
            c.setResolved(false);
            c.setInvolucrados(List.of(solicitud.getStudentId(), solicitud.getHorarioPropuestoId()));
            conflictoRepository.save(c);
        }
    }

    @Override
    public Conflicto detectConflictsForHorario(String horarioId) {
        Horario h = horarioRepository.findById(horarioId).orElse(null);
        if (h == null) return null;
        if (h.getInscritos() > h.getCupos()) {
            Conflicto c = new Conflicto();
            c.setDescripcion("Sobrecupo detectado en horario " + horarioId);
            c.setDetectedAt(Instant.now());
            c.setInvolucrados(List.of(horarioId));
            c.setResolved(false);
            return conflictoRepository.save(c);
        }
        return null;
    }
}