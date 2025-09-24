package edu.dosw.project.service.impl;

import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.Conflicto;
import edu.dosw.project.model.Horario;
import edu.dosw.project.repository.HorarioRepository;
import edu.dosw.project.repository.ConflictoRepository;
import edu.dosw.project.service.ConflictDetectionService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
        // Para el SIRHA, detectamos conflictos relacionados con el grupo destino
        if (solicitud.getGrupoDestinoId() != null) {
            // Verificar si el grupo destino tiene cupos disponibles
            // (Esto requeriría un servicio adicional para manejar grupos)
            
            // Crear conflicto por ejemplo
            Conflicto c = new Conflicto();
            c.setDescripcion("Verificando disponibilidad en grupo destino: " + solicitud.getGrupoDestinoId());
            c.setDetectedAt(LocalDateTime.now());
            c.setResolved(false);
            c.setInvolucrados(List.of(solicitud.getEstudianteId(), solicitud.getGrupoDestinoId()));
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
            c.setDetectedAt(LocalDateTime.now());
            c.setInvolucrados(List.of(horarioId));
            c.setResolved(false);
            return conflictoRepository.save(c);
        }
        return null;
    }
}