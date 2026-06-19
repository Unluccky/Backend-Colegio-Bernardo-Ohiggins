package com.colegio.servicio_asistencia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9090/asistencia")
                                .description("Servicio Asistencia - a través del API Gateway")
                ))
                .info(new Info()
                        .title("Servicio Asistencia API")
                        .description("API para gestión de asistencias y anotaciones del Colegio Bernardo O'Higgins")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Colegio Bernardo O'Higgins")
                                .email("contacto@colegiobernardoohiggins.cl"))
                        .license(new License()
                                .name("© 2025 Colegio Bernardo O'Higgins")));
    }
}
