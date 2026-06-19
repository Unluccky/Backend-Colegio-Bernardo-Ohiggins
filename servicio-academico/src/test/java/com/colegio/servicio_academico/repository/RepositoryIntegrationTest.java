package com.colegio.servicio_academico.repository;

import com.colegio.servicio_academico.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("Repositorios - Pruebas de Integración con H2")
class RepositoryIntegrationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ProfesorRepository profesorRepo;

    @Autowired
    private EstudianteRepository estudianteRepo;

    @Autowired
    private ApoderadoRepository apoderadoRepo;

    @Autowired
    private AsignaturaRepository asignaturaRepo;

    @Autowired
    private EvaluacionRepository evaluacionRepo;

    @Autowired
    private HorarioRepository horarioRepo;

    @Autowired
    private NotaRepository notaRepo;

    // ── Profesor ──────────────────────────────────────────────────

    @Test
    @DisplayName("ProfesorRepository - guardar y buscar por ID")
    void profesor_guardarYBuscarPorId() {
        Profesor p = Profesor.builder()
                .nombre("María").apellido("González").rut("11111111-1")
                .email("maria@colegio.cl").especialidad("Matemáticas")
                .contrasena("hash123").build();

        Profesor guardado = profesorRepo.save(p);
        assertThat(guardado.getId()).isNotNull();

        Optional<Profesor> encontrado = profesorRepo.findById(guardado.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("María");
        assertThat(encontrado.get().getRut()).isEqualTo("11111111-1");
    }

    @Test
    @DisplayName("ProfesorRepository - buscar por RUT")
    void profesor_buscarPorRut() {
        Profesor p = Profesor.builder()
                .nombre("Pedro").apellido("Soto").rut("22222222-2")
                .email("pedro@colegio.cl").especialidad("Historia")
                .contrasena("hash456").build();
        em.persist(p);

        Optional<Profesor> encontrado = profesorRepo.findByRut("22222222-2");
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Pedro");
    }

    @Test
    @DisplayName("ProfesorRepository - listar todos")
    void profesor_listarTodos() {
        em.persist(Profesor.builder().nombre("A").apellido("A1").rut("11111111-1").contrasena("h1").build());
        em.persist(Profesor.builder().nombre("B").apellido("B1").rut("22222222-2").contrasena("h2").build());

        List<Profesor> todos = profesorRepo.findAll();
        assertThat(todos).hasSize(2);
    }

    @Test
    @DisplayName("ProfesorRepository - eliminar")
    void profesor_eliminar() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Test").apellido("User").rut("33333333-3").contrasena("h3").build());

        profesorRepo.deleteById(p.getId());

        assertThat(profesorRepo.findById(p.getId())).isEmpty();
    }

    // ── Estudiante ────────────────────────────────────────────────

    @Test
    @DisplayName("EstudianteRepository - guardar con todos los campos")
    void estudiante_guardarCompleto() {
        Estudiante e = Estudiante.builder()
                .nombre("Juan").apellido("Pérez").rut("12345678-9")
                .email("juan@colegio.cl").curso(8).password("pass123")
                .build();

        Estudiante guardado = estudianteRepo.save(e);
        assertThat(guardado.getId()).isNotNull();

        Optional<Estudiante> encontrado = estudianteRepo.findById(guardado.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCurso()).isEqualTo(8);
        assertThat(encontrado.get().getNombre()).isEqualTo("Juan");
        assertThat(encontrado.get().getPassword()).isEqualTo("pass123");
    }

    @Test
    @DisplayName("EstudianteRepository - buscar por RUT")
    void estudiante_buscarPorRut() {
        Estudiante e = Estudiante.builder()
                .nombre("Ana").apellido("García").rut("98765432-1")
                .email("ana@colegio.cl").curso(10).password("pass456")
                .build();
        em.persist(e);

        Optional<Estudiante> encontrado = estudianteRepo.findByRut("98765432-1");
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("EstudianteRepository - buscar RUT inexistente")
    void estudiante_buscarRutInexistente() {
        Optional<Estudiante> encontrado = estudianteRepo.findByRut("00000000-0");
        assertThat(encontrado).isEmpty();
    }

    // ── Apoderado ─────────────────────────────────────────────────

    @Test
    @DisplayName("ApoderadoRepository - guardar con estudiante asociado")
    void apoderado_guardarConEstudiante() {
        Estudiante est = em.persist(Estudiante.builder()
                .nombre("Juan").apellido("Pérez").rut("12345678-9")
                .curso(8).password("pass").build());

        Apoderado a = Apoderado.builder()
                .nombre("Carlos").apellido("Pérez").rut("55555555-5")
                .email("carlos@email.cl").contrasena("hash")
                .estudiante(est).build();

        Apoderado guardado = apoderadoRepo.save(a);
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEstudiante().getId()).isEqualTo(est.getId());
    }

    @Test
    @DisplayName("ApoderadoRepository - buscar por RUT")
    void apoderado_buscarPorRut() {
        Estudiante est = em.persist(Estudiante.builder()
                .nombre("Ana").apellido("García").rut("98765432-1").curso(5).password("pass").build());
        Apoderado a = em.persist(Apoderado.builder()
                .nombre("Luis").apellido("García").rut("66666666-6")
                .contrasena("hash").estudiante(est).build());

        Optional<Apoderado> encontrado = apoderadoRepo.findByRut("66666666-6");
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Luis");
    }

    @Test
    @DisplayName("ApoderadoRepository - buscar por estudiante ID")
    void apoderado_buscarPorEstudiante() {
        Estudiante est = em.persist(Estudiante.builder()
                .nombre("Juan").apellido("Pérez").rut("12345678-9").curso(8).password("pass").build());
        em.persist(Apoderado.builder().nombre("Carlos").apellido("Pérez").rut("55555555-5")
                .contrasena("hash").estudiante(est).build());

        List<Apoderado> apoderados = apoderadoRepo.findByEstudianteId(est.getId());
        assertThat(apoderados).hasSize(1);
        assertThat(apoderados.get(0).getNombre()).isEqualTo("Carlos");
    }

    // ── Asignatura ────────────────────────────────────────────────

    @Test
    @DisplayName("AsignaturaRepository - guardar con profesor")
    void asignatura_guardarConProfesor() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("María").apellido("González").rut("11111111-1")
                .especialidad("Matemáticas").contrasena("hash").build());

        Asignatura a = Asignatura.builder()
                .nombre("Álgebra").nivelCurso(8).profesor(p).build();

        Asignatura guardada = asignaturaRepo.save(a);
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getProfesor().getId()).isEqualTo(p.getId());
    }

    @Test
    @DisplayName("AsignaturaRepository - buscar por nivel de curso")
    void asignatura_buscarPorNivelCurso() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Ana").apellido("Martínez").rut("13131313-3")
                .especialidad("Historia").contrasena("hash").build());
        em.persist(Asignatura.builder().nombre("Historia Universal").nivelCurso(10).profesor(p).build());

        List<Asignatura> encontradas = asignaturaRepo.findByNivelCurso(10);
        assertThat(encontradas).hasSize(1);
        assertThat(encontradas.get(0).getNombre()).isEqualTo("Historia Universal");
    }

    // ── Evaluacion ────────────────────────────────────────────────

    @Test
    @DisplayName("EvaluacionRepository - guardar con tipo PRUEBA")
    void evaluacion_guardarPrueba() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Roberto").apellido("Díaz").rut("12121212-1")
                .especialidad("Ciencias").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Biología").nivelCurso(9).profesor(p).build());

        Evaluacion e = Evaluacion.builder()
                .nombre("Prueba N°1").fecha(LocalDate.now())
                .tipo(TipoEvaluacion.PRUEBA).asignatura(a).build();

        Evaluacion guardada = evaluacionRepo.save(e);
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getTipo()).isEqualTo(TipoEvaluacion.PRUEBA);
    }

    @Test
    @DisplayName("EvaluacionRepository - buscar por asignatura ID")
    void evaluacion_buscarPorAsignatura() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Laura").apellido("González").rut("22222222-2")
                .especialidad("Lenguaje").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Literatura").nivelCurso(11).profesor(p).build());
        em.persist(Evaluacion.builder().nombre("Ensayo").fecha(LocalDate.now())
                .tipo(TipoEvaluacion.TRABAJO).asignatura(a).build());

        List<Evaluacion> encontradas = evaluacionRepo.findByAsignaturaId(a.getId());
        assertThat(encontradas).hasSize(1);
        assertThat(encontradas.get(0).getNombre()).isEqualTo("Ensayo");
    }

    // ── Horario ───────────────────────────────────────────────────

    @Test
    @DisplayName("HorarioRepository - guardar horario completo")
    void horario_guardarCompleto() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Daniela").apellido("Rojas").rut("15151515-5")
                .especialidad("Artes").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Artes Visuales").nivelCurso(4).profesor(p).build());

        Horario h = Horario.builder()
                .asignatura(a).profesor(p).curso(4).dia(1)
                .horaInicio("08:00").horaFin("08:45").sala("A-101")
                .build();

        Horario guardado = horarioRepo.save(h);
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getCurso()).isEqualTo(4);
        assertThat(guardado.getDia()).isEqualTo(1);
    }

    @Test
    @DisplayName("HorarioRepository - buscar por curso")
    void horario_buscarPorCurso() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Carlos").apellido("Muñoz").rut("11111111-1")
                .especialidad("Matemáticas").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Matemáticas").nivelCurso(3).profesor(p).build());
        em.persist(Horario.builder().asignatura(a).profesor(p).curso(3).dia(2)
                .horaInicio("10:00").horaFin("10:45").sala("B-201").build());

        List<Horario> encontrados = horarioRepo.findByCurso(3);
        assertThat(encontrados).hasSize(1);
    }

    @Test
    @DisplayName("HorarioRepository - buscar por profesor ID")
    void horario_buscarPorProfesor() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Fernando").apellido("Torres").rut("14141414-4")
                .especialidad("Educación Física").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Ed. Física").nivelCurso(5).profesor(p).build());
        em.persist(Horario.builder().asignatura(a).profesor(p).curso(5).dia(3)
                .horaInicio("08:00").horaFin("08:45").sala("GIM-1").build());

        List<Horario> encontrados = horarioRepo.findByProfesorId(p.getId());
        assertThat(encontrados).hasSize(1);
    }

    @Test
    @DisplayName("HorarioRepository - buscar por curso y día ordenado")
    void horario_buscarPorCursoYDia() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Carlos").apellido("Muñoz").rut("11111111-1")
                .especialidad("Matemáticas").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Matemáticas").nivelCurso(7).profesor(p).build());
        em.persist(Horario.builder().asignatura(a).profesor(p).curso(7).dia(1)
                .horaInicio("08:00").horaFin("08:45").sala("A-101").build());

        List<Horario> encontrados = horarioRepo.findByCursoAndDiaOrderByHoraInicio(7, 1);
        assertThat(encontrados).hasSize(1);
    }

    @Test
    @DisplayName("HorarioRepository - buscar por asignatura ID")
    void horario_buscarPorAsignatura() {
        Profesor p = em.persist(Profesor.builder()
                .nombre("Laura").apellido("González").rut("22222222-2")
                .especialidad("Lenguaje").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Lenguaje").nivelCurso(2).profesor(p).build());
        em.persist(Horario.builder().asignatura(a).profesor(p).curso(2).dia(4)
                .horaInicio("14:00").horaFin("14:45").sala("C-301").build());

        List<Horario> encontrados = horarioRepo.findByAsignaturaId(a.getId());
        assertThat(encontrados).hasSize(1);
    }

    // ── Nota ───────────────────────────────────────────────────────

    @Test
    @DisplayName("NotaRepository - guardar con estudiante y evaluacion")
    void nota_guardarConEstudianteYEvaluacion() {
        Estudiante est = em.persist(Estudiante.builder()
                .nombre("Juan").apellido("Pérez").rut("12345678-9").curso(8).password("pass").build());
        Profesor p = em.persist(Profesor.builder()
                .nombre("María").apellido("González").rut("11111111-1")
                .especialidad("Matemáticas").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Matemáticas").nivelCurso(8).profesor(p).build());
        Evaluacion ev = em.persist(Evaluacion.builder().nombre("Prueba N°1")
                .fecha(LocalDate.now()).tipo(TipoEvaluacion.PRUEBA).asignatura(a).build());

        Nota n = Nota.builder().estudiante(est).evaluacion(ev).valor(6.5).build();

        Nota guardada = notaRepo.save(n);
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getValor()).isEqualTo(6.5);
    }

    @Test
    @DisplayName("NotaRepository - buscar por estudiante ID")
    void nota_buscarPorEstudiante() {
        Estudiante est = em.persist(Estudiante.builder()
                .nombre("Juan").apellido("Pérez").rut("12345678-9").curso(8).password("pass").build());
        Profesor p = em.persist(Profesor.builder()
                .nombre("María").apellido("González").rut("11111111-1")
                .especialidad("Matemáticas").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Matemáticas").nivelCurso(8).profesor(p).build());
        Evaluacion ev = em.persist(Evaluacion.builder().nombre("Prueba N°1")
                .fecha(LocalDate.now()).tipo(TipoEvaluacion.PRUEBA).asignatura(a).build());
        em.persist(Nota.builder().estudiante(est).evaluacion(ev).valor(5.0).build());

        List<Nota> encontradas = notaRepo.findByEstudianteId(est.getId());
        assertThat(encontradas).hasSize(1);
        assertThat(encontradas.get(0).getValor()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("NotaRepository - buscar por evaluación ID")
    void nota_buscarPorEvaluacion() {
        Estudiante est = em.persist(Estudiante.builder()
                .nombre("Juan").apellido("Pérez").rut("12345678-9").curso(8).password("pass").build());
        Profesor p = em.persist(Profesor.builder()
                .nombre("María").apellido("González").rut("11111111-1")
                .especialidad("Matemáticas").contrasena("hash").build());
        Asignatura a = em.persist(Asignatura.builder().nombre("Matemáticas").nivelCurso(8).profesor(p).build());
        Evaluacion ev = em.persist(Evaluacion.builder().nombre("Prueba N°1")
                .fecha(LocalDate.now()).tipo(TipoEvaluacion.PRUEBA).asignatura(a).build());
        em.persist(Nota.builder().estudiante(est).evaluacion(ev).valor(6.0).build());

        List<Nota> encontradas = notaRepo.findByEvaluacionId(ev.getId());
        assertThat(encontradas).hasSize(1);
    }
}
