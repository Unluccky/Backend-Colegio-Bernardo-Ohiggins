package com.colegio.servicio_academico.repository;

import com.colegio.servicio_academico.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByEstudianteId(Long estudianteId);
    List<Nota> findByEvaluacionId(Long evaluacionId);
}