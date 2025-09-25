package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "solicitudes")
public class Solicitud {
    @Id
    private String id;
    
    @Field("codigo_solicitud")
    private String codigoSolicitud;
    
    private String estado; // PENDIENTE, EN_REVISION, APROBADA, RECHAZADA
    
    @Field("fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    
    private String tipo; // CAMBIO_GRUPO, CAMBIO_MATERIA
    
    private String descripcion;
    
    @Field("estudiante_id")
    private String estudianteId;
    
    @Field("inscripcion_origen_id")
    private String inscripcionOrigenId;
    
    @Field("grupo_destino_id")
    private String grupoDestinoId;
    
    @Field("periodo_id")
    private String periodoId;
    
    private Integer prioridad;
    
    @Field("fecha_limite_respuesta")
    private LocalDateTime fechaLimiteRespuesta;
    
    private List<HistorialSolicitud> historial;
    
    public static class HistorialSolicitud {
        private LocalDateTime fecha;
        private String accion;
        
        @Field("usuario_id")
        private String usuarioId;
        
        private String comentario;
        
        public HistorialSolicitud() {}
        
        // Getters and Setters
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
        
        public String getAccion() { return accion; }
        public void setAccion(String accion) { this.accion = accion; }
        
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
        
        public String getComentario() { return comentario; }
        public void setComentario(String comentario) { this.comentario = comentario; }
    }
    
    public Solicitud() {
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCodigoSolicitud() { return codigoSolicitud; }
    public void setCodigoSolicitud(String codigoSolicitud) { this.codigoSolicitud = codigoSolicitud; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getEstudianteId() { return estudianteId; }
    public void setEstudianteId(String estudianteId) { this.estudianteId = estudianteId; }
    
    public String getInscripcionOrigenId() { return inscripcionOrigenId; }
    public void setInscripcionOrigenId(String inscripcionOrigenId) { this.inscripcionOrigenId = inscripcionOrigenId; }
    
    public String getGrupoDestinoId() { return grupoDestinoId; }
    public void setGrupoDestinoId(String grupoDestinoId) { this.grupoDestinoId = grupoDestinoId; }
    
    public String getPeriodoId() { return periodoId; }
    public void setPeriodoId(String periodoId) { this.periodoId = periodoId; }
    
    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }
    
    public LocalDateTime getFechaLimiteRespuesta() { return fechaLimiteRespuesta; }
    public void setFechaLimiteRespuesta(LocalDateTime fechaLimiteRespuesta) { this.fechaLimiteRespuesta = fechaLimiteRespuesta; }
    
    public List<HistorialSolicitud> getHistorial() { return historial; }
    public void setHistorial(List<HistorialSolicitud> historial) { this.historial = historial; }
}