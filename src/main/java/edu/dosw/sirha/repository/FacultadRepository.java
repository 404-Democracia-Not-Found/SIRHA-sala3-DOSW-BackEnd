package edu.dosw.sirha.repository;

import edu.dosw.sirha.model.Facultad;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FacultadRepository extends MongoRepository<Facultad, String> {
}
