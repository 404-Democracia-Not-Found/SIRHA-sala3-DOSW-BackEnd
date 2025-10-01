package edu.dosw.sirha.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "grupos")
public class Grupo {

    @Id
    private String id;

    @NotBlank
    private String codigo;

    @NotBlank
    private String materiaId;

    @NotBlank
    private String periodoId;

    private String profesorId;

    @Min(0)
    private int cupoMax;

    @Min(0)
    private int cuposActuales;

    private String salon;

    private Instant fechaInicio;
    private Instant fechaFin;

    private List<Horario> horarios;

    private List<String> listaEspera;

    private boolean activo;
}
