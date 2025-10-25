package edu.dosw.sirha.service.impl;

import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.PeriodoMapper;
import edu.dosw.sirha.model.Periodo;
import edu.dosw.sirha.repository.PeriodoRepository;
import edu.dosw.sirha.service.PeriodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PeriodoServiceImpl implements PeriodoService {

	private final PeriodoRepository periodoRepository;
	private final PeriodoMapper periodoMapper;

	@Override
	public PeriodoResponse create(PeriodoRequest request) {
		Periodo periodo = periodoMapper.toEntity(request);
		if (Boolean.TRUE.equals(periodo.isActivo())) {
			desactivarPeriodoActual();
		}
		Periodo saved = periodoRepository.save(periodo);
		return periodoMapper.toResponse(saved);
	}

	@Override
	public PeriodoResponse update(String id, PeriodoRequest request) {
		Periodo existing = getById(id);
		boolean seActiva = Boolean.TRUE.equals(request.getActivo()) && !existing.isActivo();
		periodoMapper.updateEntity(existing, request);
		if (seActiva) {
			desactivarPeriodoActual();
			existing.setActivo(true);
		}
		Periodo updated = periodoRepository.save(existing);
		return periodoMapper.toResponse(updated);
	}

	@Override
	public void delete(String id) {
		Periodo existing = getById(id);
		if (existing.isActivo()) {
			throw new BusinessException("No es posible eliminar el periodo activo");
		}
		periodoRepository.delete(existing);
	}

	@Override
	@Transactional(readOnly = true)
	public PeriodoResponse findById(String id) {
		return periodoMapper.toResponse(getById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<PeriodoResponse> findAll() {
		return periodoRepository.findAll().stream()
				.map(periodoMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PeriodoResponse findActive() {
		return periodoRepository.findByActivoTrue()
				.map(periodoMapper::toResponse)
				.orElse(null);
	}

	@Override
	public PeriodoResponse markAsActive(String id) {
		Periodo periodo = getById(id);
		if (!periodo.isActivo()) {
			desactivarPeriodoActual();
			periodo.setActivo(true);
			periodo = periodoRepository.save(periodo);
		}
		return periodoMapper.toResponse(periodo);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isWithinPeriodo(Instant fecha) {
		return periodoRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(fecha, fecha)
				.isPresent();
	}

	private Periodo getById(String id) {
		return periodoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Periodo no encontrado con id " + id));
	}

	private void desactivarPeriodoActual() {
		periodoRepository.findByActivoTrue().ifPresent(actual -> {
			actual.setActivo(false);
			periodoRepository.save(actual);
		});
	}
}