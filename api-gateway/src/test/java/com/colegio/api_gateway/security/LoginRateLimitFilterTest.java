package com.colegio.api_gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRateLimitFilterTest {

    @Test
    void allowsFiveRequestsThenReturns429ForSameIp() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter();
        AtomicInteger forwarded = new AtomicInteger();
        FilterChain chain = (ServletRequest request, ServletResponse response) -> forwarded.incrementAndGet();

        for (int i = 0; i < LoginRateLimitFilter.MAX_ATTEMPTS; i++) {
            MockHttpServletResponse response = invoke(filter, chain, "192.0.2.1");
            assertThat(response.getStatus()).isEqualTo(200);
        }
        MockHttpServletResponse limited = invoke(filter, chain, "192.0.2.1");

        assertThat(forwarded).hasValue(LoginRateLimitFilter.MAX_ATTEMPTS);
        assertThat(limited.getStatus()).isEqualTo(429);
        assertThat(limited.getHeader("Retry-After")).isNotBlank();
    }

    @Test
    void tracksDifferentIpsSeparately() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter();
        AtomicInteger forwarded = new AtomicInteger();
        FilterChain chain = (ServletRequest request, ServletResponse response) -> forwarded.incrementAndGet();

        for (int i = 0; i < LoginRateLimitFilter.MAX_ATTEMPTS; i++) {
            invoke(filter, chain, "192.0.2.2");
        }
        MockHttpServletResponse otherIp = invoke(filter, chain, "192.0.2.3");

        assertThat(otherIp.getStatus()).isEqualTo(200);
        assertThat(forwarded).hasValue(LoginRateLimitFilter.MAX_ATTEMPTS + 1);
    }

    private MockHttpServletResponse invoke(LoginRateLimitFilter filter, FilterChain chain, String address)
            throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/login");
        request.setRemoteAddr(address);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        return response;
    }
}
