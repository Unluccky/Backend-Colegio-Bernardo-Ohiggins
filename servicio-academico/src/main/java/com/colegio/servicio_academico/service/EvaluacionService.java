package com.colegio.servicio_academico.service;

import com.colegio.servicio_academico.model.Evaluacion;
import java.util.List;

public interface EvaluacionService {
    List<Evaluacion> listarTodos();
    Evaluacion buscarPorId(Long id);
    Evaluacion guardar(Evaluacion evaluacion);
    Evaluacion actualizar(Long id, Evaluacion evaluacion);
    void eliminar(Long id);
}