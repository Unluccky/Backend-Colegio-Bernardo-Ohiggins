package com.colegio.servicio_comunicaciones.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RabbitMQConfig - Pruebas Unitarias")
class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    @DisplayName("colegioExchange - debe crear TopicExchange con el nombre correcto")
    void colegioExchange_deberiaCrearExchange() {
        TopicExchange exchange = config.colegioExchange();

        assertThat(exchange).isNotNull();
        assertThat(exchange.getName()).isEqualTo(RabbitMQConfig.EXCHANGE);
    }

    @Test
    @DisplayName("notificacionesQueue - debe crear cola durable con el nombre correcto")
    void notificacionesQueue_deberiaCrearCola() {
        Queue queue = config.notificacionesQueue();

        assertThat(queue).isNotNull();
        assertThat(queue.getName()).isEqualTo(RabbitMQConfig.QUEUE);
        assertThat(queue.isDurable()).isTrue();
    }

    @Test
    @DisplayName("binding - debe enlazar cola con exchange usando routing key correcta")
    void binding_deberiaEnlazarColaYExchange() {
        TopicExchange exchange = config.colegioExchange();
        Queue queue = config.notificacionesQueue();

        Binding binding = config.binding(queue, exchange);

        assertThat(binding).isNotNull();
        assertThat(binding.getExchange()).isEqualTo(RabbitMQConfig.EXCHANGE);
        assertThat(binding.getRoutingKey()).isEqualTo(RabbitMQConfig.ROUTING_KEY);
    }

    @Test
    @DisplayName("messageConverter - debe crear JacksonJsonMessageConverter")
    void messageConverter_deberiaCrearConverter() {
        JacksonJsonMessageConverter converter = config.messageConverter();

        assertThat(converter).isNotNull();
    }
}
