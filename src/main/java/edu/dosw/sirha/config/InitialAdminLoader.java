package edu.dosw.sirha.config;

import edu.dosw.sirha.model.User;
import edu.dosw.sirha.model.enums.Rol;
import edu.dosw.sirha.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

/**
 * Componente de inicialización que crea el usuario ADMIN inicial del sistema.
 * 
 * <p>Este componente refactorizado elimina las credenciales hardcoded y usa
 * variables de entorno para mayor seguridad. Solo crea el usuario ADMIN inicial
 * si no existe ningún usuario ADMIN en el sistema.</p>
 * 
 * <h2>Variables de Entorno Requeridas:</h2>
 * <ul>
 *   <li><b>ADMIN_EMAIL:</b> Email del administrador (ej: admin@sirha.local)</li>
 *   <li><b>ADMIN_PASSWORD:</b> Contraseña del administrador (mínimo 8 caracteres)</li>
 *   <li><b>ADMIN_NAME:</b> Nombre completo del administrador</li>
 * </ul>
 * 
 * <h2>Comportamiento:</h2>
 * <ul>
 *   <li>Se ejecuta solo si NO existe ningún usuario ADMIN</li>
 *   <li>Excluido en perfil 'test'</li>
 *   <li>Si faltan variables de entorno, registra un WARNING y no crea el admin</li>
 *   <li>La contraseña se encripta con BCrypt antes de almacenar</li>
 * </ul>
 * 
 * <h2>Creación de Otros Usuarios:</h2>
 * <p>Los usuarios de tipo ESTUDIANTE, DOCENTE y COORDINADOR deben crearse
 * mediante el endpoint <code>POST /api/auth/register</code>, eliminando así
 * las credenciales hardcoded del código fuente.</p>
 * 
 * <p><b>⚠️ Importante:</b> Este componente solo debe ejecutarse en la primera
 * instalación del sistema. Una vez creado el ADMIN, este puede crear otros usuarios
 * administrativos según sea necesario.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 3.0 (Refactorizado - Sin código quemado)
 * @since 2025-10-26
 * 
 * @see User
 * @see UserRepository
 * @see Rol
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class InitialAdminLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    /**
     * Email del administrador inicial (desde variable de entorno).
     */
    @Value("${sirha.admin.email:#{null}}")
    private String adminEmail;

    /**
     * Contraseña del administrador inicial (desde variable de entorno).
     */
    @Value("${sirha.admin.password:#{null}}")
    private String adminPassword;

    /**
     * Nombre del administrador inicial (desde variable de entorno).
     */
    @Value("${sirha.admin.name:#{null}}")
    private String adminName;

    /**
     * Ejecuta la lógica de creación del usuario ADMIN inicial.
     * 
     * <p>Solo crea el admin si:</p>
     * <ol>
     *   <li>No existe ningún usuario con rol ADMIN en el sistema</li>
     *   <li>Todas las variables de entorno están configuradas</li>
     * </ol>
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    @Override
    public void run(String... args) {
        log.info("Verificando existencia de usuario ADMIN en el sistema...");

        // Verificar si ya existe un ADMIN
        if (userRepository.existsByRol(Rol.ADMIN)) {
            log.info("Ya existe al menos un usuario ADMIN en el sistema. Omitiendo creación.");
            return;
        }

        log.info("No se encontró usuario ADMIN. Intentando crear desde variables de entorno...");

        // Validar que las variables de entorno estén configuradas
        if (adminEmail == null || adminEmail.isBlank() ||
            adminPassword == null || adminPassword.isBlank() ||
            adminName == null || adminName.isBlank()) {
            
            log.warn("╔════════════════════════════════════════════════════════════════╗");
            log.warn("║  ⚠️  NO SE CONFIGURARON LAS VARIABLES DE ENTORNO DEL ADMIN  ⚠️  ║");
            log.warn("╠════════════════════════════════════════════════════════════════╣");
            log.warn("║  Para crear el usuario administrador inicial, configure:      ║");
            log.warn("║                                                                ║");
            log.warn("║  • SIRHA_ADMIN_EMAIL=admin@sirha.local                        ║");
            log.warn("║  • SIRHA_ADMIN_PASSWORD=TuPasswordSeguro123!                  ║");
            log.warn("║  • SIRHA_ADMIN_NAME=Administrador Sistema                     ║");
            log.warn("║                                                                ║");
            log.warn("║  O use application.yml:                                        ║");
            log.warn("║  sirha:                                                        ║");
            log.warn("║    admin:                                                      ║");
            log.warn("║      email: ${SIRHA_ADMIN_EMAIL}                              ║");
            log.warn("║      password: ${SIRHA_ADMIN_PASSWORD}                        ║");
            log.warn("║      name: ${SIRHA_ADMIN_NAME}                                ║");
            log.warn("╚════════════════════════════════════════════════════════════════╝");
            return;
        }

        try {
            // Crear usuario ADMIN
            Instant now = Instant.now(clock);
            User admin = User.builder()
                    .nombre(adminName)
                    .email(adminEmail.toLowerCase())
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .rol(Rol.ADMIN)
                    .activo(true)
                    .creadoEn(now)
                    .actualizadoEn(now)
                    .build();

            userRepository.save(admin);

            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║  ✅ Usuario ADMIN creado exitosamente                         ║");
            log.info("╠════════════════════════════════════════════════════════════════╣");
            log.info("║  Email: {}", String.format("%-52s", adminEmail) + "║");
            log.info("║  Nombre: {}", String.format("%-51s", adminName) + "║");
            log.info("║                                                                ║");
            log.info("║  ⚠️  IMPORTANTE: Cambie la contraseña después del login       ║");
            log.info("╚════════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            log.error("Error al crear usuario ADMIN inicial: {}", e.getMessage(), e);
        }
    }
}