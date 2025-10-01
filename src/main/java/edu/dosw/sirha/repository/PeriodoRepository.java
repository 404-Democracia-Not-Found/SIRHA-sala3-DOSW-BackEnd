package edu.dosw.sirha.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.dosw.sirha.model.Periodo;

public interface PeriodoRepository extends MongoRepository<Periodo, String> {
    Optional<Periodo> findByActivoTrue();
    Optional<Periodo> findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(Instant inicio, Instant fin);
}