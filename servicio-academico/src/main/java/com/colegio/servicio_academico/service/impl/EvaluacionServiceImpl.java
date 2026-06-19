package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Evaluacion;
import com.colegio.servicio_academico.repository.EvaluacionRepository;
import com.colegio.servicio_academico.service.EvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluacionServiceImpl implements EvaluacionService {

    private final EvaluacionRepository repo;

    @Override
    public List<Evaluacion> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Evaluacion buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Evaluacion no encontrada: " + id));
    }

    @Override
    public Evaluacion guardar(Evaluacion evaluacion) {
        return repo.save(evaluacion);
    }

    @Override
    public Evaluacion actualizar(Long id, Evaluacion datos) {
        Evaluacion existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setFecha(datos.getFecha());
        existente.setTipo(datos.getTipo());
        existente.setAsignatura(datos.getAsignatura());
        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}