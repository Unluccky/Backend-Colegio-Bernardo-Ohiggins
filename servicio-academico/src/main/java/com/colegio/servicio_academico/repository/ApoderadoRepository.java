package com.colegio.servicio_academico.repository;

import com.colegio.servicio_academico.model.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApoderadoRepository extends JpaRepository<Apoderado, Long> {
    Optional<Apoderado> findByRut(String rut);
    List<Apoderado> findByEstudianteId(Long estudianteId);
}