package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Horario;
import com.colegio.servicio_academico.repository.HorarioRepository;
import com.colegio.servicio_academico.service.HorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository repo;

    @Override
    public List<Horario> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Horario buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Horario no encontrado: " + id));
    }

    @Override
    public List<Horario> buscarPorCurso(Integer curso) {
        return repo.findByCurso(curso);
    }

    @Override
    public List<Horario> buscarPorProfesor(Long profesorId) {
        return repo.findByProfesorId(profesorId);
    }

    @Override
    public List<Horario> buscarPorCursoYDia(Integer curso, Integer dia) {
        return repo.findByCursoAndDiaOrderByHoraInicio(curso, dia);
    }

    @Override
    public Horario guardar(Horario horario) {
        return repo.save(horario);
    }

    @Override
    public Horario actualizar(Long id, Horario datos) {
        Horario existente = buscarPorId(id);
        existente.setAsignatura(datos.getAsignatura());
        existente.setProfesor(datos.getProfesor());
        existente.setCurso(datos.getCurso());
        existente.setDia(datos.getDia());
        existente.setHoraInicio(datos.getHoraInicio());
        existente.setHoraFin(datos.getHoraFin());
        existente.setSala(datos.getSala());
        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
