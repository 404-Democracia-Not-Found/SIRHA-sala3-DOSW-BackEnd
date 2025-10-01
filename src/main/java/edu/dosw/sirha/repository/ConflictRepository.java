package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Conflict;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConflictRepository extends MongoRepository<Conflict, String> {
    List<Conflict> findByEstudianteId(String estudianteId);
    List<Conflict> findBySolicitudId(String solicitudId);
}