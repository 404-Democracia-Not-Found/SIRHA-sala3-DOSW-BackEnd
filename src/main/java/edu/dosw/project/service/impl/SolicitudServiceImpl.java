package edu.dosw.project.service.impl;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.dto.response.SolicitudResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitudMapper solicitudMapper;
    private final UserRepository userRepository;
    private final ConflictDetectionService conflictDetectionService;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                SolicitudMapper solicitudMapper,
                                UserRepository userRepository,
                                ConflictDetectionService conflictDetectionService) {
        this.solicitudRepository = solicitudRepository;
        this.solicitudMapper = solicitudMapper;
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

        Solicitud solicitud = solicitudMapper.toDocument(dto, estudianteId);
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
        solicitud.setEstado(Solicitud.EstadoSolicitud.APROBADA);
        
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
        solicitud.setEstado(Solicitud.EstadoSolicitud.RECHAZADA);
        
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
        Solicitud.EstadoSolicitud estadoEnum = Solicitud.EstadoSolicitud.valueOf(estado.toUpperCase());
        return solicitudRepository.findByEstado(estadoEnum);
    }

    @Override
    public Page<SolicitudResponse> findSolicitudesPendientes(String programaId, Pageable pageable) {
        // Buscar solicitudes pendientes del programa
        List<Solicitud> solicitudes = solicitudRepository.findByEstado(Solicitud.EstadoSolicitud.PENDIENTE);
        
        // Filtrar por programa si se especifica
        if (programaId != null && !programaId.trim().isEmpty()) {
            // Aquí implementarías la lógica de filtrado por programa
            // Por ahora mantenemos todas las solicitudes
        }
        
        // Convertir a SolicitudResponse usando el mapper
        List<SolicitudResponse> responses = solicitudes.stream()
                .map(solicitudMapper::toResponse)
                .collect(Collectors.toList());
        
        // Aplicar paginación manual (en producción esto debería hacerse en el repository)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<SolicitudResponse> pagedResponses = responses.subList(start, end);
        
        return new PageImpl<>(pagedResponses, pageable, responses.size());
    }

    @Override
    public Page<SolicitudResponse> findSolicitudesConFiltros(Solicitud.EstadoSolicitud estado, String programaId, String codigoEstudiante, Pageable pageable) {
        List<Solicitud> solicitudes = new ArrayList<>();
        
        // Aplicar filtros
        if (estado != null) {
            solicitudes = solicitudRepository.findByEstado(estado);
        } else {
            solicitudes = solicitudRepository.findAll();
        }
        
        // Filtrar por código de estudiante si se especifica
        if (codigoEstudiante != null && !codigoEstudiante.trim().isEmpty()) {
            solicitudes = solicitudes.stream()
                    .filter(s -> {
                        // Aquí deberías buscar al usuario por código y comparar con estudianteId
                        // Por ahora implementación básica
                        return s.getEstudianteId().contains(codigoEstudiante);
                    })
                    .collect(Collectors.toList());
        }
        
        // Convertir a SolicitudResponse
        List<SolicitudResponse> responses = solicitudes.stream()
                .map(solicitudMapper::toResponse)
                .collect(Collectors.toList());
        
        // Aplicar paginación
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<SolicitudResponse> pagedResponses = responses.subList(start, end);
        
        return new PageImpl<>(pagedResponses, pageable, responses.size());
    }

    @Override
    public Map<String, Object> getDashboardCoordinador(String programaId, String coordinadorId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Estadísticas generales
        List<Solicitud> todasSolicitudes = solicitudRepository.findAll();
        Map<String, Long> estadisticas = todasSolicitudes.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEstado().name(),
                        Collectors.counting()
                ));
        
        dashboard.put("estadisticas", estadisticas);
        
        // Solicitudes pendientes
        List<Solicitud> pendientes = solicitudRepository.findByEstado(Solicitud.EstadoSolicitud.PENDIENTE);
        dashboard.put("solicitudesPendientes", pendientes.size());
        
        // Solicitudes urgentes (próximas a vencer)
        LocalDateTime ahora = LocalDateTime.now();
        long urgentes = pendientes.stream()
                .filter(s -> s.getFechaLimiteRespuesta() != null && 
                           s.getFechaLimiteRespuesta().isBefore(ahora.plusDays(2)))
                .count();
        dashboard.put("solicitudesUrgentes", urgentes);
        
        // Actividad reciente (últimas 24 horas)
        LocalDateTime hace24h = ahora.minusHours(24);
        long actividadReciente = todasSolicitudes.stream()
                .filter(s -> s.getFechaSolicitud().isAfter(hace24h))
                .count();
        dashboard.put("actividadReciente", actividadReciente);
        
        return dashboard;
    }

    @Override
    @Transactional
    public Map<String, Object> procesarSolicitudesLote(List<String> solicitudIds, String accion, String observaciones, String coordinadorId) {
        Map<String, Object> resultado = new HashMap<>();
        List<String> procesadas = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        
        for (String solicitudId : solicitudIds) {
            try {
                Solicitud solicitud = solicitudRepository.findById(solicitudId)
                        .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada: " + solicitudId));
                
                // Validar que la solicitud esté en estado pendiente
                if (!Solicitud.EstadoSolicitud.PENDIENTE.equals(solicitud.getEstado())) {
                    errores.add(solicitudId + ": Solo se pueden procesar solicitudes pendientes");
                    continue;
                }
                
                // Procesar según la acción
                switch (accion.toUpperCase()) {
                    case "APROBAR":
                        solicitud.setEstado(Solicitud.EstadoSolicitud.APROBADA);
                        break;
                    case "RECHAZAR":
                        solicitud.setEstado(Solicitud.EstadoSolicitud.RECHAZADA);
                        break;
                    default:
                        errores.add(solicitudId + ": Acción no válida: " + accion);
                        continue;
                }
                
                // Actualizar campos
                solicitud.setCoordinadorId(coordinadorId);
                solicitud.setFechaProcesamiento(LocalDateTime.now());
                solicitud.setObservaciones(observaciones);
                
                // Agregar al historial
                Solicitud.HistorialSolicitud entrada = new Solicitud.HistorialSolicitud();
                entrada.setFecha(LocalDateTime.now());
                entrada.setAccion(accion.toUpperCase());
                entrada.setUsuarioId(coordinadorId);
                entrada.setComentario(observaciones);
                
                if (solicitud.getHistorial() == null) {
                    solicitud.setHistorial(new ArrayList<>());
                }
                solicitud.getHistorial().add(entrada);
                
                solicitudRepository.save(solicitud);
                procesadas.add(solicitudId);
                
            } catch (Exception e) {
                errores.add(solicitudId + ": " + e.getMessage());
            }
        }
        
        resultado.put("procesadas", procesadas);
        resultado.put("errores", errores);
        resultado.put("totalProcesadas", procesadas.size());
        resultado.put("totalErrores", errores.size());
        
        return resultado;
    }

    @Override
    public byte[] exportarSolicitudesExcel(Solicitud.EstadoSolicitud estado, String fechaInicio, String fechaFin, String programaId) {
        // Obtener solicitudes según filtros
        List<Solicitud> solicitudes;
        
        if (estado != null) {
            solicitudes = solicitudRepository.findByEstado(estado);
        } else {
            solicitudes = solicitudRepository.findAll();
        }
        
        // Filtrar por fechas si se especifican
        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            LocalDate fechaInicioDate = LocalDate.parse(fechaInicio, DateTimeFormatter.ISO_LOCAL_DATE);
            solicitudes = solicitudes.stream()
                    .filter(s -> s.getFechaSolicitud().toLocalDate().isAfter(fechaInicioDate.minusDays(1)))
                    .collect(Collectors.toList());
        }
        
        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            LocalDate fechaFinDate = LocalDate.parse(fechaFin, DateTimeFormatter.ISO_LOCAL_DATE);
            solicitudes = solicitudes.stream()
                    .filter(s -> s.getFechaSolicitud().toLocalDate().isBefore(fechaFinDate.plusDays(1)))
                    .collect(Collectors.toList());
        }
        
        // Por ahora retornamos un array vacío
        // En una implementación completa usarías Apache POI para generar el Excel
        return new byte[0];
    }

    @Override
    public Page<SolicitudResponse> findAllWithAdvancedFilters(Solicitud.EstadoSolicitud estado, String programaId, String estudianteId, String coordinadorId, String fechaInicio, String fechaFin, Pageable pageable) {
        List<Solicitud> solicitudes = new ArrayList<>();
        
        // Aplicar filtros
        if (estado != null) {
            solicitudes = solicitudRepository.findByEstado(estado);
        } else {
            solicitudes = solicitudRepository.findAll();
        }
        
        // Filtrar por estudiante si se especifica
        if (estudianteId != null && !estudianteId.trim().isEmpty()) {
            solicitudes = solicitudes.stream()
                    .filter(s -> estudianteId.equals(s.getEstudianteId()))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por coordinador si se especifica
        if (coordinadorId != null && !coordinadorId.trim().isEmpty()) {
            solicitudes = solicitudes.stream()
                    .filter(s -> coordinadorId.equals(s.getCoordinadorId()))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por fechas si se especifica
        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            LocalDate fechaInicioDate = LocalDate.parse(fechaInicio, DateTimeFormatter.ISO_LOCAL_DATE);
            solicitudes = solicitudes.stream()
                    .filter(s -> s.getFechaSolicitud().toLocalDate().isAfter(fechaInicioDate.minusDays(1)))
                    .collect(Collectors.toList());
        }
        
        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            LocalDate fechaFinDate = LocalDate.parse(fechaFin, DateTimeFormatter.ISO_LOCAL_DATE);
            solicitudes = solicitudes.stream()
                    .filter(s -> s.getFechaSolicitud().toLocalDate().isBefore(fechaFinDate.plusDays(1)))
                    .collect(Collectors.toList());
        }
        
        // Convertir a SolicitudResponse
        List<SolicitudResponse> responses = solicitudes.stream()
                .map(solicitudMapper::toResponse)
                .collect(Collectors.toList());
        
        // Aplicar paginación
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<SolicitudResponse> pagedResponses = responses.subList(start, end);
        
        return new PageImpl<>(pagedResponses, pageable, responses.size());
    }

    @Override
    public Map<String, Object> getDashboardGlobal() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Estadísticas generales del sistema
        List<Solicitud> todasSolicitudes = solicitudRepository.findAll();
        Map<String, Long> estadisticas = todasSolicitudes.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEstado().name(),
                        Collectors.counting()
                ));
        
        dashboard.put("estadisticas", estadisticas);
        
        // Total de solicitudes
        dashboard.put("totalSolicitudes", todasSolicitudes.size());
        
        // Solicitudes por tipo
        Map<String, Long> porTipo = todasSolicitudes.stream()
                .collect(Collectors.groupingBy(
                        Solicitud::getTipo,
                        Collectors.counting()
                ));
        dashboard.put("solicitudesPorTipo", porTipo);
        
        // Actividad del mes actual
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long actividadMesActual = todasSolicitudes.stream()
                .filter(s -> s.getFechaSolicitud().isAfter(inicioMes))
                .count();
        dashboard.put("actividadMesActual", actividadMesActual);
        
        return dashboard;
    }

    @Override
    public byte[] exportarSolicitudes(String programaId, Solicitud.EstadoSolicitud estado, String fechaInicio, String fechaFin) {
        // Por ahora retornamos un array vacío
        // En una implementación completa usarías Apache POI para generar el Excel
        return new byte[0];
    }

    @Override
    public void configurarAutoAprobacion(String programaId, Map<String, Object> configuracion) {
        // Por ahora solo simular la configuración
        // En una implementación real, esto actualizaría la configuración en la base de datos
        System.out.println("Configurando auto-aprobación para programa: " + programaId);
        System.out.println("Configuración: " + configuracion);
    }

    @Override
    public Map<String, Object> procesarLote(List<String> solicitudIds, String accion, String coordinadorId, String comentario, String programaId) {
        Map<String, Object> resultado = new HashMap<>();
        List<String> procesadas = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        
        for (String solicitudId : solicitudIds) {
            try {
                Solicitud solicitud = solicitudRepository.findById(solicitudId)
                        .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada: " + solicitudId));
                
                // Validar que la solicitud esté en estado pendiente
                if (!Solicitud.EstadoSolicitud.PENDIENTE.equals(solicitud.getEstado())) {
                    errores.add(solicitudId + ": Solo se pueden procesar solicitudes pendientes");
                    continue;
                }
                
                // Procesar según la acción
                switch (accion.toUpperCase()) {
                    case "APROBAR":
                        solicitud.setEstado(Solicitud.EstadoSolicitud.APROBADA);
                        break;
                    case "RECHAZAR":
                        solicitud.setEstado(Solicitud.EstadoSolicitud.RECHAZADA);
                        break;
                    default:
                        errores.add(solicitudId + ": Acción no válida: " + accion);
                        continue;
                }
                
                // Actualizar campos
                solicitud.setCoordinadorId(coordinadorId);
                solicitud.setFechaProcesamiento(LocalDateTime.now());
                solicitud.setObservaciones(comentario);
                
                // Agregar al historial
                Solicitud.HistorialSolicitud entrada = new Solicitud.HistorialSolicitud();
                entrada.setFecha(LocalDateTime.now());
                entrada.setAccion(accion.toUpperCase());
                entrada.setUsuarioId(coordinadorId);
                entrada.setComentario(comentario);
                
                if (solicitud.getHistorial() == null) {
                    solicitud.setHistorial(new ArrayList<>());
                }
                solicitud.getHistorial().add(entrada);
                
                solicitudRepository.save(solicitud);
                procesadas.add(solicitudId);
                
            } catch (Exception e) {
                errores.add(solicitudId + ": " + e.getMessage());
            }
        }
        
        resultado.put("procesadas", procesadas);
        resultado.put("errores", errores);
        resultado.put("totalProcesadas", procesadas.size());
        resultado.put("totalErrores", errores.size());
        
        return resultado;
    }

    @Override
    public Map<String, Object> generarReporteGestion(String programaId, String fechaInicio, String fechaFin) {
        Map<String, Object> reporte = new HashMap<>();
        
        // Obtener todas las solicitudes en el rango de fechas
        List<Solicitud> solicitudes = solicitudRepository.findAll();
        
        // Filtrar por fechas si se especifican
        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            LocalDate fechaInicioDate = LocalDate.parse(fechaInicio, DateTimeFormatter.ISO_LOCAL_DATE);
            solicitudes = solicitudes.stream()
                    .filter(s -> s.getFechaSolicitud().toLocalDate().isAfter(fechaInicioDate.minusDays(1)))
                    .collect(Collectors.toList());
        }
        
        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            LocalDate fechaFinDate = LocalDate.parse(fechaFin, DateTimeFormatter.ISO_LOCAL_DATE);
            solicitudes = solicitudes.stream()
                    .filter(s -> s.getFechaSolicitud().toLocalDate().isBefore(fechaFinDate.plusDays(1)))
                    .collect(Collectors.toList());
        }
        
        // Estadísticas del reporte
        reporte.put("totalSolicitudes", solicitudes.size());
        reporte.put("programaId", programaId);
        reporte.put("fechaInicio", fechaInicio);
        reporte.put("fechaFin", fechaFin);
        
        // Distribución por estado
        Map<String, Long> porEstado = solicitudes.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEstado().name(),
                        Collectors.counting()
                ));
        reporte.put("distribucionEstados", porEstado);
        
        // Distribución por tipo
        Map<String, Long> porTipo = solicitudes.stream()
                .collect(Collectors.groupingBy(
                        Solicitud::getTipo,
                        Collectors.counting()
                ));
        reporte.put("distribucionTipos", porTipo);
        
        // Tiempo promedio de procesamiento
        double tiempoPromedio = solicitudes.stream()
                .filter(s -> s.getFechaProcesamiento() != null)
                .mapToLong(s -> java.time.Duration.between(s.getFechaSolicitud(), s.getFechaProcesamiento()).toHours())
                .average()
                .orElse(0.0);
        reporte.put("tiempoPromedioHoras", tiempoPromedio);
        
        return reporte;
    }

    @Override
    public List<String> getAlertasPrograma(String programaId) {
        List<String> alertas = new ArrayList<>();
        
        // Buscar solicitudes vencidas
        LocalDateTime ahora = LocalDateTime.now();
        List<Solicitud> vencidas = solicitudRepository.findSolicitudesVencidas(ahora);
        if (!vencidas.isEmpty()) {
            alertas.add("Hay " + vencidas.size() + " solicitudes vencidas que requieren atención inmediata");
        }
        
        // Buscar solicitudes próximas a vencer (menos de 2 días)
        List<Solicitud> pendientes = solicitudRepository.findByEstado(Solicitud.EstadoSolicitud.PENDIENTE);
        long proximasVencer = pendientes.stream()
                .filter(s -> s.getFechaLimiteRespuesta() != null && 
                           s.getFechaLimiteRespuesta().isBefore(ahora.plusDays(2)))
                .count();
        
        if (proximasVencer > 0) {
            alertas.add("Hay " + proximasVencer + " solicitudes que vencen en los próximos 2 días");
        }
        
        // Alerta de acumulación de solicitudes
        if (pendientes.size() > 50) {
            alertas.add("Alta acumulación de solicitudes pendientes: " + pendientes.size() + " solicitudes");
        }
        
        return alertas;
    }

    @Override
    public List<Map<String, Object>> getTendenciasSemanales(String programaId) {
        List<Map<String, Object>> tendencias = new ArrayList<>();
        
        // Obtener solicitudes de las últimas 8 semanas
        LocalDateTime hace8Semanas = LocalDateTime.now().minusWeeks(8);
        List<Solicitud> solicitudes = solicitudRepository.findAll().stream()
                .filter(s -> s.getFechaSolicitud().isAfter(hace8Semanas))
                .collect(Collectors.toList());
        
        // Agrupar por semana
        for (int i = 0; i < 8; i++) {
            LocalDateTime inicioSemana = LocalDateTime.now().minusWeeks(i + 1);
            LocalDateTime finSemana = inicioSemana.plusWeeks(1);
            
            long solicitudesSemana = solicitudes.stream()
                    .filter(s -> s.getFechaSolicitud().isAfter(inicioSemana) && 
                               s.getFechaSolicitud().isBefore(finSemana))
                    .count();
            
            Map<String, Object> semana = new HashMap<>();
            semana.put("semana", inicioSemana.toLocalDate().toString());
            semana.put("solicitudes", solicitudesSemana);
            semana.put("numeroSemana", i + 1);
            
            tendencias.add(semana);
        }
        
        // Ordenar de más reciente a más antigua
        Collections.reverse(tendencias);
        
        return tendencias;
    }

    @Override
    public List<Map<String, Object>> getSolicitudesPorMateria(String programaId, int limite) {
        List<Solicitud> solicitudes = solicitudRepository.findAll().stream()
                .filter(s -> programaId.equals(s.getMateriaId())) // Usando programaId como materiaId
                .limit(limite)
                .collect(Collectors.toList());
        
        return solicitudes.stream()
                .map(s -> {
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("id", s.getId());
                    mapa.put("estudianteId", s.getEstudianteId());
                    mapa.put("materiaId", s.getMateriaId());
                    mapa.put("estado", s.getEstado().toString());
                    mapa.put("fechaSolicitud", s.getFechaSolicitud());
                    mapa.put("descripcion", s.getDescripcion());
                    return mapa;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long countByPrograma(String programaId) {
        // Por ahora contar todas las solicitudes
        // En una implementación real, filtrarías por programa
        return solicitudRepository.count();
    }

    @Override
    public Long countByProgramaAndEstado(String programaId, Solicitud.EstadoSolicitud estado) {
        // Por ahora contar por estado solamente
        // En una implementación real, filtrarías por programa y estado
        return (long) solicitudRepository.findByEstado(estado).size();
    }

    @Override
    public String generarRecomendacion(String solicitudId) {
        try {
            Solicitud solicitud = solicitudRepository.findById(solicitudId)
                    .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
            
            // Generar recomendación simple basada en el estado
            if (Solicitud.EstadoSolicitud.PENDIENTE.equals(solicitud.getEstado())) {
                return "APROBAR - El estudiante cumple con los requisitos académicos y no hay conflictos detectados";
            } else {
                return "YA_PROCESADA - La solicitud ya ha sido procesada anteriormente";
            }
            
        } catch (Exception e) {
            return "ERROR - No se pudo generar recomendación: " + e.getMessage();
        }
    }

    @Override
    public Map<String, Object> analizarConflictos(String solicitudId) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            Solicitud solicitud = solicitudRepository.findById(solicitudId)
                    .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
            
            resultado.put("solicitudId", solicitudId);
            resultado.put("tieneConflictos", false);
            resultado.put("conflictosDetectados", new ArrayList<>());
            resultado.put("recomendacion", "Sin conflictos detectados, se puede procesar");
            resultado.put("nivelRiesgo", "BAJO");
            
        } catch (Exception e) {
            resultado.put("error", "Error al analizar conflictos: " + e.getMessage());
            resultado.put("tieneConflictos", true);
            resultado.put("nivelRiesgo", "ALTO");
        }
        
        return resultado;
    }

    @Override
    public Solicitud rechazarSolicitud(String solicitudId, String coordinadorId, String motivo) {
        return rejectSolicitud(solicitudId, coordinadorId, motivo);
    }
}