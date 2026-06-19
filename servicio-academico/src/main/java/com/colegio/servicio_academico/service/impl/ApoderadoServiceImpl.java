package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Apoderado;
import com.colegio.servicio_academico.repository.ApoderadoRepository;
import com.colegio.servicio_academico.service.ApoderadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApoderadoServiceImpl implements ApoderadoService {

    private final ApoderadoRepository repo;

    @Override
    public List<Apoderado> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Apoderado buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Apoderado no encontrado: " + id));
    }

    @Override
    public Apoderado buscarPorRut(String rut) {
        return repo.findByRut(rut)
            .orElseThrow(() -> new RuntimeException("Apoderado no encontrado con RUT: " + rut));
    }

    @Override
    public List<Apoderado> buscarPorEstudiante(Long estudianteId) {
        return repo.findByEstudianteId(estudianteId);
    }

    @Override
    public Apoderado guardar(Apoderado apoderado) {
        return repo.save(apoderado);
    }

    @Override
    public Apoderado actualizar(Long id, Apoderado datos) {
        Apoderado existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setEmail(datos.getEmail());
        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}