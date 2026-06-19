package com.colegio.servicio_asistencia.repository;

import com.colegio.servicio_asistencia.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByEstudianteId(Long estudianteId);
    List<Asistencia> findByAsignaturaId(Long asignaturaId);
    List<Asistencia> findByAsignaturaIdAndFecha(Long asignaturaId, LocalDate fecha);
}