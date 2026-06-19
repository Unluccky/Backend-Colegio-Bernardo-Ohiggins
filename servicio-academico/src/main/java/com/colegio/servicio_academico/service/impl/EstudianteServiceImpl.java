package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.repository.EstudianteRepository;
import com.colegio.servicio_academico.service.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository repo;

    @Override
    public List<Estudiante> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Estudiante buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + id));
    }

    @Override
    public Estudiante buscarPorRut(String rut) {
        return repo.findByRut(rut)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con RUT: " + rut));
    }

    @Override
    public Estudiante guardar(Estudiante estudiante) {
        return repo.save(estudiante);
    }

    @Override
    public Estudiante actualizar(Long id, Estudiante datos) {
        Estudiante existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setEmail(datos.getEmail());
        existente.setCurso(datos.getCurso());
        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}