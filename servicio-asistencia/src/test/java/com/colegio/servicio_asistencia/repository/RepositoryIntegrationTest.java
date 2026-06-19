package com.colegio.servicio_asistencia.repository;

import com.colegio.servicio_asistencia.model.*;
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
    private AnotacionRepository anotacionRepo;

    @Autowired
    private AsistenciaRepository asistenciaRepo;

    // ── Asistencia ────────────────────────────────────────────────

    @Test
    @DisplayName("AsistenciaRepository - guardar y buscar por ID")
    void asistencia_guardarYBuscarPorId() {
        Asistencia a = Asistencia.builder()
                .estudianteId(10L).asignaturaId(5L)
                .fecha(LocalDate.now()).estado(EstadoAsistencia.PRESENTE)
                .build();

        Asistencia guardada = asistenciaRepo.save(a);
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getEstado()).isEqualTo(EstadoAsistencia.PRESENTE);

        Optional<Asistencia> encontrada = asistenciaRepo.findById(guardada.getId());
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getEstudianteId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("AsistenciaRepository - guardar con todos los estados")
    void asistencia_guardarTodosLosEstados() {
        for (EstadoAsistencia estado : EstadoAsistencia.values()) {
            Asistencia a = Asistencia.builder()
                    .estudianteId(1L).asignaturaId(1L)
                    .fecha(LocalDate.now()).estado(estado).build();
            Asistencia guardada = asistenciaRepo.save(a);
            assertThat(guardada.getEstado()).isEqualTo(estado);
        }
    }

    @Test
    @DisplayName("AsistenciaRepository - buscar por estudiante ID")
    void asistencia_buscarPorEstudiante() {
        em.persist(Asistencia.builder()
                .estudianteId(10L).asignaturaId(5L)
                .fecha(LocalDate.now()).estado(EstadoAsistencia.PRESENTE).build());
        em.persist(Asistencia.builder()
                .estudianteId(10L).asignaturaId(3L)
                .fecha(LocalDate.now()).estado(EstadoAsistencia.AUSENTE).build());

        List<Asistencia> encontradas = asistenciaRepo.findByEstudianteId(10L);
        assertThat(encontradas).hasSize(2);
    }

    @Test
    @DisplayName("AsistenciaRepository - buscar por asignatura ID")
    void asistencia_buscarPorAsignatura() {
        em.persist(Asistencia.builder()
                .estudianteId(10L).asignaturaId(5L)
                .fecha(LocalDate.now()).estado(EstadoAsistencia.PRESENTE).build());

        List<Asistencia> encontradas = asistenciaRepo.findByAsignaturaId(5L);
        assertThat(encontradas).hasSize(1);
    }

    @Test
    @DisplayName("AsistenciaRepository - buscar por asignatura y fecha")
    void asistencia_buscarPorAsignaturaYFecha() {
        LocalDate hoy = LocalDate.now();
        em.persist(Asistencia.builder()
                .estudianteId(10L).asignaturaId(5L)
                .fecha(hoy).estado(EstadoAsistencia.PRESENTE).build());

        List<Asistencia> encontradas = asistenciaRepo.findByAsignaturaIdAndFecha(5L, hoy);
        assertThat(encontradas).hasSize(1);
        assertThat(encontradas.get(0).getEstudianteId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("AsistenciaRepository - buscar por asignatura y fecha sin resultados")
    void asistencia_buscarPorAsignaturaYFecha_sinResultados() {
        List<Asistencia> encontradas = asistenciaRepo.findByAsignaturaIdAndFecha(99L, LocalDate.now());
        assertThat(encontradas).isEmpty();
    }

    // ── Anotacion ─────────────────────────────────────────────────

    @Test
    @DisplayName("AnotacionRepository - guardar y buscar por ID")
    void anotacion_guardarYBuscarPorId() {
        Anotacion a = Anotacion.builder()
                .estudianteId(10L).profesorId(3L)
                .descripcion("Anotación de prueba")
                .tipo(TipoAnotacion.POSITIVA)
                .fecha(LocalDate.now())
                .build();

        Anotacion guardada = anotacionRepo.save(a);
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getDescripcion()).isEqualTo("Anotación de prueba");

        Optional<Anotacion> encontrada = anotacionRepo.findById(guardada.getId());
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getTipo()).isEqualTo(TipoAnotacion.POSITIVA);
    }

    @Test
    @DisplayName("AnotacionRepository - guardar con todos los tipos")
    void anotacion_guardarTodosLosTipos() {
        for (TipoAnotacion tipo : TipoAnotacion.values()) {
            Anotacion a = Anotacion.builder()
                    .estudianteId(1L).profesorId(1L)
                    .descripcion("Test " + tipo.name())
                    .tipo(tipo).fecha(LocalDate.now()).build();
            Anotacion guardada = anotacionRepo.save(a);
            assertThat(guardada.getTipo()).isEqualTo(tipo);
        }
    }

    @Test
    @DisplayName("AnotacionRepository - buscar por estudiante ID")
    void anotacion_buscarPorEstudiante() {
        em.persist(Anotacion.builder()
                .estudianteId(10L).profesorId(3L)
                .descripcion("Anotación 1").tipo(TipoAnotacion.NEGATIVA)
                .fecha(LocalDate.now()).build());
        em.persist(Anotacion.builder()
                .estudianteId(10L).profesorId(3L)
                .descripcion("Anotación 2").tipo(TipoAnotacion.POSITIVA)
                .fecha(LocalDate.now()).build());

        List<Anotacion> encontradas = anotacionRepo.findByEstudianteId(10L);
        assertThat(encontradas).hasSize(2);
    }

    @Test
    @DisplayName("AnotacionRepository - buscar por profesor ID")
    void anotacion_buscarPorProfesor() {
        em.persist(Anotacion.builder()
                .estudianteId(10L).profesorId(3L)
                .descripcion("Anotación del profesor").tipo(TipoAnotacion.NEUTRAL)
                .fecha(LocalDate.now()).build());

        List<Anotacion> encontradas = anotacionRepo.findByProfesorId(3L);
        assertThat(encontradas).hasSize(1);
        assertThat(encontradas.get(0).getProfesorId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("AnotacionRepository - buscar por estudiante sin resultados")
    void anotacion_buscarPorEstudiante_sinResultados() {
        List<Anotacion> encontradas = anotacionRepo.findByEstudianteId(999L);
        assertThat(encontradas).isEmpty();
    }

    @Test
    @DisplayName("AnotacionRepository - eliminar")
    void anotacion_eliminar() {
        Anotacion a = em.persist(Anotacion.builder()
                .estudianteId(1L).profesorId(1L)
                .descripcion("Para eliminar").tipo(TipoAnotacion.NEUTRAL)
                .fecha(LocalDate.now()).build());

        anotacionRepo.deleteById(a.getId());

        assertThat(anotacionRepo.findById(a.getId())).isEmpty();
    }
}
