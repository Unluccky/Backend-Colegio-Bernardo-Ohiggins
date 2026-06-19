package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Asignatura;
import com.colegio.servicio_academico.model.Evaluacion;
import com.colegio.servicio_academico.model.TipoEvaluacion;
import com.colegio.servicio_academico.repository.EvaluacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluacionService - Pruebas Unitarias")
class EvaluacionServiceImplTest {

    @Mock
    private EvaluacionRepository repo;

    @InjectMocks
    private EvaluacionServiceImpl service;

    private Asignatura asignatura;
    private Evaluacion evaluacionBase;

    @BeforeEach
    void setUp() {
        asignatura = Asignatura.builder()
                .id(1L).nombre("Matemáticas").nivelCurso(8).build();

        evaluacionBase = Evaluacion.builder()
                .id(1L)
                .nombre("Prueba 1")
                .fecha(LocalDate.of(2025, 5, 10))
                .tipo(TipoEvaluacion.PRUEBA)
                .asignatura(asignatura)
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todas las evaluaciones")
    void listarTodos_deberiaRetornarListaCompleta() {
        Evaluacion otra = Evaluacion.builder()
                .id(2L).nombre("Tarea 1")
                .fecha(LocalDate.of(2025, 6, 1))
                .tipo(TipoEvaluacion.TAREA).asignatura(asignatura).build();
        when(repo.findAll()).thenReturn(List.of(evaluacionBase, otra));

        List<Evaluacion> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Evaluacion::getNombre)
                .containsExactly("Prueba 1", "Tarea 1");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId - retorna evaluacion cuando el id existe")
    void buscarPorId_existente_retornaEvaluacion() {
        when(repo.findById(1L)).thenReturn(Optional.of(evaluacionBase));

        Evaluacion resultado = service.buscarPorId(1L);

        assertThat(resultado.getNombre()).isEqualTo("Prueba 1");
        assertThat(resultado.getTipo()).isEqualTo(TipoEvaluacion.PRUEBA);
        assertThat(resultado.getAsignatura().getNombre()).isEqualTo("Matemáticas");
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException cuando no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Evaluacion no encontrada: 99");
    }

    @Test
    @DisplayName("guardar - persiste y retorna la evaluacion")
    void guardar_deberiaRetornarEvaluacionGuardada() {
        Evaluacion nueva = Evaluacion.builder()
                .nombre("Examen Final").fecha(LocalDate.of(2025, 12, 1))
                .tipo(TipoEvaluacion.TRABAJO).asignatura(asignatura).build();
        when(repo.save(any(Evaluacion.class))).thenReturn(evaluacionBase);

        Evaluacion resultado = service.guardar(nueva);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repo, times(1)).save(nueva);
    }

    @Test
    @DisplayName("actualizar - modifica campos y persiste cambios")
    void actualizar_deberiaActualizarCampos() {
        Evaluacion datosNuevos = Evaluacion.builder()
                .nombre("Prueba 1 Actualizada")
                .fecha(LocalDate.of(2025, 5, 20))
                .tipo(TipoEvaluacion.TRABAJO).asignatura(asignatura).build();
        Evaluacion actualizada = Evaluacion.builder()
                .id(1L).nombre("Prueba 1 Actualizada")
                .fecha(LocalDate.of(2025, 5, 20))
                .tipo(TipoEvaluacion.TRABAJO).asignatura(asignatura).build();

        when(repo.findById(1L)).thenReturn(Optional.of(evaluacionBase));
        when(repo.save(any(Evaluacion.class))).thenReturn(actualizada);

        Evaluacion resultado = service.actualizar(1L, datosNuevos);

        assertThat(resultado.getNombre()).isEqualTo("Prueba 1 Actualizada");
        assertThat(resultado.getTipo()).isEqualTo(TipoEvaluacion.TRABAJO);
        verify(repo).save(any(Evaluacion.class));
    }

    @Test
    @DisplayName("actualizar - lanza excepción si la evaluacion no existe")
    void actualizar_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, evaluacionBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("eliminar - invoca deleteById con el id correcto")
    void eliminar_deberiaLlamarDeleteById() {
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);

        verify(repo, times(1)).deleteById(1L);
        verifyNoMoreInteractions(repo);
    }
}