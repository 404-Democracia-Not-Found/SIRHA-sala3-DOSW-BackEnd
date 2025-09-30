package edu.dosw.project.mapper;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.dto.response.SolicitudResponse;
import edu.dosw.project.model.Solicitud;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class SolicitudMapper {
    
    public Solicitud toDocument(SolicitudCreateDto dto, String estudianteId) {
        Solicitud solicitud = new Solicitud();
        
        // Generar código único para la solicitud
        solicitud.setCodigoSolicitud("SOL-" + System.currentTimeMillis());
        
        solicitud.setEstado(Solicitud.EstadoSolicitud.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setTipo(dto.getTipo());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setEstudianteId(estudianteId);
        solicitud.setInscripcionOrigenId(dto.getInscripcionOrigenId());
        solicitud.setGrupoDestinoId(dto.getGrupoDestinoId());
        solicitud.setPeriodoId(dto.getPeriodoId());
        solicitud.setPrioridad(1); // Prioridad por defecto
        
        // Establecer fecha límite de respuesta (30 días por defecto)
        solicitud.setFechaLimiteRespuesta(LocalDateTime.now().plusDays(30));
        
        // Inicializar historial vacío
        solicitud.setHistorial(new ArrayList<>());
        
        // Agregar primera entrada al historial
        Solicitud.HistorialSolicitud entrada = new Solicitud.HistorialSolicitud();
        entrada.setFecha(LocalDateTime.now());
        entrada.setAccion("CREADA");
        entrada.setUsuarioId(estudianteId);
        entrada.setComentario("Solicitud creada por el estudiante");
        
        solicitud.getHistorial().add(entrada);
        
        return solicitud;
    }
    
    /**
     * Convierte una entidad Solicitud a SolicitudResponse
     */
    public SolicitudResponse toResponse(Solicitud solicitud) {
        if (solicitud == null) {
            return null;
        }
        
        SolicitudResponse response = new SolicitudResponse();
        response.setId(solicitud.getId());
        response.setEstudianteId(solicitud.getEstudianteId());
        response.setEstudianteNombre(null); // se puede obtener después
        response.setEstudianteCodigo(null); // se puede obtener después
        response.setMateriaId(solicitud.getMateriaId());
        response.setMateriaNombre(null); // se puede obtener después
        response.setGrupoActualId(solicitud.getInscripcionOrigenId());
        response.setGrupoActualCodigo(null); // se puede obtener después
        response.setGrupoNuevoId(solicitud.getGrupoDestinoId());
        response.setGrupoNuevoCodigo(null); // se puede obtener después
        response.setMotivo(solicitud.getDescripcion());
        response.setEstado(solicitud.getEstado().toString());
        response.setFechaCreacion(solicitud.getFechaSolicitud());
        response.setFechaProcesamiento(solicitud.getFechaProcesamiento());
        response.setCoordinadorId(solicitud.getCoordinadorId());
        response.setCoordinadorNombre(null); // se puede obtener después
        response.setObservaciones(solicitud.getObservaciones());
        response.setConflictos(null); // se puede obtener después
        
        return response;
    }
}