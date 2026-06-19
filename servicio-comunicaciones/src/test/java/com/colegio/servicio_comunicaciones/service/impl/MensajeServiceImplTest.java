package com.colegio.servicio_comunicaciones.service.impl;

import com.colegio.servicio_comunicaciones.model.Mensaje;
import com.colegio.servicio_comunicaciones.repository.MensajeRepository;
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
@DisplayName("MensajeService - Pruebas Unitarias")
class MensajeServiceImplTest {

    @Mock
    private MensajeRepository repo;

    @InjectMocks
    private MensajeServiceImpl service;

    private Mensaje mensajeBase;

    @BeforeEach
    void setUp() {
        mensajeBase = Mensaje.builder()
                .id("msg-001")
                .remitenteId(1L)
                .remitenteTipo("PROFESOR")
                .destinatarioId(2L)
                .destinatarioTipo("ESTUDIANTE")
                .asunto("Reunión de apoderados")
                .contenido("Se convoca a reunión el día viernes")
                .fechaEnvio(LocalDateTime.of(2025, 5, 1, 10, 0))
                .leido(false)
                .build();
    }

    @Test
    @DisplayName("listarTodos - retorna todos los mensajes")
    void listarTodos_retornaListaCompleta() {
        Mensaje otro = Mensaje.builder()
                .id("msg-002").remitenteId(3L).destinatarioId(2L)
                .asunto("Citación").contenido("Favor presentarse")
                .fechaEnvio(LocalDateTime.now()).leido(true).build();
        when(repo.findAll()).thenReturn(List.of(mensajeBase, otro));

        List<Mensaje> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Mensaje::getAsunto)
                .containsExactly("Reunión de apoderados", "Citación");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("listarTodos - retorna lista vacía cuando no hay mensajes")
    void listarTodos_sinMensajes_retornaListaVacia() {
        when(repo.findAll()).thenReturn(List.of());

        List<Mensaje> resultado = service.listarTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId - retorna mensaje cuando el id existe")
    void buscarPorId_existente_retornaMensaje() {
        when(repo.findById("msg-001")).thenReturn(Optional.of(mensajeBase));

        Mensaje resultado = service.buscarPorId("msg-001");

        assertThat(resultado.getAsunto()).isEqualTo("Reunión de apoderados");
        assertThat(resultado.getLeido()).isFalse();
        assertThat(resultado.getDestinatarioId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException cuando no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(repo.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("no-existe"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mensaje no encontrado: no-existe");
    }

    @Test
    @DisplayName("buscarPorDestinatario - retorna mensajes del destinatario indicado")
    void buscarPorDestinatario_retornaMensajesDelDestinatario() {
        when(repo.findByDestinatarioId(2L)).thenReturn(List.of(mensajeBase));

        List<Mensaje> resultado = service.buscarPorDestinatario(2L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDestinatarioId()).isEqualTo(2L);
        verify(repo).findByDestinatarioId(2L);
    }

    @Test
    @DisplayName("buscarPorDestinatario - retorna lista vacía si no tiene mensajes")
    void buscarPorDestinatario_sinMensajes_retornaVacio() {
        when(repo.findByDestinatarioId(99L)).thenReturn(List.of());

        List<Mensaje> resultado = service.buscarPorDestinatario(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorUsuario - retorna mensajes donde el usuario es remitente o destinatario")
    void buscarPorUsuario_retornaMensajesDelUsuario() {
        Mensaje comoRemitente = Mensaje.builder()
                .id("msg-003").remitenteId(1L).remitenteTipo("PROFESOR")
                .destinatarioId(3L).destinatarioTipo("ESTUDIANTE")
                .asunto("Aviso").contenido("Contenido").fechaEnvio(LocalDateTime.now()).leido(false).build();
        Mensaje comoDestinatario = Mensaje.builder()
                .id("msg-004").remitenteId(3L).remitenteTipo("ESTUDIANTE")
                .destinatarioId(1L).destinatarioTipo("PROFESOR")
                .asunto("Respuesta").contenido("Gracias").fechaEnvio(LocalDateTime.now()).leido(true).build();

        when(repo.findByUsuarioIdAndTipo(1L, "PROFESOR"))
                .thenReturn(List.of(comoRemitente, comoDestinatario));

        List<Mensaje> resultado = service.buscarPorUsuario(1L, "PROFESOR");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Mensaje::getId)
                .containsExactly("msg-003", "msg-004");
        verify(repo).findByUsuarioIdAndTipo(1L, "PROFESOR");
    }

    @Test
    @DisplayName("buscarPorUsuario - retorna lista vacía si no hay mensajes para ese usuario")
    void buscarPorUsuario_sinMensajes_retornaVacio() {
        when(repo.findByUsuarioIdAndTipo(99L, "ESTUDIANTE")).thenReturn(List.of());

        List<Mensaje> resultado = service.buscarPorUsuario(99L, "ESTUDIANTE");

        assertThat(resultado).isEmpty();
        verify(repo).findByUsuarioIdAndTipo(99L, "ESTUDIANTE");
    }

    @Test
    @DisplayName("guardar - persiste y retorna el mensaje guardado")
    void guardar_retornaMensajeGuardado() {
        Mensaje nuevo = Mensaje.builder()
                .remitenteId(1L).destinatarioId(3L)
                .asunto("Nuevo aviso").contenido("Contenido del aviso")
                .fechaEnvio(LocalDateTime.now()).leido(false).build();
        when(repo.save(any(Mensaje.class))).thenReturn(mensajeBase);

        Mensaje resultado = service.guardar(nuevo);

        assertThat(resultado.getId()).isEqualTo("msg-001");
        verify(repo, times(1)).save(nuevo);
    }

    @Test
    @DisplayName("guardar - asigna fechaEnvio automáticamente si es null")
    void guardar_sinFechaEnvio_asignaFechaActual() {
        Mensaje sinFecha = Mensaje.builder()
                .remitenteId(1L).destinatarioId(3L)
                .asunto("Sin fecha").contenido("Test")
                .leido(false)
                .build();
        // sin fechaEnvio (null)

        when(repo.save(any(Mensaje.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mensaje resultado = service.guardar(sinFecha);

        assertThat(resultado.getFechaEnvio()).isNotNull();
        assertThat(resultado.getAsunto()).isEqualTo("Sin fecha");
        verify(repo, times(1)).save(sinFecha);
    }

    @Test
    @DisplayName("actualizar - actualiza solo campos no nulos")
    void actualizar_camposNoNulosSeActualizan() {
        Mensaje datosNuevos = Mensaje.builder()
                .leido(true)
                .contenido("Contenido actualizado")
                .build();
        Mensaje actualizado = Mensaje.builder()
                .id("msg-001").remitenteId(1L).remitenteTipo("PROFESOR")
                .destinatarioId(2L).destinatarioTipo("ESTUDIANTE")
                .asunto("Reunión de apoderados")
                .contenido("Contenido actualizado")
                .fechaEnvio(LocalDateTime.of(2025, 5, 1, 10, 0))
                .leido(true).build();

        when(repo.findById("msg-001")).thenReturn(Optional.of(mensajeBase));
        when(repo.save(any(Mensaje.class))).thenReturn(actualizado);

        Mensaje resultado = service.actualizar("msg-001", datosNuevos);

        assertThat(resultado.getLeido()).isTrue();
        assertThat(resultado.getContenido()).isEqualTo("Contenido actualizado");
        // asunto no debería cambiar porque datosNuevos.getAsunto() es null
        assertThat(resultado.getAsunto()).isEqualTo("Reunión de apoderados");
        verify(repo).save(any(Mensaje.class));
    }

    @Test
    @DisplayName("actualizar - con datos vacíos mantiene campos existentes")
    void actualizar_camposNull_mantieneExistente() {
        Mensaje datosVacios = Mensaje.builder().build();

        when(repo.findById("msg-001")).thenReturn(Optional.of(mensajeBase));
        when(repo.save(any(Mensaje.class))).thenReturn(mensajeBase);

        Mensaje resultado = service.actualizar("msg-001", datosVacios);

        assertThat(resultado.getAsunto()).isEqualTo("Reunión de apoderados");
        assertThat(resultado.getLeido()).isFalse();
        assertThat(resultado.getContenido()).isEqualTo("Se convoca a reunión el día viernes");
        verify(repo).save(any(Mensaje.class));
    }

    @Test
    @DisplayName("actualizar - lanza excepción si no existe")
    void actualizar_noExiste_lanzaExcepcion() {
        when(repo.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar("no-existe", mensajeBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no-existe");
    }

    @Test
    @DisplayName("eliminar - invoca deleteById con el id correcto")
    void eliminar_invocaDeleteById() {
        doNothing().when(repo).deleteById("msg-001");

        service.eliminar("msg-001");

        verify(repo, times(1)).deleteById("msg-001");
    }
}