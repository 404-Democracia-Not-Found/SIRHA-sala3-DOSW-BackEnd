package edu.dosw.project.service.impl;

import edu.dosw.project.model.CalendarioAcademico;
import edu.dosw.project.model.PeriodoAcademico;
import edu.dosw.project.repository.PeriodoAcademicoRepository;
import edu.dosw.project.service.CalendarioAcademicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarioAcademicoServiceImpl implements CalendarioAcademicoService {

    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    
    @Override
    public boolean estaDentroPeriodoSolicitudes() {
        return true; // Implementación temporal
    }
    
    // Métodos adicionales para administración
    public void configurarCalendario(CalendarioAcademico calendario) {
        // Implementación temporal
    }
    
    public void configurarPeriodoSolicitudes(boolean habilitado, String fechaInicio, String fechaFin) {
        // Implementación temporal
    }

    @Override
    public Optional<PeriodoAcademico> getPeriodoActivo() {
        return periodoAcademicoRepository.findByActivoTrue();
    }

    @Override
    public List<PeriodoAcademico> getPeriodosVigentes() {
        LocalDate fechaActual = LocalDate.now();
        return periodoAcademicoRepository.findPeriodosVigentes(fechaActual);
    }

    @Override
    public boolean estaEnPeriodoSolicitudes() {
        Optional<PeriodoAcademico> periodoActivo = getPeriodoActivo();
        return periodoActivo.map(PeriodoAcademico::estaEnPeriodoSolicitudes).orElse(false);
    }

    @Override
    public boolean esPeriodoSolicitudesActivo() {
        return estaEnPeriodoSolicitudes();
    }

    @Override
    public Optional<PeriodoAcademico> getPeriodoPorAnioYSemestre(Integer anio, Integer semestre) {
        String codigo = anio + "-" + semestre;
        return periodoAcademicoRepository.findByCodigo(codigo);
    }

    @Override
    public PeriodoAcademico crearPeriodoAcademico(Integer anio, Integer semestre, 
                                                 LocalDate fechaInicio, LocalDate fechaFin) {
        String codigo = anio + "-" + semestre;
        
        PeriodoAcademico periodo = new PeriodoAcademico();
        periodo.setCodigo(codigo);
        periodo.setAnio(anio);
        periodo.setSemestre(semestre);
        periodo.setFechaInicio(fechaInicio);
        periodo.setFechaFin(fechaFin);
        periodo.setActivo(false);
        
        return periodoAcademicoRepository.save(periodo);
    }

    @Override
    public PeriodoAcademico configurarFechasSolicitudes(String periodoId, 
                                                       LocalDate fechaInicio, LocalDate fechaFin) {
        Optional<PeriodoAcademico> periodoOpt = periodoAcademicoRepository.findById(periodoId);
        if (periodoOpt.isPresent()) {
            PeriodoAcademico periodo = periodoOpt.get();
            periodo.setFechaInicioSolicitudes(fechaInicio);
            periodo.setFechaFinSolicitudes(fechaFin);
            return periodoAcademicoRepository.save(periodo);
        }
        return null;
    }

    @Override
    public List<PeriodoAcademico> getPeriodosPorAnio(Integer anio) {
        return periodoAcademicoRepository.findByAnio(anio);
    }

    @Override
    public void inicializarPeriodos2025() {
        // Crear período 2025-1
        if (getPeriodoPorAnioYSemestre(2025, 1).isEmpty()) {
            LocalDate inicioSemestre1 = LocalDate.of(2025, 1, 20);
            LocalDate finSemestre1 = LocalDate.of(2025, 5, 17);
            PeriodoAcademico periodo1 = crearPeriodoAcademico(2025, 1, inicioSemestre1, finSemestre1);
            
            // Configurar fechas de solicitudes para 2025-1 (2 semanas antes del inicio)
            LocalDate inicioSolicitudes1 = inicioSemestre1.minusWeeks(2);
            LocalDate finSolicitudes1 = inicioSemestre1.minusDays(3);
            configurarFechasSolicitudes(periodo1.getId(), inicioSolicitudes1, finSolicitudes1);
        }
        
        // Crear período 2025-2
        if (getPeriodoPorAnioYSemestre(2025, 2).isEmpty()) {
            LocalDate inicioSemestre2 = LocalDate.of(2025, 8, 11);
            LocalDate finSemestre2 = LocalDate.of(2025, 12, 6);
            PeriodoAcademico periodo2 = crearPeriodoAcademico(2025, 2, inicioSemestre2, finSemestre2);
            
            // Configurar fechas de solicitudes para 2025-2 (2 semanas antes del inicio)
            LocalDate inicioSolicitudes2 = inicioSemestre2.minusWeeks(2);
            LocalDate finSolicitudes2 = inicioSemestre2.minusDays(3);
            configurarFechasSolicitudes(periodo2.getId(), inicioSolicitudes2, finSolicitudes2);
        }
    }

    @Override
    public boolean fechaEnPeriodo(LocalDate fecha, String periodoId) {
        Optional<PeriodoAcademico> periodoOpt = periodoAcademicoRepository.findById(periodoId);
        if (periodoOpt.isPresent()) {
            PeriodoAcademico periodo = periodoOpt.get();
            return !fecha.isBefore(periodo.getFechaInicio()) && !fecha.isAfter(periodo.getFechaFin());
        }
        return false;
    }

    @Override
    public LocalDate calcularFechaLimiteRespuesta(int diasHabiles) {
        LocalDate fecha = LocalDate.now();
        int diasContados = 0;
        
        while (diasContados < diasHabiles) {
            fecha = fecha.plusDays(1);
            // Excluir sábados (6) y domingos (7)
            if (fecha.getDayOfWeek().getValue() < 6) {
                diasContados++;
            }
        }
        
        return fecha;
    }

    @Override
    public PeriodoAcademico activarPeriodo(String periodoId) {
        // Desactivar todos los periodos
        List<PeriodoAcademico> periodosActivos = periodoAcademicoRepository.findAllByActivoTrue();
        for (PeriodoAcademico p : periodosActivos) {
            p.setActivo(false);
            periodoAcademicoRepository.save(p);
        }

        // Activar el periodo especificado
        Optional<PeriodoAcademico> periodoOpt = periodoAcademicoRepository.findById(periodoId);
        if (periodoOpt.isPresent()) {
            PeriodoAcademico periodo = periodoOpt.get();
            periodo.setActivo(true);
            return periodoAcademicoRepository.save(periodo);
        }
        return null;
    }
}