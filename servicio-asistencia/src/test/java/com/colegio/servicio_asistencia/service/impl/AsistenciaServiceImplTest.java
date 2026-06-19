package com.colegio.servicio_asistencia.service.impl;

import com.colegio.servicio_asistencia.model.Asistencia;
import com.colegio.servicio_asistencia.model.EstadoAsistencia;
import com.colegio.servicio_asistencia.repository.AsistenciaRepository;
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

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayName("AsistenciaService - Pruebas Unitarias")
class AsistenciaServiceImplTest {

    @Mock
    private AsistenciaRepository repo;

    @InjectMocks
    private AsistenciaServiceImpl service;

    private Asistencia asistenciaBase;

    @BeforeEach
    void setUp() {
        asistenciaBase = Asistencia.builder()
                .id(1L)
                .estudianteId(10L)
                .asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 1))
                .estado(EstadoAsistencia.PRESENTE)
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todos los registros de asistencia")
    void listarTodos_retornaListaCompleta() {
        Asistencia segunda = Asistencia.builder()
                .id(2L).estudianteId(11L).asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 1))
                .estado(EstadoAsistencia.AUSENTE).build();
        when(repo.findAll()).thenReturn(List.of(asistenciaBase, segunda));

        List<Asistencia> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Asistencia::getEstado)
                .containsExactly(EstadoAsistencia.PRESENTE, EstadoAsistencia.AUSENTE);
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId - retorna asistencia cuando existe el id")
    void buscarPorId_existente_retornaAsistencia() {
        when(repo.findById(1L)).thenReturn(Optional.of(asistenciaBase));

        Asistencia resultado = service.buscarPorId(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoAsistencia.PRESENTE);
        assertThat(resultado.getEstudianteId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException si no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Asistencia no encontrada: 99");
    }

    @Test
    @DisplayName("buscarPorEstudiante - retorna registros del estudiante indicado")
    void buscarPorEstudiante_retornaRegistrosDelEstudiante() {
        when(repo.findByEstudianteId(10L)).thenReturn(List.of(asistenciaBase));

        List<Asistencia> resultado = service.buscarPorEstudiante(10L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstudianteId()).isEqualTo(10L);
        verify(repo).findByEstudianteId(10L);
    }

    @Test
    @DisplayName("guardar - persiste el registro y lo retorna")
    void guardar_persisteYRetornaAsistencia() {
        Asistencia nueva = Asistencia.builder()
                .estudianteId(12L).asignaturaId(5L)
                .fecha(LocalDate.now()).estado(EstadoAsistencia.PRESENTE)
                .build();
        when(repo.save(any(Asistencia.class))).thenReturn(asistenciaBase);

        Asistencia resultado = service.guardar(nueva);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repo, times(1)).save(nueva);
    }

    @Test
    @DisplayName("actualizar - cambia estado PRESENTE a AUSENTE correctamente")
    void actualizar_cambiaEstadoPresente_aAusente() {
        Asistencia datosNuevos = Asistencia.builder()
                .estado(EstadoAsistencia.AUSENTE)
                .fecha(LocalDate.of(2025, 4, 2))
                .asignaturaId(5L)
                .build();
        Asistencia actualizada = Asistencia.builder()
                .id(1L).estudianteId(10L).asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 2))
                .estado(EstadoAsistencia.AUSENTE).build();

        when(repo.findById(1L)).thenReturn(Optional.of(asistenciaBase));
        when(repo.save(any(Asistencia.class))).thenReturn(actualizada);

        Asistencia resultado = service.actualizar(1L, datosNuevos);

        assertThat(resultado.getEstado()).isEqualTo(EstadoAsistencia.AUSENTE);
        assertThat(resultado.getFecha()).isEqualTo(LocalDate.of(2025, 4, 2));
        verify(repo).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("guardarBatch - guarda múltiples asistencias")
    void guardarBatch_deberiaGuardarMultiples() {
        Asistencia a1 = Asistencia.builder()
                .estudianteId(10L).asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 1)).estado(EstadoAsistencia.PRESENTE).build();
        Asistencia a2 = Asistencia.builder()
                .estudianteId(11L).asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 1)).estado(EstadoAsistencia.AUSENTE).build();

        when(repo.findByAsignaturaIdAndFecha(anyLong(), any(LocalDate.class))).thenReturn(List.of());
        when(repo.save(any(Asistencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Asistencia> resultado = service.guardarBatch(List.of(a1, a2));

        assertThat(resultado).hasSize(2);
        verify(repo, times(2)).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("guardarBatch - actualiza existente cuando ya hay registro para mismo estudiante+asignatura+fecha")
    void guardarBatch_conExistente_deberiaActualizar() {
        Asistencia existente = Asistencia.builder()
                .id(1L).estudianteId(10L).asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 1)).estado(EstadoAsistencia.PRESENTE).build();
        Asistencia nuevo = Asistencia.builder()
                .estudianteId(10L).asignaturaId(5L)
                .fecha(LocalDate.of(2025, 4, 1)).estado(EstadoAsistencia.AUSENTE).build();

        when(repo.findByAsignaturaIdAndFecha(eq(5L), eq(LocalDate.of(2025, 4, 1))))
                .thenReturn(List.of(existente));
        when(repo.save(any(Asistencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Asistencia> resultado = service.guardarBatch(List.of(nuevo));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoAsistencia.AUSENTE);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        verify(repo, times(1)).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("guardarBatch - lista vacía retorna lista vacía")
    void guardarBatch_listaVacia_retornaVacio() {
        List<Asistencia> resultado = service.guardarBatch(new ArrayList<>());

        assertThat(resultado).isEmpty();
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("actualizar - lanza excepción si no existe")
    void actualizar_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, asistenciaBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("eliminar - invoca deleteById con el id correcto")
    void eliminar_invocaDeleteById() {
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);

        verify(repo, times(1)).deleteById(1L);
    }
}