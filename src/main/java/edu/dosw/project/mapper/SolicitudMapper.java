package edu.dosw.project.mapper;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.model.Solicitud;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class SolicitudMapper {
    
    public Solicitud toDocument(SolicitudCreateDto dto, String estudianteId) {
        Solicitud solicitud = new Solicitud();
        
        // Generar código único para la solicitud
        solicitud.setCodigoSolicitud("SOL-" + System.currentTimeMillis());
        
        solicitud.setEstado("PENDIENTE");
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
}