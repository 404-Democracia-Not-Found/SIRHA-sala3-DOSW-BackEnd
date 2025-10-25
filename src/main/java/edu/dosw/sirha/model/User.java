package edu.dosw.sirha.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import edu.dosw.sirha.model.enums.Genero;
import edu.dosw.sirha.model.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que representa un usuario del sistema SIRHA.
 * 
 * <p>Esta clase modela a todos los usuarios del sistema, independientemente de su rol
 * (estudiantes, docentes, coordinadores, administradores). Cada usuario tiene credenciales
 * de autenticación, información personal y un rol que determina sus permisos en el sistema.</p>
 * 
 * <h2>Roles de Usuario:</h2>
 * <ul>
 *   <li><b>ESTUDIANTE:</b> Puede crear y consultar sus propias solicitudes de cambio de horario</li>
 *   <li><b>DOCENTE:</b> Puede consultar información de sus grupos y estudiantes inscritos</li>
 *   <li><b>COORDINADOR:</b> Puede gestionar solicitudes y aprobar/rechazar cambios de su facultad</li>
 *   <li><b>ADMIN:</b> Tiene acceso completo al sistema para configuración y administración</li>
 * </ul>
 * 
 * <h2>Campos Principales:</h2>
 * <ul>
 *   <li><b>email:</b> Identificador único del usuario, usado para autenticación</li>
 *   <li><b>passwordHash:</b> Contraseña cifrada con BCrypt (nunca se almacena en texto plano)</li>
 *   <li><b>rol:</b> Define los permisos y capacidades del usuario en el sistema</li>
 *   <li><b>activo:</b> Indica si la cuenta está habilitada o suspendida</li>
 *   <li><b>codigoEstudiante:</b> Código institucional (solo para estudiantes)</li>
 *   <li><b>semaforo:</b> Indicador de rendimiento académico (solo para estudiantes)</li>
 * </ul>
 * 
 * <h2>Campos Específicos de Estudiante:</h2>
 * <p>Los siguientes campos solo aplican para usuarios con rol {@code ESTUDIANTE}:</p>
 * <ul>
 *   <li><b>codigoEstudiante:</b> Código único institucional del estudiante</li>
 *   <li><b>semestre:</b> Semestre académico actual que está cursando</li>
 *   <li><b>facultadId:</b> Referencia a la facultad a la que pertenece</li>
 *   <li><b>semaforo:</b> {@link SemaforoAcademico} con métricas de rendimiento académico</li>
 * </ul>
 * 
 * <h2>Seguridad de Contraseñas:</h2>
 * <p>Las contraseñas se almacenan usando BCrypt con salt automático. El campo {@code passwordHash}
 * nunca debe contener texto plano. Para crear/actualizar contraseñas, usar
 * {@code PasswordEncoder.encode(rawPassword)} de Spring Security.</p>
 * 
 * <h2>Auditoría Temporal:</h2>
 * <ul>
 *   <li><b>creadoEn:</b> Timestamp de creación del usuario (automático con {@code @CreatedDate})</li>
 *   <li><b>actualizadoEn:</b> Timestamp de última modificación (automático con {@code @LastModifiedDate})</li>
 *   <li><b>ultimoAcceso:</b> Timestamp del último inicio de sesión exitoso</li>
 * </ul>
 * 
 * <h2>Indexación y Búsqueda:</h2>
 * <p>El campo {@code email} tiene un índice único en MongoDB para garantizar que no existan
 * duplicados y optimizar las búsquedas por email durante la autenticación.</p>
 * 
 * <h2>Estados de Cuenta:</h2>
 * <ul>
 *   <li><b>Activo ({@code activo=true}):</b> Usuario puede iniciar sesión y usar el sistema</li>
 *   <li><b>Inactivo ({@code activo=false}):</b> Cuenta suspendida, no puede autenticarse</li>
 * </ul>
 * 
 * <h2>Relaciones con Otras Entidades:</h2>
 * <pre>
 * User (Estudiante) ──┬──> Facultad (facultadId)
 *                     ├──> Solicitud (estudianteId)
 *                     ├──> Inscripcion (estudianteId)
 *                     └──> Conflict (estudianteId)
 * 
 * User (Docente) ──────> Grupo (profesorId)
 * User (Coordinador) ──> Facultad (decanoId)
 * </pre>
 * 
 * <p><b>Ejemplo de usuario estudiante:</b></p>
 * <pre>
 * {
 *   "id": "665d7f9a1234567890abcdef",
 *   "nombre": "María González Pérez",
 *   "email": "maria.gonzalez@mail.escuelaing.edu.co",
 *   "passwordHash": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
 *   "rol": "ESTUDIANTE",
 *   "activo": true,
 *   "codigoEstudiante": "2021105123",
 *   "semestre": 5,
 *   "facultadId": "665e8a1b2345678901bcdef0",
 *   "genero": "FEMENINO",
 *   "semaforo": {
 *     "materiasAprobadas": 18,
 *     "materiasEnProgreso": 5,
 *     "materiasPerdidas": 2
 *   },
 *   "creadoEn": "2024-01-15T10:30:00Z",
 *   "actualizadoEn": "2024-06-15T14:20:00Z",
 *   "ultimoAcceso": "2024-06-15T14:20:00Z"
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see Rol
 * @see Genero
 * @see SemaforoAcademico
 * @see edu.dosw.sirha.security.UserPrincipal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class User {

    /**
     * Identificador único del usuario en MongoDB.
     * Generado automáticamente como ObjectId.
     */
    @Id
    private String id;

    /**
     * Nombre completo del usuario.
     * <p>Campo obligatorio. Incluye nombres y apellidos.</p>
     * <p><b>Ejemplo:</b> "María González Pérez"</p>
     */
    @NotBlank
    private String nombre;

    /**
     * Correo electrónico institucional o personal del usuario.
     * <p>Este campo es único en toda la base de datos y se usa como identificador
     * principal para autenticación. Debe ser un email válido y no puede repetirse.</p>
     * <p><b>Indexado:</b> Índice único en MongoDB para optimizar búsquedas y garantizar unicidad.</p>
     * <p><b>Ejemplo:</b> "maria.gonzalez@mail.escuelaing.edu.co"</p>
     */
    @Email
    @NotBlank
    @Indexed(unique = true)
    private String email;

    /**
     * Contraseña del usuario cifrada con BCrypt.
     * <p><b>IMPORTANTE:</b> Nunca almacenar contraseñas en texto plano. Este campo debe
     * contener el hash generado por {@code PasswordEncoder.encode(rawPassword)}.</p>
     * <p>BCrypt genera hashes de 60 caracteres con salt automático incluido.</p>
     * <p><b>Ejemplo de hash:</b> "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"</p>
     * 
     * @see org.springframework.security.crypto.password.PasswordEncoder
     */
    @NotBlank
    private String passwordHash;

    /**
     * Rol del usuario que determina sus permisos y capacidades en el sistema.
     * <p>Campo obligatorio que define el nivel de acceso:</p>
     * <ul>
     *   <li>{@code ESTUDIANTE} - Puede crear y gestionar sus solicitudes</li>
     *   <li>{@code DOCENTE} - Puede consultar información de sus grupos</li>
     *   <li>{@code COORDINADOR} - Puede aprobar/rechazar solicitudes de su facultad</li>
     *   <li>{@code ADMIN} - Acceso completo al sistema</li>
     * </ul>
     * 
     * @see Rol
     */
    @NotNull
    private Rol rol;

    /**
     * Indica si la cuenta del usuario está activa o suspendida.
     * <p><b>true:</b> El usuario puede iniciar sesión y usar el sistema normalmente</p>
     * <p><b>false:</b> La cuenta está suspendida y no puede autenticarse</p>
     * <p>Las cuentas inactivas mantienen sus datos pero no permiten acceso al sistema.</p>
     */
    private boolean activo;

    /**
     * Código estudiantil institucional único.
     * <p>Solo aplica para usuarios con rol {@code ESTUDIANTE}. Es el identificador
     * académico oficial del estudiante en la institución.</p>
     * <p><b>Ejemplo:</b> "2021105123" (año de ingreso + código secuencial)</p>
     * <p><b>Opcional:</b> Puede ser null para usuarios no estudiantes.</p>
     */
    private String codigoEstudiante;

    /**
     * Semestre académico actual que está cursando el estudiante.
     * <p>Solo aplica para usuarios con rol {@code ESTUDIANTE}. Indica el nivel académico
     * en el que se encuentra el estudiante dentro de su carrera.</p>
     * <p><b>Rango típico:</b> 1 a 10 (dependiendo de la duración del programa)</p>
     * <p><b>Opcional:</b> Puede ser null para usuarios no estudiantes.</p>
     */
    private Integer semestre;

    /**
     * Referencia al ID de la facultad a la que pertenece el usuario.
     * <p>Para <b>estudiantes:</b> Facultad donde está inscrito en su programa académico</p>
     * <p>Para <b>docentes:</b> Facultad a la que pertenece (si aplica)</p>
     * <p>Para <b>coordinadores:</b> Facultad que administran</p>
     * <p><b>Relación:</b> Referencia a documento en colección "facultades"</p>
     * 
     * @see Facultad
     */
    private String facultadId;

    /**
     * Género del usuario para información demográfica y estadísticas.
     * <p>Campo opcional utilizado para reportes y análisis demográficos.
     * Respeta la privacidad del usuario con opción de no responder.</p>
     * <p><b>Valores posibles:</b> FEMENINO, MASCULINO, NO_BINARIO, PREFIERE_NO_RESPONDER</p>
     * 
     * @see Genero
     */
    private Genero genero;

    /**
     * Semáforo académico que refleja el rendimiento del estudiante.
     * <p>Solo aplica para usuarios con rol {@code ESTUDIANTE}. Contiene métricas de
     * desempeño académico que se usan para alertas y seguimiento estudiantil.</p>
     * <p><b>Métricas incluidas:</b></p>
     * <ul>
     *   <li>Materias aprobadas</li>
     *   <li>Materias en progreso (inscritas actualmente)</li>
     *   <li>Materias perdidas/reprobadas</li>
     * </ul>
     * <p>El sistema calcula automáticamente el porcentaje de avance y alertas basándose
     * en estas métricas.</p>
     * <p><b>Opcional:</b> Puede ser null para usuarios no estudiantes o estudiantes nuevos.</p>
     * 
     * @see SemaforoAcademico
     */
    private SemaforoAcademico semaforo;

    /**
     * Fecha y hora de creación del usuario en el sistema.
     * <p>Campo de auditoría gestionado automáticamente por Spring Data MongoDB.
     * Se establece una sola vez al crear el documento.</p>
     * <p><b>Formato:</b> Instant UTC (ISO-8601)</p>
     * <p><b>Ejemplo:</b> "2024-01-15T10:30:00Z"</p>
     */
    @CreatedDate
    @Field("creado_en")
    private Instant creadoEn;

    /**
     * Fecha y hora de la última modificación del usuario.
     * <p>Campo de auditoría gestionado automáticamente por Spring Data MongoDB.
     * Se actualiza cada vez que se modifica cualquier campo del documento.</p>
     * <p><b>Formato:</b> Instant UTC (ISO-8601)</p>
     */
    @LastModifiedDate
    @Field("actualizado_en")
    private Instant actualizadoEn;

    /**
     * Fecha y hora del último inicio de sesión exitoso del usuario.
     * <p>Este campo se actualiza manualmente en el controlador de autenticación
     * cada vez que el usuario inicia sesión exitosamente. Útil para auditoría
     * de seguridad y detección de cuentas inactivas.</p>
     * <p><b>Formato:</b> Instant UTC (ISO-8601)</p>
     * <p><b>Nota:</b> Puede ser null si el usuario nunca ha iniciado sesión.</p>
     */
    @Field("ultimo_acceso")
    private Instant ultimoAcceso;
}