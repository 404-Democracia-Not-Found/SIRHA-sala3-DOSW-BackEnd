# ============================================
# Multi-stage Dockerfile para SIRHA Backend
# ============================================
# Stage 1: Build - Compilación con Maven
# ============================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (para cache de dependencias)
COPY pom.xml .
COPY .mvn .mvn

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación (saltar tests para build más rápido)
# Si quieres ejecutar tests: mvn clean package -B
RUN mvn clean package -DskipTests -B

# ============================================
# Stage 2: Runtime - Imagen ligera para producción
# ============================================
FROM eclipse-temurin:21-jre-alpine

# Metadata del contenedor
LABEL maintainer="DOSW - SIRHA Team"
LABEL description="SIRHA Backend - Sistema de Inscripción y Reserva de Horarios Académicos"
LABEL version="1.0"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde el stage de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar propiedad del archivo al usuario spring
RUN chown spring:spring app.jar

# Cambiar al usuario no-root
USER spring:spring

# Exponer puerto 8081 (Swagger UI y API)
EXPOSE 8081

# Variables de entorno con valores por defecto
ENV SPRING_PROFILES_ACTIVE=production \
    SERVER_PORT=8081 \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Healthcheck para verificar que la aplicación está funcionando
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Comando de inicio con configuración optimizada para contenedores
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
