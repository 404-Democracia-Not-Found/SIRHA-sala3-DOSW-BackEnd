package edu.dosw.sirha.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Facultad o unidad académica de la universidad.
 * 
 * <p>Representa una división académica que agrupa programas y materias relacionadas.
 * Cada facultad tiene su propio catálogo de materias y requisitos de créditos.</p>
 * 
 * <p>Ejemplos: Facultad de Ingeniería, Facultad de Ciencias, Facultad de Administración.</p>
 * 
 * @see Materia
 * @see User
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "facultades")
public class Facultad {

    /**
     * ID único de la facultad.
     */
    @Id
    private String id;

    /**
     * Nombre oficial de la facultad.
     * 
     * <p>Ejemplo: "Facultad de Ingeniería de Sistemas".</p>
     */
    @NotBlank
    private String nombre;

    /**
     * Total de créditos requeridos para graduarse en programas de esta facultad.
     * 
     * <p>Ejemplo: 160 créditos para pregrado.</p>
     */
    private int creditosTotales;
    
    /**
     * Número total de materias en el catálogo de la facultad.
     */
    private int numeroMaterias;
    
    /**
     * Indica si la facultad está activa.
     * 
     * <p>false si la facultad está inactiva o en proceso de reestructuración.</p>
     */
    private boolean activo;

    /**
     * ID del decano o director de la facultad.
     * 
     * <p>Referencia a {@link User} con rol COORDINADOR o superior.</p>
     */
    private String decanoId;
}
