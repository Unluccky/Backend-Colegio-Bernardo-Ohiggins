package com.colegio.api_gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                                .url("http://localhost:9090")
                                .description("API Gateway - Desarrollo local")
                ))
                .info(new Info()
                        .title("API Gateway - Colegio Bernardo O'Higgins")
                        .description("""
                                API Gateway central que expone los microservicios del sistema escolar.
                                
                                ## Autenticación
                                Todos los endpoints protegidos requieren un token JWT en el header `Authorization: Bearer <token>`.
                                
                                1. Usa el endpoint `POST /auth/login` con RUT y contraseña para obtener un token.
                                2. Haz clic en **Authorize** (arriba a la derecha) y pega el token.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Colegio Bernardo O'Higgins")
                                .email("contacto@colegiobernardoohiggins.cl"))
                        .license(new License()
                                .name("© 2025 Colegio Bernardo O'Higgins")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa tu token JWT obtenido de /auth/login")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
