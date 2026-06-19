package com.colegio.servicio_academico.service;

import com.colegio.servicio_academico.model.Nota;
import java.util.List;

public interface NotaService {
    List<Nota> listarTodos();
    Nota buscarPorId(Long id);
    List<Nota> buscarPorEstudiante(Long estudianteId);
    Nota guardar(Nota nota);
    void eliminar(Long id);
}