package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Asignatura;
import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.repository.AsignaturaRepository;
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
@DisplayName("AsignaturaService - Pruebas Unitarias")
class AsignaturaServiceImplTest {

    @Mock
    private AsignaturaRepository repo;

    @InjectMocks
    private AsignaturaServiceImpl service;

    private Profesor profesor;
    private Asignatura asignaturaBase;

    @BeforeEach
    void setUp() {
        profesor = Profesor.builder()
                .id(1L).nombre("María").apellido("González")
                .rut("11111111-1").especialidad("Matemáticas")
                .email("maria@colegio.cl").build();

        asignaturaBase = Asignatura.builder()
                .id(1L)
                .nombre("Matemáticas")
                .nivelCurso(8)
                .profesor(profesor)
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todas las asignaturas")
    void listarTodos_deberiaRetornarListaCompleta() {
        Asignatura otra = Asignatura.builder()
                .id(2L).nombre("Historia").nivelCurso(9).profesor(profesor).build();
        when(repo.findAll()).thenReturn(List.of(asignaturaBase, otra));

        List<Asignatura> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Asignatura::getNombre)
                .containsExactly("Matemáticas", "Historia");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId - retorna asignatura cuando el id existe")
    void buscarPorId_existente_retornaAsignatura() {
        when(repo.findById(1L)).thenReturn(Optional.of(asignaturaBase));

        Asignatura resultado = service.buscarPorId(1L);

        assertThat(resultado.getNombre()).isEqualTo("Matemáticas");
        assertThat(resultado.getNivelCurso()).isEqualTo(8);
        assertThat(resultado.getProfesor().getNombre()).isEqualTo("María");
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException cuando no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Asignatura no encontrada: 99");
    }

    @Test
    @DisplayName("guardar - persiste y retorna la asignatura")
    void guardar_deberiaRetornarAsignaturaGuardada() {
        Asignatura nueva = Asignatura.builder()
                .nombre("Física").nivelCurso(10).profesor(profesor).build();
        when(repo.save(any(Asignatura.class))).thenReturn(asignaturaBase);

        Asignatura resultado = service.guardar(nueva);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repo, times(1)).save(nueva);
    }

    @Test
    @DisplayName("actualizar - modifica nombre, nivel y profesor")
    void actualizar_deberiaActualizarCampos() {
        Profesor nuevoPro = Profesor.builder().id(2L).nombre("Pedro")
                .apellido("Soto").rut("22222222-2").especialidad("Física")
                .email("pedro@colegio.cl").build();
        Asignatura datosNuevos = Asignatura.builder()
                .nombre("Matemáticas Avanzadas").nivelCurso(10).profesor(nuevoPro).build();
        Asignatura actualizada = Asignatura.builder()
                .id(1L).nombre("Matemáticas Avanzadas").nivelCurso(10).profesor(nuevoPro).build();

        when(repo.findById(1L)).thenReturn(Optional.of(asignaturaBase));
        when(repo.save(any(Asignatura.class))).thenReturn(actualizada);

        Asignatura resultado = service.actualizar(1L, datosNuevos);

        assertThat(resultado.getNombre()).isEqualTo("Matemáticas Avanzadas");
        assertThat(resultado.getNivelCurso()).isEqualTo(10);
        verify(repo).save(any(Asignatura.class));
    }

    @Test
    @DisplayName("eliminar - invoca deleteById con el id correcto")
    void eliminar_deberiaLlamarDeleteById() {
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);

        verify(repo, times(1)).deleteById(1L);
    }
}