package edu.dosw.project.repository;

import edu.dosw.project.model.Conflicto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConflictoRepository extends MongoRepository<Conflicto, String> {
}