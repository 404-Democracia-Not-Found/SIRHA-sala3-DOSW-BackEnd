package edu.dosw.sirha.service.impl;

import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.MateriaMapper;
import edu.dosw.sirha.model.Materia;
import edu.dosw.sirha.repository.MateriaRepository;
import edu.dosw.sirha.service.MateriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MateriaServiceImpl implements MateriaService {

	private final MateriaRepository materiaRepository;
	private final MateriaMapper materiaMapper;

	@Override
	public MateriaResponse create(MateriaRequest request) {
		Materia materia = materiaMapper.toEntity(request);
		materia.setSearchTerms(buildSearchTerms(request));
		Materia saved = materiaRepository.save(materia);
		return materiaMapper.toResponse(saved);
	}

	@Override
	public MateriaResponse update(String id, MateriaRequest request) {
		Materia existing = getById(id);
		materiaMapper.updateEntity(existing, request);
		existing.setSearchTerms(buildSearchTerms(request));
		Materia updated = materiaRepository.save(existing);
		return materiaMapper.toResponse(updated);
	}

	@Override
	public void delete(String id) {
		Materia existing = getById(id);
		materiaRepository.delete(existing);
	}

	@Override
	@Transactional(readOnly = true)
	public MateriaResponse findById(String id) {
		return materiaMapper.toResponse(getById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<MateriaResponse> findAll() {
		return materiaRepository.findAll().stream()
				.map(materiaMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<MateriaResponse> findByFacultad(String facultadId) {
		return materiaRepository.findByFacultadIdAndActivoTrue(facultadId).stream()
				.map(materiaMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<MateriaResponse> search(String term) {
		if (!StringUtils.hasText(term)) {
			return findAll();
		}
		return materiaRepository.findBySearchTermsContainingIgnoreCase(term.trim()).stream()
				.map(materiaMapper::toResponse)
				.toList();
	}

	private Materia getById(String id) {
		return materiaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id " + id));
	}

	private List<String> buildSearchTerms(MateriaRequest request) {
		Set<String> terms = new LinkedHashSet<>();
		if (request.getSearchTerms() != null) {
			request.getSearchTerms().stream()
					.filter(StringUtils::hasText)
					.map(term -> term.toLowerCase(Locale.ROOT))
					.forEach(terms::add);
		}
		if (StringUtils.hasText(request.getMnemonico())) {
			terms.add(request.getMnemonico().toLowerCase(Locale.ROOT));
		}
		if (StringUtils.hasText(request.getNombre())) {
			terms.add(request.getNombre().toLowerCase(Locale.ROOT));
		}
		return new ArrayList<>(terms);
	}
}