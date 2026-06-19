package com.colegio.servicio_asistencia.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnotacionEventPublisher - Pruebas Unitarias")
class AnotacionEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private AnotacionEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new AnotacionEventPublisher(rabbitTemplate);
    }

    @Test
    @DisplayName("publicar - envía evento a RabbitMQ con exchange y routing key correctos")
    void publicar_deberiaEnviarEvento() {
        AnotacionEvent evento = AnotacionEvent.builder()
                .estudianteId(10L)
                .profesorId(3L)
                .descripcion("Anotación de prueba")
                .tipo("NEGATIVA")
                .fecha(LocalDate.now())
                .apoderadoId(1L)
                .build();

        publisher.publicar(evento);

        verify(rabbitTemplate, times(1)).convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                evento
        );
    }
}
