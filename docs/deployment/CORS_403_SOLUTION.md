# 🔒 Solución: Error 403 Forbidden en SIRHA Frontend-Backend

## 🚨 Síntomas
- Frontend recibe: **403 Forbidden** al conectar con backend
- Backend en Render está corriendo
- MongoDB conecta correctamente
- JWT authentication parece estar configurada

---

## 🔍 Causas Comunes

### 1. **CORS no está permitiendo el origen del frontend**
- El frontend está en un dominio/puerto diferente al backend
- La variable `ALLOWED_ORIGINS` no incluye el dominio del frontend
- Headers CORS no están siendo enviados correctamente

### 2. **JWT Token no está siendo enviado o es inválido**
- El token no se incluye en el header `Authorization: Bearer <token>`
- El token expiró
- El token está mal formado

### 3. **Punto de acceso bloqueado por @PreAuthorize**
- El endpoint requiere un rol específico (ADMIN, COORDINADOR, etc.)
- El usuario no tiene ese rol

---

## ✅ Solución Paso a Paso

### **Paso 1: Configurar ALLOWED_ORIGINS en Render**

En tu dashboard de Render:

1. Ve a **Environment** (Entorno)
2. Agrega/Edita la variable:
   ```
   ALLOWED_ORIGINS=https://tu-frontend-dominio.com,https://www.tu-frontend-dominio.com,http://localhost:3000,http://localhost:5173
   ```
   
   **Nota**: Reemplaza `tu-frontend-dominio.com` con el dominio **exacto** donde está tu frontend

3. Haz clic en **Save** (Guardar)
4. Espera a que la aplicación se redeploy

---

### **Paso 2: Verificar que el Frontend envía el token JWT correctamente**

En tu frontend (React/Angular/Vue), asegúrate de que:

#### **A) Al guardar el token después de login:**
```javascript
// Después de /api/auth/login exitoso
const response = await fetch('https://tu-backend.render.app/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'admin@sirha.local', password: 'password' })
});

const data = await response.json();
// GUARDAR EL TOKEN
localStorage.setItem('token', data.token); // O sessionStorage
```

#### **B) Al hacer requests subsecuentes, enviar el token:**
```javascript
// En CADA request que requiera autenticación
const token = localStorage.getItem('token');
const response = await fetch('https://tu-backend.render.app/api/materias', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,  // ⚠️ CRÍTICO
    'Content-Type': 'application/json'
  }
});
```

#### **C) Si usas Axios, interceptor para agregar token automáticamente:**
```javascript
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

#### **D) Si usas Fetch, wrapper para agregar token:**
```javascript
const apiCall = async (url, options = {}) => {
  const token = localStorage.getItem('token');
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };
  
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  
  return fetch(url, { ...options, headers });
};

// Uso:
const data = await apiCall('https://tu-backend.render.app/api/materias');
```

---

### **Paso 3: Verificar que el endpoint NO está protegido por @PreAuthorize**

Si el endpoint tiene anotación como esta:
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/{id}")
public ResponseEntity<MateriaResponse> get(@PathVariable String id) { ... }
```

Entonces el usuario DEBE tener ese rol. Si NO lo tiene, obtendrá **403**.

**Solución**: 
- Si es un endpoint de lectura pública, quita `@PreAuthorize`
- Si es sensible, asegúrate de que el usuario logueado tiene el rol correcto

---

### **Paso 4: Test rápido con Swagger**

1. Abre Swagger: `https://tu-backend.render.app/swagger-ui.html`
2. Ve a **Auth → POST /api/auth/login**
3. Ejecuta:
   ```json
   {
     "email": "admin@sirha.local",
     "password": "tu-password"
   }
   ```
4. Copia el `token` de la respuesta
5. Haz clic en el botón **Authorize** (arriba a la derecha en Swagger)
6. Pega: `Bearer <tu-token>`
7. Intenta ejecutar otros endpoints
8. **Si funciona en Swagger, el backend está bien. El problema es el frontend.**

---

### **Paso 5: Validar que CORS funciona desde frontend**

Abre la consola del navegador (F12) y ejecuta:

```javascript
fetch('https://tu-backend.render.app/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include', // Importante para cookies
  body: JSON.stringify({ 
    email: 'admin@sirha.local', 
    password: 'tu-password' 
  })
})
  .then(r => r.json())
  .then(data => console.log('✅ Success:', data))
  .catch(err => console.error('❌ Error:', err));
```

**Si ves error de CORS en consola**, el problema es la configuración `ALLOWED_ORIGINS`.

---

## 🧪 Checklist de Validación

- [ ] `ALLOWED_ORIGINS` en Render incluye el dominio del frontend
- [ ] El frontend envía `Authorization: Bearer <token>` en headers
- [ ] El token no está expirado (expiración por defecto: 60 minutos)
- [ ] El usuario tiene el rol requerido por @PreAuthorize (si existe)
- [ ] El endpoint `/api/auth/login` retorna token exitosamente desde Swagger
- [ ] CORS preflight (OPTIONS) retorna 200 OK
- [ ] No hay errores en logs del backend: `docker logs -f sirha-backend`

---

## 🔐 URLs de Referencia

| Servicio | URL |
|----------|-----|
| **Backend** | https://sirha-backend.render.app |
| **Swagger UI** | https://sirha-backend.render.app/swagger-ui.html |
| **Health Check** | https://sirha-backend.render.app/actuator/health |
| **API Docs** | https://sirha-backend.render.app/v3/api-docs |

---

## 📋 Logs para revisar

### En Render:
```bash
# Ver logs de la aplicación
# En Render dashboard → Logs
```

### Busca estos mensajes:
- ✅ `✅ Conexión a MongoDB exitosa` — BD conectada correctamente
- ⚠️ `NO SE CONFIGURARON LAS VARIABLES DE ENTORNO DEL ADMIN` — Admin user no creado
- ❌ `Invalid token` — JWT invalida
- ❌ `Access denied` — Falta rol @PreAuthorize

---

## 🛠️ Soluciones Rápidas

### Si el error persiste:

1. **Redeploy en Render**:
   - Dashboard → Manual Deploy → Deploy latest commit
   - Espera 2-3 minutos a que se reinicie

2. **Limpiar caché del navegador**:
   - F12 → Application → Clear Storage
   - Refresh

3. **Test local primero**:
   - Levanta backend y frontend en localhost
   - Si funciona localmente, el problema es ALLOWED_ORIGINS en producción

4. **Ver logs detallados**:
   - En Render: Logs tab (muestra errores en tiempo real)
   - Busca: `403`, `CORS`, `Forbidden`

---

## 📞 Contacto para soporte

Si aún tienes problemas:
1. Comparte el error exacto de la consola (F12)
2. Comparte la URL del frontend
3. Confirma que `ALLOWED_ORIGINS` está configurada en Render

---

**Última actualización**: 30 de Octubre de 2025
**Autor**: Equipo DOSW - SIRHA
