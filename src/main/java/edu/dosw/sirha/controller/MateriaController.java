package edu.dosw.sirha.controller;

import edu.dosw.sirha.dto.request.MateriaRequest;
import edu.dosw.sirha.dto.response.MateriaResponse;
import edu.dosw.sirha.service.MateriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la gestión de materias académicas en el sistema SIRHA.
 * 
 * <p>Este controlador proporciona los endpoints necesarios para administrar el catálogo de
 * materias (asignaturas) ofrecidas por la institución. Las materias son la base fundamental
 * del sistema de inscripciones y cambios de horario, ya que los estudiantes solicitan cambios
 * entre grupos de estas materias.</p>
 * 
 * <h2>Información de Materia:</h2>
 * <ul>
 *   <li><b>Código:</b> Identificador único de la materia (ej: "MATH101", "PHYS202")</li>
 *   <li><b>Nombre:</b> Denominación oficial de la materia (ej: "Cálculo Diferencial")</li>
 *   <li><b>Créditos:</b> Número de créditos académicos que otorga la materia</li>
 *   <li><b>Facultad:</b> Facultad a la que pertenece la materia</li>
 *   <li><b>Descripción:</b> Descripción detallada del contenido y objetivos</li>
 *   <li><b>Prerrequisitos:</b> Lista de materias que deben cursarse antes</li>
 *   <li><b>Grupos:</b> Grupos (horarios) disponibles para la materia en el período actual</li>
 * </ul>
 * 
 * <h2>Operaciones CRUD Disponibles:</h2>
 * <ol>
 *   <li><b>CREATE:</b> Registrar nuevas materias en el catálogo (POST /api/materias)</li>
 *   <li><b>READ:</b> Consultar materias por ID, facultad o búsqueda de texto (GET)</li>
 *   <li><b>UPDATE:</b> Actualizar información de materias existentes (PUT /api/materias/{id})</li>
 *   <li><b>DELETE:</b> Eliminar materias del catálogo (DELETE /api/materias/{id})</li>
 * </ol>
 * 
 * <h2>Funcionalidades de Búsqueda:</h2>
 * <ul>
 *   <li><b>Por ID:</b> Obtener materia específica por su identificador único</li>
 *   <li><b>Por Facultad:</b> Listar todas las materias de una facultad específica</li>
 *   <li><b>Búsqueda de texto:</b> Buscar materias por código, nombre o descripción</li>
 *   <li><b>Listar todas:</b> Obtener catálogo completo de materias</li>
 * </ul>
 * 
 * <h2>Relaciones con Otras Entidades:</h2>
 * <pre>
 * Materia ──┬──> Facultad (pertenece a una facultad)
 *           ├──> Prerrequisitos (lista de materias requeridas)
 *           └──> Grupos (horarios disponibles en período actual)
 * </pre>
 * 
 * <h2>Validaciones de Negocio:</h2>
 * <ul>
 *   <li>El código de materia debe ser único en el sistema</li>
 *   <li>El número de créditos debe ser mayor a 0 y menor o igual a 10</li>
 *   <li>La facultad referenciada debe existir en el sistema</li>
 *   <li>Los prerrequisitos deben ser materias válidas existentes</li>
 *   <li>No se puede eliminar una materia si tiene inscripciones activas o grupos con estudiantes</li>
 * </ul>
 * 
 * <h2>Seguridad y Permisos:</h2>
 * <ul>
 *   <li><b>Consulta:</b> Todos los usuarios autenticados pueden consultar materias</li>
 *   <li><b>Creación/Actualización:</b> Solo administradores y decanatura</li>
 *   <li><b>Eliminación:</b> Solo administradores</li>
 * </ul>
 * 
 * <p><b>Ejemplo de registro de materia:</b></p>
 * <pre>
 * POST /api/materias
 * Authorization: Bearer eyJhbGc...
 * Content-Type: application/json
 * 
 * {
 *   "codigo": "CALC101",
 *   "nombre": "Cálculo Diferencial",
 *   "creditos": 4,
 *   "facultadId": "665d7f9a1234567890abcdef",
 *   "descripcion": "Fundamentos del cálculo diferencial de una variable",
 *   "prerequisitos": []
 * }
 * 
 * Respuesta (201 CREATED):
 * {
 *   "id": "6661bd4e5678901234ef0123",
 *   "codigo": "CALC101",
 *   "nombre": "Cálculo Diferencial",
 *   "creditos": 4,
 *   "facultad": {
 *     "id": "665d7f9a1234567890abcdef",
 *     "nombre": "Ingeniería"
 *   },
 *   "descripcion": "Fundamentos del cálculo diferencial de una variable",
 *   "prerequisitos": [],
 *   "grupos": []
 * }
 * </pre>
 * 
 * @author Equipo DOSW - SIRHA
 * @version 1.0
 * @since 2024-06
 * 
 * @see MateriaRequest
 * @see MateriaResponse
 * @see MateriaService
 */
@RestController
@RequestMapping("/api/materias")
@RequiredArgsConstructor
@Validated
public class MateriaController {

    /**
     * Servicio de lógica de negocio para gestión de materias académicas.
     * Contiene las operaciones CRUD y validaciones relacionadas con materias.
     */
    private final MateriaService materiaService;

    /**
     * Crea una nueva materia en el catálogo académico.
     * 
     * <p>Este endpoint permite registrar nuevas asignaturas en el sistema. La materia se valida
     * para garantizar que el código sea único, que la facultad exista y que los prerrequisitos
     * sean materias válidas registradas previamente.</p>
     * 
     * <h3>Validaciones Aplicadas:</h3>
     * <ul>
     *   <li><b>Código único:</b> El código de materia no debe existir previamente</li>
     *   <li><b>Créditos válidos:</b> Debe ser un número entre 1 y 10</li>
     *   <li><b>Facultad existente:</b> El ID de facultad debe corresponder a una facultad registrada</li>
     *   <li><b>Prerrequisitos válidos:</b> Todos los IDs de prerrequisitos deben ser materias existentes</li>
     *   <li><b>Campos requeridos:</b> Código, nombre, créditos y facultadId son obligatorios</li>
     * </ul>
     * 
     * @param request Objeto {@link MateriaRequest} con los datos de la nueva materia.
     * 
     * @return {@link ResponseEntity} con status 201 CREATED y body {@link MateriaResponse}
     *         conteniendo la materia creada con su ID generado.
     * 
     * @throws edu.dosw.sirha.exception.BusinessException Si el código ya existe o la facultad no existe.
     */
    @PostMapping
    public ResponseEntity<MateriaResponse> create(@Valid @RequestBody MateriaRequest request) {
        MateriaResponse response = materiaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza la información de una materia existente.
     * 
     * <p>Permite modificar los datos de una materia ya registrada. Es útil para corregir información,
     * actualizar descripción, ajustar créditos o modificar la lista de prerrequisitos.</p>
     * 
     * <h3>Campos Actualizables:</h3>
     * <ul>
     *   <li>Nombre de la materia</li>
     *   <li>Descripción y contenido</li>
     *   <li>Número de créditos</li>
     *   <li>Facultad asignada</li>
     *   <li>Lista de prerrequisitos</li>
     * </ul>
     * 
     * <p><b>Nota:</b> El código de materia NO puede ser modificado una vez creada,
     * ya que es el identificador principal en sistemas académicos externos.</p>
     * 
     * @param id ID de la materia a actualizar (formato ObjectId de MongoDB).
     * @param request Objeto {@link MateriaRequest} con los nuevos datos de la materia.
     * 
     * @return {@link MateriaResponse} con la información actualizada de la materia.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si la materia no existe.
     */
    @PutMapping("/{id}")
    public MateriaResponse update(@PathVariable String id, @Valid @RequestBody MateriaRequest request) {
        return materiaService.update(id, request);
    }

    /**
     * Elimina una materia del catálogo académico.
     * 
     * <p>Este endpoint permite eliminar materias del sistema. La eliminación solo es posible
     * si la materia no tiene dependencias activas (grupos con estudiantes inscritos,
     * solicitudes pendientes, etc.).</p>
     * 
     * <h3>Restricciones de Eliminación:</h3>
     * <ul>
     *   <li>No se puede eliminar si tiene grupos activos con estudiantes inscritos</li>
     *   <li>No se puede eliminar si es prerrequisito de otras materias</li>
     *   <li>No se puede eliminar si tiene solicitudes de cambio pendientes</li>
     *   <li>Solo administradores pueden eliminar materias</li>
     * </ul>
     * 
     * <p><b>Recomendación:</b> En lugar de eliminar, considerar marcar la materia como inactiva
     * para mantener integridad referencial e histórico académico.</p>
     * 
     * @param id ID de la materia a eliminar (formato ObjectId de MongoDB).
     * 
     * @return {@link ResponseEntity} con status 204 NO CONTENT si la eliminación fue exitosa.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si la materia no existe.
     * @throws edu.dosw.sirha.exception.BusinessException Si la materia tiene dependencias activas.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        materiaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene los detalles completos de una materia específica por su ID.
     * 
     * <p>Retorna toda la información de una materia incluyendo código, nombre, créditos,
     * facultad, descripción, prerrequisitos y grupos disponibles en el período actual.</p>
     * 
     * @param id ID de la materia a consultar (formato ObjectId de MongoDB).
     * 
     * @return {@link MateriaResponse} con los datos completos de la materia.
     * 
     * @throws edu.dosw.sirha.exception.ResourceNotFoundException Si la materia no existe.
     */
    @GetMapping("/{id}")
    public MateriaResponse findById(@PathVariable String id) {
        return materiaService.findById(id);
    }

    /**
     * Obtiene el catálogo completo de materias del sistema.
     * 
     * <p>Retorna todas las materias registradas en el sistema, independientemente de su estado
     * o facultad. Útil para reportes globales y administración del catálogo académico.</p>
     * 
     * <p><b>Nota de rendimiento:</b> Para instituciones con catálogos grandes (más de 1000 materias),
     * considerar usar paginación o filtros por facultad para optimizar el tiempo de respuesta.</p>
     * 
     * @return Lista de {@link MateriaResponse} con todas las materias del catálogo.
     *         Retorna lista vacía si no hay materias registradas.
     */
    @GetMapping
    public List<MateriaResponse> findAll() {
        return materiaService.findAll();
    }

    /**
     * Obtiene todas las materias de una facultad específica.
     * 
     * <p>Este endpoint es particularmente útil para filtrar el catálogo de materias por facultad,
     * facilitando la gestión académica y permitiendo a los estudiantes explorar las materias
     * ofrecidas en su programa de estudio.</p>
     * 
     * <h3>Casos de Uso:</h3>
     * <ul>
     *   <li><b>Decanatura:</b> Gestionar las materias de su facultad específica</li>
     *   <li><b>Estudiantes:</b> Explorar materias disponibles en su programa académico</li>
     *   <li><b>Reportes:</b> Análisis de oferta académica por facultad</li>
     * </ul>
     * 
     * @param facultadId ID de la facultad (formato ObjectId de MongoDB).
     * 
     * @return Lista de {@link MateriaResponse} con todas las materias de la facultad especificada.
     *         Retorna lista vacía si la facultad no tiene materias o no existe.
     */
    @GetMapping("/facultad/{facultadId}")
    public List<MateriaResponse> findByFacultad(@PathVariable String facultadId) {
        return materiaService.findByFacultad(facultadId);
    }

    /**
     * Busca materias por código, nombre o descripción usando búsqueda de texto.
     * 
     * <p>Este endpoint proporciona una funcionalidad de búsqueda flexible que permite a los usuarios
     * encontrar materias escribiendo parte del código, nombre o descripción. La búsqueda es
     * insensible a mayúsculas/minúsculas y busca coincidencias parciales.</p>
     * 
     * <h3>Campos de Búsqueda:</h3>
     * <ul>
     *   <li><b>Código:</b> Búsqueda por código de materia (ej: buscar "CALC" encuentra "CALC101", "CALC102")</li>
     *   <li><b>Nombre:</b> Búsqueda por nombre (ej: "cálculo" encuentra "Cálculo Diferencial", "Cálculo Integral")</li>
     *   <li><b>Descripción:</b> Búsqueda en descripción completa de la materia</li>
     * </ul>
     * 
     * <h3>Comportamiento de Búsqueda:</h3>
     * <ul>
     *   <li>Si {@code term} está vacío o es null, retorna todas las materias</li>
     *   <li>La búsqueda es case-insensitive ("CALC" = "calc" = "Calc")</li>
     *   <li>Soporta búsqueda parcial ("cálc" encuentra "Cálculo")</li>
     *   <li>Los resultados se ordenan por relevancia (mejor coincidencia primero)</li>
     * </ul>
     * 
     * <p><b>Ejemplos de búsqueda:</b></p>
     * <pre>
     * GET /api/materias/search?term=MATH
     * → Retorna: MATH101, MATH201, MATH305...
     * 
     * GET /api/materias/search?term=física
     * → Retorna: Física I, Física II, Física Moderna...
     * 
     * GET /api/materias/search?term=programa
     * → Retorna materias con "programa" en nombre o descripción
     * </pre>
     * 
     * @param term Término de búsqueda opcional. Puede ser código, nombre o parte de la descripción.
     *             Si es null o vacío, se comporta como {@link #findAll()}.
     * 
     * @return Lista de {@link MateriaResponse} con materias que coinciden con el término de búsqueda.
     *         Retorna lista vacía si no hay coincidencias.
     */
    @GetMapping("/search")
    public List<MateriaResponse> search(@RequestParam(name = "term", required = false) String term) {
        return materiaService.search(term);
    }
}