package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Asignatura;
import com.colegio.servicio_academico.model.Horario;
import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.repository.HorarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HorarioService - Pruebas Unitarias")
class HorarioServiceImplTest {

    @Mock
    private HorarioRepository repo;

    @InjectMocks
    private HorarioServiceImpl service;

    private Horario horarioBase;

    @BeforeEach
    void setUp() {
        Profesor profe = Profesor.builder().id(1L).nombre("Maria").apellido("Lopez").build();
        Asignatura asig = Asignatura.builder().id(1L).nombre("Matematicas").build();

        horarioBase = Horario.builder()
                .id(1L)
                .asignatura(asig)
                .profesor(profe)
                .curso(8)
                .dia(1)
                .horaInicio("08:00")
                .horaFin("09:30")
                .sala("A101")
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todos los horarios")
    void listarTodos_deberiaRetornarLista() {
        Horario otro = Horario.builder().id(2L).curso(8).dia(2)
                .horaInicio("10:00").horaFin("11:30").sala("B202")
                .build();
        when(repo.findAll()).thenReturn(List.of(horarioBase, otro));

        List<Horario> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId - retorna horario cuando existe")
    void buscarPorId_existente_retornaHorario() {
        when(repo.findById(1L)).thenReturn(Optional.of(horarioBase));

        Horario resultado = service.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCurso()).isEqualTo(8);
        assertThat(resultado.getDia()).isEqualTo(1);
        assertThat(resultado.getSala()).isEqualTo("A101");
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException cuando no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Horario no encontrado: 99");
    }

    @Test
    @DisplayName("buscarPorCurso - retorna horarios filtrados por curso")
    void buscarPorCurso_deberiaRetornarHorariosDelCurso() {
        when(repo.findByCurso(8)).thenReturn(List.of(horarioBase));

        List<Horario> resultado = service.buscarPorCurso(8);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCurso()).isEqualTo(8);
        verify(repo).findByCurso(8);
    }

    @Test
    @DisplayName("buscarPorProfesor - retorna horarios filtrados por profesor")
    void buscarPorProfesor_deberiaRetornarHorariosDelProfesor() {
        when(repo.findByProfesorId(1L)).thenReturn(List.of(horarioBase));

        List<Horario> resultado = service.buscarPorProfesor(1L);

        assertThat(resultado).hasSize(1);
        verify(repo).findByProfesorId(1L);
    }

    @Test
    @DisplayName("buscarPorCursoYDia - retorna horarios filtrados")
    void buscarPorCursoYDia_deberiaRetornarHorarios() {
        when(repo.findByCursoAndDiaOrderByHoraInicio(8, 1)).thenReturn(List.of(horarioBase));

        List<Horario> resultado = service.buscarPorCursoYDia(8, 1);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDia()).isEqualTo(1);
        verify(repo).findByCursoAndDiaOrderByHoraInicio(8, 1);
    }

    @Test
    @DisplayName("guardar - persiste y retorna el horario guardado")
    void guardar_deberiaRetornarHorarioGuardado() {
        Horario nuevo = Horario.builder().curso(9).dia(3)
                .horaInicio("08:00").horaFin("09:30").sala("C303")
                .build();
        when(repo.save(any(Horario.class))).thenReturn(horarioBase);

        Horario resultado = service.guardar(nuevo);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repo, times(1)).save(nuevo);
    }

    @Test
    @DisplayName("actualizar - modifica campos y persiste cambios")
    void actualizar_deberiaActualizarCampos() {
        Horario datosNuevos = Horario.builder()
                .curso(9).dia(2).horaInicio("10:00").horaFin("11:30").sala("B202")
                .build();
        Horario actualizado = Horario.builder()
                .id(1L).curso(9).dia(2)
                .horaInicio("10:00").horaFin("11:30").sala("B202")
                .build();

        when(repo.findById(1L)).thenReturn(Optional.of(horarioBase));
        when(repo.save(any(Horario.class))).thenReturn(actualizado);

        Horario resultado = service.actualizar(1L, datosNuevos);

        assertThat(resultado.getCurso()).isEqualTo(9);
        assertThat(resultado.getDia()).isEqualTo(2);
        assertThat(resultado.getSala()).isEqualTo("B202");
        assertThat(resultado.getHoraInicio()).isEqualTo("10:00");
        verify(repo).save(any(Horario.class));
    }

    @Test
    @DisplayName("actualizar - lanza excepcion si el horario no existe")
    void actualizar_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, horarioBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("eliminar - invoca deleteById con el id correcto")
    void eliminar_deberiaLlamarDeleteById() {
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);

        verify(repo, times(1)).deleteById(1L);
    }
}
