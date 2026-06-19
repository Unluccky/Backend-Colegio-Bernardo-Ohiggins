package com.colegio.servicio_comunicaciones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Servicio Comunicaciones API")
                        .description("API para gestión de mensajes y notificaciones del Colegio Bernardo O'Higgins")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Colegio Bernardo O'Higgins")
                                .email("contacto@colegiobernardoohiggins.cl"))
                        .license(new License()
                                .name("© 2025 Colegio Bernardo O'Higgins")));
    }
}
