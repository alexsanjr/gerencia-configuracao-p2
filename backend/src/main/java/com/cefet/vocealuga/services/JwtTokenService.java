package com.cefet.vocealuga.services;

import com.cefet.vocealuga.services.exceptions.InvalidDataAccessApiUsageException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenService {

    @Value("${JWT_SECRET_KEY}")
    private String secret;

    @Value("${JWT_EXPIRATION:3600000}")
    private Long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("usuario_id", userId);
        return Jwts.builder()
                .setClaims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("usuario_id", Long.class);
        } catch (Exception e) {
            throw new InvalidDataAccessApiUsageException("Token invalid");
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            throw new InvalidDataAccessApiUsageException("Token inválido");
        }
    }

    public String getRoleFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (Exception e) {
            throw new InvalidDataAccessApiUsageException("Token invalid");
        }
    }
}