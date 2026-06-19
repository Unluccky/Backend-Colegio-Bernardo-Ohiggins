package com.colegio.servicio_asistencia.service.impl;

import com.colegio.servicio_asistencia.model.Asistencia;
import com.colegio.servicio_asistencia.repository.AsistenciaRepository;
import com.colegio.servicio_asistencia.service.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository repo;

    @Override
    public List<Asistencia> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Asistencia buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Asistencia no encontrada: " + id));
    }

    @Override
    public List<Asistencia> buscarPorEstudiante(Long estudianteId) {
        return repo.findByEstudianteId(estudianteId);
    }

    @Override
    public List<Asistencia> buscarPorAsignaturaYFecha(Long asignaturaId, LocalDate fecha) {
        return repo.findByAsignaturaIdAndFecha(asignaturaId, fecha);
    }

    @Override
    public Asistencia guardar(Asistencia asistencia) {
        return repo.save(asistencia);
    }

    @Override
    public List<Asistencia> guardarBatch(List<Asistencia> asistencias) {
        // Upsert: si ya existe un registro para el mismo estudiante+asignatura+fecha, actualizar
        return asistencias.stream().map(a -> {
            Optional<Asistencia> existente = repo.findByAsignaturaIdAndFecha(a.getAsignaturaId(), a.getFecha())
                .stream()
                .filter(e -> e.getEstudianteId().equals(a.getEstudianteId()))
                .findFirst();
            if (existente.isPresent()) {
                Asistencia e = existente.get();
                e.setEstado(a.getEstado());
                return repo.save(e);
            }
            return repo.save(a);
        }).toList();
    }

    @Override
    public Asistencia actualizar(Long id, Asistencia datos) {
        Asistencia existente = buscarPorId(id);
        existente.setEstado(datos.getEstado());
        existente.setFecha(datos.getFecha());
        existente.setAsignaturaId(datos.getAsignaturaId());
        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}