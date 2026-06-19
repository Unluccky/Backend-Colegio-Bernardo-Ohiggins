package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Evaluacion;
import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.model.Nota;
import com.colegio.servicio_academico.repository.NotaRepository;
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
@DisplayName("NotaService - Pruebas Unitarias")
class NotaServiceImplTest {

    @Mock
    private NotaRepository repo;

    @InjectMocks
    private NotaServiceImpl service;

    private Estudiante estudiante;
    private Evaluacion evaluacion;
    private Nota notaBase;

    @BeforeEach
    void setUp() {
        estudiante = Estudiante.builder()
                .id(1L).nombre("Juan").apellido("Pérez")
                .rut("12345678-9").email("juan@colegio.cl").curso(8)
                .build();

        evaluacion = Evaluacion.builder()
                .id(1L)
                .build();

        notaBase = Nota.builder()
                .id(1L)
                .estudiante(estudiante)
                .evaluacion(evaluacion)
                .valor(6.5)
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todas las notas registradas")
    void listarTodos_deberiaRetornarListaCompleta() {
        Nota otra = Nota.builder().id(2L).estudiante(estudiante)
                .evaluacion(evaluacion).valor(5.0).build();
        when(repo.findAll()).thenReturn(List.of(notaBase, otra));

        List<Nota> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Nota::getValor)
                .containsExactlyInAnyOrder(6.5, 5.0);
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId - retorna la nota cuando el id existe")
    void buscarPorId_existente_retornaNota() {
        when(repo.findById(1L)).thenReturn(Optional.of(notaBase));

        Nota resultado = service.buscarPorId(1L);

        assertThat(resultado.getValor()).isEqualTo(6.5);
        assertThat(resultado.getEstudiante().getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException si no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Nota no encontrada: 99");
    }

    @Test
    @DisplayName("buscarPorEstudiante - retorna notas filtradas por estudianteId")
    void buscarPorEstudiante_deberiaRetornarNotasDelEstudiante() {
        when(repo.findByEstudianteId(1L)).thenReturn(List.of(notaBase));

        List<Nota> resultado = service.buscarPorEstudiante(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstudiante().getId()).isEqualTo(1L);
        verify(repo).findByEstudianteId(1L);
    }

    @Test
    @DisplayName("buscarPorEstudiante - retorna lista vacía si no tiene notas")
    void buscarPorEstudiante_sinNotas_retornaVacio() {
        when(repo.findByEstudianteId(2L)).thenReturn(List.of());

        List<Nota> resultado = service.buscarPorEstudiante(2L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("guardar - persiste la nota y la retorna con id asignado")
    void guardar_deberiaRetornarNotaConId() {
        Nota sinId = Nota.builder().estudiante(estudiante)
                .evaluacion(evaluacion).valor(4.8).build();
        when(repo.save(any(Nota.class))).thenReturn(notaBase);

        Nota resultado = service.guardar(sinId);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repo, times(1)).save(sinId);
    }

    @Test
    @DisplayName("guardar - nota con valor límite inferior (1.0) se persiste correctamente")
    void guardar_valorMinimo_guardaCorrectamente() {
        Nota notaMinima = Nota.builder().id(5L).estudiante(estudiante)
                .evaluacion(evaluacion).valor(1.0).build();
        when(repo.save(any(Nota.class))).thenReturn(notaMinima);

        Nota resultado = service.guardar(notaMinima);

        assertThat(resultado.getValor()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("eliminar - invoca deleteById exactamente una vez")
    void eliminar_deberiaInvocarDeleteById() {
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);

        verify(repo, times(1)).deleteById(1L);
        verifyNoMoreInteractions(repo);
    }
}