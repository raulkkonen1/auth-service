package com.auth.jwt.security;

import com.auth.jwt.dto.RequestDto;
import com.auth.jwt.model.AuthUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;


import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    // Clave segura de al menos 32 caracteres (256 bits)
    private final String secret = "clave_super_segura_para_demo2025_con_mas_de_32_chars!";
    private SecretKey key;


    private final RouteValidator routeValidator;

    public JwtProvider(RouteValidator routeValidator) {
        this.routeValidator = routeValidator;
    }

    @PostConstruct
    public void init() {
        // Genera la clave a partir del secreto definido
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String createToken(AuthUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", user.getRole());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(convertToDate(LocalDateTime.now()))
                .expiration(convertToDate(LocalDateTime.now().plusHours(1)))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            return "Token inválido";
        }
    }

    public boolean validate(String token, RequestDto dto) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject().equals(dto.getUsername()) &&
                    claims.get("role").equals("ROLE_" + dto.getPassword());
        } catch (Exception e) {
            return false;
        }
    }
}
