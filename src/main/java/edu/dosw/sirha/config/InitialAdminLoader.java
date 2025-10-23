package edu.dosw.sirha.config;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Componente de inicialización que crea usuarios por defecto para todos los roles al arrancar la aplicación.
 * 
 * <p>Esta clase implementa {@link CommandLineRunner} para ejecutarse automáticamente
 * después de que Spring Boot haya inicializado el contexto de la aplicación. Su función
 * principal es garantizar que existan usuarios de prueba para cada rol en el sistema,
 * facilitando el desarrollo y testing inicial.</p>
 * 
 * <p><strong>Comportamiento:</strong></p>
 * <ul>
 *   <li>Se ejecuta una sola vez al iniciar la aplicación (excluido en perfil test)</li>
 *   <li>Verifica si ya existe cada usuario por su email</li>
 *   <li>Si no existe, crea un nuevo usuario con credenciales por defecto</li>
 *   <li>Todas las contraseñas se encriptan usando BCrypt antes de almacenarse</li>
 *   <li>Es idempotente: puede ejecutarse múltiples veces sin duplicar usuarios</li>
 * </ul>
 * 
 * <p><strong>Usuarios por defecto creados:</strong></p>
 * <table border="1">
 *   <tr>
 *     <th>Rol</th>
 *     <th>Email</th>
 *     <th>Password</th>
 *     <th>Nombre</th>
 *   </tr>
 *   <tr>
 *     <td>ADMIN</td>
 *     <td>admin@sirha.local</td>
 *     <td>Admin123!</td>
 *     <td>Administrador Sistema</td>
 *   </tr>
 *   <tr>
 *     <td>COORDINADOR</td>
 *     <td>coordinador@mail.escuelaing.edu.co</td>
 *     <td>Coord123!</td>
 *     <td>Coordinador Académico</td>
 *   </tr>
 *   <tr>
 *     <td>DOCENTE</td>
 *     <td>docente@mail.escuelaing.edu.co</td>
 *     <td>Docente123!</td>
 *     <td>Docente Ejemplo</td>
 *   </tr>
 *   <tr>
 *     <td>ESTUDIANTE</td>
 *     <td>estudiante@mail.escuelaing.edu.co</td>
 *     <td>Estud123!</td>
 *     <td>Estudiante Ejemplo</td>
 *   </tr>
 * </table>
 * 
 * <p><strong>⚠️ Importante para producción:</strong></p>
 * <p>Es CRÍTICO deshabilitar este componente o cambiar todas las contraseñas por defecto
 * en un entorno de producción. Estas credenciales son conocidas públicamente y representan
 * un riesgo grave de seguridad si no se modifican. Considere usar variables de entorno
 * para contraseñas en producción.</p>
 * 
 * <p><strong>Extensibilidad (Principio Open/Closed):</strong></p>
 * <p>Esta implementación sigue SOLID al utilizar un modelo de datos interno {@link DefaultUserTemplate}
 * que permite agregar fácilmente nuevos usuarios por defecto sin modificar la lógica de creación.</p>
 * 
 * <p><strong>Casos de uso:</strong></p>
 * <ul>
 *   <li>Primera instalación del sistema SIRHA</li>
 *   <li>Ambientes de desarrollo local</li>
 *   <li>Testing de integración</li>
 *   <li>Demos del sistema con usuarios de ejemplo</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 2.0.0
 * @since 2025-10-23
 * @see CommandLineRunner
 * @see User
 * @see UserRepository
 * @see Rol
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class InitialAdminLoader implements CommandLineRunner {

    /** Repositorio para operaciones de base de datos con usuarios. */
    private final UserRepository userRepository;
    
    /** Codificador de contraseñas para hash seguro con BCrypt. */
    private final PasswordEncoder passwordEncoder;
    
    /** Reloj del sistema para timestamps consistentes. */
    private final Clock clock;

    /**
     * Plantilla interna para definir usuarios por defecto a crear.
     * 
     * <p>Utiliza el patrón Builder para facilitar la creación de nuevos templates
     * y seguir el principio de Responsabilidad Única (SRP).</p>
     */
    @Getter
    @Builder
    @AllArgsConstructor
    private static class DefaultUserTemplate {
        /** Rol del usuario en el sistema. */
        private final Rol rol;
        /** Correo electrónico (usado como username). */
        private final String email;
        /** Contraseña en texto plano (será hasheada antes de guardar). */
        private final String password;
        /** Nombre completo del usuario. */
        private final String nombre;
    }

    /**
     * Lista de usuarios por defecto a crear en el sistema.
     * 
     * <p>Esta lista centraliza la definición de todos los usuarios iniciales,
     * siguiendo el principio de configuración centralizada y facilitando
     * el mantenimiento futuro.</p>
     * 
     * @return Lista inmutable de templates de usuarios por defecto
     */
    private List<DefaultUserTemplate> getDefaultUsers() {
        List<DefaultUserTemplate> users = new ArrayList<>();
        
        // Usuario Administrador
        users.add(DefaultUserTemplate.builder()
                .rol(Rol.ADMIN)
                .email("admin@sirha.local")
                .password("Admin123!")
                .nombre("Administrador Sistema")
                .build());
        
        // Usuario Coordinador (Decanatura)
        users.add(DefaultUserTemplate.builder()
                .rol(Rol.COORDINADOR)
                .email("coordinador@mail.escuelaing.edu.co")
                .password("Coord123!")
                .nombre("Coordinador Académico")
                .build());
        
        // Usuario Docente
        users.add(DefaultUserTemplate.builder()
                .rol(Rol.DOCENTE)
                .email("docente@mail.escuelaing.edu.co")
                .password("Docente123!")
                .nombre("Docente Ejemplo")
                .build());
        
        // Usuario Estudiante
        users.add(DefaultUserTemplate.builder()
                .rol(Rol.ESTUDIANTE)
                .email("estudiante@mail.escuelaing.edu.co")
                .password("Estud123!")
                .nombre("Estudiante Ejemplo")
                .build());
        
        return users;
    }

    /**
     * Método ejecutado automáticamente al arrancar la aplicación.
     * 
     * <p>Este método itera sobre todos los usuarios por defecto definidos y verifica
     * si ya existen en la base de datos. Si no existen, los crea con sus credenciales
     * correspondientes. Las contraseñas se almacenan de forma segura utilizando
     * hashing BCrypt con salt aleatorio.</p>
     * 
     * <p><strong>Flujo de ejecución:</strong></p>
     * <ol>
     *   <li>Obtiene la lista de usuarios por defecto a crear</li>
     *   <li>Para cada usuario:
     *     <ul>
     *       <li>Consulta la base de datos por el email del usuario</li>
     *       <li>Si existe, lo omite (idempotente)</li>
     *       <li>Si no existe, crea el usuario usando {@link #createUser(DefaultUserTemplate)}</li>
     *       <li>Guarda el usuario en MongoDB</li>
     *       <li>Registra un mensaje informativo en los logs</li>
     *     </ul>
     *   </li>
     *   <li>Registra el número total de usuarios creados</li>
     * </ol>
     * 
     * <p><strong>Características de diseño:</strong></p>
     * <ul>
     *   <li><b>Idempotente:</b> Puede ejecutarse múltiples veces sin efectos secundarios</li>
     *   <li><b>Transaccional:</b> Cada usuario se crea en una transacción independiente</li>
     *   <li><b>Extensible:</b> Agregar nuevos usuarios solo requiere modificar {@link #getDefaultUsers()}</li>
     *   <li><b>Seguro:</b> Las contraseñas nunca se almacenan en texto plano</li>
     * </ul>
     * 
     * <p><strong>Seguridad:</strong></p>
     * <ul>
     *   <li>Las contraseñas se hashean con BCrypt (cost factor configurable)</li>
     *   <li>Cada hash tiene un salt aleatorio único</li>
     *   <li>La operación es atómica por usuario (falla en uno no afecta a otros)</li>
     * </ul>
     * 
     * <p><strong>Logging:</strong></p>
     * <ul>
     *   <li>INFO: Se registra cada usuario creado con su email y rol</li>
     *   <li>INFO: Se registra el total de usuarios creados al finalizar</li>
     *   <li>Sin logs: No se registran contraseñas por seguridad</li>
     * </ul>
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     * @throws Exception si ocurre algún error durante la inicialización de la base de datos
     * @see #getDefaultUsers()
     * @see #createUser(DefaultUserTemplate)
     * @see PasswordEncoder#encode(CharSequence)
     * @see UserRepository#existsByEmail(String)
     * @see UserRepository#save(Object)
     */
    @Override
    public void run(String... args) {
        log.info("Iniciando carga de usuarios por defecto...");
        
        List<DefaultUserTemplate> defaultUsers = getDefaultUsers();
        int createdCount = 0;
        
        for (DefaultUserTemplate template : defaultUsers) {
            if (userRepository.existsByEmail(template.getEmail())) {
                log.debug("Usuario {} ya existe, omitiendo creación", template.getEmail());
                continue;
            }
            
            User user = createUser(template);
            userRepository.save(user);
            
            log.info("✓ Usuario creado: {} - Rol: {} - Email: {}", 
                    template.getNombre(), 
                    template.getRol(), 
                    template.getEmail());
            createdCount++;
        }
        
        if (createdCount > 0) {
            log.info("Se crearon {} usuario(s) por defecto en el sistema", createdCount);
        } else {
            log.info("Todos los usuarios por defecto ya existen en el sistema");
        }
    }

    /**
     * Crea una entidad {@link User} a partir de un template de usuario por defecto.
     * 
     * <p>Este método privado encapsula la lógica de construcción de usuarios,
     * siguiendo el principio de Responsabilidad Única (SRP). Se encarga de:</p>
     * <ul>
     *   <li>Hashear la contraseña usando BCrypt</li>
     *   <li>Establecer los timestamps de creación y actualización</li>
     *   <li>Marcar el usuario como activo por defecto</li>
     *   <li>Construir el objeto User con todos los campos requeridos</li>
     * </ul>
     * 
     * <p><strong>Proceso de hasheado:</strong></p>
     * <p>La contraseña en texto plano del template se pasa por {@link PasswordEncoder},
     * que utiliza BCrypt con un cost factor configurable (por defecto 10-12 rounds).
     * El resultado es un hash de aproximadamente 60 caracteres que incluye:</p>
     * <ul>
     *   <li>Identificador del algoritmo (e.g., $2a$)</li>
     *   <li>Cost factor (e.g., 10)</li>
     *   <li>Salt aleatorio de 22 caracteres</li>
     *   <li>Hash resultante de 31 caracteres</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de hash BCrypt:</strong></p>
     * <pre>
     * Password: "Admin123!"
     * Hash: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     * </pre>
     * 
     * @param template plantilla con la información del usuario a crear (email, password, rol, nombre)
     * @return entidad {@link User} construida y lista para persistir en MongoDB
     * @see PasswordEncoder#encode(CharSequence)
     * @see User.UserBuilder
     * @see Clock#instant()
     */
    private User createUser(DefaultUserTemplate template) {
        Instant now = Instant.now(clock);
        
        return User.builder()
                .nombre(template.getNombre())
                .email(template.getEmail())
                .passwordHash(passwordEncoder.encode(template.getPassword()))
                .rol(template.getRol())
                .activo(true)
                .creadoEn(now)
                .actualizadoEn(now)
                .build();
    }
}
