package com.colegio.servicio_comunicaciones.messaging;

import com.colegio.servicio_comunicaciones.model.Notificacion;
import com.colegio.servicio_comunicaciones.model.TipoNotificacion;
import com.colegio.servicio_comunicaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnotacionEventConsumer {

    private final NotificacionService notificacionService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void procesarAnotacionNegativa(AnotacionEvent evento) {
        log.info("Evento recibido: anotacion negativa estudianteId={}", evento.getEstudianteId());

        // Si tiene apoderado, notificar al apoderado, si no al estudiante
        Long destinatario = evento.getApoderadoId() != null
                ? evento.getApoderadoId()
                : evento.getEstudianteId();

        Notificacion notificacion = Notificacion.builder()
                .destinatarioId(destinatario)
                .titulo("Anotación negativa registrada")
                .mensaje("Se ha registrado una anotación negativa: " + evento.getDescripcion())
                .fecha(LocalDateTime.now())
                .leida(false)
                .tipo(TipoNotificacion.ANOTACION)
                .build();

        notificacionService.guardar(notificacion);
        log.info("Notificacion creada para destinatarioId={}", destinatario);
    }
}