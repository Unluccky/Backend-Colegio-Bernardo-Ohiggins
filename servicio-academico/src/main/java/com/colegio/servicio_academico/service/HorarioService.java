package com.colegio.servicio_academico.service;

import com.colegio.servicio_academico.model.Horario;
import java.util.List;

public interface HorarioService {
    List<Horario> listarTodos();
    Horario buscarPorId(Long id);
    List<Horario> buscarPorCurso(Integer curso);
    List<Horario> buscarPorProfesor(Long profesorId);
    List<Horario> buscarPorCursoYDia(Integer curso, Integer dia);
    Horario guardar(Horario horario);
    Horario actualizar(Long id, Horario horario);
    void eliminar(Long id);
}
