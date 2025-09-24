package edu.dosw.project.service.impl;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.mapper.SolicitudMapper;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.User;
import edu.dosw.project.repository.SolicitudRepository;
import edu.dosw.project.repository.UserRepository;
import edu.dosw.project.service.SolicitudService;
import edu.dosw.project.service.ConflictDetectionService;
import edu.dosw.project.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitudMapper mapper;
    private final UserRepository userRepository;
    private final ConflictDetectionService conflictDetectionService;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                SolicitudMapper mapper,
                                UserRepository userRepository,
                                ConflictDetectionService conflictDetectionService) {
        this.solicitudRepository = solicitudRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.conflictDetectionService = conflictDetectionService;
    }

    @Override
    public Solicitud createSolicitud(SolicitudCreateDto dto) {
        // Buscar al estudiante por su ID (que sería obtenido del contexto de seguridad)
        String estudianteId = "CURRENT_USER_ID"; // En producción esto vendría del SecurityContext
        
        User estudiante = userRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        
        // Validar que el usuario tiene rol de ESTUDIANTE
        boolean esEstudiante = estudiante.getRoles().stream()
                .anyMatch(rol -> "ESTUDIANTE".equals(rol.getTipo()) && Boolean.TRUE.equals(rol.getActivo()));
        
        if (!esEstudiante) {
            throw new IllegalStateException("Solo los estudiantes pueden crear solicitudes");
        }

        Solicitud solicitud = mapper.toDocument(dto, estudianteId);
        solicitud = solicitudRepository.save(solicitud);

        // Detectar posibles conflictos
        conflictDetectionService.detectConflictsForSolicitud(solicitud);
        
        return solicitud;
    }

    @Override
    public List<Solicitud> findByStudent(String studentId) {
        return solicitudRepository.findByEstudianteId(studentId);
    }

    @Override
    @Transactional
    public Solicitud approveSolicitud(String solicitudId, String approverId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        
        // Validar que la solicitud esté en estado PENDIENTE
        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden aprobar solicitudes pendientes");
        }
        
        // Actualizar estado
        solicitud.setEstado("APROBADA");
        
        // Agregar entrada al historial
        Solicitud.HistorialSolicitud entrada = new Solicitud.HistorialSolicitud();
        entrada.setFecha(LocalDateTime.now());
        entrada.setAccion("APROBADA");
        entrada.setUsuarioId(approverId);
        entrada.setComentario("Solicitud aprobada por coordinador");
        
        solicitud.getHistorial().add(entrada);
        
        return solicitudRepository.save(solicitud);
    }

    @Override
    @Transactional
    public Solicitud rejectSolicitud(String solicitudId, String approverId, String reason) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        
        // Validar que la solicitud esté en estado PENDIENTE
        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden rechazar solicitudes pendientes");
        }
        
        // Actualizar estado
        solicitud.setEstado("RECHAZADA");
        
        // Agregar entrada al historial
        Solicitud.HistorialSolicitud entrada = new Solicitud.HistorialSolicitud();
        entrada.setFecha(LocalDateTime.now());
        entrada.setAccion("RECHAZADA");
        entrada.setUsuarioId(approverId);
        entrada.setComentario("Solicitud rechazada. Motivo: " + reason);
        
        solicitud.getHistorial().add(entrada);
        
        return solicitudRepository.save(solicitud);
    }
    
    @Override
    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }
    
    @Override
    public Solicitud findById(String id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
    }
    
    @Override
    public List<Solicitud> findByEstado(String estado) {
        return solicitudRepository.findByEstado(estado);
    }
}