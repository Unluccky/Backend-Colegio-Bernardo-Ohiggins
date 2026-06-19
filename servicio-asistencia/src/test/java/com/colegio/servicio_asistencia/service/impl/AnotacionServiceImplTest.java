package com.colegio.servicio_asistencia.service.impl;

import com.colegio.servicio_asistencia.feign.AcademicoFeignClient;
import com.colegio.servicio_asistencia.messaging.AnotacionEvent;
import com.colegio.servicio_asistencia.messaging.AnotacionEventPublisher;
import com.colegio.servicio_asistencia.model.Anotacion;
import com.colegio.servicio_asistencia.model.TipoAnotacion;
import com.colegio.servicio_asistencia.repository.AnotacionRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnotacionService - Pruebas Unitarias")
class AnotacionServiceImplTest {

    @Mock
    private AnotacionRepository repo;

    @Mock
    private AcademicoFeignClient academicoClient;

    @Mock
    private AnotacionEventPublisher eventPublisher;

    @InjectMocks
    private AnotacionServiceImpl service;

    private Anotacion anotacionNegativa;
    private Anotacion anotacionPositiva;

    @BeforeEach
    void setUp() {
        anotacionNegativa = Anotacion.builder()
                .id(1L)
                .estudianteId(10L)
                .profesorId(3L)
                .descripcion("Mal comportamiento en clase")
                .tipo(TipoAnotacion.NEGATIVA)
                .fecha(LocalDate.of(2025, 4, 15))
                .build();

        anotacionPositiva = Anotacion.builder()
                .id(2L)
                .estudianteId(10L)
                .profesorId(3L)
                .descripcion("Excelente participación")
                .tipo(TipoAnotacion.POSITIVA)
                .fecha(LocalDate.of(2025, 4, 15))
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todas las anotaciones")
    void listarTodos_retornaListaCompleta() {
        when(repo.findAll()).thenReturn(List.of(anotacionNegativa, anotacionPositiva));

        List<Anotacion> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Anotacion::getTipo)
                .containsExactlyInAnyOrder(TipoAnotacion.NEGATIVA, TipoAnotacion.POSITIVA);
    }

    @Test
    @DisplayName("buscarPorId - retorna anotación cuando el id existe")
    void buscarPorId_existente_retornaAnotacion() {
        when(repo.findById(1L)).thenReturn(Optional.of(anotacionNegativa));

        Anotacion resultado = service.buscarPorId(1L);

        assertThat(resultado.getDescripcion()).isEqualTo("Mal comportamiento en clase");
        assertThat(resultado.getTipo()).isEqualTo(TipoAnotacion.NEGATIVA);
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException si no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Anotacion no encontrada: 99");
    }

    @Test
    @DisplayName("buscarPorEstudiante - retorna anotaciones del estudiante indicado")
    void buscarPorEstudiante_retornaAnotacionesDelEstudiante() {
        when(repo.findByEstudianteId(10L)).thenReturn(List.of(anotacionNegativa, anotacionPositiva));

        List<Anotacion> resultado = service.buscarPorEstudiante(10L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(a -> a.getEstudianteId().equals(10L));
    }

    @Test
    @DisplayName("guardar NEGATIVA - valida estudiante via Feign y publica evento RabbitMQ")
    void guardar_anotacionNegativa_validaFeignYPublicaEvento() {
        when(academicoClient.buscarEstudiantePorId(anyLong())).thenReturn(null);
        when(repo.save(any(Anotacion.class))).thenReturn(anotacionNegativa);
        when(academicoClient.buscarApoderadosPorEstudiante(anyLong()))
                .thenReturn(List.of(Map.of("id", 1L)));
        doNothing().when(eventPublisher).publicar(any(AnotacionEvent.class));

        Anotacion resultado = service.guardar(anotacionNegativa);

        verify(academicoClient, times(1)).buscarEstudiantePorId(10L);
        verify(eventPublisher, times(1)).publicar(any(AnotacionEvent.class));
        assertThat(resultado.getTipo()).isEqualTo(TipoAnotacion.NEGATIVA);
    }

    @Test
    @DisplayName("guardar POSITIVA - valida estudiante via Feign pero NO publica evento")
    void guardar_anotacionPositiva_noPublicaEvento() {
        when(academicoClient.buscarEstudiantePorId(anyLong())).thenReturn(null);
        when(repo.save(any(Anotacion.class))).thenReturn(anotacionPositiva);

        service.guardar(anotacionPositiva);

        verify(academicoClient, times(1)).buscarEstudiantePorId(10L);
        verify(eventPublisher, never()).publicar(any(AnotacionEvent.class));
    }

    @Test
    @DisplayName("guardar - lanza excepción cuando Feign indica que el estudiante no existe")
    void guardar_estudianteNoExiste_lanzaExcepcion() {
        doThrow(FeignException.NotFound.class)
                .when(academicoClient).buscarEstudiantePorId(10L);

        assertThatThrownBy(() -> service.guardar(anotacionNegativa))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Estudiante no encontrado con id: 10");

        verify(repo, never()).save(any());
        verify(eventPublisher, never()).publicar(any());
    }

    @Test
    @DisplayName("guardar - continúa si Feign falla por indisponibilidad del servicio")
    void guardar_feignNoDisponible_continuaGuardando() {
        doThrow(new RuntimeException("Connection refused"))
                .when(academicoClient).buscarEstudiantePorId(10L);
        when(repo.save(any(Anotacion.class))).thenReturn(anotacionPositiva);

        Anotacion resultado = service.guardar(anotacionPositiva);

        assertThat(resultado).isNotNull();
        verify(repo, times(1)).save(anotacionPositiva);
    }

    @Test
    @DisplayName("guardar NEGATIVA - el evento publicado contiene los datos correctos")
    void guardar_eventoPublicado_contieneEstudianteYDescripcion() {
        when(academicoClient.buscarEstudiantePorId(anyLong())).thenReturn(null);
        when(repo.save(any(Anotacion.class))).thenReturn(anotacionNegativa);
        when(academicoClient.buscarApoderadosPorEstudiante(anyLong()))
                .thenReturn(List.of(Map.of("id", 1L)));

        ArgumentCaptor<AnotacionEvent> captor = ArgumentCaptor.forClass(AnotacionEvent.class);
        doNothing().when(eventPublisher).publicar(captor.capture());

        service.guardar(anotacionNegativa);

        AnotacionEvent eventoCapturado = captor.getValue();
        assertThat(eventoCapturado.getEstudianteId()).isEqualTo(10L);
        assertThat(eventoCapturado.getDescripcion()).isEqualTo("Mal comportamiento en clase");
        assertThat(eventoCapturado.getTipo()).isEqualTo("NEGATIVA");
        assertThat(eventoCapturado.getApoderadoId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("actualizar - modifica descripción y tipo de la anotación")
    void actualizar_modificaCampos() {
        Anotacion datosNuevos = Anotacion.builder()
                .descripcion("Comportamiento mejorado").tipo(TipoAnotacion.POSITIVA)
                .fecha(LocalDate.of(2025, 4, 16)).profesorId(3L)
                .build();
        Anotacion actualizada = Anotacion.builder()
                .id(1L).estudianteId(10L).profesorId(3L)
                .descripcion("Comportamiento mejorado").tipo(TipoAnotacion.POSITIVA)
                .fecha(LocalDate.of(2025, 4, 16)).build();

        when(repo.findById(1L)).thenReturn(Optional.of(anotacionNegativa));
        when(repo.save(any(Anotacion.class))).thenReturn(actualizada);

        Anotacion resultado = service.actualizar(1L, datosNuevos);

        assertThat(resultado.getDescripcion()).isEqualTo("Comportamiento mejorado");
        assertThat(resultado.getTipo()).isEqualTo(TipoAnotacion.POSITIVA);
        verify(repo).save(any(Anotacion.class));
    }

    @Test
    @DisplayName("eliminar - invoca deleteById con el id correcto")
    void eliminar_invocaDeleteById() {
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);

        verify(repo, times(1)).deleteById(1L);
    }
}