package com.colegio.servicio_academico.service.impl;

import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.repository.ProfesorRepository;
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
@DisplayName("ProfesorService - Pruebas Unitarias")
class ProfesorServiceImplTest {

    @Mock
    private ProfesorRepository repo;

    @InjectMocks
    private ProfesorServiceImpl service;

    private Profesor profesorBase;

    @BeforeEach
    void setUp() {
        profesorBase = Profesor.builder()
                .id(1L)
                .nombre("María")
                .apellido("González")
                .rut("11111111-1")
                .especialidad("Matemáticas")
                .email("maria.gonzalez@colegio.cl")
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna lista con todos los profesores")
    void listarTodos_deberiaRetornarListaCompleta() {
        Profesor otro = Profesor.builder()
                .id(2L).nombre("Pedro").apellido("Soto")
                .rut("22222222-2").especialidad("Historia")
                .email("pedro@colegio.cl").build();
        when(repo.findAll()).thenReturn(List.of(profesorBase, otro));

        List<Profesor> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Profesor::getNombre)
                .containsExactly("María", "Pedro");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("listarTodos - retorna lista vacía cuando no hay profesores")
    void listarTodos_sinProfesores_retornaListaVacia() {
        when(repo.findAll()).thenReturn(List.of());

        List<Profesor> resultado = service.listarTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId - retorna profesor cuando el id existe")
    void buscarPorId_existente_retornaProfesor() {
        when(repo.findById(1L)).thenReturn(Optional.of(profesorBase));

        Profesor resultado = service.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("María");
        assertThat(resultado.getEspecialidad()).isEqualTo("Matemáticas");
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException cuando no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profesor no encontrado: 99");
    }

    @Test
    @DisplayName("guardar - persiste y retorna el profesor guardado")
    void guardar_deberiaRetornarProfesorGuardado() {
        Profesor nuevo = Profesor.builder()
                .nombre("Luis").apellido("Mora")
                .rut("33333333-3").especialidad("Física")
                .email("luis@colegio.cl").build();
        when(repo.save(any(Profesor.class))).thenReturn(profesorBase);

        Profesor resultado = service.guardar(nuevo);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repo, times(1)).save(nuevo);
    }

    @Test
    @DisplayName("actualizar - modifica campos y persiste cambios")
    void actualizar_deberiaActualizarCampos() {
        Profesor datosNuevos = Profesor.builder()
                .nombre("María Actualizada").apellido("González")
                .email("maria.nueva@colegio.cl").especialidad("Cálculo")
                .build();
        Profesor actualizado = Profesor.builder()
                .id(1L).nombre("María Actualizada").apellido("González")
                .rut("11111111-1").especialidad("Cálculo")
                .email("maria.nueva@colegio.cl").build();

        when(repo.findById(1L)).thenReturn(Optional.of(profesorBase));
        when(repo.save(any(Profesor.class))).thenReturn(actualizado);

        Profesor resultado = service.actualizar(1L, datosNuevos);

        assertThat(resultado.getNombre()).isEqualTo("María Actualizada");
        assertThat(resultado.getEspecialidad()).isEqualTo("Cálculo");
        verify(repo).save(any(Profesor.class));
    }

    @Test
    @DisplayName("actualizar - lanza excepción si el profesor no existe")
    void actualizar_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, profesorBase))
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