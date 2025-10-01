package edu.dosw.sirha.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.model.enums.SolicitudTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "solicitudes")
public class Solicitud {

    @Id
    private String id;

    @NotBlank
    private String codigoSolicitud;

    @NotNull
    private SolicitudEstado estado;

    @NotNull
    private SolicitudTipo tipo;

    private String descripcion;

    private String observaciones;

    @NotBlank
    private String estudianteId;

    private String inscripcionOrigenId;

    private String grupoDestinoId;

    private String materiaDestinoId;

    private String periodoId;

    private int prioridad;

    @CreatedDate
    @Field("fecha_solicitud")
    private Instant fechaSolicitud;

    @Field("fecha_limite_respuesta")
    private Instant fechaLimiteRespuesta;

    @Field("fecha_actualizacion")
    @LastModifiedDate
    private Instant fechaActualizacion;

    @Builder.Default
    private List<SolicitudHistorialEntry> historial = new ArrayList<>();

    public void agregarEventoHistorial(SolicitudHistorialEntry evento) {
        historial.add(evento);
    }
}