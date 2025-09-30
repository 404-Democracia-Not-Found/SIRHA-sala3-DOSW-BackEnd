package edu.dosw.project.repository;

import edu.dosw.project.model.ProgramaAcademico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramaAcademicoRepository extends MongoRepository<ProgramaAcademico, String> {
    
    /**
     * Busca programas por facultad
     */
    List<ProgramaAcademico> findByFacultadId(String facultadId);
    
    /**
     * Busca programas activos
     */
    List<ProgramaAcademico> findByActivoTrue();
    
    /**
     * Busca programa por código
     */
    Optional<ProgramaAcademico> findByCodigo(String codigo);
    
    /**
     * Busca programas por coordinador
     */
    List<ProgramaAcademico> findByCoordinadorId(String coordinadorId);
}