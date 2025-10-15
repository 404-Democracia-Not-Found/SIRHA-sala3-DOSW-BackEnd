package edu.dosw.sirha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuración específica de un periodo académico.
 * 
 * <p>Define las reglas y restricciones que aplican durante un {@link Periodo}.
 * Permite variar políticas entre periodos (ej: intersemestres más flexibles).</p>
 * 
 * @see Periodo#configuracion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoConfiguracion {
    /**
     * Indica si se permiten cambios de inscripción durante el periodo.
     * 
     * <p>false durante exámenes finales o periodos cerrados.</p>
     */
    private boolean permitirCambios;
    
    /**
     * Días máximos para responder una solicitud.
     * 
     * <p>Tiempo límite para que coordinación apruebe/rechace solicitudes.
     * Después de este tiempo, se puede escalar o aprobar automáticamente.</p>
     */
    private int diasMaxRespuesta;
}
