package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Asignatura;
import com.colegio.servicio_academico.repository.AsignaturaRepository;
import com.colegio.servicio_academico.service.AsignaturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsignaturaServiceImpl implements AsignaturaService {

    private final AsignaturaRepository repo;

    @Override
    public List<Asignatura> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Asignatura buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Asignatura no encontrada: " + id));
    }

    @Override
    public Asignatura guardar(Asignatura asignatura) {
        return repo.save(asignatura);
    }

    @Override
    public Asignatura actualizar(Long id, Asignatura datos) {
        Asignatura existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setNivelCurso(datos.getNivelCurso());
        existente.setProfesor(datos.getProfesor());
        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}