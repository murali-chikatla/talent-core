package com.nexora.rsp.talentcore.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSecurityExceptionHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void authenticationEntryPointWritesUnauthorizedJsonResponse() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        JsonAuthenticationEntryPoint entryPoint =
                new JsonAuthenticationEntryPoint(objectMapper);

        entryPoint.commence(
                new MockHttpServletRequest(),
                response,
                new BadCredentialsException("Bad credentials")
        );

        JsonNode body = objectMapper.readTree(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(body.get("status").asInt()).isEqualTo(401);
        assertThat(body.get("message").asText()).isEqualTo("Authentication required");
        assertThat(body.hasNonNull("timestamp")).isTrue();
    }

    @Test
    void accessDeniedHandlerWritesForbiddenJsonResponse() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        JsonAccessDeniedHandler accessDeniedHandler =
                new JsonAccessDeniedHandler(objectMapper);

        accessDeniedHandler.handle(
                new MockHttpServletRequest(),
                response,
                new AccessDeniedException("Access Denied")
        );

        JsonNode body = objectMapper.readTree(response.getContentAsString());

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(body.get("status").asInt()).isEqualTo(403);
        assertThat(body.get("message").asText()).isEqualTo("Access denied");
        assertThat(body.hasNonNull("timestamp")).isTrue();
    }
}
