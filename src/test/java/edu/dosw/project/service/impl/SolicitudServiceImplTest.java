package edu.dosw.project.service.impl;

import edu.dosw.project.dto.SolicitudCreateDto;
import edu.dosw.project.mapper.SolicitudMapper;
import edu.dosw.project.model.Solicitud;
import edu.dosw.project.model.Horario;
import edu.dosw.project.repository.SolicitudRepository;
import edu.dosw.project.repository.HorarioRepository;
import edu.dosw.project.service.ConflictDetectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitudServiceImplTest {

    @Mock SolicitudRepository solicitudRepository;
    @Mock HorarioRepository horarioRepository;
    @Mock ConflictDetectionService conflictDetectionService;
    @InjectMocks SolicitudServiceImpl service;
    private SolicitudMapper mapper = new SolicitudMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SolicitudServiceImpl(solicitudRepository, mapper, horarioRepository, conflictDetectionService);
    }

    @Test
    void createSolicitud_whenHorarioFull_thenThrow() {
        Horario h = new Horario();
        h.setId("h1");
        h.setCupos(10);
        h.setInscritos(10); // full

        when(horarioRepository.findById("h1")).thenReturn(Optional.of(h));
        SolicitudCreateDto dto = new SolicitudCreateDto();
        dto.setStudentId("s1");
        dto.setMateriaId("m1");
        dto.setHorarioActualId("ha");
        dto.setHorarioPropuestoId("h1");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.createSolicitud(dto));
        assertEquals("El horario propuesto no tiene cupos.", ex.getMessage());
    }

    @Test
    void createSolicitud_whenOK_thenSaves() {
        Horario h = new Horario();
        h.setId("h2");
        h.setCupos(10);
        h.setInscritos(5);
        when(horarioRepository.findById("h2")).thenReturn(Optional.of(h));
        when(solicitudRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SolicitudCreateDto dto = new SolicitudCreateDto();
        dto.setStudentId("s1");
        dto.setMateriaId("m1");
        dto.setHorarioActualId("ha");
        dto.setHorarioPropuestoId("h2");

        Solicitud s = service.createSolicitud(dto);
        assertNotNull(s);
        assertEquals(Solicitud.Status.PENDING, s.getStatus());
        verify(conflictDetectionService, times(1)).detectConflictsForSolicitud(any());
    }
}