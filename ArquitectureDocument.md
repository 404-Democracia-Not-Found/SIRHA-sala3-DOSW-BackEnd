# DOCUMENTO DE ARQUITECTURA DE SOFTWARE
## SIRHA - Sistema de Reasignación de Horarios Académicos

**Equipo:** Dockerizados pero libres  
**Curso:** Desarrollo y Operaciones Software
**Fecha:** Octubre 2025  
**Versión:** 2.0

---

## 1. INTRODUCCIÓN

### 1.1 Propósito
Este documento describe la arquitectura de software del Sistema de Reasignación de Horarios Académicos (SIRHA) de la Escuela Colombiana de Ingeniería. Define la estructura del sistema, componentes principales, tecnologías utilizadas y decisiones arquitectónicas.

### 1.2 Alcance
SIRHA es una aplicación web que permite a estudiantes, profesores y coordinadores académicos gestionar y optimizar la reasignación de horarios académicos dentro de la institución. El sistema gestiona solicitudes de cambio de horario, detecta conflictos automáticamente, y proporciona un flujo de aprobación para coordinadores.

### 1.3 Audiencia
- Equipo de desarrollo
- Coordinadores académicos
- Futuros mantenedores del sistema

### 1.4 Referencias
- Repositorio Backend: https://github.com/404-Democracia-Not-Found/SIRHA-sala3-DOSW-BackEnd
- Repositorio Frontend: https://github.com/404-Democracia-Not-Found/SIRHA-sala3-DOSW-FrontEnd
- API Documentation (Swagger): https://sistema-horarios-fyf5a2bkfggjc8hs.canadacentral-01.azurewebsites.net/swagger-ui.html
- Azure Deployment: https://sistema-horarios-fyf5a2bkfggjc8hs.canadacentral-01.azurewebsites.net

---

## 2. VISIÓN GENERAL DE LA ARQUITECTURA

### 2.1 Estilo Arquitectónico
**Cliente-Servidor con Arquitectura en Capas**

El sistema implementa una arquitectura de tres capas claramente separadas:
- **Capa de Presentación:** Aplicación React (Frontend)
- **Capa de Lógica de Negocio:** API REST con Spring Boot (Backend)
- **Capa de Datos:** MongoDB Atlas

### 2.2 Patrón Arquitectónico Principal
**MVC (Model-View-Controller)** con Service Layer en el backend mediante Spring Boot

```
┌─────────────────────────────────────────┐
│          FRONTEND (React)               │
│     Presentación e Interacción          │
└──────────────┬──────────────────────────┘
               │ HTTPS/REST API
               │ JWT Authentication
┌──────────────▼──────────────────────────┐
│      BACKEND (Spring Boot)              │
│  ┌─────────────────────────────────┐    │
│  │  Controllers (@RestController)  │    │
│  │  - AuthController               │    │
│  │  - SolicitudController          │    │
│  │  - MateriaController            │    │
│  │  - PeriodoController            │    │
│  │  - ConflictController           │    │
│  └────────────┬────────────────────┘    │
│  ┌────────────▼────────────────────┐    │
│  │    Services (@Service)          │    │
│  │  - SolicitudServiceImpl         │    │
│  │  - MateriaServiceImpl           │    │
│  │  - PeriodoServiceImpl           │    │
│  │  - ConflictDetectionServiceImpl │    │
│  └────────────┬────────────────────┘    │
│  ┌────────────▼────────────────────┐    │
│  │  Repositories (MongoRepository) │    │
│  │  - SolicitudRepository          │    │
│  │  - MateriaRepository            │    │
│  │  - PeriodoRepository            │    │
│  │  - ConflictRepository           │    │
│  │  - UserRepository               │    │
│  └────────────┬────────────────────┘    │
│  ┌────────────▼────────────────────┐    │
│  │  Security Layer                 │    │
│  │  - JwtAuthFilter                │    │
│  │  - JwtTokenService              │    │
│  │  - SecurityConfig               │    │
│  └─────────────────────────────────┘    │
└───────────────┼─────────────────────────┘
                │ MongoDB Protocol
┌───────────────▼─────────────────────────┐
│        MongoDB Atlas (NoSQL)            │
│         Base de Datos SIRHA             │
└─────────────────────────────────────────┘
```

---

## 3. ARQUITECTURA DEL SISTEMA

### 3.1 Diagrama de Contexto

```
<img width="921" height="516" alt="image" src="https://github.com/user-attachments/assets/1179afc4-c796-4cea-9f97-6abb0ae849df" />

```

### 3.2 Estructura del Proyecto Backend

**Paquete raíz:** `edu.dosw.sirha`

```
src/main/java/edu/dosw/sirha/
│
├── SirhaApplication.java                # Clase principal Spring Boot
│
├── config/                              # Configuraciones
│   ├── AppConfig.java                   # Beans generales (Clock, ObjectMapper)
│   ├── DotenvApplicationContextInitializer.java  # Carga variables .env
│   ├── InitialAdminLoader.java          # Carga admin inicial
│   ├── MongoConnectionTester.java       # Verifica conexión MongoDB
│   ├── OpenApiConfig.java               # Configuración Swagger/OpenAPI
│   ├── SecurityBeansConfig.java         # Beans de seguridad (PasswordEncoder)
│   └── WebConfig.java                   # Configuración CORS
│
├── controller/                          # REST Controllers
│   ├── AuthController.java              # Login y autenticación
│   ├── ConflictController.java          # Gestión de conflictos de horarios
│   ├── MateriaController.java           # CRUD de materias
│   ├── PeriodoController.java           # Gestión de períodos académicos
│   └── SolicitudController.java         # Solicitudes de reasignación
│
├── dto/                                 # Data Transfer Objects
│   ├── auth/
│   │   ├── AuthRequest.java             # DTO para login
│   │   └── AuthResponse.java            # DTO respuesta con JWT
│   ├── request/
│   │   ├── ConflictRequest.java
│   │   ├── MateriaRequest.java
│   │   ├── PeriodoRequest.java
│   │   ├── SolicitudEstadoChangeRequest.java
│   │   └── SolicitudRequest.java
│   └── response/
│       ├── ConflictResponse.java
│       ├── MateriaResponse.java
│       ├── PeriodoResponse.java
│       └── SolicitudResponse.java
│
├── exception/                           # Manejo de excepciones
│   ├── BusinessException.java           # Excepciones de negocio
│   ├── GlobalExceptionHandler.java      # Handler global @ControllerAdvice
│   └── ResourceNotFoundException.java   # Excepciones de recursos no encontrados
│
├── mapper/                              # Mappers DTO ↔ Entity
│   ├── ConflictMapper.java
│   ├── MateriaMapper.java
│   ├── PeriodoMapper.java
│   └── SolicitudMapper.java
│
├── model/                               # Entidades del dominio (@Document)
│   ├── Conflict.java                    # Conflictos de horario
│   ├── Facultad.java                    # Facultades académicas
│   ├── Grupo.java                       # Grupos de clase
│   ├── Horario.java                     # Horarios de clase
│   ├── Inscripcion.java                 # Inscripciones de estudiantes
│   ├── Materia.java                     # Materias académicas
│   ├── Periodo.java                     # Períodos académicos
│   ├── PeriodoConfiguracion.java        # Configuración de períodos
│   ├── SemaforoAcademico.java          # Semáforo académico del estudiante
│   ├── Solicitud.java                   # Solicitudes de reasignación
│   ├── SolicitudHistorialEntry.java     # Historial de cambios de solicitud
│   ├── User.java                        # Usuarios del sistema
│   └── enums/                           # Enumeraciones
│       ├── EstadoInscripcion.java
│       ├── Genero.java
│       ├── Rol.java                     # ESTUDIANTE, PROFESOR, COORDINADOR, ADMIN
│       ├── SolicitudEstado.java         # PENDIENTE, EN_REVISION, APROBADA, RECHAZADA
│       └── SolicitudTipo.java
│
├── repository/                          # Repositorios MongoDB
│   ├── ConflictRepository.java
│   ├── FacultadRepository.java
│   ├── GrupoRepository.java
│   ├── InscripcionRepository.java
│   ├── MateriaRepository.java
│   ├── PeriodoRepository.java
│   ├── SolicitudRepository.java         # Con queries personalizadas
│   └── UserRepository.java
│
├── security/                            # Seguridad y JWT
│   ├── JwtAuthFilter.java               # Filtro de autenticación JWT
│   ├── JwtProperties.java               # Propiedades JWT (@ConfigurationProperties)
│   ├── JwtTokenService.java             # Generación y validación de tokens
│   ├── SecurityConfig.java              # Configuración Spring Security
│   └── UserPrincipal.java               # Implementación UserDetails
│
└── service/                             # Capa de servicios
    ├── ConflictDetectionService.java     # Interface
    ├── MateriaService.java               # Interface
    ├── PeriodoService.java               # Interface
    ├── SolicitudService.java             # Interface
    └── impl/                             # Implementaciones
        ├── ConflictDetectionServiceImpl.java  # Detección automática de conflictos
        ├── MateriaServiceImpl.java
        ├── PeriodoServiceImpl.java
        └── SolicitudServiceImpl.java
```

### 3.3 Componentes Principales del Backend

#### 3.3.1 Controllers (Capa de Presentación REST)

**AuthController** (`/api/auth`)
- `POST /login`: Autenticación con email/password → JWT token

**SolicitudController** (`/api/solicitudes`)
- Gestión completa de solicitudes de reasignación
- Cambio de estado (aprobación/rechazo)
- Búsqueda por estudiante, estado, período

**MateriaController** (`/api/materias`)
- CRUD de materias académicas
- Búsqueda por código, nombre, facultad

**PeriodoController** (`/api/periodos`)
- Gestión de períodos académicos
- Configuración de períodos activos

**ConflictController** (`/api/conflicts`)
- Detección y resolución de conflictos de horarios
- Validación de solapamientos

#### 3.3.2 Services (Capa de Lógica de Negocio)

**SolicitudServiceImpl**
- Validación de reglas de negocio para solicitudes
- Gestión de estados y prioridades
- Historial de cambios (`SolicitudHistorialEntry`)

**ConflictDetectionServiceImpl**
- **Algoritmo de detección de conflictos:**
  - Verifica solapamiento de horarios
  - Valida disponibilidad de salones
  - Detecta sobrecupo de grupos
  - Verifica conflictos de profesor

**MateriaServiceImpl**
- Lógica de gestión de materias
- Validación de prerrequisitos
- Asignación de profesores

**PeriodoServiceImpl**
- Control de períodos académicos activos
- Validación de fechas de solicitud
- Configuración de límites por período

#### 3.3.3 Security Layer (Seguridad)

**JwtAuthFilter**
- Intercepta requests HTTP
- Extrae y valida JWT del header `Authorization: Bearer {token}`
- Establece autenticación en `SecurityContext`

**JwtTokenService**
- Genera tokens JWT con HS256
- Valida firma y expiración
- Extrae claims (username, roles)
- Configuración:
  - `sirha.security.jwt.secret`: Clave secreta (mín 256 bits)
  - `sirha.security.jwt.expiration-minutes`: Tiempo de vida (default: 60 min)
  - `sirha.security.jwt.issuer`: Emisor (default: "sirha")

**SecurityConfig**
- Configuración de Spring Security
- Endpoints públicos: `/api/auth/**`, `/swagger-ui/**`, `/actuator/**`
- Endpoints protegidos: Requieren JWT válido
- Session management: STATELESS (sin sesiones HTTP)

**UserPrincipal**
- Implementa `UserDetails` de Spring Security
- Adapta modelo `User` a interface de seguridad
- Gestiona roles y permisos

#### 3.3.4 Repositories (Capa de Datos)

**Queries personalizadas implementadas:**

```java
// SolicitudRepository
List<Solicitud> findByEstudianteIdOrderByFechaSolicitudDesc(String estudianteId);
List<Solicitud> findByEstadoInOrderByPrioridadAsc(List<SolicitudEstado> estados);
long countByEstado(SolicitudEstado estado);
List<Solicitud> findByPeriodoIdAndFechaSolicitudBetween(String periodoId, Instant inicio, Instant fin);

// UserRepository
Optional<User> findByEmail(String email);

// Otros repositorios usan solo operaciones CRUD estándar
```

### 3.4 Modelo de Datos (MongoDB)

#### Colecciones Principales:

**users**
```json
{
  "_id": "ObjectId",
  "nombre": "String",
  "email": "String (unique)",
  "password": "String (BCrypt hashed)",
  "rol": "Enum [ESTUDIANTE, PROFESOR, COORDINADOR, ADMIN]",
  "activo": "Boolean",
  "ultimoAcceso": "Instant"
}
```

**solicitudes**
```json
{
  "_id": "ObjectId",
  "estudianteId": "String (ref User)",
  "periodoId": "String (ref Periodo)",
  "tipo": "Enum SolicitudTipo",
  "estado": "Enum [PENDIENTE, EN_REVISION, APROBADA, RECHAZADA]",
  "prioridad": "Integer",
  "fechaSolicitud": "Instant",
  "historial": [
    {
      "estadoAnterior": "SolicitudEstado",
      "estadoNuevo": "SolicitudEstado",
      "fecha": "Instant",
      "comentario": "String"
    }
  ]
}
```

**materias**
```json
{
  "_id": "ObjectId",
  "codigo": "String (unique)",
  "nombre": "String",
  "creditos": "Integer",
  "facultadId": "String (ref Facultad)"
}
```

**periodos**
```json
{
  "_id": "ObjectId",
  "nombre": "String",
  "fechaInicio": "LocalDate",
  "fechaFin": "LocalDate",
  "activo": "Boolean",
  "configuracion": {
    "diasMaxRespuesta": 5
  }
}
```

**conflicts**
```json
{
  "_id": "ObjectId",
  "tipo": "String",
  "descripcion": "String",
  "solicitudId": "String (ref Solicitud)",
  "detectadoEn": "Instant",
  "resuelto": "Boolean"
}
```

---

## 4. DECISIONES ARQUITECTÓNICAS

### 4.1 ¿Por qué Cliente-Servidor?
**Decisión:** Separar frontend y backend

**Razones:**
- ✅ Separación de responsabilidades (SRP)
- ✅ Escalabilidad independiente de cada capa
- ✅ Permite múltiples clientes (web actual, móvil futuro)
- ✅ Equipos pueden trabajar en paralelo
- ✅ Testing más sencillo con mocks

### 4.2 ¿Por qué MongoDB (NoSQL)?
**Decisión:** Base de datos documental

**Razones:**
- ✅ Flexibilidad en el esquema de datos (horarios complejos)
- ✅ Mejor rendimiento para lecturas frecuentes
- ✅ Estructura de solicitudes con historial se adapta bien a documentos
- ✅ MongoDB Atlas ofrece gestión automática (backups, replicación)
- ✅ Escalamiento horizontal con sharding

**Trade-offs aceptados:**
- ⚠️ No tiene transacciones ACID complejas (mitigado con validaciones en servicio)
- ⚠️ Relaciones deben manejarse manualmente (usamos referencias por ID)

### 4.3 ¿Por qué Spring Boot?
**Decisión:** Framework para backend

**Razones:**
- ✅ Inyección de dependencias integrada (@Autowired, @RequiredArgsConstructor)
- ✅ Spring Data MongoDB facilita acceso a datos
- ✅ Spring Security maduro y robusto para JWT
- ✅ Configuración por convención (menos boilerplate)
- ✅ Ecosistema de testing (JUnit, Mockito, TestContainers)
- ✅ Actuator para health checks y métricas

### 4.4 ¿Por qué React?
**Decisión:** Librería para frontend

**Razones:**
- ✅ Componentes reutilizables (Dashboard, Forms, Tables)
- ✅ Virtual DOM para rendimiento
- ✅ Hooks para manejo de estado
- ✅ Gran ecosistema (React Router, Axios, Lucide React)
- ✅ Curva de aprendizaje manejable

### 4.5 ¿Por qué JWT para autenticación?
**Decisión:** JSON Web Tokens sobre sesiones tradicionales

**Razones:**
- ✅ Stateless (no requiere almacenar sesiones en servidor)
- ✅ Escalable horizontalmente
- ✅ Funciona bien con arquitectura Cliente-Servidor
- ✅ Puede incluir claims (roles, permisos)
- ✅ Estándar de industria (RFC 7519)

**Implementación:**
- Algoritmo: HS256 (HMAC con SHA-256)
- Expiración: 60 minutos (configurable)
- Secret: Mínimo 256 bits (variable de entorno)
- Estructura: Header.Payload.Signature

### 4.6 ¿Por qué Azure App Service?
**Decisión:** Plataforma de hosting

**Razones:**
- ✅ PaaS (Platform as a Service) - menos gestión de infraestructura
- ✅ CI/CD integrado con GitHub Actions
- ✅ Escalamiento automático basado en métricas
- ✅ Soporte nativo para Spring Boot JAR
- ✅ SSL/TLS automático
- ✅ Créditos educativos disponibles

**URL de producción:**
`https://sistema-horarios-fyf5a2bkfggjc8hs.canadacentral-01.azurewebsites.net`

### 4.7 ¿Por qué separar Interfaces y ServiceImpl?
**Decisión:** Patrón Interface-Implementation para servicios

**Razones:**
- ✅ Facilita testing con mocks/stubs
- ✅ Permite múltiples implementaciones futuras
- ✅ Cumple Dependency Inversion Principle (SOLID)
- ✅ Documentación clara de contratos en interfaces

**Ejemplo:**
```java
// Interface define contrato
public interface SolicitudService {
    SolicitudResponse crearSolicitud(SolicitudRequest request);
}

// Implementación con lógica de negocio
@Service
public class SolicitudServiceImpl implements SolicitudService {
    // Implementación real
}
```

---

## 5. VISTAS ARQUITECTÓNICAS

### 5.1 Vista de Despliegue

**Ambientes:**

1. **Desarrollo Local (Development)**
   - Frontend: `http://localhost:3000` (React Dev Server)
   - Backend: `http://localhost:8081` (Spring Boot)
   - Base de datos: MongoDB Atlas (cluster compartido)
   - Variables: Archivo `.env` local

2. **Producción (Production)**
   - Frontend: Vercel (pendiente despliegue)
   - Backend: Azure App Service - Canada Central
     - URL: `https://sistema-horarios-fyf5a2bkfggjc8hs.canadacentral-01.azurewebsites.net`
   - Base de datos: MongoDB Atlas (cluster producción)
   - Variables: Azure App Service Configuration / GitHub Secrets

**Infraestructura actual:**
```
GitHub Repositories
├── SIRHA-sala3-DOSW-BackEnd
│   ├── Branch: main (estable)
│   └── Branch: develop (desarrollo activo)
│       └── GitHub Actions CI/CD
│           ├── ci.yml (Integración Continua)
│           └── cd.yml (Despliegue Continuo)
│
└── SIRHA-sala3-DOSW-FrontEnd
    ├── Branch: main
    ├── Branch: develop
    └── Branches feature/* (funcionalidades)
    
Azure Cloud (Canada Central Region)
└── App Service: sistema-horarios-fyf5a2bkfggjc8hs
    ├── Runtime: Java 17 (Temurin JDK)
    ├── Deploy: Artifact JAR desde GitHub
    └── Configuration: Secrets desde Azure

MongoDB Atlas
└── Cluster: sirha.qtoisgb.mongodb.net
    ├── Database: SIRHA
    ├── Collections: users, solicitudes, materias, periodos, conflicts, etc.
    └── Access: Whitelisted IPs + Connection String
```

### 5.2 Vista de Componentes

**Backend - Spring Boot:**
```
@SpringBootApplication
└── SirhaApplication
    │
    ├── @Configuration
    │   ├── AppConfig (Clock, ObjectMapper)
    │   ├── SecurityConfig (FilterChain, AuthenticationManager)
    │   ├── OpenApiConfig (Swagger)
    │   └── WebConfig (CORS)
    │
    ├── @RestController
    │   ├── AuthController
    │   ├── SolicitudController
    │   ├── MateriaController
    │   ├── PeriodoController
    │   └── ConflictController
    │
    ├── @Service
    │   ├── SolicitudServiceImpl
    │   ├── MateriaServiceImpl
    │   ├── PeriodoServiceImpl
    │   ├── ConflictDetectionServiceImpl
    │   └── JwtTokenService
    │
    ├── @Repository (MongoRepository)
    │   ├── SolicitudRepository
    │   ├── MateriaRepository
    │   ├── UserRepository
    │   └── ...
    │
    ├── @Component
    │   ├── JwtAuthFilter (OncePerRequestFilter)
    │   ├── InitialAdminLoader (CommandLineRunner)
    │   └── MongoConnectionTester (CommandLineRunner)
    │
    └── @ControllerAdvice
        └── GlobalExceptionHandler
```

**Frontend - React:**
```
React Application
├── components/
│   ├── Student/
│   │   ├── StudentDashboard
│   │   ├── AcademicRecords
│   │   ├── ClassSchedule
│   │   └── Messages
│   ├── Professor/
│   │   ├── ProfessorDashboard
│   │   ├── ClassesRecords
│   │   └── Schedule
│   ├── Administrative/
│   │   ├── AdministrativeDashboard
│   │   ├── Registration
│   │   └── RequestReviews
│   ├── StudentLogin
│   ├── AdminLogin
│   └── RoleSelection
│
├── services/
│   └── api.js (Axios HTTP client)
│
└── App.js (React Router)
```

### 5.3 Vista de Seguridad

**Flujo de autenticación:**
```
1. Usuario → POST /api/auth/login {email, password}
2. AuthController → AuthenticationManager.authenticate()
3. Spring Security → UserDetailsService.loadUserByUsername()
4. UserRepository → MongoDB query findByEmail()
5. PasswordEncoder.matches() → BCrypt verification
6. JwtTokenService.generateToken() → JWT creation
7. Usuario ← AuthResponse {token, expiresAt, userInfo}

Subsecuentes requests:
1. Usuario → GET /api/solicitudes + Header: Authorization: Bearer {token}
2. JwtAuthFilter.doFilterInternal()
3. JwtTokenService.isTokenValid()
4. SecurityContext.setAuthentication()
5. Controller method execution
6. Usuario ← Response data
```

**Endpoints de seguridad:**
- **Públicos** (sin autenticación):
  - `/api/auth/login`
  - `/swagger-ui/**`
  - `/v3/api-docs/**`
  - `/actuator/health`
  
- **Protegidos** (requieren JWT):
  - `/api/solicitudes/**`
  - `/api/materias/**`
  - `/api/periodos/**`
  - `/api/conflicts/**`

---

## 6. PATRONES DE DISEÑO UTILIZADOS

### 6.1 Backend Patterns

**1. Repository Pattern**
- Abstrae acceso a datos
- Spring Data MongoDB genera implementaciones automáticamente
```java
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    List<Solicitud> findByEstudianteIdOrderByFechaSolicitudDesc(String id);
}
```

**2. Service Layer Pattern**
- Encapsula lógica de negocio
- Transaccionalidad y validaciones
```java
@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {
    private final SolicitudRepository solicitudRepository;
    // Lógica de negocio
}
```

**3. DTO (Data Transfer Object)**
- Separa modelo de dominio de API
- Evita exponer detalles internos
- Mappers para conversiones: `SolicitudMapper`, `MateriaMapper`, etc.

**4. Dependency Injection (IoC)**
- Spring gestiona ciclo de vida de beans
- Constructor injection con `@RequiredArgsConstructor` (Lombok)
- Facilita testing con mocks

**5. Strategy Pattern**
- `ConflictDetectionService`: Diferentes estrategias de detección
- Permite agregar nuevos tipos de conflictos sin modificar código existente

**6. Builder Pattern (indirecto)**
- Lombok `@Builder` en DTOs
- Construcción fluida de objetos complejos

**7. Filter Chain Pattern**
- `JwtAuthFilter` extiende `OncePerRequestFilter`
- Cadena de filtros de Spring Security

### 6.2 Frontend Patterns

**1. Component Pattern**
- Componentes reutilizables (`Dashboard`, `Form`, `Table`)
- Single Responsibility Principle

**2. Container/Presentational**
- Contenedores manejan lógica y estado
- Presentacionales solo renderizar UI

**3. Custom Hooks**
- Reutilización de lógica de estado
- Encapsulación de comportamiento

---

## 7. SEGURIDAD

### 7.1 Autenticación y Autorización

**Implementado:**
- ✅ Spring Security con JWT
- ✅ Autenticación basada en tokens
- ✅ Passwords hasheados con BCrypt (strength: 10)
- ✅ Roles: `ESTUDIANTE`, `PROFESOR`, `COORDINADOR`, `ADMIN`
- ✅ `@PreAuthorize` para control de acceso por rol (método por método)

**Configuración JWT:**
```yaml
sirha:
  security:
    jwt:
      issuer: sirha
      expiration-minutes: 60
      secret: ${JWT_SECRET:change-me}
```

**Tokens JWT contienen:**
- Subject: Email del usuario
- Issued At: Timestamp de emisión
- Expiration: Timestamp de expiración
- Issuer: "sirha"

### 7.2 Comunicación Segura

**SSL/HTTPS:**
- ✅ Azure App Service proporciona certificado SSL automático
- ✅ URL con HTTPS: `https://sistema-horarios-fyf5a2bkfggjc8hs.canadacentral-01.azurewebsites.net`
- ✅ Redirect HTTP → HTTPS automático en Azure

**CORS:**
- Configurado en `WebConfig.java`
- Permite requests desde frontend (localhost:3000 en dev, Vercel en prod)
- Headers permitidos: `Authorization`, `Content-Type`

### 7.3 Protección de Datos Sensibles

**Variables de entorno:**
- ✅ Archivo `.env` para desarrollo local (excluido de Git)
- ✅ `DotenvApplicationContextInitializer` carga variables al inicio
- ✅ GitHub Secrets para CI/CD
- ✅ Azure App Service Configuration para producción

**Secrets gestionados:**
- `MONGODB_URI`: Connection string de MongoDB Atlas
- `JWT_SECRET`: Clave secreta para firmar tokens (mínimo 256 bits)
- `AZURE_WEBAPP_PUBLISH_PROFILE`: Credenciales de despliegue

**Protección de passwords:**
- BCrypt con factor de costo 10
- Salt único por password
- Nunca se almacenan en texto plano

### 7.4 Validaciones y Sanitización

**Validaciones implementadas:**
- `@Valid` en controllers para validar DTOs
- Jakarta Bean Validation (`@NotNull`, `@Email`, `@Size`)
- Validaciones de negocio en capa de servicio
- `GlobalExceptionHandler` para manejo centralizado de errores

---

## 8. ESCALABILIDAD Y RENDIMIENTO

### 8.1 Estrategias de Escalabilidad

**Horizontal (Scale Out):**
- Azure App Service permite agregar instancias fácilmente
- MongoDB Atlas soporta sharding automático
- Backend stateless (JWT permite balanceo de carga sin sticky sessions)

**Vertical (Scale Up):**
- Upgrade del plan de Azure App Service (CPU/RAM)
- Upgrade del tier de MongoDB Atlas (M10, M20, M30...)

### 8.2 Optimizaciones Implementadas

**Backend:**
- ✅ Índices en MongoDB para consultas frecuentes:
  - `users.email` (unique)
  - `solicitudes.estudianteId`
  - `solicitudes.estado`
  - `materias.codigo` (unique)
- ✅ Queries optimizadas en repositories
- ✅ Paginación en endpoints que retornan listas (planificado)
- ✅ Projection de campos en queries MongoDB (solo campos necesarios)

---

## 9. TESTING Y CALIDAD

### 9.1 Estrategia de Testing

**Backend:**
- ✅ **Pruebas Unitarias:** JUnit 5 + Mockito
  - Tests para servicios con mocks de repositories
  - Tests de mappers
  - Tests de utilidades
- ✅ **Cobertura de Código:** JaCoCo
  - Reporte en `target/site/jacoco/index.html`
  - Umbral mínimo configurado en `pom.xml`
- ✅ **CI/CD con GitHub Actions:**
  - Pipeline CI ejecuta tests en cada push/PR a `develop`
  - Fallos bloquean merge
- ✅ **Reportes a Codecov:** Análisis de cobertura automático

### 9.2 CI/CD Pipeline

**Integración Continua (ci.yml):**
```yaml
Trigger: Push o Pull Request a develop
Pasos:
  1. Checkout código (fetch-depth: 0 para SonarQube)
  2. Setup Java 17 (Temurin JDK)
  3. Cachear dependencias Maven (~/.m2)
  4. Ejecutar: mvn verify jacoco:report
  5. Publicar reporte de tests (dorny/test-reporter)
  6. Subir cobertura a Codecov
  7. Guardar artifacts (JAR + reports)
```

**Despliegue Continuo (cd.yml):**
```yaml
Trigger: Push a develop (o workflow_dispatch manual)
Pasos:
  1. Checkout código
  2. Setup Java 17
  3. Build: mvn package -DskipTests
  4. Verificar conexión a MongoDB (mongosh ping)
  5. Deploy a Azure Web App
     - App Name: sistema-horarios-fyf5a2bkfggjc8hs
     - Método: Publish Profile
     - Package: target/*.jar
```

**Secrets requeridos en GitHub:**
- `MONGO_URI`: Para verificación de conexión en CD
- `AZURE_WEBAPP_NAME`: Nombre de la app en Azure
- `AZURE_WEBAPP_PUBLISH_PROFILE`: Credenciales de despliegue

### 9.3 Análisis de Calidad

**Herramientas utilizadas:**
- ✅ JaCoCo: Cobertura de código
- ✅ Codecov: Visualización de cobertura en PRs
- ✅ GitHub Actions Test Reporter: Reportes visuales de tests

---

## 10. CONFIGURACIÓN Y GESTIÓN

### 10.1 Archivos de Configuración

**application.yml** (principal)
```yaml
spring:
  application:
    name: sirha
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017}
      database: ${MONGODB_DATABASE:SIRHA}
  jackson:
    default-property-inclusion: non_null

server:
  port: ${SERVER_PORT:8081}

sirha:
  security:
    jwt:
      issuer: sirha
      expiration-minutes: 60
      secret: ${JWT_SECRET:change-me}
  solicitudes:
    dias-max-respuesta: 5
```

**application-development.yml** (perfil de desarrollo)
```yaml
spring:
  config:
    activate:
      on-profile: development

logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
    org.springframework.data.mongodb.core: DEBUG
    edu.dosw.sirha: DEBUG
```

**application-test.yml** (perfil de testing)
```yaml
spring:
  config:
    activate:
      on-profile: test
  data:
    mongodb:
      uri: mongodb://localhost:27017/sirha-test
      database: sirha-test

sirha:
  security:
    jwt:
      secret: test-secret
```

### 10.2 Variables de Entorno

**Desarrollo local (.env):**
```bash
MONGODB_URI=mongodb+srv://user:pass@sirha.qtoisgb.mongodb.net/
MONGODB_DATABASE=SIRHA
JWT_SECRET=super-secret-key-must-be-at-least-256-bits-long
SERVER_PORT=8081
```

**Producción (Azure App Service Configuration):**
```
MONGODB_URI=mongodb+srv://...
MONGODB_DATABASE=SIRHA
JWT_SECRET=****** (generado seguro)
SPRING_PROFILES_ACTIVE=prod
```

### 10.3 Estrategia de Branches (GitFlow simplificado)

**Branches principales:**
- `main`: Código estable de producción (protegida)
- `develop`: Integración de desarrollo activo

**Branches de soporte:**
- `feature/*`: Nuevas funcionalidades
  - Ejemplo: `feature/StudentDashboard`, `feature/HOTFIX-migracion-REACT`
- `hotfix/*`: Correcciones urgentes en producción (futuro)

**Flujo de trabajo:**
```
1. Crear feature branch desde develop
   git checkout develop
   git checkout -b feature/nueva-funcionalidad

2. Desarrollar y commitear
   git add .
   git commit -m "feat: descripción del cambio"

3. Push y crear Pull Request
   git push origin feature/nueva-funcionalidad
   
4. Review + CI checks → Merge a develop

5. Cuando develop está estable → Merge a main → Deploy
```

**Política de commits:**
- Conventional Commits: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`
- Commits descriptivos en español

---

## 11. MONITOREO Y OBSERVABILIDAD

### 11.1 Logs

**Implementación:**
- Spring Boot Logging (Logback)
- Niveles configurables por paquete
- Formato ISO-8601 para timestamps
- Logs estructurados en producción

**Configuración:**
```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd'T'HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n"
  level:
    root: INFO
    org.springframework.data.mongodb.core: DEBUG  # Queries MongoDB
    edu.dosw.sirha: DEBUG
```

**Tipos de logs:**
- `MongoConnectionTester`: Verificación de conexión al inicio
- Controllers: Requests entrantes (nivel DEBUG)
- Services: Operaciones de negocio y errores
- Security: Autenticación y autorización

### 11.2 Health Checks

**Spring Boot Actuator:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,threaddump,loggers
  endpoint:
    health:
      probes:
        enabled: true
      show-details: when_authorized
```

**Endpoints disponibles:**
- `/actuator/health`: Estado general del sistema
- `/actuator/health/liveness`: Liveness probe (para Kubernetes)
- `/actuator/health/readiness`: Readiness probe
- `/actuator/info`: Información de la aplicación
- `/actuator/metrics`: Métricas de JVM, HTTP, etc.

### 11.3 Métricas (Planificado)

**Futuro con Azure Application Insights:**
- Métricas de uso y rendimiento
- Trazas distribuidas
- Alertas automáticas (errores, latencia, disponibilidad)
- Dashboards personalizados

---

## 12. DEPENDENCIAS Y TECNOLOGÍAS

### 12.1 Backend (pom.xml)

**Spring Boot 3.x:**
```xml
<dependencies>
    <!-- Core Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MongoDB -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    
    <!-- Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- OpenAPI / Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    </dependency>
    
    <!-- Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Dotenv -->
    <dependency>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>dotenv-java</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Build plugins:**
- maven-compiler-plugin
- spring-boot-maven-plugin
- jacoco-maven-plugin (cobertura)
- maven-surefire-plugin (tests)

### 12.2 Frontend

**package.json:**
```json
{
  "dependencies": {
    "react": "^18.x",
    "react-dom": "^18.x",
    "react-router-dom": "^6.x",
    "lucide-react": "^0.x",
    "axios": "^1.x"
  },
  "devDependencies": {
    "react-scripts": "^5.x"
  }
}
```

**Node.js:**
- Versión: 22.20.0
- npm incluido

### 12.3 Infraestructura

**Azure:**
- Azure App Service (PaaS)
- Plan: Basic B1 (o superior)
- Runtime Stack: Java 17 (Temurin JDK)
- Region: Canada Central

**MongoDB:**
- MongoDB Atlas (DBaaS)
- Cluster: M0 (Free Tier) o superior
- Versión: MongoDB 6.0+
- Región: Más cercana a Azure Canada Central

**CI/CD:**
- GitHub Actions
- Workflows en `.github/workflows/`
- Runners: ubuntu-latest

---

## 13. ROADMAP Y MEJORAS FUTURAS

### 13.1 Corto Plazo (S9-S10)

**Prioridad Alta:**
- [ ] Desplegar frontend a Vercel
- [ ] Conectar frontend con backend (variables de entorno)
- [ ] Configurar CORS para dominio de Vercel
- [ ] Pruebas de carga con JMeter (objetivo S10)
- [ ] Completar tests unitarios (cobertura > 80%)

**Prioridad Media:**
- [ ] Implementar paginación en endpoints de listas
- [ ] Agregar filtros avanzados en búsquedas
- [ ] Mejorar manejo de errores con códigos HTTP específicos
- [ ] Documentar todos los endpoints en Swagger

### 13.2 Mediano Plazo (Post-entrega)

**Funcionalidades:**
- [ ] Notificaciones en tiempo real (WebSockets con STOMP)
- [ ] Sistema de notificaciones por email (SendGrid/AWS SES)
- [ ] Reportes en PDF (JasperReports o iText)
- [ ] Dashboard con gráficos (Chart.js en frontend)
- [ ] Exportación de datos a Excel

**Calidad:**
- [ ] Implementar pruebas de integración (TestContainers)
- [ ] Configurar SonarQube para análisis estático
- [ ] Implementar pruebas E2E en frontend (Cypress)
- [ ] Agregar Checkstyle y SpotBugs

**Infraestructura:**
- [ ] Crear Dockerfile para backend
- [ ] Implementar contenedores Docker
- [ ] Configurar Kubernetes (AKS) para orquestación
- [ ] Implementar caché con Redis
- [ ] Configurar Azure Application Insights

### 13.3 Largo Plazo

**Innovación:**
- [ ] Machine Learning para sugerencias inteligentes de horarios
- [ ] Algoritmo de optimización de asignación de salones
- [ ] Sistema de recomendación de materias

**Escalabilidad:**
- [ ] Integración con sistemas externos (ERP académico de la ECI)
- [ ] API pública para integraciones de terceros
- [ ] Multi-tenancy (múltiples instituciones)
- [ ] Aplicación móvil (React Native o Flutter)

**Analytics:**
- [ ] Dashboard analítico para coordinadores
- [ ] Métricas de uso del sistema
- [ ] Predicción de demanda de materias

---

## 14. GLOSARIO

- **API REST:** Application Programming Interface con arquitectura RESTful (transferencia de estado representacional)
- **BCrypt:** Algoritmo de hashing de passwords diseñado para ser lento y resistente a ataques de fuerza bruta
- **CI/CD:** Continuous Integration / Continuous Deployment (integración y despliegue continuos)
- **CORS:** Cross-Origin Resource Sharing (intercambio de recursos entre orígenes cruzados)
- **DTO:** Data Transfer Object (objeto de transferencia de datos entre capas)
- **JWT:** JSON Web Token (token web de formato JSON para autenticación)
- **MVC:** Model-View-Controller (patrón arquitectónico de separación de responsabilidades)
- **NoSQL:** Base de datos no relacional (Not Only SQL)
- **PaaS:** Platform as a Service (plataforma como servicio)
- **POJO:** Plain Old Java Object (objeto Java simple sin dependencias de frameworks)
- **REST:** Representational State Transfer (arquitectura de servicios web)
- **SBX:** Sandbox (ambiente de pruebas aislado)
- **SOLID:** Principios de diseño de software (Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion)
- **SSL/TLS:** Secure Sockets Layer / Transport Layer Security (protocolos de seguridad para comunicación encriptada)

---

## 15. CONCLUSIONES

SIRHA implementa una arquitectura moderna cliente-servidor que separa claramente las responsabilidades entre frontend (React) y backend (Spring Boot), comunicados mediante una API REST segura con JWT. El uso de MongoDB Atlas proporciona flexibilidad en el modelo de datos y facilita la escalabilidad horizontal.

### Fortalezas de la arquitectura:

**Seguridad robusta:**
- Autenticación JWT implementada correctamente
- Spring Security configurado con filtros personalizados
- Passwords hasheados con BCrypt
- HTTPS en todos los endpoints de producción

**Separación de responsabilidades:**
- Controllers solo manejan HTTP
- Services contienen toda la lógica de negocio
- Repositories abstraen el acceso a datos
- DTOs separan la API del modelo de dominio

**Mantenibilidad:**
- Código bien estructurado por paquetes
- Interfaces para servicios facilitan testing
- Mappers centralizados para conversiones
- Documentación Javadoc completa

**DevOps automatizado:**
- CI/CD con GitHub Actions funcional
- Tests ejecutados automáticamente en cada PR
- Despliegue continuo a Azure desde `develop`
- Cobertura de código reportada a Codecov

**Escalabilidad preparada:**
- Backend stateless (ideal para balanceo de carga)
- MongoDB con sharding potencial
- Azure App Service con escalamiento automático
- Arquitectura cloud-native

### Áreas de mejora identificadas:

1. **Testing:** Aumentar cobertura de tests de integración
2. **Frontend:** Completar despliegue a Vercel y conexión con backend
3. **Monitoreo:** Implementar Application Insights para observabilidad
4. **Caché:** Agregar Redis para optimizar consultas frecuentes
5. **Documentación:** Crear guías de usuario y manuales operativos

### Cumplimiento de objetivos:

La arquitectura elegida cumple exitosamente con los requisitos del proyecto SIRHA:
- ✅ **Simplicidad:** Arquitectura clara y comprensible
- ✅ **Escalabilidad:** Preparada para crecer según necesidades
- ✅ **Seguridad:** Autenticación, autorización y HTTPS implementados
- ✅ **Calidad:** Tests automatizados y CI/CD funcional
- ✅ **Documentación:** Swagger para API, Javadoc en código, este documento de arquitectura

El sistema está listo para soportar las operaciones de reasignación de horarios de la Escuela Colombiana de Ingeniería, con una base sólida para evolucionar y adaptarse a futuras necesidades.

---

**Documento preparado por:** Equipo Dockerizados pero libres
**Última actualización:** Octubre 2025  
**Próxima revisión:** Semana 11 (Presentación final)  
**Versión del documento:** 1.0  
**Estado del proyecto:** Semana 8 - Backend desplegado, Frontend en desarrollo
