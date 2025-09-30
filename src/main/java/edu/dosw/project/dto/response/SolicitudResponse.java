package edu.dosw.project.dto.response;

import edu.dosw.project.model.Inscripcion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la respuesta de solicitudes
 */
public class SolicitudResponse {
    
    private String id;
    private String codigoSolicitud;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private String tipo;
    private String descripcion;
    private String estudianteId;
    private String estudianteNombre;
    private String materiaOrigen;
    private String grupoOrigen;
    private String materiaDestino;
    private String grupoDestino;
    private Integer prioridad;
    private LocalDateTime fechaLimiteRespuesta;
    private List<HistorialResponse> historial;
    private String semaforoAcademico; // VERDE, AZUL, ROJO

    // Clase interna para el historial
    public static class HistorialResponse {
        private LocalDateTime fecha;
        private String accion;
        private String usuarioId;
        private String usuarioNombre;
        private String observaciones;

        // Constructors
        public HistorialResponse() {}

        public HistorialResponse(LocalDateTime fecha, String accion, String usuarioId, String observaciones) {
            this.fecha = fecha;
            this.accion = accion;
            this.usuarioId = usuarioId;
            this.observaciones = observaciones;
        }

        // Getters y Setters
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

        public String getAccion() { return accion; }
        public void setAccion(String accion) { this.accion = accion; }

        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

        public String getUsuarioNombre() { return usuarioNombre; }
        public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }

    // Constructor por defecto
    public SolicitudResponse() {}

    // Método para determinar el semáforo académico basado en estado de inscripción
    public String calcularSemaforoAcademico(Inscripcion.EstadoMateria estado) {
        switch (estado) {
            case APROBADA:
                return "VERDE";
            case EN_CURSO:
                return "AZUL";
            case NO_APROBADA:
                return "ROJO";
            default:
                return "AZUL";
        }
    }

    // Getters y Setters
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

    public String getEstudianteNombre() { return estudianteNombre; }
    public void setEstudianteNombre(String estudianteNombre) { this.estudianteNombre = estudianteNombre; }

    public String getMateriaOrigen() { return materiaOrigen; }
    public void setMateriaOrigen(String materiaOrigen) { this.materiaOrigen = materiaOrigen; }

    public String getGrupoOrigen() { return grupoOrigen; }
    public void setGrupoOrigen(String grupoOrigen) { this.grupoOrigen = grupoOrigen; }

    public String getMateriaDestino() { return materiaDestino; }
    public void setMateriaDestino(String materiaDestino) { this.materiaDestino = materiaDestino; }

    public String getGrupoDestino() { return grupoDestino; }
    public void setGrupoDestino(String grupoDestino) { this.grupoDestino = grupoDestino; }

    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }

    public LocalDateTime getFechaLimiteRespuesta() { return fechaLimiteRespuesta; }
    public void setFechaLimiteRespuesta(LocalDateTime fechaLimiteRespuesta) { this.fechaLimiteRespuesta = fechaLimiteRespuesta; }

    public List<HistorialResponse> getHistorial() { return historial; }
    public void setHistorial(List<HistorialResponse> historial) { this.historial = historial; }

    public String getSemaforoAcademico() { return semaforoAcademico; }
    public void setSemaforoAcademico(String semaforoAcademico) { this.semaforoAcademico = semaforoAcademico; }
}