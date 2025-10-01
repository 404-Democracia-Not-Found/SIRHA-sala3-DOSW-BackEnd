package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.enums.SolicitudEstado;

import java.time.Instant;
import java.util.List;

public interface SolicitudService {

	SolicitudResponse create(SolicitudRequest request);

	SolicitudResponse update(String id, SolicitudRequest request);

	SolicitudResponse changeEstado(String id, SolicitudEstado nuevoEstado, String observaciones);

	SolicitudResponse findById(String id);

	void delete(String id);

	List<SolicitudResponse> findAll();

	List<SolicitudResponse> findByEstudiante(String estudianteId);

	List<SolicitudResponse> findByEstados(List<SolicitudEstado> estados);

	long countByEstado(SolicitudEstado estado);

	List<SolicitudResponse> findByPeriodoAndRango(String periodoId, Instant inicio, Instant fin);
}