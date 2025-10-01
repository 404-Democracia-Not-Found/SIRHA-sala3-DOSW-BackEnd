package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;
import edu.dosw.sirha.service.MateriaService;
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
@RequestMapping("/api/materias")
@RequiredArgsConstructor
@Validated
public class MateriaController {

    private final MateriaService materiaService;

    @PostMapping
    public ResponseEntity<MateriaResponse> create(@Valid @RequestBody MateriaRequest request) {
        MateriaResponse response = materiaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public MateriaResponse update(@PathVariable String id, @Valid @RequestBody MateriaRequest request) {
        return materiaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        materiaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public MateriaResponse findById(@PathVariable String id) {
        return materiaService.findById(id);
    }

    @GetMapping
    public List<MateriaResponse> findAll() {
        return materiaService.findAll();
    }

    @GetMapping("/facultad/{facultadId}")
    public List<MateriaResponse> findByFacultad(@PathVariable String facultadId) {
        return materiaService.findByFacultad(facultadId);
    }

    @GetMapping("/search")
    public List<MateriaResponse> search(@RequestParam(name = "term", required = false) String term) {
        return materiaService.search(term);
    }
}