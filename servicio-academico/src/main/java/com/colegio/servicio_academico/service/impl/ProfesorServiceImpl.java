package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.repository.ProfesorRepository;
import com.colegio.servicio_academico.service.ProfesorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfesorServiceImpl implements ProfesorService {

    private final ProfesorRepository repo;

    @Override
    public List<Profesor> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Profesor buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + id));
    }

    @Override
    public Profesor guardar(Profesor profesor) {
        return repo.save(profesor);
    }

    @Override
    public Profesor actualizar(Long id, Profesor datos) {
        Profesor existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setEmail(datos.getEmail());
        existente.setEspecialidad(datos.getEspecialidad());
        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}