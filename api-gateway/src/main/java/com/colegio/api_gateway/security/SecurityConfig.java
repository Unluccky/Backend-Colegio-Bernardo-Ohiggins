package com.colegio.api_gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // Preflight OPTIONS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Login público
                .requestMatchers("/auth/login").permitAll()

                // Cambio de contraseña (autenticado)
                .requestMatchers(HttpMethod.POST, "/auth/cambiar-contrasena").authenticated()

                // Reset de contraseña (público para olvidé mi contraseña; UTP también puede usarlo autenticado)
                .requestMatchers(HttpMethod.POST, "/auth/resetear-contrasena").permitAll()

                // Swagger/OpenAPI público
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/academico/swagger-ui/**", "/academico/v3/api-docs/**").permitAll()
                .requestMatchers("/asistencia/swagger-ui/**", "/asistencia/v3/api-docs/**").permitAll()
                .requestMatchers("/comunicaciones/swagger-ui/**", "/comunicaciones/v3/api-docs/**").permitAll()

                // ── UTP (admin: POST/DELETE solo UTP; GET: cualquier rol autenticado) ──
                .requestMatchers(HttpMethod.GET, "/admin/usuarios-utp")
                    .authenticated()
                .requestMatchers("/admin/**")
                    .hasRole("UTP")

                // ── Estudiantes (lectura: todos los roles; escritura: solo UTP) ──
                .requestMatchers(HttpMethod.GET, "/academico/api/estudiantes/**")
                    .hasAnyRole("UTP", "PROFESOR", "ALUMNO", "APODERADO")
                .requestMatchers("/academico/api/estudiantes/**")
                    .hasRole("UTP")

                // ── Profesores (lectura: todos; escritura: UTP) ──────────
                .requestMatchers(HttpMethod.GET, "/academico/api/profesores/**")
                    .hasAnyRole("UTP", "PROFESOR", "ALUMNO", "APODERADO")
                .requestMatchers("/academico/api/profesores/**")
                    .hasRole("UTP")

                // ── Apoderados (lectura: todos; escritura: UTP) ──────────
                .requestMatchers(HttpMethod.GET, "/academico/api/apoderados/**")
                    .hasAnyRole("UTP", "PROFESOR", "ALUMNO", "APODERADO")
                .requestMatchers("/academico/api/apoderados/**")
                    .hasRole("UTP")

                // ── Notas (lectura: todos; escritura: UTP, PROFESOR) ────
                .requestMatchers(HttpMethod.GET, "/academico/api/notas/**")
                    .hasAnyRole("UTP", "PROFESOR", "ALUMNO", "APODERADO")
                .requestMatchers("/academico/api/notas/**")
                    .hasAnyRole("UTP", "PROFESOR")

                // ── Evaluaciones (lectura: todos; escritura: UTP, PROFESOR) ─
                .requestMatchers(HttpMethod.GET, "/academico/api/evaluaciones/**")
                    .hasAnyRole("UTP", "PROFESOR", "ALUMNO", "APODERADO")
                .requestMatchers("/academico/api/evaluaciones/**")
                    .hasAnyRole("UTP", "PROFESOR")

                // ── Asignaturas (lectura: todos; escritura: UTP) ────────
                .requestMatchers(HttpMethod.GET, "/academico/api/asignaturas/**")
                    .hasAnyRole("UTP", "PROFESOR", "ALUMNO", "APODERADO")
                .requestMatchers("/academico/api/asignaturas/**")
                    .hasRole("UTP")

                // ── Horarios (lectura: todos; escritura: UTP) ───────────
                .requestMatchers(HttpMethod.GET, "/academico/api/horarios/**")
                    .hasAnyRole("UTP", "PROFESOR", "ALUMNO", "APODERADO")
                .requestMatchers("/academico/api/horarios/**")
                    .hasRole("UTP")

                // ── Resto académico (catch-all: solo UTP) ───────────────
                .requestMatchers("/academico/**")
                    .hasRole("UTP")

                // ── Asistencia (lectura: todos; escritura: UTP, PROFESOR) ─
                .requestMatchers(HttpMethod.GET, "/asistencia/**")
                    .hasAnyRole("UTP", "PROFESOR", "ALUMNO", "APODERADO")
                .requestMatchers("/asistencia/**")
                    .hasAnyRole("UTP", "PROFESOR")

                // ── Comunicaciones ────────────────────────────────────
                .requestMatchers("/comunicaciones/**")
                    .authenticated()

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}