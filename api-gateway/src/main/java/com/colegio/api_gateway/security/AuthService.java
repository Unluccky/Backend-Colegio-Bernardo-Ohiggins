package com.colegio.api_gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${academico.service.url:http://servicio-academico:8081}")
    private String academicoServiceUrl;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Mapa de usuarios persistido en memoria.
     * UTP, PROFESOR, ALUMNO y APODERADO pueden autenticarse localmente
     * sin necesidad del Servicio Académico (útil para desarrollo).
     *
     * En producción, PROFESOR, ALUMNO y APODERADO se validan
     * contra el Servicio Académico.
     */
    private static final Map<String, String[]> USUARIOS = new ConcurrentHashMap<>();

    static {
        // —— UTP ——
        USUARIOS.put("77777777-7", new String[]{
                passwordEncoder.encode("utp123"), "UTP"});
        USUARIOS.put("88888888-8", new String[]{
                passwordEncoder.encode("utp123"), "UTP"});
        USUARIOS.put("99999999-9", new String[]{
                passwordEncoder.encode("utp123"), "UTP"});

        // —— Profesores ——
        USUARIOS.put("11111111-1", new String[]{
                passwordEncoder.encode("profesor123"), "PROFESOR"});
        USUARIOS.put("22222222-2", new String[]{
                passwordEncoder.encode("profesor123"), "PROFESOR"});
        USUARIOS.put("66666666-6", new String[]{
                passwordEncoder.encode("profesor123"), "PROFESOR"});
        USUARIOS.put("12121212-1", new String[]{
                passwordEncoder.encode("profesor123"), "PROFESOR"});
        USUARIOS.put("13131313-3", new String[]{
                passwordEncoder.encode("profesor123"), "PROFESOR"});
        USUARIOS.put("14141414-4", new String[]{
                passwordEncoder.encode("profesor123"), "PROFESOR"});
        USUARIOS.put("15151515-5", new String[]{
                passwordEncoder.encode("profesor123"), "PROFESOR"});

        // —— Alumnos (cursos 1-4) ——
        USUARIOS.put("33333333-3", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("44444444-4", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("16161616-6", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("17171717-7", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("18181818-8", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("19191919-9", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("24242424-4", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("25252525-5", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});

        // —— Alumnos (cursos 5-12) — sincronizados con DataSeeder ——
        USUARIOS.put("27272727-7", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("28282828-8", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("29292929-9", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("30303030-0", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("31313131-1", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("32323232-2", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("34343434-4", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("35353535-5", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("36363636-6", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("37373737-7", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("38383838-8", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("39393939-9", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("40404040-0", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("41414141-1", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("42424242-2", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});
        USUARIOS.put("43434343-3", new String[]{
                passwordEncoder.encode("alumno123"), "ALUMNO"});

        // —— Apoderados (cursos 1-4) ——
        USUARIOS.put("55555555-5", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("20202020-0", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("21212121-1", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("23232323-3", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("26262626-6", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});

        // —— Apoderados (cursos 5-12) — sincronizados con DataSeeder ——
        USUARIOS.put("53535353-3", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("45454545-5", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("46464646-6", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("47474747-7", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("48484848-8", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("49494949-9", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("50505050-0", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("51515151-1", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
        USUARIOS.put("52525252-2", new String[]{
                passwordEncoder.encode("apoderado123"), "APODERADO"});
    }

    /**
     * Autentica primero localmente (UTP), luego contra el Servicio Académico.
     */
    public Optional<Map<String, String>> autenticar(String rut, String password) {
        Optional<Map<String, String>> local = autenticarLocal(rut, password);
        if (local.isPresent()) {
            return local;
        }
        return autenticarRemoto(rut, password);
    }

    private Optional<Map<String, String>> autenticarLocal(String rut, String password) {
        String[] credenciales = USUARIOS.get(rut);
        if (credenciales == null || !passwordEncoder.matches(password, credenciales[0])) {
            return Optional.empty();
        }
        String token = jwtUtil.generateToken(rut, credenciales[1]);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("token", token);
        respuesta.put("rut", rut);
        respuesta.put("role", credenciales[1]);
        return Optional.of(respuesta);
    }

    /**
     * Consulta al Servicio Académico para validar PROFESOR, ALUMNO, APODERADO.
     * La llamada está protegida por un Circuit Breaker Resilience4J.
     * Si el servicio-academico falla o el circuito se abre, el fallback
     * retorna Optional.empty() permitiendo que el login falle gracefulmente.
     */
    private Optional<Map<String, String>> autenticarRemoto(String rut, String password) {
        Supplier<Optional<Map<String, String>>> operacion = () -> {
            String url = academicoServiceUrl + "/api/auth/validar";
            Map<String, String> request = new HashMap<>();
            request.put("rut", rut);
            request.put("password", password);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("valido"))) {
                String role = (String) response.get("role");
                String token = jwtUtil.generateToken(rut, role);

                Map<String, String> resultado = new HashMap<>();
                resultado.put("token", token);
                resultado.put("rut", rut);
                resultado.put("role", role);
                return Optional.of(resultado);
            }

            return Optional.empty();
        };

        Function<Throwable, Optional<Map<String, String>>> fallback = ex -> {
            log.warn("Circuit Breaker abierto o error en servicio-academico: {}",
                    ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName());
            return Optional.empty();
        };

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("auth-academico");
        try {
            return circuitBreaker.executeSupplier(operacion);
        } catch (Exception e) {
            return fallback.apply(e);
        }
    }

    // ========== MÉTODOS PARA GESTIÓN DE USUARIOS UTP ==========

    public List<Map<String, String>> listarUsuariosUtp() {
        List<Map<String, String>> lista = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : USUARIOS.entrySet()) {
            // Solo devolver usuarios con rol UTP
            if (!"UTP".equals(entry.getValue()[1])) continue;
            Map<String, String> user = new HashMap<>();
            user.put("rut", entry.getKey());
            user.put("role", entry.getValue()[1]);
            lista.add(user);
        }
        return lista;
    }

    public boolean existeUsuarioUtp(String rut) {
        return USUARIOS.containsKey(rut);
    }

    public Map<String, String> crearUsuarioUtp(String rut, String password) {
        String hash = passwordEncoder.encode(password);
        USUARIOS.put(rut, new String[]{hash, "UTP"});
        Map<String, String> user = new HashMap<>();
        user.put("rut", rut);
        user.put("role", "UTP");
        return user;
    }

    public boolean eliminarUsuarioUtp(String rut) {
        return USUARIOS.remove(rut) != null;
    }

    // ========== CAMBIO DE CONTRASEÑA ==========

    /**
     * Cambia la contraseña de un usuario.
     * Para UTP: se actualiza localmente.
     * Para PROFESOR/ALUMNO/APODERADO: se delega al Servicio Académico.
     */
    public Map<String, Object> cambiarContrasena(String rut, String currentPassword, String newPassword) {
        // Intentar localmente primero (UTP)
        String[] credenciales = USUARIOS.get(rut);
        if (credenciales != null) {
            if (!passwordEncoder.matches(currentPassword, credenciales[0])) {
                return Map.of("exito", false, "error", "La contraseña actual no es correcta");
            }
            String hash = passwordEncoder.encode(newPassword);
            USUARIOS.put(rut, new String[]{hash, credenciales[1]});
            log.info("Contraseña actualizada para UTP: {}", rut);
            return Map.of("exito", true, "mensaje", "Contraseña actualizada correctamente");
        }

        // Delegar al Servicio Académico
        String url = academicoServiceUrl + "/api/auth/cambiar-contrasena";
        Map<String, String> request = new HashMap<>();
        request.put("rut", rut);
        request.put("currentPassword", currentPassword);
        request.put("newPassword", newPassword);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            return response != null ? response : Map.of("exito", false, "error", "Error al comunicarse con el servicio académico");
        } catch (Exception e) {
            log.warn("Error al cambiar contraseña en servicio-academico: {}", e.getMessage());
            return Map.of("exito", false, "error", "Error al comunicarse con el servicio académico");
        }
    }

    /**
     * Resetea la contraseña de un usuario sin verificar la actual (solo UTP).
     * Para UTP: se actualiza localmente.
     * Para otros roles: se delega al Servicio Académico.
     */
    public Map<String, Object> resetearContrasena(String rut, String newPassword) {
        // Intentar localmente primero (UTP)
        String[] credenciales = USUARIOS.get(rut);
        if (credenciales != null) {
            String hash = passwordEncoder.encode(newPassword);
            USUARIOS.put(rut, new String[]{hash, credenciales[1]});
            log.info("Contraseña reseteada para UTP: {}", rut);
            return Map.of("exito", true, "mensaje", "Contraseña reseteada correctamente");
        }

        // Delegar al Servicio Académico
        String url = academicoServiceUrl + "/api/auth/resetear-contrasena";
        Map<String, String> request = new HashMap<>();
        request.put("rut", rut);
        request.put("newPassword", newPassword);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            return response != null ? response : Map.of("exito", false, "error", "Error al comunicarse con el servicio académico");
        } catch (Exception e) {
            log.warn("Error al resetear contraseña en servicio-academico: {}", e.getMessage());
            return Map.of("exito", false, "error", "Error al comunicarse con el servicio académico");
        }
    }
}