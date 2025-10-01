package edu.dosw.sirha.dto.request;

import edu.dosw.sirha.model.enums.SolicitudEstado;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudEstadoChangeRequest {

    @NotNull
    private SolicitudEstado estado;

    private String observaciones;
}
