package edu.dosw.sirha.dto.auth;

import java.time.Instant;

/**
 * DTO de respuesta de autenticación exitosa.
 * 
 * <p>Retorna tokens JWT (acceso y refresh) junto con información básica del usuario autenticado.
 * Este DTO proporciona toda la información necesaria para que el cliente maneje la sesión
 * del usuario de forma segura.</p>
 * 
 * <h3>Tokens Incluidos:</h3>
 * <ul>
 *   <li><b>Access Token:</b> Token de corta duración para acceder a recursos protegidos</li>
 *   <li><b>Refresh Token:</b> Token de larga duración para renovar el access token sin reautenticación</li>
 * </ul>
 * 
 * <h3>Uso del Access Token:</h3>
 * <p>El token de acceso debe incluirse en el header {@code Authorization: Bearer <token>}
 * para todas las requests a endpoints protegidos. Ejemplo:</p>
 * <pre>
 * GET /api/estudiantes/perfil
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 * </pre>
 * 
 * <h3>Uso del Refresh Token:</h3>
 * <p>Cuando el access token expire, el cliente debe usar el refresh token para obtener
 * un nuevo access token sin solicitar credenciales al usuario. Esto se hace enviando
 * el refresh token al endpoint {@code /api/auth/refresh}.</p>
 * 
 * <h3>Consideraciones de Seguridad:</h3>
 * <ul>
 *   <li><b>Access Token:</b> Almacenar en memoria (variables JavaScript) o sessionStorage</li>
 *   <li><b>Refresh Token:</b> Almacenar en httpOnly cookies o localStorage con precauciones XSS</li>
 *   <li><b>Expiración:</b> Implementar lógica de renovación automática antes de que expire el access token</li>
 *   <li><b>Logout:</b> Eliminar ambos tokens del almacenamiento del cliente</li>
 * </ul>
 * 
 * <p><b>Ejemplo de respuesta completa:</b></p>
 * <pre>
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzE4NDc0NDAwLCJleHAiOjE3MTg0NzgwMDB9.signature",
 *   "expiresAt": "2024-06-15T18:00:00Z",
 *   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzE4NDc0NDAwLCJleHAiOjE3MTkwNzkyMDB9.signature",
 *   "refreshExpiresAt": "2024-06-22T17:00:00Z",
 *   "user": {
 *     "id": "665d7f9a1234567890abcdef",
 *     "nombre": "Juan Pérez García",
 *     "email": "usuario@mail.escuelaing.edu.co",
 *     "rol": "ESTUDIANTE"
 *   }
 * }
 * </pre>
 * 
 * @param token Token JWT de acceso firmado (válido por tiempo corto, ej: 1 hora)
 * @param expiresAt Fecha y hora UTC de expiración del token de acceso
 * @param refreshToken Token JWT para renovar el acceso (válido por tiempo largo, ej: 7 días)
 * @param refreshExpiresAt Fecha y hora UTC de expiración del refresh token
 * @param user Información básica del usuario autenticado (id, nombre, email, rol)
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see AuthRequest
 * @see edu.dosw.sirha.security.JwtTokenService
 * @see edu.dosw.sirha.controller.AuthController#login(AuthRequest)
 */
public record AuthResponse(
        String token,
        Instant expiresAt,
        String refreshToken,
        Instant refreshExpiresAt,
        UserInfo user
) {
    /**
     * Información básica del usuario autenticado.
     * 
     * <p>Contiene los datos esenciales del usuario que el cliente necesita para
     * personalizar la interfaz y gestionar permisos. Esta información se incluye
     * en la respuesta de autenticación para evitar una segunda petición al servidor.</p>
     * 
     * <h3>Campos Incluidos:</h3>
     * <ul>
     *   <li><b>id:</b> Identificador único del usuario en MongoDB (ObjectId como String)</li>
     *   <li><b>nombre:</b> Nombre completo del usuario para mostrar en la UI</li>
     *   <li><b>email:</b> Email institucional usado como username</li>
     *   <li><b>rol:</b> Rol del usuario que determina sus permisos en el sistema</li>
     * </ul>
     * 
     * <h3>Roles Disponibles:</h3>
     * <ul>
     *   <li><b>ESTUDIANTE:</b> Estudiante de la institución</li>
     *   <li><b>DOCENTE:</b> Profesor o docente</li>
     *   <li><b>COORDINADOR:</b> Coordinador de programa o área</li>
     *   <li><b>DECANATURA:</b> Personal de decanatura</li>
     *   <li><b>ADMIN:</b> Administrador del sistema con acceso completo</li>
     * </ul>
     * 
     * <p><b>Ejemplo de uso en frontend (React/TypeScript):</b></p>
     * <pre>
     * interface UserInfo {
     *   id: string;
     *   nombre: string;
     *   email: string;
     *   rol: 'ESTUDIANTE' | 'DOCENTE' | 'COORDINADOR' | 'DECANATURA' | 'ADMIN';
     * }
     * 
     * // Usar la información del usuario
     * const { user } = authResponse;
     * console.log(`Bienvenido, ${user.nombre}`);
     * 
     * // Mostrar contenido según el rol
     * if (user.rol === 'ADMIN') {
     *   // Mostrar panel de administración
     * }
     * </pre>
     * 
     * @param id ID único del usuario en base de datos MongoDB
     * @param nombre Nombre completo del usuario
     * @param email Email institucional (usado como username para autenticación)
     * @param rol Rol del usuario en el sistema (ESTUDIANTE, DOCENTE, COORDINADOR, DECANATURA, ADMIN)
     * 
     * @see edu.dosw.sirha.model.User
     * @see edu.dosw.sirha.model.Rol
     */
    public record UserInfo(
            String id,
            String nombre,
            String email,
            String rol
    ) {
    }
}