package edu.dosw.sirha.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.dosw.sirha.model.Materia;

public interface MateriaRepository extends MongoRepository<Materia, String> {
    List<Materia> findByFacultadIdAndActivoTrue(String facultadId);
    List<Materia> findBySearchTermsContainingIgnoreCase(String term);
}