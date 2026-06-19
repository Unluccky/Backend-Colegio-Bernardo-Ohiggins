package com.colegio.servicio_asistencia.service;

import com.colegio.servicio_asistencia.model.Anotacion;
import java.util.List;

public interface AnotacionService {
    List<Anotacion> listarTodos();
    Anotacion buscarPorId(Long id);
    List<Anotacion> buscarPorEstudiante(Long estudianteId);
    Anotacion guardar(Anotacion anotacion);
    Anotacion actualizar(Long id, Anotacion anotacion);
    void eliminar(Long id);
}