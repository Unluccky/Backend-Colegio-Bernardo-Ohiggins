package com.colegio.servicio_academico.service;

import com.colegio.servicio_academico.model.Profesor;
import java.util.List;

public interface ProfesorService {
    List<Profesor> listarTodos();
    Profesor buscarPorId(Long id);
    Profesor guardar(Profesor profesor);
    Profesor actualizar(Long id, Profesor profesor);
    void eliminar(Long id);
}