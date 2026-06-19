package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.repository.EstudianteRepository;
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
@DisplayName("EstudianteService - Pruebas Unitarias")
class EstudianteServiceImplTest {

    @Mock
    private EstudianteRepository repo;

    @InjectMocks
    private EstudianteServiceImpl service;

    private Estudiante estudianteBase;

    @BeforeEach
    void setUp() {
        estudianteBase = Estudiante.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .rut("12345678-9")
                .email("juan.perez@colegio.cl")
                .curso(8)
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna lista con todos los estudiantes")
    void listarTodos_deberiaRetornarLista() {
        Estudiante otro = Estudiante.builder()
                .id(2L).nombre("Ana").apellido("García")
                .rut("98765432-1").email("ana@colegio.cl").curso(10)
                .build();
        when(repo.findAll()).thenReturn(List.of(estudianteBase, otro));

        List<Estudiante> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Estudiante::getNombre)
                .containsExactly("Juan", "Ana");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("listarTodos - retorna lista vacía cuando no hay estudiantes")
    void listarTodos_sinEstudiantes_retornaListaVacia() {
        when(repo.findAll()).thenReturn(List.of());

        List<Estudiante> resultado = service.listarTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId - retorna estudiante cuando existe")
    void buscarPorId_existente_retornaEstudiante() {
        when(repo.findById(1L)).thenReturn(Optional.of(estudianteBase));

        Estudiante resultado = service.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        assertThat(resultado.getRut()).isEqualTo("12345678-9");
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException cuando no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Estudiante no encontrado: 99");
    }

    @Test
    @DisplayName("guardar - persiste y retorna el estudiante guardado")
    void guardar_deberiaRetornarEstudianteGuardado() {
        Estudiante nuevo = Estudiante.builder()
                .nombre("Pedro").apellido("López")
                .rut("11111111-1").email("pedro@colegio.cl").curso(9)
                .build();
        when(repo.save(any(Estudiante.class))).thenReturn(estudianteBase);

        Estudiante resultado = service.guardar(nuevo);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repo, times(1)).save(nuevo);
    }

    @Test
    @DisplayName("actualizar - modifica campos y persiste cambios")
    void actualizar_deberiaActualizarCampos() {
        Estudiante datosNuevos = Estudiante.builder()
                .nombre("Juan Actualizado").apellido("Pérez")
                .email("juan.nuevo@colegio.cl").curso(9)
                .build();
        Estudiante actualizado = Estudiante.builder()
                .id(1L).nombre("Juan Actualizado").apellido("Pérez")
                .rut("12345678-9").email("juan.nuevo@colegio.cl").curso(9)
                .build();

        when(repo.findById(1L)).thenReturn(Optional.of(estudianteBase));
        when(repo.save(any(Estudiante.class))).thenReturn(actualizado);

        Estudiante resultado = service.actualizar(1L, datosNuevos);

        assertThat(resultado.getNombre()).isEqualTo("Juan Actualizado");
        assertThat(resultado.getEmail()).isEqualTo("juan.nuevo@colegio.cl");
        assertThat(resultado.getCurso()).isEqualTo(9);
        verify(repo).save(any(Estudiante.class));
    }

    @Test
    @DisplayName("actualizar - lanza excepción si el estudiante no existe")
    void actualizar_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, estudianteBase))
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