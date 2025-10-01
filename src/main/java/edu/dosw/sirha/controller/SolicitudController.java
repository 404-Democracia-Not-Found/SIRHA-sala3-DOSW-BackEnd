
package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.request.SolicitudEstadoChangeRequest;
import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@Validated
@RequiredArgsConstructor
public class SolicitudController {

	private final SolicitudService solicitudService;

	@PostMapping
	public ResponseEntity<SolicitudResponse> create(@Valid @RequestBody SolicitudRequest request) {
		SolicitudResponse response = solicitudService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public SolicitudResponse update(@PathVariable String id, @Valid @RequestBody SolicitudRequest request) {
		return solicitudService.update(id, request);
	}

	@PatchMapping("/{id}/estado")
	public SolicitudResponse changeEstado(@PathVariable String id,
			@Valid @RequestBody SolicitudEstadoChangeRequest request) {
		return solicitudService.changeEstado(id, request.getEstado(), request.getObservaciones());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		solicitudService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public List<SolicitudResponse> findAll() {
		return solicitudService.findAll();
	}

	@GetMapping("/{id}")
	public SolicitudResponse findById(@PathVariable String id) {
		return solicitudService.findById(id);
	}

	@GetMapping("/estudiante/{estudianteId}")
	public List<SolicitudResponse> findByEstudiante(@PathVariable String estudianteId) {
		return solicitudService.findByEstudiante(estudianteId);
	}

	@GetMapping("/estados")
	public List<SolicitudResponse> findByEstados(
			@RequestParam(name = "estado", required = false) List<SolicitudEstado> estados) {
		return solicitudService.findByEstados(estados);
	}

	@GetMapping("/estados/{estado}/conteo")
	public long countByEstado(@PathVariable SolicitudEstado estado) {
		return solicitudService.countByEstado(estado);
	}

	@GetMapping("/periodo/{periodoId}")
	public List<SolicitudResponse> findByPeriodoAndRango(
			@PathVariable String periodoId,
			@RequestParam(name = "inicio", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
			@RequestParam(name = "fin", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fin) {
		return solicitudService.findByPeriodoAndRango(periodoId, inicio, fin);
	}
}
