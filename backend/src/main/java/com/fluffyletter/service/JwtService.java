package com.fluffyletter.service;

import com.fluffyletter.config.FluffyProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final FluffyProperties properties;

    public JwtService(FluffyProperties properties) {
        this.properties = properties;
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String issueUserToken(Long userId, String openid) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.getJwt().getTtlSeconds());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "user")
                .claim("openid", openid)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key())
                .compact();
    }

    public String issueAdminToken(Long adminId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.getJwt().getTtlSeconds());

        return Jwts.builder()
                .subject(String.valueOf(adminId))
                .claim("type", "admin")
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
