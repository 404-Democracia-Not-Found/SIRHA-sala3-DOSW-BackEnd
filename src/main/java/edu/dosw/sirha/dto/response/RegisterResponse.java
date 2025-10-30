package edu.dosw.sirha.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO para la respuesta exitosa de registro de usuario.
 * 
 * <p>Contiene información del usuario recién creado y un mensaje de confirmación.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2025-10-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    
    /**
     * Indica si la operación fue exitosa.
     */
    private boolean success;
    
    /**
     * Mensaje descriptivo del resultado de la operación.
     */
    private String message;
    
    /**
     * Información del usuario recién creado.
     */
    private UserInfo user;
    
    /**
     * Clase interna que encapsula la información del usuario creado.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        
        /**
         * ID único del usuario en MongoDB.
         */
        private String id;
        
        /**
         * Nombre completo del usuario.
         */
        private String nombre;
        
        /**
         * Email institucional del usuario.
         */
        private String email;
        
        /**
         * Rol asignado al usuario.
         */
        private String rol;
        
        /**
         * Género del usuario (opcional, principalmente para estudiantes).
         */
        private String genero;
        
        /**
         * Indica si la cuenta está activa.
         */
        private boolean activo;
        
        /**
         * Fecha y hora de creación del usuario.
         */
        private Instant creadoEn;
    }
}