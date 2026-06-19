package com.colegio.api_gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ─────────────────────────────────────────────
        // PERMITIR PREFLIGHT CORS
        // ─────────────────────────────────────────────
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // ─────────────────────────────────────────────
        // RUTAS PÚBLICAS (sin token)
        // ─────────────────────────────────────────────
        if (path.startsWith("/auth/login") ||
            path.startsWith("/auth/resetear-contrasena") ||
            path.equals("/swagger-ui.html") ||
            path.startsWith("/swagger-ui/") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/academico/swagger-ui/") ||
            path.startsWith("/academico/v3/api-docs") ||
            path.startsWith("/asistencia/swagger-ui/") ||
            path.startsWith("/asistencia/v3/api-docs") ||
            path.startsWith("/comunicaciones/swagger-ui/") ||
            path.startsWith("/comunicaciones/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // ─────────────────────────────────────────────
        // TOKEN NO ENVIADO
        // ─────────────────────────────────────────────
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Token no proporcionado");
            return;
        }

        String token = authHeader.substring(7);

        // ─────────────────────────────────────────────
        // TOKEN INVÁLIDO
        // ─────────────────────────────────────────────
        if (!jwtUtil.isTokenValid(token)) {
            sendUnauthorized(response, "Token inválido o expirado");
            return;
        }

        String rut = jwtUtil.extractRut(token);
        String role = jwtUtil.extractRole(token);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        rut,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response,
                                  String mensaje) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"error\": \"" + mensaje + "\"}"
        );
    }
}