package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Inscripcion;
import edu.dosw.sirha.model.enums.EstadoInscripcion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InscripcionRepository extends MongoRepository<Inscripcion, String> {
    List<Inscripcion> findByEstudianteIdAndPeriodoId(String estudianteId, String periodoId);
    boolean existsByEstudianteIdAndGrupoIdAndEstado(String estudianteId, String grupoId, EstadoInscripcion estado);
}
