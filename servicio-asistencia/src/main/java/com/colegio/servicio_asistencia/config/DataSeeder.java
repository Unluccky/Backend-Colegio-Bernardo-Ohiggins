package com.colegio.servicio_asistencia.config;

import com.colegio.servicio_asistencia.model.Anotacion;
import com.colegio.servicio_asistencia.model.Asistencia;
import com.colegio.servicio_asistencia.model.EstadoAsistencia;
import com.colegio.servicio_asistencia.model.TipoAnotacion;
import com.colegio.servicio_asistencia.repository.AnotacionRepository;
import com.colegio.servicio_asistencia.repository.AsistenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AsistenciaRepository asistenciaRepo;
    private final AnotacionRepository anotacionRepo;

    @Override
    public void run(String... args) {
        if (asistenciaRepo.count() > 0) {
            log.info("Base de datos ya contiene datos — se omite el seed.");
            return;
        }

        log.info("=== Sembrando datos de prueba en Servicio Asistencia ===");

        Long estudianteAna = 1L; // ID de Ana Soto (insertada por servicio-academico)
        Long estudiantePedro = 2L; // ID de Pedro Ramírez
        Long asignaturaMate = 1L; // ID de Matemáticas
        Long asignaturaLengua = 2L; // ID de Lenguaje

        // ── Asistencias ─────────────────────────────────────────────
        asistenciaRepo.save(Asistencia.builder()
                .estudianteId(estudianteAna).asignaturaId(asignaturaMate)
                .fecha(LocalDate.now().minusDays(2)).estado(EstadoAsistencia.PRESENTE).build());
        asistenciaRepo.save(Asistencia.builder()
                .estudianteId(estudianteAna).asignaturaId(asignaturaLengua)
                .fecha(LocalDate.now().minusDays(1)).estado(EstadoAsistencia.PRESENTE).build());
        asistenciaRepo.save(Asistencia.builder()
                .estudianteId(estudianteAna).asignaturaId(asignaturaMate)
                .fecha(LocalDate.now()).estado(EstadoAsistencia.ATRASADO).build());

        asistenciaRepo.save(Asistencia.builder()
                .estudianteId(estudiantePedro).asignaturaId(asignaturaMate)
                .fecha(LocalDate.now().minusDays(2)).estado(EstadoAsistencia.AUSENTE).build());
        asistenciaRepo.save(Asistencia.builder()
                .estudianteId(estudiantePedro).asignaturaId(asignaturaLengua)
                .fecha(LocalDate.now().minusDays(1)).estado(EstadoAsistencia.PRESENTE).build());
        asistenciaRepo.save(Asistencia.builder()
                .estudianteId(estudiantePedro).asignaturaId(asignaturaMate)
                .fecha(LocalDate.now()).estado(EstadoAsistencia.PRESENTE).build());
        log.info("  ✔ Asistencias: 6 registros creados");

        // ── Anotaciones ─────────────────────────────────────────────
        anotacionRepo.save(Anotacion.builder()
                .estudianteId(estudianteAna).profesorId(1L)
                .descripcion("Participó activamente en la resolución de ejercicios en la pizarra.")
                .tipo(TipoAnotacion.POSITIVA).fecha(LocalDate.now().minusDays(3)).build());
        anotacionRepo.save(Anotacion.builder()
                .estudianteId(estudianteAna).profesorId(1L)
                .descripcion("Llegó 10 minutos tarde a clase sin justificación.")
                .tipo(TipoAnotacion.NEGATIVA).fecha(LocalDate.now().minusDays(1)).build());
        anotacionRepo.save(Anotacion.builder()
                .estudianteId(estudiantePedro).profesorId(2L)
                .descripcion("Entregó el trabajo de lectura antes de la fecha límite.")
                .tipo(TipoAnotacion.POSITIVA).fecha(LocalDate.now().minusDays(2)).build());
        anotacionRepo.save(Anotacion.builder()
                .estudianteId(estudiantePedro).profesorId(2L)
                .descripcion("Se reporta que el estudiante ha mejorado su comportamiento en clases.")
                .tipo(TipoAnotacion.NEUTRAL).fecha(LocalDate.now()).build());
        log.info("  ✔ Anotaciones: 4 registros creados");

        log.info("✅ Seed de Servicio Asistencia completado.");
    }
}
