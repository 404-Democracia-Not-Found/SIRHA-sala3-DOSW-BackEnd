package edu.dosw.sirha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoConfiguracion {
    private boolean permitirCambios;
    private int diasMaxRespuesta;
}
