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

/**
 * DTO de solicitud para crear o actualizar una materia.
 * 
 * <p>Usado en endpoints {@code POST /api/materias} y {@code PUT /api/materias/{id}}
 * por coordinadores o admins para gestionar el catálogo de materias.</p>
 * 
 * @see MateriaResponse
 * @see edu.dosw.sirha.model.Materia
 * @see edu.dosw.sirha.controller.MateriaController
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateriaRequest {

    /**
     * Mnemónico único de la materia (código corto).
     * 
     * <p>Ejemplo: "SIST-101", "MATE-201".</p>
     */
    @NotBlank
    private String mnemonico;

    /**
     * Nombre completo de la materia.
     */
    @NotBlank
    private String nombre;

    /**
     * Créditos académicos que otorga la materia.
     */
    @Min(0)
    private int creditos;

    /**
     * Horas de clase presencial por semana.
     */
    @Min(0)
    private int horasPresenciales;

    /**
     * Horas de trabajo independiente estimadas por semana.
     */
    @Min(0)
    private int horasIndependientes;

    /**
     * Nivel o semestre sugerido (1-10).
     */
    @Min(0)
    private int nivel;

    /**
     * Indica si la materia requiere laboratorio.
     */
    @NotNull
    private Boolean laboratorio;

    /**
     * ID de la facultad a la que pertenece.
     */
    @NotBlank
    private String facultadId;

    /**
     * IDs de materias prerequisitos (deben aprobarse antes).
     */
    @Builder.Default
    private List<String> prerequisitos = new ArrayList<>();

    /**
     * IDs de materias que esta materia desbloquea.
     */
    @Builder.Default
    private List<String> desbloquea = new ArrayList<>();

    /**
     * Indica si la materia está activa en el catálogo.
     */
    /**
     * Indica si la materia está activa en el catálogo.
     */
    private Boolean activo;

    /**
     * Términos de búsqueda adicionales para mejorar búsquedas.
     * 
     * <p>Se autogeneran combinando mnemonico, nombre y aliases.</p>
     */
    @Builder.Default
    private List<String> searchTerms = new ArrayList<>();
}