package com.nexora.rsp.talentcore.config;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter correlationIdFilter = new CorrelationIdFilter();

    @Test
    void doFilterUsesIncomingCorrelationIdAndClearsMdc() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> correlationIdFromChain = new AtomicReference<>();

        request.addHeader("X-Correlation-Id", " inbound-id ");

        FilterChain filterChain = (servletRequest, servletResponse) ->
                correlationIdFromChain.set(MDC.get("correlationId"));

        correlationIdFilter.doFilter(request, response, filterChain);

        assertThat(correlationIdFromChain).hasValue("inbound-id");
        assertThat(response.getHeader("X-Correlation-Id")).isEqualTo("inbound-id");
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void doFilterGeneratesCorrelationIdWhenHeaderIsBlank() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> correlationIdFromChain = new AtomicReference<>();

        request.addHeader("X-Correlation-Id", "   ");

        FilterChain filterChain = (servletRequest, servletResponse) ->
                correlationIdFromChain.set(MDC.get("correlationId"));

        correlationIdFilter.doFilter(request, response, filterChain);

        assertThat(correlationIdFromChain.get()).isNotBlank();
        assertThat(response.getHeader("X-Correlation-Id"))
                .isEqualTo(correlationIdFromChain.get());
        assertThat(MDC.get("correlationId")).isNull();
    }
}
