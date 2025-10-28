package edu.dosw.sirha.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de registro de nuevos usuarios en SIRHA.
 * 
 * <p>Este objeto encapsula los datos necesarios para crear un nuevo usuario en el sistema,
 * con validaciones específicas según el rol del usuario (Estudiante, Docente, Coordinador).</p>
 * 
 * <h2>Validaciones por Campo:</h2>
 * <ul>
 *   <li><b>nombre:</b> Solo letras, espacios, acentos y guiones. 2-100 caracteres</li>
 *   <li><b>email:</b> Formato válido, dominio institucional según rol</li>
 *   <li><b>password:</b> Mínimo 8 caracteres, debe contener mayúscula, minúscula y número</li>
 *   <li><b>rol:</b> Solo permite: ESTUDIANTE, DOCENTE, COORDINADOR</li>
 *   <li><b>genero:</b> Opcional, solo para ESTUDIANTE (MASCULINO, FEMENINO, OTRO)</li>
 * </ul>
 * 
 * <h2>Validaciones de Dominio de Email:</h2>
 * <ul>
 *   <li>ESTUDIANTE: debe usar @mail.escuelaing.edu.co</li>
 *   <li>DOCENTE/COORDINADOR: deben usar @escuelaing.edu.co</li>
 * </ul>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2025-10-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    /**
     * Nombre completo del usuario.
     * Debe contener solo letras, espacios, acentos del español y guiones.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(
        regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s\\-]+$", 
        message = "El nombre solo puede contener letras, espacios y guiones"
    )
    private String nombre;
    
    /**
     * Email institucional del usuario.
     * Debe ser válido y cumplir con el dominio requerido según el rol.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;
    
    /**
     * Contraseña del usuario en texto plano.
     * Se encriptará con BCrypt antes de almacenar en la base de datos.
     * Debe contener al menos 1 mayúscula, 1 minúscula y 1 número.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$",
        message = "La contraseña debe contener al menos 1 mayúscula, 1 minúscula y 1 número"
    )
    private String password;
    
    /**
     * Rol del usuario en el sistema.
     * Solo se permiten roles de usuarios regulares (no ADMIN).
     * El rol ADMIN debe crearse mediante otro mecanismo seguro.
     */
    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
        regexp = "ESTUDIANTE|DOCENTE|COORDINADOR", 
        message = "Rol inválido. Valores permitidos: ESTUDIANTE, DOCENTE, COORDINADOR"
    )
    private String rol;
    
    /**
     * Género del usuario (opcional, principalmente para estudiantes).
     * Usado para estadísticas demográficas y reportes institucionales.
     */
    @Pattern(
        regexp = "MASCULINO|FEMENINO|OTRO", 
        message = "Género inválido. Valores permitidos: MASCULINO, FEMENINO, OTRO"
    )
    private String genero;
}