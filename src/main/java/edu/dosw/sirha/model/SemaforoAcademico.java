package edu.dosw.sirha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemaforoAcademico {
    private int materiasAprobadas;
    private int materiasEnProgreso;
    private int materiasPerdidas;

    public double porcentajeAvance() {
        int total = materiasAprobadas + materiasEnProgreso + materiasPerdidas;
        if (total == 0) {
            return 0;
        }
        return (double) materiasAprobadas / total;
    }
}
