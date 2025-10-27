# Guía de Integración Frontend

## Configuración del Entorno

### Variables de Entorno Necesarias
```env
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173  # Ajustar según el dominio de frontend
```

### Endpoints Base
- Desarrollo: `http://localhost:8080`
- Producción: [Agregar URL de producción cuando esté disponible]

### Autenticación
- Tipo: JWT
- Header: `Authorization: Bearer <token>`
- Endpoint de login: `/api/auth/login`
- Endpoint de refresh: `/api/auth/refresh`

### Documentación API
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Manejo de Errores
Los errores siguen este formato:
```json
{
  "timestamp": "2025-10-24T10:00:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/endpoint"
}
```

### Códigos de Estado HTTP
- 200: Éxito
- 201: Creado
- 400: Error de validación
- 401: No autorizado
- 403: Prohibido
- 404: No encontrado
- 500: Error del servidor

## Ejemplos de Integración

### Login
```javascript
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify({ username, password })
  });
  return await response.json();
};
```

### Peticiones Autenticadas
```javascript
const fetchData = async (token) => {
  const response = await fetch('http://localhost:8080/api/resource', {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    credentials: 'include'
  });
  return await response.json();
};
```

## Consejos para el Desarrollo
1. Utiliza axios o fetch con interceptores para manejar tokens
2. Implementa refresh token automático
3. Maneja errores de forma centralizada
4. Usa TypeScript para mejor tipado de respuestas API
5. Considera usar react-query o SWR para cache y manejo de estado

## Contrato mínimo y snippets útiles
Login response (exacto que devuelve backend):

```json
{
  "token": "<access-token>",
  "expiresAt": "2025-10-26T12:34:56Z",
  "userInfo": {
    "id": "507f1f77bcf86cd799439011",
    "nombre": "Juan Pérez",
    "email": "juan@uni.edu",
    "rol": "ESTUDIANTE"
  }
}
```

Error estándar (manejar en frontend):

```json
{
  "timestamp": "2025-10-26T...Z",
  "status": 400,
  "error": "Bad Request",
  "errorCode": "SIRHA-400-002",
  "message": "Descripción legible",
  "path": "/api/...",
  "validationErrors": { "campo": "mensaje" }
}
```

Axios interceptor recomendado (simplificado):

```javascript
// interceptor de respuestas
api.interceptors.response.use(
  res => res,
  err => {
    const status = err.response?.status;
    const body = err.response?.data || {};
    if (status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
      return Promise.reject(err);
    }
    // manejo de validaciones
    if (body.validationErrors) {
      return Promise.reject({ type: 'validation', errors: body.validationErrors });
    }
    return Promise.reject({ status, errorCode: body.errorCode, message: body.message });
  }
);
```

Lista de códigos de error útiles (inicial):
- `SIRHA-404-001` - Recurso no encontrado
- `SIRHA-400-001` - Regla de negocio genérica
- `SIRHA-400-002` - Errores de validación
- `SIRHA-400-003` - Conflicto de horario
- `SIRHA-400-004` - Cupo de grupo lleno
- `SIRHA-400-005` - Periodo académico cerrado
- `SIRHA-401-001` - Autenticación fallida
- `SIRHA-403-001` - Permisos insuficientes
- `SIRHA-500-001` - Error interno del servidor

> Nota: revisa `/swagger-ui.html` para confirmar rutas exactas y nombres de parámetros.