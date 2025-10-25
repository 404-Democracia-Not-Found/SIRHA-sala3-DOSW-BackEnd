package edu.dosw.sirha.model;

import java.time.Instant;
import java.util.ArrayList;
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
 * Entidad de dominio que representa un grupo (sección) de una materia en el sistema SIRHA.
 * 
 * <p>Un grupo es una oferta específica de una materia en un período académico, con horario,
 * profesor y salón asignados. Una materia puede tener múltiples grupos en el mismo período
 * con diferentes horarios para acomodar a más estudiantes.</p>
 * 
 * <h2>Conceptos Clave:</h2>
 * <ul>
 *   <li><b>Código:</b> Identificador del grupo (ej: "01", "02", "A", "B")</li>
 *   <li><b>Cupo máximo:</b> Número máximo de estudiantes permitidos</li>
 *   <li><b>Cupos actuales:</b> Número de estudiantes inscritos actualmente</li>
 *   <li><b>Lista de espera:</b> Estudiantes esperando cupo cuando el grupo está lleno</li>
 * </ul>
 * 
 * <h2>Gestión de Cupos:</h2>
 * <p>El sistema maneja cupos automáticamente:</p>
 * <ul>
 *   <li>Al inscribir: se incrementa {@code cuposActuales}</li>
 *   <li>Al cancelar: se decrementa {@code cuposActuales}</li>
 *   <li>Al llenar: estudiantes van a {@code listaEspera}</li>
 *   <li>Alerta al 90% de ocupación ({@code estaEnAlerta()})</li>
 * </ul>
 * 
 * <h2>Horarios del Grupo:</h2>
 * <p>Cada grupo tiene una lista de {@link Horario} que define cuándo se dictan las clases.
 * Los horarios se validan automáticamente para detectar conflictos con otros grupos.</p>
 * 
 * <h2>Métodos Útiles:</h2>
 * <ul>
 *   <li>{@code tieneCuposDisponibles()} - Verifica si hay espacio</li>
 *   <li>{@code porcentajeOcupacion()} - Calcula % de llenado</li>
 *   <li>{@code estaEnAlerta()} - True si está al 90%+ de capacidad</li>
 *   <li>{@code tieneConflictoHorarioCon(otroGrupo)} - Detecta cruces de horario</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see Materia
 * @see Horario
 * @see Periodo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "grupos")
public class Grupo {

    /** Identificador único del grupo en MongoDB. */
    @Id
    private String id;

    /**
     * Código identificador del grupo dentro de la materia.
     * <p><b>Ejemplos:</b> "01", "02", "A", "B", "LAB1"</p>
     * <p>Junto con materia y período forman identificación única.</p>
     */
    @NotBlank
    private String codigo;

    /**
     * Referencia a la materia que se dicta en este grupo.
     * <p>Todos los grupos pertenecen a una materia específica.</p>
     * 
     * @see Materia
     */
    @NotBlank
    private String materiaId;

    /**
     * Referencia al período académico en que se oferta el grupo.
     * <p>Los grupos son específicos de un período (semestre).</p>
     * 
     * @see Periodo
     */
    @NotBlank
    private String periodoId;

    /**
     * Referencia al docente asignado al grupo.
     * <p>Puede ser null si aún no se ha asignado profesor.</p>
     * 
     * @see User
     */
    private String profesorId;

    /**
     * Capacidad máxima del grupo (cupo total).
     * <p>Número máximo de estudiantes que pueden inscribirse.
     * Depende de capacidad del salón y políticas académicas.</p>
     * <p><b>Típicamente:</b> 30-40 estudiantes por grupo</p>
     */
    @Min(0)
    private int cupoMax;

    /**
     * Número de estudiantes actualmente inscritos.
     * <p>Se actualiza automáticamente con inscripciones/cancelaciones.</p>
     * <p><b>Invariante:</b> {@code 0 <= cuposActuales <= cupoMax}</p>
     */
    @Min(0)
    private int cuposActuales;

    /**
     * Salón o aula asignado al grupo.
     * <p><b>Ejemplos:</b> "H-201", "LAB-A3", "Auditorio Principal"</p>
     */
    private String salon;

    /**
     * Fecha de inicio de clases del grupo.
     * <p>Primera sesión del grupo en el semestre.</p>
     */
    private Instant fechaInicio;
    
    /**
     * Fecha de finalización de clases del grupo.
     * <p>Última sesión del grupo en el semestre.</p>
     */
    private Instant fechaFin;

    /**
     * Lista de horarios en los que se dicta el grupo.
     * <p>Un grupo puede tener múltiples horarios (ej: Lunes y Miércoles 8-10am).</p>
     * <p>Cada horario especifica día, hora inicio y hora fin.</p>
     * 
     * @see Horario
     */
    private List<Horario> horarios;

    /**
     * Lista de IDs de estudiantes en espera de cupo.
     * <p>Cuando el grupo está lleno, estudiantes pueden agregarse a lista de espera.
     * Si se libera un cupo, se notifica al primer estudiante de la lista.</p>
     */
    private List<String> listaEspera;

    /**
     * Indica si el grupo está activo y disponible para inscripciones.
     * <p><b>false:</b> Grupo cancelado o cerrado</p>
     */
    private boolean activo;
    
    /**
     * Verifica si el grupo tiene cupos disponibles.
     * 
     * @return true si cuposActuales < cupoMax, false si está lleno
     */
    public boolean tieneCuposDisponibles() {
        return cuposActuales < cupoMax;
    }
    
    /**
     * Calcula el porcentaje de ocupación del grupo.
     * 
     * <p>Útil para dashboards, reportes y alertas de cupos críticos.</p>
     * 
     * @return Porcentaje de 0.0 a 100.0, o 0.0 si cupoMax es 0
     */
    public double porcentajeOcupacion() {
        if (cupoMax == 0) return 0.0;
        return (double) cuposActuales / cupoMax * 100;
    }
    
    /**
     * Verifica si el grupo está llegando al 90% de capacidad.
     * 
     * <p>Genera alertas para coordinación cuando el grupo está casi lleno.</p>
     * 
     * @return true si ocupación >= 90%, false en caso contrario
     */
    public boolean estaEnAlerta() {
        return porcentajeOcupacion() >= 90.0;
    }
    
    /**
     * Valida que todos los horarios del grupo sean válidos.
     * 
     * <p>Verifica que los horarios cumplan reglas institucionales:
     * dentro de horarios permitidos, no en domingos, etc.</p>
     * 
     * @return true si todos los horarios son válidos, false si hay al menos uno inválido o no hay horarios
     * 
     * @see Horario#esHorarioValido()
     */
    public boolean tieneHorariosValidos() {
        if (horarios == null || horarios.isEmpty()) {
            return false;
        }
        return horarios.stream().allMatch(Horario::esHorarioValido);
    }
    
    /**
     * Verifica si hay conflicto de horarios con otro grupo.
     * 
     * <p>Detecta si algún horario de este grupo se solapa con algún horario del otro grupo.
     * Usado para validar que un estudiante no tenga dos materias al mismo tiempo.</p>
     * 
     * @param otroGrupo El grupo con el que se compara
     * @return true si hay al menos un horario que se solapa, false si no hay conflictos
     * 
     * @see Horario#tieneConflictoCon(Horario)
     */
    public boolean tieneConflictoHorarioCon(Grupo otroGrupo) {
        if (this.horarios == null || otroGrupo.getHorarios() == null) {
            return false;
        }
        
        return this.horarios.stream()
                .anyMatch(horario1 -> otroGrupo.getHorarios().stream()
                        .anyMatch(horario1::tieneConflictoCon));
    }
    
    /**
     * Incrementa el cupo actual en 1 (para inscripciones).
     * 
     * <p>Llamado cuando un estudiante se inscribe exitosamente en el grupo.</p>
     * 
     * @return true si se pudo incrementar (había cupo), false si el grupo está lleno
     */
    public boolean incrementarCupo() {
        if (tieneCuposDisponibles()) {
            cuposActuales++;
            return true;
        }
        return false;
    }
    
    /**
     * Decrementa el cupo actual en 1 (para cancelaciones).
     * 
     * <p>Llamado cuando un estudiante cancela su inscripción o se retira del grupo.</p>
     * <p>Solo decrementa si cuposActuales > 0 para mantener invariantes.</p>
     */
    public void decrementarCupo() {
        if (cuposActuales > 0) {
            cuposActuales--;
        }
    }
    
    /**
     * Agrega un estudiante a la lista de espera del grupo.
     * 
     * <p>Llamado cuando el grupo está lleno pero el estudiante quiere ser notificado
     * si se libera un cupo. No permite duplicados.</p>
     * 
     * @param estudianteId ID del estudiante a agregar a lista de espera
     */
    public void agregarAListaEspera(String estudianteId) {
        if (listaEspera == null) {
            listaEspera = new ArrayList<>();
        }
        if (!listaEspera.contains(estudianteId)) {
            listaEspera.add(estudianteId);
        }
    }
    
    /**
     * Remueve un estudiante de la lista de espera.
     * 
     * <p>Llamado cuando el estudiante ya no desea esperar o logró inscribirse.</p>
     * 
     * @param estudianteId ID del estudiante a remover de lista de espera
     */
    public void removerDeListaEspera(String estudianteId) {
        if (listaEspera != null) {
            listaEspera.remove(estudianteId);
        }
    }
}
