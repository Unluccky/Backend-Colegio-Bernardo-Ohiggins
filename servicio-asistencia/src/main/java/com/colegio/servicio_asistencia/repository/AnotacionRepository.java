package com.colegio.servicio_asistencia.repository;

import com.colegio.servicio_asistencia.model.Anotacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnotacionRepository extends JpaRepository<Anotacion, Long> {
    List<Anotacion> findByEstudianteId(Long estudianteId);
    List<Anotacion> findByProfesorId(Long profesorId);
}