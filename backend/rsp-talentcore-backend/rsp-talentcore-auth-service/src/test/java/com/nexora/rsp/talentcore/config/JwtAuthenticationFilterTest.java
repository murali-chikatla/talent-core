package com.nexora.rsp.talentcore.config;

import com.nexora.rsp.talentcore.TestDataFactory;
import com.nexora.rsp.talentcore.domain.User;
import com.nexora.rsp.talentcore.repository.UserRepository;
import com.nexora.rsp.talentcore.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void clearSecurityContext() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterSkipsAuthenticationWhenAuthorizationHeaderIsMissing() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userRepository);
    }

    @Test
    void doFilterBuildsPrincipalAndFiltersBlankRoles() throws Exception {

        MockHttpServletRequest request = bearerRequest("valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        User user = TestDataFactory.user();

        when(jwtUtil.extractEmail("valid-token")).thenReturn(user.getEmail());
        when(jwtUtil.extractUserId("valid-token")).thenReturn(user.getUserId());
        when(jwtUtil.extractRoles("valid-token"))
                .thenReturn(Arrays.asList("TEAM_MANAGER", null, "", "  EMPLOYEE  "));
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        assertThat(principal.getUserId()).isEqualTo(user.getUserId());
        assertThat(principal.getEmail()).isEqualTo(user.getEmail());
        assertThat(principal.getRoles()).containsExactly("TEAM_MANAGER", "EMPLOYEE");
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_TEAM_MANAGER", "ROLE_EMPLOYEE");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterSupportsNullRoles() throws Exception {

        MockHttpServletRequest request = bearerRequest("no-role-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        User user = TestDataFactory.user();

        when(jwtUtil.extractEmail("no-role-token")).thenReturn(user.getEmail());
        when(jwtUtil.extractUserId("no-role-token")).thenReturn(user.getUserId());
        when(jwtUtil.extractRoles("no-role-token")).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        assertThat(principal.getRoles()).isEmpty();
        assertThat(authentication.getAuthorities()).isEmpty();
    }

    @Test
    void doFilterDoesNotAuthenticateWhenUserIsMissing() throws Exception {

        MockHttpServletRequest request = bearerRequest("valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.extractEmail("valid-token")).thenReturn(TestDataFactory.EMAIL);
        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        when(userRepository.findByEmail(TestDataFactory.EMAIL)).thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterClearsContextWhenJwtParsingFails() throws Exception {

        MockHttpServletRequest request = bearerRequest("invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.extractEmail("invalid-token"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    private MockHttpServletRequest bearerRequest(String token) {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        return request;
    }
}
