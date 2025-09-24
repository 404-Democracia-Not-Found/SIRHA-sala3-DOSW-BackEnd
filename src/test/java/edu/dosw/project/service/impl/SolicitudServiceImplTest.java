package edu.dosw.project.service.impl;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.mapper.SolicitudMapper;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.User;
import edu.dosw.project.repository.SolicitudRepository;
import edu.dosw.project.repository.UserRepository;
import edu.dosw.project.service.ConflictDetectionService;
import edu.dosw.project.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitudServiceImplTest {

    @Mock SolicitudRepository solicitudRepository;
    @Mock UserRepository userRepository;
    @Mock ConflictDetectionService conflictDetectionService;
    @InjectMocks SolicitudServiceImpl service;
    private SolicitudMapper mapper = new SolicitudMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SolicitudServiceImpl(solicitudRepository, mapper, userRepository, conflictDetectionService);
    }

    @Test
    void createSolicitud_whenUserNotFound_thenThrow() {
        when(userRepository.findById("CURRENT_USER_ID")).thenReturn(Optional.empty());
        
        SolicitudCreateDto dto = new SolicitudCreateDto();
        dto.setTipo("CAMBIO_GRUPO");
        dto.setDescripcion("Test solicitud");
        dto.setInscripcionOrigenId("inscripcion1");
        dto.setGrupoDestinoId("grupo2");
        dto.setPeriodoId("periodo1");

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
            () -> service.createSolicitud(dto));
        assertEquals("Estudiante no encontrado", ex.getMessage());
    }

    @Test
    void createSolicitud_whenUserIsNotStudent_thenThrow() {
        User usuario = new User();
        usuario.setId("CURRENT_USER_ID");
        
        User.Rol rolProfesor = new User.Rol();
        rolProfesor.setTipo("PROFESOR");
        rolProfesor.setActivo(true);
        usuario.setRoles(List.of(rolProfesor));
        
        when(userRepository.findById("CURRENT_USER_ID")).thenReturn(Optional.of(usuario));
        
        SolicitudCreateDto dto = new SolicitudCreateDto();
        dto.setTipo("CAMBIO_GRUPO");
        dto.setDescripcion("Test solicitud");
        dto.setInscripcionOrigenId("inscripcion1");
        dto.setGrupoDestinoId("grupo2");
        dto.setPeriodoId("periodo1");

        IllegalStateException ex = assertThrows(IllegalStateException.class, 
            () -> service.createSolicitud(dto));
        assertEquals("Solo los estudiantes pueden crear solicitudes", ex.getMessage());
    }

    @Test
    void createSolicitud_whenValidStudent_thenCreates() {
        User estudiante = new User();
        estudiante.setId("CURRENT_USER_ID");
        
        User.Rol rolEstudiante = new User.Rol();
        rolEstudiante.setTipo("ESTUDIANTE");
        rolEstudiante.setActivo(true);
        estudiante.setRoles(List.of(rolEstudiante));
        
        when(userRepository.findById("CURRENT_USER_ID")).thenReturn(Optional.of(estudiante));
        when(solicitudRepository.save(any())).thenAnswer(i -> {
            Solicitud sol = i.getArgument(0);
            sol.setId("sol123");
            return sol;
        });

        SolicitudCreateDto dto = new SolicitudCreateDto();
        dto.setTipo("CAMBIO_GRUPO");
        dto.setDescripcion("Test solicitud");
        dto.setInscripcionOrigenId("inscripcion1");
        dto.setGrupoDestinoId("grupo2");
        dto.setPeriodoId("periodo1");

        Solicitud resultado = service.createSolicitud(dto);
        
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("CAMBIO_GRUPO", resultado.getTipo());
        assertEquals("Test solicitud", resultado.getDescripcion());
        assertEquals("CURRENT_USER_ID", resultado.getEstudianteId());
        assertEquals("inscripcion1", resultado.getInscripcionOrigenId());
        assertEquals("grupo2", resultado.getGrupoDestinoId());
        assertEquals("periodo1", resultado.getPeriodoId());
        assertNotNull(resultado.getFechaSolicitud());
        assertNotNull(resultado.getHistorial());
        assertFalse(resultado.getHistorial().isEmpty());
        
        verify(conflictDetectionService, times(1)).detectConflictsForSolicitud(any());
    }
    
    @Test
    void approveSolicitud_whenFound_thenApproves() {
        Solicitud solicitud = new Solicitud();
        solicitud.setId("sol1");
        solicitud.setEstado("PENDIENTE");
        solicitud.setHistorial(List.of());
        
        when(solicitudRepository.findById("sol1")).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        Solicitud resultado = service.approveSolicitud("sol1", "coordinador1");
        
        assertEquals("APROBADA", resultado.getEstado());
        assertFalse(resultado.getHistorial().isEmpty());
    }
}