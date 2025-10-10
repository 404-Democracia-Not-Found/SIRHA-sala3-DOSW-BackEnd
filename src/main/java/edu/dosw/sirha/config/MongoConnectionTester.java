package edu.dosw.sirha.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Verificador de conectividad con MongoDB Atlas
 * Se ejecuta al iniciar la aplicación para verificar la conexión
 */
@Component
@Slf4j
public class MongoConnectionTester implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    
    @Value("${spring.data.mongodb.uri:No configurado}")
    private String mongoUri;
    
    @Value("${spring.data.mongodb.database:No configurado}")
    private String mongoDatabase;

    public MongoConnectionTester(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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
    
    private String maskMongoUri(String uri) {
        if (uri == null || !uri.contains("://")) {
            return uri;
        }
        return uri.replaceAll("://([^:]+):([^@]+)@", "://****:****@");
    }
}