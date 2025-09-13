package edu.dosw.project.controller;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<Solicitud> create(@Valid @RequestBody SolicitudCreateDto dto) {
        Solicitud created = solicitudService.createSolicitud(dto);
        return ResponseEntity.created(URI.create("/api/solicitudes/" + created.getId())).body(created);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<List<Solicitud>> byStudent(@PathVariable String id) {
        return ResponseEntity.ok(solicitudService.findByStudent(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Solicitud> approve(@PathVariable String id, @RequestParam String approverId) {
        Solicitud s = solicitudService.approveSolicitud(id, approverId);
        return ResponseEntity.ok(s);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Solicitud> reject(@PathVariable String id, @RequestParam String approverId, @RequestParam String reason) {
        Solicitud s = solicitudService.rejectSolicitud(id, approverId, reason);
        return ResponseEntity.ok(s);
    }
}