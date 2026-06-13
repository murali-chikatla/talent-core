package com.nexora.rsp.talentcore.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;



    public String generateAccessToken(Long userId, String email, List<String> roles) {

        return Jwts.builder()
                .claim("userId", userId)
                .subject(email)
                .claim("roles", roles)
                .claim("tokenType", "ACCESS")
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getKey(), Jwts.SIG.HS384)
                .compact();
    }


    public String generateRefreshToken(
            Long userId,
            String email) {

        return Jwts.builder()
                .claim("userId", userId)
                .subject(email)
                .claim("tokenType", "REFRESH")
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +refreshExpiration))
                .signWith(getKey(), Jwts.SIG.HS384)
                .compact();
    }


    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Long extractUserId(String token) {

        Object userId = getClaims(token).get("userId");

        return userId instanceof Number number
                ? number.longValue()
                : Long.valueOf(userId.toString());
    }


    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
