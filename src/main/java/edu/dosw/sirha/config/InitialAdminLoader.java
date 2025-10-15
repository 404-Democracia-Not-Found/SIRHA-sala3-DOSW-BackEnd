package edu.dosw.sirha.config;

import java.time.Clock;
import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Componente de inicialización que crea el usuario administrador por defecto al arrancar la aplicación.
 * 
 * <p>Esta clase implementa {@link CommandLineRunner} para ejecutarse automáticamente
 * después de que Spring Boot haya inicializado el contexto de la aplicación. Su función
 * principal es garantizar que siempre exista al menos un usuario administrador en el
 * sistema para poder realizar la configuración inicial.</p>
 * 
 * <p><strong>Comportamiento:</strong></p>
 * <ul>
 *   <li>Se ejecuta una sola vez al iniciar la aplicación</li>
 *   <li>Verifica si ya existe un usuario con el email del administrador</li>
 *   <li>Si no existe, crea un nuevo usuario administrador con credenciales por defecto</li>
 *   <li>La contraseña se encripta usando BCrypt antes de almacenarse</li>
 * </ul>
 * 
 * <p><strong>Credenciales por defecto:</strong></p>
 * <ul>
 *   <li><strong>Email:</strong> admin@sirha.local</li>
 *   <li><strong>Password:</strong> Admin123!</li>
 *   <li><strong>Rol:</strong> ADMIN</li>
 * </ul>
 * 
 * <p><strong>⚠️ Importante para producción:</strong></p>
 * <p>Es crítico cambiar la contraseña del administrador por defecto después del
 * primer inicio de sesión en un entorno de producción. Esta contraseña es conocida
 * públicamente y representa un riesgo de seguridad si no se modifica.</p>
 * 
 * <p><strong>Casos de uso:</strong></p>
 * <ul>
 *   <li>Primera instalación del sistema SIRHA</li>
 *   <li>Recuperación de acceso cuando no hay usuarios administradores</li>
 *   <li>Ambientes de desarrollo y testing</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0.0
 * @since 2025-10-14
 * @see CommandLineRunner
 * @see User
 * @see UserRepository
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitialAdminLoader implements CommandLineRunner {

    /** Repositorio para operaciones de base de datos con usuarios. */
    private final UserRepository userRepository;
    
    /** Codificador de contraseñas para hash seguro con BCrypt. */
    private final PasswordEncoder passwordEncoder;
    
    /** Reloj del sistema para timestamps consistentes. */
    private final Clock clock;

    /** Email por defecto del usuario administrador inicial. */
    private static final String DEFAULT_EMAIL = "admin@sirha.local";
    
    /** 
     * Contraseña por defecto del administrador inicial.
     * ⚠️ DEBE cambiarse en producción por seguridad.
     */
    private static final String DEFAULT_PASSWORD = "Admin123!";

    /**
     * Método ejecutado automáticamente al arrancar la aplicación.
     * 
     * <p>Este método verifica si ya existe un usuario administrador en la base de datos.
     * Si no existe, crea uno nuevo con las credenciales por defecto. La contraseña se
     * almacena de forma segura utilizando hashing BCrypt.</p>
     * 
     * <p><strong>Flujo de ejecución:</strong></p>
     * <ol>
     *   <li>Consulta la base de datos por el email del administrador</li>
     *   <li>Si existe, termina sin hacer nada (idempotente)</li>
     *   <li>Si no existe, crea un nuevo usuario con:
     *     <ul>
     *       <li>Nombre: "Administrador"</li>
     *       <li>Email: admin@sirha.local</li>
     *       <li>Contraseña: Admin123! (hasheada con BCrypt)</li>
     *       <li>Rol: ADMIN</li>
     *       <li>Estado: Activo</li>
     *     </ul>
     *   </li>
     *   <li>Guarda el usuario en la base de datos</li>
     *   <li>Registra un mensaje informativo en los logs</li>
     * </ol>
     * 
     * <p><strong>Seguridad:</strong></p>
     * <ul>
     *   <li>La contraseña nunca se almacena en texto plano</li>
     *   <li>Se utiliza BCrypt con salt aleatorio</li>
     *   <li>La operación es idempotente (puede ejecutarse múltiples veces sin efectos secundarios)</li>
     * </ul>
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     * @throws Exception si ocurre algún error durante la inicialización
     * @see PasswordEncoder#encode(CharSequence)
     * @see UserRepository#existsByEmail(String)
     * @see UserRepository#save(Object)
     */
    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(DEFAULT_EMAIL)) {
            return;
        }
        User admin = User.builder()
                .nombre("Administrador")
                .email(DEFAULT_EMAIL)
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .rol(Rol.ADMIN)
                .activo(true)
                .creadoEn(Instant.now(clock))
                .actualizadoEn(Instant.now(clock))
                .build();
        userRepository.save(admin);
        log.info("Se creó el usuario administrador por defecto con email {}", DEFAULT_EMAIL);
    }
}
