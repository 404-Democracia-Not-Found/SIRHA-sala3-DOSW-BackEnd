package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Grupo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends MongoRepository<Grupo, String> {
    List<Grupo> findByMateriaIdAndActivoTrue(String materiaId);
    Optional<Grupo> findByCodigoAndPeriodoId(String codigo, String periodoId);
}
