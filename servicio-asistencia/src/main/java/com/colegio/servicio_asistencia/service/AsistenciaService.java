package com.colegio.servicio_asistencia.service;

import com.colegio.servicio_asistencia.model.Asistencia;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaService {
    List<Asistencia> listarTodos();
    Asistencia buscarPorId(Long id);
    List<Asistencia> buscarPorEstudiante(Long estudianteId);
    List<Asistencia> buscarPorAsignaturaYFecha(Long asignaturaId, LocalDate fecha);
    Asistencia guardar(Asistencia asistencia);
    List<Asistencia> guardarBatch(List<Asistencia> asistencias);
    Asistencia actualizar(Long id, Asistencia asistencia);
    void eliminar(Long id);
}