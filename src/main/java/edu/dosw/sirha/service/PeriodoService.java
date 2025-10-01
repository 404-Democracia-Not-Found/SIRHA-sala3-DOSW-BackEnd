package edu.dosw.sirha.service;

import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;

import java.time.Instant;
import java.util.List;

public interface PeriodoService {

	PeriodoResponse create(PeriodoRequest request);

	PeriodoResponse update(String id, PeriodoRequest request);

	void delete(String id);

	PeriodoResponse findById(String id);

	List<PeriodoResponse> findAll();

	PeriodoResponse findActive();

	PeriodoResponse markAsActive(String id);

	boolean isWithinPeriodo(Instant fecha);
}