package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.request.PeriodoRequest;
import edu.dosw.sirha.dto.response.PeriodoResponse;
import edu.dosw.sirha.service.PeriodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/periodos")
@Validated
@RequiredArgsConstructor
public class PeriodoController {

    private final PeriodoService periodoService;

    @PostMapping
    public ResponseEntity<PeriodoResponse> create(@Valid @RequestBody PeriodoRequest request) {
        PeriodoResponse response = periodoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public PeriodoResponse update(@PathVariable String id, @Valid @RequestBody PeriodoRequest request) {
        return periodoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        periodoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<PeriodoResponse> findAll() {
        return periodoService.findAll();
    }

    @GetMapping("/{id}")
    public PeriodoResponse findById(@PathVariable String id) {
        return periodoService.findById(id);
    }

    @GetMapping("/activo")
    public ResponseEntity<PeriodoResponse> findActive() {
        PeriodoResponse response = periodoService.findActive();
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/activar")
    public PeriodoResponse markAsActive(@PathVariable String id) {
        return periodoService.markAsActive(id);
    }

    @GetMapping("/vigencia")
    public ResponseEntity<Boolean> isWithinPeriodo(
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fecha) {
        return ResponseEntity.ok(periodoService.isWithinPeriodo(fecha));
    }
}