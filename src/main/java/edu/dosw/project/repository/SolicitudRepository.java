package edu.dosw.project.repository;

import edu.dosw.project.model.Solicitud;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    List<Solicitud> findByEstudianteId(String estudianteId);
    List<Solicitud> findByEstado(Solicitud.EstadoSolicitud estado);
    List<Solicitud> findByTipo(String tipo);
    List<Solicitud> findByPeriodoId(String periodoId);
    
    @Query("{'estudianteId': ?0, 'estado': ?1}")
    List<Solicitud> findByEstudianteIdAndEstado(String estudianteId, Solicitud.EstadoSolicitud estado);
    
    @Query("{'fechaSolicitud': {'$gte': ?0, '$lte': ?1}}")
    List<Solicitud> findByFechaSolicitudBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'fechaLimiteRespuesta': {'$lt': ?0}, 'estado': 'PENDIENTE'}")
    List<Solicitud> findSolicitudesVencidas(LocalDateTime fechaActual);
    
    Optional<Solicitud> findByCodigoSolicitud(String codigoSolicitud);
    
    @Query(value = "{}", sort = "{'prioridad': 1}")
    List<Solicitud> findAllOrderByPrioridad();
}