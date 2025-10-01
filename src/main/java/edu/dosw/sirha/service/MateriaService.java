package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;

import java.util.List;

public interface MateriaService {

	MateriaResponse create(MateriaRequest request);

	MateriaResponse update(String id, MateriaRequest request);

	void delete(String id);

	MateriaResponse findById(String id);

	List<MateriaResponse> findAll();

	List<MateriaResponse> findByFacultad(String facultadId);

	List<MateriaResponse> search(String term);
}