package edu.dosw.sirha.service.impl;

import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.ConflictMapper;
import edu.dosw.sirha.model.Conflict;
import edu.dosw.sirha.repository.ConflictRepository;
import edu.dosw.sirha.service.ConflictDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConflictDetectionServiceImpl implements ConflictDetectionService {

	private final ConflictRepository conflictRepository;
	private final ConflictMapper conflictMapper;
	private final Clock clock;

	@Override
	public ConflictResponse registrar(ConflictRequest request) {
		Conflict conflict = conflictMapper.toNewEntity(request);
		conflict.setFechaDeteccion(Instant.now(clock));
		Conflict saved = conflictRepository.save(conflict);
		return conflictMapper.toResponse(saved);
	}

	@Override
	public ConflictResponse actualizar(String id, ConflictRequest request) {
		Conflict conflict = obtenerPorId(id);
		conflict.setTipo(request.getTipo());
		conflict.setDescripcion(request.getDescripcion());
		conflict.setEstudianteId(request.getEstudianteId());
		conflict.setSolicitudId(request.getSolicitudId());
		conflict.setGrupoId(request.getGrupoId());
		conflict.setObservaciones(request.getObservaciones());
		Conflict updated = conflictRepository.save(conflict);
		return conflictMapper.toResponse(updated);
	}

	@Override
	public ConflictResponse marcarResuelto(String id, boolean resuelto, String observaciones) {
		Conflict conflict = obtenerPorId(id);
		conflict.setResuelto(resuelto);
		conflict.setObservaciones(observaciones);
		Conflict updated = conflictRepository.save(conflict);
		return conflictMapper.toResponse(updated);
	}

	@Override
	@Transactional(readOnly = true)
	public ConflictResponse findById(String id) {
		return conflictMapper.toResponse(obtenerPorId(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConflictResponse> findAll() {
		return conflictRepository.findAll().stream()
				.map(conflictMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConflictResponse> findByEstudiante(String estudianteId) {
		return conflictRepository.findByEstudianteId(estudianteId).stream()
				.map(conflictMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConflictResponse> findBySolicitud(String solicitudId) {
		return conflictRepository.findBySolicitudId(solicitudId).stream()
				.map(conflictMapper::toResponse)
				.toList();
	}

	@Override
	public void delete(String id) {
		Conflict conflict = obtenerPorId(id);
		conflictRepository.delete(conflict);
	}

	private Conflict obtenerPorId(String id) {
		return conflictRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Conflicto no encontrado con id " + id));
	}
}