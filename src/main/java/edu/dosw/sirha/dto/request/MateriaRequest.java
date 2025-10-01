package edu.dosw.sirha.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateriaRequest {

    @NotBlank
    private String mnemonico;

    @NotBlank
    private String nombre;

    @Min(0)
    private int creditos;

    @Min(0)
    private int horasPresenciales;

    @Min(0)
    private int horasIndependientes;

    @Min(0)
    private int nivel;

    @NotNull
    private Boolean laboratorio;

    @NotBlank
    private String facultadId;

    @Builder.Default
    private List<String> prerequisitos = new ArrayList<>();

    @Builder.Default
    private List<String> desbloquea = new ArrayList<>();

    @NotNull
    private Boolean activo;

    @Builder.Default
    private List<String> searchTerms = new ArrayList<>();
}