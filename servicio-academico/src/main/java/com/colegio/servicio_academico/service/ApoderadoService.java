package com.colegio.servicio_academico.service;

import com.colegio.servicio_academico.model.Apoderado;
import java.util.List;

public interface ApoderadoService {
    List<Apoderado> listarTodos();
    Apoderado buscarPorId(Long id);
    Apoderado buscarPorRut(String rut);
    List<Apoderado> buscarPorEstudiante(Long estudianteId);
    Apoderado guardar(Apoderado apoderado);
    Apoderado actualizar(Long id, Apoderado apoderado);
    void eliminar(Long id);
}