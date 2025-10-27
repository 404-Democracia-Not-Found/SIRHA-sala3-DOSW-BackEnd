package edu.dosw.sirha.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para Facultad.
 * 
 * <p>Contiene toda la información de una facultad para ser enviada
 * al cliente en las respuestas de la API.</p>
 * 
 * <p>Este DTO incluye campos calculados como el número de materias
 * y el nombre del decano si está disponible.</p>
 * 
 * @see edu.dosw.sirha.model.Facultad
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultadResponse {

    /**
     * ID único de la facultad.
     */
    private String id;

    /**
     * Nombre oficial de la facultad.
     */
    private String nombre;

    /**
     * Total de créditos requeridos para graduarse.
     */
    private int creditosTotales;
    
    /**
     * Número total de materias en el catálogo de la facultad.
     */
    private int numeroMaterias;
    
    /**
     * Indica si la facultad está activa.
     */
    private boolean activo;

    /**
     * ID del decano o director de la facultad.
     */
    private String decanoId;
    
    /**
     * Nombre completo del decano (opcional).
     * 
     * <p>Se incluye para facilitar la visualización en el frontend.</p>
     */
    private String decanoNombre;
}
