package com.colegio.servicio_comunicaciones.messaging;

import com.colegio.servicio_comunicaciones.model.Notificacion;
import com.colegio.servicio_comunicaciones.model.TipoNotificacion;
import com.colegio.servicio_comunicaciones.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnotacionEventConsumer - Pruebas Unitarias")
class AnotacionEventConsumerTest {

    @Mock
    private NotificacionService notificacionService;

    private AnotacionEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new AnotacionEventConsumer(notificacionService);
    }

    @Test
    @DisplayName("procesarAnotacionNegativa - notifica al apoderado cuando existe")
    void procesar_conApoderado_debeNotificarAlApoderado() {
        AnotacionEvent evento = AnotacionEvent.builder()
                .estudianteId(10L)
                .profesorId(3L)
                .descripcion("Mal comportamiento")
                .tipo("NEGATIVA")
                .fecha(LocalDate.now())
                .apoderadoId(1L)
                .build();

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        when(notificacionService.guardar(any(Notificacion.class))).thenReturn(null);

        consumer.procesarAnotacionNegativa(evento);

        verify(notificacionService, times(1)).guardar(captor.capture());
        Notificacion notif = captor.getValue();

        assertThat(notif.getDestinatarioId()).isEqualTo(1L); // apoderadoId
        assertThat(notif.getTitulo()).contains("Anotación negativa");
        assertThat(notif.getMensaje()).contains("Mal comportamiento");
        assertThat(notif.getTipo()).isEqualTo(TipoNotificacion.ANOTACION);
        assertThat(notif.getLeida()).isFalse();
    }

    @Test
    @DisplayName("procesarAnotacionNegativa - notifica al estudiante si no hay apoderado")
    void procesar_sinApoderado_debeNotificarAlEstudiante() {
        AnotacionEvent evento = AnotacionEvent.builder()
                .estudianteId(10L)
                .profesorId(3L)
                .descripcion("Llegó tarde")
                .tipo("NEGATIVA")
                .fecha(LocalDate.now())
                .apoderadoId(null) // sin apoderado
                .build();

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        when(notificacionService.guardar(any(Notificacion.class))).thenReturn(null);

        consumer.procesarAnotacionNegativa(evento);

        verify(notificacionService, times(1)).guardar(captor.capture());
        Notificacion notif = captor.getValue();

        assertThat(notif.getDestinatarioId()).isEqualTo(10L); // estudianteId (fallback)
        assertThat(notif.getTitulo()).contains("Anotación negativa");
    }
}
