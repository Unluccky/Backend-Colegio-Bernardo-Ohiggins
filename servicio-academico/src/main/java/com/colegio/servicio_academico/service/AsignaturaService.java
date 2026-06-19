package com.colegio.servicio_academico.service;

import com.colegio.servicio_academico.model.Asignatura;
import java.util.List;

public interface AsignaturaService {
    List<Asignatura> listarTodos();
    Asignatura buscarPorId(Long id);
    Asignatura guardar(Asignatura asignatura);
    Asignatura actualizar(Long id, Asignatura asignatura);
    void eliminar(Long id);
}
