# SIRHA Backend - Sistema de Reasignación de Horarios Académicos

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green)
![Build](https://img.shields.io/badge/build-passing-success)

**API REST profesional para la gestión integral de horarios académicos de la Escuela Colombiana de Ingeniería**

[Documentación API](https://sistema-horarios-fyf5a2bkfggjc8hs.canadacentral-01.azurewebsites.net/swagger-ui.html) • [Repositorio Frontend](https://github.com/404-Democracia-Not-Found/SIRHA-sala3-DOSW-FrontEnd)

---

## Descripción del Proyecto

Sistema backend desarrollado en **Spring Boot** para la gestión integral de horarios académicos, solicitudes de reasignación y detección automática de conflictos. El sistema implementa principios SOLID, seguridad JWT, documentación Swagger y una arquitectura escalable basada en roles.

---

## Objetivos Cumplidos

- ✅ **Endpoints profesionales** – API REST completa y documentada
- ✅ **Principios SOLID** – Arquitectura limpia con separación de responsabilidades
- ✅ **MongoDB Atlas integrado** – Conexión exitosa a base de datos cloud
- ✅ **Swagger/OpenAPI** – Documentación interactiva de API
- ✅ **Autenticación JWT** – Seguridad implementada con Spring Security
- ✅ **Sistema de roles** – ADMIN, COORDINADOR, PROFESOR, ESTUDIANTE
- ✅ **CI/CD automatizado** – GitHub Actions con deploy a Azure
- ✅ **Detección de conflictos** – Validación automática de horarios

---

## Arquitectura del Sistema

### Capas de la Aplicación

```
Frontend (React) 
    ↓ HTTPS/REST API (JWT)
┌─────────────────────────────┐
│   Controllers (REST)        │ ← Presentation Layer
├─────────────────────────────┤
│   Services (Business)       │ ← Business Logic Layer
├─────────────────────────────┤
│   Repositories (Data)       │ ← Data Access Layer
├─────────────────────────────┤
│   Security (JWT + Spring)   │ ← Security Layer
└─────────────────────────────┘
    ↓ MongoDB Protocol
   MongoDB Atlas
```

### Patrones Implementados

- **MVC** con Service Layer
- **Repository Pattern** para abstracción de datos
- **DTO Pattern** para transferencia de datos
- **Dependency Injection** mediante Spring IoC
- **Strategy Pattern** para detección de conflictos
- Principios **SOLID**

---

## Tecnologías

- **Java 17** (Temurin JDK)
- **Spring Boot 3.x** (Web, Data MongoDB, Security, Actuator)
- **MongoDB Atlas** (base de datos NoSQL)
- **JWT** (io.jsonwebtoken) para autenticación
- **Spring Security 6.x** para autorización
- **Swagger/OpenAPI 3** para documentación
- **JUnit 5 + Mockito** para testing
- **JaCoCo** para cobertura de código
- **Maven** para gestión de dependencias
- **Lombok** para reducción de boilerplate
- **GitHub Actions** para CI/CD
- **Azure App Service** para hosting

---

## Prerrequisitos

- **Java 17** o superior ([descargar](https://adoptium.net/))
- **Maven 3.6+** ([descargar](https://maven.apache.org/download.cgi))
- **MongoDB Atlas** o MongoDB local
- **Git**

### Verificar instalación

```bash
java -version   # Java 17+
mvn -version    # Maven 3.6+
```

---

## Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/404-Democracia-Not-Found/SIRHA-sala3-DOSW-BackEnd.git
cd SIRHA-sala3-DOSW-BackEnd
```

### 2. Configurar variables de entorno

Crear archivo `.env` en la raíz:

```bash
MONGODB_URI=mongodb+srv://usuario:password@cluster.mongodb.net/
MONGODB_DATABASE=SIRHA
JWT_SECRET=tu-clave-secreta-minimo-256-bits
SERVER_PORT=8081
```

**⚠️ IMPORTANTE:** Nunca commitear `.env` a Git. Usar valores fuertes en producción.

### 3. Instalar y ejecutar

```bash
# Instalar dependencias
mvn clean install

# Ejecutar aplicación
mvn spring-boot:run
```

**Aplicación disponible en:** `http://localhost:8081`

### 4. Verificar instalación

- **Health Check:** http://localhost:8081/actuator/health
- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **API Docs:** http://localhost:8081/v3/api-docs

---

## Base de Datos

### Colecciones MongoDB

- `users` – Usuarios del sistema con roles
- `solicitudes` – Solicitudes de reasignación con historial
- `materias` – Catálogo de materias académicas
- `periodos` – Períodos académicos con configuración
- `conflicts` – Registro de conflictos detectados
- `grupos` – Grupos de clase con horarios
- `inscripciones` – Inscripciones de estudiantes
- `facultades` – Facultades académicas

### Índices Optimizados

- `users.email` (único)
- `solicitudes.estudianteId`
- `solicitudes.estado`
- `materias.codigo` (único)
- `periodos.activo`

---

## Sistema de Autenticación y Roles

### Roles Implementados

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| `ADMIN` | Administrador del sistema | Acceso completo a todas las operaciones |
| `COORDINADOR` | Coordinador académico | Aprobar/rechazar solicitudes, gestionar períodos |
| `PROFESOR` | Docente | Consultar horarios, ver solicitudes relacionadas |
| `ESTUDIANTE` | Estudiante | Crear solicitudes, consultar horarios propios |

### Seguridad JWT

- **Algoritmo:** HS256 (HMAC con SHA-256)
- **Expiración:** 60 minutos (configurable)
- **Validación:** Automática en cada request
- **Header:** `Authorization: Bearer {token}`
- **Passwords:** Hasheados con BCrypt (factor 10)

### Usuario Administrador por Defecto

```
Email: admin@sirha.local
Password: Admin123!
Rol: ADMIN
```

**⚠️ Cambiar estas credenciales en producción**

---

## Endpoints de la API

### Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `POST` | `/login` | Autenticación de usuarios | No |

**Ejemplo de Login:**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@sirha.local",
  "password": "Admin123!"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresAt": "2024-06-15T18:30:00Z",
  "userInfo": {
    "id": "507f1f77bcf86cd799439011",
    "nombre": "Administrador SIRHA",
    "email": "admin@sirha.local",
    "rol": "ADMIN"
  }
}
```

---

### Solicitudes (`/api/solicitudes`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `POST` | `/` | Crear solicitud de reasignación | ✅ |
| `GET` | `/` | Listar solicitudes (con filtros) | ✅ |
| `GET` | `/{id}` | Obtener solicitud específica | ✅ |
| `PUT` | `/{id}` | Actualizar solicitud | ✅ |
| `PATCH` | `/{id}/estado` | Cambiar estado (aprobar/rechazar) | ✅ Coordinador |
| `DELETE` | `/{id}` | Eliminar solicitud | ✅ |

**Estados de Solicitud:**
- `PENDIENTE` – Recién creada
- `EN_REVISION` – Siendo revisada
- `APROBADA` – Solicitud aprobada
- `RECHAZADA` – Solicitud rechazada
- `INFORMACION_ADICIONAL` – Requiere más información

---

### Materias (`/api/materias`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `POST` | `/` | Crear materia | ✅ Admin |
| `GET` | `/` | Listar todas las materias | ✅ |
| `GET` | `/{id}` | Obtener materia específica | ✅ |
| `GET` | `/search` | Buscar por facultad/semestre | ✅ |
| `PUT` | `/{id}` | Actualizar materia | ✅ Admin |
| `DELETE` | `/{id}` | Eliminar materia | ✅ Admin |

---

### Períodos Académicos (`/api/periodos`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `POST` | `/` | Crear período académico | ✅ Admin |
| `GET` | `/` | Listar todos los períodos | ✅ |
| `GET` | `/activo` | Obtener período activo actual | ✅ |
| `GET` | `/{id}` | Obtener período específico | ✅ |
| `POST` | `/{id}/activar` | Activar período | ✅ Admin |
| `PUT` | `/{id}` | Actualizar período | ✅ Admin |

---

### Conflictos (`/api/conflicts`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `POST` | `/detect` | Detectar conflictos de horario | ✅ |
| `GET` | `/solicitud/{id}` | Conflictos de una solicitud | ✅ |
| `GET` | `/` | Listar todos los conflictos | ✅ Admin |

---

### Documentación

- `GET /swagger-ui.html` – Interfaz Swagger interactiva
- `GET /v3/api-docs` – Especificación OpenAPI 3.0
- `GET /actuator/health` – Health check del sistema
- `GET /actuator/metrics` – Métricas de la aplicación

---

## Testing

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests con cobertura
mvn verify jacoco:report

# Reporte en: target/site/jacoco/index.html
```

### Cobertura

- **Objetivo:** >80% de cobertura de código
- **Herramientas:** JUnit 5, Mockito, JaCoCo
- **CI:** Automático en cada PR via GitHub Actions
- **Reportes:** Codecov

---

## CI/CD

### Pipelines Implementados

**Integración Continua (ci.yml)**
- Trigger: Push/PR a `develop`
- Ejecuta tests automáticos
- Genera reportes de cobertura
- Publica resultados en Codecov

**Despliegue Continuo (cd.yml)**
- Trigger: Push a `develop`
- Build con Maven
- Verificación de MongoDB
- Deploy automático a Azure

### Azure Deployment

**Producción:** https://sistema-horarios-fyf5a2bkfggjc8hs.canadacentral-01.azurewebsites.net

- **Runtime:** Java 17 (Temurin JDK)
- **Region:** Canada Central
- **Deploy:** Automático desde GitHub Actions

---

## Estructura del Proyecto

```
src/main/java/edu/dosw/sirha/
├── SirhaApplication.java          # Clase principal
├── config/                        # Configuraciones
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   └── WebConfig.java
├── controller/                    # REST Controllers
│   ├── AuthController.java
│   ├── SolicitudController.java
│   ├── MateriaController.java
│   ├── PeriodoController.java
│   └── ConflictController.java
├── service/                       # Lógica de negocio
│   ├── impl/
│   │   ├── SolicitudServiceImpl.java
│   │   ├── MateriaServiceImpl.java
│   │   └── ConflictDetectionServiceImpl.java
│   └── interfaces...
├── repository/                    # Acceso a datos
│   ├── SolicitudRepository.java
│   ├── MateriaRepository.java
│   └── UserRepository.java
├── model/                         # Entidades del dominio
│   ├── User.java
│   ├── Solicitud.java
│   ├── Materia.java
│   └── enums/
├── dto/                          # Data Transfer Objects
│   ├── request/
│   └── response/
├── security/                     # JWT y Spring Security
│   ├── JwtAuthFilter.java
│   ├── JwtTokenService.java
│   └── SecurityConfig.java
├── mapper/                       # Conversiones DTO ↔ Entity
└── exception/                    # Manejo de excepciones
    └── GlobalExceptionHandler.java
```

---

## Seguridad y Validaciones

### Implementadas

- ✅ Email válido y único
- ✅ Campos obligatorios validados
- ✅ Roles y permisos verificados
- ✅ Contraseñas encriptadas (BCrypt)
- ✅ Tokens JWT con expiración
- ✅ HTTPS en producción
- ✅ CORS configurado
- ✅ Validación de períodos académicos
- ✅ Detección automática de conflictos

---

## Recursos Adicionales

### Documentación

- **Documento de Arquitectura:** `/docs/arquitectura.md`
- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **API Docs:** http://localhost:8081/v3/api-docs

### Enlaces Útiles

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [MongoDB Spring Data](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [JWT.io](https://jwt.io/introduction)

---

## Equipo

**404-Democracia-Not-Found**

Curso: Ciclos de Vida de Desarrollo de Software  
Escuela Colombiana de Ingeniería Julio Garavito  
2025-2

---

## Soporte

¿Problemas o preguntas?

- 🐛 [Reportar un bug](https://github.com/404-Democracia-Not-Found/SIRHA-sala3-DOSW-BackEnd/issues)
- 📧 Contactar al equipo via GitHub

---

<div align="center">

**Desarrollado con ❤️ por 404-Democracia-Not-Found**

</div>