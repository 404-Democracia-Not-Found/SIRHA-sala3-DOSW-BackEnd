package edu.dosw.sirha.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que representa una materia (asignatura) académica en el sistema SIRHA.
 * 
 * <p>Una materia es una asignatura que forma parte del plan de estudios de un programa académico.
 * Las materias se organizan por facultades, tienen créditos asociados, pueden tener prerrequisitos
 * y se ofertan en uno o más grupos por período académico.</p>
 * 
 * <h2>Identificación de Materias:</h2>
 * <ul>
 *   <li><b>Mnemónico:</b> Código único de la materia (ej: "CALC101", "PHYS202")</li>
 *   <li><b>Nombre:</b> Denominación oficial (ej: "Cálculo Diferencial", "Física II")</li>
 *   <li><b>Nivel:</b> Semestre sugerido en el plan de estudios (1-10)</li>
 * </ul>
 * 
 * <h2>Sistema de Créditos:</h2>
 * <p>Los créditos representan la carga académica de la materia. Según estándares colombianos:</p>
 * <ul>
 *   <li><b>1 crédito =</b> 48 horas totales de trabajo (16 semanas × 3 horas/semana)</li>
 *   <li><b>Horas presenciales:</b> Tiempo en aula con docente</li>
 *   <li><b>Horas independientes:</b> Trabajo autónomo del estudiante</li>
 *   <li><b>Distribución típica:</b> 1 hora presencial : 2 horas independientes</li>
 * </ul>
 * 
 * <h2>Prerrequisitos y Encadenamiento:</h2>
 * <ul>
 *   <li><b>prerequisitos:</b> Lista de materias que se deben aprobar ANTES de cursar esta</li>
 *   <li><b>desbloquea:</b> Lista de materias que se habilitan al aprobar esta</li>
 * </ul>
 * <p>Esto crea un grafo de dependencias que el sistema valida automáticamente.</p>
 * 
 * <h2>Materias con Laboratorio:</h2>
 * <p>El campo {@code laboratorio} indica si la materia incluye componente práctico en laboratorio.
 * Estas materias típicamente tienen horarios adicionales y requisitos especiales de aula/equipo.</p>
 * 
 * <h2>Búsqueda y Filtrado:</h2>
 * <p>El campo {@code searchTerms} contiene términos de búsqueda precalculados (mnemónico, nombre,
 * palabras clave) para optimizar búsquedas de texto completo en MongoDB.</p>
 * 
 * <h2>Relaciones:</h2>
 * <pre>
 * Materia ──┬──> Facultad (facultadId)
 *           ├──> Materia[] (prerequisitos)
 *           ├──> Materia[] (desbloquea)
 *           └──> Grupo[] (grupos ofertados por período)
 * </pre>
 * 
 * <p><b>Ejemplo de materia:</b></p>
 * <pre>
 * {
 *   "id": "665f9b2c3456789012cdef01",
 *   "mnemonico": "CALC101",
 *   "nombre": "Cálculo Diferencial",
 *   "creditos": 4,
 *   "horasPresenciales": 4,
 *   "horasIndependientes": 8,
 *   "nivel": 1,
 *   "laboratorio": false,
 *   "facultadId": "665d7f9a1234567890abcdef",
 *   "prerequisitos": [],
 *   "desbloquea": ["665f9b2c3456789012cdef02"],
 *   "activo": true,
 *   "searchTerms": ["CALC101", "calculo", "diferencial", "derivadas"]
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see Grupo
 * @see Facultad
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "materias")
public class Materia {

    /**
     * Identificador único de la materia en MongoDB.
     */
    @Id
    private String id;

    /**
     * Código mnemónico único de la materia.
     * <p>Identificador institucional de la materia usado en documentos académicos oficiales.</p>
     * <p><b>Formato típico:</b> Siglas + Número (ej: "CALC101", "PHYS202", "PROG301")</p>
     * <p><b>Debe ser único</b> en toda la institución.</p>
     */
    @NotBlank
    private String mnemonico;

    /**
     * Nombre oficial completo de la materia.
     * <p><b>Ejemplos:</b></p>
     * <ul>
     *   <li>"Cálculo Diferencial"</li>
     *   <li>"Física para Ingenieros II"</li>
     *   <li>"Programación Orientada a Objetos"</li>
     * </ul>
     */
    @NotBlank
    private String nombre;

    /**
     * Número de créditos académicos de la materia.
     * <p>Representa la carga académica total. En Colombia, 1 crédito = 48 horas de trabajo
     * (presencial + independiente) durante un semestre de 16 semanas.</p>
     * <p><b>Rango típico:</b> 1-6 créditos</p>
     * <p><b>Validación:</b> Debe ser mayor o igual a 0</p>
     */
    @Min(0)
    private int creditos;

    /**
     * Horas semanales de trabajo presencial con docente.
     * <p>Tiempo que el estudiante debe asistir a clase cada semana.</p>
     * <p><b>Ejemplo:</b> Materia de 4 créditos típicamente tiene 4 horas presenciales</p>
     */
    private int horasPresenciales;
    
    /**
     * Horas semanales de trabajo independiente del estudiante.
     * <p>Tiempo estimado que el estudiante debe dedicar fuera del aula (tareas, estudio, proyectos).</p>
     * <p><b>Regla general:</b> 2 horas independientes por cada hora presencial</p>
     */
    private int horasIndependientes;
    
    /**
     * Nivel sugerido de la materia en el plan de estudios.
     * <p>Indica en qué semestre se recomienda cursar la materia.</p>
     * <p><b>Rango:</b> 1-10 (típico para programas de pregrado)</p>
     * <p><b>Ejemplo:</b> nivel=1 para materias de primer semestre</p>
     */
    private int nivel;
    
    /**
     * Indica si la materia incluye componente de laboratorio.
     * <p><b>true:</b> Materia con prácticas de laboratorio (requiere equipos, espacios especiales)</p>
     * <p><b>false:</b> Materia teórica o teórico-práctica sin laboratorio</p>
     * <p>Las materias con laboratorio pueden tener horarios adicionales y restricciones de salón.</p>
     */
    private boolean laboratorio;

    /**
     * Referencia a la facultad a la que pertenece la materia.
     * <p>Relaciona la materia con su unidad académica organizadora.</p>
     * <p><b>Relación:</b> Documento en colección "facultades"</p>
     * 
     * @see Facultad
     */
    private String facultadId;

    /**
     * Lista de IDs de materias que son prerrequisitos de esta materia.
     * <p>Materias que el estudiante debe haber aprobado antes de inscribir esta.
     * El sistema valida automáticamente el cumplimiento de prerrequisitos.</p>
     * <p><b>Ejemplo:</b> "Cálculo Integral" tiene como prerrequisito "Cálculo Diferencial"</p>
     * <p><b>Puede ser vacía</b> si la materia no tiene prerrequisitos (típico de primer semestre)</p>
     */
    private List<String> prerequisitos;

    /**
     * Lista de IDs de materias que se desbloquean al aprobar esta materia.
     * <p>Materias posteriores que requieren esta como prerrequisito.
     * Se calcula generalmente de forma inversa a {@code prerequisitos}.</p>
     * <p><b>Uso:</b> Visualización de rutas académicas y planeación de semestres futuros</p>
     */
    private List<String> desbloquea;

    /**
     * Indica si la materia está activa en el catálogo académico.
     * <p><b>true:</b> Materia vigente, puede ofertarse en grupos</p>
     * <p><b>false:</b> Materia inactiva (obsoleta, retirada del plan de estudios)</p>
     * <p>Las materias inactivas se mantienen en el sistema para preservar históricos académicos.</p>
     */
    private boolean activo;

    /**
     * Términos de búsqueda precalculados para optimizar consultas de texto.
     * <p>Lista de palabras clave derivadas del mnemónico, nombre y conceptos principales
     * de la materia. Se usa para búsqueda full-text eficiente en MongoDB.</p>
     * <p><b>Ejemplo para "CALC101 - Cálculo Diferencial":</b></p>
     * <pre>["CALC101", "calculo", "diferencial", "derivadas", "limites"]</pre>
     * <p>Se actualiza automáticamente al crear/modificar la materia.</p>
     */
    private List<String> searchTerms;
}