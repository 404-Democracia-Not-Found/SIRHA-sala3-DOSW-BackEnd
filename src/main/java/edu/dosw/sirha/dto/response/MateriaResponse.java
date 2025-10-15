package edu.dosw.sirha.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * DTO de respuesta con información completa de una materia.
 * 
 * <p>Usado en endpoints de consulta de materias, incluyendo búsquedas
 * y listados del catálogo.</p>
 * 
 * @see MateriaRequest
 * @see edu.dosw.sirha.model.Materia
 */
@Value
@Builder
public class MateriaResponse {
    /** ID único de la materia. */
    String id;
    
    /** Mnemónico (código corto). */
    String mnemonico;
    
    /** Nombre completo. */
    String nombre;
    
    /** Créditos académicos. */
    int creditos;
    
    /** Horas presenciales semanales. */
    int horasPresenciales;
    
    /** Horas independientes semanales. */
    int horasIndependientes;
    
    /** Nivel sugerido (1-10). */
    int nivel;
    
    /** Requiere laboratorio. */
    boolean laboratorio;
    
    /** ID de la facultad. */
    String facultadId;
    
    /** IDs de prerequisitos. */
    List<String> prerequisitos;
    
    /** IDs de materias que desbloquea. */
    List<String> desbloquea;
    
    /** Está activa en catálogo. */
    boolean activo;
    
    /** Términos de búsqueda. */
    List<String> searchTerms;
}