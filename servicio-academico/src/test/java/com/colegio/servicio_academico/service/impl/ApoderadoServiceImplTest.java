package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Apoderado;
import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.repository.ApoderadoRepository;
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
@DisplayName("ApoderadoService - Pruebas Unitarias")
class ApoderadoServiceImplTest {

    @Mock
    private ApoderadoRepository repo;

    @InjectMocks
    private ApoderadoServiceImpl service;

    private Apoderado apoderado;
    private Estudiante estudiante;

    @BeforeEach
    void setUp() {
        estudiante = Estudiante.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .rut("33333333-3")
                .email("juan@colegio.cl")
                .curso(8)
                .build();

        apoderado = Apoderado.builder()
                .id(1L)
                .nombre("Carlos")
                .apellido("Pérez")
                .rut("55555555-5")
                .email("carlos@email.cl")
                .estudiante(estudiante)
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todos los apoderados")
    void listarTodos_retornaListaCompleta() {
        when(repo.findAll()).thenReturn(List.of(apoderado));

        List<Apoderado> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Carlos");
    }

    @Test
    @DisplayName("buscarPorId - retorna apoderado cuando el id existe")
    void buscarPorId_existente_retornaApoderado() {
        when(repo.findById(1L)).thenReturn(Optional.of(apoderado));

        Apoderado resultado = service.buscarPorId(1L);

        assertThat(resultado.getRut()).isEqualTo("55555555-5");
        assertThat(resultado.getNombre()).isEqualTo("Carlos");
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException si no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Apoderado no encontrado: 99");
    }

    @Test
    @DisplayName("buscarPorRut - retorna apoderado cuando el rut existe")
    void buscarPorRut_existente_retornaApoderado() {
        when(repo.findByRut("55555555-5")).thenReturn(Optional.of(apoderado));

        Apoderado resultado = service.buscarPorRut("55555555-5");

        assertThat(resultado.getNombre()).isEqualTo("Carlos");
        assertThat(resultado.getEstudiante().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarPorRut - lanza RuntimeException si el rut no existe")
    void buscarPorRut_noExiste_lanzaExcepcion() {
        when(repo.findByRut("00000000-0")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorRut("00000000-0"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Apoderado no encontrado con RUT: 00000000-0");
    }

    @Test
    @DisplayName("buscarPorEstudiante - retorna apoderados del estudiante indicado")
    void buscarPorEstudiante_retornaApoderadosDelEstudiante() {
        when(repo.findByEstudianteId(1L)).thenReturn(List.of(apoderado));

        List<Apoderado> resultado = service.buscarPorEstudiante(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstudiante().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("guardar - guarda y retorna el apoderado")
    void guardar_retornaApoderadoGuardado() {
        when(repo.save(any(Apoderado.class))).thenReturn(apoderado);

        Apoderado resultado = service.guardar(apoderado);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getRut()).isEqualTo("55555555-5");
        verify(repo, times(1)).save(apoderado);
    }

    @Test
    @DisplayName("actualizar - modifica nombre, apellido y email del apoderado")
    void actualizar_modificaCampos() {
        Apoderado datosNuevos = Apoderado.builder()
                .nombre("Carlos Actualizado")
                .apellido("Pérez Nuevo")
                .email("nuevo@email.cl")
                .build();

        Apoderado actualizado = Apoderado.builder()
                .id(1L)
                .nombre("Carlos Actualizado")
                .apellido("Pérez Nuevo")
                .email("nuevo@email.cl")
                .rut("55555555-5")
                .estudiante(estudiante)
                .build();

        when(repo.findById(1L)).thenReturn(Optional.of(apoderado));
        when(repo.save(any(Apoderado.class))).thenReturn(actualizado);

        Apoderado resultado = service.actualizar(1L, datosNuevos);

        assertThat(resultado.getNombre()).isEqualTo("Carlos Actualizado");
        assertThat(resultado.getEmail()).isEqualTo("nuevo@email.cl");
        verify(repo).save(any(Apoderado.class));
    }

    @Test
    @DisplayName("eliminar - invoca deleteById con el id correcto")
    void eliminar_invocaDeleteById() {
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);

        verify(repo, times(1)).deleteById(1L);
    }
}