package com.colegio.servicio_comunicaciones.service.impl;

import com.colegio.servicio_comunicaciones.model.Notificacion;
import com.colegio.servicio_comunicaciones.model.TipoNotificacion;
import com.colegio.servicio_comunicaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionService - Pruebas Unitarias")
class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository repo;

    @InjectMocks
    private NotificacionServiceImpl service;

    private Notificacion notificacionBase;

    @BeforeEach
    void setUp() {
        notificacionBase = Notificacion.builder()
                .id("notif-001")
                .destinatarioId(2L)
                .titulo("Anotación negativa registrada")
                .mensaje("Su pupilo recibió una anotación negativa")
                .fecha(LocalDateTime.of(2025, 5, 1, 9, 0))
                .leida(false)
                .tipo(TipoNotificacion.ANOTACION)
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todas las notificaciones")
    void listarTodos_retornaListaCompleta() {
        Notificacion otra = Notificacion.builder()
                .id("notif-002").destinatarioId(3L)
                .titulo("Inasistencia registrada").mensaje("Faltó a clases")
                .fecha(LocalDateTime.now()).leida(true)
                .tipo(TipoNotificacion.ASISTENCIA).build();
        when(repo.findAll()).thenReturn(List.of(notificacionBase, otra));

        List<Notificacion> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Notificacion::getTipo)
                .containsExactly(TipoNotificacion.ANOTACION, TipoNotificacion.ASISTENCIA);
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId - retorna notificacion cuando el id existe")
    void buscarPorId_existente_retornaNotificacion() {
        when(repo.findById("notif-001")).thenReturn(Optional.of(notificacionBase));

        Notificacion resultado = service.buscarPorId("notif-001");

        assertThat(resultado.getTitulo()).isEqualTo("Anotación negativa registrada");
        assertThat(resultado.getLeida()).isFalse();
        assertThat(resultado.getTipo()).isEqualTo(TipoNotificacion.ANOTACION);
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException cuando no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("no-existe"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Notificacion no encontrada: no-existe");
    }

    @Test
    @DisplayName("buscarPorDestinatario - retorna notificaciones del destinatario indicado")
    void buscarPorDestinatario_retornaNotificacionesDelDestinatario() {
        when(repo.findByDestinatarioId(2L)).thenReturn(List.of(notificacionBase));

        List<Notificacion> resultado = service.buscarPorDestinatario(2L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDestinatarioId()).isEqualTo(2L);
        verify(repo).findByDestinatarioId(2L);
    }

    @Test
    @DisplayName("buscarPorDestinatario - retorna vacío si no tiene notificaciones")
    void buscarPorDestinatario_sinNotificaciones_retornaVacio() {
        when(repo.findByDestinatarioId(99L)).thenReturn(List.of());

        List<Notificacion> resultado = service.buscarPorDestinatario(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("guardar - persiste y retorna la notificacion guardada")
    void guardar_retornaNotificacionGuardada() {
        Notificacion nueva = Notificacion.builder()
                .destinatarioId(5L).titulo("Nueva nota registrada")
                .mensaje("Se registró una nota en Matemáticas")
                .fecha(LocalDateTime.now()).leida(false)
                .tipo(TipoNotificacion.NOTA).build();
        when(repo.save(any(Notificacion.class))).thenReturn(notificacionBase);

        Notificacion resultado = service.guardar(nueva);

        assertThat(resultado.getId()).isEqualTo("notif-001");
        verify(repo, times(1)).save(nueva);
    }

    @Test
    @DisplayName("eliminar - invoca deleteById con el id correcto")
    void eliminar_invocaDeleteById() {
        doNothing().when(repo).deleteById("notif-001");

        service.eliminar("notif-001");

        verify(repo, times(1)).deleteById("notif-001");
    }
}