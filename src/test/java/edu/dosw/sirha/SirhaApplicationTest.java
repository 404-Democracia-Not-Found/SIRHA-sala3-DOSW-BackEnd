package edu.dosw.sirha;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de integración básica para la aplicación SIRHA.
 * 
 * <p>Esta clase verifica que el contexto de Spring Boot se carga correctamente al iniciar
 * la aplicación, incluyendo la configuración de todos los beans, componentes, servicios,
 * repositorios, controladores y configuraciones de seguridad.</p>
 * 
 * <p><strong>Propósito de la prueba:</strong></p>
 * <ul>
 *   <li>Valida que todas las anotaciones de Spring están correctas (@Component, @Service, etc.)</li>
 *   <li>Verifica que las dependencias se inyectan sin conflictos</li>
 *   <li>Confirma que las configuraciones (application.yml, Java Config) son válidas</li>
 *   <li>Detecta errores de carga de beans o problemas de configuración</li>
 *   <li>Asegura que MongoDB, seguridad y otros componentes se inicializan</li>
 * </ul>
 * 
 * <p>Esta prueba actúa como un smoke test que falla rápidamente si hay problemas
 * fundamentales en la configuración de la aplicación Spring Boot.</p>
 * 
 * @see org.springframework.boot.test.context.SpringBootTest
 */
@SpringBootTest(properties = "spring.autoconfigure.exclude=de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration")
class SirhaApplicationTest {

	/**
	 * Verifica que el contexto de aplicación Spring Boot se carga sin errores.
	 * 
	 * <p>Si esta prueba falla, indica un problema grave de configuración que impediría
	 * el arranque de la aplicación en producción.</p>
	 */
	@Test
	void contextLoads() {
		// Ensures the Spring context bootstraps.
	}
}
