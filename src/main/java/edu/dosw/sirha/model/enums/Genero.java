package edu.dosw.sirha.model.enums;

/**
 * Enumeración que define las opciones de género para usuarios en el sistema SIRHA.
 * 
 * <p>Este campo es opcional y se utiliza únicamente para estadísticas demográficas
 * y reportes institucionales. Se respeta la privacidad del usuario con la opción
 * de no proporcionar esta información.</p>
 * 
 * <h2>Uso de Datos:</h2>
 * <ul>
 *   <li>Reportes demográficos institucionales</li>
 *   <li>Estadísticas de participación por género</li>
 *   <li>Análisis de equidad en acceso a programas</li>
 * </ul>
 * 
 * <p><b>Nota de privacidad:</b> Esta información es confidencial y solo se usa de forma
 * agregada en reportes estadísticos, nunca de forma individual identificable.</p>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see edu.dosw.sirha.model.User
 */
public enum Genero {
    /** Usuario de género femenino */
    FEMENINO,
    
    /** Usuario de género masculino */
    MASCULINO,
    
    /** Usuario que se identifica como no binario */
    NO_BINARIO,
    
    /** Usuario que prefiere no proporcionar información de género */
    PREFIERE_NO_RESPONDER
}
