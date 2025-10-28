package edu.dosw.sirha.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar una Facultad.
 * 
 * <p>Contiene las validaciones necesarias para garantizar la integridad
 * de los datos al momento de crear o modificar una facultad.</p>
 * 
 * <p><strong>Reglas de negocio:</strong></p>
 * <ul>
 *   <li>El nombre es obligatorio y debe tener entre 3 y 200 caracteres</li>
 *   <li>Los créditos totales deben ser positivos (mínimo 1)</li>
 *   <li>El número de materias debe ser no negativo</li>
 *   <li>El estado activo es obligatorio</li>
 *   <li>El decanoId es opcional</li>
 * </ul>
 * 
 * @see edu.dosw.sirha.model.Facultad
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultadRequest {

    /**
     * Nombre oficial de la facultad.
     * 
     * <p>Ejemplo: "Facultad de Ingeniería de Sistemas".</p>
     */
    @NotBlank(message = "El nombre de la facultad es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nombre;

    /**
     * Total de créditos requeridos para graduarse.
     * 
     * <p>Debe ser un número positivo mayor a 0.</p>
     */
    @NotNull(message = "El total de créditos es obligatorio")
    @Min(value = 1, message = "Los créditos totales deben ser al menos 1")
    private Integer creditosTotales;
    
    /**
     * Número total de materias en el catálogo.
     * 
     * <p>Por defecto es 0 al crear una facultad nueva.</p>
     */
    @NotNull(message = "El número de materias es obligatorio")
    @Min(value = 0, message = "El número de materias no puede ser negativo")
    private Integer numeroMaterias;
    
    /**
     * Indica si la facultad está activa.
     * 
     * <p>Por defecto debe ser true al crear una nueva facultad.</p>
     */
    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    /**
     * ID del decano o director de la facultad.
     * 
     * <p>Opcional. Si se proporciona, debe ser un ID válido de usuario.</p>
     */
    @Size(max = 100, message = "El ID del decano no puede exceder 100 caracteres")
    private String decanoId;
}
