package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.request.ConflictRequest;
import edu.dosw.sirha.dto.response.ConflictResponse;
import edu.dosw.sirha.service.ConflictDetectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conflictos")
@Validated
@RequiredArgsConstructor
public class ConflictController {

    private final ConflictDetectionService conflictDetectionService;

    @PostMapping
    public ResponseEntity<ConflictResponse> registrar(@Valid @RequestBody ConflictRequest request) {
        ConflictResponse response = conflictDetectionService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ConflictResponse actualizar(@PathVariable String id, @Valid @RequestBody ConflictRequest request) {
        return conflictDetectionService.actualizar(id, request);
    }

    @PostMapping("/{id}/resolver")
    public ConflictResponse resolver(@PathVariable String id,
            @RequestParam(name = "resuelto", defaultValue = "true") boolean resuelto,
            @RequestParam(name = "observaciones", required = false) String observaciones) {
        return conflictDetectionService.marcarResuelto(id, resuelto, observaciones);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        conflictDetectionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<ConflictResponse> findAll() {
        return conflictDetectionService.findAll();
    }

    @GetMapping("/{id}")
    public ConflictResponse findById(@PathVariable String id) {
        return conflictDetectionService.findById(id);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public List<ConflictResponse> findByEstudiante(@PathVariable String estudianteId) {
        return conflictDetectionService.findByEstudiante(estudianteId);
    }

    @GetMapping("/solicitud/{solicitudId}")
    public List<ConflictResponse> findBySolicitud(@PathVariable String solicitudId) {
        return conflictDetectionService.findBySolicitud(solicitudId);

    }
}