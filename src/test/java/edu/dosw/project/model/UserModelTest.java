package edu.dosw.project.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Test unitarios para User modelo
 * Aumenta significativamente la cobertura de código
 */
public class UserModelTest {

    private User user;
    
    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void testUserDefaultConstructor() {
        User newUser = new User();
        assertNotNull(newUser.getFechaCreacion());
        assertTrue(newUser.getActivo());
    }

    @Test
    public void testGettersAndSetters() {
        // Test ID
        String id = "user123";
        user.setId(id);
        assertEquals(id, user.getId());

        // Test nombre
        String nombre = "Juan Pérez";
        user.setNombre(nombre);
        assertEquals(nombre, user.getNombre());

        // Test email
        String email = "juan@test.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());

        // Test fecha nacimiento
        LocalDateTime fechaNacimiento = LocalDateTime.of(1990, 1, 1, 0, 0);
        user.setFechaNacimiento(fechaNacimiento);
        assertEquals(fechaNacimiento, user.getFechaNacimiento());

        // Test genero
        String genero = "M";
        user.setGenero(genero);
        assertEquals(genero, user.getGenero());

        // Test pais nacimiento
        String paisNacimiento = "Colombia";
        user.setPaisNacimiento(paisNacimiento);
        assertEquals(paisNacimiento, user.getPaisNacimiento());

        // Test estado civil
        String estadoCivil = "Soltero";
        user.setEstadoCivil(estadoCivil);
        assertEquals(estadoCivil, user.getEstadoCivil());

        // Test password hash
        String passwordHash = "hashed_password";
        user.setPasswordHash(passwordHash);
        assertEquals(passwordHash, user.getPasswordHash());

        // Test fecha creacion
        LocalDateTime fechaCreacion = LocalDateTime.now();
        user.setFechaCreacion(fechaCreacion);
        assertEquals(fechaCreacion, user.getFechaCreacion());

        // Test activo
        user.setActivo(false);
        assertFalse(user.getActivo());

        // Test search terms
        List<String> searchTerms = Arrays.asList("juan", "perez", "estudiante");
        user.setSearchTerms(searchTerms);
        assertEquals(searchTerms, user.getSearchTerms());
    }

    @Test
    public void testRolesManagement() {
        User.Rol rol1 = new User.Rol();
        rol1.setTipo("ESTUDIANTE");
        rol1.setActivo(true);
        rol1.setFechaAsignacion(LocalDateTime.now());

        User.Rol rol2 = new User.Rol();
        rol2.setTipo("COORDINADOR");
        rol2.setActivo(false);

        List<User.Rol> roles = Arrays.asList(rol1, rol2);
        user.setRoles(roles);

        assertEquals(2, user.getRoles().size());
        assertEquals("ESTUDIANTE", user.getRoles().get(0).getTipo());
        assertEquals("COORDINADOR", user.getRoles().get(1).getTipo());
    }

    @Test
    public void testRolClass() {
        User.Rol rol = new User.Rol();
        
        // Test tipo
        String tipo = "PROFESOR";
        rol.setTipo(tipo);
        assertEquals(tipo, rol.getTipo());

        // Test activo
        rol.setActivo(true);
        assertTrue(rol.getActivo());

        // Test fecha asignacion
        LocalDateTime fechaAsignacion = LocalDateTime.now();
        rol.setFechaAsignacion(fechaAsignacion);
        assertEquals(fechaAsignacion, rol.getFechaAsignacion());

        // Test datos especificos
        Object datosEspecificos = "datos_test";
        rol.setDatosEspecificos(datosEspecificos);
        assertEquals(datosEspecificos, rol.getDatosEspecificos());
    }

    @Test
    public void testCompleteUserScenario() {
        // Scenario completo de usuario estudiante
        user.setNombre("Ana García");
        user.setEmail("ana.garcia@universidad.edu");
        user.setGenero("F");
        user.setPaisNacimiento("Colombia");
        user.setEstadoCivil("Soltera");
        user.setPasswordHash("$2a$10$encrypted_password");

        User.Rol rolEstudiante = new User.Rol();
        rolEstudiante.setTipo("ESTUDIANTE");
        rolEstudiante.setActivo(true);
        rolEstudiante.setFechaAsignacion(LocalDateTime.now());

        user.setRoles(Arrays.asList(rolEstudiante));
        user.setSearchTerms(Arrays.asList("ana", "garcia", "estudiante"));

        // Verificaciones
        assertEquals("Ana García", user.getNombre());
        assertEquals("ana.garcia@universidad.edu", user.getEmail());
        assertEquals("F", user.getGenero());
        assertTrue(user.getActivo());
        assertEquals(1, user.getRoles().size());
        assertEquals("ESTUDIANTE", user.getRoles().get(0).getTipo());
        assertEquals(3, user.getSearchTerms().size());
    }
}