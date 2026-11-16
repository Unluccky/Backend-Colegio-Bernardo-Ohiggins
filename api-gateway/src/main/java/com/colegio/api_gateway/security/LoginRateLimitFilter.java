package com.colegio.api_gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Límite básico por IP para reducir intentos automatizados de contraseña. */
final class LoginRateLimitFilter extends OncePerRequestFilter {

    static final int MAX_ATTEMPTS = 5;
    static final long WINDOW_MILLIS = 60_000L;
    private static final int MAX_TRACKED_IPS = 10_000;

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicLong attempts = new AtomicLong();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod()) || !"/auth/login".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long now = System.currentTimeMillis();
        if (attempts.incrementAndGet() % 128 == 0) {
            windows.entrySet().removeIf(entry -> now - entry.getValue().startedAt >= WINDOW_MILLIS);
        }

        String address = request.getRemoteAddr();
        Window window = windows.get(address);
        if (window == null) {
            if (windows.size() >= MAX_TRACKED_IPS) {
                reject(response, 60);
                return;
            }
            Window candidate = new Window(now);
            Window previous = windows.putIfAbsent(address, candidate);
            window = previous == null ? candidate : previous;
        }

        synchronized (window) {
            if (now - window.startedAt >= WINDOW_MILLIS) {
                window.startedAt = now;
                window.count = 0;
            }
            if (window.count >= MAX_ATTEMPTS) {
                long seconds = Math.max(1, (WINDOW_MILLIS - (now - window.startedAt) + 999) / 1000);
                reject(response, seconds);
                return;
            }
            window.count++;
        }
        chain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, long retryAfterSeconds) throws IOException {
        response.setStatus(429);
        response.setHeader("Retry-After", Long.toString(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"Demasiados intentos. Intenta nuevamente más tarde.\"}");
    }

    private static final class Window {
        private long startedAt;
        private int count;

        private Window(long startedAt) {
            this.startedAt = startedAt;
        }
    }
}
