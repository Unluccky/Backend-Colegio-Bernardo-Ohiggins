package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Nota;
import com.colegio.servicio_academico.repository.NotaRepository;
import com.colegio.servicio_academico.service.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotaServiceImpl implements NotaService {

    private final NotaRepository repo;

    @Override
    public List<Nota> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Nota buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Nota no encontrada: " + id));
    }

    @Override
    public List<Nota> buscarPorEstudiante(Long estudianteId) {
        return repo.findByEstudianteId(estudianteId);
    }

    @Override
    public Nota guardar(Nota nota) {
        return repo.save(nota);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}