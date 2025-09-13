package edu.dosw.project.repository;

import edu.dosw.project.model.Horario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.DayOfWeek;
import java.util.List;

public interface HorarioRepository extends MongoRepository<Horario, String> {
    List<Horario> findByMateriaId(String materiaId);
    List<Horario> findByDiaAndAulaId(DayOfWeek dia, String aulaId);
}