package com.colegio.servicio_comunicaciones.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Notificacion - Pruebas Unitarias")
class NotificacionTest {

    @Test
    @DisplayName("builder - crea notificación con todos los campos")
    void builder_deberiaCrearNotificacionCompleta() {
        LocalDateTime now = LocalDateTime.now();

        Notificacion notif = Notificacion.builder()
                .id("notif-001")
                .destinatarioId(2L)
                .titulo("Anotación negativa")
                .mensaje("Se registró una anotación")
                .fecha(now)
                .leida(false)
                .tipo(TipoNotificacion.ANOTACION)
                .build();

        assertThat(notif.getId()).isEqualTo("notif-001");
        assertThat(notif.getDestinatarioId()).isEqualTo(2L);
        assertThat(notif.getTitulo()).isEqualTo("Anotación negativa");
        assertThat(notif.getMensaje()).isEqualTo("Se registró una anotación");
        assertThat(notif.getFecha()).isEqualTo(now);
        assertThat(notif.getLeida()).isFalse();
        assertThat(notif.getTipo()).isEqualTo(TipoNotificacion.ANOTACION);
    }

    @Test
    @DisplayName("setters - asignan valores correctamente")
    void setters_deberianAsignarValores() {
        Notificacion notif = new Notificacion();
        notif.setId("notif-002");
        notif.setTitulo("Aviso");
        notif.setLeida(true);

        assertThat(notif.getId()).isEqualTo("notif-002");
        assertThat(notif.getTitulo()).isEqualTo("Aviso");
        assertThat(notif.getLeida()).isTrue();
    }

    @Test
    @DisplayName("equals y hashCode - notificaciones con mismos campos son iguales")
    void equals_hashCode_deberianSerConsistentes() {
        Notificacion n1 = Notificacion.builder().id("notif-001").titulo("Test").build();
        Notificacion n2 = Notificacion.builder().id("notif-001").titulo("Test").build();

        assertThat(n1).isEqualTo(n2);
        assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
    }

    @Test
    @DisplayName("toString - no lanza excepción")
    void toString_deberiaFuncionar() {
        Notificacion notif = Notificacion.builder().id("notif-001").titulo("Test").build();

        assertThat(notif.toString()).contains("notif-001").contains("Test");
    }
}
