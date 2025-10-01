package edu.dosw.sirha.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.enums.SolicitudEstado;

public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    List<Solicitud> findByEstudianteIdOrderByFechaSolicitudDesc(String estudianteId);
    List<Solicitud> findByEstadoInOrderByPrioridadAsc(List<SolicitudEstado> estados);
    long countByEstado(SolicitudEstado estado);
    List<Solicitud> findByPeriodoIdAndFechaSolicitudBetween(String periodoId, Instant inicio, Instant fin);
}