package edu.dosw.sirha.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "materias")
public class Materia {

    @Id
    private String id;

    @NotBlank
    private String mnemonico;

    @NotBlank
    private String nombre;

    @Min(0)
    private int creditos;

    private int horasPresenciales;
    private int horasIndependientes;
    private int nivel;
    private boolean laboratorio;

    private String facultadId;

    private List<String> prerequisitos;

    private List<String> desbloquea;

    private boolean activo;

    private List<String> searchTerms;
}