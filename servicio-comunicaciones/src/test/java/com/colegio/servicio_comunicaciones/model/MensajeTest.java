package com.colegio.servicio_comunicaciones.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Mensaje - Pruebas Unitarias")
class MensajeTest {

    @Test
    @DisplayName("builder - crea mensaje con todos los campos")
    void builder_deberiaCrearMensajeCompleto() {
        LocalDateTime now = LocalDateTime.now();

        Mensaje mensaje = Mensaje.builder()
                .id("msg-001")
                .remitenteId(1L)
                .remitenteTipo("PROFESOR")
                .destinatarioId(2L)
                .destinatarioTipo("ESTUDIANTE")
                .asunto("Reunión")
                .contenido("Contenido del mensaje")
                .fechaEnvio(now)
                .leido(false)
                .build();

        assertThat(mensaje.getId()).isEqualTo("msg-001");
        assertThat(mensaje.getRemitenteId()).isEqualTo(1L);
        assertThat(mensaje.getRemitenteTipo()).isEqualTo("PROFESOR");
        assertThat(mensaje.getDestinatarioId()).isEqualTo(2L);
        assertThat(mensaje.getDestinatarioTipo()).isEqualTo("ESTUDIANTE");
        assertThat(mensaje.getAsunto()).isEqualTo("Reunión");
        assertThat(mensaje.getContenido()).isEqualTo("Contenido del mensaje");
        assertThat(mensaje.getFechaEnvio()).isEqualTo(now);
        assertThat(mensaje.getLeido()).isFalse();
    }

    @Test
    @DisplayName("setters - asignan valores correctamente")
    void setters_deberianAsignarValores() {
        Mensaje mensaje = new Mensaje();
        mensaje.setId("msg-002");
        mensaje.setAsunto("Aviso");
        mensaje.setLeido(true);

        assertThat(mensaje.getId()).isEqualTo("msg-002");
        assertThat(mensaje.getAsunto()).isEqualTo("Aviso");
        assertThat(mensaje.getLeido()).isTrue();
    }

    @Test
    @DisplayName("equals y hashCode - mensajes con mismos campos son iguales")
    void equals_hashCode_deberianSerConsistentes() {
        Mensaje m1 = Mensaje.builder().id("msg-001").asunto("Test").contenido("Cont").build();
        Mensaje m2 = Mensaje.builder().id("msg-001").asunto("Test").contenido("Cont").build();

        assertThat(m1).isEqualTo(m2);
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
    }

    @Test
    @DisplayName("toString - no lanza excepción")
    void toString_deberiaFuncionar() {
        Mensaje mensaje = Mensaje.builder().id("msg-001").asunto("Test").build();

        assertThat(mensaje.toString()).contains("msg-001").contains("Test");
    }
}
