package com.nexora.rsp.talentcore.config;

import com.nexora.rsp.talentcore.domain.User;
import com.nexora.rsp.talentcore.repository.UserRepository;
import com.nexora.rsp.talentcore.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(token);

            Long userId = jwtUtil.extractUserId(token);

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                log.warn("JWT authentication failed because user was not found for email={}", email);
                filterChain.doFilter(request, response);
                return;
            }
            List<String> roles = jwtUtil.extractRoles(token);

            List<GrantedAuthority> authorities = Optional.ofNullable(roles)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(role -> !role.isBlank())
                    .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();

            UserPrincipal principal = UserPrincipal
                    .builder()
                    .userId(userId)
                    .email(email)
                    .roles(authorities.stream().map(GrantedAuthority::getAuthority
                    ).map(role -> role.replace("ROLE_", ""))
                            .toList()).build();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RuntimeException ex) {
            SecurityContextHolder.clearContext();
            log.warn("JWT authentication failed for requestUri={} reason={}", request.getRequestURI(), ex.getClass().getSimpleName());
        }
        filterChain.doFilter(request, response);
    }
}
