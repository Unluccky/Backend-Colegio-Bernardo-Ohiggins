package com.colegio.servicio_academico.repository;

import com.colegio.servicio_academico.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByCurso(Integer curso);
    List<Horario> findByProfesorId(Long profesorId);
    List<Horario> findByAsignaturaId(Long asignaturaId);
    List<Horario> findByCursoAndDiaOrderByHoraInicio(Integer curso, Integer dia);
}
