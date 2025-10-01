package edu.dosw.sirha.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "facultades")
public class Facultad {

    @Id
    private String id;

    @NotBlank
    private String nombre;

    private int creditosTotales;
    private int numeroMaterias;
    private boolean activo;

    private String decanoId;
}
