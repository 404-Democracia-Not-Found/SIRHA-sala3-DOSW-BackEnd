package edu.dosw.sirha.model.enums;

/**
 * Enumeración que define los roles de usuario en el sistema SIRHA.
 * 
 * <p>Los roles determinan los permisos y capacidades de cada usuario en el sistema.
 * Se utilizan en Spring Security para control de acceso a endpoints y funcionalidades.</p>
 * 
 * <h2>Jerarquía de Permisos:</h2>
 * <pre>
 * ADMIN (todos los permisos)
 *   ↓
 * COORDINADOR (gestión de solicitudes de su facultad)
 *   ↓
 * DOCENTE (consulta de grupos e estudiantes)
 *   ↓
 * ESTUDIANTE (gestión de solicitudes propias)
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see edu.dosw.sirha.model.User
 * @see edu.dosw.sirha.security.SecurityConfig
 */
public enum Rol {
    /**
     * Usuario estudiante que puede gestionar sus propias solicitudes de cambio.
     * <p><b>Permisos:</b></p>
     * <ul>
     *   <li>Crear solicitudes de cambio de horario</li>
     *   <li>Consultar sus propias solicitudes</li>
     *   <li>Actualizar solicitudes en estado PENDIENTE o INFORMACION_ADICIONAL</li>
     *   <li>Cancelar solicitudes propias en estado PENDIENTE</li>
     *   <li>Ver conflictos detectados en sus solicitudes</li>
     * </ul>
     */
    ESTUDIANTE,
    
    /**
     * Usuario docente que puede consultar información de sus grupos.
     * <p><b>Permisos:</b></p>
     * <ul>
     *   <li>Consultar grupos asignados</li>
     *   <li>Ver lista de estudiantes inscritos</li>
     *   <li>Consultar solicitudes relacionadas con sus grupos</li>
     *   <li>Ver horarios y asignaciones de aulas</li>
     * </ul>
     */
    DOCENTE,
    
    /**
     * Usuario coordinador/decanatura que gestiona solicitudes de su facultad.
     * <p><b>Permisos:</b></p>
     * <ul>
     *   <li>Ver todas las solicitudes de su facultad</li>
     *   <li>Aprobar o rechazar solicitudes</li>
     *   <li>Solicitar información adicional a estudiantes</li>
     *   <li>Agregar observaciones a solicitudes</li>
     *   <li>Consultar conflictos y métricas de su facultad</li>
     *   <li>Gestionar materias y grupos de su facultad</li>
     * </ul>
     */
    COORDINADOR,
    
    /**
     * Usuario administrador con acceso completo al sistema.
     * <p><b>Permisos:</b></p>
     * <ul>
     *   <li>Acceso completo a todas las funcionalidades</li>
     *   <li>Gestionar usuarios (crear, editar, desactivar)</li>
     *   <li>Configurar períodos académicos</li>
     *   <li>Gestionar facultades, materias y grupos</li>
     *   <li>Ver y gestionar todas las solicitudes del sistema</li>
     *   <li>Acceso a configuraciones del sistema</li>
     *   <li>Generar reportes globales</li>
     * </ul>
     */
    ADMIN
}
