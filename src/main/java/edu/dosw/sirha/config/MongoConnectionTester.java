package edu.dosw.sirha.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Componente de diagnóstico para verificar la conectividad con MongoDB Atlas.
 * 
 * <p>Esta clase se ejecuta automáticamente al iniciar la aplicación SIRHA para
 * validar que la conexión con la base de datos MongoDB Atlas está funcionando
 * correctamente. Proporciona información detallada sobre el estado de la conexión
 * y sugerencias de solución en caso de errores.</p>
 * 
 * <p><strong>Funcionalidades de diagnóstico:</strong></p>
 * <ul>
 *   <li><strong>Ping a MongoDB:</strong> Verifica conectividad básica con el cluster</li>
 *   <li><strong>Listado de colecciones:</strong> Muestra todas las colecciones disponibles</li>
 *   <li><strong>Información de configuración:</strong> Muestra URI y database configurados (con datos sensibles enmascarados)</li>
 *   <li><strong>Mensajes de error detallados:</strong> Proporciona pasos de solución cuando falla la conexión</li>
 * </ul>
 * 
 * <p><strong>Ejecución y timing:</strong></p>
 * <p>Este componente se ejecuta después de que Spring Boot haya inicializado el contexto
 * pero antes de que la aplicación esté lista para recibir requests HTTP. Esto permite
 * detectar problemas de conectividad tempranamente.</p>
 * 
 * <p><strong>Seguridad:</strong></p>
 * <p>Las credenciales en la URI de MongoDB son enmascaradas automáticamente en los logs
 * para prevenir exposición accidental de información sensible.</p>
 * 
 * <p><strong>Ejemplo de salida exitosa:</strong></p>
 * <pre>{@code
 * === SIRHA - Verificación de Conexión MongoDB ===
 * Base de datos configurada: SIRHA
 * URI MongoDB: mongodb://****:****@sirha.qtoisgb.mongodb.net/SIRHA
 * ✅ Conexión a MongoDB exitosa
 * 📊 Colecciones disponibles:
 *   - usuarios
 *   - periodos
 *   - solicitudes
 * === Fin de verificación ===
 * }</pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0.0
 * @since 2025-10-14
 * @see MongoTemplate
 * @see CommandLineRunner
 */
@Component
@Slf4j
public class MongoConnectionTester implements CommandLineRunner {

    /** Template de Spring Data MongoDB para operaciones con la base de datos. */
    private final MongoTemplate mongoTemplate;
    
    /** URI de conexión a MongoDB (inyectado desde configuración). */
    @Value("${spring.data.mongodb.uri:No configurado}")
    private String mongoUri;
    
    /** Nombre de la base de datos MongoDB (inyectado desde configuración). */
    @Value("${spring.data.mongodb.database:No configurado}")
    private String mongoDatabase;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param mongoTemplate template de MongoDB para realizar operaciones de base de datos
     */
    public MongoConnectionTester(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Ejecuta la verificación de conectividad con MongoDB al iniciar la aplicación.
     * 
     * <p>Este método realiza las siguientes comprobaciones:</p>
     * <ol>
     *   <li>Muestra la configuración actual de MongoDB (URI y database)</li>
     *   <li>Ejecuta un comando ping para verificar conectividad básica</li>
     *   <li>Lista todas las colecciones disponibles en la base de datos</li>
     *   <li>En caso de error, proporciona pasos detallados de solución</li>
     * </ol>
     * 
     * <p><strong>Posibles problemas y soluciones:</strong></p>
     * <ul>
     *   <li><strong>Credenciales inválidas:</strong> Verificar usuario y contraseña en el archivo .env</li>
     *   <li><strong>IP no permitida:</strong> Añadir la IP del servidor en la configuración de MongoDB Atlas</li>
     *   <li><strong>Cluster inactivo:</strong> Confirmar que el cluster de MongoDB Atlas está en ejecución</li>
     *   <li><strong>Problemas de red:</strong> Verificar firewall y conectividad a internet</li>
     * </ul>
     * 
     * <p><strong>Nota de seguridad:</strong></p>
     * <p>La URI de MongoDB mostrada en los logs tiene las credenciales enmascaradas
     * automáticamente mediante el método {@link #maskMongoUri(String)} para prevenir
     * la exposición de información sensible.</p>
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     * @throws Exception si ocurre un error durante la verificación (se captura y registra)
     * @see MongoTemplate#getDb()
     * @see org.bson.Document
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("=== SIRHA - Verificación de Conexión MongoDB ===");
        log.info("Base de datos configurada: {}", mongoDatabase);
        log.info("URI MongoDB: {}", maskMongoUri(mongoUri));
        
        try {
            // Ping a MongoDB
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
            log.info("✅ Conexión a MongoDB exitosa");
            
            // Verificar colecciones existentes
            var collections = mongoTemplate.getDb().listCollectionNames();
            log.info("📊 Colecciones disponibles:");
            collections.forEach(collection -> log.info("  - {}", collection));
            
        } catch (Exception e) {
            log.error("❌ Error al conectar con MongoDB: {}", e.getMessage());
            log.error("🔧 Posibles soluciones:");
            log.error("   1. Verificar credenciales en archivo .env");
            log.error("   2. Revisar lista de IPs permitidas en MongoDB Atlas");
            log.error("   3. Confirmar que el cluster está activo");
            log.error("   4. Verificar conectividad de red");
        }
        log.info("=== Fin de verificación ===");
    }
    
    /**
     * Enmascara las credenciales en la URI de MongoDB para proteger información sensible.
     * 
     * <p>Este método reemplaza el usuario y contraseña en una URI de MongoDB con asteriscos,
     * permitiendo mostrar la estructura de la conexión en logs sin exponer credenciales reales.</p>
     * 
     * <p><strong>Ejemplo de transformación:</strong></p>
     * <pre>{@code
     * Entrada:  mongodb://usuario:password@cluster.mongodb.net/database
     * Salida:   mongodb://****:****@cluster.mongodb.net/database
     * }</pre>
     * 
     * <p>Este patrón de seguridad es importante para:</p>
     * <ul>
     *   <li>Prevenir exposición de credenciales en logs de producción</li>
     *   <li>Facilitar debugging sin comprometer seguridad</li>
     *   <li>Cumplir con buenas prácticas de manejo de información sensible</li>
     * </ul>
     * 
     * @param uri la URI de MongoDB con credenciales en texto plano
     * @return la misma URI con credenciales reemplazadas por asteriscos
     */
    private String maskMongoUri(String uri) {
        if (uri == null || !uri.contains("://")) {
            return uri;
        }
        return uri.replaceAll("://([^:]+):([^@]+)@", "://****:****@");
    }
}