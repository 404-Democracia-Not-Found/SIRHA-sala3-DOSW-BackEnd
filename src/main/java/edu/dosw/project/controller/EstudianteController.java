package edu.dosw.project.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/estudiante")
public class EstudianteController {

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Estudiante Controller OK");
    }
}
