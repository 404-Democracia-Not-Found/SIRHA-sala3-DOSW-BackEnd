package edu.dosw.project.service.impl;

import edu.dosw.project.model.Grupo;
import edu.dosw.project.repository.GrupoRepository;
import edu.dosw.project.service.CupoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CupoServiceImpl implements CupoService {

    private final GrupoRepository grupoRepository;

    public CupoServiceImpl(GrupoRepository grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    @Override
    public boolean tieneCupoDisponible(String grupoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        return grupoOpt.map(Grupo::tieneCupoDisponible).orElse(false);
    }

    @Override
    public int getCuposDisponibles(String grupoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        return grupoOpt.map(Grupo::getCuposDisponibles).orElse(0);
    }

    @Override
    public boolean incrementarInscritos(String grupoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        if (grupoOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            if (grupo.tieneCupoDisponible()) {
                grupo.setEstudiantesInscritos(grupo.getEstudiantesInscritos() + 1);
                grupoRepository.save(grupo);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean decrementarInscritos(String grupoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        if (grupoOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            if (grupo.getEstudiantesInscritos() > 0) {
                grupo.setEstudiantesInscritos(grupo.getEstudiantesInscritos() - 1);
                grupoRepository.save(grupo);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Grupo> getGruposConCupo(String materiaId, String periodoAcademicoId) {
        return grupoRepository.findGruposConCupoDisponible(materiaId, periodoAcademicoId);
    }

    @Override
    public boolean estaEnCapacidadCritica(String grupoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        return grupoOpt.map(Grupo::estaEnCapacidadCritica).orElse(false);
    }

    @Override
    public List<Grupo> getGruposEnCapacidadCritica() {
        return grupoRepository.findGruposEnCapacidadCritica();
    }

    @Override
    public double calcularPorcentajeOcupacion(String grupoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        return grupoOpt.map(Grupo::getPorcentajeOcupacion).orElse(0.0);
    }

    @Override
    public boolean agregarAListaEspera(String grupoId, String estudianteId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        if (grupoOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            if (grupo.getListaEspera() != null && !grupo.getListaEspera().contains(estudianteId)) {
                grupo.getListaEspera().add(estudianteId);
                grupoRepository.save(grupo);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removerDeListaEspera(String grupoId, String estudianteId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        if (grupoOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            if (grupo.getListaEspera() != null) {
                boolean removed = grupo.getListaEspera().remove(estudianteId);
                if (removed) {
                    grupoRepository.save(grupo);
                }
                return removed;
            }
        }
        return false;
    }

    @Override
    public int getPosicionEnListaEspera(String grupoId, String estudianteId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        if (grupoOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            if (grupo.getListaEspera() != null) {
                int index = grupo.getListaEspera().indexOf(estudianteId);
                return index != -1 ? index + 1 : -1; // 1-indexed
            }
        }
        return -1;
    }
}