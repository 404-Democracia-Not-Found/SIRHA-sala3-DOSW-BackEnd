package edu.dosw.project.repository;

import edu.dosw.project.model.Solicitud;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    List<Solicitud> findByStudentId(String studentId);
    List<Solicitud> findByStatus(Solicitud.Status status);
}