package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;

import java.util.List;

public interface ConflictDetectionService {

    ConflictResponse registrar(ConflictRequest request);

    ConflictResponse actualizar(String id, ConflictRequest request);

    ConflictResponse marcarResuelto(String id, boolean resuelto, String observaciones);

    ConflictResponse findById(String id);

    List<ConflictResponse> findAll();

    List<ConflictResponse> findByEstudiante(String estudianteId);

    List<ConflictResponse> findBySolicitud(String solicitudId);

    void delete(String id);
}