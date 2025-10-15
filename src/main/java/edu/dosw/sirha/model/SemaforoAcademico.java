package edu.dosw.sirha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Indicador visual del rendimiento académico de un estudiante.
 * 
 * <p>Resume el estado académico mediante conteo de materias aprobadas, en progreso y perdidas.
 * Usado para dashboards, alertas tempranas y análisis de riesgo académico.</p>
 * 
 * <p>El "semáforo" puede tener colores asociados:</p>
 * <ul>
 *   <li>Verde: Alto % de aprobadas, pocas perdidas</li>
 *   <li>Amarillo: Rendimiento moderado</li>
 *   <li>Rojo: Muchas materias perdidas, riesgo académico</li>
 * </ul>
 * 
 * @see User#semaforo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemaforoAcademico {
    /**
     * Cantidad de materias aprobadas hasta la fecha.
     * 
     * <p>Incluye todas las materias con calificación >= 3.0.</p>
     */
    private int materiasAprobadas;
    
    /**
     * Cantidad de materias actualmente en progreso (inscritas pero no finalizadas).
     */
    private int materiasEnProgreso;
    
    /**
     * Cantidad de materias perdidas (reprobadas).
     * 
     * <p>Incluye materias con calificación < 3.0 y materias canceladas después de fecha límite.</p>
     */
    private int materiasPerdidas;

    /**
     * Calcula el porcentaje de avance académico.
     * 
     * <p>Se calcula como: (materiasAprobadas / totalMaterias) * 100.</p>
     * <p>totalMaterias = aprobadas + enProgreso + perdidas.</p>
     * 
     * @return Porcentaje de 0.0 a 1.0 (0% a 100%), o 0.0 si no hay materias
     */
    public double porcentajeAvance() {
        int total = materiasAprobadas + materiasEnProgreso + materiasPerdidas;
        if (total == 0) {
            return 0;
        }
        return (double) materiasAprobadas / total;
    }
}
