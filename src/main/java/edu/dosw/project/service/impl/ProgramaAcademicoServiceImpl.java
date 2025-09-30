package edu.dosw.project.service.impl;

import edu.dosw.project.model.ProgramaAcademico;
import edu.dosw.project.repository.ProgramaAcademicoRepository;
import edu.dosw.project.service.ProgramaAcademicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProgramaAcademicoServiceImpl implements ProgramaAcademicoService {

    private static final Logger log = LoggerFactory.getLogger(ProgramaAcademicoServiceImpl.class);
    
    private final ProgramaAcademicoRepository programaAcademicoRepository;

    public ProgramaAcademicoServiceImpl(ProgramaAcademicoRepository programaAcademicoRepository) {
        this.programaAcademicoRepository = programaAcademicoRepository;
    }

    @Override
    public Optional<ProgramaAcademico> findById(String programaId) {
        log.debug("Buscando programa académico con ID: {}", programaId);
        return programaAcademicoRepository.findById(programaId);
    }

    @Override
    public List<ProgramaAcademico> findAll() {
        log.debug("Obteniendo todos los programas académicos");
        return programaAcademicoRepository.findAll();
    }

    @Override
    public ProgramaAcademico createPrograma(ProgramaAcademico programa) {
        log.info("Creando nuevo programa académico: {}", programa.getNombre());
        return programaAcademicoRepository.save(programa);
    }

    @Override
    public Map<String, Object> configurarAutoAprobacion(String programaId, Map<String, Object> configuracion) {
        log.info("Configurando auto-aprobación para programa: {}", programaId);
        
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            Optional<ProgramaAcademico> programaOpt = programaAcademicoRepository.findById(programaId);
            
            if (programaOpt.isEmpty()) {
                resultado.put("success", false);
                resultado.put("message", "Programa académico no encontrado");
                return resultado;
            }

            ProgramaAcademico programa = programaOpt.get();
            
            // Configurar auto-aprobación
            if (configuracion.containsKey("habilitada")) {
                programa.setAutoAprobacionHabilitada((Boolean) configuracion.get("habilitada"));
            }
            
            if (configuracion.containsKey("criterios")) {
                programa.setCriteriosAutoAprobacion((Map<String, Object>) configuracion.get("criterios"));
            }
            
            if (configuracion.containsKey("limiteCapacidad")) {
                programa.setLimiteCapacidadAutoAprobacion((Integer) configuracion.get("limiteCapacidad"));
            }

            programaAcademicoRepository.save(programa);
            
            resultado.put("success", true);
            resultado.put("message", "Configuración de auto-aprobación actualizada correctamente");
            resultado.put("programa", programa);
            
            log.info("Auto-aprobación configurada exitosamente para programa: {}", programaId);
            
        } catch (Exception e) {
            log.error("Error configurando auto-aprobación para programa {}: {}", programaId, e.getMessage());
            resultado.put("success", false);
            resultado.put("message", "Error interno del servidor");
        }
        
        return resultado;
    }
}