package com.nexora.rsp.talentcore.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private static final String SECRET = "TalentCoreTestSecretKeyForJWTAuthenticationAndAuthorization123456";

    private final JwtUtil jwtUtil = new JwtUtil();

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "accessExpiration", 900000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 2592000000L);
    }

    @Test
    void generateAccessTokenIncludesUserIdEmailAndRoles() {

        String token = jwtUtil.generateAccessToken(
                1L,
                "murali@test.com",
                List.of("EMPLOYEE")
        );

        assertThat(jwtUtil.extractUserId(token)).isEqualTo(1L);
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("murali@test.com");
        assertThat(jwtUtil.extractRoles(token)).containsExactly("EMPLOYEE");
    }

    @Test
    void generateAccessTokenSupportsNoRoleUsers() {

        String token = jwtUtil.generateAccessToken(
                2L,
                "norole@test.com",
                List.of()
        );

        assertThat(jwtUtil.extractUserId(token)).isEqualTo(2L);
        assertThat(jwtUtil.extractRoles(token)).isEmpty();
    }

    @Test
    void generateRefreshTokenIncludesUserIdAndEmailWithoutRoles() {

        String token = jwtUtil.generateRefreshToken(
                3L,
                "refresh@test.com"
        );

        assertThat(jwtUtil.extractUserId(token)).isEqualTo(3L);
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("refresh@test.com");
        assertThat(jwtUtil.extractRoles(token)).isNull();
    }

    @Test
    void extractUserIdSupportsStringClaimValues() {

        String token = Jwts.builder()
                .claim("userId", "4")
                .subject("string-id@test.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThat(jwtUtil.extractUserId(token)).isEqualTo(4L);
    }

    @Test
    void extractEmailThrowsForExpiredToken() {

        ReflectionTestUtils.setField(jwtUtil, "accessExpiration", -1000L);

        String token = jwtUtil.generateAccessToken(
                1L,
                "expired@test.com",
                List.of("EMPLOYEE")
        );

        assertThatThrownBy(() -> jwtUtil.extractEmail(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
