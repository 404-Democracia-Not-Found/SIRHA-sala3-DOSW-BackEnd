# 🎓 SIRHA Backend - Sistema Integral de Registro Horario Académico

---

## Descripción del Proyecto

Sistema backend desarrollado en **Spring Boot** para la gestión integral de horarios académicos, solicitudes de cambio y detección de conflictos de programación. El sistema implementa principios SOLID, seguridad JWT, documentación Swagger y una arquitectura escalable basada en roles.

---

## Objetivos Cumplidos

- Endpoints profesionales creados – Eliminación de endpoints de prueba
- Principios SOLID implementados – Arquitectura limpia con separación de responsabilidades
- MongoDB Atlas integrado – Conexión exitosa a la base de datos
- Swagger documentación completa – API profesional documentada
- Autenticación JWT configurada – Seguridad implementada
- Arquitectura basada en roles – Sistema de permisos implementado

---

## Arquitectura del Sistema

### Capas de la Aplicación

- **Domain Layer**: Modelos de dominio (`User`, `Solicitud`, `Horario`, `Materia`, `Conflicto`)
- **Data Access Layer**: Repositorios MongoDB con consultas optimizadas
- **Business Logic Layer**: Servicios con lógica de negocio y validaciones
- **Presentation Layer**: Controladores REST con documentación Swagger
- **Security Layer**: Autenticación JWT y autorización por roles

### Patrones Implementados

- Principios **SOLID**
- Separación de responsabilidades
- Inyección de dependencias
- Manejo centralizado de excepciones


---

## Prerrequisitos

- Java 17 o superior  
- Maven 3.6+  
- MongoDB Atlas o MongoDB local  
- Git  

---

### Colecciones Configuradas

- `usuarios` – Gestión de usuarios con roles múltiples  
- `solicitudes` – Solicitudes de cambio de horario  
- `horarios` – Programación académica  
- `materias` – Catálogo de materias  
- `conflictos` – Registro de conflictos detectados  

### Índices Implementados

- Email único en colección `usuarios`  
- Búsqueda por roles y estados  
- Optimización de consultas frecuentes  

---

## Sistema de Autenticación y Roles

### Roles Implementados

- `ADMIN` – Acceso completo al sistema  
- `DOCENTE` – Gestión de horarios y solicitudes  
- `ESTUDIANTE` – Consulta y solicitudes limitadas  
- `COORDINADOR` – Aprobación de solicitudes  

### Seguridad JWT

- Tokens con expiración configurable  
- Validación de firma (signature)  
- Mecanismo de refresh token  
- Protección contra ataques comunes  

---

## Endpoints de la API

### Autenticación (`/api/auth`)

| Método | Endpoint     | Descripción               |
|--------|--------------|---------------------------|
| POST   | `/login`     | Autenticación de usuarios |
| POST   | `/register`  | Registro de nuevos usuarios |
| POST   | `/refresh`   | Renovación de token       |

### Usuarios (`/api/usuarios`)

| Método | Endpoint             | Descripción                      |
|--------|----------------------|----------------------------------|
| GET    | `/`                  | Listar todos los usuarios (Admin) |
| GET    | `/{id}`              | Obtener usuario por ID          |
| GET    | `/email/{email}`     | Buscar por email                |
| GET    | `/rol/{roleType}`    | Filtrar por tipo de rol         |
| GET    | `/activos`           | Usuarios activos                |
| GET    | `/buscar`            | Búsqueda avanzada               |
| POST   | `/`                  | Crear nuevo usuario             |
| PUT    | `/{id}`              | Actualizar usuario              |
| DELETE | `/{id}`              | Eliminar usuario (Soft Delete)  |

### Solicitudes (`/api/solicitudes`)

| Método | Endpoint                     | Descripción                      |
|--------|------------------------------|----------------------------------|
| GET    | `/`                          | Listar solicitudes con filtros   |
| POST   | `/`                          | Crear nueva solicitud            |
| PUT    | `/{id}/estado`               | Cambiar estado de solicitud      |
| GET    | `/usuario/{userId}`          | Solicitudes por usuario          |

### Sistema (`/api/sistema`)

| Método | Endpoint     | Descripción               |
|--------|--------------|---------------------------|
| GET    | `/salud`     | Health check del sistema  |
| GET    | `/metrics`   | Métricas de la aplicación |

### Documentación

- `GET /swagger-ui.html` – Interfaz Swagger  
- `GET /v3/api-docs` – Especificación OpenAPI  

---

## Seguridad y Validaciones

- Email válido – Formato y unicidad  
- Campos obligatorios – Validación de nulos y vacíos  
- Roles activos – Verificación de permisos  
- Contraseñas encriptadas – BCrypt para seguridad  
- Fechas automáticas – Creación y actualización  

---




