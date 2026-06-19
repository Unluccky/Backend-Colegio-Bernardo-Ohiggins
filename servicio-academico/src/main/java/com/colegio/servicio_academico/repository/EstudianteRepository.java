package com.colegio.servicio_academico.repository;

import com.colegio.servicio_academico.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByRut(String rut);
}