package com.colegio.servicio_asistencia.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnotacionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicar(AnotacionEvent evento) {
        log.info("Publicando evento anotacion negativa - estudianteId: {}", evento.getEstudianteId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                evento
        );
    }
}