package edu.dosw.sirha.dto.request;

import edu.dosw.sirha.model.enums.SolicitudEstado;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para cambiar el estado de una solicitud.
 * 
 * <p>Usado en {@code PATCH /api/solicitudes/{id}/estado} por coordinadores
 * para aprobar, rechazar o solicitar información adicional.</p>
 * 
 * <p>Estados válidos: EN_REVISION, APROBADA, RECHAZADA, INFORMACION_ADICIONAL.</p>
 * 
 * @see edu.dosw.sirha.controller.SolicitudController#cambiarEstado
 * @see edu.dosw.sirha.model.enums.SolicitudEstado
 */
@Data
public class SolicitudEstadoChangeRequest {

    /**
     * Nuevo estado de la solicitud.
     */
    @NotNull
    private SolicitudEstado estado;

    /**
     * Observaciones o justificación del cambio de estado.
     * 
     * <p>Ejemplo: "Aprobado, cupo disponible", "Rechazado: no cumple prerequisitos".</p>
     */
    private String observaciones;
}
