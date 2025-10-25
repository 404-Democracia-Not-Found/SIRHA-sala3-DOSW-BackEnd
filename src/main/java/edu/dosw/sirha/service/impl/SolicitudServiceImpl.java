package edu.dosw.sirha.service.impl;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import edu.dosw.sirha.dto.request.SolicitudRequest;
import edu.dosw.sirha.dto.response.SolicitudResponse;
import edu.dosw.sirha.exception.BusinessException;
import edu.dosw.sirha.exception.ResourceNotFoundException;
import edu.dosw.sirha.mapper.SolicitudMapper;
import edu.dosw.sirha.model.Grupo;
import edu.dosw.sirha.model.Periodo;
import edu.dosw.sirha.model.Solicitud;
import edu.dosw.sirha.model.SolicitudHistorialEntry;
import edu.dosw.sirha.model.enums.SolicitudEstado;
import edu.dosw.sirha.repository.GrupoRepository;
import edu.dosw.sirha.repository.PeriodoRepository;
import edu.dosw.sirha.repository.SolicitudRepository;
import edu.dosw.sirha.service.SolicitudService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SolicitudServiceImpl implements SolicitudService {

	private static final DateTimeFormatter CODIGO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
				.withZone(ZoneOffset.UTC);

	private final SolicitudRepository solicitudRepository;
	private final SolicitudMapper solicitudMapper;
	private final GrupoRepository grupoRepository;
	private final PeriodoRepository periodoRepository;
	private final Clock clock;

	@Value("${sirha.solicitudes.dias-max-respuesta:5}")
	private int diasMaxRespuesta;

	@Override
	public SolicitudResponse create(SolicitudRequest request) {
		// Validar que estamos en periodo válido
		validarPeriodoActivo();
		
		// Validar que el grupo destino tenga cupos disponibles
		if (request.getGrupoDestinoId() != null) {
			validarCuposDisponibles(request.getGrupoDestinoId());
		}
		
		Solicitud solicitud = solicitudMapper.toNewEntity(request);
		Instant now = Instant.now(clock);
		solicitud.setCodigoSolicitud(generarCodigo(now));
		solicitud.setFechaSolicitud(now);
		solicitud.setFechaActualizacion(now);
		solicitud.setFechaLimiteRespuesta(now.plus(diasMaxRespuesta, ChronoUnit.DAYS));
		
		// Obtener periodo activo
		Periodo periodoActivo = periodoRepository.findByActivoTrue()
				.orElseThrow(() -> new BusinessException("No hay un período académico activo"));
		solicitud.setPeriodoId(periodoActivo.getId());
		
		agregarEvento(solicitud, "CREADA", request.getObservaciones());
		Solicitud saved = solicitudRepository.save(solicitud);
		return solicitudMapper.toResponse(saved);
	}

	@Override
	public SolicitudResponse update(String id, SolicitudRequest request) {
		Solicitud existente = obtenerPorId(id);
		
		// Solo permitir actualización si está en estado PENDIENTE o INFORMACION_ADICIONAL
		if (existente.getEstado() != SolicitudEstado.PENDIENTE && 
			existente.getEstado() != SolicitudEstado.INFORMACION_ADICIONAL) {
			throw new BusinessException("No se puede actualizar una solicitud en estado " + existente.getEstado());
		}
		
		// Validar cupos si cambió el grupo destino
		if (request.getGrupoDestinoId() != null && 
			!request.getGrupoDestinoId().equals(existente.getGrupoDestinoId())) {
			validarCuposDisponibles(request.getGrupoDestinoId());
		}
		
		solicitudMapper.updateEntity(existente, request);
		Instant now = Instant.now(clock);
		existente.setFechaActualizacion(now);
		agregarEvento(existente, "ACTUALIZADA", request.getObservaciones());
		Solicitud guardada = solicitudRepository.save(existente);
		return solicitudMapper.toResponse(guardada);
	}

	@Override
	public SolicitudResponse changeEstado(String id, SolicitudEstado nuevoEstado, String observaciones) {
		Solicitud solicitud = obtenerPorId(id);
		if (solicitud.getEstado() == nuevoEstado) {
			throw new BusinessException("La solicitud ya se encuentra en estado " + nuevoEstado);
		}
		
		// Validaciones especiales para aprobación
		if (nuevoEstado == SolicitudEstado.APROBADA) {
			validarAprobacion(solicitud);
		}
		
		SolicitudEstado estadoAnterior = solicitud.getEstado();
		solicitud.setEstado(nuevoEstado);
		solicitud.setFechaActualizacion(Instant.now(clock));
		
		// Si se aprueba, actualizar cupos
		if (nuevoEstado == SolicitudEstado.APROBADA && solicitud.getGrupoDestinoId() != null) {
			actualizarCupos(solicitud);
		}
		
		agregarEvento(solicitud, "ESTADO:" + nuevoEstado.name(), 
				observaciones + " (Estado anterior: " + estadoAnterior + ")");
		Solicitud guardada = solicitudRepository.save(solicitud);
		return solicitudMapper.toResponse(guardada);
	}

	@Override
	@Transactional(readOnly = true)
	public SolicitudResponse findById(String id) {
		return solicitudMapper.toResponse(obtenerPorId(id));
	}

	@Override
	public void delete(String id) {
		Solicitud solicitud = obtenerPorId(id);
		// Solo permitir eliminar si está en estado PENDIENTE
		if (solicitud.getEstado() != SolicitudEstado.PENDIENTE) {
			throw new BusinessException("Solo se pueden eliminar solicitudes en estado PENDIENTE");
		}
		solicitudRepository.delete(solicitud);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SolicitudResponse> findAll() {
		return solicitudRepository.findAll().stream()
				.map(solicitudMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SolicitudResponse> findByEstudiante(String estudianteId) {
		return solicitudRepository.findByEstudianteIdOrderByFechaSolicitudDesc(estudianteId).stream()
				.map(solicitudMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SolicitudResponse> findByEstados(List<SolicitudEstado> estados) {
		List<SolicitudEstado> estadosValidos = estados == null || estados.isEmpty() ?
				List.of(SolicitudEstado.values()) : estados;
		return solicitudRepository.findByEstadoInOrderByPrioridadAsc(estadosValidos).stream()
				.map(solicitudMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public long countByEstado(SolicitudEstado estado) {
		return solicitudRepository.countByEstado(estado);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SolicitudResponse> findByPeriodoAndRango(String periodoId, Instant inicio, Instant fin) {
		Instant fechaInicio = inicio != null ? inicio : Instant.now(clock).minus(30, ChronoUnit.DAYS);
		Instant fechaFin = fin != null ? fin : Instant.now(clock);
		return solicitudRepository.findByPeriodoIdAndFechaSolicitudBetween(periodoId, fechaInicio, fechaFin).stream()
				.map(solicitudMapper::toResponse)
				.toList();
	}

	private void validarPeriodoActivo() {
		Periodo periodoActivo = periodoRepository.findByActivoTrue()
				.orElseThrow(() -> new BusinessException("No hay un período académico activo para crear solicitudes"));
		
		Instant now = Instant.now(clock);
		if (now.isAfter(periodoActivo.getFechaLimiteSolicitudes())) {
			throw new BusinessException("El período de solicitudes ha cerrado. Fecha límite: " + 
					periodoActivo.getFechaLimiteSolicitudes());
		}
	}

	private void validarCuposDisponibles(String grupoId) {
		Grupo grupo = grupoRepository.findById(grupoId)
				.orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado con id " + grupoId));
		
		if (!grupo.tieneCuposDisponibles()) {
			throw new BusinessException("El grupo " + grupo.getCodigo() + " no tiene cupos disponibles");
		}
	}

	private void validarAprobacion(Solicitud solicitud) {
		// Verificar que el grupo destino aún tenga cupos
		if (solicitud.getGrupoDestinoId() != null) {
			validarCuposDisponibles(solicitud.getGrupoDestinoId());
		}
		
		// Verificar que estamos dentro del período académico
		validarPeriodoActivo();
	}

	private void actualizarCupos(Solicitud solicitud) {
		// Decrementar cupo del grupo origen si existe
		if (solicitud.getInscripcionOrigenId() != null) {
			// Aquí implementarías la lógica para encontrar el grupo origen y decrementar
		}
		
		// Incrementar cupo del grupo destino
		if (solicitud.getGrupoDestinoId() != null) {
			Grupo grupoDestino = grupoRepository.findById(solicitud.getGrupoDestinoId())
					.orElseThrow(() -> new ResourceNotFoundException("Grupo destino no encontrado"));
			
			if (!grupoDestino.incrementarCupo()) {
				throw new BusinessException("No se pudo asignar cupo en el grupo destino");
			}
			
			grupoRepository.save(grupoDestino);
		}
	}

	private Solicitud obtenerPorId(String id) {
		return solicitudRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con id " + id));
	}

	private void agregarEvento(Solicitud solicitud, String accion, String observaciones) {
		List<SolicitudHistorialEntry> historial = solicitud.getHistorial();
		if (historial == null) {
			historial = new ArrayList<>();
			solicitud.setHistorial(historial);
		}
		String comentario = StringUtils.hasText(observaciones) ? observaciones : null;
		SolicitudHistorialEntry entry = SolicitudHistorialEntry.builder()
				.fecha(Instant.now(clock))
				.accion(accion)
				.usuarioId(null) // TODO: Obtener del contexto de seguridad
				.comentario(comentario)
				.build();
		historial.add(entry);
	}

	private String generarCodigo(Instant timestamp) {
		String timePart = CODIGO_FORMATTER.format(timestamp);
		String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
		return "SOL-" + timePart + "-" + randomPart;
	}
}