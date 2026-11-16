package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Horario;
import com.colegio.servicio_academico.repository.HorarioRepository;
import com.colegio.servicio_academico.service.HorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

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
        validarHorario(horario, null);
        return repo.save(horario);
    }

    @Override
    public Horario actualizar(Long id, Horario datos) {
        Horario existente = buscarPorId(id);
        validarHorario(datos, id);
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

    private void validarHorario(Horario horario, Long horarioIdExcluido) {
        if (horario.getCurso() == null || horario.getCurso() < 1 || horario.getCurso() > 12) {
            throw new IllegalArgumentException("El curso debe estar entre 1 y 12");
        }
        if (horario.getDia() == null || horario.getDia() < 1 || horario.getDia() > 5) {
            throw new IllegalArgumentException("El día debe estar entre 1 y 5");
        }

        final LocalTime inicio;
        final LocalTime fin;
        try {
            inicio = LocalTime.parse(horario.getHoraInicio());
            fin = LocalTime.parse(horario.getHoraFin());
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("Las horas deben usar el formato HH:mm", ex);
        }
        if (!inicio.isBefore(fin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de término");
        }

        boolean conflictoCurso = repo.findByCursoAndDiaOrderByHoraInicio(
                horario.getCurso(), horario.getDia()).stream()
                .anyMatch(otro -> esConflicto(otro, horarioIdExcluido, inicio, fin));
        boolean conflictoProfesor = horario.getProfesor() != null && horario.getProfesor().getId() != null
                && repo.findByProfesorId(horario.getProfesor().getId()).stream()
                .anyMatch(otro -> otro.getDia().equals(horario.getDia())
                        && esConflicto(otro, horarioIdExcluido, inicio, fin));

        if (conflictoCurso || conflictoProfesor) {
            throw new IllegalArgumentException("El horario se superpone con otro bloque del curso o profesor");
        }
    }

    private boolean esConflicto(Horario otro, Long horarioIdExcluido, LocalTime inicio, LocalTime fin) {
        if (horarioIdExcluido != null && horarioIdExcluido.equals(otro.getId())) {
            return false;
        }
        try {
            LocalTime inicioOtro = LocalTime.parse(otro.getHoraInicio());
            LocalTime finOtro = LocalTime.parse(otro.getHoraFin());
            return inicioOtro.isBefore(fin) && finOtro.isAfter(inicio);
        } catch (DateTimeParseException | NullPointerException ex) {
            // Un registro existente malformado también debe impedir agregar otro horario
            // que no pueda comprobarse con seguridad.
            throw new IllegalStateException("Existe un horario almacenado con horas inválidas", ex);
        }
    }
}
