package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * Modelo para el calendario académico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "calendarios_academicos")
public class CalendarioAcademico {
    
    @Id
    private String id;
    
    private String nombre;
    
    private LocalDate fechaInicioSolicitudes;
    
    private LocalDate fechaFinSolicitudes;
    
    private boolean periodoSolicitudesActivo;
    
    private String observaciones;
}