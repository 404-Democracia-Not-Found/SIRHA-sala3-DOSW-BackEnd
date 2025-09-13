package edu.dosw.project.service;

import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.Conflicto;

public interface ConflictDetectionService {
    void detectConflictsForSolicitud(Solicitud solicitud);
    Conflicto detectConflictsForHorario(String horarioId);
}