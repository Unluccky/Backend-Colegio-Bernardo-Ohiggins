package com.colegio.api_gateway.config;

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
                        .title("API Gateway - Colegio Bernardo O'Higgins")
                        .description("API Gateway central que expone los microservicios del sistema escolar. Autenticación mediante JWT.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Colegio Bernardo O'Higgins")
                                .email("contacto@colegiobernardoohiggins.cl"))
                        .license(new License()
                                .name("© 2025 Colegio Bernardo O'Higgins")));
    }
}
