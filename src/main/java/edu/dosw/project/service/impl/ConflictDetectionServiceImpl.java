package edu.dosw.project.service.impl;

import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.Conflicto;
import edu.dosw.project.model.Horario;
import edu.dosw.project.model.Grupo;
import edu.dosw.project.model.Inscripcion;
import edu.dosw.project.repository.HorarioRepository;
import edu.dosw.project.repository.ConflictoRepository;
import edu.dosw.project.repository.GrupoRepository;
import edu.dosw.project.repository.InscripcionRepository;
import edu.dosw.project.repository.SolicitudRepository;
import edu.dosw.project.service.ConflictDetectionService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class ConflictDetectionServiceImpl implements ConflictDetectionService {

    private final HorarioRepository horarioRepository;
    private final ConflictoRepository conflictoRepository;
    private final GrupoRepository grupoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final SolicitudRepository solicitudRepository;

    public ConflictDetectionServiceImpl(HorarioRepository horarioRepository,
                                        ConflictoRepository conflictoRepository,
                                        GrupoRepository grupoRepository,
                                        InscripcionRepository inscripcionRepository,
                                        SolicitudRepository solicitudRepository) {
        this.horarioRepository = horarioRepository;
        this.conflictoRepository = conflictoRepository;
        this.grupoRepository = grupoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public void detectConflictsForSolicitud(Solicitud solicitud) {
        // Para el SIRHA, detectamos conflictos relacionados con el grupo destino
        if (solicitud.getGrupoDestinoId() != null) {
            // Verificar si el grupo destino tiene cupos disponibles
            // (Esto requeriría un servicio adicional para manejar grupos)
            
            // Crear conflicto por ejemplo
            Conflicto c = new Conflicto();
            c.setDescripcion("Verificando disponibilidad en grupo destino: " + solicitud.getGrupoDestinoId());
            c.setDetectedAt(LocalDateTime.now());
            c.setResolved(false);
            c.setInvolucrados(List.of(solicitud.getEstudianteId(), solicitud.getGrupoDestinoId()));
            conflictoRepository.save(c);
        }
    }

    @Override
    public Conflicto detectConflictsForHorario(String horarioId) {
        Horario h = horarioRepository.findById(horarioId).orElse(null);
        if (h == null) return null;
        
        if (h.getInscritos() > h.getCupos()) {
            Conflicto c = new Conflicto();
            c.setDescripcion("Sobrecupo detectado en horario " + horarioId);
            c.setDetectedAt(LocalDateTime.now());
            c.setInvolucrados(List.of(horarioId));
            c.setResolved(false);
            return conflictoRepository.save(c);
        }
        return null;
    }

    @Override
    public Map<String, Object> analizarConflictos(String solicitudId) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            Optional<Solicitud> solicitudOpt = solicitudRepository.findById(solicitudId);
            
            if (solicitudOpt.isEmpty()) {
                resultado.put("success", false);
                resultado.put("message", "Solicitud no encontrada");
                return resultado;
            }
            
            Solicitud solicitud = solicitudOpt.get();
            
            // Detectar conflictos de horario
            List<Conflicto> conflictosHorario = detectarConflictosHorario(
                solicitud.getEstudianteId(), 
                solicitud.getGrupoDestinoId()
            );
            
            // Verificar conflictos específicos
            boolean tieneConflicto = tieneConflictoHorarioEstudiante(
                solicitud.getEstudianteId(), 
                solicitud.getGrupoDestinoId()
            );
            
            resultado.put("success", true);
            resultado.put("tieneConflictos", tieneConflicto);
            resultado.put("conflictosDetallados", conflictosHorario);
            resultado.put("totalConflictos", conflictosHorario.size());
            resultado.put("solicitudId", solicitudId);
            resultado.put("recomendacion", tieneConflicto ? "RECHAZAR" : "APROBAR");
            
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("message", "Error analizando conflictos: " + e.getMessage());
        }
        
        return resultado;
    }

    @Override
    public List<Conflicto> detectarConflictosHorario(String estudianteId, String nuevoGrupoId) {
        List<Conflicto> conflictos = new ArrayList<>();
        
        Optional<Grupo> grupoOpt = grupoRepository.findById(nuevoGrupoId);
        if (grupoOpt.isEmpty()) {
            return conflictos;
        }
        
        Grupo nuevoGrupo = grupoOpt.get();
        
        // Obtener horarios del estudiante actual
        List<Horario> horariosEstudiante = obtenerHorariosEstudiante(estudianteId, nuevoGrupo.getPeriodoAcademicoId());
        
        // Obtener horarios del nuevo grupo
        List<Horario> horariosNuevoGrupo = nuevoGrupo.getHorariosIds().stream()
                .map(horarioRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        
        // Verificar conflictos
        for (Horario horarioEstudiante : horariosEstudiante) {
            for (Horario horarioNuevo : horariosNuevoGrupo) {
                if (tieneSolapamiento(horarioEstudiante, horarioNuevo)) {
                    Conflicto conflicto = new Conflicto();
                    conflicto.setDescripcion(String.format(
                        "Conflicto de horario: %s se solapa con %s", 
                        horarioEstudiante.getId(), horarioNuevo.getId()
                    ));
                    conflicto.setDetectedAt(LocalDateTime.now());
                    conflicto.setResolved(false);
                    conflicto.setInvolucrados(List.of(estudianteId, nuevoGrupoId));
                    conflictos.add(conflicto);
                }
            }
        }
        
        return conflictos;
    }

    @Override
    public boolean tieneConflictoHorarioGrupos(String grupoActualId, String grupoDestinoId) {
        Optional<Grupo> grupoActualOpt = grupoRepository.findById(grupoActualId);
        Optional<Grupo> grupoDestinoOpt = grupoRepository.findById(grupoDestinoId);
        
        if (grupoActualOpt.isEmpty() || grupoDestinoOpt.isEmpty()) {
            return false;
        }
        
        List<Horario> horariosActual = grupoActualOpt.get().getHorariosIds().stream()
                .map(horarioRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
                
        List<Horario> horariosDestino = grupoDestinoOpt.get().getHorariosIds().stream()
                .map(horarioRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        
        for (Horario h1 : horariosActual) {
            for (Horario h2 : horariosDestino) {
                if (tieneSolapamiento(h1, h2)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean puedeAprobarSolicitud(String solicitudId) {
        Optional<Solicitud> solicitudOpt = solicitudRepository.findById(solicitudId);
        if (solicitudOpt.isEmpty()) {
            return false;
        }
        
        Solicitud solicitud = solicitudOpt.get();
        
        // Verificar conflictos de horario
        List<Conflicto> conflictos = detectarConflictosHorario(
            solicitud.getEstudianteId(), 
            solicitud.getGrupoDestinoId()
        );
        
        return conflictos.isEmpty();
    }

    @Override
    public List<Horario> obtenerHorariosEstudiante(String estudianteId, String periodoAcademicoId) {
        List<Inscripcion> inscripciones = inscripcionRepository
                .findByEstudianteIdAndPeriodoAcademicoId(estudianteId, periodoAcademicoId);
        
        List<Horario> horariosEstudiante = new ArrayList<>();
        
        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getActiva()) {
                Optional<Grupo> grupoOpt = grupoRepository.findById(inscripcion.getGrupoId());
                if (grupoOpt.isPresent()) {
                    List<Horario> horariosGrupo = grupoOpt.get().getHorariosIds().stream()
                            .map(horarioRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList());
                    horariosEstudiante.addAll(horariosGrupo);
                }
            }
        }
        
        return horariosEstudiante;
    }

    @Override
    public boolean tieneSolapamiento(Horario horario1, Horario horario2) {
        // Si no son el mismo día, no hay conflicto
        if (!horario1.getDia().equals(horario2.getDia())) {
            return false;
        }
        
        LocalTime inicio1 = horario1.getInicio();
        LocalTime fin1 = horario1.getFin();
        LocalTime inicio2 = horario2.getInicio();
        LocalTime fin2 = horario2.getFin();
        
        // Verificar solapamiento de tiempo
        return !(fin1.isBefore(inicio2) || fin1.equals(inicio2) || 
                 inicio1.isAfter(fin2) || inicio1.equals(fin2));
    }

    @Override
    public List<Grupo> buscarGruposSinConflictos(String estudianteId, String materiaId, String periodoAcademicoId) {
        List<Grupo> gruposDisponibles = grupoRepository
                .findByMateriaIdAndPeriodoAcademicoId(materiaId, periodoAcademicoId);
        
        List<Horario> horariosEstudiante = obtenerHorariosEstudiante(estudianteId, periodoAcademicoId);
        
        return gruposDisponibles.stream()
                .filter(grupo -> {
                    List<Horario> horariosGrupo = grupo.getHorariosIds().stream()
                            .map(horarioRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList());
                    
                    // Verificar que no haya conflictos con ningún horario del estudiante
                    for (Horario horarioEstudiante : horariosEstudiante) {
                        for (Horario horarioGrupo : horariosGrupo) {
                            if (tieneSolapamiento(horarioEstudiante, horarioGrupo)) {
                                return false;
                            }
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean tieneConflictoHorarioEstudiante(String estudianteId, String grupoDestinoId) {
        // Obtener horarios actuales del estudiante
        List<Inscripcion> inscripciones = inscripcionRepository.findByEstudianteId(estudianteId);
        List<String> gruposInscritos = inscripciones.stream()
                .map(Inscripcion::getGrupoId)
                .collect(Collectors.toList());
        
        // Obtener horarios del grupo destino
        Optional<Grupo> grupoDestino = grupoRepository.findById(grupoDestinoId);
        if (grupoDestino.isEmpty()) {
            return true; // Si no existe el grupo, considerar conflicto
        }
        
        List<Horario> horariosDestino = grupoDestino.get().getHorariosIds().stream()
                .map(horarioRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        
        // Verificar conflictos con grupos actuales
        for (String grupoId : gruposInscritos) {
            Optional<Grupo> grupo = grupoRepository.findById(grupoId);
            if (grupo.isPresent()) {
                List<Horario> horariosGrupo = grupo.get().getHorariosIds().stream()
                        .map(horarioRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
                
                for (Horario horarioActual : horariosGrupo) {
                    for (Horario horarioDestino : horariosDestino) {
                        if (tieneSolapamiento(horarioActual, horarioDestino)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean tieneConflictoHorarioGrupos(String grupoActualId, String grupoDestinoId) {
        Optional<Grupo> grupoActual = grupoRepository.findById(grupoActualId);
        Optional<Grupo> grupoDestino = grupoRepository.findById(grupoDestinoId);
        
        if (grupoActual.isEmpty() || grupoDestino.isEmpty()) {
            return true; // Si algún grupo no existe, considerar conflicto
        }
        
        List<Horario> horariosActual = grupoActual.get().getHorariosIds().stream()
                .map(horarioRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
                
        List<Horario> horariosDestino = grupoDestino.get().getHorariosIds().stream()
                .map(horarioRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        
        for (Horario horarioActual : horariosActual) {
            for (Horario horarioDestino : horariosDestino) {
                if (tieneSolapamiento(horarioActual, horarioDestino)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public Map<String, Object> validarConflictosMasivo(List<String> solicitudIds) {
        Map<String, Object> resultado = new HashMap<>();
        Map<String, Object> validaciones = new HashMap<>();
        int totalSolicitudes = solicitudIds.size();
        int conflictos = 0;
        int aptas = 0;
        
        for (String solicitudId : solicitudIds) {
            Map<String, Object> validacionSolicitud = new HashMap<>();
            
            try {
                Optional<Solicitud> solicitudOpt = solicitudRepository.findById(solicitudId);
                
                if (solicitudOpt.isEmpty()) {
                    validacionSolicitud.put("status", "ERROR");
                    validacionSolicitud.put("message", "Solicitud no encontrada");
                    validaciones.put(solicitudId, validacionSolicitud);
                    continue;
                }
                
                Solicitud solicitud = solicitudOpt.get();
                
                // Validar conflictos
                boolean tieneConflicto = tieneConflictoHorarioEstudiante(
                    solicitud.getEstudianteId(), 
                    solicitud.getGrupoDestinoId()
                );
                
                if (tieneConflicto) {
                    validacionSolicitud.put("status", "CONFLICTO");
                    validacionSolicitud.put("message", "Tiene conflictos de horario");
                    conflictos++;
                } else {
                    validacionSolicitud.put("status", "APTA");
                    validacionSolicitud.put("message", "Sin conflictos detectados");
                    aptas++;
                }
                
                validacionSolicitud.put("solicitud", solicitud);
                validaciones.put(solicitudId, validacionSolicitud);
                
            } catch (Exception e) {
                validacionSolicitud.put("status", "ERROR");
                validacionSolicitud.put("message", "Error procesando solicitud: " + e.getMessage());
                validaciones.put(solicitudId, validacionSolicitud);
            }
        }
        
        resultado.put("totalSolicitudes", totalSolicitudes);
        resultado.put("conflictos", conflictos);
        resultado.put("aptas", aptas);
        resultado.put("errores", totalSolicitudes - conflictos - aptas);
        resultado.put("validaciones", validaciones);
        
        return resultado;
    }
}