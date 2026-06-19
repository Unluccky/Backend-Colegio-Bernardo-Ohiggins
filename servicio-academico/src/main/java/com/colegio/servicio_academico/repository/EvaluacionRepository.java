package com.colegio.servicio_academico.repository;

import com.colegio.servicio_academico.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByAsignaturaId(Long asignaturaId);
}