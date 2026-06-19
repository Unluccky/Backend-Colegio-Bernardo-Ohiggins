package com.colegio.servicio_academico.repository;

import com.colegio.servicio_academico.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    List<Asignatura> findByNivelCurso(Integer nivelCurso);
}