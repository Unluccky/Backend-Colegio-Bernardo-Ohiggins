package com.colegio.servicio_academico.service;

import com.colegio.servicio_academico.model.Estudiante;
import java.util.List;

public interface EstudianteService {
    List<Estudiante> listarTodos();
    Estudiante buscarPorId(Long id);
    Estudiante buscarPorRut(String rut);
    Estudiante guardar(Estudiante estudiante);
    Estudiante actualizar(Long id, Estudiante estudiante);
    void eliminar(Long id);
}