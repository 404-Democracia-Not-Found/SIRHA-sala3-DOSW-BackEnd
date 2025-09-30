package edu.dosw.project.service;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.dto.request.SolicitudCambioRequest;
import edu.dosw.project.dto.response.SolicitudResponse;
import edu.dosw.project.model.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SolicitudService {
    Solicitud createSolicitud(SolicitudCreateDto dto);
    List<Solicitud> findByStudent(String studentId);
    Solicitud approveSolicitud(String solicitudId, String approverId);
    Solicitud rejectSolicitud(String solicitudId, String approverId, String reason);
    List<Solicitud> findAll();
    Solicitud findById(String id);
    List<Solicitud> findByEstado(String estado);
    
    // Métodos adicionales para SIRHA
    Solicitud crearSolicitudCambio(String estudianteId, SolicitudCambioRequest request);
    Page<SolicitudResponse> findByEstudianteId(String estudianteId, Pageable pageable);
    Optional<SolicitudResponse> findByIdAndEstudianteId(String solicitudId, String estudianteId);
    Page<SolicitudResponse> findHistorialByEstudiante(String estudianteId, Solicitud.EstadoSolicitud estado, String periodoId, Pageable pageable);
    Page<SolicitudResponse> findSolicitudesByDocente(String docenteId, Solicitud.EstadoSolicitud estado, Pageable pageable);
    Optional<SolicitudResponse> findByIdForDocente(String solicitudId, String docenteId);
    Page<SolicitudResponse> findPendientesByPrograma(String programaId, Pageable pageable);
    Page<SolicitudResponse> findByProgramaWithFilters(String programaId, Solicitud.EstadoSolicitud estado, String estudianteId, String materiaId, Pageable pageable);
    Optional<SolicitudResponse> findByIdAndPrograma(String solicitudId, String programaId);
    boolean perteneceAPrograma(String solicitudId, String programaId);
    Solicitud aprobarSolicitud(String solicitudId, String coordinadorId, String comentario);
    Solicitud rechazarSolicitud(String solicitudId, String coordinadorId, String motivo);
    Map<String, Object> analizarConflictos(String solicitudId);
    String generarRecomendacion(String solicitudId);
    Long countByProgramaAndEstado(String programaId, Solicitud.EstadoSolicitud estado);
    Long countByPrograma(String programaId);
    List<Map<String, Object>> getSolicitudesPorMateria(String programaId, int limit);
    List<Map<String, Object>> getTendenciasSemanales(String programaId);
    List<String> getAlertasPrograma(String programaId);
    Map<String, Object> generarReporteGestion(String programaId, String fechaInicio, String fechaFin);
    Map<String, Object> procesarLote(List<String> solicitudIds, String accion, String coordinadorId, String comentario, String programaId);
    void configurarAutoAprobacion(String programaId, Map<String, Object> criterios);
    byte[] exportarSolicitudes(String programaId, Solicitud.EstadoSolicitud estado, String fechaInicio, String fechaFin);
    Map<String, Object> getDashboardGlobal();
    Page<SolicitudResponse> findAllWithAdvancedFilters(Solicitud.EstadoSolicitud estado, String programaId, String estudianteId, String coordinadorId, String fechaInicio, String fechaFin, Pageable pageable);
    
    // Métodos específicos para coordinador
    Page<SolicitudResponse> findSolicitudesPendientes(String programaId, Pageable pageable);
    Page<SolicitudResponse> findSolicitudesConFiltros(Solicitud.EstadoSolicitud estado, String programaId, String codigoEstudiante, Pageable pageable);
    Map<String, Object> getDashboardCoordinador(String programaId, String coordinadorId);
    Map<String, Object> procesarSolicitudesLote(List<String> solicitudIds, String accion, String observaciones, String coordinadorId);
    byte[] exportarSolicitudesExcel(Solicitud.EstadoSolicitud estado, String fechaInicio, String fechaFin, String programaId);
}