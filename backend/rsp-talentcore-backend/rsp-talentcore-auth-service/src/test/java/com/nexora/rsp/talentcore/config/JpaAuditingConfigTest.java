package com.nexora.rsp.talentcore.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JpaAuditingConfigTest {

    private final JpaAuditingConfig config = new JpaAuditingConfig();

    @AfterEach
    void clearSecurityContext() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void auditorProviderReturnsSystemWhenAuthenticationIsMissing() {

        AuditorAware<String> auditorAware = config.auditorProvider();

        assertThat(auditorAware.getCurrentAuditor()).contains("SYSTEM");
    }

    @Test
    void auditorProviderReturnsUserPrincipalEmailWhenAuthenticated() {

        UserPrincipal principal = UserPrincipal.builder()
                .userId(1L)
                .email("murali@test.com")
                .roles(List.of("EMPLOYEE"))
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, List.of()));

        AuditorAware<String> auditorAware = config.auditorProvider();

        assertThat(auditorAware.getCurrentAuditor()).contains("murali@test.com");
    }

    @Test
    void auditorProviderReturnsSystemWhenAuthenticationIsNotAuthenticated() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("murali@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuditorAware<String> auditorAware = config.auditorProvider();

        assertThat(auditorAware.getCurrentAuditor()).contains("SYSTEM");
    }

    @Test
    void auditorProviderReturnsSystemWhenPrincipalIsNotUserPrincipal() {

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("murali@test.com", null, List.of()));

        AuditorAware<String> auditorAware = config.auditorProvider();

        assertThat(auditorAware.getCurrentAuditor()).contains("SYSTEM");
    }

    @Test
    void auditorProviderReturnsSystemWhenUserPrincipalEmailIsBlank() {

        UserPrincipal principal = UserPrincipal.builder()
                .userId(1L)
                .email(" ")
                .roles(List.of("EMPLOYEE"))
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, List.of()));

        AuditorAware<String> auditorAware = config.auditorProvider();

        assertThat(auditorAware.getCurrentAuditor()).contains("SYSTEM");
    }

    @Test
    void logAuditingInitializedCompletesWithoutError() {

        config.logAuditingInitialized();
    }
}
