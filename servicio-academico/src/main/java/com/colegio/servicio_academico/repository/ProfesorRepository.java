package com.colegio.servicio_academico.repository;

import com.colegio.servicio_academico.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Optional<Profesor> findByRut(String rut);
}