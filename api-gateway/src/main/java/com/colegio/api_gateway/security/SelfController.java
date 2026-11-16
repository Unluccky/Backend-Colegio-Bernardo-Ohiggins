package com.colegio.api_gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/** Endpoints de autoservicio que resuelven la identidad desde el JWT validado. */
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class SelfController {

    private final RestTemplate restTemplate;

    @Value("${academico.service.url:http://servicio-academico:8081}")
    private String academicoServiceUrl;

    @GetMapping("/estudiante")
    public ResponseEntity<?> estudianteAutenticado() {
        String rut = rutAutenticado();
        if (rut == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }
        String url = UriComponentsBuilder.fromUriString(academicoServiceUrl)
                .pathSegment("api", "estudiantes", "rut", rut).build().encode().toUriString();
        try {
            Map<?, ?> estudiante = restTemplate.getForObject(url, Map.class);
            return estudiante == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(perfilEstudiante(estudiante));
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode().is4xxClientError() ? ex.getStatusCode() : HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "No fue posible consultar el perfil"));
        }
    }

    @GetMapping("/pupilo")
    public ResponseEntity<?> pupiloAutenticado() {
        String rut = rutAutenticado();
        if (rut == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }
        String url = UriComponentsBuilder.fromUriString(academicoServiceUrl)
                .pathSegment("api", "apoderados", "rut", rut).build().encode().toUriString();
        try {
            Map<?, ?> apoderado = restTemplate.getForObject(url, Map.class);
            if (apoderado == null || !(apoderado.get("estudiante") instanceof Map<?, ?> estudiante)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(perfilEstudiante(estudiante));
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode().is4xxClientError() ? ex.getStatusCode() : HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "No fue posible consultar el perfil del pupilo"));
        }
    }

    private String rutAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || !authentication.isAuthenticated() ? null : authentication.getName();
    }

    private Map<String, Object> perfilEstudiante(Map<?, ?> estudiante) {
        Map<String, Object> perfil = new java.util.LinkedHashMap<>();
        for (String campo : new String[]{"id", "nombre", "apellido", "rut", "curso"}) {
            if (estudiante.containsKey(campo)) {
                perfil.put(campo, estudiante.get(campo));
            }
        }
        return perfil;
    }
}
